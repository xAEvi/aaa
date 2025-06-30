package org.example.modelo;

import java.time.LocalDateTime;

public class Cita {
    private Integer id;
    private int idPaciente; // FK a Paciente
    private int idMedico;   // FK a Medico
    private LocalDateTime fechaHora;
    private String estado; // 'Agendada', 'Confirmada', 'Cancelada', 'Completada'
    private String notas;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaModificacion;

    // Para un modelo de dominio más rico, podrías tener objetos Paciente y Medico aquí
    // private Paciente paciente;
    // private Medico medico;

    // Constructor vacío
    public Cita() {
        this.estado = "Agendada"; // Valor por defecto
    }

    // Constructor con campos principales
    public Cita(int idPaciente, int idMedico, LocalDateTime fechaHora) {
        this.idPaciente = idPaciente;
        this.idMedico = idMedico;
        this.fechaHora = fechaHora;
        this.estado = "Agendada";
    }

    // Getters y Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getIdPaciente() {
        return idPaciente;
    }

    public void setIdPaciente(int idPaciente) {
        this.idPaciente = idPaciente;
    }

    public int getIdMedico() {
        return idMedico;
    }

    public void setIdMedico(int idMedico) {
        this.idMedico = idMedico;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getNotas() {
        return notas;
    }

    public void setNotas(String notas) {
        this.notas = notas;
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
        return "Cita{" +
                "id=" + id +
                ", idPaciente=" + idPaciente +
                ", idMedico=" + idMedico +
                ", fechaHora=" + fechaHora +
                ", estado='" + estado + '\'' +
                '}';
    }
}