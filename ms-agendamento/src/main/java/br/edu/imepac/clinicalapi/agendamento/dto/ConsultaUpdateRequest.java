package br.edu.imepac.clinicalapi.agendamento.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ConsultaUpdateRequest {

    @NotNull(message = "é obrigatório")
    @Future(message = "deve ser uma data futura")
    private LocalDateTime dataAgendamento;

    @Size(max = 500, message = "deve ter no máximo 500 caracteres")
    private String sintomas;

    @Size(max = 500, message = "deve ter no máximo 500 caracteres")
    private String observacoes;

    @Size(max = 50, message = "deve ter no máximo 50 caracteres")
    private String numeroCarteiraPlanoSaude;
}
