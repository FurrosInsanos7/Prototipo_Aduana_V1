package com.example.demo.controller;

import com.example.demo.model.Usuario;
import com.example.demo.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import jakarta.servlet.http.HttpSession;

import java.util.List;

@Controller
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/")
    public String raiz(HttpSession session) {
        return session.getAttribute("usuario") == null ? "redirect:/login" : "redirect:/inicio";
    }

    @GetMapping("/inicio")
    public String mostrarInicio(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) {
            return "redirect:/login";
        }
        model.addAttribute("usuario", usuario);
        return "inicio";
    }


    // Mostrar formulario de inicio de sesión
    @GetMapping("/login")
    public String mostrarLogin(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "login";
    }

    // Mostrar formulario de registro
    @GetMapping("/registro")
    public String mostrarRegistro(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "registro";
    }

    // Procesar registro de nuevo usuario
    @PostMapping("/registro")
    public String procesarRegistro(@ModelAttribute Usuario usuario) {
        if (usuario.getRol() == null || usuario.getRol().isBlank()) {
            usuario.setRol("usuario");
        }
        usuarioService.guardarUsuario(usuario);
        return "redirect:/login";
    }

    // Procesar inicio de sesión
    @PostMapping("/login")
    public String iniciarSesion(@ModelAttribute Usuario usuario, Model model, HttpSession session) {
        // Verificar si el usuario existe y la contraseña es correcta
        Usuario usuarioEncontrado = usuarioService.obtenerUsuarios().stream()
            .filter(u -> u.getCorreo().equals(usuario.getCorreo()) && u.getContraseña().equals(usuario.getContraseña()))
            .findFirst()
            .orElse(null);

        if (usuarioEncontrado != null) {
            session.setAttribute("usuario", usuarioEncontrado);
            return "redirect:/inicio";
        } else {
            // Si las credenciales son incorrectas, mostrar un mensaje de error
            model.addAttribute("error", "Credenciales incorrectas");
            return "login";  // Vuelve a mostrar el formulario de login
        }
    }

    @GetMapping("/usuarios/lista")
    public String listarUsuarios(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) {
            return "redirect:/login";
        }
        if (!"admin".equalsIgnoreCase(usuario.getRol())) {
            return "redirect:/inicio";
        }
        model.addAttribute("usuarios", usuarioService.obtenerUsuarios());
        return "usuario_lista";
    }

    @GetMapping("/usuarios/editar/{id}")
    public String mostrarEditarUsuario(@PathVariable int id, HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) {
            return "redirect:/login";
        }
        if (!"admin".equalsIgnoreCase(usuario.getRol())) {
            return "redirect:/inicio";
        }
        Usuario u = usuarioService.obtenerUsuarioPorId(id);
        if (u == null) {
            return "redirect:/usuarios/lista";
        }
        model.addAttribute("usuarioEditar", u);
        return "usuario_form";
    }

    @PostMapping("/usuarios/editar/{id}")
    public String guardarUsuarioEditado(@PathVariable int id, @ModelAttribute("usuarioEditar") Usuario usuarioForm, HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) {
            return "redirect:/login";
        }
        if (!"admin".equalsIgnoreCase(usuario.getRol())) {
            return "redirect:/inicio";
        }
        usuarioForm.setId(id);
        usuarioService.guardarUsuario(usuarioForm);
        return "redirect:/usuarios/lista";
    }

    @GetMapping("/usuarios/eliminar/{id}")
    public String eliminarUsuario(@PathVariable int id, HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) {
            return "redirect:/login";
        }
        if (!"admin".equalsIgnoreCase(usuario.getRol())) {
            return "redirect:/inicio";
        }
        usuarioService.eliminarUsuario(id);
        return "redirect:/usuarios/lista";
    }


    // Endpoint para obtener todos los usuarios en formato JSON (para Postman)
    @RequestMapping("/api/usuarios")
    @ResponseBody
    public List<Usuario> obtenerUsuarios() {
        return usuarioService.obtenerUsuarios();  // Devuelve la lista de usuarios como JSON
    }

    // Endpoint para crear un usuario mediante POST (para Postman)
    @PostMapping("/api/usuarios")
    @ResponseBody
    public Usuario crearUsuario(@RequestBody Usuario usuario) {
        return usuarioService.guardarUsuario(usuario);  // Guarda el usuario y devuelve el objeto creado
    }

    // Endpoint para login (para Postman)
    @PostMapping("/api/login")
    @ResponseBody
    public String login(@RequestBody Usuario usuario) {
        Usuario usuarioEncontrado = usuarioService.obtenerUsuarios().stream()
            .filter(u -> u.getCorreo().equals(usuario.getCorreo()) && u.getContraseña().equals(usuario.getContraseña()))
            .findFirst()
            .orElse(null);

        if (usuarioEncontrado != null) {
            return "Login exitoso";  // Retorna mensaje de éxito
        } else {
            return "Credenciales incorrectas";  // Retorna mensaje de error
        }
    }

    // API adicional para operaciones CRUD de usuario
    @GetMapping("/api/usuarios/{id}")
    @ResponseBody
    public Usuario obtenerUsuario(@PathVariable int id) {
        return usuarioService.obtenerUsuarioPorId(id);
    }

    @PutMapping("/api/usuarios/{id}")
    @ResponseBody
    public Usuario actualizarUsuario(@PathVariable int id, @RequestBody Usuario usuario) {
        usuario.setId(id);
        return usuarioService.guardarUsuario(usuario);
    }

    @DeleteMapping("/api/usuarios/{id}")
    @ResponseBody
    public void eliminarUsuarioApi(@PathVariable int id) {
        usuarioService.eliminarUsuario(id);
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}

