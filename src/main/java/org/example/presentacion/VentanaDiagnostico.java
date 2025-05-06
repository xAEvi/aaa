// src/main/java/org/example/presentacion/VentanaDiagnostico.java
package org.example.presentacion;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class VentanaDiagnostico extends JFrame {

    // Pestañas
    private JTabbedPane tabbedPane;

    // Pestaña 1: Registrar / Modificar Diagnóstico (RF-DGN-001, RF-DGN-005)
    private JPanel panelRegistroModificacion;
    private JTextField txtCitaIdParaNuevoDiagnostico; // Para RF-DGN-001
    private JTextField txtDiagnosticoIdParaCargar;    // Para RF-DGN-005 (y para cargar antes de RF-DGN-002, RF-DGN-006)
    private JTextArea txtAreaNotasDiagnostico;
    private JButton btnCargarDiagnosticoPorId; // Para cargar datos para modificar notas o añadir medicamentos
    private JButton btnGuardarNuevoDiagnostico; // RF-DGN-001
    private JButton btnActualizarNotasDiagnostico; // RF-DGN-005
    private JLabel lblPacienteInfoDiagnostico; // Para mostrar info del paciente de la cita/diagnóstico cargado
    private JLabel lblMedicoInfoDiagnostico;   // Para mostrar info del médico de la cita/diagnóstico cargado
    private JLabel lblFechaDiagnostico;        // Para mostrar fecha del diagnóstico cargado


    // Pestaña 2: Prescripción de Medicamentos (RF-DGN-002, RF-DGN-006)
    private JPanel panelPrescripcion;
    private JTextField txtDiagnosticoIdParaPrescripcion; // ID del diagnóstico al que se agregarán medicamentos
    private JButton btnCargarDiagnosticoParaPrescripcion; // Para cargar el diagnóstico y su lista de medicamentos
    private JTextField txtBuscarMedicamentoCatalogo;
    private JButton btnBuscarMedicamento;
    private JComboBox<String> cmbMedicamentosEncontrados; // Simulación, podría ser una JList o JTable de resultados
    private JTextField txtCantidadMedicamento;
    private JTextArea txtAreaIndicacionesMedicamento;
    private JButton btnAgregarMedicamentoAPrescripcion; // RF-DGN-002
    private JTable tblMedicamentosPrescritos;
    private DefaultTableModel modelMedicamentosPrescritos;
    private JButton btnQuitarMedicamentoSeleccionado; // RF-DGN-006

    // Pestaña 3: Historial y Consulta Detallada (RF-DGN-003, RF-DGN-004)
    private JPanel panelHistorialConsulta;
    private JTextField txtPacienteIdParaHistorial;
    private JButton btnBuscarHistorialPaciente; // RF-DGN-003
    private JTable tblHistorialDiagnosticosPaciente;
    private DefaultTableModel modelHistorialDiagnosticos;
    private JButton btnVerDetalleDiagnosticoSeleccionado; // RF-DGN-004
    // Área para mostrar detalle del diagnóstico (RF-DGN-004)
    private JTextArea txtAreaDetalleDiagnosticoFull; // Muestra notas, paciente, médico, y medicamentos.

    // Pestaña 4: Administración (RF-DGN-007)
    private JPanel panelAdministracion;
    private JTextField txtDiagnosticoIdParaEliminar;
    private JButton btnEliminarDiagnosticoCompleto; // RF-DGN-007

    // TODO: Instancias de los servicios (DiagnosticoServicio, CitaServicio, PacienteServicio, MedicamentoServicio)
    // private DiagnosticoServicio diagnosticoServicio;
    // private CitaServicio citaServicio;
    // etc.

    public VentanaDiagnostico() {
        // TODO: Inicializar servicios
        // this.diagnosticoServicio = new DiagnosticoServicio();

        setTitle("Gestión de Diagnósticos");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // O HIDE_ON_CLOSE si prefieres
        setSize(800, 700);
        setLocationRelativeTo(null); // Centrar en pantalla

        initComponents();
        layoutComponents();
        addEventListeners();

        // Inicialmente algunos campos/botones podrían estar deshabilitados
        btnActualizarNotasDiagnostico.setEnabled(false);
        panelPrescripcion.setEnabled(false); // Habilitar componentes internos cuando se cargue un diagnóstico
        for(Component comp : panelPrescripcion.getComponents()){
            comp.setEnabled(false);
        }
        btnEliminarDiagnosticoCompleto.setEnabled(false); // Podría requerir confirmación extra
    }

    private void initComponents() {
        tabbedPane = new JTabbedPane();

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
        cmbMedicamentosEncontrados = new JComboBox<>(); // Llenar con resultados de búsqueda
        txtCantidadMedicamento = new JTextField(5);
        txtAreaIndicacionesMedicamento = new JTextArea(3, 30);
        btnAgregarMedicamentoAPrescripcion = new JButton("Agregar Medicamento (RF-DGN-002)");
        modelMedicamentosPrescritos = new DefaultTableModel(new String[]{"ID Med.", "Nombre", "Cantidad", "Indicaciones"}, 0);
        tblMedicamentosPrescritos = new JTable(modelMedicamentosPrescritos);
        btnQuitarMedicamentoSeleccionado = new JButton("Quitar Medicamento (RF-DGN-006)");

        // --- Pestaña 3: Historial y Consulta Detallada ---
        panelHistorialConsulta = new JPanel();
        txtPacienteIdParaHistorial = new JTextField(10);
        btnBuscarHistorialPaciente = new JButton("Buscar Historial (RF-DGN-003)");
        modelHistorialDiagnosticos = new DefaultTableModel(new String[]{"ID Diag.", "Fecha", "Médico", "Resumen Notas"}, 0);
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
        // --- Layout Pestaña 1: Registrar / Modificar Diagnóstico ---
        panelRegistroModificacion.setLayout(new GridBagLayout());
        GridBagConstraints gbcReg = new GridBagConstraints();
        gbcReg.insets = new Insets(5, 5, 5, 5);
        gbcReg.anchor = GridBagConstraints.WEST;

        gbcReg.gridx = 0; gbcReg.gridy = 0; panelRegistroModificacion.add(new JLabel("ID Cita (para nuevo):"), gbcReg);
        gbcReg.gridx = 1; panelRegistroModificacion.add(txtCitaIdParaNuevoDiagnostico, gbcReg);
        gbcReg.gridx = 2; panelRegistroModificacion.add(btnGuardarNuevoDiagnostico, gbcReg);

        gbcReg.gridx = 0; gbcReg.gridy = 1; panelRegistroModificacion.add(new JLabel("ID Diagnóstico (para cargar/modificar):"), gbcReg);
        gbcReg.gridx = 1; panelRegistroModificacion.add(txtDiagnosticoIdParaCargar, gbcReg);
        gbcReg.gridx = 2; panelRegistroModificacion.add(btnCargarDiagnosticoPorId, gbcReg);

        gbcReg.gridx = 0; gbcReg.gridy = 2; panelRegistroModificacion.add(lblPacienteInfoDiagnostico, gbcReg);
        gbcReg.gridx = 0; gbcReg.gridy = 3; panelRegistroModificacion.add(lblMedicoInfoDiagnostico, gbcReg);
        gbcReg.gridx = 0; gbcReg.gridy = 4; panelRegistroModificacion.add(lblFechaDiagnostico, gbcReg);


        gbcReg.gridx = 0; gbcReg.gridy = 5; gbcReg.gridwidth = 1; panelRegistroModificacion.add(new JLabel("Notas del Diagnóstico:"), gbcReg);
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
        gbcPresc.gridx = 1; gbcPresc.gridwidth = 2; panelCentroPrescripcion.add(cmbMedicamentosEncontrados, gbcPresc);
        gbcPresc.gridwidth = 1;

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

        // Añadir el JTabbedPane al JFrame
        add(tabbedPane);
    }

    private void addEventListeners() {
        // --- Pestaña 1 Listeners ---
        btnGuardarNuevoDiagnostico.addActionListener(e -> {
            // TODO: RF-DGN-001 - Implementar lógica para guardar nuevo diagnóstico
            // 1. Validar que txtCitaIdParaNuevoDiagnostico tenga un valor numérico.
            // 2. Validar que txtAreaNotasDiagnostico no esté vacío.
            // 3. Llamar a diagnosticoServicio.registrarNuevoDiagnostico(idCita, notas, /* idUsuarioLogueado */);
            //    - El servicio obtendrá idPaciente, idMedico de la cita.
            //    - El servicio llamará al DAO para insertar diagnóstico y actualizar estado de cita.
            // 4. Mostrar mensaje de éxito/error.
            // 5. Limpiar campos o cargar el nuevo diagnóstico.
            JOptionPane.showMessageDialog(this, "TODO: Implementar RF-DGN-001");
        });

        btnCargarDiagnosticoPorId.addActionListener(e -> {
            // TODO: Implementar lógica para cargar un diagnóstico existente
            // 1. Validar que txtDiagnosticoIdParaCargar tenga un valor numérico.
            // 2. Llamar a diagnosticoServicio.obtenerDiagnosticoPorId(idDiagnostico);
            // 3. Poblar txtAreaNotasDiagnostico, lblPacienteInfoDiagnostico, lblMedicoInfoDiagnostico, lblFechaDiagnostico.
            // 4. Habilitar btnActualizarNotasDiagnostico.
            // 5. Copiar el ID a txtDiagnosticoIdParaPrescripcion y habilitar esa pestaña/componentes.
            // 6. Poblar tblMedicamentosPrescritos si el diagnóstico cargado tiene medicamentos.
            JOptionPane.showMessageDialog(this, "TODO: Cargar diagnóstico por ID para ver/modificar notas y/o gestionar medicamentos.");
            lblPacienteInfoDiagnostico.setText("Paciente: Juan Pérez (ID: 1)"); // Ejemplo
            lblMedicoInfoDiagnostico.setText("Médico: Dra. Ana Smith (ID: 1)"); // Ejemplo
            lblFechaDiagnostico.setText("Fecha Diagnóstico: 2024-05-10 10:30"); // Ejemplo
            txtAreaNotasDiagnostico.setText("El paciente presenta síntomas de..."); // Ejemplo
            btnActualizarNotasDiagnostico.setEnabled(true);
            // También se podría rellenar txtDiagnosticoIdParaPrescripcion y activar la carga allí
            txtDiagnosticoIdParaPrescripcion.setText(txtDiagnosticoIdParaCargar.getText());

        });

        btnActualizarNotasDiagnostico.addActionListener(e -> {
            // TODO: RF-DGN-005 - Implementar lógica para actualizar notas
            // 1. Obtener idDiagnostico (de txtDiagnosticoIdParaCargar o una variable interna).
            // 2. Obtener nuevas notas de txtAreaNotasDiagnostico.
            // 3. Llamar a diagnosticoServicio.modificarNotasDiagnostico(idDiagnostico, nuevasNotas, /* idUsuarioLogueado */);
            // 4. Mostrar mensaje de éxito/error.
            JOptionPane.showMessageDialog(this, "TODO: Implementar RF-DGN-005");
        });

        // --- Pestaña 2 Listeners ---
        btnCargarDiagnosticoParaPrescripcion.addActionListener(e -> {
            // TODO: Cargar diagnóstico específico para gestionar sus medicamentos
            // 1. Validar txtDiagnosticoIdParaPrescripcion.
            // 2. Llamar a diagnosticoServicio.obtenerDiagnosticoConMedicamentos(idDiagnostico);
            // 3. Poblar tblMedicamentosPrescritos.
            // 4. Habilitar el resto de componentes de esta pestaña.
            JOptionPane.showMessageDialog(this, "TODO: Cargar diagnóstico para prescribir.");
            // Simular carga
            modelMedicamentosPrescritos.setRowCount(0); // Limpiar tabla
            modelMedicamentosPrescritos.addRow(new Object[]{101, "Paracetamol 500mg", 20, "Cada 8 horas por 3 días"});
            for(Component comp : panelPrescripcion.getComponents()){
                if (comp instanceof JPanel) { // Habilitar componentes dentro de subpaneles
                    for(Component subComp : ((JPanel) comp).getComponents()){
                        subComp.setEnabled(true);
                    }
                } else {
                    comp.setEnabled(true);
                }
            }
            // Asegurar que el panel general esté habilitado
            panelPrescripcion.setEnabled(true);
            // El botón de cargar el diagnóstico en sí mismo debe seguir habilitado
            btnCargarDiagnosticoParaPrescripcion.setEnabled(true);
            txtDiagnosticoIdParaPrescripcion.setEnabled(true);
        });


        btnBuscarMedicamento.addActionListener(e -> {
            // TODO: Implementar búsqueda de medicamentos en el catálogo
            // 1. Obtener texto de txtBuscarMedicamentoCatalogo.
            // 2. Llamar a medicamentoServicio.buscarMedicamentosActivos(criterio);
            // 3. Poblar cmbMedicamentosEncontrados.
            JOptionPane.showMessageDialog(this, "TODO: Buscar medicamento en catálogo.");
            cmbMedicamentosEncontrados.removeAllItems();
            cmbMedicamentosEncontrados.addItem("Amoxicilina 500mg (ID: 1)"); // Ejemplo
            cmbMedicamentosEncontrados.addItem("Ibuprofeno 200mg (ID: 2)"); // Ejemplo
        });

        btnAgregarMedicamentoAPrescripcion.addActionListener(e -> {
            // TODO: RF-DGN-002 - Implementar lógica para agregar medicamento
            // 1. Obtener idDiagnostico (de txtDiagnosticoIdParaPrescripcion).
            // 2. Obtener idMedicamento (del cmbMedicamentosEncontrados o un campo de ID).
            // 3. Obtener cantidad de txtCantidadMedicamento.
            // 4. Obtener indicaciones de txtAreaIndicacionesMedicamento.
            // 5. Llamar a diagnosticoServicio.agregarMedicamentoADiagnostico(idDiagnostico, idMedicamento, cantidad, indicaciones, /* idUsuarioLogueado */);
            // 6. Actualizar tblMedicamentosPrescritos.
            // 7. Mostrar mensaje de éxito/error.
            JOptionPane.showMessageDialog(this, "TODO: Implementar RF-DGN-002");
        });

        btnQuitarMedicamentoSeleccionado.addActionListener(e -> {
            // TODO: RF-DGN-006 - Implementar lógica para quitar medicamento
            // 1. Obtener idDiagnostico.
            // 2. Obtener idDiagnosticoMedicamento o idMedicamento de la fila seleccionada en tblMedicamentosPrescritos.
            // 3. Pedir confirmación.
            // 4. Llamar a diagnosticoServicio.eliminarMedicamentoDeDiagnostico(idDiagnosticoMedicamento, /* idUsuarioLogueado */);
            // 5. Actualizar tblMedicamentosPrescritos.
            // 6. Mostrar mensaje de éxito/error.
            JOptionPane.showMessageDialog(this, "TODO: Implementar RF-DGN-006");
        });

        // --- Pestaña 3 Listeners ---
        btnBuscarHistorialPaciente.addActionListener(e -> {
            // TODO: RF-DGN-003 - Implementar lógica para buscar historial
            // 1. Obtener idPaciente de txtPacienteIdParaHistorial.
            // 2. Llamar a diagnosticoServicio.consultarHistorialDiagnosticosPorPaciente(idPaciente);
            // 3. Poblar tblHistorialDiagnosticosPaciente.
            // 4. Mostrar mensaje si no hay resultados.
            JOptionPane.showMessageDialog(this, "TODO: Implementar RF-DGN-003");
            modelHistorialDiagnosticos.setRowCount(0); // Limpiar
            modelHistorialDiagnosticos.addRow(new Object[]{55, "2024-05-01", "Dra. Smith", "Gripe común..."});
            modelHistorialDiagnosticos.addRow(new Object[]{23, "2023-10-15", "Dr. House", "Revisión general..."});
        });

        btnVerDetalleDiagnosticoSeleccionado.addActionListener(e -> {
            // TODO: RF-DGN-004 - Implementar lógica para ver detalle
            // 1. Obtener idDiagnostico de la fila seleccionada en tblHistorialDiagnosticosPaciente.
            // 2. Llamar a diagnosticoServicio.obtenerDetalleCompletoDiagnostico(idDiagnostico);
            //    (Este método debería devolver un objeto Diagnostico con Paciente, Medico, y lista de DiagnosticoMedicamento populados)
            // 3. Poblar txtAreaDetalleDiagnosticoFull con toda la información.
            JOptionPane.showMessageDialog(this, "TODO: Implementar RF-DGN-004");
            txtAreaDetalleDiagnosticoFull.setText(
                    "--- DETALLE DIAGNÓSTICO ID: 55 ---\n" +
                            "Paciente: Juan Pérez (DNI: 12345678A)\n" +
                            "Médico: Dra. Ana Smith (Especialidad: General)\n" +
                            "Fecha Diagnóstico: 2024-05-01 11:00\n\n" +
                            "Notas:\n" +
                            "Gripe común. Se recomienda reposo e hidratación.\n\n" +
                            "Medicamentos Prescritos:\n" +
                            "- Paracetamol 500mg (ID: 101) | Cant: 1 caja (20 comps) | Indicaciones: 1 comp cada 8h si fiebre.\n" +
                            "- Ibuprofeno 200mg (ID: 105) | Cant: 1 caja (10 comps) | Indicaciones: 1 comp cada 12h para dolor."
            );
        });

        // --- Pestaña 4 Listeners ---
        btnEliminarDiagnosticoCompleto.addActionListener(e -> {
            // TODO: RF-DGN-007 - Implementar lógica para eliminar diagnóstico
            // 1. Obtener idDiagnostico de txtDiagnosticoIdParaEliminar.
            // 2. Pedir confirmación MUY CLARA al usuario.
            // 3. Llamar a diagnosticoServicio.eliminarDiagnosticoCompleto(idDiagnostico, /* idUsuarioLogueado */);
            //    (El servicio verificará el rol de 'Administrador').
            // 4. Mostrar mensaje de éxito/error.
            int confirm = JOptionPane.showConfirmDialog(this,
                    "¿Está SEGURO de que desea eliminar este diagnóstico (ID: " + txtDiagnosticoIdParaEliminar.getText() + ")?\n" +
                            "Esta acción es IRREVERSIBLE y borrará todas sus prescripciones asociadas.",
                    "Confirmar Eliminación", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (confirm == JOptionPane.YES_OPTION) {
                JOptionPane.showMessageDialog(this, "TODO: Implementar RF-DGN-007. Diagnóstico ID: " + txtDiagnosticoIdParaEliminar.getText() + " supuestamente eliminado.");
            }
        });

        // Habilitar/Deshabilitar componentes basados en el estado
        // Por ejemplo, el botón de eliminar diagnóstico solo debería estar activo si se ingresa un ID.
        txtDiagnosticoIdParaEliminar.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { check(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { check(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { check(); }
            public void check() {
                btnEliminarDiagnosticoCompleto.setEnabled(!txtDiagnosticoIdParaEliminar.getText().trim().isEmpty());
            }
        });

    }

    public static void main(String[] args) {
        // Para probar la ventana de forma aislada
        SwingUtilities.invokeLater(() -> {
            // Opcional: Cambiar el Look and Feel para que se vea más moderno
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new VentanaDiagnostico().setVisible(true);
        });
    }
}