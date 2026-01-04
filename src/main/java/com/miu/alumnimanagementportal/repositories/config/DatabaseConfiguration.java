package com.miu.alumnimanagementportal.repositories.config;

import org.springframework.context.ApplicationContextException;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;


@Configuration
public class DatabaseConfiguration implements EnvironmentAware {
    private Environment env;

    @Override
    public void setEnvironment(Environment environment) {
        this.env = environment;
    }

    @Bean
    public PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
        return new PersistenceExceptionTranslationPostProcessor();
    }
}