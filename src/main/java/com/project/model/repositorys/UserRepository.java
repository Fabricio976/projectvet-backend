package com.project.model.repositorys;

import com.project.model.entitys.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<Usuario, String> {

    UserDetails findByEmail(String email);

    UserDetails findByEmailAndCodeRecoveryPassword(String email, String codeRecoveryPassword);

    Usuario findByCpf(String cpf);
}
