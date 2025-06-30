/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.example.acceso_datos;

import org.example.modelo.UsuarioSistema;
import org.example.acceso_datos.util.DatabaseConnector;
import org.example.util.HashUtil;

import java.sql.*;
import java.time.LocalDateTime;


/**
 *
 * @author Mateo
 */
public class UsuarioDAO {
    
     public boolean existeUsuario(String nombreUsuario) {
        String sql = "SELECT COUNT(*) FROM usuarios_sistema WHERE nombre_usuario = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nombreUsuario);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean registrarUsuario(UsuarioSistema usuario) {
        if (existeUsuario(usuario.getNombreUsuario())) {
            System.out.println("El usuario ya existe.");
            return false;
        }

       String sql = "INSERT INTO usuarios_sistema (...) VALUES (...)";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, usuario.getNombreUsuario());
            ps.setString(2, usuario.getContrasenaHash());
            ps.setString(3, usuario.getRol());
            ps.setString(4, usuario.getNombreCompleto());
            ps.setBoolean(5, usuario.isActivo());
            ps.setTimestamp(6, Timestamp.valueOf(LocalDateTime.now()));
            ps.setTimestamp(7, Timestamp.valueOf(LocalDateTime.now()));
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public boolean modificarUsuario(UsuarioSistema usuario) {
    String sql = "UPDATE usuarios_sistema SET nombre_completo = ?, rol = ?, fecha_modificacion = ? WHERE nombre_usuario = ?";


    try (Connection conn = DatabaseConnector.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setString(1, usuario.getNombreCompleto());
        ps.setString(2, usuario.getRol());
        ps.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
        ps.setString(4, usuario.getNombreUsuario());

        return ps.executeUpdate() == 1;

    } catch (SQLException e) {
        System.err.println("Error al modificar usuario: " + e.getMessage());
        e.printStackTrace();
    }
    return false;
}

    
    public boolean cambiarContrasena(String nombreUsuario, String contrasenaActual, String nuevaContrasena) {
    String sqlCheck = "SELECT contrasena_hash FROM usuarios_sistema WHERE nombre_usuario = ?";
    String sqlUpdate = "UPDATE usuarios_sistema SET contrasena_hash = ?, fecha_modificacion = ? WHERE nombre_usuario = ?";

    try (Connection conn = DatabaseConnector.getConnection();
         PreparedStatement psCheck = conn.prepareStatement(sqlCheck)) {

        psCheck.setString(1, nombreUsuario);
        ResultSet rs = psCheck.executeQuery();

        if (rs.next()) {
            String hashActual = rs.getString("contrasena_hash");

            if (!hashActual.equals(HashUtil.sha256(contrasenaActual))) {
                System.out.println("Contraseña actual incorrecta.");
                return false;
            }

            try (PreparedStatement psUpdate = conn.prepareStatement(sqlUpdate)) {
                psUpdate.setString(1, HashUtil.sha256(nuevaContrasena));
                psUpdate.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
                psUpdate.setString(3, nombreUsuario);
                return psUpdate.executeUpdate() == 1;
            }
        }
    } catch (SQLException e) {
        System.err.println("Error al cambiar contraseña: " + e.getMessage());
        e.printStackTrace();
    }
    return false;
}

    public boolean inhabilitarUsuario(String nombreUsuario) {
    String sql = "UPDATE usuarios_sistema SET activo = false, fecha_modificacion = ? WHERE nombre_usuario = ?";

    try (Connection conn = DatabaseConnector.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
        ps.setString(2, nombreUsuario);

        return ps.executeUpdate() == 1;

    } catch (SQLException e) {
        System.err.println("Error al inhabilitar usuario: " + e.getMessage());
        e.printStackTrace();
    }
    return false;
}
    
}
