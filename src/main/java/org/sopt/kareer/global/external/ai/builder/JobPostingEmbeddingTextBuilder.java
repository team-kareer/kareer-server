package org.sopt.kareer.global.external.ai.builder;

import org.sopt.kareer.domain.jobposting.entity.JobPosting;

public class JobPostingEmbeddingTextBuilder {

    public static String buildEmbeddingText(JobPosting jp) {

        StringBuilder sb = new StringBuilder();

        append(sb, "Address", jp.getAddress());
        append(sb, "Arrangement", jp.getArrangement());
        append(sb, "Company", jp.getCompany());
        append(sb, "Title", jp.getPostTitle());
        append(sb, "PreferredLanguage", jp.getPreferredLanguage());
        append(sb, "PreferredVisa", jp.getPreferredVisa());
        append(sb, "Detail", jp.getDetail());
        append(sb, "Career", jp.getCareer());
        append(sb, "Education", jp.getEducation());


        return sb.toString();
    }

    private static void append(StringBuilder sb, String label, Object value) {
        if (value == null) return;
        String v = String.valueOf(value).trim();
        if (v.isBlank()) return;
        sb.append(label).append(": ").append(v).append("\n");
    }

}
