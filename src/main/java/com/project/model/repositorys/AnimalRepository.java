package com.project.model.repositorys;

import java.util.List;
import java.util.Optional;


import com.project.model.entitys.Animal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AnimalRepository extends JpaRepository<Animal, String> {

    Optional<Animal> findById(String id);
    
    Animal findByRg(int rg);
    
    boolean existsByRg(int rg);

    @Query("SELECT a FROM Animal a WHERE a.responsible.cpf = :cpf")
    List<Animal> findAnimalsByUserCpf(@Param("cpf") String cpf);

    List<Animal> findByResponsibleId(String id);
}
