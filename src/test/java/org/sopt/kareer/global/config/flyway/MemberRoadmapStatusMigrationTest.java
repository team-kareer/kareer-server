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

class MemberRoadmapStatusMigrationTest {

    private static final String MIGRATION_PATH =
            "db/migration/V3__drop_member_roadmap_status.sql";

    @Test
    void 기존_roadmap_status_컬럼을_제거해_신규_회원을_저장할_수_있다() throws Exception {
        try (Connection connection = DriverManager.getConnection(
                "jdbc:h2:mem:legacy-member-schema;MODE=PostgreSQL",
                "sa",
                ""
        ); Statement statement = connection.createStatement()) {
            statement.execute("""
                    CREATE TABLE members
                    (
                        member_id     BIGINT PRIMARY KEY,
                        name          VARCHAR(255) NOT NULL,
                        status        VARCHAR(50) NOT NULL,
                        provider      VARCHAR(50) NOT NULL,
                        provider_id   VARCHAR(255) NOT NULL,
                        roadmap_status VARCHAR(50) NOT NULL,
                        created_at    TIMESTAMP NOT NULL,
                        updated_at    TIMESTAMP NOT NULL
                    )
                    """);

            statement.execute(loadMigration());

            try (ResultSet resultSet = statement.executeQuery("""
                    SELECT COUNT(*)
                    FROM information_schema.columns
                    WHERE LOWER(table_name) = 'members'
                      AND LOWER(column_name) = 'roadmap_status'
                    """)) {
                resultSet.next();
                assertThat(resultSet.getInt(1)).isZero();
            }

            assertThatCode(() -> statement.executeUpdate("""
                    INSERT INTO members
                        (member_id, name, status, provider, provider_id, created_at, updated_at)
                    VALUES
                        (1, '재가입 회원', 'PENDING', 'GOOGLE', 'google-provider-id', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
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
