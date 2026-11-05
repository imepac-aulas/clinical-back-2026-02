package br.edu.imepac.clinicalapi.administrativo.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "pacientes")
public class Paciente extends Pessoa {
}
