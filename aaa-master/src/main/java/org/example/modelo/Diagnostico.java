// src/main/java/org/example/modelo/Diagnostico.java
package org.example.modelo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Diagnostico {
    private Integer id;
    private int idCita;
    private int idPaciente;
    private int idMedico;
    private LocalDateTime fechaDiagnostico;
    private String notasDiagnostico;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaModificacion;

    // Campos para información adicional (poblados por DAO/Servicio con JOINs o lógica adicional)
    private String nombrePacienteCompleto;
    private String nombreMedicoCompleto;

    private List<DiagnosticoMedicamento> medicamentosPrescritos;

    public Diagnostico() {
        this.medicamentosPrescritos = new ArrayList<>();
    }

    // Constructor con campos principales (puedes añadir más o usar setters)
    public Diagnostico(int idCita, int idPaciente, int idMedico, LocalDateTime fechaDiagnostico, String notasDiagnostico) {
        this(); // Llama al constructor por defecto para inicializar la lista
        this.idCita = idCita;
        this.idPaciente = idPaciente;
        this.idMedico = idMedico;
        this.fechaDiagnostico = fechaDiagnostico;
        this.notasDiagnostico = notasDiagnostico;
    }

    // Getters y Setters para todos los campos, incluyendo los nuevos
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public int getIdCita() { return idCita; }
    public void setIdCita(int idCita) { this.idCita = idCita; }
    public int getIdPaciente() { return idPaciente; }
    public void setIdPaciente(int idPaciente) { this.idPaciente = idPaciente; }
    public int getIdMedico() { return idMedico; }
    public void setIdMedico(int idMedico) { this.idMedico = idMedico; }
    public LocalDateTime getFechaDiagnostico() { return fechaDiagnostico; }
    public void setFechaDiagnostico(LocalDateTime fechaDiagnostico) { this.fechaDiagnostico = fechaDiagnostico; }
    public String getNotasDiagnostico() { return notasDiagnostico; }
    public void setNotasDiagnostico(String notasDiagnostico) { this.notasDiagnostico = notasDiagnostico; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
    public LocalDateTime getFechaModificacion() { return fechaModificacion; }
    public void setFechaModificacion(LocalDateTime fechaModificacion) { this.fechaModificacion = fechaModificacion; }

    public String getNombrePacienteCompleto() { return nombrePacienteCompleto; }
    public void setNombrePacienteCompleto(String nombrePacienteCompleto) { this.nombrePacienteCompleto = nombrePacienteCompleto; }
    public String getNombreMedicoCompleto() { return nombreMedicoCompleto; }
    public void setNombreMedicoCompleto(String nombreMedicoCompleto) { this.nombreMedicoCompleto = nombreMedicoCompleto; }

    public List<DiagnosticoMedicamento> getMedicamentosPrescritos() { return medicamentosPrescritos; }
    public void setMedicamentosPrescritos(List<DiagnosticoMedicamento> medicamentosPrescritos) { this.medicamentosPrescritos = medicamentosPrescritos; }
    public void addMedicamentoPrescrito(DiagnosticoMedicamento dm) {
        if (this.medicamentosPrescritos == null) {
            this.medicamentosPrescritos = new ArrayList<>();
        }
        this.medicamentosPrescritos.add(dm);
    }

    @Override
    public String toString() {
        return "Diagnostico{" + "id=" + id + ", idCita=" + idCita + ", fechaDiagnostico=" + fechaDiagnostico + '}';
    }
}