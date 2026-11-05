package br.edu.imepac.clinicalapi.agendamento.entities;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@DiscriminatorValue("CONSULTA")
public class Consulta extends Agendamento {

    @Column(length = 500)
    private String sintomas;
}
