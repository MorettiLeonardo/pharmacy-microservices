package com.pharmacy.gateway.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class GatewayRequestLoggingFilter implements GlobalFilter, Ordered {

    private static final Logger log = LoggerFactory.getLogger(GatewayRequestLoggingFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();
        if (path.startsWith("/actuator/health")) {
            return chain.filter(exchange);
        }

        String method = exchange.getRequest().getMethod().name();
        long startTime = System.currentTimeMillis();
        log.info("HTTP IN  -> {} {}", method, path);

        return chain.filter(exchange).doFinally(signalType -> {
            int statusCode = exchange.getResponse().getStatusCode() != null
                    ? exchange.getResponse().getStatusCode().value()
                    : 0;
            long durationMs = System.currentTimeMillis() - startTime;
            log.info("HTTP OUT <- {} {} status={} took={}ms", method, path, statusCode, durationMs);
        });
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
