package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.MascotaForm;
import com.example.demo.model.Usuario;

public interface MascotaFormRepository extends JpaRepository<MascotaForm, Integer> {
    List<MascotaForm> findByUsuario(Usuario usuario);
}
