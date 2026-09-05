# AGENTS.md

이 파일은 Codex가 이 저장소에서 작업할 때 참고하는 안내 문서입니다.

## 프로젝트 가이드

Kareer는 Spring Boot 백엔드 프로젝트입니다. 구조 변경, 신규 기능 구현, 코드 위치 검토를 하기 전에 `docs/codex/` 아래의 프로젝트 전용 가이드를 먼저 확인합니다.

다음 파일을 기준 문서로 사용합니다.

- `docs/codex/check.md`: 패키지 위치, 의존 방향, Facade 필요 여부, Controller/Service 책임, domain/global import, 기존 구조 적절성 등을 검토할 때 참고합니다.
- `docs/codex/implement.md`: 신규 기능, API, Controller/Service/Facade/Repository 콜체인, 외부 연동, i18n 엔티티 구조를 설계하거나 구현할 때 참고합니다.

## 기본 명령어

```bash
# 테스트 제외 빌드
./gradlew clean build -x test

# 로컬 실행, 8080 포트
./gradlew bootRun --args='--spring.profiles.active=local'

# 전체 테스트 실행
./gradlew test

# QueryDSL Q-class 생성
./gradlew compileJava
```

## 작업 원칙

- 패키지 구조와 의존성 규칙은 `docs/codex/check.md`, `docs/codex/implement.md`의 내용을 우선합니다.
- 두 문서에 이미 있는 아키텍처 규칙을 `AGENTS.md`에 중복해서 늘리지 않습니다.
- 구현 작업에서는 먼저 도메인을 식별하고, Facade 필요 여부를 판단한 뒤 `docs/codex/implement.md`의 파일 배치와 콜체인 규칙을 따릅니다.
- 구조 검토 작업에서는 `docs/codex/check.md` 기준으로 위반 여부를 판단하고, 필요한 경우 권장 패키지 구조와 콜체인을 제안합니다.
- 변경 범위는 요청과 관련된 코드로 제한하고, 동작 변경이 있으면 가능한 한 가장 좁은 범위의 Gradle 테스트를 실행합니다.
