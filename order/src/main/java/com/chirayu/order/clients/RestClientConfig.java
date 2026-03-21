package com.chirayu.order.clients;

import io.micrometer.observation.ObservationRegistry;
import io.micrometer.tracing.Tracer;
import io.micrometer.tracing.propagation.Propagator;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestClient;

@Configuration
@RequiredArgsConstructor
public class RestClientConfig {


    private final ObservationRegistry observationRegistry;
    private final Tracer tracer;
    private final Propagator propagator;

    @Bean
    @LoadBalanced
    public RestClient.Builder restClientBuilder() {
        RestClient.Builder builder = RestClient.builder();
        if(observationRegistry !=null){
            builder.requestInterceptor(createTracingInterceptor());
        }
        return builder;
    }

    private ClientHttpRequestInterceptor createTracingInterceptor() {
        return ((request, body, execution) -> {
            if(tracer !=null && propagator != null && tracer.currentSpan() != null){
                propagator.inject(tracer.currentTraceContext().context(),
                        request.getHeaders(),
                        (carrier,key,value)
                                -> carrier.add(key,value));
            }
            return execution.execute(request,body);
        }
        );
    }
}
