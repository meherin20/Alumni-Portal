package com.miu.alumnimanagementportal;

import com.miu.alumnimanagementportal.repositories.config.AlumniManagementRepositoryFactoryBean;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories(value = "com.miu.alumnimanagementportal.repositories", repositoryFactoryBeanClass = AlumniManagementRepositoryFactoryBean.class)
@SpringBootApplication
public class AlumniManagementPortalApplication {

    public static void main(String[] args) {
        SpringApplication.run(AlumniManagementPortalApplication.class, args);
    }

}
