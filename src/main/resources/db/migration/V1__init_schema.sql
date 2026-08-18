-- =============================================================
-- V1: 초기 스키마 (Roadmap 엔티티 도입 이전, dev/prod 현재 상태)
-- 기존 dev/prod DB에는 baseline-on-migrate=true 로 이 스크립트를
-- 실행하지 않고 V1을 베이스라인으로 마킹함.
-- 신규 환경 구성 시에는 V1 → V2 순으로 실행하여 최신 스키마를 구성함.
-- =============================================================

-- -------------------------
-- members
-- -------------------------
CREATE TABLE members
(
    member_id                BIGSERIAL PRIMARY KEY,
    name                     VARCHAR(255) NOT NULL,
    email                    VARCHAR(320),
    profile_image_url        VARCHAR(255),
    status                   VARCHAR(50)  NOT NULL,
    provider                 VARCHAR(50)  NOT NULL,
    provider_id              VARCHAR(255) NOT NULL,
    birth_date               DATE,
    country_code             VARCHAR(255),
    primary_major_code       VARCHAR(255),
    secondary_major          VARCHAR(255),
    target_job               VARCHAR(255),
    graduation_date          DATE,
    expected_graduation_date DATE,
    personal_background      VARCHAR(255),
    university_code          VARCHAR(255),
    english_level_code       VARCHAR(255),
    language_level           VARCHAR(50),
    degree_code              VARCHAR(255),
    target_job_skill         VARCHAR(1000),
    preparation_status       VARCHAR(255),
    fields_of_interest       VARCHAR(255),
    created_at               TIMESTAMP    NOT NULL,
    updated_at               TIMESTAMP    NOT NULL,
    CONSTRAINT uk_member_provider_provider_id UNIQUE (provider, provider_id)
);

-- -------------------------
-- terms
-- -------------------------
CREATE TABLE terms
(
    term_id    BIGSERIAL PRIMARY KEY,
    type       VARCHAR(50)  NOT NULL,
    version    VARCHAR(255) NOT NULL,
    required   BOOLEAN      NOT NULL,
    active     BOOLEAN      NOT NULL,
    created_at TIMESTAMP    NOT NULL,
    updated_at TIMESTAMP    NOT NULL
);

-- -------------------------
-- term_translations
-- -------------------------
CREATE TABLE term_translations
(
    term_translation_id BIGSERIAL PRIMARY KEY,
    term_id             BIGINT       NOT NULL REFERENCES terms (term_id),
    language_code       VARCHAR(10)  NOT NULL,
    title               VARCHAR(255) NOT NULL,
    content             TEXT         NOT NULL,
    created_at          TIMESTAMP    NOT NULL,
    updated_at          TIMESTAMP    NOT NULL,
    CONSTRAINT uk_term_translation_language UNIQUE (term_id, language_code)
);

-- -------------------------
-- member_visas
-- -------------------------
CREATE TABLE member_visas
(
    member_visa_id  BIGSERIAL PRIMARY KEY,
    member_id       BIGINT    NOT NULL REFERENCES members (member_id),
    visa_type       VARCHAR(50) NOT NULL,
    visa_status     VARCHAR(50) NOT NULL,
    visa_expired_at DATE      NOT NULL,
    visa_start_date DATE      NOT NULL,
    created_at      TIMESTAMP NOT NULL,
    updated_at      TIMESTAMP NOT NULL
);

-- -------------------------
-- member_terms
-- -------------------------
CREATE TABLE member_terms
(
    term_agreement_id BIGSERIAL PRIMARY KEY,
    agreed            BOOLEAN   NOT NULL,
    member_id         BIGINT    NOT NULL REFERENCES members (member_id),
    term_id           BIGINT    NOT NULL REFERENCES terms (term_id),
    created_at        TIMESTAMP NOT NULL,
    updated_at        TIMESTAMP NOT NULL,
    CONSTRAINT uk_member_term UNIQUE (member_id, term_id)
);

-- -------------------------
-- localized_onboard_category
-- -------------------------
CREATE TABLE localized_onboard_category
(
    id         BIGSERIAL PRIMARY KEY,
    type       VARCHAR(50)  NOT NULL,
    code       VARCHAR(255) NOT NULL,
    use_order  INTEGER      NOT NULL,
    created_at TIMESTAMP    NOT NULL,
    updated_at TIMESTAMP    NOT NULL
);

-- -------------------------
-- localized_onboard_category_translation
-- -------------------------
CREATE TABLE localized_onboard_category_translation
(
    id          BIGSERIAL PRIMARY KEY,
    category_id BIGINT       NOT NULL REFERENCES localized_onboard_category (id),
    language    VARCHAR(255) NOT NULL,
    label       VARCHAR(255) NOT NULL
);

-- -------------------------
-- phases (Roadmap 도입 이전: member_id FK)
-- -------------------------
CREATE TABLE phases
(
    phase_id    BIGSERIAL PRIMARY KEY,
    member_id   BIGINT    NOT NULL REFERENCES members (member_id),
    sequence    INTEGER   NOT NULL,
    goal        VARCHAR(255) NOT NULL,
    description TEXT      NOT NULL,
    status      VARCHAR(50) NOT NULL,
    start_date  DATE      NOT NULL,
    end_date    DATE      NOT NULL,
    created_at  TIMESTAMP NOT NULL,
    updated_at  TIMESTAMP NOT NULL
);

-- -------------------------
-- phase_translations
-- -------------------------
CREATE TABLE phase_translations
(
    id          BIGSERIAL PRIMARY KEY,
    phase_id    BIGINT      NOT NULL REFERENCES phases (phase_id),
    language    VARCHAR(10) NOT NULL,
    goal        VARCHAR(255) NOT NULL,
    description TEXT        NOT NULL,
    CONSTRAINT uq_phase_translation_language UNIQUE (phase_id, language)
);

-- -------------------------
-- phase_actions
-- -------------------------
CREATE TABLE phase_actions
(
    phase_actions_id BIGSERIAL PRIMARY KEY,
    title            VARCHAR(255),
    description      VARCHAR(255) NOT NULL,
    type             VARCHAR(50)  NOT NULL,
    deadline         DATE         NOT NULL,
    importance       VARCHAR(255) NOT NULL,
    phase_id         BIGINT       NOT NULL REFERENCES phases (phase_id),
    added            BOOLEAN      NOT NULL DEFAULT FALSE,
    completed        BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at       TIMESTAMP    NOT NULL,
    updated_at       TIMESTAMP    NOT NULL
);

-- -------------------------
-- phase_action_translations
-- -------------------------
CREATE TABLE phase_action_translations
(
    id               BIGSERIAL PRIMARY KEY,
    phase_actions_id BIGINT      NOT NULL REFERENCES phase_actions (phase_actions_id),
    language         VARCHAR(10) NOT NULL,
    title            VARCHAR(255),
    description      TEXT,
    importance       TEXT,
    CONSTRAINT uq_phase_action_translation_language UNIQUE (phase_actions_id, language)
);

-- -------------------------
-- phase_action_mistakes
-- -------------------------
CREATE TABLE phase_action_mistakes
(
    phase_action_mistakes_id BIGSERIAL PRIMARY KEY,
    content                  VARCHAR(255),
    phase_actions_id         BIGINT REFERENCES phase_actions (phase_actions_id)
);

-- -------------------------
-- phase_action_mistake_translations
-- -------------------------
CREATE TABLE phase_action_mistake_translations
(
    id         BIGSERIAL PRIMARY KEY,
    mistake_id BIGINT      NOT NULL REFERENCES phase_action_mistakes (phase_action_mistakes_id),
    language   VARCHAR(10) NOT NULL,
    content    TEXT,
    CONSTRAINT uq_phase_action_mistake_translation_language UNIQUE (mistake_id, language)
);

-- -------------------------
-- phase_action_guidelines
-- -------------------------
CREATE TABLE phase_action_guidelines
(
    id               BIGSERIAL PRIMARY KEY,
    content          VARCHAR(255),
    phase_actions_id BIGINT REFERENCES phase_actions (phase_actions_id)
);

-- -------------------------
-- phase_action_guideline_translations
-- -------------------------
CREATE TABLE phase_action_guideline_translations
(
    id           BIGSERIAL PRIMARY KEY,
    guideline_id BIGINT      NOT NULL REFERENCES phase_action_guidelines (id),
    language     VARCHAR(10) NOT NULL,
    content      TEXT,
    CONSTRAINT uq_phase_action_guideline_translation_language UNIQUE (guideline_id, language)
);

-- -------------------------
-- action_items
-- -------------------------
CREATE TABLE action_items
(
    action_item_id   BIGSERIAL PRIMARY KEY,
    title            VARCHAR(255),
    actions_type     VARCHAR(50) NOT NULL,
    status           VARCHAR(50) NOT NULL,
    deadline         DATE        NOT NULL,
    completed        BOOLEAN     NOT NULL DEFAULT FALSE,
    member_id        BIGINT      NOT NULL REFERENCES members (member_id),
    phase_actions_id BIGINT      NOT NULL REFERENCES phase_actions (phase_actions_id),
    created_at       TIMESTAMP   NOT NULL,
    updated_at       TIMESTAMP   NOT NULL
);

-- -------------------------
-- action_item_translations
-- -------------------------
CREATE TABLE action_item_translations
(
    id             BIGSERIAL PRIMARY KEY,
    action_item_id BIGINT      NOT NULL REFERENCES action_items (action_item_id),
    language       VARCHAR(10) NOT NULL,
    title          VARCHAR(255),
    CONSTRAINT uq_action_item_translation_language UNIQUE (action_item_id, language)
);

-- -------------------------
-- JobPostings (엔티티에 대소문자 그대로 사용)
-- -------------------------
CREATE TABLE "JobPostings"
(
    job_posting_id     BIGSERIAL PRIMARY KEY,
    address            VARCHAR(255) NOT NULL,
    arrangement        VARCHAR(255) NOT NULL,
    company            VARCHAR(255) NOT NULL,
    deadline           DATE,
    image_url          VARCHAR(255),
    post_title         VARCHAR(255) NOT NULL,
    preferred_language VARCHAR(255) NOT NULL,
    preferred_visa     VARCHAR(255) NOT NULL,
    detail             TEXT         NOT NULL,
    career             VARCHAR(255) NOT NULL,
    education          VARCHAR(255) NOT NULL,
    website_url        VARCHAR(255) NOT NULL
);

-- -------------------------
-- job_posting_bookmark
-- -------------------------
CREATE TABLE job_posting_bookmark
(
    job_posting_bookmark_id BIGSERIAL PRIMARY KEY,
    member_id               BIGINT    NOT NULL REFERENCES members (member_id),
    job_posting_id          BIGINT    NOT NULL REFERENCES "JobPostings" (job_posting_id),
    created_at              TIMESTAMP NOT NULL,
    updated_at              TIMESTAMP NOT NULL,
    CONSTRAINT uk_member_job_posting UNIQUE (member_id, job_posting_id)
);
