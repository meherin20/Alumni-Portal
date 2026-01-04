package com.miu.alumnimanagementportal.repositories.config;

import com.miu.alumnimanagementportal.repositories.common.NonDeletableRepository;
import com.miu.alumnimanagementportal.repositories.common.NonDeletableRepositoryImpl;
import jakarta.persistence.EntityManager;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactoryBean;
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;
import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;

import java.io.Serializable;

public class AlumniManagementRepositoryFactoryBean<R extends JpaRepository<T, ID>, T, ID extends Serializable>
        extends JpaRepositoryFactoryBean<R, T, ID> {

    public AlumniManagementRepositoryFactoryBean(Class<? extends R> repositoryInterface) {
        super(repositoryInterface);
    }

    protected RepositoryFactorySupport createRepositoryFactory(EntityManager entityManager) {

        return new AlumniManagementRepositoryFactory(entityManager);
    }

    private static class AlumniManagementRepositoryFactory<T, ID extends Serializable> extends JpaRepositoryFactory {
        public AlumniManagementRepositoryFactory(EntityManager entityManager) {
            super(entityManager);
        }

        @Override
        protected JpaRepositoryImplementation<?, ?> getTargetRepository(RepositoryInformation metadata, EntityManager entityManager) {
            final JpaEntityInformation<?, Serializable> entityInformation = getEntityInformation(metadata.getDomainType());

            return new NonDeletableRepositoryImpl<>(entityInformation, entityManager);
        }

        @Override
        protected Class<?> getRepositoryBaseClass(RepositoryMetadata metadata) {

            // The RepositoryMetadata can be safely ignored, it is used by the JpaRepositoryFactory
            //to check for QueryDslJpaRepository's which is out of scope.
            return NonDeletableRepository.class;
        }
    }
}