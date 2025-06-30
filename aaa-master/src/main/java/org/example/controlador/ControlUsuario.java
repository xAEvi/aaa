/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.example.controlador;

import org.example.modelo.UsuarioSistema;
import org.example.acceso_datos.UsuarioDAO;
import org.example.util.HashUtil;


/**
 *
 * @author Mateo
 */
public class ControlUsuario {
    
  private UsuarioDAO dao = new UsuarioDAO();

    public String registrar(String nombreUsuario, String contrasena, String nombreCompleto, String rol) {
        if (dao.existeUsuario(nombreUsuario)) {
            return "El nombre de usuario ya existe.";
        }
        if (!rolValido(rol)) {
            return "Rol inválido.";
        }
        String hash = HashUtil.sha256(contrasena);
        UsuarioSistema usuario = new UsuarioSistema(nombreUsuario, hash, rol, nombreCompleto);
        boolean exito = dao.registrarUsuario(usuario);
        return exito ? "Usuario registrado exitosamente." : "Error al registrar usuario.";
    }

    public String modificarUsuario(UsuarioSistema usuario, String nuevoNombre, String nuevoRol, String rolActual) {
        if (!"Administrador".equals(rolActual)) return "Permiso denegado.";

        usuario.setNombreCompleto(nuevoNombre);
        usuario.setRol(nuevoRol);
        boolean exito = dao.modificarUsuario(usuario);
        return exito ? "Usuario actualizado correctamente." : "Error al actualizar usuario.";
    }

    public String cambiarContrasena(UsuarioSistema usuario, String actual, String nueva) {
        if (!usuario.getContrasenaHash().equals(HashUtil.sha256(actual))) {
            return "Contraseña actual incorrecta.";
        }
        usuario.setContrasenaHash(HashUtil.sha256(nueva));
        boolean exito = dao.modificarUsuario(usuario);
        return exito ? "Contraseña actualizada correctamente." : "Error al cambiar la contraseña.";
    }

    public String inhabilitarUsuario(UsuarioSistema usuario, String rolActual) {
        if (!"Administrador".equals(rolActual)) return "Permiso denegado.";
        boolean exito = dao.inhabilitarUsuario(usuario.getNombreUsuario());
        return exito ? "Usuario inhabilitado correctamente." : "Error al inhabilitar usuario.";
    }

    private boolean rolValido(String rol) {
        return "Administrador".equals(rol) || "Recepcionista".equals(rol) || "Medico".equals(rol);
    }
    
}
