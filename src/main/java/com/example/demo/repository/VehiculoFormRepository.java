package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.Usuario;
import com.example.demo.model.VehiculoForm;

public interface VehiculoFormRepository extends JpaRepository<VehiculoForm, Integer> {
    List<VehiculoForm> findByUsuario(Usuario usuario);
}
