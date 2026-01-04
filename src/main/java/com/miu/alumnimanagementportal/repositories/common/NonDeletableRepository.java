package com.miu.alumnimanagementportal.repositories.common;

import com.miu.alumnimanagementportal.entities.common.NonDeletable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@NoRepositoryBean
public interface NonDeletableRepository<T,  ID extends Serializable> extends JpaRepository<T , ID> {

    Optional<T> findOneById(ID id);

    default List<T> findAllDeletedFalse(){

        return findAll()
                .stream()
                .filter(t -> {
                    if (t instanceof NonDeletable) {
                        return ((NonDeletable) t).isNotDeleted();
                    } else {
                        return true;
                    }
                })
                .collect(Collectors.toList());
    }

    @Transactional
    void delete(ID id);

    void delete(Iterable<? extends T> entities);
}
