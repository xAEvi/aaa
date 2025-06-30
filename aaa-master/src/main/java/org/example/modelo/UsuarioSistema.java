package org.example.modelo;

import java.time.LocalDateTime;

public class UsuarioSistema {
    private Integer id;
    private String nombreUsuario;
    private String contrasenaHash;
    private String rol; // 'Administrador', 'Recepcionista', 'Medico'
    private String nombreCompleto;
    private boolean activo;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaModificacion;

    // Constructor vacío
    public UsuarioSistema() {
        this.activo = true; // Valor por defecto
    }

    // Constructor con campos principales
    public UsuarioSistema(String nombreUsuario, String contrasenaHash, String rol, String nombreCompleto) {
        this.nombreUsuario = nombreUsuario;
        this.contrasenaHash = contrasenaHash; // IMPORTANTE: Esto debe ser un hash, no la contraseña en texto plano
        this.rol = rol;
        this.nombreCompleto = nombreCompleto;
        this.activo = true;
    }

    // Getters y Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public String getContrasenaHash() {
        return contrasenaHash;
    }

    public void setContrasenaHash(String contrasenaHash) {
        this.contrasenaHash = contrasenaHash;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public LocalDateTime getFechaModificacion() {
        return fechaModificacion;
    }

    public void setFechaModificacion(LocalDateTime fechaModificacion) {
        this.fechaModificacion = fechaModificacion;
    }

    @Override
    public String toString() {
        return "UsuarioSistema{" +
                "id=" + id +
                ", nombreUsuario='" + nombreUsuario + '\'' +
                ", rol='" + rol + '\'' +
                '}';
    }
}