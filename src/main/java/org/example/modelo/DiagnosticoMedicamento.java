package org.example.modelo;

public class DiagnosticoMedicamento {
    private Integer id;
    private int idDiagnostico;
    private int idMedicamento;
    private String indicaciones;
    private int cantidad;

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

    @Override
    public String toString() {
        return "DiagnosticoMedicamento{" +
                "id=" + id +
                ", idDiagnostico=" + idDiagnostico +
                ", idMedicamento=" + idMedicamento +
                ", cantidad=" + cantidad +
                '}';
    }
}
