package br.edu.imepac.clinicalapi.administrativo.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MedicoResponse extends PessoaResponse {

    private String crm;
    private EspecialidadeResponse especialidade;
}
