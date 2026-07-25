---
name: check
description: 'Kareer Spring Boot 프로젝트에서 기존 코드 구조, 패키지 위치, Facade 필요 여부, 도메인 간 의존 관계를 검토할 때 사용한다. "이 구조 맞아", "여기 둬도 돼", "import해도 돼", "Facade 필요해", "global에 둬도 돼", "다른 도메인 참조해도 돼" 요청 시 사용한다.'
---

# Kareer 패키지 구조 검증 가이드

이 Skill은 `Kareer` 백엔드에서 현재 코드 구조가 프로젝트의 패키지/의존 규칙을 지키는지 검증할 때 사용한다.

Kareer는 단일 Gradle 모듈이며, `domain/<name>/{controller,dto,entity,repository,service,facade,exception}` 패키지와 `global/`(공통 인프라) 두 축으로 구성된다. Service는 QueryDSL/JPA Repository를 Port 없이 직접 주입해서 쓴다.

## 사용 상황

다음 요청에서 이 Skill을 사용한다.

* "이 구조 맞아?"
* "이거 여기 둬도 돼?"
* "domain에서 이거 import해도 돼?"
* "Facade가 필요한 상황이야?"
* "global에 둬도 돼?"
* "Service에서 다른 도메인 Repository 써도 돼?"
* "Controller에 로직 넣어도 돼?"

## 목표

답변은 다음을 명확히 해야 한다.

1. 현재 구조가 적절한지
2. 패키지 위치나 의존 위반이 있는지
3. 어떤 규칙을 위반했는지, 왜 문제가 되는지
4. 어떻게 수정해야 하는지
5. 권장 패키지 구조와 콜체인이 무엇인지

---

## Step 1. 검토할 대상 식별

* 클래스 위치 검토 (controller/dto/entity/repository/service/facade/exception 중 어디)
* 도메인 간 의존 검토 (roadmap이 member를, jobposting이 member를 참조하는 식)
* domain ↔ global 의존 방향 검토
* API 인터페이스(`XxxApi`)와 Controller 구현체 분리 검토
* Controller/Service 책임 검토
* Facade 필요 여부 검토
* Entity / i18n 번역(Translation) 엔티티 분리 검토
* 예외(`XxxException`/`XxxErrorCode`) 배치 검토

---

## Step 2. 패키지 책임

| 위치 | 책임 |
|---|---|
| `domain/<name>/controller/` | `XxxApi` 인터페이스(Swagger 문서) + `XxxController implements XxxApi`(Spring MVC 매핑) |
| `domain/<name>/dto/` | HTTP request/response DTO |
| `domain/<name>/entity/` | JPA Entity. `BaseEntity`(global/entity) 상속. 번역이 필요하면 `Xxx` + `XxxTranslation` 쌍 |
| `domain/<name>/repository/` | `XxxRepository` + 복잡 쿼리용 `XxxRepositoryCustom`/`XxxRepositoryCustomImpl`(QueryDSL) |
| `domain/<name>/service/` | 단일 책임 비즈니스 로직. 하위 세분화 가능(`service/phase`, `service/actionitem` 등) |
| `domain/<name>/facade/` | 여러 Service(자기 도메인 + 다른 도메인)를 조합하는 트랜잭션 단위 유스케이스 |
| `domain/<name>/exception/` | `XxxException extends CustomException` + `XxxErrorCode implements ErrorCode` |
| `global/annotation/` | `@Auth` 등 커스텀 애노테이션 |
| `global/auth/` | 로그인 코드 교환, refresh 쿠키, 토큰 블랙리스트 (jwt/oauth와 별개) |
| `global/jwt/` | JWT 생성/검증, `JwtFilter` |
| `global/oauth/` | Google OAuth2 로그인 |
| `global/security/` | Spring Security, CORS |
| `global/external/<provider>/` | 외부 API 클라이언트(OpenAI, Cohere, Google Translate, Clova OCR, Discord). Port 인터페이스 없이 concrete Service를 도메인이 직접 주입 |
| `global/document/` | PDF/OCR 텍스트 후처리 공통 유틸 (여러 도메인의 OCR 파서가 공유) |
| `global/config/locale/` | `KareerLocaleResolver` 등 i18n 리졸버 |
| `global/response/` | `BaseResponse` |
| `global/exception/` | `GlobalExceptionHandler`, `ErrorCode` 인터페이스, `CustomException`/`GlobalException` 베이스 |
| `global/entity/` | `BaseEntity` |

---

## Step 3. 의존 규칙

### domain → domain (도메인 간 참조)

원칙: **도메인 간 유스케이스 조합은 Facade에서 한다.** Facade는 다른 도메인의 QueryService(`MemberQueryService` 등)를 주입해서 쓸 수 있다. 예: `RoadmapGenerateFacade`가 `MemberQueryService`를 주입.

Service 레벨에서 다른 도메인의 **Entity**를 참조하는 것(예: `RoadmapGenerateService`가 `Member`, `MemberVisa` 엔티티를 파라미터로 받는 것)은 허용된다 — Facade가 조회해서 넘겨주는 값을 그대로 쓰는 흐름이면 문제 없다.

Service 레벨에서 다른 도메인의 **Repository**를 직접 주입하는 것은 원칙적으로 지양한다. 다만 회원 탈퇴처럼 여러 도메인에 걸친 라이프사이클 정리가 필요한 경우(`MemberDeletionService`가 roadmap 쪽 Repository들을 직접 참조해 cascade 삭제하는 것)는 실제로 허용된 예외다. 이 예외는 "다른 도메인 데이터를 조회해서 비즈니스 로직에 쓰는" 경우가 아니라 "삭제/정리 전용"일 때만 정당화된다. 일반 유스케이스에서 이 패턴을 새로 늘리려 하면 지적한다.

### domain → global

자유롭게 참조 가능. `global/exception`, `global/response`, `global/entity`, `global/external/*`, `global/annotation` 등은 도메인이 가져다 쓰는 공통 인프라다.

### global → domain (역방향)

원칙적으로 금지 — `global`은 도메인에 오염되면 안 된다. 단, **예외가 실제로 존재**한다: `global/external/ai/builder/`(`MemberContextBuilder`, `RoadmapContextBuilder`, `JobPostingEmbeddingTextBuilder`, `PolicyQueryBuilder`)는 LLM 프롬프트/임베딩 텍스트를 만들기 위해 도메인 Entity/Repository를 직접 참조한다. 이는 RAG 파이프라인의 의도된 설계이며, "그 도메인 데이터를 텍스트로 직렬화하는 빌더"라는 좁은 역할에 한정된다.

이 예외를 다른 `global/*` 코드(예: `global/security`, `global/response`, `global/config`)로 확대하려는 시도는 위반으로 본다 — RAG builder류가 아니라면 global에서 domain을 import하면 안 된다.

### 요약

```text
domain/<name>          → global/*                (자유)
domain/<name>          → domain/<name>/자기 자신  (자유)
domain/<name>/facade   → 다른 domain/<other>/service (도메인 조합 목적, 정상)
domain/<name>/service  → 다른 domain/<other>/entity (Facade가 넘겨준 값을 쓰는 경우 정상)
domain/<name>/service  → 다른 domain/<other>/repository (원칙 금지, 라이프사이클 cascade 정리만 예외)
global/external/ai/builder/* → domain/*/entity, domain/*/repository (RAG 컨텍스트 빌더 한정 허용)
그 외 global/*          → domain/*                (금지)
```

---

## Step 4. 계층별 검토 기준

### Controller

* `XxxApi` 인터페이스: 같은 `controller/` 패키지, `@Tag`/`@Operation`/`@Schema` 등 Swagger 애노테이션과 메서드 시그니처만 선언. Service/Facade 주입이나 구현 로직을 가지면 안 된다.
* `XxxController`: `XxxApi`를 `implements`, `@RestController`/`@RequestMapping`/`@Valid` 등 실제 Spring MVC 애노테이션, Service/Facade 호출, `BaseResponse` 변환만 담당.
* Controller에 있으면 안 되는 것: 비즈니스 정책 판단, 여러 Repository 조합, 트랜잭션 경계 결정 — Service/Facade로 이동.

### Service

* 비즈니스 규칙, 자기 도메인 Repository 접근, 자기 도메인 엔티티 상태 변경을 담당.
* `Transactional(readOnly = true)` 클래스 레벨 + 쓰기 메서드에 개별 `@Transactional`을 얹는 패턴이 흔하다(`PhaseQueryService` 등 참고).
* i18n이 필요한 조회 Service는 `LocaleContextHolder.getLocale()`으로 언어를 가져와 `XxxTranslation` 테이블을 조회하고, 없으면 원본(Korean) 값으로 폴백한다.

### Facade

다음일 때 Facade가 적절하다.
* 여러 Service(자기 도메인 + 다른 도메인)를 하나의 트랜잭션 유스케이스로 조율한다.
* 외부 연동 Service(`OpenAiService`, `GoogleTranslationService` 등)와 도메인 Service를 함께 오케스트레이션한다.

Facade가 부적절한 경우:
* 단일 Service만 감싼다.
* 아무 로직 없이 위임만 한다 — Controller에서 Service를 직접 호출해도 충분하다.

### Exception

* 도메인 전용 예외는 `domain/<name>/exception/XxxException extends CustomException` + `XxxErrorCode implements ErrorCode`(enum, `HttpStatus`+메시지) 쌍으로 만든다.
* 여러 도메인이 공유하는 예외만 `global/exception/`에 둔다(`GlobalException`, `GlobalErrorCode`).

---

## Step 5. 자주 발생하는 위반과 수정 방향

| 위반 | 문제 | 수정 방향 |
|---|---|---|
| Controller가 여러 Service를 조합해 비즈니스 흐름을 직접 짬 | HTTP 계층에 유스케이스 오케스트레이션이 섞임 | `domain/<name>/facade/`로 이동 |
| `XxxController`가 `XxxApi`를 구현하지 않음 | API 문서 계약과 구현이 분리되지 않음 | 같은 패키지에 `XxxApi` 생성 후 `implements` |
| `XxxApi`에 Service 주입/구현 로직이 있음 | 문서 인터페이스에 실행 책임이 섞임 | 로직을 `XxxController`로 이동 |
| Service가 다른 도메인 Repository를 상시적으로 주입 | 도메인 결합도 증가, 캐스케이드 삭제 목적이 아님 | Facade에서 다른 도메인 QueryService를 조합하도록 변경 |
| Facade가 단일 Service만 위임 | 불필요한 계층 증가 | Facade 제거, Controller→Service 직결 |
| `global/*`(RAG builder 제외)가 domain entity/repository를 import | global이 도메인에 오염됨 | 해당 로직을 domain/service 또는 domain/facade로 이동 |
| 도메인 전용 예외를 `global/exception`에 배치 | global이 도메인 정책에 오염됨 | `domain/<name>/exception/`으로 이동 |
| i18n 번역 폴백 로직 없이 Translation 엔티티만 조회 | 번역이 없는 로케일에서 응답이 비게 됨 | 원본(Korean) 엔티티로 폴백하는 분기 추가 |

---

## Step 6. 검토 답변 형식

```md
## 구조 검토 결과: [대상]

### 결론
- 적절함 / 일부 수정 필요 / 위반 있음

### 현재 구조
```text
...
```

### 문제점
* ...

### 수정 방향
* ...

### 권장 구조
```text
...
```

### 권장 콜체인
```text
HTTP Request
  → XxxController implements XxxApi
    (API 문서 계약: XxxApi)
  → Service/Facade
  → Repository
```

### 의존성 검증
* ...
```

---

## Step 7. 답변 원칙

- 단순히 "안 돼요"라고 하지 말고 어떤 의존 규칙을 위반하는지 설명한다.
- 위반이 없다면 왜 괜찮은지 설명한다 (특히 RAG builder의 domain 참조나 `MemberDeletionService`의 cascade 삭제처럼 "예외로 허용된 패턴"과 헷갈리지 않도록 근거를 댄다).
- 애매한 경우 유지보수성과 의존 방향 기준으로 판단한다.
- Facade가 필요한지 여부를 명확히 판단한다.
- `XxxApi`/`XxxController` 분리가 되어 있는지, 각 책임이 올바른 파일에 있는지 확인한다.
- 도메인 간 참조는 "Facade를 통한 조합"과 "Service의 상시적 다른 도메인 Repository 의존"을 구분해서 판단한다.
- 가능한 경우 권장 패키지 구조와 콜체인을 함께 제시한다.
