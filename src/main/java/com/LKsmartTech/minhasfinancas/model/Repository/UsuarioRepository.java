package com.LKsmartTech.minhasfinancas.model.Repository;

import com.LKsmartTech.minhasfinancas.model.entity.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {


    boolean existsByEmail(String email);

    Optional<Usuario> findByEmail(String email);

}
