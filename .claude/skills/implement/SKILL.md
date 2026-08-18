---
name: implement
description: 'Kareer Spring Boot 프로젝트에서 신규 기능의 구현 위치, 생성 파일, Controller → Facade/Service → Repository 콜체인을 설계할 때 사용한다. "새 기능", "구현", "어디에 둬야", "API 만들기", "Controller", "Service", "Facade" 요청 시 사용한다.'
---

# Kareer 신규 기능 구현 설계 가이드

이 Skill은 `Kareer` 백엔드에서 새로운 기능을 구현할 때 코드가 어느 패키지에 들어가야 하는지 판단하고, 전체 콜체인을 설계하기 위해 사용한다.

Kareer는 단일 Gradle 모듈이며 `domain/<name>/{controller,dto,entity,repository,service,facade,exception}` + `global/`(공통 인프라) 구조를 따른다. Port/Adapter 추상화는 쓰지 않는다 — Service가 Repository를, Facade가 외부 연동 Service(`OpenAiService`, `GoogleTranslationService` 등)를 직접 주입해서 쓴다.

## 사용 상황

* 새로운 기능을 구현할 때
* 신규 API의 Controller, Service, Facade 구조를 잡을 때
* 코드가 어느 도메인 패키지에 들어가야 하는지 판단할 때
* 특정 기능의 생성 파일 목록을 설계할 때
* 외부 시스템 연동(OCR, 번역, LLM, 크롤링 등)을 설계할 때
* 여러 도메인을 조율해야 하는지 판단할 때
* 다국어(i18n) 대응이 필요한 엔티티를 설계할 때

## 목표

답변은 다음을 명확히 해야 한다.

1. 어떤 도메인 기능인지 (`member` / `roadmap` / `jobposting` / `term` / 신규 도메인)
2. 단일 도메인인지, 다중 도메인 조율인지
3. Facade가 필요한지
4. i18n(다국어) 대응이 필요한지 — 필요하면 `Xxx`+`XxxTranslation` 쌍 설계
5. 어떤 파일을 생성/수정해야 하는지
6. 각 파일이 어느 패키지에 위치해야 하는지
7. 의존 방향이 올바른지

---

## Step 1. 도메인 식별

| 도메인 | 책임 |
|---|---|
| `member` | 회원 프로필, 온보딩, 여권/비자 OCR 파싱 |
| `roadmap` | AI 로드맵 생성, Phase/PhaseAction/ActionItem |
| `jobposting` | 공고 크롤링, 추천 |
| `term` | 약관 콘텐츠/번역 |
| (신규) | 위 네 가지에 속하지 않으면 새 `domain/<new-name>` 패키지 생성을 검토 |

도메인이 애매하면:
1. 해당 데이터의 생명주기를 소유하는 도메인을 우선한다.
2. 상태 변경의 주체가 되는 도메인을 우선한다.
3. 단순 조회만 필요한 다른 도메인은 조율 대상(Facade에서 QueryService 주입)으로 본다.
4. 여러 도메인이 함께 필요하면 Facade 사용을 검토한다(Step 4).

---

## Step 2. 코드 배치 규칙

| 코드 유형 | 위치 |
|---|---|
| API 문서 인터페이스 | `domain/<name>/controller/XxxApi` |
| REST Controller 구현체 | `domain/<name>/controller/XxxController` |
| HTTP Request/Response DTO | `domain/<name>/dto/request/`, `domain/<name>/dto/response/` |
| 비즈니스 로직, 유스케이스 | `domain/<name>/service/` (세분화 시 `service/<sub>/`) |
| 도메인 간 조율 유스케이스 | `domain/<name>/facade/XxxFacade` |
| JPA Entity | `domain/<name>/entity/Xxx` (BaseEntity 상속) |
| 다국어 번역 Entity | `domain/<name>/entity/XxxTranslation` (원본 Entity와 FK 연관관계) |
| Enum | `domain/<name>/entity/enums/` |
| Repository | `domain/<name>/repository/XxxRepository` |
| 복잡 조회(QueryDSL) | `domain/<name>/repository/XxxRepositoryCustom` + `XxxRepositoryCustomImpl` |
| 도메인 예외 | `domain/<name>/exception/XxxException`, `XxxErrorCode` |
| 외부 API 클라이언트 | `global/external/<provider>/service/` (예: `external/clova`, `external/cohere`, `external/google`) |
| RAG 프롬프트/임베딩 텍스트 조립 | `global/external/ai/builder/` (도메인 Entity/Repository 직접 참조 허용) |
| 공통 문서/OCR 후처리 유틸 | `global/document/` |
| 공통 응답/예외/엔티티 | `global/response/`, `global/exception/`, `global/entity/` |
| 인증/보안 | `global/auth/`(로그인 코드·쿠키·블랙리스트), `global/jwt/`, `global/oauth/`, `global/security/` |
| 커스텀 애노테이션 | `global/annotation/` |
| 설정/프로퍼티 바인딩 | `global/config/` |

---

## Step 3. 콜체인 설계

### API 인터페이스와 Controller 구현체

신규 HTTP API는 기존 패턴을 그대로 따른다.

* `XxxApi` 인터페이스를 같은 `controller/` 패키지에 만들고 Swagger 문서 책임(`@Tag`, `@Operation`, `@CustomExceptionDescription` 등)을 둔다.
* `XxxController`는 `XxxApi`를 `implements`하고, `@RestController`/`@RequestMapping`/`@RequestBody`/`@Valid` 등 실제 매핑 애노테이션과 `@Auth` 인증 파라미터 처리, Facade/Service 호출, `BaseResponse` 변환을 담당한다.

```text
domain/<name>/controller/XxxApi
domain/<name>/controller/XxxController implements XxxApi
domain/<name>/dto/request/XxxRequest
domain/<name>/dto/response/XxxResponse
```

### 단일 도메인 기능

```text
HTTP Request
  → domain/<name>/controller/XxxController implements XxxApi
    (API 문서 계약: domain/<name>/controller/XxxApi)
  → domain/<name>/service/XxxService
  → domain/<name>/repository/XxxRepository
```

### 다중 도메인 기능 (Facade 필요)

```text
HTTP Request
  → domain/<name>/controller/XxxController implements XxxApi
    (API 문서 계약: domain/<name>/controller/XxxApi)
  → domain/<name>/facade/XxxFacade
      ├─ domain/<name>/service/XxxService
      ├─ domain/<other>/service/OtherQueryService   (다른 도메인 조회는 QueryService 주입)
      └─ global/external/<provider>/service/XxxExternalService  (필요 시)
  → domain/<name>/repository/XxxRepository
```

예: `RoadmapGenerateFacade`는 `MemberQueryService`(다른 도메인 조회) + `RoadmapGenerateService`/`RoadMapPersistService`(자기 도메인) + `OpenAiService`/`GoogleTranslationService`(외부 연동)를 조합한다.

### 외부 시스템 연동 기능

Port 인터페이스로 추상화하지 않는다. `global/external/<provider>/service/`에 concrete Service를 만들고, 필요한 도메인의 Facade(또는 Service)가 이를 직접 주입해서 쓴다.

```text
domain/<name>/facade/XxxFacade
  → global/external/<provider>/service/XxxExternalService
```

새 provider를 추가할 때는 `global/external/<provider>/{service,dto/request,dto/response,exception,properties}` 하위 구조를 따른다(`external/cohere` 참고).

### 비동기 처리가 필요한 기능

응답을 기다리지 않아도 되는 후속 작업(번역, 알림 등)은 `ExecutorServiceConfig`가 제공하는 virtual-thread `ExecutorService`로 fire-and-forget 실행하고, 실패는 로깅만 하고 호출자에 전파하지 않는다(로드맵 번역 참고).

### 다국어(i18n) 대응이 필요한 기능

읽기 API가 `X-Preferred-Language` 헤더 기준으로 번역된 콘텐츠를 내려줘야 하면:

1. `Xxx` 원본 엔티티(한국어) + `XxxTranslation`(language 컬럼 포함) 엔티티 쌍을 만든다.
2. 조회 Service에서 `LocaleContextHolder.getLocale()`로 언어를 얻어 `XxxTranslation`을 조회하고, 없으면 원본 `Xxx` 값으로 폴백한다.
3. 로드맵처럼 생성 시점에 번역을 만들어야 하면, 원본 저장 후 비동기로 번역 Service를 호출해 `XxxTranslation`을 채운다.

---

## Step 4. Facade 판단 기준

Facade는 다음 경우에만 만든다.

* 여러 도메인 Service를 조율해야 한다 (자기 도메인 Service + 다른 도메인 QueryService).
* 도메인 Service와 외부 연동 Service(`global/external/*`)를 함께 오케스트레이션한다.
* 하나의 API에서 서로 다른 책임의 유스케이스가 한 트랜잭션으로 묶여야 한다.

Facade를 만들지 않는 경우:
* 단일 Service만 호출한다 — Controller에서 Service로 바로 위임한다.
* 단순히 Service를 한 번 감싸기만 한다.

---

## Step 5. 도메인 간 참조 판단 기준

* 다른 도메인 데이터를 **조회**해서 써야 하면: Facade에서 그 도메인의 `XxxQueryService`를 주입한다. Service 레벨에서 다른 도메인 Repository를 직접 주입하지 않는다.
* 다른 도메인 데이터를 **엔티티 값으로 전달**받아 쓰는 것(Facade가 조회한 `Member`를 자기 도메인 Service 메서드 인자로 넘기는 것)은 정상이다.
* 회원 탈퇴처럼 여러 도메인에 걸친 **cascade 삭제/정리**가 필요하면 예외적으로 해당 Service가 다른 도메인 Repository를 직접 참조할 수 있다(`MemberDeletionService` 참고) — 이 패턴은 정리 전용으로 한정하고 일반 유스케이스로 확장하지 않는다.
* RAG 프롬프트/임베딩 텍스트를 만드는 코드가 아니라면, `global/*`에서 `domain/*` entity/repository를 새로 import하지 않는다.

---

## Step 6. 네이밍 규칙

Controller/Api:
```text
XxxApi
XxxController
```

Service:
```text
XxxQueryService   (조회 전용)
XxxCommandService (생성/수정/삭제)
XxxService        (조회/커맨드가 굳이 안 나뉘는 단순 도메인)
```

Facade:
```text
XxxFacade
XxxGenerateFacade
```

Entity / Translation:
```text
Xxx
XxxTranslation
```

Repository:
```text
XxxRepository
XxxRepositoryCustom
XxxRepositoryCustomImpl
```

Exception:
```text
XxxException extends CustomException
XxxErrorCode implements ErrorCode
```

외부 연동:
```text
global/external/<provider>/service/XxxService  (예: CohereRerankClient, GoogleTranslationService)
```

---

## Step 7. 답변 형식

```md
## 구현 플랜: [기능명]

### 판단
- 도메인:
- 단일/다중 도메인:
- Facade 필요 여부:
- i18n(번역) 필요 여부:

### 생성할 파일
- `domain/<name>/controller/XxxApi`
- `domain/<name>/controller/XxxController`
- `domain/<name>/dto/request/XxxRequest`
- `domain/<name>/dto/response/XxxResponse`
- `domain/<name>/service/XxxService`
- `domain/<name>/repository/XxxRepository`

### 수정할 파일
- `...`

### 콜체인
```text
HTTP Request
  → ...
```

### 의존성 검증
* 다른 도메인 참조는 Facade의 QueryService 조합으로 처리했는지
* 외부 연동은 `global/external/<provider>`의 concrete Service를 직접 주입했는지 (Port 불필요)
* i18n이 필요한 경우 Translation 엔티티/폴백 로직을 포함했는지
```

---

## Step 8. 답변 원칙

- 파일 경로를 가능한 한 구체적으로 제안한다.
- 도메인 배치는 데이터 생명주기와 비즈니스 책임 기준으로 정한다.
- HTTP API를 만들 때는 `XxxApi` 인터페이스와 `XxxController implements XxxApi`를 항상 함께 제안한다.
- Swagger 애노테이션은 `XxxApi`에, Spring MVC 매핑과 실제 구현 로직은 `XxxController`에 둔다.
- 단순히 "서비스에 둔다"고 하지 말고 어떤 도메인의 어떤 Service인지 명시한다.
- Port/Adapter를 새로 도입하자고 제안하지 않는다 — 이 프로젝트는 concrete Service 직접 주입 방식을 쓴다.
- 다른 도메인 조회가 필요하면 Facade + QueryService 조합을 제안하고, Repository 직접 참조는 cascade 삭제 같은 예외 상황에만 허용한다.
- 다국어 콘텐츠가 필요하면 `Xxx`+`XxxTranslation` 쌍과 로케일 폴백 로직을 함께 설계한다.
- Facade는 여러 도메인/외부 연동 조율이 있을 때만 제안한다.
- 비동기 후속 작업(번역, 알림 등)이 필요하면 virtual-thread `ExecutorService` + fire-and-forget + 실패 로깅 패턴을 제안한다.
