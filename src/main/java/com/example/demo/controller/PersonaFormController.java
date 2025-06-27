package com.example.demo.controller;

import com.example.demo.model.PersonaForm;
import com.example.demo.model.Usuario;
import com.example.demo.service.PersonaFormService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.validation.BindingResult;
import jakarta.validation.Valid;
import jakarta.servlet.http.HttpSession;

import java.util.List;

@Controller
public class PersonaFormController {

    @Autowired
    private PersonaFormService personaFormService;


    @GetMapping("/personas")
    public String mostrarPersonaForm(Model model, HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) {
            return "redirect:/login";
        }
        PersonaForm form = new PersonaForm();
        form.setTipo("persona");
        model.addAttribute("personaForm", form);
        return "persona_form";
    }

    @PostMapping("/personas")
    public String guardarPersonaForm(@Valid @ModelAttribute PersonaForm personaForm, BindingResult result,
                                    HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) {
            return "redirect:/login";
        }
        if (result.hasErrors()) {
            model.addAttribute("personaForm", personaForm);
            return "persona_form";
        }
        personaForm.setUsuario(usuario);
        personaFormService.guardarPersonaForm(personaForm);
        return "redirect:/personas/lista";
    }

    @GetMapping("/personas/lista")
    public String listarPersonaForms(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) {
            return "redirect:/login";
        }
        List<PersonaForm> lista;
        if ("admin".equalsIgnoreCase(usuario.getRol())) {
            lista = personaFormService.obtenerPersonaForms();
        } else {
            lista = personaFormService.obtenerPersonaFormsPorUsuario(usuario);
        }
        model.addAttribute("personaForms", lista);
        return "persona_informe";
    }

    @GetMapping("/personas/lista/{id}")
    public String mostrarInforme(@PathVariable int id, HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) {
            return "redirect:/login";
        }
        model.addAttribute("personaForm", personaFormService.obtenerPersonaFormPorId(id));
        List<PersonaForm> lista;
        if ("admin".equalsIgnoreCase(usuario.getRol())) {
            lista = personaFormService.obtenerPersonaForms();
        } else {
            lista = personaFormService.obtenerPersonaFormsPorUsuario(usuario);
        }
        model.addAttribute("personaForms", lista);
        return "persona_informe";
    }

    @GetMapping("/personas/eliminar/{id}")
    public String eliminarPersonaForm(@PathVariable int id, HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) {
            return "redirect:/login";
        }
        PersonaForm form = personaFormService.obtenerPersonaFormPorId(id);
        if (form != null && ("admin".equalsIgnoreCase(usuario.getRol()) ||
                form.getUsuario().getId() == usuario.getId())) {
            personaFormService.eliminarPersonaForm(id);
        }
        return "redirect:/personas/lista";
    }

    @GetMapping("/personas/export")
    public void exportarCsv(HttpSession session, jakarta.servlet.http.HttpServletResponse response) throws java.io.IOException {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) {
            response.sendRedirect("/login");
            return;
        }
        java.util.List<PersonaForm> lista = "admin".equalsIgnoreCase(usuario.getRol()) ?
                personaFormService.obtenerPersonaForms() :
                personaFormService.obtenerPersonaFormsPorUsuario(usuario);
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=personaForms.csv");
        java.io.PrintWriter writer = response.getWriter();
        writer.println("Usuario,Tipo,Nombre,TipoDocumento,NumeroDocumento,PaisNacimiento");
        for (PersonaForm f : lista) {
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
    @RequestMapping("/api/personaForms")
    @ResponseBody
    public List<PersonaForm> obtenerPersonaForms() {
        return personaFormService.obtenerPersonaForms();
    }
}
