package br.edu.imepac.clinicalapi.administrativo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EspecialidadeRequest {

    @NotBlank(message = "é obrigatório")
    @Size(max = 100, message = "deve ter no máximo 100 caracteres")
    private String nome;

    @Size(max = 255, message = "deve ter no máximo 255 caracteres")
    private String descricao;
}
