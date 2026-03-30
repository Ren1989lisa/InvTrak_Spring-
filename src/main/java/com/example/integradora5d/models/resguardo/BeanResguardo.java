package com.example.integradora5d.models.resguardo;

import com.example.integradora5d.models.activo.BeanActivo;
import com.example.integradora5d.models.aula_laboratorio.BeanAula;
import com.example.integradora5d.models.checklist_resguardo.BeanChecklist;
import com.example.integradora5d.models.usuario.BeanUsuario;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "resguardo")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class BeanResguardo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idResguardo;

    private LocalDate fechaAsignacion;
    private String observaciones;
    private LocalDate fechaDevolucion;

    @ManyToOne
    @JoinColumn(name = "id_usuario")
    private BeanUsuario usuario;

    @OneToOne(mappedBy = "resguardo")
    private BeanChecklist checklists;

    @ManyToOne
    @JoinColumn(name = "id_activo")
    private BeanActivo activo;

}
