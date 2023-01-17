package com.microservices.deomo.common.config;

import com.microservices.demo.config.RetryConfigData;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;


@Configuration
@RequiredArgsConstructor
public class RetryConfig {

    private final RetryConfigData configData;

    @Bean
    public RetryTemplate retryTemplate() {
        RetryTemplate retryTemplate = new RetryTemplate();
        ExponentialBackOffPolicy exponentialBackOffPolicy = new ExponentialBackOffPolicy();
        exponentialBackOffPolicy.setInitialInterval(configData.getInitialIntervalMs());
        exponentialBackOffPolicy.setMaxInterval(configData.getMaxIntervalMs());
        exponentialBackOffPolicy.setMultiplier(configData.getMultiplier());
        retryTemplate.setBackOffPolicy(exponentialBackOffPolicy);

        SimpleRetryPolicy simpleRetryPolicy = new SimpleRetryPolicy();
        simpleRetryPolicy.setMaxAttempts(configData.getMaxAttempts());
        retryTemplate.setRetryPolicy(simpleRetryPolicy);

        return retryTemplate;
    }

}
