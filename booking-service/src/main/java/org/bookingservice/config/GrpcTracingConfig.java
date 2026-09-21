package org.bookingservice.config;

import io.grpc.ClientInterceptor;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.instrumentation.grpc.v1_6.GrpcTelemetry;
import net.devh.boot.grpc.client.interceptor.GrpcGlobalClientInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GrpcTracingConfig {

    @Bean
    @GrpcGlobalClientInterceptor
    ClientInterceptor grpcClientTracingInterceptor(OpenTelemetry otel) {
        return GrpcTelemetry.create(otel).newClientInterceptor();
    }
}
