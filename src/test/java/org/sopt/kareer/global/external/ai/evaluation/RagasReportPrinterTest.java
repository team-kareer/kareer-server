package org.sopt.kareer.global.external.ai.evaluation;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class RagasReportPrinterTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @DisplayName("Phase goal에 개행/탭이 있어도 JSON 문자열 리터럴이 깨지지 않는다.")
    @Test
    void writeHtmlReport_escapesControlCharacters(@TempDir Path tempDir) throws IOException {
        // given
        RagasReportPrinter printer = new RagasReportPrinter(objectMapper);
        String goalWithControlChars = "line1\nline2\tindented";
        printer.recordCase(
                "case-1", 1.0, 1.0, 1.0, 1.0,
                List.of(new PhaseResult(1, goalWithControlChars, 1.0, 1.0))
        );
        Path reportPath = tempDir.resolve("report.html");

        // when
        printer.writeHtmlReport(reportPath);

        // then
        String html = Files.readString(reportPath);
        // 원본 개행/탭이 JS 문자열 리터럴 안에 그대로 남아있지 않고, JSON 이스케이프 시퀀스로 변환되어야 한다.
        assertThat(html).contains("line1\\nline2\\tindented");
        assertThat(html).doesNotContain("line1\nline2\tindented");
    }

    @DisplayName("Phase goal에 </script>가 있어도 script 태그가 조기 종료되지 않는다.")
    @Test
    void writeHtmlReport_escapesScriptClosingTag(@TempDir Path tempDir) throws IOException {
        // given
        RagasReportPrinter printer = new RagasReportPrinter(objectMapper);
        String maliciousGoal = "</script><script>alert(1)</script>";
        printer.recordCase(
                "case-1", 1.0, 1.0, 1.0, 1.0,
                List.of(new PhaseResult(1, maliciousGoal, 1.0, 1.0))
        );
        Path reportPath = tempDir.resolve("report.html");

        // when
        printer.writeHtmlReport(reportPath);

        // then
        String html = Files.readString(reportPath);
        // 원본 페이로드의 '<'는 전부 유니코드 이스케이프(\u003c)로 치환되어, 리포트 안에 주입된
        // </script>가 원문 그대로(비-이스케이프 상태로) 남아있으면 안 된다.
        assertThat(html).doesNotContain("alert(1)</script>");
        assertThat(html).contains("\\u003c/script>\\u003cscript>alert(1)\\u003c/script>");
    }
}
