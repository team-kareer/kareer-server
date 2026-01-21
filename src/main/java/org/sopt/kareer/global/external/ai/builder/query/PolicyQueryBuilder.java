package org.sopt.kareer.global.external.ai.builder.query;

import org.sopt.kareer.domain.member.entity.Member;
import org.sopt.kareer.domain.member.entity.MemberVisa;

public class PolicyQueryBuilder {

    public static String buildPolicyQuery(Member member, MemberVisa visa) {
        return String.join(" | ",
                "Korea visa policy and employment rules",
                "visaType=" + (visa == null ? "" : visa.getVisaType().name()),
                "targetJob=" + nullSafe(member.getTargetJob()),
                "degree=" + (member.getDegree() == null ? "" : member.getDegree().name()),
                "graduation=" + (member.getExpectedGraduationDate() == null ? "" : member.getExpectedGraduationDate().toString())
        );
    }

    private static String nullSafe(String v) {
        return v == null ? "" : v;
    }
}
