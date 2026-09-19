package org.flightservice.config;

import io.grpc.ServerInterceptor;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.instrumentation.grpc.v1_6.GrpcTelemetry;
import net.devh.boot.grpc.server.interceptor.GrpcGlobalServerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GrpcTracingConfig {

    @Bean
    @GrpcGlobalServerInterceptor
    ServerInterceptor grpcServerTracingInterceptor(OpenTelemetry otel) {
        return GrpcTelemetry.create(otel).newServerInterceptor();
    }
}
