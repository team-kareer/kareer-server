# RAGAS-equivalent golden set

`golden-set.json`은 시작용 템플릿이다. 각 케이스는 로드맵 생성 흐름이 실제로 쓰는
필드(`targetJob`, `degreeCode`, `expectedGraduationDate`, `visaType`)로 가상의 회원 프로필을
만들고, `referenceAnswer`(사람이 작성한 정답 요지)를 기준으로 Context Recall을 계산한다.

실행 전에 팀이 반드시 확인/보완해야 하는 것:

- `targetJob` 문자열은 `required_document_vectors`에 실제로 임베딩된 문서의 `domain` 메타데이터와
  일치해야 검색이 걸린다 (`RequiredDocumentRetriever.retrieveCareer`가 `member.getTargetJob()`을
  그대로 domain 필터로 씀). 실제 임베딩된 도메인 값으로 바꿀 것.
- `referenceAnswer`의 `TODO`는 실제 pgvector에 들어있는 문서를 열어보고 정답 요지를 사람이 직접
  채워야 한다. Claude는 실제로 어떤 PDF가 임베딩되어 있는지 알 수 없어 채울 수 없었다.
- 케이스를 늘릴수록 `contextPrecision`이 케이스당 청크 수만큼 LLM 호출을 추가로 발생시킨다
  (`RagasMetricsCalculator.contextPrecision`). 비용을 고려해 소규모로 유지할 것.

실행: `./gradlew ragasEval -Dspring.profiles.active=local` (local 프로파일과 동일한 실제
Postgres+pgvector+OpenAI 키 env var 필요).
