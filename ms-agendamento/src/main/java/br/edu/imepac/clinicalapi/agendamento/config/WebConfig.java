package br.edu.imepac.clinicalapi.agendamento.config;

import br.edu.imepac.clinicalapi.common.exception.GlobalExceptionHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.data.web.config.EnableSpringDataWebSupport.PageSerializationMode;

/**
 * Registra o tratamento central de erros do módulo common e define a serialização de páginas
 * (Page) como {"content":[...],"page":{...}}, igual ao contrato OpenAPI.
 */
@Configuration
@Import(GlobalExceptionHandler.class)
@EnableSpringDataWebSupport(pageSerializationMode = PageSerializationMode.VIA_DTO)
public class WebConfig {
}
