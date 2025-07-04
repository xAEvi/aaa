package org.example.servicio;

import org.example.acceso_datos.DiagnosticoDAO;
import org.example.modelo.Diagnostico;
import org.example.modelo.DiagnosticoMedicamento;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Gestiona la lógica de negocio para los diagnósticos médicos.
 * Se encarga de las validaciones y de coordinar las operaciones con la capa de acceso a datos.
 */
public class ServicioDiagnostico {

    /**
     *
     *
     * @author Xavier Molina Cisneros
     */

    private static final String ESTADO_CITA_CANCELADA = "Cancelada";

    /**
     * Objeto de Acceso a Datos (DAO) para diagnósticos.
     * Encapsula la interacción con la base de datos.
     */
    private final DiagnosticoDAO diagnosticoDAO;

    /**
     * Constructor que inicializa el DAO de diagnóstico.
     */
    public ServicioDiagnostico() {
        this.diagnosticoDAO = new DiagnosticoDAO();
    }

    /**
     * RF-DGN-001: Registra un nuevo diagnóstico asociado a una cita.
     *
     * @param diagnostico Objeto con los datos del diagnóstico.
     * @param idCita      ID de la cita a asociar y completar.
     * @return El ID del diagnóstico generado.
     * @throws SQLException Si ocurre un error en la base de datos.
     * @throws IllegalArgumentException Si la cita no es válida o las notas están vacías.
     */
    public int registrarDiagnosticoServicio(Diagnostico diagnostico, int idCita)
            throws SQLException {

        String estadoCita = diagnosticoDAO.getEstadoCita(idCita);
        if (estadoCita == null) {
            throw new IllegalArgumentException("La cita con ID " + idCita + " no fue encontrada.");
        }
        if (ESTADO_CITA_CANCELADA.equalsIgnoreCase(estadoCita)) {
            throw new IllegalArgumentException("No se puede registrar un diagnóstico para una cita cancelada (ID: " + idCita + ").");
        }

        if (diagnostico.getNotasDiagnostico() == null || diagnostico.getNotasDiagnostico().isBlank()) {
            throw new IllegalArgumentException("Las notas del diagnóstico son obligatorias.");
        }

        if (diagnostico.getFechaDiagnostico() == null) {
            diagnostico.setFechaDiagnostico(LocalDateTime.now());
        }

        return diagnosticoDAO.registrarDiagnostico(diagnostico, idCita);
    }

    /**
     * RF-DGN-002: Agrega un medicamento a un diagnóstico existente.
     *
     * @param prescripcion El objeto DiagnosticoMedicamento a agregar.
     * @return true si se agregó correctamente.
     * @throws SQLException Si ocurre un error en la base de datos.
     * @throws IllegalArgumentException Si la cantidad es inválida.
     */
    public boolean agregarMedicamentoADiagnosticoServicio(DiagnosticoMedicamento prescripcion)
            throws SQLException {

        if (prescripcion.getCantidad() <= 0) {
            throw new IllegalArgumentException("La cantidad del medicamento debe ser mayor a cero.");
        }
        return diagnosticoDAO.agregarMedicamentoADiagnostico(prescripcion);
    }

    /**
     * RF-DGN-003: Consulta el historial de diagnósticos de un paciente.
     *
     * @param idPaciente El ID del paciente.
     * @return Lista de diagnósticos del paciente.
     * @throws SQLException Si ocurre un error en la base de datos.
     */
    public List<Diagnostico> consultarHistorialDiagnosticosPacienteServicio(int idPaciente) throws SQLException {
        return diagnosticoDAO.obtenerDiagnosticosPorPaciente(idPaciente);
    }

    /**
     * RF-DGN-004: Consulta el detalle completo de un diagnóstico.
     *
     * @param idDiagnostico El ID del diagnóstico a consultar.
     * @return El diagnóstico con sus detalles, o null si no se encuentra.
     * @throws SQLException Si ocurre un error en la base de datos.
     */
    public Diagnostico consultarDiagnosticoDetalladoServicio(int idDiagnostico) throws SQLException {
        return diagnosticoDAO.obtenerDiagnosticoDetalladoPorId(idDiagnostico);
    }

    /**
     * RF-DGN-005: Modifica las notas de un diagnóstico.
     *
     * @param idDiagnostico El ID del diagnóstico a modificar.
     * @param nuevasNotas   Las nuevas notas descriptivas.
     * @return true si la modificación fue exitosa.
     * @throws SQLException Si ocurre un error en la base de datos.
     * @throws IllegalArgumentException Si las nuevas notas están vacías.
     */
    public boolean modificarNotasDiagnosticoServicio(int idDiagnostico, String nuevasNotas)
            throws SQLException {

        if (nuevasNotas == null || nuevasNotas.isBlank()) {
            throw new IllegalArgumentException("Las nuevas notas del diagnóstico no pueden estar vacías.");
        }
        return diagnosticoDAO.modificarNotasDiagnostico(idDiagnostico, nuevasNotas);
    }

    /**
     * RF-DGN-006: Elimina una prescripción de medicamento de un diagnóstico.
     *
     * @param idPrescripcion El ID de la prescripción a eliminar (tabla diagnostico_medicamentos).
     * @return true si se eliminó correctamente.
     * @throws SQLException Si ocurre un error en la base de datos.
     * @throws IllegalStateException Si no se puede eliminar por alguna restricción.
     */
    public boolean eliminarMedicamentoDeDiagnosticoServicio(int idPrescripcion)
            throws SQLException {
        return diagnosticoDAO.eliminarMedicamentoDeDiagnostico(idPrescripcion);
    }

    /**
     * RF-DGN-006 (Alternativa): Elimina un medicamento de un diagnóstico por sus IDs.
     *
     * @param idDiagnostico El ID del diagnóstico.
     * @param idMedicamento El ID del medicamento.
     * @return true si se eliminó correctamente.
     * @throws SQLException Si ocurre un error en la base de datos.
     * @throws IllegalStateException Si no se puede eliminar por alguna restricción.
     */
    public boolean eliminarMedicamentoDeDiagnosticoServicio(int idDiagnostico, int idMedicamento)
            throws SQLException {
        return diagnosticoDAO.eliminarMedicamentoDeDiagnostico(idDiagnostico, idMedicamento);
    }

    /**
     * RF-DGN-007: Elimina un diagnóstico completo y sus prescripciones.
     *
     * @param idDiagnostico El ID del diagnóstico a eliminar.
     * @return true si se eliminó correctamente.
     * @throws SQLException Si ocurre un error en la base de datos.
     */
    public boolean eliminarDiagnosticoCompletoServicio(int idDiagnostico) throws SQLException {
        return diagnosticoDAO.eliminarDiagnosticoCompleto(idDiagnostico);
    }
}