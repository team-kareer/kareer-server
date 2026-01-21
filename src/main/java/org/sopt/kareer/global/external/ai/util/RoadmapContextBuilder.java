package org.sopt.kareer.global.external.ai.util;

import org.sopt.kareer.global.external.ai.service.RequiredDocumentRetriever;
import org.springframework.ai.document.Document;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
public class RoadmapContextBuilder {

    public RoadmapContext build(
            List<Document> visaRequiredAll,
            RequiredDocumentRetriever.CareerSelectedDocs careerSelected,
            List<Document> policyDocs
    ) {
        String visaCtx = formatDocs("REQUIRED_REFERENCE_DOCUMENTS - VISA (ALL)", visaRequiredAll);
        String careerActionCtx = formatDocs("REQUIRED_REFERENCE_DOCUMENTS - CAREER (Action Required)", careerSelected.actionRequired());
        String careerGuideCtx = formatDocs("REQUIRED_REFERENCE_DOCUMENTS - CAREER (AI Guide & Risk)", careerSelected.aiGuideRisk());
        String careerTodoCtx = formatDocs("REQUIRED_REFERENCE_DOCUMENTS - CAREER (To-do List)", careerSelected.todoList());
        String policyCtx = formatDocs("POLICY_DOCUMENTS - TOPK", policyDocs);

        return new RoadmapContext(visaCtx, careerActionCtx, careerGuideCtx, careerTodoCtx, policyCtx);
    }

    private String formatDocs(String title, List<Document> docs) {
        StringBuilder sb = new StringBuilder();
        sb.append("[").append(title).append("]\n");

        for (int i = 0; i < docs.size(); i++) {
            Document d = docs.get(i);

            sb.append("### chunk ").append(i).append("\n");

            sb.append("meta:")
                    .append(" label=").append(Objects.toString(d.getMetadata().get("label"), ""))
                    .append(", title=").append(Objects.toString(d.getMetadata().get("title"), ""))
                    .append(", domain=").append(Objects.toString(d.getMetadata().get("domain"), ""))
                    .append(", caseNo=").append(Objects.toString(d.getMetadata().get("caseNo"), ""))
                    .append("\n");

            sb.append(d.getText()).append("\n\n");
        }
        return sb.toString();
    }

    public record RoadmapContext(
            String visaRequiredContext,
            String careerActionContext,
            String careerGuideContext,
            String careerTodoContext,
            String policyContext
    ) {}
}
