package org.example.modelo;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Paciente {
    private Integer id;
    private String nombre;
    private String apellidos;
    private LocalDate fechaNacimiento;
    private String dniId;
    private String telefono;
    private String email;
    private String direccion;
    private boolean activo;
    private LocalDateTime fechaRegistro;
    private LocalDateTime fechaModificacion;

    // Constructor vacío
    public Paciente() {
        this.activo = true; // Valor por defecto
    }

    // Constructor con todos los campos (excepto los autogenerados/default)
    public Paciente(String nombre, String apellidos, LocalDate fechaNacimiento, String dniId, String telefono, String email, String direccion) {
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.fechaNacimiento = fechaNacimiento;
        this.dniId = dniId;
        this.telefono = telefono;
        this.email = email;
        this.direccion = direccion;
        this.activo = true;
    }

    // Getters y Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public String getDniId() {
        return dniId;
    }

    public void setDniId(String dniId) {
        this.dniId = dniId;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public LocalDateTime getFechaModificacion() {
        return fechaModificacion;
    }

    public void setFechaModificacion(LocalDateTime fechaModificacion) {
        this.fechaModificacion = fechaModificacion;
    }

    @Override
    public String toString() {
        return "Paciente{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", apellidos='" + apellidos + '\'' +
                ", dniId='" + dniId + '\'' +
                '}';
    }
}
