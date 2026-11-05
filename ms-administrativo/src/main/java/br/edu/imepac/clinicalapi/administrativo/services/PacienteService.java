package br.edu.imepac.clinicalapi.administrativo.services;

import br.edu.imepac.clinicalapi.administrativo.dto.PacienteRequest;
import br.edu.imepac.clinicalapi.administrativo.dto.PacienteResponse;
import br.edu.imepac.clinicalapi.administrativo.entities.Paciente;
import br.edu.imepac.clinicalapi.administrativo.mapper.PacienteMapper;
import br.edu.imepac.clinicalapi.administrativo.repositories.PacienteRepository;
import br.edu.imepac.clinicalapi.common.exception.ConflictException;
import br.edu.imepac.clinicalapi.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PacienteService {

    private final PacienteRepository repository;
    private final PacienteMapper mapper;

    @Transactional
    public PacienteResponse criar(PacienteRequest request) {
        log.info("Criando paciente");
        if (repository.existsByCpf(request.getCpf())) {
            throw new ConflictException("Já existe paciente com o CPF informado");
        }
        return mapper.toResponse(repository.save(mapper.toEntity(request)));
    }

    @Transactional(readOnly = true)
    public Page<PacienteResponse> listar(Pageable pageable) {
        return repository.findAll(pageable).map(mapper::toResponse);
    }

    @Transactional(readOnly = true)
    public PacienteResponse buscar(Long id) {
        return mapper.toResponse(buscarEntidade(id));
    }

    @Transactional
    public PacienteResponse atualizar(Long id, PacienteRequest request) {
        log.info("Atualizando paciente {}", id);
        Paciente paciente = buscarEntidade(id);
        if (repository.existsByCpfAndIdNot(request.getCpf(), id)) {
            throw new ConflictException("Já existe paciente com o CPF informado");
        }
        mapper.updateEntity(request, paciente);
        return mapper.toResponse(repository.save(paciente));
    }

    @Transactional
    public void remover(Long id) {
        log.info("Removendo paciente {}", id);
        repository.delete(buscarEntidade(id));
    }

    private Paciente buscarEntidade(Long id) {
        return repository.findById(id).orElseThrow(() -> ResourceNotFoundException.of("Paciente", id));
    }
}
