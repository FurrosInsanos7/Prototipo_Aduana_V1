package com.example.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.model.MascotaForm;
import com.example.demo.model.Usuario;
import com.example.demo.repository.MascotaFormRepository;

@Service
public class MascotaFormService {

    @Autowired
    private MascotaFormRepository mascotaFormRepository;

    public MascotaForm guardarMascotaForm(MascotaForm mascota) {
        return mascotaFormRepository.save(mascota);
    }

    public List<MascotaForm> obtenerMascotaForms() {
        return mascotaFormRepository.findAll();
    }

    public List<MascotaForm> obtenerMascotaFormsPorUsuario(Usuario usuario) {
        return mascotaFormRepository.findByUsuario(usuario);
    }

    public MascotaForm obtenerMascotaFormPorId(int id) {
        return mascotaFormRepository.findById(id).orElse(null);
    }

    public void eliminarMascotaForm(int id) {
        mascotaFormRepository.deleteById(id);
    }
}
