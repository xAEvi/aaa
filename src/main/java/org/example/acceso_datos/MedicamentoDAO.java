package org.example.acceso_datos;

import org.example.acceso_datos.util.DatabaseConnector;
import org.example.modelo.Medicamento;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MedicamentoDAO {

    // RF-MDC-001: Registrar Nuevo Medicamento
    public String registrarMedicamento(Medicamento medicamento) throws SQLException {
        String sql = "{call sp_medicamento_registrar(?, ?, ?, ?)}";

        try (Connection conn = DatabaseConnector.getConnection();
             CallableStatement cstmt = conn.prepareCall(sql)) {

            cstmt.setString(1, medicamento.getNombre());
            cstmt.setString(2, medicamento.getDescripcionPresentacion());
            cstmt.setBigDecimal(3, medicamento.getPrecio());
            cstmt.registerOutParameter(4, Types.VARCHAR);

            cstmt.execute();

            return cstmt.getString(4);
        }
    }

    // RF-MDC-002: Búsqueda Avanzada de Medicamentos
    public List<Medicamento> buscarMedicamentos(String nombre, BigDecimal precioMin, BigDecimal precioMax, boolean incluirInactivos)
            throws SQLException {
        List<Medicamento> medicamentos = new ArrayList<>();
        String sql = "{call sp_medicamento_buscar(?, ?, ?, ?)}";

        try (Connection conn = DatabaseConnector.getConnection();
             CallableStatement cstmt = conn.prepareCall(sql)) {

            cstmt.setString(1, nombre);
            cstmt.setBigDecimal(2, precioMin);
            cstmt.setBigDecimal(3, precioMax);
            cstmt.setBoolean(4, incluirInactivos);

            try (ResultSet rs = cstmt.executeQuery()) {
                while (rs.next()) {
                    Medicamento m = new Medicamento();
                    m.setId(rs.getInt("id"));
                    m.setNombre(rs.getString("nombre"));
                    m.setPrecio(rs.getBigDecimal("precio"));
                    m.setDescripcionPresentacion(rs.getString("descripcion_presentacion"));
                    m.setActivo(rs.getBoolean("activo"));

                    medicamentos.add(m);
                }
            }
        }
        return medicamentos;
    }

    // RF-MDC-003: Editar Información de Medicamento
    public String modificarMedicamento(Medicamento medicamento) throws SQLException {
        String sql = "{call sp_medicamento_modificar(?, ?, ?, ?, ?)}";

        try (Connection conn = DatabaseConnector.getConnection();
             CallableStatement cstmt = conn.prepareCall(sql)) {

            cstmt.setInt(1, medicamento.getId());
            cstmt.setString(2, medicamento.getNombre());
            cstmt.setString(3, medicamento.getDescripcionPresentacion());
            cstmt.setBigDecimal(4, medicamento.getPrecio());
            cstmt.registerOutParameter(5, Types.VARCHAR);

            cstmt.execute();

            return cstmt.getString(5);
        }
    }

    // RF-MDC-004: Desactivar Medicamento
    public String desactivarMedicamento(int idMedicamento, String rolUsuario) throws SQLException {
        String sql = "{call sp_medicamento_desactivar(?, ?, ?)}";

        try (Connection conn = DatabaseConnector.getConnection();
             CallableStatement cstmt = conn.prepareCall(sql)) {

            cstmt.setInt(1, idMedicamento);
            cstmt.setString(2, rolUsuario);
            cstmt.registerOutParameter(3, Types.VARCHAR);

            cstmt.execute();

            return cstmt.getString(3);
        }
    }

    // RF-MDC-004: Reactivar Medicamento
    public String reactivarMedicamento(int idMedicamento, String rolUsuario) throws SQLException {
        String sql = "{call sp_medicamento_reactivar(?, ?, ?)}";

        try (Connection conn = DatabaseConnector.getConnection();
             CallableStatement cstmt = conn.prepareCall(sql)) {

            cstmt.setInt(1, idMedicamento);
            cstmt.setString(2, rolUsuario);
            cstmt.registerOutParameter(3, Types.VARCHAR);

            cstmt.execute();

            return cstmt.getString(3);
        }
    }

    // Método auxiliar para obtener un medicamento por ID
    public Medicamento obtenerMedicamentoPorId(int id) throws SQLException {
        String sql = "SELECT * FROM medicamentos WHERE id = ?";

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Medicamento m = new Medicamento();
                    m.setId(rs.getInt("id"));
                    m.setNombre(rs.getString("nombre"));
                    m.setDescripcionPresentacion(rs.getString("descripcion_presentacion"));
                    m.setPrecio(rs.getBigDecimal("precio"));
                    m.setActivo(rs.getBoolean("activo"));

                    Timestamp fechaRegistroTS = rs.getTimestamp("fecha_registro");
                    if (fechaRegistroTS != null) {
                        m.setFechaRegistro(fechaRegistroTS.toLocalDateTime());
                    }

                    Timestamp fechaModificacionTS = rs.getTimestamp("fecha_modificacion");
                    if (fechaModificacionTS != null) {
                        m.setFechaModificacion(fechaModificacionTS.toLocalDateTime());
                    }

                    return m;
                }
            }
        }
        return null;
    }
}