package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.PersonaForm;
import com.example.demo.model.Usuario;

import java.util.List;

public interface PersonaFormRepository extends JpaRepository<PersonaForm, Integer> {
    List<PersonaForm> findByUsuario(Usuario usuario);
}

