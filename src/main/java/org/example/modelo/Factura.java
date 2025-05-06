package org.example.modelo;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Factura {
    private Integer id;
    private int idPaciente;
    private Integer idCita; // Puede ser nulo
    private Integer idDiagnostico; // Puede ser nulo
    private String numeroFactura;
    private LocalDateTime fechaEmision;
    private BigDecimal montoServicioMedico;
    private BigDecimal montoMedicamentos;
    private BigDecimal montoTotal;
    private String estado; // 'Pendiente', 'Pagada', 'Anulada'
    private LocalDateTime fechaPago;
    private String metodoPago;
    private String notas;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaModificacion;

    // Constructor vacío
    public Factura() {
        this.montoServicioMedico = BigDecimal.ZERO;
        this.montoMedicamentos = BigDecimal.ZERO;
        this.montoTotal = BigDecimal.ZERO;
        this.estado = "Pendiente"; // Valor por defecto
    }

    // Constructor con campos principales
    public Factura(int idPaciente, String numeroFactura, LocalDateTime fechaEmision, BigDecimal montoTotal) {
        this(); // Llama al constructor vacío para los defaults
        this.idPaciente = idPaciente;
        this.numeroFactura = numeroFactura;
        this.fechaEmision = fechaEmision;
        this.montoTotal = montoTotal; // Simplificado, podrías calcularlo
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

    public Integer getIdCita() {
        return idCita;
    }

    public void setIdCita(Integer idCita) {
        this.idCita = idCita;
    }

    public Integer getIdDiagnostico() {
        return idDiagnostico;
    }

    public void setIdDiagnostico(Integer idDiagnostico) {
        this.idDiagnostico = idDiagnostico;
    }

    public String getNumeroFactura() {
        return numeroFactura;
    }

    public void setNumeroFactura(String numeroFactura) {
        this.numeroFactura = numeroFactura;
    }

    public LocalDateTime getFechaEmision() {
        return fechaEmision;
    }

    public void setFechaEmision(LocalDateTime fechaEmision) {
        this.fechaEmision = fechaEmision;
    }

    public BigDecimal getMontoServicioMedico() {
        return montoServicioMedico;
    }

    public void setMontoServicioMedico(BigDecimal montoServicioMedico) {
        this.montoServicioMedico = montoServicioMedico;
    }

    public BigDecimal getMontoMedicamentos() {
        return montoMedicamentos;
    }

    public void setMontoMedicamentos(BigDecimal montoMedicamentos) {
        this.montoMedicamentos = montoMedicamentos;
    }

    public BigDecimal getMontoTotal() {
        return montoTotal;
    }

    public void setMontoTotal(BigDecimal montoTotal) {
        this.montoTotal = montoTotal;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public LocalDateTime getFechaPago() {
        return fechaPago;
    }

    public void setFechaPago(LocalDateTime fechaPago) {
        this.fechaPago = fechaPago;
    }

    public String getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
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
        return "Factura{" +
                "id=" + id +
                ", numeroFactura='" + numeroFactura + '\'' +
                ", idPaciente=" + idPaciente +
                ", montoTotal=" + montoTotal +
                ", estado='" + estado + '\'' +
                '}';
    }
}