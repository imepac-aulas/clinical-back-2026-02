package br.edu.imepac.clinicalapi.agendamento.client;

import br.edu.imepac.clinicalapi.agendamento.client.dto.MedicoResumo;
import br.edu.imepac.clinicalapi.agendamento.client.dto.PacienteResumo;
import br.edu.imepac.clinicalapi.common.exception.ExternalServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.Optional;

/**
 * Comunicação síncrona (REST) com o ms-administrativo.
 * 404 vira {@code Optional.empty()}; qualquer outra falha vira {@link ExternalServiceException} (HTTP 503).
 */
@Slf4j
@Component
public class AdministrativoClient {

    private final RestClient restClient;

    public AdministrativoClient(RestClient.Builder builder,
                                @Value("${clients.administrativo.url}") String baseUrl) {
        this.restClient = builder.baseUrl(baseUrl).build();
    }

    public Optional<MedicoResumo> buscarMedico(Long id) {
        return buscar("/medicos/{id}", id, MedicoResumo.class);
    }

    public Optional<PacienteResumo> buscarPaciente(Long id) {
        return buscar("/pacientes/{id}", id, PacienteResumo.class);
    }

    private <T> Optional<T> buscar(String uri, Long id, Class<T> tipo) {
        log.debug("GET ms-administrativo {} (id={})", uri, id);
        try {
            return Optional.ofNullable(restClient.get().uri(uri, id).retrieve().body(tipo));
        } catch (HttpClientErrorException.NotFound e) {
            return Optional.empty();
        } catch (RestClientException e) {
            throw new ExternalServiceException("ms-administrativo indisponível ou com erro", e);
        }
    }
}
