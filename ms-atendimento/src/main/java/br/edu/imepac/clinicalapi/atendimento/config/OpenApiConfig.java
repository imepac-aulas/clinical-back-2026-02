package br.edu.imepac.clinicalapi.atendimento.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "MS Atendimento",
                version = "1.0",
                description = """
            Documentação do Microsserviço de Atendimento.

            ## Diagrama do Microsserviço

            ![Diagrama do MS Atendimento](http://localhost:8080/diagrama.jpeg)
            """
        )
)
public class OpenApiConfig {
}