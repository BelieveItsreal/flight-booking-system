package org.apigateway.filter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class RequestLoggingFilter implements GlobalFilter, Ordered {
    private static final Logger log = LoggerFactory.getLogger(RequestLoggingFilter.class);
    private static final String REQUEST_ID_HEADER = "X-Request-Id";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String requestId = request.getHeaders().getFirst(REQUEST_ID_HEADER);
        if (requestId == null || requestId.isBlank()){
            requestId = UUID.randomUUID().toString();
        }
        String finalRequestId = requestId;
        ServerHttpRequest mutatedRequest = request.mutate()
                .header(REQUEST_ID_HEADER, finalRequestId)
                .build();
        ServerWebExchange mutatedExchange = exchange.mutate().request(mutatedRequest).build();
        long startTime = System.currentTimeMillis();
        log.info("[{}] --> {} {} headers={}", finalRequestId, request.getMethod(),
                request.getURI(), safeHeaders(request.getHeaders()));
        return chain.filter(mutatedExchange)
                .doFinally(signalType -> {
                    long duration = System.currentTimeMillis() - startTime;
                    log.info("[{}] <-- {} {} status={} duration={}ms", finalRequestId,
                            request.getMethod(), request.getURI(),
                            mutatedExchange.getResponse().getStatusCode(), duration);
                });
    }

    private  String safeHeaders(HttpHeaders headers){
        return headers.entrySet().stream()
                .map(this::redactIfSensitive)
                .collect(Collectors.joining(", "));
    }

    private String redactIfSensitive(Map.Entry<String, java.util.List<String>> entry){
        if (entry.getKey().equalsIgnoreCase("Authorization")){
            return entry.getKey() + "=[REDACTED]";
        }
        return entry.getKey() + "=" + entry.getValue();
    }

    @Override
    public int getOrder() {
        return -2;
    }
}
