package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.Formulario;
import com.example.demo.model.Usuario;

import java.util.List;

public interface FormularioRepository extends JpaRepository<Formulario, Integer> {
    List<Formulario> findByUsuario(Usuario usuario);
}

