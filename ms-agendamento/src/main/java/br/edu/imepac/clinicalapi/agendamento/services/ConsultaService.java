package br.edu.imepac.clinicalapi.agendamento.services;

import br.edu.imepac.clinicalapi.agendamento.client.AdministrativoClient;
import br.edu.imepac.clinicalapi.agendamento.dto.ConsultaRequest;
import br.edu.imepac.clinicalapi.agendamento.dto.ConsultaResponse;
import br.edu.imepac.clinicalapi.agendamento.dto.ConsultaUpdateRequest;
import br.edu.imepac.clinicalapi.agendamento.entities.Consulta;
import br.edu.imepac.clinicalapi.agendamento.entities.StatusConsulta;
import br.edu.imepac.clinicalapi.agendamento.mapper.ConsultaMapper;
import br.edu.imepac.clinicalapi.agendamento.repositories.ConsultaRepository;
import br.edu.imepac.clinicalapi.common.exception.BusinessRuleException;
import br.edu.imepac.clinicalapi.common.exception.ConflictException;
import br.edu.imepac.clinicalapi.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConsultaService {

    private final ConsultaRepository repository;
    private final AdministrativoClient administrativoClient;
    private final ConsultaMapper mapper;

    @Transactional
    public ConsultaResponse agendar(ConsultaRequest request) {
        log.info("Agendando consulta: médico={}, paciente={}, data={}",
                request.getMedicoId(), request.getPacienteId(), request.getDataAgendamento());

        // comunicação com o ms-administrativo
        administrativoClient.buscarMedico(request.getMedicoId())
                .orElseThrow(() -> new BusinessRuleException("Médico não encontrado: " + request.getMedicoId()));
        administrativoClient.buscarPaciente(request.getPacienteId())
                .orElseThrow(() -> new BusinessRuleException("Paciente não encontrado: " + request.getPacienteId()));

        if (repository.existsByMedicoIdAndDataAgendamentoAndStatusIn(
                request.getMedicoId(), request.getDataAgendamento(), StatusConsulta.ATIVOS)) {
            throw horarioOcupado();
        }

        Consulta consulta = mapper.toEntity(request);
        consulta.setStatus(StatusConsulta.AGENDADO);
        return mapper.toResponse(repository.save(consulta));
    }

    @Transactional(readOnly = true)
    public Page<ConsultaResponse> listar(Long medicoId, Long pacienteId, StatusConsulta status, Pageable pageable) {
        return repository.buscar(medicoId, pacienteId, status, pageable).map(mapper::toResponse);
    }

    @Transactional(readOnly = true)
    public ConsultaResponse buscar(Long id) {
        return mapper.toResponse(buscarEntidade(id));
    }

    @Transactional
    public ConsultaResponse remarcar(Long id, ConsultaUpdateRequest request) {
        log.info("Remarcando consulta {} para {}", id, request.getDataAgendamento());
        Consulta consulta = buscarEntidade(id);
        exigirAtiva(consulta, "remarcada");

        LocalDateTime dataAnterior = consulta.getDataAgendamento();
        boolean mudouData = !dataAnterior.equals(request.getDataAgendamento());
        if (mudouData && repository.existsByMedicoIdAndDataAgendamentoAndStatusInAndIdNot(
                consulta.getMedicoId(), request.getDataAgendamento(), StatusConsulta.ATIVOS, id)) {
            throw horarioOcupado();
        }

        mapper.updateEntity(request, consulta);
        if (mudouData) {
            consulta.setStatus(StatusConsulta.REMARCADO);
        }
        return mapper.toResponse(repository.save(consulta));
    }

    @Transactional
    public ConsultaResponse cancelar(Long id) {
        log.info("Cancelando consulta {}", id);
        return mudarStatus(id, StatusConsulta.CANCELADO, "cancelada");
    }

    @Transactional
    public ConsultaResponse realizar(Long id) {
        log.info("Marcando consulta {} como realizada", id);
        return mudarStatus(id, StatusConsulta.REALIZADO, "realizada");
    }

    private ConsultaResponse mudarStatus(Long id, StatusConsulta novo, String acao) {
        Consulta consulta = buscarEntidade(id);
        exigirAtiva(consulta, acao);
        consulta.setStatus(novo);
        return mapper.toResponse(repository.save(consulta));
    }

    private void exigirAtiva(Consulta consulta, String acao) {
        if (!consulta.getStatus().isAtivo()) {
            throw new BusinessRuleException(
                    "Consulta com status " + consulta.getStatus() + " não pode ser " + acao);
        }
    }

    private ConflictException horarioOcupado() {
        return new ConflictException("Médico já possui consulta agendada nesse horário");
    }

    private Consulta buscarEntidade(Long id) {
        return repository.findById(id).orElseThrow(() -> ResourceNotFoundException.of("Consulta", id));
    }
}
