package org.example.modelo;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Medicamento {
    private Integer id;
    private String nombre;
    private String descripcionPresentacion;
    private BigDecimal precio;
    private boolean activo;
    private LocalDateTime fechaRegistro;
    private LocalDateTime fechaModificacion;

    // Constructor vacío
    public Medicamento() {
        this.activo = true; // Valor por defecto
    }

    // Constructor con campos principales
    public Medicamento(String nombre, String descripcionPresentacion, BigDecimal precio) {
        this.nombre = nombre;
        this.descripcionPresentacion = descripcionPresentacion;
        this.precio = precio;
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

    public String getDescripcionPresentacion() {
        return descripcionPresentacion;
    }

    public void setDescripcionPresentacion(String descripcionPresentacion) {
        this.descripcionPresentacion = descripcionPresentacion;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
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
        return "Medicamento{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", precio=" + precio +
                '}';
    }
}
