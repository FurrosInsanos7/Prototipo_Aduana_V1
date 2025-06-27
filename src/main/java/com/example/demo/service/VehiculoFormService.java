package com.example.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.model.Usuario;
import com.example.demo.model.VehiculoForm;
import com.example.demo.repository.VehiculoFormRepository;

@Service
public class VehiculoFormService {

    @Autowired
    private VehiculoFormRepository vehiculoFormRepository;

    public VehiculoForm guardarVehiculoForm(VehiculoForm vehiculo) {
        return vehiculoFormRepository.save(vehiculo);
    }

    public List<VehiculoForm> obtenerVehiculoForms() {
        return vehiculoFormRepository.findAll();
    }

    public List<VehiculoForm> obtenerVehiculoFormsPorUsuario(Usuario usuario) {
        return vehiculoFormRepository.findByUsuario(usuario);
    }

    public VehiculoForm obtenerVehiculoFormPorId(int id) {
        return vehiculoFormRepository.findById(id).orElse(null);
    }

    public void eliminarVehiculoForm(int id) {
        vehiculoFormRepository.deleteById(id);
    }
}
