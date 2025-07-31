package com.project.model.repositorys;

import java.util.List;
import java.util.Optional;


import com.project.model.entitys.Animal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AnimalRepository extends JpaRepository<Animal, String> {

    Optional<Animal> findById(String id);

    Optional<Animal> findByRg(Integer rg);
    
    boolean existsByRg(int rg);

    @Query("SELECT a FROM Animal a WHERE a.responsible.cpf = :cpf")
    List<Animal> findAnimalsByUserCpf(@Param("cpf") String cpf);

    Page<Animal> findByResponsibleId(String userId, Pageable pageable);
}
