// src/main/java/org/example/presentacion/VentanaDiagnostico.java
package org.example.presentacion;

import org.example.modelo.Diagnostico; // Necesitas importar tus modelos
import org.example.modelo.DiagnosticoMedicamento;
import org.example.modelo.Medicamento; // Para el ComboBox y la búsqueda
import org.example.servicio.ServicioDiagnostico; // Importa tu servicio

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class VentanaDiagnostico extends JFrame {

    // Pestañas
    private JTabbedPane tabbedPane;

    // Pestaña 1: Registrar / Modificar Diagnóstico (RF-DGN-001, RF-DGN-005)
    private JPanel panelRegistroModificacion;
    private JTextField txtCitaIdParaNuevoDiagnostico;
    private JTextField txtDiagnosticoIdParaCargar;
    private JTextArea txtAreaNotasDiagnostico;
    private JButton btnCargarDiagnosticoPorId;
    private JButton btnGuardarNuevoDiagnostico;
    private JButton btnActualizarNotasDiagnostico;
    private JLabel lblPacienteInfoDiagnostico;
    private JLabel lblMedicoInfoDiagnostico;
    private JLabel lblFechaDiagnostico;
    private Diagnostico diagnosticoCargado; // Para mantener el diagnóstico actual cargado

    // Pestaña 2: Prescripción de Medicamentos (RF-DGN-002, RF-DGN-006)
    private JPanel panelPrescripcion;
    private JTextField txtDiagnosticoIdParaPrescripcion;
    private JButton btnCargarDiagnosticoParaPrescripcion;
    private JTextField txtBuscarMedicamentoCatalogo;
    private JButton btnBuscarMedicamento;
    private JComboBox<MedicamentoComboBoxItem> cmbMedicamentosEncontrados; // Usaremos un item personalizado
    private JTextField txtCantidadMedicamento;
    private JTextArea txtAreaIndicacionesMedicamento;
    private JButton btnAgregarMedicamentoAPrescripcion;
    private JTable tblMedicamentosPrescritos;
    private DefaultTableModel modelMedicamentosPrescritos;
    private JButton btnQuitarMedicamentoSeleccionado;

    // Pestaña 3: Historial y Consulta Detallada (RF-DGN-003, RF-DGN-004)
    private JPanel panelHistorialConsulta;
    private JTextField txtPacienteIdParaHistorial;
    private JButton btnBuscarHistorialPaciente;
    private JTable tblHistorialDiagnosticosPaciente;
    private DefaultTableModel modelHistorialDiagnosticos;
    private JButton btnVerDetalleDiagnosticoSeleccionado;
    private JTextArea txtAreaDetalleDiagnosticoFull;

    // Pestaña 4: Administración (RF-DGN-007)
    private JPanel panelAdministracion;
    private JTextField txtDiagnosticoIdParaEliminar;
    private JButton btnEliminarDiagnosticoCompleto;

    // Servicios
    private ServicioDiagnostico servicioDiagnostico;
    // private ServicioCita servicioCita; // Necesitarías este para obtener info de cita
    // private ServicioMedicamento servicioMedicamento; // Para buscar medicamentos

    // Formateador de fecha y hora
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


    public VentanaDiagnostico() {
        // Inicializar servicios
        this.servicioDiagnostico = new ServicioDiagnostico();
        // this.servicioCita = new ServicioCita();
        // this.servicioMedicamento = new ServicioMedicamento();


        setTitle("Gestión de Diagnósticos");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(850, 750); // Un poco más grande
        setLocationRelativeTo(null);

        initComponents();
        layoutComponents();
        addEventListeners();

        // Estado inicial de componentes
        btnActualizarNotasDiagnostico.setEnabled(false);
        habilitarPanelPrescripcion(false); // Deshabilita todo en el panel de prescripción
        btnEliminarDiagnosticoCompleto.setEnabled(false);
    }

    private void habilitarPanelPrescripcion(boolean habilitar) {
        // Habilitar o deshabilitar todos los componentes dentro del panel de prescripción,
        // excepto el campo de ID de diagnóstico y el botón de cargar.
        txtDiagnosticoIdParaPrescripcion.setEnabled(true); // Siempre habilitado para ingresar ID
        btnCargarDiagnosticoParaPrescripcion.setEnabled(true); // Siempre habilitado para intentar cargar

        txtBuscarMedicamentoCatalogo.setEnabled(habilitar);
        btnBuscarMedicamento.setEnabled(habilitar);
        cmbMedicamentosEncontrados.setEnabled(habilitar);
        txtCantidadMedicamento.setEnabled(habilitar);
        txtAreaIndicacionesMedicamento.setEnabled(habilitar);
        btnAgregarMedicamentoAPrescripcion.setEnabled(habilitar);
        tblMedicamentosPrescritos.setEnabled(habilitar);
        btnQuitarMedicamentoSeleccionado.setEnabled(habilitar);
    }


    private void initComponents() {
        tabbedPane = new JTabbedPane();
        diagnosticoCargado = null;

        // --- Pestaña 1: Registrar / Modificar Diagnóstico ---
        panelRegistroModificacion = new JPanel();
        txtCitaIdParaNuevoDiagnostico = new JTextField(10);
        txtDiagnosticoIdParaCargar = new JTextField(10);
        txtAreaNotasDiagnostico = new JTextArea(10, 40);
        btnCargarDiagnosticoPorId = new JButton("Cargar Diagnóstico por ID");
        btnGuardarNuevoDiagnostico = new JButton("Guardar Nuevo Diagnóstico (RF-DGN-001)");
        btnActualizarNotasDiagnostico = new JButton("Actualizar Notas (RF-DGN-005)");
        lblPacienteInfoDiagnostico = new JLabel("Paciente: No cargado");
        lblMedicoInfoDiagnostico = new JLabel("Médico: No cargado");
        lblFechaDiagnostico = new JLabel("Fecha Diagnóstico: N/A");

        // --- Pestaña 2: Prescripción de Medicamentos ---
        panelPrescripcion = new JPanel();
        txtDiagnosticoIdParaPrescripcion = new JTextField(10);
        btnCargarDiagnosticoParaPrescripcion = new JButton("Cargar Diag. para Prescribir");
        txtBuscarMedicamentoCatalogo = new JTextField(20);
        btnBuscarMedicamento = new JButton("Buscar Med.");
        cmbMedicamentosEncontrados = new JComboBox<>();
        txtCantidadMedicamento = new JTextField(5);
        txtAreaIndicacionesMedicamento = new JTextArea(3, 30);
        btnAgregarMedicamentoAPrescripcion = new JButton("Agregar Medicamento (RF-DGN-002)");
        modelMedicamentosPrescritos = new DefaultTableModel(new String[]{"ID Presc.", "ID Med.", "Nombre", "Cantidad", "Indicaciones"}, 0){
            @Override
            public boolean isCellEditable(int row, int column) { return false; } // No editable
        };
        tblMedicamentosPrescritos = new JTable(modelMedicamentosPrescritos);
        btnQuitarMedicamentoSeleccionado = new JButton("Quitar Medicamento (RF-DGN-006)");

        // --- Pestaña 3: Historial y Consulta Detallada ---
        panelHistorialConsulta = new JPanel();
        txtPacienteIdParaHistorial = new JTextField(10);
        btnBuscarHistorialPaciente = new JButton("Buscar Historial (RF-DGN-003)");
        modelHistorialDiagnosticos = new DefaultTableModel(new String[]{"ID Diag.", "Fecha", "ID Médico", "Resumen Notas"}, 0){
            @Override
            public boolean isCellEditable(int row, int column) { return false; } // No editable
        };
        tblHistorialDiagnosticosPaciente = new JTable(modelHistorialDiagnosticos);
        btnVerDetalleDiagnosticoSeleccionado = new JButton("Ver Detalle (RF-DGN-004)");
        txtAreaDetalleDiagnosticoFull = new JTextArea(15, 60);
        txtAreaDetalleDiagnosticoFull.setEditable(false);

        // --- Pestaña 4: Administración ---
        panelAdministracion = new JPanel();
        txtDiagnosticoIdParaEliminar = new JTextField(10);
        btnEliminarDiagnosticoCompleto = new JButton("Eliminar Diagnóstico Completo (RF-DGN-007)");
    }

    private void layoutComponents() {
        // ... (Tu código de layout existente es bueno, no lo repito aquí por brevedad)
        // Asegúrate que el panel de prescripción se maneje bien con habilitarPanelPrescripcion
        // --- Layout Pestaña 1: Registrar / Modificar Diagnóstico ---
        panelRegistroModificacion.setLayout(new GridBagLayout());
        GridBagConstraints gbcReg = new GridBagConstraints();
        gbcReg.insets = new Insets(5, 5, 5, 5);
        gbcReg.anchor = GridBagConstraints.WEST;

        gbcReg.gridx = 0; gbcReg.gridy = 0; panelRegistroModificacion.add(new JLabel("ID Cita (para nuevo):"), gbcReg);
        gbcReg.gridx = 1; panelRegistroModificacion.add(txtCitaIdParaNuevoDiagnostico, gbcReg);
        gbcReg.gridx = 2; panelRegistroModificacion.add(btnGuardarNuevoDiagnostico, gbcReg);

        gbcReg.gridx = 0; gbcReg.gridy = 1; panelRegistroModificacion.add(new JLabel("ID Diagnóstico (cargar/modificar):"), gbcReg);
        gbcReg.gridx = 1; panelRegistroModificacion.add(txtDiagnosticoIdParaCargar, gbcReg);
        gbcReg.gridx = 2; panelRegistroModificacion.add(btnCargarDiagnosticoPorId, gbcReg);

        gbcReg.gridx = 0; gbcReg.gridy = 2; gbcReg.gridwidth=3; panelRegistroModificacion.add(lblPacienteInfoDiagnostico, gbcReg);
        gbcReg.gridx = 0; gbcReg.gridy = 3; panelRegistroModificacion.add(lblMedicoInfoDiagnostico, gbcReg);
        gbcReg.gridx = 0; gbcReg.gridy = 4; panelRegistroModificacion.add(lblFechaDiagnostico, gbcReg);
        gbcReg.gridwidth=1;


        gbcReg.gridx = 0; gbcReg.gridy = 5; panelRegistroModificacion.add(new JLabel("Notas del Diagnóstico:"), gbcReg);
        gbcReg.gridx = 0; gbcReg.gridy = 6; gbcReg.gridwidth = 3; gbcReg.fill = GridBagConstraints.HORIZONTAL;
        panelRegistroModificacion.add(new JScrollPane(txtAreaNotasDiagnostico), gbcReg);

        gbcReg.gridx = 0; gbcReg.gridy = 7; gbcReg.gridwidth = 1; gbcReg.fill = GridBagConstraints.NONE;
        panelRegistroModificacion.add(btnActualizarNotasDiagnostico, gbcReg);

        tabbedPane.addTab("Registrar/Modificar Diagnóstico", panelRegistroModificacion);


        // --- Layout Pestaña 2: Prescripción de Medicamentos ---
        panelPrescripcion.setLayout(new BorderLayout(10, 10));

        JPanel panelNortePrescripcion = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelNortePrescripcion.add(new JLabel("ID Diagnóstico:"));
        panelNortePrescripcion.add(txtDiagnosticoIdParaPrescripcion);
        panelNortePrescripcion.add(btnCargarDiagnosticoParaPrescripcion);
        panelPrescripcion.add(panelNortePrescripcion, BorderLayout.NORTH);

        JPanel panelCentroPrescripcion = new JPanel(new GridBagLayout());
        GridBagConstraints gbcPresc = new GridBagConstraints();
        gbcPresc.insets = new Insets(5,5,5,5);
        gbcPresc.anchor = GridBagConstraints.WEST;

        gbcPresc.gridx = 0; gbcPresc.gridy = 0; panelCentroPrescripcion.add(new JLabel("Buscar Medicamento:"), gbcPresc);
        gbcPresc.gridx = 1; panelCentroPrescripcion.add(txtBuscarMedicamentoCatalogo, gbcPresc);
        gbcPresc.gridx = 2; panelCentroPrescripcion.add(btnBuscarMedicamento, gbcPresc);
        gbcPresc.gridx = 0; gbcPresc.gridy = 1; panelCentroPrescripcion.add(new JLabel("Seleccionar:"), gbcPresc);
        gbcPresc.gridx = 1; gbcPresc.gridwidth = 2; gbcPresc.fill = GridBagConstraints.HORIZONTAL; cmbMedicamentosEncontrados.setPreferredSize(new Dimension(250, cmbMedicamentosEncontrados.getPreferredSize().height)); panelCentroPrescripcion.add(cmbMedicamentosEncontrados, gbcPresc);
        gbcPresc.gridwidth = 1; gbcPresc.fill = GridBagConstraints.NONE;

        gbcPresc.gridx = 0; gbcPresc.gridy = 2; panelCentroPrescripcion.add(new JLabel("Cantidad:"), gbcPresc);
        gbcPresc.gridx = 1; panelCentroPrescripcion.add(txtCantidadMedicamento, gbcPresc);
        gbcPresc.gridx = 0; gbcPresc.gridy = 3; panelCentroPrescripcion.add(new JLabel("Indicaciones:"), gbcPresc);
        gbcPresc.gridx = 1; gbcPresc.gridwidth = 2; gbcPresc.fill = GridBagConstraints.HORIZONTAL;
        panelCentroPrescripcion.add(new JScrollPane(txtAreaIndicacionesMedicamento), gbcPresc);
        gbcPresc.gridwidth = 1; gbcPresc.fill = GridBagConstraints.NONE;
        gbcPresc.gridx = 1; gbcPresc.gridy = 4; panelCentroPrescripcion.add(btnAgregarMedicamentoAPrescripcion, gbcPresc);

        panelPrescripcion.add(panelCentroPrescripcion, BorderLayout.CENTER);

        JPanel panelSurPrescripcion = new JPanel(new BorderLayout(5,5));
        panelSurPrescripcion.setBorder(BorderFactory.createTitledBorder("Medicamentos Prescritos"));
        panelSurPrescripcion.add(new JScrollPane(tblMedicamentosPrescritos), BorderLayout.CENTER);
        panelSurPrescripcion.add(btnQuitarMedicamentoSeleccionado, BorderLayout.SOUTH);
        panelPrescripcion.add(panelSurPrescripcion, BorderLayout.SOUTH);

        tabbedPane.addTab("Prescripción de Medicamentos", panelPrescripcion);


        // --- Layout Pestaña 3: Historial y Consulta Detallada ---
        panelHistorialConsulta.setLayout(new BorderLayout(10, 10));

        JPanel panelNorteHistorial = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelNorteHistorial.add(new JLabel("ID Paciente:"));
        panelNorteHistorial.add(txtPacienteIdParaHistorial);
        panelNorteHistorial.add(btnBuscarHistorialPaciente);
        panelHistorialConsulta.add(panelNorteHistorial, BorderLayout.NORTH);

        JPanel panelCentroHistorial = new JPanel(new BorderLayout(5,5));
        panelCentroHistorial.setBorder(BorderFactory.createTitledBorder("Historial de Diagnósticos del Paciente"));
        panelCentroHistorial.add(new JScrollPane(tblHistorialDiagnosticosPaciente), BorderLayout.CENTER);
        panelCentroHistorial.add(btnVerDetalleDiagnosticoSeleccionado, BorderLayout.SOUTH);
        panelHistorialConsulta.add(panelCentroHistorial, BorderLayout.CENTER);

        JPanel panelSurHistorial = new JPanel(new BorderLayout(5,5));
        panelSurHistorial.setBorder(BorderFactory.createTitledBorder("Detalle del Diagnóstico Seleccionado"));
        panelSurHistorial.add(new JScrollPane(txtAreaDetalleDiagnosticoFull), BorderLayout.CENTER);
        panelHistorialConsulta.add(panelSurHistorial, BorderLayout.SOUTH);

        tabbedPane.addTab("Historial y Consulta", panelHistorialConsulta);


        // --- Layout Pestaña 4: Administración ---
        panelAdministracion.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panelAdministracion.setBorder(BorderFactory.createTitledBorder("Acciones Administrativas"));
        panelAdministracion.add(new JLabel("ID Diagnóstico a Eliminar:"));
        panelAdministracion.add(txtDiagnosticoIdParaEliminar);
        panelAdministracion.add(btnEliminarDiagnosticoCompleto);

        tabbedPane.addTab("Administración", panelAdministracion);
        add(tabbedPane);
    }


    private void addEventListeners() {
        // --- Pestaña 1 Listeners ---
        btnGuardarNuevoDiagnostico.addActionListener(e -> guardarNuevoDiagnostico());
        btnCargarDiagnosticoPorId.addActionListener(e -> cargarDiagnosticoExistente(txtDiagnosticoIdParaCargar));
        btnActualizarNotasDiagnostico.addActionListener(e -> actualizarNotasDelDiagnostico());

        // --- Pestaña 2 Listeners ---
        btnCargarDiagnosticoParaPrescripcion.addActionListener(e -> cargarDiagnosticoExistente(txtDiagnosticoIdParaPrescripcion));
        btnBuscarMedicamento.addActionListener(e -> buscarMedicamentos());
        btnAgregarMedicamentoAPrescripcion.addActionListener(e -> agregarMedicamentoAPrescripcion());
        btnQuitarMedicamentoSeleccionado.addActionListener(e -> quitarMedicamentoSeleccionado());

        // --- Pestaña 3 Listeners ---
        btnBuscarHistorialPaciente.addActionListener(e -> buscarHistorialPorPaciente());
        btnVerDetalleDiagnosticoSeleccionado.addActionListener(e -> verDetalleDiagnosticoSeleccionado());

        // --- Pestaña 4 Listeners ---
        btnEliminarDiagnosticoCompleto.addActionListener(e -> eliminarDiagnosticoCompleto());

        txtDiagnosticoIdParaEliminar.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) { check(); }
            public void removeUpdate(DocumentEvent e) { check(); }
            public void insertUpdate(DocumentEvent e) { check(); }
            public void check() {
                btnEliminarDiagnosticoCompleto.setEnabled(!txtDiagnosticoIdParaEliminar.getText().trim().isEmpty());
            }
        });
    }

    // --- Métodos de Acción para los Listeners ---

    private void guardarNuevoDiagnostico() {
        try {
            String citaIdStr = txtCitaIdParaNuevoDiagnostico.getText().trim();
            if (citaIdStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Por favor, ingrese el ID de la Cita.", "Error de Validación", JOptionPane.ERROR_MESSAGE);
                return;
            }
            int idCita = Integer.parseInt(citaIdStr);
            String notas = txtAreaNotasDiagnostico.getText().trim();

            // Simulación: Necesitarías obtener idPaciente e idMedico de la Cita
            // Esto requeriría un ServicioCita
            // Cita cita = servicioCita.obtenerCitaPorId(idCita);
            // if (cita == null) { JOptionPane.showMessageDialog(this, "Cita no encontrada.", "Error", JOptionPane.ERROR_MESSAGE); return; }
            // int idPaciente = cita.getIdPaciente();
            // int idMedico = cita.getIdMedico();
            int idPacienteSimulado = 1; // Placeholder
            int idMedicoSimulado = 1;   // Placeholder

            Diagnostico nuevoDiagnostico = new Diagnostico();
            nuevoDiagnostico.setIdCita(idCita);
            nuevoDiagnostico.setIdPaciente(idPacienteSimulado); // Usar el ID real de la cita
            nuevoDiagnostico.setIdMedico(idMedicoSimulado);   // Usar el ID real de la cita
            nuevoDiagnostico.setNotasDiagnostico(notas);
            // La fecha del diagnóstico se establece en el servicio si es null

            int idGenerado = servicioDiagnostico.registrarDiagnosticoServicio(nuevoDiagnostico, idCita);
            JOptionPane.showMessageDialog(this, "Diagnóstico registrado con éxito. ID: " + idGenerado, "Éxito", JOptionPane.INFORMATION_MESSAGE);
            diagnosticoCargado = servicioDiagnostico.consultarDiagnosticoDetalladoServicio(idGenerado); // Recargar
            actualizarCamposDiagnosticoUI();
            txtCitaIdParaNuevoDiagnostico.setText("");
            txtDiagnosticoIdParaCargar.setText(String.valueOf(idGenerado));
            txtDiagnosticoIdParaPrescripcion.setText(String.valueOf(idGenerado)); // Para facilitar el flujo a prescripción
            btnActualizarNotasDiagnostico.setEnabled(true);
            habilitarPanelPrescripcion(true);
            cargarMedicamentosPrescritosUI();


        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "El ID de la Cita debe ser un número.", "Error de Formato", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException | SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al guardar diagnóstico: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void cargarDiagnosticoExistente(JTextField idField) {
        try {
            String diagIdStr = idField.getText().trim();
            if (diagIdStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Por favor, ingrese el ID del Diagnóstico a cargar.", "Error de Validación", JOptionPane.ERROR_MESSAGE);
                return;
            }
            int idDiagnostico = Integer.parseInt(diagIdStr);
            diagnosticoCargado = servicioDiagnostico.consultarDiagnosticoDetalladoServicio(idDiagnostico);

            if (diagnosticoCargado != null) {
                actualizarCamposDiagnosticoUI();
                cargarMedicamentosPrescritosUI();
                btnActualizarNotasDiagnostico.setEnabled(true);
                habilitarPanelPrescripcion(true); // Habilita componentes en la pestaña de prescripción
                // Si la carga fue desde la pestaña 1, actualiza el campo de ID en la pestaña 2
                if (idField == txtDiagnosticoIdParaCargar) {
                    txtDiagnosticoIdParaPrescripcion.setText(diagIdStr);
                }
                // Si la carga fue desde la pestaña 2, actualiza el campo de ID en la pestaña 1
                else if (idField == txtDiagnosticoIdParaPrescripcion) {
                    txtDiagnosticoIdParaCargar.setText(diagIdStr);
                }


            } else {
                JOptionPane.showMessageDialog(this, "Diagnóstico con ID " + idDiagnostico + " no encontrado.", "No Encontrado", JOptionPane.WARNING_MESSAGE);
                limpiarCamposDiagnosticoUI();
                diagnosticoCargado = null;
                btnActualizarNotasDiagnostico.setEnabled(false);
                habilitarPanelPrescripcion(false);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "El ID del Diagnóstico debe ser un número.", "Error de Formato", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al cargar diagnóstico: " + ex.getMessage(), "Error de Base de Datos", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void actualizarCamposDiagnosticoUI() {
        if (diagnosticoCargado != null) {
            txtAreaNotasDiagnostico.setText(diagnosticoCargado.getNotasDiagnostico());
            // Para paciente y médico, necesitarías obtener sus nombres usando sus IDs y sus respectivos servicios
            lblPacienteInfoDiagnostico.setText("Paciente ID: " + diagnosticoCargado.getIdPaciente() + " (Nombre no disponible)");
            lblMedicoInfoDiagnostico.setText("Médico ID: " + diagnosticoCargado.getIdMedico() + " (Nombre no disponible)");
            lblFechaDiagnostico.setText("Fecha Diagnóstico: " + (diagnosticoCargado.getFechaDiagnostico() != null ? diagnosticoCargado.getFechaDiagnostico().format(dateTimeFormatter) : "N/A"));
        }
    }
    private void limpiarCamposDiagnosticoUI() {
        txtAreaNotasDiagnostico.setText("");
        lblPacienteInfoDiagnostico.setText("Paciente: No cargado");
        lblMedicoInfoDiagnostico.setText("Médico: No cargado");
        lblFechaDiagnostico.setText("Fecha Diagnóstico: N/A");
        modelMedicamentosPrescritos.setRowCount(0);
    }


    private void actualizarNotasDelDiagnostico() {
        if (diagnosticoCargado == null) {
            JOptionPane.showMessageDialog(this, "No hay un diagnóstico cargado para modificar.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            String nuevasNotas = txtAreaNotasDiagnostico.getText().trim();
            boolean exito = servicioDiagnostico.modificarNotasDiagnosticoServicio(diagnosticoCargado.getId(), nuevasNotas);
            if (exito) {
                JOptionPane.showMessageDialog(this, "Notas del diagnóstico actualizadas con éxito.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                diagnosticoCargado.setNotasDiagnostico(nuevasNotas); // Actualizar el objeto en memoria
                diagnosticoCargado.setFechaModificacion(LocalDateTime.now()); // Simular actualización de fecha
                actualizarCamposDiagnosticoUI(); // Refrescar la UI
            } else {
                JOptionPane.showMessageDialog(this, "No se pudieron actualizar las notas.", "Fallo", JOptionPane.WARNING_MESSAGE);
            }
        } catch (IllegalArgumentException | SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al actualizar notas: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void cargarMedicamentosPrescritosUI() {
        modelMedicamentosPrescritos.setRowCount(0); // Limpiar tabla
        if (diagnosticoCargado != null && diagnosticoCargado.getMedicamentosPrescritos() != null) {
            for (DiagnosticoMedicamento dm : diagnosticoCargado.getMedicamentosPrescritos()) {
                modelMedicamentosPrescritos.addRow(new Object[]{
                        dm.getId(), // ID de la prescripción (diagnostico_medicamentos.id)
                        dm.getIdMedicamento(),
                        dm.getNombreMedicamento(), // Asume que este campo está en DiagnosticoMedicamento
                        dm.getCantidad(),
                        dm.getIndicaciones()
                });
            }
        }
    }


    private void buscarMedicamentos() {
        // Esto requeriría un ServicioMedicamento
        String criterio = txtBuscarMedicamentoCatalogo.getText().trim();
        // List<Medicamento> medicamentos = servicioMedicamento.buscarMedicamentosActivos(criterio);
        // cmbMedicamentosEncontrados.removeAllItems();
        // if (medicamentos != null && !medicamentos.isEmpty()) {
        //     for (Medicamento med : medicamentos) {
        //         cmbMedicamentosEncontrados.addItem(new MedicamentoComboBoxItem(med.getId(), med.getNombre() + " (" + med.getDescripcionPresentacion() + ")"));
        //     }
        // } else {
        //     JOptionPane.showMessageDialog(this, "No se encontraron medicamentos con ese criterio.", "Búsqueda", JOptionPane.INFORMATION_MESSAGE);
        // }
        // Simulación:
        cmbMedicamentosEncontrados.removeAllItems();
        cmbMedicamentosEncontrados.addItem(new MedicamentoComboBoxItem(1, "Paracetamol 500mg"));
        cmbMedicamentosEncontrados.addItem(new MedicamentoComboBoxItem(2, "Ibuprofeno 200mg"));
        cmbMedicamentosEncontrados.addItem(new MedicamentoComboBoxItem(3, "Amoxicilina 250mg/5ml Susp."));
        if(cmbMedicamentosEncontrados.getItemCount() == 0) {
            JOptionPane.showMessageDialog(this, "No se encontraron medicamentos con ese criterio.", "Búsqueda", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void agregarMedicamentoAPrescripcion() {
        if (diagnosticoCargado == null) {
            JOptionPane.showMessageDialog(this, "Cargue un diagnóstico primero.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        MedicamentoComboBoxItem itemSeleccionado = (MedicamentoComboBoxItem) cmbMedicamentosEncontrados.getSelectedItem();
        if (itemSeleccionado == null) {
            JOptionPane.showMessageDialog(this, "Seleccione un medicamento de la lista.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            int cantidad = Integer.parseInt(txtCantidadMedicamento.getText().trim());
            String indicaciones = txtAreaIndicacionesMedicamento.getText().trim();
            if (indicaciones.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Las indicaciones son obligatorias.", "Error de Validación", JOptionPane.ERROR_MESSAGE);
                return;
            }


            DiagnosticoMedicamento nuevaPrescripcion = new DiagnosticoMedicamento();
            nuevaPrescripcion.setIdDiagnostico(diagnosticoCargado.getId());
            nuevaPrescripcion.setIdMedicamento(itemSeleccionado.getId());
            nuevaPrescripcion.setCantidad(cantidad);
            nuevaPrescripcion.setIndicaciones(indicaciones);

            boolean exito = servicioDiagnostico.agregarMedicamentoADiagnosticoServicio(nuevaPrescripcion);
            if (exito) {
                JOptionPane.showMessageDialog(this, "Medicamento agregado a la prescripción.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                // Volver a cargar el diagnóstico para refrescar la lista de medicamentos
                diagnosticoCargado = servicioDiagnostico.consultarDiagnosticoDetalladoServicio(diagnosticoCargado.getId());
                cargarMedicamentosPrescritosUI();
                txtCantidadMedicamento.setText("");
                txtAreaIndicacionesMedicamento.setText("");
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo agregar el medicamento (verifique si ya existe).", "Fallo", JOptionPane.WARNING_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "La cantidad debe ser un número.", "Error de Formato", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException | SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al agregar medicamento: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void quitarMedicamentoSeleccionado() {
        int filaSeleccionada = tblMedicamentosPrescritos.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un medicamento de la tabla para quitar.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (diagnosticoCargado == null) {
            JOptionPane.showMessageDialog(this, "No hay diagnóstico cargado.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // El ID de la prescripción está en la primera columna (índice 0) de la tabla
        int idDiagnosticoMedicamento = (Integer) modelMedicamentosPrescritos.getValueAt(filaSeleccionada, 0);

        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Está seguro de que desea quitar este medicamento de la prescripción?",
                "Confirmar Eliminación", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                boolean exito = servicioDiagnostico.eliminarMedicamentoDeDiagnosticoServicio(idDiagnosticoMedicamento);
                if (exito) {
                    JOptionPane.showMessageDialog(this, "Medicamento quitado de la prescripción.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    diagnosticoCargado = servicioDiagnostico.consultarDiagnosticoDetalladoServicio(diagnosticoCargado.getId());
                    cargarMedicamentosPrescritosUI();
                } else {
                    JOptionPane.showMessageDialog(this, "No se pudo quitar el medicamento.", "Fallo", JOptionPane.WARNING_MESSAGE);
                }
            } catch (IllegalStateException | SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error al quitar medicamento: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }

    private void buscarHistorialPorPaciente() {
        try {
            String pacienteIdStr = txtPacienteIdParaHistorial.getText().trim();
            if (pacienteIdStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Ingrese el ID del Paciente.", "Error de Validación", JOptionPane.ERROR_MESSAGE);
                return;
            }
            int idPaciente = Integer.parseInt(pacienteIdStr);
            List<Diagnostico> historial = servicioDiagnostico.consultarHistorialDiagnosticosPacienteServicio(idPaciente);

            modelHistorialDiagnosticos.setRowCount(0); // Limpiar tabla
            if (historial != null && !historial.isEmpty()) {
                for (Diagnostico diag : historial) {
                    String resumenNotas = diag.getNotasDiagnostico();
                    if (resumenNotas != null && resumenNotas.length() > 50) {
                        resumenNotas = resumenNotas.substring(0, 50) + "...";
                    }
                    modelHistorialDiagnosticos.addRow(new Object[]{
                            diag.getId(),
                            diag.getFechaDiagnostico() != null ? diag.getFechaDiagnostico().format(dateTimeFormatter) : "N/A",
                            diag.getIdMedico(), // Podrías obtener el nombre del médico con otro servicio
                            resumenNotas
                    });
                }
            } else {
                JOptionPane.showMessageDialog(this, "No se encontraron diagnósticos para el paciente ID: " + idPaciente, "Sin Resultados", JOptionPane.INFORMATION_MESSAGE);
            }
            txtAreaDetalleDiagnosticoFull.setText(""); // Limpiar detalle
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "El ID del Paciente debe ser un número.", "Error de Formato", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al buscar historial: " + ex.getMessage(), "Error de Base de Datos", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void verDetalleDiagnosticoSeleccionado() {
        int filaSeleccionada = tblHistorialDiagnosticosPaciente.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un diagnóstico del historial para ver su detalle.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int idDiagnostico = (Integer) modelHistorialDiagnosticos.getValueAt(filaSeleccionada, 0);

        try {
            Diagnostico diagDetallado = servicioDiagnostico.consultarDiagnosticoDetalladoServicio(idDiagnostico);
            if (diagDetallado != null) {
                StringBuilder detalle = new StringBuilder();
                detalle.append("--- DETALLE DIAGNÓSTICO ID: ").append(diagDetallado.getId()).append(" ---\n");
                // Necesitarías servicios para obtener nombres de Paciente y Médico
                detalle.append("Paciente ID: ").append(diagDetallado.getIdPaciente()).append(" (Nombre no disponible)\n");
                detalle.append("Médico ID: ").append(diagDetallado.getIdMedico()).append(" (Nombre no disponible)\n");
                detalle.append("Fecha Diagnóstico: ").append(diagDetallado.getFechaDiagnostico() != null ? diagDetallado.getFechaDiagnostico().format(dateTimeFormatter) : "N/A").append("\n\n");
                detalle.append("Notas:\n").append(diagDetallado.getNotasDiagnostico()).append("\n\n");

                if (diagDetallado.getMedicamentosPrescritos() != null && !diagDetallado.getMedicamentosPrescritos().isEmpty()) {
                    detalle.append("Medicamentos Prescritos:\n");
                    for (DiagnosticoMedicamento dm : diagDetallado.getMedicamentosPrescritos()) {
                        detalle.append("- ").append(dm.getNombreMedicamento() != null ? dm.getNombreMedicamento() : "Med ID: " + dm.getIdMedicamento());
                        detalle.append(" (Presentación: ").append(dm.getDescripcionPresentacion() != null ? dm.getDescripcionPresentacion() : "N/A").append(")");
                        detalle.append(" | Cant: ").append(dm.getCantidad());
                        detalle.append(" | Indicaciones: ").append(dm.getIndicaciones());
                        if (dm.getPrecioMedicamento() != null && dm.getPrecioMedicamento().compareTo(BigDecimal.ZERO) > 0) {
                            detalle.append(" | Precio Unit.: ").append(dm.getPrecioMedicamento());
                        }
                        detalle.append("\n");
                    }
                } else {
                    detalle.append("No hay medicamentos prescritos para este diagnóstico.\n");
                }
                txtAreaDetalleDiagnosticoFull.setText(detalle.toString());
                txtAreaDetalleDiagnosticoFull.setCaretPosition(0); // Scroll al inicio
            } else {
                txtAreaDetalleDiagnosticoFull.setText("No se pudo cargar el detalle del diagnóstico ID: " + idDiagnostico);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al ver detalle del diagnóstico: " + ex.getMessage(), "Error de Base de Datos", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
            txtAreaDetalleDiagnosticoFull.setText("Error al cargar detalle: " + ex.getMessage());
        }
    }


    private void eliminarDiagnosticoCompleto() {
        String diagIdStr = txtDiagnosticoIdParaEliminar.getText().trim();
        if (diagIdStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese el ID del Diagnóstico a eliminar.", "Error de Validación", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            int idDiagnostico = Integer.parseInt(diagIdStr);
            // Validar si el usuario tiene rol 'Administrador' (esta lógica estaría fuera, o se pasaría el rol al servicio)
            // Por ahora, asumimos que si llega aquí, tiene permiso o el servicio lo maneja (aunque el RF dice que la UI pide confirmación y el servicio valida rol)

            int confirm = JOptionPane.showConfirmDialog(this,
                    "¿Está SEGURO de que desea eliminar este diagnóstico (ID: " + idDiagnostico + ")?\n" +
                            "Esta acción es IRREVERSIBLE y borrará todas sus prescripciones asociadas.",
                    "Confirmar Eliminación", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

            if (confirm == JOptionPane.YES_OPTION) {
                boolean exito = servicioDiagnostico.eliminarDiagnosticoCompletoServicio(idDiagnostico);
                if (exito) {
                    JOptionPane.showMessageDialog(this, "Diagnóstico ID: " + idDiagnostico + " eliminado con éxito.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    txtDiagnosticoIdParaEliminar.setText("");
                    // Si el diagnóstico eliminado estaba cargado en otras pestañas, limpiar esas vistas
                    if (diagnosticoCargado != null && diagnosticoCargado.getId() == idDiagnostico) {
                        limpiarCamposDiagnosticoUI();
                        diagnosticoCargado = null;
                        btnActualizarNotasDiagnostico.setEnabled(false);
                        habilitarPanelPrescripcion(false);
                    }
                    // Podrías también querer refrescar la tabla de historial si estaba visible
                } else {
                    JOptionPane.showMessageDialog(this, "No se pudo eliminar el diagnóstico ID: " + idDiagnostico + ". Puede que ya no exista.", "Fallo", JOptionPane.WARNING_MESSAGE);
                }
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "El ID del Diagnóstico debe ser un número.", "Error de Formato", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al eliminar diagnóstico: " + ex.getMessage() +
                            (ex.getSQLState().equals("23000") ? "\nPosiblemente tiene referencias que impiden su borrado (ej. facturas)." : ""),
                    "Error de Base de Datos", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }


    // Clase interna para el ComboBox de Medicamentos
    private static class MedicamentoComboBoxItem {
        private final int id;
        private final String nombreMostrado;

        public MedicamentoComboBoxItem(int id, String nombreMostrado) {
            this.id = id;
            this.nombreMostrado = nombreMostrado;
        }

        public int getId() {
            return id;
        }

        @Override
        public String toString() {
            return nombreMostrado; // Esto es lo que se muestra en el ComboBox
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new VentanaDiagnostico().setVisible(true);
        });
    }
}