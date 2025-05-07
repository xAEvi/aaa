package org.example.modelo;

import java.math.BigDecimal; // Necesario para el precio

public class DiagnosticoMedicamento {
    private Integer id;
    private int idDiagnostico;
    private int idMedicamento;
    private String indicaciones;
    private int cantidad;

    // Campos adicionales para mostrar detalles del medicamento (RF-DGN-004)
    private String nombreMedicamento;
    private String descripcionPresentacion;
    private BigDecimal precioMedicamento; // Usar BigDecimal para precios

    // Para un modelo más rico, podrías tener objetos Diagnostico y Medicamento
    // private Diagnostico diagnostico;
    // private Medicamento medicamento;


    // Constructor vacío
    public DiagnosticoMedicamento() {
        this.cantidad = 1; // Valor por defecto
    }

    // Constructor con campos principales
    public DiagnosticoMedicamento(int idDiagnostico, int idMedicamento, String indicaciones, int cantidad) {
        this.idDiagnostico = idDiagnostico;
        this.idMedicamento = idMedicamento;
        this.indicaciones = indicaciones;
        this.cantidad = cantidad;
    }

    // Getters y Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getIdDiagnostico() {
        return idDiagnostico;
    }

    public void setIdDiagnostico(int idDiagnostico) {
        this.idDiagnostico = idDiagnostico;
    }

    public int getIdMedicamento() {
        return idMedicamento;
    }

    public void setIdMedicamento(int idMedicamento) {
        this.idMedicamento = idMedicamento;
    }

    public String getIndicaciones() {
        return indicaciones;
    }

    public void setIndicaciones(String indicaciones) {
        this.indicaciones = indicaciones;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    // Getters y Setters para los campos adicionales
    public String getNombreMedicamento() {
        return nombreMedicamento;
    }

    public void setNombreMedicamento(String nombreMedicamento) {
        this.nombreMedicamento = nombreMedicamento;
    }

    public String getDescripcionPresentacion() {
        return descripcionPresentacion;
    }

    public void setDescripcionPresentacion(String descripcionPresentacion) {
        this.descripcionPresentacion = descripcionPresentacion;
    }

    public BigDecimal getPrecioMedicamento() {
        return precioMedicamento;
    }

    public void setPrecioMedicamento(BigDecimal precioMedicamento) {
        this.precioMedicamento = precioMedicamento;
    }

    @Override
    public String toString() {
        return "DiagnosticoMedicamento{" +
                "id=" + id +
                ", idDiagnostico=" + idDiagnostico +
                ", idMedicamento=" + idMedicamento +
                ", indicaciones='" + indicaciones + '\'' +
                ", cantidad=" + cantidad +
                (nombreMedicamento != null ? ", nombreMedicamento='" + nombreMedicamento + '\'' : "") +
                (descripcionPresentacion != null ? ", descripcionPresentacion='" + descripcionPresentacion + '\'' : "") +
                (precioMedicamento != null ? ", precioMedicamento=" + precioMedicamento : "") +
                '}';
    }
}