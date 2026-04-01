package org.sopt.kareer.domain.member.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "localized_onboard_category_translation")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LocalizedOnboardCategoryTranslation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private LocalizedOnboardCategory category;

    @Column(nullable = false)
    private String language;

    @Column(nullable = false)
    private String label;

    public LocalizedOnboardCategoryTranslation(String language, String label) {
        this.language = language;
        this.label = label;
    }

    void assignCategory(LocalizedOnboardCategory category) {
        this.category = category;
    }

    boolean hasLanguage(String targetLanguage) {
        return this.language.equalsIgnoreCase(targetLanguage);
    }

    void updateLabel(String label) {
        this.label = label;
    }
}
