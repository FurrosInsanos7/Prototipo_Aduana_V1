package com.example.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.model.Formulario;
import com.example.demo.model.Usuario;
import com.example.demo.repository.FormularioRepository;

@Service
public class FormularioService {

    @Autowired
    private FormularioRepository formularioRepository;

    public Formulario guardarFormulario(Formulario formulario) {
        return formularioRepository.save(formulario);
    }

    public List<Formulario> obtenerFormularios() {
        return formularioRepository.findAll();
    }

    public List<Formulario> obtenerFormulariosPorUsuario(Usuario usuario) {
        return formularioRepository.findByUsuario(usuario);
    }

    public Formulario obtenerFormularioPorId(int id) {
        return formularioRepository.findById(id).orElse(null);
    }

    public void eliminarFormulario(int id) {
        formularioRepository.deleteById(id);
    }
}
