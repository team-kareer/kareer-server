package org.sopt.kareer.global.external.ai.evaluation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * 케이스별 원시 지표를 모아뒀다가, 파이프라인별(Policy RAG / Required Document RAG) 및 전체(Faithfulness/Answer Relevancy)
 * 평균을 콘솔에 리포트하고, 브라우저로 열어볼 수 있는 정적 HTML 리포트도 함께 남긴다.
 */
public class RagasReportPrinter {

    private static final Logger log = LoggerFactory.getLogger(RagasReportPrinter.class);

    private record CaseRow(
            String caseId,
            double policyPrecision,
            double policyRecall,
            double requiredDocPrecision,
            double requiredDocRecall,
            List<PhaseResult> phaseResults
    ) {
        double avgFaithfulness() {
            return phaseResults.stream().mapToDouble(PhaseResult::faithfulness).average().orElse(0.0);
        }

        double avgAnswerRelevancy() {
            return phaseResults.stream().mapToDouble(PhaseResult::answerRelevancy).average().orElse(0.0);
        }
    }

    private final List<CaseRow> rows = new ArrayList<>();

    public void recordCase(
            String caseId,
            double policyPrecision,
            double policyRecall,
            double requiredDocPrecision,
            double requiredDocRecall,
            List<PhaseResult> phaseResults
    ) {
        rows.add(new CaseRow(caseId, policyPrecision, policyRecall, requiredDocPrecision, requiredDocRecall, phaseResults));
    }

    public void printReport() {
        if (rows.isEmpty()) {
            log.warn("[RAGAS] no cases were evaluated");
            return;
        }

        log.info("========== RAGAS-equivalent evaluation report ({} cases) ==========", rows.size());
        for (CaseRow row : rows) {
            log.info(
                    "[case={}] policy(precision={}, recall={}) requiredDoc(precision={}, recall={}) generation-avg(faithfulness={}, answerRelevancy={})",
                    row.caseId(),
                    format(row.policyPrecision()), format(row.policyRecall()),
                    format(row.requiredDocPrecision()), format(row.requiredDocRecall()),
                    format(row.avgFaithfulness()), format(row.avgAnswerRelevancy())
            );
            for (PhaseResult phase : row.phaseResults()) {
                log.info(
                        "    phase[{}] \"{}\" faithfulness={} answerRelevancy={}",
                        phase.sequence(), phase.goal(),
                        format(phase.faithfulness()), format(phase.answerRelevancy())
                );
            }
        }

        for (PipelineMetrics metrics : pipelineAverages()) {
            log.info(
                    "[{}] contextPrecision={} contextRecall={} faithfulness={} answerRelevancy={} (n={})",
                    metrics.pipelineName(),
                    format(metrics.contextPrecision()),
                    format(metrics.contextRecall()),
                    format(metrics.faithfulness()),
                    format(metrics.answerRelevancy()),
                    metrics.caseCount()
            );
        }
        log.info("=====================================================================");
    }

    /**
     * 케이스별/파이프라인별 막대그래프가 그려진 자체완결형 HTML 리포트를 파일로 남긴다.
     * 브라우저로 바로 열어볼 수 있다 (외부 리소스 의존 없음).
     */
    public void writeHtmlReport(Path outputPath) throws IOException {
        if (outputPath.getParent() != null) {
            Files.createDirectories(outputPath.getParent());
        }
        String html = HTML_TEMPLATE.replace("__RAGAS_DATA__", toJson());
        Files.writeString(outputPath, html);
        log.info("[RAGAS] HTML report written to {}", outputPath.toAbsolutePath());
    }

    private List<PipelineMetrics> pipelineAverages() {
        if (rows.isEmpty()) {
            return List.of();
        }
        PipelineMetrics policy = new PipelineMetrics(
                "Policy RAG",
                average(CaseRow::policyPrecision),
                average(CaseRow::policyRecall),
                null,
                null,
                rows.size()
        );
        PipelineMetrics requiredDoc = new PipelineMetrics(
                "Required Document RAG",
                average(CaseRow::requiredDocPrecision),
                average(CaseRow::requiredDocRecall),
                null,
                null,
                rows.size()
        );
        PipelineMetrics overall = new PipelineMetrics(
                "Overall (generation)",
                null,
                null,
                average(CaseRow::avgFaithfulness),
                average(CaseRow::avgAnswerRelevancy),
                rows.size()
        );
        return List.of(policy, requiredDoc, overall);
    }

    private Double average(java.util.function.ToDoubleFunction<CaseRow> extractor) {
        return rows.stream().mapToDouble(extractor).average().orElse(0.0);
    }

    private String format(Double value) {
        return value == null ? "-" : String.format("%.2f", value);
    }

    private String toJson() {
        StringBuilder sb = new StringBuilder();
        sb.append("{\"generatedAt\":\"").append(Instant.now()).append("\",\"cases\":[");
        for (int i = 0; i < rows.size(); i++) {
            if (i > 0) {
                sb.append(",");
            }
            CaseRow r = rows.get(i);
            sb.append("{")
                    .append("\"caseId\":\"").append(escapeJson(r.caseId())).append("\",")
                    .append("\"policyPrecision\":").append(r.policyPrecision()).append(",")
                    .append("\"policyRecall\":").append(r.policyRecall()).append(",")
                    .append("\"requiredDocPrecision\":").append(r.requiredDocPrecision()).append(",")
                    .append("\"requiredDocRecall\":").append(r.requiredDocRecall()).append(",")
                    .append("\"faithfulness\":").append(r.avgFaithfulness()).append(",")
                    .append("\"answerRelevancy\":").append(r.avgAnswerRelevancy()).append(",")
                    .append("\"phases\":[");
            List<PhaseResult> phases = r.phaseResults();
            for (int p = 0; p < phases.size(); p++) {
                if (p > 0) {
                    sb.append(",");
                }
                PhaseResult ph = phases.get(p);
                sb.append("{")
                        .append("\"sequence\":").append(ph.sequence()).append(",")
                        .append("\"goal\":\"").append(escapeJson(ph.goal())).append("\",")
                        .append("\"faithfulness\":").append(ph.faithfulness()).append(",")
                        .append("\"answerRelevancy\":").append(ph.answerRelevancy())
                        .append("}");
            }
            sb.append("]}");
        }
        sb.append("]}");
        return sb.toString();
    }

    private String escapeJson(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    private static final String HTML_TEMPLATE = """
            <!doctype html>
            <html lang="ko">
            <head>
            <meta charset="utf-8">
            <title>RAGAS-equivalent Evaluation Report</title>
            <style>
              :root {
                color-scheme: light;
                --surface-1:      #fcfcfb;
                --page-plane:     #f9f9f7;
                --text-primary:   #0b0b0b;
                --text-secondary: #52514e;
                --text-muted:     #898781;
                --gridline:       #e1e0d9;
                --baseline:       #c3c2b7;
                --border:         rgba(11,11,11,0.10);
                --series-precision: #2a78d6;
                --series-recall:    #eb6834;
                --series-faithfulness: #1baf7a;
                --series-relevancy:    #eda100;
                --status-good:      #0ca30c;
                --status-critical:  #d03b3b;
              }
              @media (prefers-color-scheme: dark) {
                :root:where(:not([data-theme="light"])) {
                  color-scheme: dark;
                  --surface-1:      #1a1a19;
                  --page-plane:     #0d0d0d;
                  --text-primary:   #ffffff;
                  --text-secondary: #c3c2b7;
                  --text-muted:     #898781;
                  --gridline:       #2c2c2a;
                  --baseline:       #383835;
                  --border:         rgba(255,255,255,0.10);
                  --series-precision: #3987e5;
                  --series-recall:    #d95926;
                  --series-faithfulness: #199e70;
                  --series-relevancy:    #c98500;
                }
              }
              :root[data-theme="dark"] {
                color-scheme: dark;
                --surface-1:      #1a1a19;
                --page-plane:     #0d0d0d;
                --text-primary:   #ffffff;
                --text-secondary: #c3c2b7;
                --text-muted:     #898781;
                --gridline:       #2c2c2a;
                --baseline:       #383835;
                --border:         rgba(255,255,255,0.10);
                --series-precision: #3987e5;
                --series-recall:    #d95926;
                --series-faithfulness: #199e70;
                --series-relevancy:    #c98500;
              }

              * { box-sizing: border-box; }
              html, body {
                margin: 0; padding: 0;
                background: var(--page-plane);
                color: var(--text-primary);
                font-family: system-ui, -apple-system, "Segoe UI", sans-serif;
              }
              body { padding: 32px 24px 64px; }
              .page { max-width: 1040px; margin: 0 auto; }

              h1 { font-size: 22px; font-weight: 650; margin: 0 0 4px; }
              .subtitle { color: var(--text-secondary); font-size: 13px; margin: 0 0 28px; }

              .card {
                background: var(--surface-1);
                border: 1px solid var(--border);
                border-radius: 12px;
                padding: 20px 20px 16px;
                margin-bottom: 20px;
              }
              .card h2 { font-size: 14px; font-weight: 600; margin: 0 0 2px; }
              .card .card-sub { font-size: 12px; color: var(--text-muted); margin: 0 0 16px; }

              .stat-grid {
                display: grid;
                grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
                gap: 1px;
                background: var(--border);
                border: 1px solid var(--border);
                border-radius: 12px;
                overflow: hidden;
                margin-bottom: 20px;
              }
              .stat-tile { background: var(--surface-1); padding: 16px 18px; }
              .stat-tile .stat-label {
                font-size: 12px; color: var(--text-secondary);
                display: flex; align-items: center; gap: 6px;
                margin-bottom: 8px;
              }
              .stat-tile .stat-swatch { width: 8px; height: 8px; border-radius: 2px; flex: none; }
              .stat-tile .stat-value { font-size: 26px; font-weight: 650; font-variant-numeric: proportional-nums; }
              .stat-tile .stat-value.na { color: var(--text-muted); font-size: 20px; font-weight: 500; }

              .legend { display: flex; gap: 16px; margin-bottom: 14px; flex-wrap: wrap; }
              .legend-item { display: flex; align-items: center; gap: 6px; font-size: 12px; color: var(--text-secondary); }
              .legend-swatch { width: 10px; height: 10px; border-radius: 2px; flex: none; }

              .chart-scroll { overflow-x: auto; }
              svg.chart { display: block; overflow: visible; }
              .bar-value-label { font-size: 10px; fill: var(--text-secondary); font-variant-numeric: tabular-nums; text-anchor: middle; }
              .axis-label { font-size: 11px; fill: var(--text-muted); }
              .case-label { font-size: 11px; fill: var(--text-secondary); text-anchor: middle; }
              .gridline { stroke: var(--gridline); stroke-width: 1; shape-rendering: crispEdges; }
              .baseline { stroke: var(--baseline); stroke-width: 1; shape-rendering: crispEdges; }
              .bar-group { cursor: pointer; }
              .bar-rect { transition: opacity .1s ease; }
              .bar-group:hover .bar-rect, .bar-group:focus .bar-rect { opacity: 0.75; }
              .bar-group:focus { outline: none; }

              .tooltip {
                position: fixed; pointer-events: none;
                background: var(--surface-1); border: 1px solid var(--border);
                border-radius: 8px; padding: 8px 10px; font-size: 12px;
                box-shadow: 0 4px 16px rgba(0,0,0,0.12);
                opacity: 0; transform: translate(-50%, -100%);
                transition: opacity .08s ease; z-index: 10; white-space: nowrap;
              }
              .tooltip.visible { opacity: 1; }
              .tooltip .tt-title { color: var(--text-secondary); font-size: 11px; margin-bottom: 4px; }
              .tooltip .tt-row { display: flex; align-items: center; gap: 6px; }
              .tooltip .tt-row + .tt-row { margin-top: 3px; }
              .tooltip .tt-key { width: 10px; height: 2px; border-radius: 1px; flex: none; }
              .tooltip .tt-val { font-weight: 650; font-variant-numeric: tabular-nums; }
              .tooltip .tt-name { color: var(--text-secondary); }

              .table-toggle {
                font-size: 12px; color: var(--text-secondary);
                background: none; border: 1px solid var(--border); border-radius: 6px;
                padding: 4px 10px; cursor: pointer; margin-bottom: 14px;
              }
              .table-toggle:hover { background: var(--gridline); }
              table.data-table { width: 100%; border-collapse: collapse; font-size: 12px; display: none; }
              table.data-table.visible { display: table; }
              table.data-table th, table.data-table td {
                text-align: right; padding: 6px 10px; border-bottom: 1px solid var(--gridline);
                font-variant-numeric: tabular-nums;
              }
              table.data-table th:first-child, table.data-table td:first-child { text-align: left; font-variant-numeric: initial; }
              table.data-table th { color: var(--text-muted); font-weight: 500; }

              /* Phase-level detail table — always visible, sorted worst-first */
              table.phase-table { width: 100%; border-collapse: collapse; font-size: 12px; }
              table.phase-table th, table.phase-table td {
                text-align: left; padding: 7px 10px; border-bottom: 1px solid var(--gridline);
              }
              table.phase-table th { color: var(--text-muted); font-weight: 500; }
              table.phase-table td.num { text-align: right; font-variant-numeric: tabular-nums; }
              table.phase-table td.case-cell { color: var(--text-secondary); white-space: nowrap; }
              table.phase-table td.goal-cell { max-width: 320px; }

              .pill {
                display: inline-flex; align-items: center; gap: 5px;
                padding: 2px 8px; border-radius: 999px; font-size: 11px; font-weight: 600;
              }
              .pill.pass { background: color-mix(in srgb, var(--status-good) 16%, transparent); color: var(--status-good); }
              .pill.fail { background: color-mix(in srgb, var(--status-critical) 16%, transparent); color: var(--status-critical); }

              .empty-state { color: var(--text-muted); font-size: 13px; padding: 24px 0; text-align: center; }
            </style>
            </head>
            <body>
            <div class="page">
              <h1>RAGAS-equivalent Evaluation Report</h1>
              <p class="subtitle" id="subtitle"></p>

              <div class="stat-grid" id="stat-grid"></div>

              <div class="card">
                <h2>Policy RAG — retrieval quality by case</h2>
                <p class="card-sub">Context Precision / Context Recall, 검색된 정책 문서 기준</p>
                <div class="legend">
                  <span class="legend-item"><span class="legend-swatch" style="background:var(--series-precision)"></span>Context Precision</span>
                  <span class="legend-item"><span class="legend-swatch" style="background:var(--series-recall)"></span>Context Recall</span>
                </div>
                <button class="table-toggle" data-target="table-policy">테이블로 보기</button>
                <div class="chart-scroll"><div id="chart-policy"></div></div>
                <table class="data-table" id="table-policy">
                  <thead><tr><th>Case</th><th>Precision</th><th>Recall</th></tr></thead>
                  <tbody></tbody>
                </table>
              </div>

              <div class="card">
                <h2>Required Document RAG — retrieval quality by case</h2>
                <p class="card-sub">Context Precision / Context Recall, 검색된 비자·커리어 필수서류 문서 기준</p>
                <div class="legend">
                  <span class="legend-item"><span class="legend-swatch" style="background:var(--series-precision)"></span>Context Precision</span>
                  <span class="legend-item"><span class="legend-swatch" style="background:var(--series-recall)"></span>Context Recall</span>
                </div>
                <button class="table-toggle" data-target="table-required">테이블로 보기</button>
                <div class="chart-scroll"><div id="chart-required"></div></div>
                <table class="data-table" id="table-required">
                  <thead><tr><th>Case</th><th>Precision</th><th>Recall</th></tr></thead>
                  <tbody></tbody>
                </table>
              </div>

              <div class="card">
                <h2>Overall generation quality by case</h2>
                <p class="card-sub">Faithfulness / Answer Relevancy, 최종 생성된 로드맵 기준 (파이프라인 구분 없음)</p>
                <div class="legend">
                  <span class="legend-item"><span class="legend-swatch" style="background:var(--series-faithfulness)"></span>Faithfulness</span>
                  <span class="legend-item"><span class="legend-swatch" style="background:var(--series-relevancy)"></span>Answer Relevancy</span>
                </div>
                <button class="table-toggle" data-target="table-overall">테이블로 보기</button>
                <div class="chart-scroll"><div id="chart-overall"></div></div>
                <table class="data-table" id="table-overall">
                  <thead><tr><th>Case</th><th>Faithfulness</th><th>Answer Relevancy</th></tr></thead>
                  <tbody></tbody>
                </table>
              </div>

              <div class="card">
                <h2>Phase-level generation quality</h2>
                <p class="card-sub">Phase 하나하나를 개별 claim으로 판정 (근거 부족한 Phase가 위로 정렬됨)</p>
                <div class="chart-scroll">
                  <table class="phase-table" id="table-phases">
                    <thead>
                      <tr><th>Case</th><th>Phase</th><th>Goal</th><th class="num">Faithfulness</th><th class="num">Answer Relevancy</th></tr>
                    </thead>
                    <tbody id="table-phases-body"></tbody>
                  </table>
                </div>
              </div>
            </div>

            <div class="tooltip" id="tooltip" role="status" aria-live="polite"></div>

            <script>
            const RAGAS_DATA = __RAGAS_DATA__;

            (function () {
              const data = RAGAS_DATA;
              const cases = data.cases;

              document.getElementById('subtitle').textContent =
                cases.length + ' cases · generated ' + data.generatedAt;

              function avg(arr, fn) {
                if (arr.length === 0) return null;
                return arr.reduce((s, d) => s + fn(d), 0) / arr.length;
              }
              function fmt(v) { return v === null || v === undefined ? '-' : v.toFixed(2); }

              const summary = [
                { label: 'Policy RAG — Precision', value: avg(cases, d => d.policyPrecision), color: 'var(--series-precision)' },
                { label: 'Policy RAG — Recall', value: avg(cases, d => d.policyRecall), color: 'var(--series-recall)' },
                { label: 'Required Doc RAG — Precision', value: avg(cases, d => d.requiredDocPrecision), color: 'var(--series-precision)' },
                { label: 'Required Doc RAG — Recall', value: avg(cases, d => d.requiredDocRecall), color: 'var(--series-recall)' },
                { label: 'Overall — Faithfulness', value: avg(cases, d => d.faithfulness), color: 'var(--series-faithfulness)' },
                { label: 'Overall — Answer Relevancy', value: avg(cases, d => d.answerRelevancy), color: 'var(--series-relevancy)' }
              ];

              const statGrid = document.getElementById('stat-grid');
              summary.forEach(s => {
                const tile = document.createElement('div');
                tile.className = 'stat-tile';
                const label = document.createElement('div');
                label.className = 'stat-label';
                const swatch = document.createElement('span');
                swatch.className = 'stat-swatch';
                swatch.style.background = s.color;
                label.appendChild(swatch);
                label.appendChild(document.createTextNode(s.label));
                const value = document.createElement('div');
                value.className = 'stat-value' + (s.value === null ? ' na' : '');
                value.textContent = s.value === null ? 'no cases' : fmt(s.value);
                tile.appendChild(label);
                tile.appendChild(value);
                statGrid.appendChild(tile);
              });

              const tooltip = document.getElementById('tooltip');
              function showTooltip(evt, title, rows) {
                tooltip.innerHTML = '';
                const t = document.createElement('div');
                t.className = 'tt-title';
                t.textContent = title;
                tooltip.appendChild(t);
                rows.forEach(r => {
                  const row = document.createElement('div');
                  row.className = 'tt-row';
                  const key = document.createElement('span');
                  key.className = 'tt-key';
                  key.style.background = r.color;
                  const name = document.createElement('span');
                  name.className = 'tt-name';
                  name.textContent = r.name + ': ';
                  const val = document.createElement('span');
                  val.className = 'tt-val';
                  val.textContent = r.value;
                  row.appendChild(key);
                  row.appendChild(name);
                  row.appendChild(val);
                  tooltip.appendChild(row);
                });
                tooltip.style.left = evt.clientX + 'px';
                tooltip.style.top = (evt.clientY - 12) + 'px';
                tooltip.classList.add('visible');
              }
              function hideTooltip() { tooltip.classList.remove('visible'); }

              function renderGroupedBarChart(containerId, series) {
                const container = document.getElementById(containerId);
                if (cases.length === 0) {
                  const empty = document.createElement('div');
                  empty.className = 'empty-state';
                  empty.textContent = '평가된 케이스가 없습니다.';
                  container.appendChild(empty);
                  return;
                }

                const barW = 20, barGap = 2, groupGap = 28;
                const groupW = series.length * barW + (series.length - 1) * barGap;
                const plotH = 180, axisH = 34, topPad = 10, leftPad = 34;
                const plotW = cases.length * (groupW + groupGap) + groupGap;
                const svgW = plotW + leftPad + 8;
                const svgH = plotH + axisH + topPad;

                const svg = document.createElementNS('http://www.w3.org/2000/svg', 'svg');
                svg.setAttribute('class', 'chart');
                svg.setAttribute('width', svgW);
                svg.setAttribute('height', svgH);
                svg.setAttribute('viewBox', '0 0 ' + svgW + ' ' + svgH);

                const yScale = v => topPad + plotH - v * plotH;

                [0, 0.25, 0.5, 0.75, 1].forEach(t => {
                  const y = yScale(t);
                  const line = document.createElementNS(svg.namespaceURI, 'line');
                  line.setAttribute('class', t === 0 ? 'baseline' : 'gridline');
                  line.setAttribute('x1', leftPad);
                  line.setAttribute('x2', svgW);
                  line.setAttribute('y1', y);
                  line.setAttribute('y2', y);
                  svg.appendChild(line);

                  const label = document.createElementNS(svg.namespaceURI, 'text');
                  label.setAttribute('class', 'axis-label');
                  label.setAttribute('x', leftPad - 8);
                  label.setAttribute('y', y + 3);
                  label.setAttribute('text-anchor', 'end');
                  label.textContent = t.toFixed(2);
                  svg.appendChild(label);
                });

                cases.forEach((d, i) => {
                  const groupX = leftPad + groupGap + i * (groupW + groupGap);
                  const g = document.createElementNS(svg.namespaceURI, 'g');
                  g.setAttribute('class', 'bar-group');
                  g.setAttribute('tabindex', '0');

                  const tooltipRows = series.map(s => ({
                    name: s.name, color: s.color, value: s.get(d).toFixed(2)
                  }));

                  series.forEach((s, si) => {
                    const val = s.get(d);
                    const x = groupX + si * (barW + barGap);
                    const y = yScale(val);
                    const h = Math.max(0, yScale(0) - y);

                    const rect = document.createElementNS(svg.namespaceURI, 'rect');
                    rect.setAttribute('class', 'bar-rect');
                    rect.setAttribute('x', x);
                    rect.setAttribute('y', y);
                    rect.setAttribute('width', barW);
                    rect.setAttribute('height', h);
                    rect.setAttribute('rx', 4);
                    rect.setAttribute('ry', 4);
                    rect.setAttribute('fill', s.color);
                    g.appendChild(rect);

                    if (h > 4) {
                      const squareBottom = document.createElementNS(svg.namespaceURI, 'rect');
                      squareBottom.setAttribute('x', x);
                      squareBottom.setAttribute('y', yScale(0) - 4);
                      squareBottom.setAttribute('width', barW);
                      squareBottom.setAttribute('height', 4);
                      squareBottom.setAttribute('fill', s.color);
                      g.appendChild(squareBottom);
                    }

                    const valueLabel = document.createElementNS(svg.namespaceURI, 'text');
                    valueLabel.setAttribute('class', 'bar-value-label');
                    valueLabel.setAttribute('x', x + barW / 2);
                    valueLabel.setAttribute('y', y - 5);
                    valueLabel.textContent = val.toFixed(2);
                    g.appendChild(valueLabel);
                  });

                  const caseLabel = document.createElementNS(svg.namespaceURI, 'text');
                  caseLabel.setAttribute('class', 'case-label');
                  caseLabel.setAttribute('x', groupX + groupW / 2);
                  caseLabel.setAttribute('y', plotH + topPad + 16);
                  const shortId = d.caseId.length > 22 ? d.caseId.slice(0, 20) + '…' : d.caseId;
                  caseLabel.textContent = shortId;
                  svg.appendChild(caseLabel);
                  const titleEl = document.createElementNS(svg.namespaceURI, 'title');
                  titleEl.textContent = d.caseId;
                  caseLabel.appendChild(titleEl);

                  g.addEventListener('pointermove', evt => showTooltip(evt, d.caseId, tooltipRows));
                  g.addEventListener('pointerleave', hideTooltip);
                  g.addEventListener('focus', evt => showTooltip(evt, d.caseId, tooltipRows));
                  g.addEventListener('blur', hideTooltip);

                  svg.appendChild(g);
                });

                container.appendChild(svg);
              }

              renderGroupedBarChart('chart-policy', [
                { name: 'Context Precision', color: 'var(--series-precision)', get: d => d.policyPrecision },
                { name: 'Context Recall', color: 'var(--series-recall)', get: d => d.policyRecall }
              ]);

              renderGroupedBarChart('chart-required', [
                { name: 'Context Precision', color: 'var(--series-precision)', get: d => d.requiredDocPrecision },
                { name: 'Context Recall', color: 'var(--series-recall)', get: d => d.requiredDocRecall }
              ]);

              renderGroupedBarChart('chart-overall', [
                { name: 'Faithfulness', color: 'var(--series-faithfulness)', get: d => d.faithfulness },
                { name: 'Answer Relevancy', color: 'var(--series-relevancy)', get: d => d.answerRelevancy }
              ]);

              function pill(value) {
                const isPass = value === 1;
                const span = document.createElement('span');
                span.className = 'pill ' + (isPass ? 'pass' : 'fail');
                span.textContent = (isPass ? '✓ Pass' : '✗ Fail') + ' (' + value.toFixed(2) + ')';
                return span;
              }

              function renderPhaseTable() {
                const tbody = document.getElementById('table-phases-body');
                const flat = [];
                cases.forEach(d => {
                  (d.phases || []).forEach(p => {
                    flat.push({
                      caseId: d.caseId, sequence: p.sequence, goal: p.goal,
                      faithfulness: p.faithfulness, answerRelevancy: p.answerRelevancy
                    });
                  });
                });
                // 근거 부족한(faithfulness+relevancy 합이 낮은) Phase가 위로 오도록 정렬
                flat.sort((a, b) => (a.faithfulness + a.answerRelevancy) - (b.faithfulness + b.answerRelevancy));

                if (flat.length === 0) {
                  const tr = document.createElement('tr');
                  const td = document.createElement('td');
                  td.colSpan = 5;
                  td.className = 'empty-state';
                  td.textContent = '평가된 Phase가 없습니다.';
                  tr.appendChild(td);
                  tbody.appendChild(tr);
                  return;
                }

                flat.forEach(row => {
                  const tr = document.createElement('tr');

                  const tdCase = document.createElement('td');
                  tdCase.className = 'case-cell';
                  tdCase.textContent = row.caseId;
                  tr.appendChild(tdCase);

                  const tdSeq = document.createElement('td');
                  tdSeq.className = 'num';
                  tdSeq.textContent = row.sequence;
                  tr.appendChild(tdSeq);

                  const tdGoal = document.createElement('td');
                  tdGoal.className = 'goal-cell';
                  tdGoal.textContent = row.goal;
                  tr.appendChild(tdGoal);

                  const tdFaith = document.createElement('td');
                  tdFaith.className = 'num';
                  tdFaith.appendChild(pill(row.faithfulness));
                  tr.appendChild(tdFaith);

                  const tdRel = document.createElement('td');
                  tdRel.className = 'num';
                  tdRel.appendChild(pill(row.answerRelevancy));
                  tr.appendChild(tdRel);

                  tbody.appendChild(tr);
                });
              }
              renderPhaseTable();

              function fillTable(tableId, cols) {
                const tbody = document.querySelector('#' + tableId + ' tbody');
                cases.forEach(d => {
                  const tr = document.createElement('tr');
                  const tdCase = document.createElement('td');
                  tdCase.textContent = d.caseId;
                  tr.appendChild(tdCase);
                  cols.forEach(c => {
                    const td = document.createElement('td');
                    td.textContent = fmt(c.get(d));
                    tr.appendChild(td);
                  });
                  tbody.appendChild(tr);
                });
              }
              fillTable('table-policy', [{ get: d => d.policyPrecision }, { get: d => d.policyRecall }]);
              fillTable('table-required', [{ get: d => d.requiredDocPrecision }, { get: d => d.requiredDocRecall }]);
              fillTable('table-overall', [{ get: d => d.faithfulness }, { get: d => d.answerRelevancy }]);

              document.querySelectorAll('.table-toggle').forEach(btn => {
                btn.addEventListener('click', () => {
                  const target = document.getElementById(btn.dataset.target);
                  const willShow = !target.classList.contains('visible');
                  target.classList.toggle('visible', willShow);
                  btn.textContent = willShow ? '차트로 보기' : '테이블로 보기';
                });
              });
            })();
            </script>
            </body>
            </html>
            """;
}
