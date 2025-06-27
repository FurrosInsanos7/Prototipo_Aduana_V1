package com.example.demo.controller;

import com.example.demo.model.Formulario;
import com.example.demo.model.Usuario;
import com.example.demo.service.FormularioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import jakarta.servlet.http.HttpSession;

import java.util.List;

@Controller
public class FormularioController {

    @Autowired
    private FormularioService formularioService;


    @GetMapping("/formulario")
    public String mostrarFormulario(Model model, HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) {
            return "redirect:/login";
        }
        Formulario form = new Formulario();
        form.setTipo("persona");
        model.addAttribute("formulario", form);
        return "formulario";
    }

    @PostMapping("/formulario")
    public String guardarFormulario(@ModelAttribute Formulario formulario, HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) {
            return "redirect:/login";
        }
        formulario.setUsuario(usuario);
        formularioService.guardarFormulario(formulario);
        return listarFormularios(session, model);
    }

    @GetMapping("/informe")
    public String listarFormularios(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) {
            return "redirect:/login";
        }
        List<Formulario> lista;
        if ("admin".equalsIgnoreCase(usuario.getRol())) {
            lista = formularioService.obtenerFormularios();
        } else {
            lista = formularioService.obtenerFormulariosPorUsuario(usuario);
        }
        model.addAttribute("formularios", lista);
        return "informe";
    }

    @GetMapping("/informe/{id}")
    public String mostrarInforme(@PathVariable int id, HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) {
            return "redirect:/login";
        }
        model.addAttribute("formulario", formularioService.obtenerFormularioPorId(id));
        List<Formulario> lista;
        if ("admin".equalsIgnoreCase(usuario.getRol())) {
            lista = formularioService.obtenerFormularios();
        } else {
            lista = formularioService.obtenerFormulariosPorUsuario(usuario);
        }
        model.addAttribute("formularios", lista);
        return "informe";
    }

    @GetMapping("/formulario/eliminar/{id}")
    public String eliminarFormulario(@PathVariable int id, HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) {
            return "redirect:/login";
        }
        Formulario form = formularioService.obtenerFormularioPorId(id);
        if (form != null && ("admin".equalsIgnoreCase(usuario.getRol()) ||
                form.getUsuario().getId() == usuario.getId())) {
            formularioService.eliminarFormulario(id);
        }
        return "redirect:/informe";
    }

    @GetMapping("/informe/export")
    public void exportarCsv(HttpSession session, jakarta.servlet.http.HttpServletResponse response) throws java.io.IOException {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) {
            response.sendRedirect("/login");
            return;
        }
        java.util.List<Formulario> lista = "admin".equalsIgnoreCase(usuario.getRol()) ?
                formularioService.obtenerFormularios() :
                formularioService.obtenerFormulariosPorUsuario(usuario);
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=formularios.csv");
        java.io.PrintWriter writer = response.getWriter();
        writer.println("Usuario,Tipo,Nombre,TipoDocumento,NumeroDocumento,PaisNacimiento");
        for (Formulario f : lista) {
            writer.printf("%s,%s,%s,%s,%s,%s%n",
                    f.getUsuario().getNombre(),
                    f.getTipo(),
                    f.getNombre(),
                    f.getTipoDocumento(),
                    f.getNumeroDocumento(),
                    f.getPaisNacimiento());
        }
        writer.flush();
    }

    // API endpoints
    @RequestMapping("/api/formularios")
    @ResponseBody
    public List<Formulario> obtenerFormularios() {
        return formularioService.obtenerFormularios();
    }
}