package org.sopt.kareer.domain.member.entity.constants;

import java.util.List;

public final class Major {

    private Major() {
    }

    public static final List<String> MAJOR_LIST = List.of(
            // Engineering & CS
            "Computer Science",
            "Software Engineering",
            "Computer Engineering",
            "Information Technology",
            "Information Systems",
            "Information Security",
            "Data Science",
            "Artificial Intelligence",
            "Electrical Engineering",
            "Electronics Engineering",
            "Mechanical Engineering",
            "Civil Engineering",
            "Industrial Engineering",
            "Chemical Engineering",
            "Biomedical Engineering",
            "Environmental Engineering",

            // Natural Sciences
            "Mathematics",
            "Statistics",
            "Physics",
            "Chemistry",
            "Biology",
            "Biotechnology",
            "Environmental Science",

            // Business & Economics
            "Business Administration",
            "Management",
            "Economics",
            "Finance",
            "Accounting",
            "Marketing",
            "International Business",

            // Social Sciences
            "Political Science",
            "International Relations",
            "Sociology",
            "Psychology",
            "Communications",
            "Public Administration",

            // Arts & Humanities
            "English",
            "Linguistics",
            "History",
            "Philosophy",
            "Design",
            "Graphic Design",
            "Fine Arts",

            // Education & Health
            "Education",
            "Early Childhood Education",
            "Nursing",
            "Public Health",

            // Law & Misc
            "Law",
            "Criminal Justice",
            "Social Work"
    );
}
