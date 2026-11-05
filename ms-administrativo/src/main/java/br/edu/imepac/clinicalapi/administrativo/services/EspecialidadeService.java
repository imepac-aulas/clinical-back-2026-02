package br.edu.imepac.clinicalapi.administrativo.services;

import br.edu.imepac.clinicalapi.administrativo.dto.EspecialidadeRequest;
import br.edu.imepac.clinicalapi.administrativo.dto.EspecialidadeResponse;
import br.edu.imepac.clinicalapi.administrativo.entities.Especialidade;
import br.edu.imepac.clinicalapi.administrativo.mapper.EspecialidadeMapper;
import br.edu.imepac.clinicalapi.administrativo.repositories.EspecialidadeRepository;
import br.edu.imepac.clinicalapi.administrativo.repositories.MedicoRepository;
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
public class EspecialidadeService {

    private final EspecialidadeRepository repository;
    private final MedicoRepository medicoRepository;
    private final EspecialidadeMapper mapper;

    @Transactional
    public EspecialidadeResponse criar(EspecialidadeRequest request) {
        log.info("Criando especialidade '{}'", request.getNome());
        if (repository.existsByNomeIgnoreCase(request.getNome())) {
            throw new ConflictException("Já existe especialidade com o nome: " + request.getNome());
        }
        Especialidade salva = repository.save(mapper.toEntity(request));
        return mapper.toResponse(salva);
    }

    @Transactional(readOnly = true)
    public Page<EspecialidadeResponse> listar(Pageable pageable) {
        return repository.findAll(pageable).map(mapper::toResponse);
    }

    @Transactional(readOnly = true)
    public EspecialidadeResponse buscar(Long id) {
        return mapper.toResponse(buscarEntidade(id));
    }

    @Transactional
    public EspecialidadeResponse atualizar(Long id, EspecialidadeRequest request) {
        log.info("Atualizando especialidade {}", id);
        Especialidade entidade = buscarEntidade(id);
        if (repository.existsByNomeIgnoreCaseAndIdNot(request.getNome(), id)) {
            throw new ConflictException("Já existe especialidade com o nome: " + request.getNome());
        }
        mapper.updateEntity(request, entidade);
        return mapper.toResponse(repository.save(entidade));
    }

    @Transactional
    public void remover(Long id) {
        log.info("Removendo especialidade {}", id);
        Especialidade entidade = buscarEntidade(id);
        if (medicoRepository.existsByEspecialidadeId(id)) {
            throw new ConflictException("Especialidade possui médicos vinculados e não pode ser removida");
        }
        repository.delete(entidade);
    }

    /** Usado também pelo MedicoService. */
    @Transactional(readOnly = true)
    public Especialidade buscarEntidade(Long id) {
        return repository.findById(id).orElseThrow(() -> ResourceNotFoundException.of("Especialidade", id));
    }
}
