package org.sopt.kareer.global.config.flyway;

import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

class ActionItemPhaseActionMigrationTest {

    private static final String MIGRATION_PATH =
            "db/migration/V4__allow_action_item_without_phase_action.sql";

    @Test
    void PhaseAction이_없는_사용자_ActionItem을_저장할_수_있다() throws Exception {
        try (Connection connection = DriverManager.getConnection(
                "jdbc:h2:mem:legacy-action-item-schema;MODE=PostgreSQL",
                "sa",
                ""
        ); Statement statement = connection.createStatement()) {
            statement.execute("""
                    CREATE TABLE action_items
                    (
                        action_item_id   BIGINT PRIMARY KEY,
                        title            VARCHAR(255),
                        actions_type     VARCHAR(50) NOT NULL,
                        status           VARCHAR(50) NOT NULL,
                        deadline         DATE NOT NULL,
                        completed        BOOLEAN NOT NULL,
                        member_id        BIGINT NOT NULL,
                        phase_actions_id BIGINT NOT NULL,
                        created_at       TIMESTAMP NOT NULL,
                        updated_at       TIMESTAMP NOT NULL
                    )
                    """);

            statement.execute(loadMigration());

            try (ResultSet resultSet = statement.executeQuery("""
                    SELECT is_nullable
                    FROM information_schema.columns
                    WHERE LOWER(table_name) = 'action_items'
                      AND LOWER(column_name) = 'phase_actions_id'
                    """)) {
                resultSet.next();
                assertThat(resultSet.getString(1)).isEqualTo("YES");
            }

            assertThatCode(() -> statement.executeUpdate("""
                    INSERT INTO action_items
                        (action_item_id, title, actions_type, status, deadline,
                         completed, member_id, phase_actions_id, created_at, updated_at)
                    VALUES
                        (1, '사용자 Todo', 'CAREER', 'ACTIVE', CURRENT_DATE + 1,
                         FALSE, 1, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
                    """))
                    .doesNotThrowAnyException();
        }
    }

    private String loadMigration() throws Exception {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(MIGRATION_PATH);
        assertThat(inputStream)
                .as("%s 마이그레이션이 존재해야 한다", MIGRATION_PATH)
                .isNotNull();

        try (InputStream migration = Objects.requireNonNull(inputStream)) {
            return new String(migration.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}
