package org.sopt.kareer.domain.actionitem.repository;

import org.sopt.kareer.domain.actionitem.entity.ActionItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActionItemRepository extends JpaRepository<ActionItem, Long> {
}
