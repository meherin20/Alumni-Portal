package com.miu.alumnimanagementportal.repositories.common;

import com.miu.alumnimanagementportal.entities.common.NonDeletable;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import jakarta.persistence.EntityManager;
import java.io.Serializable;
import java.util.List;
import java.util.Optional;

@SuppressWarnings("unchecked")
public class NonDeletableRepositoryImpl<T, ID extends Serializable> extends SimpleJpaRepository<T, ID> implements NonDeletableRepository<T, ID> {

    public NonDeletableRepositoryImpl(JpaEntityInformation<T , ?> entityInformation, EntityManager entityManager) {
        super(entityInformation, entityManager);
    }


    @Override
    public Optional<T> findOneById(ID id) {
        return super.findById(id);
    }

    @Override
    public List<T> findAllDeletedFalse() {
        return NonDeletableRepository.super.findAllDeletedFalse();
    }

    @Override
    @Transactional
    public void delete(ID id) {
        findOneById(id).ifPresent(t -> {
            if (t instanceof NonDeletable) {
                NonDeletable nonDeletable = (NonDeletable) t;
                nonDeletable.setDeleted(true);
                updateNonDeletable(nonDeletable);
            } else {
                super.delete(t);
            }
        });
    }

    @Override
    public void delete(T entity) {
        if (entity instanceof NonDeletable) {
            NonDeletable nonDeletable = (NonDeletable) entity;
            nonDeletable.setDeleted(true);
            updateNonDeletable(nonDeletable);
        } else {
            super.delete(entity);
        }
    }

    private void updateNonDeletable(NonDeletable nonDeletable) {
        T entity = (T) nonDeletable;
        super.save(entity);
    }

    @Override
    public void delete(Iterable<? extends T> entities) {
        for (T entity: entities) {
            delete(entity);
        }
    }
}
