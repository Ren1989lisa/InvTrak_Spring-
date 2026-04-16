package com.example.integradora5d.models.checklist_resguardo;

import com.example.integradora5d.models.resguardo.BeanResguardo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "checklist_resguardo")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class BeanChecklist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idChecklist;

    private String observaciones;

    @Enumerated(EnumType.STRING)
    private ENUM_CHECKLIST enciende;

    @Enumerated(EnumType.STRING)
    private ENUM_CHECKLIST pantallaFunciona;

    @Enumerated(EnumType.STRING)
    private ENUM_CHECKLIST tieneCargador;

    @Enumerated(EnumType.STRING)
    private ENUM_CHECKLIST danios;

    @OneToOne
    @JoinColumn(name = "id_resguardo")
    @JsonIgnore
    private BeanResguardo resguardo;
}
