package com.example.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.model.PersonaForm;
import com.example.demo.model.Usuario;
import com.example.demo.repository.PersonaFormRepository;

@Service
public class PersonaFormService {

    @Autowired
    private PersonaFormRepository personaFormRepository;

    public PersonaForm guardarPersonaForm(PersonaForm personaForm) {
        return personaFormRepository.save(personaForm);
    }

    public List<PersonaForm> obtenerPersonaForms() {
        return personaFormRepository.findAll();
    }

    public List<PersonaForm> obtenerPersonaFormsPorUsuario(Usuario usuario) {
        return personaFormRepository.findByUsuario(usuario);
    }

    public PersonaForm obtenerPersonaFormPorId(int id) {
        return personaFormRepository.findById(id).orElse(null);
    }

    public void eliminarPersonaForm(int id) {
        personaFormRepository.deleteById(id);
    }
}
