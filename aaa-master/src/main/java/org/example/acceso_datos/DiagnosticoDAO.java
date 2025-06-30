package org.example.acceso_datos;

import org.example.acceso_datos.util.DatabaseConnector; // Asegúrate que esta clase existe y funciona
import org.example.modelo.Diagnostico;
import org.example.modelo.DiagnosticoMedicamento;
import org.example.modelo.Medicamento; // Necesario para construir DiagnosticoMedicamento con info del medicamento

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class DiagnosticoDAO {

    // --- RF-DGN-001: Registrar Diagnóstico Principal (asociado a una Cita) ---
    public int registrarDiagnostico(Diagnostico diagnostico, int idCitaACompletar) throws SQLException {
        String sqlDiagnostico = "INSERT INTO diagnosticos (id_cita, id_paciente, id_medico, fecha_diagnostico, notas_diagnostico) VALUES (?, ?, ?, ?, ?)";
        String sqlActualizarCita = "UPDATE citas SET estado = 'Completada' WHERE id = ?";
        Connection conn = null;
        PreparedStatement pstmtDiagnostico = null;
        PreparedStatement pstmtCita = null;
        ResultSet generatedKeys = null;
        int idDiagnosticoGenerado = -1;

        try {
            conn = DatabaseConnector.getConnection();
            conn.setAutoCommit(false); // Iniciar transacción

            // 1. Registrar el diagnóstico
            pstmtDiagnostico = conn.prepareStatement(sqlDiagnostico, Statement.RETURN_GENERATED_KEYS);
            pstmtDiagnostico.setInt(1, diagnostico.getIdCita());
            pstmtDiagnostico.setInt(2, diagnostico.getIdPaciente());
            pstmtDiagnostico.setInt(3, diagnostico.getIdMedico());
            pstmtDiagnostico.setTimestamp(4, Timestamp.valueOf(diagnostico.getFechaDiagnostico() != null ? diagnostico.getFechaDiagnostico() : LocalDateTime.now()));
            pstmtDiagnostico.setString(5, diagnostico.getNotasDiagnostico());
            pstmtDiagnostico.executeUpdate();

            generatedKeys = pstmtDiagnostico.getGeneratedKeys();
            if (generatedKeys.next()) {
                idDiagnosticoGenerado = generatedKeys.getInt(1);
                diagnostico.setId(idDiagnosticoGenerado); // Actualizar el ID en el objeto
            } else {
                throw new SQLException("Error al registrar el diagnóstico, no se obtuvo ID.");
            }

            // 2. Cambiar el estado de la cita a 'Completada'
            pstmtCita = conn.prepareStatement(sqlActualizarCita);
            pstmtCita.setInt(1, idCitaACompletar);
            int affectedRows = pstmtCita.executeUpdate();
            if (affectedRows == 0) {
                // Podría ser un warning o una excepción si se espera que siempre se actualice una fila
                System.err.println("Advertencia: No se actualizó ninguna cita con ID " + idCitaACompletar + " a 'Completada'.");
            }

            conn.commit(); // Confirmar transacción
            return idDiagnosticoGenerado;

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback(); // Revertir transacción en caso de error
                } catch (SQLException ex) {
                    System.err.println("Error al hacer rollback: " + ex.getMessage());
                }
            }
            System.err.println("Error SQL al registrar diagnóstico: " + e.getMessage());
            throw e; // Re-lanzar para que la capa de servicio la maneje
        } finally {
            if (generatedKeys != null) try { generatedKeys.close(); } catch (SQLException e) { /* Ignored */ }
            if (pstmtDiagnostico != null) try { pstmtDiagnostico.close(); } catch (SQLException e) { /* Ignored */ }
            if (pstmtCita != null) try { pstmtCita.close(); } catch (SQLException e) { /* Ignored */ }
            if (conn != null) try { conn.setAutoCommit(true); conn.close(); } catch (SQLException e) { /* Ignored */ }
        }
    }

    // --- RF-DGN-002: Agregar Medicamento a Diagnóstico (Prescripción) ---
    public boolean agregarMedicamentoADiagnostico(DiagnosticoMedicamento prescripcion) throws SQLException {
        String sql = "INSERT INTO diagnostico_medicamentos (id_diagnostico, id_medicamento, indicaciones, cantidad) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, prescripcion.getIdDiagnostico());
            pstmt.setInt(2, prescripcion.getIdMedicamento());
            pstmt.setString(3, prescripcion.getIndicaciones());
            pstmt.setInt(4, prescripcion.getCantidad());

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error SQL al agregar medicamento a diagnóstico: " + e.getMessage());
            throw e;
        }
    }

    // --- RF-DGN-003: Consultar Historial de Diagnósticos por Paciente ---
    public List<Diagnostico> obtenerDiagnosticosPorPaciente(int idPaciente) throws SQLException {
        List<Diagnostico> diagnosticos = new ArrayList<>();
        String sql = "SELECT * FROM diagnosticos WHERE id_paciente = ? ORDER BY fecha_diagnostico DESC";

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idPaciente);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    diagnosticos.add(mapResultSetToDiagnostico(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error SQL al obtener diagnósticos por paciente: " + e.getMessage());
            throw e;
        }
        return diagnosticos;
    }

    // --- RF-DGN-004: Consultar Detalle de Diagnóstico Específico (incluye medicamentos) ---
    public Diagnostico obtenerDiagnosticoDetalladoPorId(int idDiagnostico) throws SQLException {
        Diagnostico diagnostico = null;
        String sqlDiagnostico = "SELECT * FROM diagnosticos WHERE id = ?";
        // Unimos con medicamentos para obtener nombre, presentación y precio.
        // Solo medicamentos activos.
        String sqlMedicamentos = "SELECT dm.*, m.nombre AS nombre_medicamento, m.descripcion_presentacion, m.precio " +
                "FROM diagnostico_medicamentos dm " +
                "JOIN medicamentos m ON dm.id_medicamento = m.id " +
                "WHERE dm.id_diagnostico = ? AND m.activo = TRUE";

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmtDiagnostico = conn.prepareStatement(sqlDiagnostico);
             PreparedStatement pstmtMedicamentos = conn.prepareStatement(sqlMedicamentos)) {

            // 1. Obtener el diagnóstico principal
            pstmtDiagnostico.setInt(1, idDiagnostico);
            try (ResultSet rsDiagnostico = pstmtDiagnostico.executeQuery()) {
                if (rsDiagnostico.next()) {
                    diagnostico = mapResultSetToDiagnostico(rsDiagnostico);
                } else {
                    return null; // Diagnóstico no encontrado
                }
            }

            // 2. Obtener los medicamentos prescritos para ese diagnóstico
            List<DiagnosticoMedicamento> prescripciones = new ArrayList<>();
            pstmtMedicamentos.setInt(1, idDiagnostico);
            try (ResultSet rsMedicamentos = pstmtMedicamentos.executeQuery()) {
                while (rsMedicamentos.next()) {
                    prescripciones.add(mapResultSetToDiagnosticoMedicamentoConDetalles(rsMedicamentos));
                }
            }
            diagnostico.setMedicamentosPrescritos(prescripciones);

        } catch (SQLException e) {
            System.err.println("Error SQL al obtener detalle de diagnóstico: " + e.getMessage());
            throw e;
        }
        return diagnostico;
    }

    // --- RF-DGN-005: Modificar Notas del Diagnóstico ---
    public boolean modificarNotasDiagnostico(int idDiagnostico, String nuevasNotas) throws SQLException {
        String sql = "UPDATE diagnosticos SET notas_diagnostico = ?, fecha_modificacion = CURRENT_TIMESTAMP WHERE id = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nuevasNotas);
            pstmt.setInt(2, idDiagnostico);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error SQL al modificar notas del diagnóstico: " + e.getMessage());
            throw e;
        }
    }

    // --- RF-DGN-006: Eliminar Medicamento de un Diagnóstico (Quitar Prescripción) ---
    // Se puede eliminar por el ID de la tabla diagnostico_medicamentos si lo tienes,
    // o por la combinación de id_diagnostico e id_medicamento.
    // Optaré por el ID de la tabla intermedia (PK) si está disponible en el objeto DiagnosticoMedicamento.
    public boolean eliminarMedicamentoDeDiagnostico(int idDiagnosticoMedicamento) throws SQLException {
        String sql = "DELETE FROM diagnostico_medicamentos WHERE id = ?";
        // Considerar si se necesita verificar facturas aquí (RF-DGN-006 "Consideración")
        // Por ahora, se elimina directamente. La lógica de negocio iría en la capa de servicio.
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idDiagnosticoMedicamento);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error SQL al eliminar medicamento del diagnóstico: " + e.getMessage());
            throw e;
        }
    }

    // Alternativa si solo tienes idDiagnostico e idMedicamento
    public boolean eliminarMedicamentoDeDiagnostico(int idDiagnostico, int idMedicamento) throws SQLException {
        String sql = "DELETE FROM diagnostico_medicamentos WHERE id_diagnostico = ? AND id_medicamento = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idDiagnostico);
            pstmt.setInt(2, idMedicamento);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error SQL al eliminar medicamento del diagnóstico: " + e.getMessage());
            throw e;
        }
    }

    // --- RF-DGN-007: Eliminar Diagnóstico Completo ---
    public boolean eliminarDiagnosticoCompleto(int idDiagnostico) throws SQLException {
        // La FK en diagnostico_medicamentos tiene ON DELETE CASCADE,
        // por lo que al borrar un diagnóstico, sus medicamentos asociados se borrarán.
        // La FK en facturas tiene ON DELETE SET NULL, por lo que id_diagnostico en facturas se pondrá a NULL.
        String sql = "DELETE FROM diagnosticos WHERE id = ?";
        // Esta acción debe quedar registrada detalladamente (auditoría) - esto se maneja en la capa de servicio o un sistema de logging.
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idDiagnostico);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error SQL al eliminar diagnóstico completo: " + e.getMessage());
            // Se podría lanzar una excepción más específica si hay restricciones de FK que no sean SET NULL o CASCADE
            // (aunque aquí las FKs están definidas para permitirlo)
            if (e.getSQLState().equals("23000")) { // Código genérico para violación de integridad referencial
                System.err.println("No se puede eliminar el diagnóstico debido a referencias existentes (ej. facturas no anuladas, si la FK fuera RESTRICT).");
            }
            throw e;
        }
    }

    // --- Métodos Helper para Mapeo ---
    private Diagnostico mapResultSetToDiagnostico(ResultSet rs) throws SQLException {
        Diagnostico d = new Diagnostico();
        d.setId(rs.getInt("id"));
        d.setIdCita(rs.getInt("id_cita"));
        d.setIdPaciente(rs.getInt("id_paciente"));
        d.setIdMedico(rs.getInt("id_medico"));
        Timestamp fechaDiagnosticoTS = rs.getTimestamp("fecha_diagnostico");
        if (fechaDiagnosticoTS != null) {
            d.setFechaDiagnostico(fechaDiagnosticoTS.toLocalDateTime());
        }
        d.setNotasDiagnostico(rs.getString("notas_diagnostico"));
        Timestamp fechaCreacionTS = rs.getTimestamp("fecha_creacion");
        if (fechaCreacionTS != null) {
            d.setFechaCreacion(fechaCreacionTS.toLocalDateTime());
        }
        Timestamp fechaModificacionTS = rs.getTimestamp("fecha_modificacion");
        if (fechaModificacionTS != null) {
            d.setFechaModificacion(fechaModificacionTS.toLocalDateTime());
        }
        // La lista de medicamentos se llena por separado si es necesario (ej. en obtenerDiagnosticoDetalladoPorId)
        return d;
    }

    private DiagnosticoMedicamento mapResultSetToDiagnosticoMedicamentoConDetalles(ResultSet rs) throws SQLException {
        DiagnosticoMedicamento dm = new DiagnosticoMedicamento();
        dm.setId(rs.getInt("id")); // PK de diagnostico_medicamentos
        dm.setIdDiagnostico(rs.getInt("id_diagnostico"));
        dm.setIdMedicamento(rs.getInt("id_medicamento"));
        dm.setIndicaciones(rs.getString("indicaciones"));
        dm.setCantidad(rs.getInt("cantidad"));

        // Campos adicionales del medicamento (gracias al JOIN)
        // Asumo que DiagnosticoMedicamento tiene setters para estos, o un objeto Medicamento anidado
        dm.setNombreMedicamento(rs.getString("nombre_medicamento"));
        dm.setDescripcionPresentacion(rs.getString("descripcion_presentacion"));
        dm.setPrecioMedicamento(rs.getBigDecimal("precio"));

        return dm;
    }

    // --- Opcional: Obtener Cita para validación (usado por servicio antes de llamar a registrarDiagnostico) ---
    // Este método podría estar en CitaDAO, pero lo pongo aquí para ilustrar la validación del RF-DGN-001
    public String getEstadoCita(int idCita) throws SQLException {
        String sql = "SELECT estado FROM citas WHERE id = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idCita);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("estado");
                }
            }
        }
        return null; // Cita no encontrada
    }
}