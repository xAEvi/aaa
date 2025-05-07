package org.example.servicio;

import org.example.acceso_datos.DiagnosticoDAO;
// Si necesitas validar contra otros DAOs (ej. CitaDAO, FacturaDAO), los importarías aquí
// import org.example.acceso_datos.CitaDAO;
import org.example.modelo.Diagnostico;
import org.example.modelo.DiagnosticoMedicamento;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public class ServicioDiagnostico {

    private final DiagnosticoDAO diagnosticoDAO;
    // private final CitaDAO citaDAO; // Si el método getEstadoCita estuviera en CitaDAO

    public ServicioDiagnostico() {
        this.diagnosticoDAO = new DiagnosticoDAO();
        // this.citaDAO = new CitaDAO();
    }

    /**
     * RF-DGN-001: Registra un nuevo diagnóstico asociado a una cita.
     * Valida la cita y las notas del diagnóstico antes de proceder.
     * @param diagnostico El objeto Diagnostico a registrar (idCita, idPaciente, idMedico, notasDiagnostico deben estar seteados).
     *                    La fechaDiagnostico se tomará como actual si es null.
     * @param idCita El ID de la cita a la que se asocia el diagnóstico y que se marcará como 'Completada'.
     * @return El ID del diagnóstico generado.
     * @throws IllegalArgumentException Si las notas del diagnóstico están vacías, la cita no se encuentra o está cancelada.
     * @throws SQLException Si ocurre un error durante la operación de base de datos.
     */
    public int registrarDiagnosticoServicio(Diagnostico diagnostico, int idCita)
            throws SQLException, IllegalArgumentException {

        // Validación de RF-DGN-001: "El usuario (Médico) debe seleccionar una cita válida (que no esté cancelada)"
        String estadoCita = diagnosticoDAO.getEstadoCita(idCita); // Usamos el método del DAO
        if (estadoCita == null) {
            throw new IllegalArgumentException("La cita con ID " + idCita + " no fue encontrada.");
        }
        if ("Cancelada".equalsIgnoreCase(estadoCita)) {
            throw new IllegalArgumentException("No se puede registrar un diagnóstico para una cita cancelada (ID: " + idCita + ").");
        }
        // Opcional: Verificar si la cita ya está 'Completada' y si se permite un nuevo diagnóstico.
        // if ("Completada".equalsIgnoreCase(estadoCita)) {
        //     throw new IllegalArgumentException("La cita con ID " + idCita + " ya está completada.");
        // }


        // Validación de RF-DGN-001: "Es obligatorio escribir las notas del diagnóstico."
        if (diagnostico.getNotasDiagnostico() == null || diagnostico.getNotasDiagnostico().trim().isEmpty()) {
            throw new IllegalArgumentException("Las notas del diagnóstico son obligatorias.");
        }

        // Asegurar que la fecha del diagnóstico se establezca si no viene
        if (diagnostico.getFechaDiagnostico() == null) {
            diagnostico.setFechaDiagnostico(LocalDateTime.now());
        }

        // La regla "Generalmente, solo se puede registrar un diagnóstico principal por cada cita"
        // está cubierta por la restricción UNIQUE en la BD (diagnosticos.id_cita).
        // Si el DAO lanza una SQLException por violación de UNIQUE, se propagará.

        // La regla "Se necesita permiso de 'Medico' para realizar esta acción"
        // se asume que se maneja en la capa de presentación o con un sistema de autenticación/autorización.

        return diagnosticoDAO.registrarDiagnostico(diagnostico, idCita);
    }

    /**
     * RF-DGN-002: Agrega un medicamento a un diagnóstico existente.
     * @param prescripcion El objeto DiagnosticoMedicamento a agregar.
     * @return true si se agregó correctamente, false en caso contrario.
     * @throws IllegalArgumentException Si la cantidad es inválida.
     * @throws SQLException Si ocurre un error durante la operación de base de datos (ej. medicamento duplicado).
     */
    public boolean agregarMedicamentoADiagnosticoServicio(DiagnosticoMedicamento prescripcion)
            throws SQLException, IllegalArgumentException {

        if (prescripcion.getCantidad() <= 0) {
            throw new IllegalArgumentException("La cantidad del medicamento debe ser mayor a cero.");
        }

        // La regla "No se puede añadir el mismo medicamento más de una vez a la misma receta/diagnóstico"
        // está cubierta por la restricción UNIQUE KEY uk_diag_med (id_diagnostico, id_medicamento) en la BD.
        // Si el DAO lanza una SQLException por violación de UNIQUE, se propagará.

        // La regla "Se necesita permiso de 'Medico'" se asume manejada externamente.
        // La regla "buscar y seleccionar un medicamento del catálogo del sistema (solo medicamentos activos)"
        // es responsabilidad de la UI, que debería proporcionar un id_medicamento válido y activo.

        return diagnosticoDAO.agregarMedicamentoADiagnostico(prescripcion);
    }

    /**
     * RF-DGN-003: Consulta el historial de diagnósticos para un paciente específico.
     * @param idPaciente El ID del paciente.
     * @return Una lista de diagnósticos, ordenada cronológicamente (más reciente primero).
     * @throws SQLException Si ocurre un error durante la operación de base de datos.
     */
    public List<Diagnostico> consultarHistorialDiagnosticosPacienteServicio(int idPaciente) throws SQLException {
        return diagnosticoDAO.obtenerDiagnosticosPorPaciente(idPaciente);
    }

    /**
     * RF-DGN-004: Consulta el detalle completo de un diagnóstico específico, incluyendo medicamentos.
     * @param idDiagnostico El ID del diagnóstico.
     * @return El objeto Diagnostico con su detalle y medicamentos, o null si no se encuentra.
     * @throws SQLException Si ocurre un error durante la operación de base de datos.
     */
    public Diagnostico consultarDiagnosticoDetalladoServicio(int idDiagnostico) throws SQLException {
        // La regla "Solo se mostrarán medicamentos que estén actualmente activos en el catálogo"
        // es manejada por el JOIN y la condición `m.activo = TRUE` en el DAO.
        // La regla "El acceso podría estar limitado por rol de usuario" se asume manejada externamente.
        return diagnosticoDAO.obtenerDiagnosticoDetalladoPorId(idDiagnostico);
    }

    /**
     * RF-DGN-005: Modifica únicamente las notas descriptivas de un diagnóstico existente.
     * @param idDiagnostico El ID del diagnóstico a modificar.
     * @param nuevasNotas Las nuevas notas para el diagnóstico.
     * @return true si se modificó correctamente, false en caso contrario.
     * @throws IllegalArgumentException Si las nuevas notas están vacías.
     * @throws SQLException Si ocurre un error durante la operación de base de datos.
     */
    public boolean modificarNotasDiagnosticoServicio(int idDiagnostico, String nuevasNotas)
            throws SQLException, IllegalArgumentException {

        if (nuevasNotas == null || nuevasNotas.trim().isEmpty()) {
            throw new IllegalArgumentException("Las nuevas notas del diagnóstico no pueden estar vacías.");
        }

        // La regla "Solo usuarios con permiso ('Medico', quizás solo el médico original o un administrador) pueden modificar"
        // y "Es recomendable registrar quién y cuándo hizo cambios (auditoría)"
        // se asumen manejadas externamente o parcialmente por la BD (fecha_modificacion).
        // La no modificación de otros datos está implícita en la query del DAO.

        return diagnosticoDAO.modificarNotasDiagnostico(idDiagnostico, nuevasNotas);
    }

    /**
     * RF-DGN-006: Elimina una prescripción de medicamento de un diagnóstico.
     * @param idDiagnosticoMedicamento El ID de la entrada en la tabla diagnostico_medicamentos.
     * @return true si se eliminó correctamente, false en caso contrario.
     * @throws SQLException Si ocurre un error durante la operación de base de datos.
     * @throws IllegalStateException Si no se puede eliminar debido a restricciones (ej. factura existente, no implementado aquí).
     */
    public boolean eliminarMedicamentoDeDiagnosticoServicio(int idDiagnosticoMedicamento)
            throws SQLException, IllegalStateException {

        // RF-DGN-006 "Consideración: Evaluar si se debe bloquear esta acción si ya se ha generado una factura..."
        // Esta lógica es compleja y requeriría un FacturaDAO. Por simplicidad, no se implementa aquí.
        // boolean existeFactura = false; // Lógica para verificar con FacturaDAO
        // if (existeFactura) {
        //     throw new IllegalStateException("No se puede eliminar el medicamento, ya está asociado a una factura no anulada.");
        // }

        // La regla "Se necesita permiso de 'Medico'" se asume manejada externamente.
        return diagnosticoDAO.eliminarMedicamentoDeDiagnostico(idDiagnosticoMedicamento);
    }

    /**
     * RF-DGN-006 (Alternativa): Elimina una prescripción de medicamento de un diagnóstico.
     * @param idDiagnostico El ID del diagnóstico.
     * @param idMedicamento El ID del medicamento.
     * @return true si se eliminó correctamente, false en caso contrario.
     * @throws SQLException Si ocurre un error durante la operación de base de datos.
     * @throws IllegalStateException Si no se puede eliminar debido a restricciones (ej. factura existente, no implementado aquí).
     */
    public boolean eliminarMedicamentoDeDiagnosticoServicio(int idDiagnostico, int idMedicamento)
            throws SQLException, IllegalStateException {
        // Misma consideración sobre facturas que el método anterior.
        // boolean existeFactura = false; // Lógica para verificar con FacturaDAO
        // if (existeFactura) {
        //     throw new IllegalStateException("No se puede eliminar el medicamento, ya está asociado a una factura no anulada.");
        // }
        return diagnosticoDAO.eliminarMedicamentoDeDiagnostico(idDiagnostico, idMedicamento);
    }


    /**
     * RF-DGN-007: Elimina un diagnóstico completo, incluyendo sus prescripciones.
     * La confirmación explícita debe ser manejada por la UI.
     * @param idDiagnostico El ID del diagnóstico a eliminar.
     * @return true si se eliminó correctamente, false en caso contrario.
     * @throws SQLException Si ocurre un error durante la operación de base de datos.
     */
    public boolean eliminarDiagnosticoCompletoServicio(int idDiagnostico) throws SQLException {
        // La regla "Se necesita rol de 'Administrador'" y la "confirmación explícita"
        // se asumen manejadas por la capa de presentación.
        // La regla "Las facturas que estaban vinculadas a este diagnóstico no se borrarán,
        // pero perderán la referencia directa a él" es manejada por `ON DELETE SET NULL` en la FK.
        // La regla "El estado de la cita asociada ('Completada') no se modificará automáticamente" es correcta.
        // La regla "Esta acción debe quedar registrada detalladamente (auditoría)" se maneja parcialmente
        // por un sistema de logging externo o triggers.

        return diagnosticoDAO.eliminarDiagnosticoCompleto(idDiagnostico);
    }
}