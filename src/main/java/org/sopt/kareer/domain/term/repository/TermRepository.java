package org.sopt.kareer.domain.term.repository;

import org.sopt.kareer.domain.term.entity.Term;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TermRepository extends JpaRepository<Term, Long> {

    List<Term> findByActiveTrue();
}
