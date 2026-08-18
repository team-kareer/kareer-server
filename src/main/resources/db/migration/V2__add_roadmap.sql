-- =============================================================
-- V2: Roadmap 엔티티 도입
-- phases.member_id → phases.roadmap_id 로 연관관계 변경
-- 기존 데이터: 멤버별로 ACTIVE 로드맵을 하나씩 생성한 뒤 phases에 연결
-- =============================================================

-- -------------------------
-- 1. roadmaps 테이블 생성
-- -------------------------
CREATE TABLE roadmaps
(
    roadmap_id BIGSERIAL PRIMARY KEY,
    member_id  BIGINT      NOT NULL REFERENCES members (member_id),
    status     VARCHAR(50) NOT NULL,
    created_at TIMESTAMP   NOT NULL,
    updated_at TIMESTAMP   NOT NULL
);

-- -------------------------
-- 2. phases에 roadmap_id 컬럼 추가 (백필 전 nullable)
-- -------------------------
ALTER TABLE phases
    ADD COLUMN roadmap_id BIGINT;

-- -------------------------
-- 3. 기존 phases의 member_id 기준으로 roadmap을 생성하고 roadmap_id 백필
--    멤버당 정확히 하나의 ACTIVE 로드맵을 생성
-- -------------------------
WITH inserted_roadmaps AS (
    INSERT INTO roadmaps (member_id, status, created_at, updated_at)
        SELECT DISTINCT member_id, 'ACTIVE', NOW(), NOW()
        FROM phases
        RETURNING roadmap_id, member_id
)
UPDATE phases p
SET roadmap_id = ir.roadmap_id
FROM inserted_roadmaps ir
WHERE p.member_id = ir.member_id;

-- -------------------------
-- 4. roadmap_id NOT NULL 제약 추가
-- -------------------------
ALTER TABLE phases
    ALTER COLUMN roadmap_id SET NOT NULL;

-- -------------------------
-- 5. roadmaps 테이블에 FK 제약 추가
-- -------------------------
ALTER TABLE phases
    ADD CONSTRAINT fk_phases_roadmap FOREIGN KEY (roadmap_id) REFERENCES roadmaps (roadmap_id);

-- -------------------------
-- 6. 기존 member_id FK 제약 및 컬럼 제거
--    Hibernate가 auto-generated 이름으로 제약을 생성했으므로 동적으로 조회 후 삭제
-- -------------------------
DO
$$
    DECLARE
        v_constraint_name TEXT;
    BEGIN
        SELECT tc.constraint_name
        INTO v_constraint_name
        FROM information_schema.table_constraints tc
                 JOIN information_schema.key_column_usage kcu
                      ON tc.constraint_name = kcu.constraint_name
                          AND tc.table_schema = kcu.table_schema
        WHERE tc.table_name = 'phases'
          AND tc.constraint_type = 'FOREIGN KEY'
          AND kcu.column_name = 'member_id';

        IF v_constraint_name IS NOT NULL THEN
            EXECUTE 'ALTER TABLE phases DROP CONSTRAINT ' || quote_ident(v_constraint_name);
        END IF;
    END
$$;

ALTER TABLE phases
    DROP COLUMN member_id;
