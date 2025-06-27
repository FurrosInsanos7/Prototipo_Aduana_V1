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

import com.example.demo.model.MascotaForm;
import com.example.demo.model.Usuario;
import com.example.demo.service.MascotaFormService;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Controller
public class MascotaFormController {

    @Autowired
    private MascotaFormService mascotaFormService;

    @GetMapping("/mascotas")
    public String mostrarFormulario(Model model, HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) {
            return "redirect:/login";
        }
        model.addAttribute("mascotaForm", new MascotaForm());
        return "mascota_form";
    }

    @PostMapping("/mascotas")
    public String guardarMascota(@ModelAttribute MascotaForm mascota, HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) {
            return "redirect:/login";
        }
        mascota.setUsuario(usuario);
        mascotaFormService.guardarMascotaForm(mascota);
        return "redirect:/mascotas/lista";
    }

    @GetMapping("/mascotas/lista")
    public String listarMascotas(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) {
            return "redirect:/login";
        }
        List<MascotaForm> lista = "admin".equalsIgnoreCase(usuario.getRol()) ?
                mascotaFormService.obtenerMascotaForms() :
                mascotaFormService.obtenerMascotaFormsPorUsuario(usuario);
        model.addAttribute("mascotas", lista);
        return "mascota_informe";
    }

    @GetMapping("/mascotas/eliminar/{id}")
    public String eliminarMascota(@PathVariable int id, HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) {
            return "redirect:/login";
        }
        MascotaForm mascota = mascotaFormService.obtenerMascotaFormPorId(id);
        if (mascota != null && ("admin".equalsIgnoreCase(usuario.getRol()) || mascota.getUsuario().getId() == usuario.getId())) {
            mascotaFormService.eliminarMascotaForm(id);
        }
        return "redirect:/mascotas/lista";
    }

    @GetMapping("/mascotas/export")
    public void exportarCsv(HttpSession session, HttpServletResponse response) throws java.io.IOException {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) {
            response.sendRedirect("/login");
            return;
        }
        List<MascotaForm> lista = "admin".equalsIgnoreCase(usuario.getRol()) ?
                mascotaFormService.obtenerMascotaForms() :
                mascotaFormService.obtenerMascotaFormsPorUsuario(usuario);
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=mascotas.csv");
        java.io.PrintWriter writer = response.getWriter();
        writer.println("Usuario,Nombre,Especie,Tipo,PaisOrigen");
        for (MascotaForm m : lista) {
            writer.printf("%s,%s,%s,%s,%s%n",
                    m.getUsuario().getNombre(),
                    m.getNombre(),
                    m.getEspecie(),
                    m.getTipo(),
                    m.getPaisOrigen());
        }
        writer.flush();
    }

    @RequestMapping("/api/mascotas")
    @ResponseBody
    public List<MascotaForm> obtenerMascotas() {
        return mascotaFormService.obtenerMascotaForms();
    }
}
