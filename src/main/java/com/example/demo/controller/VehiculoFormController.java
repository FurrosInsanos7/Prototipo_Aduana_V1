package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.demo.model.Usuario;
import com.example.demo.model.VehiculoForm;
import com.example.demo.service.VehiculoFormService;

import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpServletResponse;

@Controller
public class VehiculoFormController {

    @Autowired
    private VehiculoFormService vehiculoFormService;

    @GetMapping("/vehiculos")
    public String mostrarFormulario(Model model, HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) {
            return "redirect:/login";
        }
        model.addAttribute("vehiculoForm", new VehiculoForm());
        return "vehiculo_form";
    }

    @PostMapping("/vehiculos")
    public String guardarVehiculo(@ModelAttribute VehiculoForm vehiculo, HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) {
            return "redirect:/login";
        }
        vehiculo.setUsuario(usuario);
        vehiculoFormService.guardarVehiculoForm(vehiculo);
        return "redirect:/vehiculos/lista";
    }

    @GetMapping("/vehiculos/lista")
    public String listarVehiculos(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) {
            return "redirect:/login";
        }
        List<VehiculoForm> lista;
        if ("admin".equalsIgnoreCase(usuario.getRol())) {
            lista = vehiculoFormService.obtenerVehiculoForms();
        } else {
            lista = vehiculoFormService.obtenerVehiculoFormsPorUsuario(usuario);
        }
        model.addAttribute("vehiculos", lista);
        return "vehiculo_informe";
    }

    @GetMapping("/vehiculos/eliminar/{id}")
    public String eliminarVehiculo(@PathVariable int id, HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) {
            return "redirect:/login";
        }
        VehiculoForm vehiculo = vehiculoFormService.obtenerVehiculoFormPorId(id);
        if (vehiculo != null && ("admin".equalsIgnoreCase(usuario.getRol()) || vehiculo.getUsuario().getId() == usuario.getId())) {
            vehiculoFormService.eliminarVehiculoForm(id);
        }
        return "redirect:/vehiculos/lista";
    }

    @GetMapping("/vehiculos/export")
    public void exportarCsv(HttpSession session, HttpServletResponse response) throws java.io.IOException {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) {
            response.sendRedirect("/login");
            return;
        }
        List<VehiculoForm> lista = "admin".equalsIgnoreCase(usuario.getRol()) ?
                vehiculoFormService.obtenerVehiculoForms() :
                vehiculoFormService.obtenerVehiculoFormsPorUsuario(usuario);
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=vehiculos.csv");
        java.io.PrintWriter writer = response.getWriter();
        writer.println("Usuario,Patente,Marca,Modelo,Tipo,PaisOrigen");
        for (VehiculoForm v : lista) {
            writer.printf("%s,%s,%s,%s,%s,%s%n",
                    v.getUsuario().getNombre(),
                    v.getPatente(),
                    v.getMarca(),
                    v.getModelo(),
                    v.getTipo(),
                    v.getPaisOrigen());
        }
        writer.flush();
    }

    @RequestMapping("/api/vehiculos")
    @ResponseBody
    public List<VehiculoForm> obtenerVehiculos() {
        return vehiculoFormService.obtenerVehiculoForms();
    }
}
