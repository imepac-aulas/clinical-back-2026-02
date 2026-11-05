package br.edu.imepac.clinicalapi.administrativo.services;

import br.edu.imepac.clinicalapi.administrativo.dto.MedicoRequest;
import br.edu.imepac.clinicalapi.administrativo.dto.MedicoResponse;
import br.edu.imepac.clinicalapi.administrativo.entities.Especialidade;
import br.edu.imepac.clinicalapi.administrativo.entities.Medico;
import br.edu.imepac.clinicalapi.administrativo.mapper.MedicoMapper;
import br.edu.imepac.clinicalapi.administrativo.repositories.EspecialidadeRepository;
import br.edu.imepac.clinicalapi.administrativo.repositories.MedicoRepository;
import br.edu.imepac.clinicalapi.common.exception.BusinessRuleException;
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
public class MedicoService {

    private final MedicoRepository repository;
    private final EspecialidadeRepository especialidadeRepository;
    private final MedicoMapper mapper;

    @Transactional
    public MedicoResponse criar(MedicoRequest request) {
        log.info("Criando médico CRM {}", request.getCrm());
        if (repository.existsByCpf(request.getCpf())) {
            throw new ConflictException("Já existe médico com o CPF informado");
        }
        if (repository.existsByCrm(request.getCrm())) {
            throw new ConflictException("Já existe médico com o CRM: " + request.getCrm());
        }
        Medico medico = mapper.toEntity(request);
        medico.setEspecialidade(buscarEspecialidade(request.getEspecialidadeId()));
        return mapper.toResponse(repository.save(medico));
    }

    @Transactional(readOnly = true)
    public Page<MedicoResponse> listar(Pageable pageable) {
        return repository.findAll(pageable).map(mapper::toResponse);
    }

    @Transactional(readOnly = true)
    public MedicoResponse buscar(Long id) {
        return mapper.toResponse(buscarEntidade(id));
    }

    @Transactional
    public MedicoResponse atualizar(Long id, MedicoRequest request) {
        log.info("Atualizando médico {}", id);
        Medico medico = buscarEntidade(id);
        if (repository.existsByCpfAndIdNot(request.getCpf(), id)) {
            throw new ConflictException("Já existe médico com o CPF informado");
        }
        if (repository.existsByCrmAndIdNot(request.getCrm(), id)) {
            throw new ConflictException("Já existe médico com o CRM: " + request.getCrm());
        }
        mapper.updateEntity(request, medico);
        medico.setEspecialidade(buscarEspecialidade(request.getEspecialidadeId()));
        return mapper.toResponse(repository.save(medico));
    }

    @Transactional
    public void remover(Long id) {
        log.info("Removendo médico {}", id);
        repository.delete(buscarEntidade(id));
    }

    private Medico buscarEntidade(Long id) {
        return repository.findById(id).orElseThrow(() -> ResourceNotFoundException.of("Médico", id));
    }

    private Especialidade buscarEspecialidade(Long id) {
        return especialidadeRepository.findById(id)
                .orElseThrow(() -> new BusinessRuleException("Especialidade não encontrada: " + id));
    }
}
