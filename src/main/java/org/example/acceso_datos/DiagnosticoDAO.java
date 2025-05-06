// src/main/java/org/example/acceso_datos/DiagnosticoDAO.java
package org.example.acceso_datos;

import org.example.modelo.Diagnostico;
import org.example.modelo.DiagnosticoMedicamento;
import org.example.acceso_datos.util.DatabaseConnector; // Asumiendo que tienes esta clase

import java.sql.*;
import java.time.LocalDateTime; // Necesario para trabajar con fechas y horas
import java.util.ArrayList;
import java.util.List;

public class DiagnosticoDAO {

    // --- RF-DGN-001: Registrar Diagnóstico Principal ---
    // TODO: RF-DGN-001 DAO - Implementar método 'registrarDiagnosticoPrincipal'.
    //  - Parámetros: Objeto Diagnostico (con idCita, idPaciente, idMedico, notasDiagnostico, fechaDiagnostico).
    //  - Acción 1: INSERT INTO diagnosticos (id_cita, id_paciente, id_medico, fecha_diagnostico, notas_diagnostico) VALUES (?, ?, ?, ?, ?).
    //  - Acción 2: Obtener el ID autogenerado del diagnóstico insertado.
    //  - Acción 3: UPDATE citas SET estado = 'Completada' WHERE id = [idCita del diagnóstico].
    //  - Transaccionalidad: Ambas acciones (INSERT y UPDATE) deben ser parte de una transacción. Si una falla, ambas deben revertirse.
    //  - Retorno: El ID del diagnóstico generado.
    //  - Considerar: El campo 'fecha_modificacion' en 'diagnosticos' se actualiza automáticamente por la BD. 'fecha_creacion' también.
    /*
    public int registrarDiagnosticoPrincipal(Diagnostico diagnostico) throws SQLException {
        // SQL para insertar diagnóstico
        // SQL para actualizar cita
        // Lógica de transacción (conn.setAutoCommit(false), commit, rollback)
        // Retornar ID generado
        return -1; // Placeholder
    }
    */

    // --- RF-DGN-002: Agregar Medicamento a Diagnóstico (Prescripción) ---
    // TODO: RF-DGN-002 DAO - Implementar método 'agregarMedicamentoAPrescripcion'.
    //  - Parámetros: idDiagnostico, idMedicamento, indicaciones (String), cantidad (int).
    //  - Acción: INSERT INTO diagnostico_medicamentos (id_diagnostico, id_medicamento, indicaciones, cantidad) VALUES (?, ?, ?, ?).
    //  - Restricción: La BD tiene una UNIQUE KEY (uk_diag_med) en (id_diagnostico, id_medicamento) para prevenir duplicados.
    //               El DAO debe manejar la SQLException que esto podría generar (e.g., MySQLIntegrityConstraintViolationException).
    //  - Retorno: El ID generado de la tabla 'diagnostico_medicamentos' o un booleano de éxito.
    /*
    public int agregarMedicamentoAPrescripcion(int idDiagnostico, int idMedicamento, String indicaciones, int cantidad) throws SQLException {
        // SQL para insertar en diagnostico_medicamentos
        // Manejar posible excepción por UNIQUE KEY
        // Retornar ID generado o booleano
        return -1; // Placeholder
    }
    */

    // --- RF-DGN-003: Consultar Historial de Diagnósticos por Paciente ---
    // TODO: RF-DGN-003 DAO - Implementar método 'consultarHistorialPorPaciente'.
    //  - Parámetros: idPaciente (int).
    //  - Acción: SELECT d.id, d.fecha_diagnostico, d.notas_diagnostico, m.nombre AS medico_nombre, m.apellidos AS medico_apellidos
    //            FROM diagnosticos d
    //            JOIN medicos m ON d.id_medico = m.id
    //            WHERE d.id_paciente = ?
    //            ORDER BY d.fecha_diagnostico DESC;
    //  - Retorno: List<Diagnostico> donde cada objeto Diagnostico contiene al menos:
    //             id (del diagnóstico), fechaDiagnostico, notasDiagnostico (o un resumen), nombre completo del médico.
    //             (El servicio podría truncar notas_diagnostico si son muy largas para el resumen).
    /*
    public List<Diagnostico> consultarHistorialPorPaciente(int idPaciente) throws SQLException {
        // SQL para seleccionar diagnósticos y datos del médico
        // Mapear ResultSet a List<Diagnostico>
        return new ArrayList<>(); // Placeholder
    }
    */

    // --- RF-DGN-004: Consultar Detalle de Diagnóstico Específico ---
    // Para este RF, usualmente se necesitan dos métodos DAO o uno más complejo.
    // Mét0do 1: Obtener datos base del diagnóstico y nombres.
    // TODO: RF-DGN-004 DAO (Parte 1) - Implementar método 'obtenerDiagnosticoBaseConNombresPorId'.
    //  - Parámetros: idDiagnostico (int).
    //  - Acción: SELECT d.id, d.id_cita, d.id_paciente, d.id_medico, d.fecha_diagnostico, d.notas_diagnostico,
    //                   p.nombre AS paciente_nombre, p.apellidos AS paciente_apellidos,
    //                   doc.nombre AS medico_nombre, doc.apellidos AS medico_apellidos
    //            FROM diagnosticos d
    //            JOIN pacientes p ON d.id_paciente = p.id
    //            JOIN medicos doc ON d.id_medico = doc.id
    //            WHERE d.id = ?;
    //  - Retorno: Objeto Diagnostico con todos sus campos base populados, incluyendo nombres completos del paciente y médico.
    //             No incluye la lista de medicamentos aquí.
    /*
    public Diagnostico obtenerDiagnosticoBaseConNombresPorId(int idDiagnostico) throws SQLException {
        // SQL para seleccionar datos del diagnóstico y JOINs con pacientes y medicos
        // Mapear ResultSet a objeto Diagnostico
        return null; // Placeholder
    }
    */

    // Método 2: Obtener los medicamentos prescritos para un diagnóstico.
    // TODO: RF-DGN-004 DAO (Parte 2) - Implementar método 'obtenerMedicamentosDeDiagnostico'.
    //  - Parámetros: idDiagnostico (int).
    //  - Acción: SELECT dm.id, dm.id_medicamento, m.nombre AS medicamento_nombre, dm.cantidad, dm.indicaciones,
    //                   m.descripcion_presentacion, m.precio
    //            FROM diagnostico_medicamentos dm
    //            JOIN medicamentos m ON dm.id_medicamento = m.id
    //            WHERE dm.id_diagnostico = ? AND m.activo = TRUE;
    //  - Retorno: List<DiagnosticoMedicamento> donde cada objeto contiene id (de diagnostico_medicamentos), idMedicamento,
    //             nombreMedicamento, cantidad, indicaciones, descripcion_presentacion (del medicamento), y precio (del medicamento).
    //             Filtrar por medicamentos activos (m.activo = TRUE).
    /*
    public List<DiagnosticoMedicamento> obtenerMedicamentosDeDiagnostico(int idDiagnostico) throws SQLException {
        // SQL para seleccionar medicamentos prescritos y datos del medicamento
        // Mapear ResultSet a List<DiagnosticoMedicamento>
        return new ArrayList<>(); // Placeholder
    }
    */
    // NOTA RF-DGN-004: El servicio combinará los resultados de 'obtenerDiagnosticoBaseConNombresPorId' y 'obtenerMedicamentosDeDiagnostico'.

    // --- RF-DGN-005: Modificar Notas del Diagnóstico ---
    // TODO: RF-DGN-005 DAO - Implementar método 'modificarNotasDiagnostico'.
    //  - Parámetros: idDiagnostico (int), nuevasNotasDiagnostico (String).
    //  - Acción: UPDATE diagnosticos
    //            SET notas_diagnostico = ?, fecha_modificacion = CURRENT_TIMESTAMP
    //            WHERE id = ?;
    //  - Retorno: Booleano (true si se actualizó al menos una fila, false en caso contrario).
    //  - Considerar: 'fecha_modificacion' se actualiza aquí explícitamente o por trigger de BD si está configurado (ON UPDATE CURRENT_TIMESTAMP ya está en tu DDL).
    /*
    public boolean modificarNotasDiagnostico(int idDiagnostico, String nuevasNotas) throws SQLException {
        // SQL para actualizar notas_diagnostico y fecha_modificacion
        // Retornar true si executeUpdate() > 0
        return false; // Placeholder
    }
    */

    // --- RF-DGN-006: Eliminar Medicamento de un Diagnóstico (Quitar Prescripción) ---
    // TODO: RF-DGN-006 DAO - Implementar método 'eliminarMedicamentoDePrescripcion'.
    //  - Parámetros: idDiagnosticoMedicamento (int) - Este es el ID de la fila en la tabla 'diagnostico_medicamentos'.
    //    Alternativa: idDiagnostico (int) e idMedicamento (int) si es más fácil obtener esos dos.
    //  - Acción (si usa idDiagnosticoMedicamento): DELETE FROM diagnostico_medicamentos WHERE id = ?;
    //  - Acción (si usa idDiagnostico e idMedicamento): DELETE FROM diagnostico_medicamentos WHERE id_diagnostico = ? AND id_medicamento = ?;
    //  - Retorno: Booleano (true si se eliminó al menos una fila, false en caso contrario).
    //  - Nota: La lógica de negocio (ej. no eliminar si hay factura asociada) reside en el Servicio, no en el DAO.
    /*
    public boolean eliminarMedicamentoDePrescripcion(int idDiagnosticoMedicamento) throws SQLException {
        // SQL para eliminar de diagnostico_medicamentos
        // Retornar true si executeUpdate() > 0
        return false; // Placeholder
    }
    */

    // --- RF-DGN-007: Eliminar Diagnóstico Completo ---
    // TODO: RF-DGN-007 DAO - Implementar método 'eliminarDiagnosticoCompleto'.
    //  - Parámetros: idDiagnostico (int).
    //  - Acción: DELETE FROM diagnosticos WHERE id = ?;
    //  - Efecto cascada: La BD tiene 'ON DELETE CASCADE' para 'diagnostico_medicamentos.id_diagnostico',
    //    por lo que los medicamentos asociados se borrarán automáticamente.
    //  - Facturas: La BD tiene 'ON DELETE SET NULL' para 'facturas.id_diagnostico', por lo que no se borrarán,
    //    solo perderán la referencia al diagnóstico.
    //  - Retorno: Booleano (true si se eliminó al menos una fila, false en caso contrario).
    /*
    public boolean eliminarDiagnosticoCompleto(int idDiagnostico) throws SQLException {
        // SQL para eliminar de diagnosticos
        // Retornar true si executeUpdate() > 0
        return false; // Placeholder
    }
    */

    // TODO: (Opcional/Helper) Método 'obtenerDiagnosticoSimplePorId' para verificar existencia o datos básicos sin JOINs.
    //  - Parámetros: idDiagnostico (int).
    //  - Acción: SELECT id, id_cita, id_paciente, id_medico FROM diagnosticos WHERE id = ?;
    //  - Retorno: Objeto Diagnostico (solo con los IDs y campos básicos) o null si no existe.
    //  - Utilidad: Podría ser usado por otros métodos o servicios para validaciones rápidas.
    /*
    public Diagnostico obtenerDiagnosticoSimplePorId(int idDiagnostico) throws SQLException {
        // SQL para seleccionar campos básicos del diagnóstico
        // Mapear a objeto Diagnostico
        return null; // Placeholder
    }
    */
}