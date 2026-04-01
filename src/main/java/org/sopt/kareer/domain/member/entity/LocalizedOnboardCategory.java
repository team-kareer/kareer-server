package org.sopt.kareer.domain.member.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.sopt.kareer.domain.member.entity.enums.LocalizedOnboardCategoryType;

@Entity
@Table(name = "localized_onboard_category")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LocalizedOnboardCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LocalizedOnboardCategoryType type;

    @Column(nullable = false)
    private String code;

    @Column(name = "use_order", nullable = false)
    private int useOrder;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LocalizedOnboardCategoryTranslation> translations = new ArrayList<>();

    public LocalizedOnboardCategory(LocalizedOnboardCategoryType type, String code, int useOrder) {
        this.type = type;
        this.code = code;
        this.useOrder = useOrder;
    }

    public void addTranslation(String language, String label) {
        LocalizedOnboardCategoryTranslation translation = new LocalizedOnboardCategoryTranslation(language, label);
        translation.assignCategory(this);
        this.translations.add(translation);
    }

    public void updateTranslation(String language, String label) {
        this.translations.stream()
                .filter(t -> t.hasLanguage(language))
                .findFirst()
                .ifPresentOrElse(t -> t.updateLabel(label), () -> addTranslation(language, label));
    }
}
