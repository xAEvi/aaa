package org.example.servicio;

import org.example.acceso_datos.MedicamentoDAO;
import org.example.modelo.Medicamento;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

public class ServicioMedicamento {

    private final MedicamentoDAO medicamentoDAO;

    public ServicioMedicamento() {
        this.medicamentoDAO = new MedicamentoDAO();
    }

    // CU-MDC-001: Registrar Nuevo Medicamento
    public String registrarMedicamento(Medicamento medicamento) {
        try {
            // Validaciones adicionales antes de llamar al DAO
            if (medicamento.getNombre() == null || medicamento.getNombre().trim().isEmpty()) {
                return "ERROR: El nombre del medicamento es obligatorio";
            }

            if (medicamento.getPrecio() == null || medicamento.getPrecio().compareTo(BigDecimal.ZERO) <= 0) {
                return "ERROR: El precio debe ser mayor a cero";
            }

            return medicamentoDAO.registrarMedicamento(medicamento);
        } catch (SQLException e) {
            return "ERROR: " + e.getMessage();
        }
    }

    // CU-MDC-002: Buscar Medicamentos
    public List<Medicamento> buscarMedicamentos(String nombre, BigDecimal precioMin, BigDecimal precioMax, boolean incluirInactivos) {
        try {
            return medicamentoDAO.buscarMedicamentos(nombre, precioMin, precioMax, incluirInactivos);
        } catch (SQLException e) {
            e.printStackTrace();
            return List.of(); // Retorna lista vacía en caso de error
        }
    }

    // CU-MDC-003: Modificar Medicamento Existente
    public String modificarMedicamento(Medicamento medicamento) {
        try {
            // Validaciones adicionales
            if (medicamento.getId() == null) {
                return "ERROR: ID de medicamento no proporcionado";
            }

            if (medicamento.getNombre() == null || medicamento.getNombre().trim().isEmpty()) {
                return "ERROR: El nombre del medicamento es obligatorio";
            }

            if (medicamento.getPrecio() == null || medicamento.getPrecio().compareTo(BigDecimal.ZERO) <= 0) {
                return "ERROR: El precio debe ser mayor a cero";
            }

            return medicamentoDAO.modificarMedicamento(medicamento);
        } catch (SQLException e) {
            return "ERROR: " + e.getMessage();
        }
    }

    // CU-MDC-004: Desactivar Medicamento
    public String desactivarMedicamento(int idMedicamento, String rolUsuario) {
        try {
            if (!"Administrador".equals(rolUsuario)) {
                return "ERROR: Solo los administradores pueden desactivar medicamentos";
            }

            return medicamentoDAO.desactivarMedicamento(idMedicamento, rolUsuario);
        } catch (SQLException e) {
            return "ERROR: " + e.getMessage();
        }
    }

    // CU-MDC-005: Reactivar Medicamento
    public String reactivarMedicamento(int idMedicamento, String rolUsuario) {
        try {
            if (!"Administrador".equals(rolUsuario)) {
                return "ERROR: Solo los administradores pueden reactivar medicamentos";
            }

            return medicamentoDAO.reactivarMedicamento(idMedicamento, rolUsuario);
        } catch (SQLException e) {
            return "ERROR: " + e.getMessage();
        }
    }

    // Método auxiliar para obtener un medicamento por ID
    public Medicamento obtenerMedicamentoPorId(int id) {
        try {
            return medicamentoDAO.obtenerMedicamentoPorId(id);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}