package com.example.integradora5d.models.checklist_resguardo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChecklistRepository extends JpaRepository<BeanChecklist, Long> {
    void deleteByResguardo_IdResguardo(Long resguardoId);
}
