package br.edu.imepac.clinicalapi.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/** Registra método, caminho, status e duração de cada requisição que passa pelo gateway. */
@Slf4j
@Component
public class RequestLoggingFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        long inicio = System.currentTimeMillis();
        String metodo = exchange.getRequest().getMethod().name();
        String caminho = exchange.getRequest().getURI().getPath();
        return chain.filter(exchange).doFinally(signal -> log.info("{} {} -> {} ({} ms)", metodo, caminho,
                exchange.getResponse().getStatusCode(), System.currentTimeMillis() - inicio));
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
