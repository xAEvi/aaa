package org.example.presentacion;

import org.example.modelo.Medicamento;
import org.example.servicio.ServicioMedicamento;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.util.List;

public class VentanaMedicamento extends JFrame {

    // Servicio
    private final ServicioMedicamento servicioMedicamento;

    // Componentes principales
    private JTabbedPane tabbedPane;

    // Pestaña 1: Registro
    private JPanel panelRegistro;
    private JTextField txtNombre;
    private JTextArea txtDescripcion;
    private JTextField txtPrecio;
    private JButton btnRegistrar;
    private JButton btnLimpiarFormulario;

    // Pestaña 2: Búsqueda
    private JPanel panelBusqueda;
    private JTextField txtBusqueda;
    private JCheckBox chkIncluirInactivos;
    private JTable tblResultados;
    private DefaultTableModel modelResultados;
    private JButton btnBuscar;

    // Pestaña 3: Edición
    private JPanel panelEdicion;
    private JTextField txtIdEditar;
    private JTextField txtNombreEditar;
    private JTextArea txtDescripcionEditar;
    private JTextField txtPrecioEditar;
    private JButton btnCargarParaEditar;
    private JButton btnGuardarCambios;
    private JButton btnRestaurarPrecioOriginal;
    private JLabel lblEstadoEditar;
    private JButton btnDesactivarReactivar;
    private BigDecimal precioOriginal; // Para restaurar el precio

    // Estado
    private String rolUsuarioActual = "Recepcionista"; // Debería obtenerse del sistema de autenticación

    public VentanaMedicamento() {
        this.servicioMedicamento = new ServicioMedicamento();

        setTitle("Gestión de Medicamentos");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(900, 650);
        setLocationRelativeTo(null);

        initComponents();
        layoutComponents();
        addEventListeners();
    }

    private void initComponents() {
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 12));

        // --- Pestaña 1: Registro ---
        panelRegistro = new JPanel();
        panelRegistro.setLayout(new BorderLayout(10, 10));
        panelRegistro.setBorder(new EmptyBorder(10, 10, 10, 10));

        txtNombre = new JTextField(25);
        txtDescripcion = new JTextArea(5, 25);
        txtPrecio = new JTextField(10);
        btnRegistrar = new JButton("Registrar Medicamento");
        btnRegistrar.setBackground(new Color(70, 130, 180));
        btnRegistrar.setForeground(Color.WHITE);
        btnRegistrar.setFocusPainted(false);
        btnLimpiarFormulario = new JButton("Limpiar");
        btnLimpiarFormulario.setBackground(new Color(220, 220, 220));
        btnLimpiarFormulario.setFocusPainted(false);

        // --- Pestaña 2: Búsqueda ---
        panelBusqueda = new JPanel();
        panelBusqueda.setLayout(new BorderLayout(10, 10));
        panelBusqueda.setBorder(new EmptyBorder(10, 10, 10, 10));

        txtBusqueda = new JTextField(25);
        chkIncluirInactivos = new JCheckBox("Incluir medicamentos inactivos");
        btnBuscar = new JButton("Buscar");
        btnBuscar.setBackground(new Color(70, 130, 180));
        btnBuscar.setForeground(Color.WHITE);
        btnBuscar.setFocusPainted(false);

        modelResultados = new DefaultTableModel(new String[]{"ID", "Nombre", "Precio", "Estado", "Descripción"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Tabla no editable
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 3) return Boolean.class; // Para renderizado de checkbox
                return Object.class;
            }
        };

        tblResultados = new JTable(modelResultados);
        tblResultados.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblResultados.setRowHeight(25);
        tblResultados.getColumnModel().getColumn(0).setPreferredWidth(50);  // ID
        tblResultados.getColumnModel().getColumn(1).setPreferredWidth(200); // Nombre
        tblResultados.getColumnModel().getColumn(2).setPreferredWidth(80);  // Precio
        tblResultados.getColumnModel().getColumn(3).setPreferredWidth(80);  // Estado
        tblResultados.getColumnModel().getColumn(4).setPreferredWidth(300); // Descripción

        // --- Pestaña 3: Edición ---
        panelEdicion = new JPanel();
        panelEdicion.setLayout(new BorderLayout(10, 10));
        panelEdicion.setBorder(new EmptyBorder(10, 10, 10, 10));

        txtIdEditar = new JTextField(5);
        txtIdEditar.setEditable(false);
        txtNombreEditar = new JTextField(25);
        txtDescripcionEditar = new JTextArea(5, 25);
        txtPrecioEditar = new JTextField(10);
        btnCargarParaEditar = new JButton("Cargar");
        btnCargarParaEditar.setBackground(new Color(70, 130, 180));
        btnCargarParaEditar.setForeground(Color.WHITE);
        btnCargarParaEditar.setFocusPainted(false);
        btnGuardarCambios = new JButton("Guardar Cambios");
        btnGuardarCambios.setBackground(new Color(60, 179, 113));
        btnGuardarCambios.setForeground(Color.WHITE);
        btnGuardarCambios.setFocusPainted(false);
        btnRestaurarPrecioOriginal = new JButton("Restaurar Precio");
        btnRestaurarPrecioOriginal.setBackground(new Color(255, 140, 0));
        btnRestaurarPrecioOriginal.setForeground(Color.WHITE);
        btnRestaurarPrecioOriginal.setFocusPainted(false);
        lblEstadoEditar = new JLabel("Estado: ");
        btnDesactivarReactivar = new JButton("Desactivar");
        btnDesactivarReactivar.setBackground(new Color(220, 20, 60));
        btnDesactivarReactivar.setForeground(Color.WHITE);
        btnDesactivarReactivar.setFocusPainted(false);

        // Configurar precio original
        precioOriginal = BigDecimal.ZERO;
    }

    private void layoutComponents() {
        // --- Pestaña 1: Registro ---
        JPanel panelFormularioRegistro = new JPanel(new GridBagLayout());
        panelFormularioRegistro.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(70, 130, 180)),
                "Registrar Nuevo Medicamento",
                TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 12), new Color(70, 130, 180))
        );

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Nombre
        gbc.gridx = 0; gbc.gridy = 0;
        panelFormularioRegistro.add(new JLabel("Nombre*:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        panelFormularioRegistro.add(txtNombre, gbc);

        // Descripción
        gbc.gridx = 0; gbc.gridy = 1;
        panelFormularioRegistro.add(new JLabel("Descripción:"), gbc);
        gbc.gridx = 1;
        JScrollPane scrollDesc = new JScrollPane(txtDescripcion);
        scrollDesc.setPreferredSize(new Dimension(300, 100));
        panelFormularioRegistro.add(scrollDesc, gbc);

        // Precio
        gbc.gridx = 0; gbc.gridy = 2;
        panelFormularioRegistro.add(new JLabel("Precio*:"), gbc);
        gbc.gridx = 1;
        panelFormularioRegistro.add(txtPrecio, gbc);

        // Botones
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.CENTER;
        gbc.anchor = GridBagConstraints.CENTER;
        JPanel panelBotonesRegistro = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        panelBotonesRegistro.add(btnRegistrar);
        panelBotonesRegistro.add(btnLimpiarFormulario);
        panelFormularioRegistro.add(panelBotonesRegistro, gbc);

        panelRegistro.add(panelFormularioRegistro, BorderLayout.NORTH);

        // Panel de información
        JPanel panelInfo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelInfo.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
        JLabel lblInfo = new JLabel("* Campos obligatorios");
        lblInfo.setForeground(Color.RED);
        panelInfo.add(lblInfo);
        panelRegistro.add(panelInfo, BorderLayout.CENTER);

        tabbedPane.addTab("Registro", new JScrollPane(panelRegistro));

        // --- Pestaña 2: Búsqueda ---
        JPanel panelControlesBusqueda = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panelControlesBusqueda.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(70, 130, 180)),
                "Filtros de Búsqueda",
                TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 12), new Color(70, 130, 180))
        );

        panelControlesBusqueda.add(new JLabel("Buscar:"));
        panelControlesBusqueda.add(txtBusqueda);
        panelControlesBusqueda.add(chkIncluirInactivos);
        panelControlesBusqueda.add(btnBuscar);

        JPanel panelResultados = new JPanel(new BorderLayout());
        panelResultados.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(70, 130, 180)),
                "Resultados",
                TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 12), new Color(70, 130, 180))
        );

        JScrollPane scrollTabla = new JScrollPane(tblResultados);
        scrollTabla.setPreferredSize(new Dimension(800, 400));
        panelResultados.add(scrollTabla, BorderLayout.CENTER);

        panelBusqueda.add(panelControlesBusqueda, BorderLayout.NORTH);
        panelBusqueda.add(panelResultados, BorderLayout.CENTER);

        tabbedPane.addTab("Búsqueda", new JScrollPane(panelBusqueda));

        // --- Pestaña 3: Edición ---
        JPanel panelFormularioEdicion = new JPanel(new GridBagLayout());
        panelFormularioEdicion.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(70, 130, 180)),
                "Editar Medicamento",
                TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 12), new Color(70, 130, 180))
        );

        GridBagConstraints gbcEditar = new GridBagConstraints();
        gbcEditar.insets = new Insets(8, 8, 8, 8);
        gbcEditar.anchor = GridBagConstraints.WEST;
        gbcEditar.fill = GridBagConstraints.HORIZONTAL;

        // ID Medicamento
        gbcEditar.gridx = 0; gbcEditar.gridy = 0;
        panelFormularioEdicion.add(new JLabel("ID Medicamento:"), gbcEditar);
        gbcEditar.gridx = 1;
        panelFormularioEdicion.add(txtIdEditar, gbcEditar);
        gbcEditar.gridx = 2;
        panelFormularioEdicion.add(btnCargarParaEditar, gbcEditar);

        // Nombre
        gbcEditar.gridx = 0; gbcEditar.gridy = 1;
        panelFormularioEdicion.add(new JLabel("Nombre*:"), gbcEditar);
        gbcEditar.gridx = 1; gbcEditar.gridwidth = 2; gbcEditar.weightx = 1.0;
        panelFormularioEdicion.add(txtNombreEditar, gbcEditar);

        // Descripción
        gbcEditar.gridx = 0; gbcEditar.gridy = 2;
        panelFormularioEdicion.add(new JLabel("Descripción:"), gbcEditar);
        gbcEditar.gridx = 1; gbcEditar.gridwidth = 2;
        JScrollPane scrollDescEdit = new JScrollPane(txtDescripcionEditar);
        scrollDescEdit.setPreferredSize(new Dimension(300, 100));
        panelFormularioEdicion.add(scrollDescEdit, gbcEditar);

        // Precio
        gbcEditar.gridx = 0; gbcEditar.gridy = 3;
        panelFormularioEdicion.add(new JLabel("Precio*:"), gbcEditar);
        gbcEditar.gridx = 1; gbcEditar.gridwidth = 1; gbcEditar.weightx = 0;
        panelFormularioEdicion.add(txtPrecioEditar, gbcEditar);
        gbcEditar.gridx = 2;
        panelFormularioEdicion.add(btnRestaurarPrecioOriginal, gbcEditar);

        // Estado
        gbcEditar.gridx = 0; gbcEditar.gridy = 4;
        panelFormularioEdicion.add(new JLabel("Estado:"), gbcEditar);
        gbcEditar.gridx = 1;
        panelFormularioEdicion.add(lblEstadoEditar, gbcEditar);
        gbcEditar.gridx = 2;
        panelFormularioEdicion.add(btnDesactivarReactivar, gbcEditar);

        // Botones
        gbcEditar.gridx = 0; gbcEditar.gridy = 5; gbcEditar.gridwidth = 3;
        gbcEditar.fill = GridBagConstraints.CENTER;
        gbcEditar.anchor = GridBagConstraints.CENTER;
        JPanel panelBotonesEdicion = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        panelBotonesEdicion.add(btnGuardarCambios);
        panelFormularioEdicion.add(panelBotonesEdicion, gbcEditar);

        panelEdicion.add(panelFormularioEdicion, BorderLayout.NORTH);

        // Panel de información
        JPanel panelInfoEdicion = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelInfoEdicion.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
        JLabel lblInfoEdicion = new JLabel("* Campos obligatorios");
        lblInfoEdicion.setForeground(Color.RED);
        panelInfoEdicion.add(lblInfoEdicion);
        panelEdicion.add(panelInfoEdicion, BorderLayout.CENTER);

        tabbedPane.addTab("Edición", new JScrollPane(panelEdicion));

        // Agregar pestañas a la ventana
        add(tabbedPane);
    }

    private void addEventListeners() {
        // --- Pestaña 1: Registro ---
        btnRegistrar.addActionListener(e -> registrarMedicamento());
        btnLimpiarFormulario.addActionListener(e -> limpiarFormularioRegistro());

        // Validación de precio en tiempo real (RF-MDC-005)
        txtPrecio.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { validarPrecio(txtPrecio); }
            @Override
            public void removeUpdate(DocumentEvent e) { validarPrecio(txtPrecio); }
            @Override
            public void changedUpdate(DocumentEvent e) { validarPrecio(txtPrecio); }
        });

        // --- Pestaña 2: Búsqueda ---
        btnBuscar.addActionListener(e -> buscarMedicamentos());

        // Doble clic en tabla para cargar en pestaña de edición (RF-MDC-002)
        tblResultados.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    cargarMedicamentoSeleccionadoParaEdicion();
                }
            }
        });

        // --- Pestaña 3: Edición ---
        btnCargarParaEditar.addActionListener(e -> cargarMedicamentoParaEdicion());
        btnGuardarCambios.addActionListener(e -> guardarCambiosMedicamento());
        btnRestaurarPrecioOriginal.addActionListener(e -> restaurarPrecioOriginal());
        btnDesactivarReactivar.addActionListener(e -> cambiarEstadoMedicamento());

        // Validación de precio en tiempo real en edición
        txtPrecioEditar.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { validarPrecio(txtPrecioEditar); }
            @Override
            public void removeUpdate(DocumentEvent e) { validarPrecio(txtPrecioEditar); }
            @Override
            public void changedUpdate(DocumentEvent e) { validarPrecio(txtPrecioEditar); }
        });
    }

    // --- Métodos de acción ---

    // RF-MDC-001: Registrar Nuevo Medicamento
    private void registrarMedicamento() {
        try {
            // Validar campos obligatorios
            if (txtNombre.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "El nombre del medicamento es obligatorio", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            BigDecimal precio;
            try {
                precio = new BigDecimal(txtPrecio.getText().trim());
                if (precio.compareTo(BigDecimal.ZERO) <= 0) {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Ingrese un precio válido (mayor a cero)", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Crear objeto Medicamento
            Medicamento nuevo = new Medicamento();
            nuevo.setNombre(txtNombre.getText().trim());
            nuevo.setDescripcionPresentacion(txtDescripcion.getText().trim());
            nuevo.setPrecio(precio);

            // Llamar al servicio
            String resultado = servicioMedicamento.registrarMedicamento(nuevo);

            if (resultado.startsWith("OK:")) {
                JOptionPane.showMessageDialog(this, resultado.substring(4), "Éxito", JOptionPane.INFORMATION_MESSAGE);
                limpiarFormularioRegistro();
                buscarMedicamentos(); // Actualizar lista
            } else {
                JOptionPane.showMessageDialog(this, resultado.substring(7), "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al registrar medicamento: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    // RF-MDC-002: Búsqueda Avanzada de Medicamentos
    private void buscarMedicamentos() {
        try {
            String textoBusqueda = txtBusqueda.getText().trim();
            boolean incluirInactivos = chkIncluirInactivos.isSelected();

            List<Medicamento> resultados = servicioMedicamento.buscarMedicamentos(
                    textoBusqueda.isEmpty() ? null : textoBusqueda,
                    null, // precioMin - podríamos agregar controles para esto
                    null, // precioMax
                    incluirInactivos
            );

            // Actualizar tabla
            modelResultados.setRowCount(0);
            for (Medicamento m : resultados) {
                modelResultados.addRow(new Object[]{
                        m.getId(),
                        m.getNombre(),
                        m.getPrecio(),
                        m.isActivo(),
                        m.getDescripcionPresentacion()
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al buscar medicamentos: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    // RF-MDC-003: Editar Información de Medicamento
    private void cargarMedicamentoParaEdicion() {
        try {
            int id = Integer.parseInt(txtIdEditar.getText().trim());
            Medicamento m = servicioMedicamento.obtenerMedicamentoPorId(id);

            if (m == null) {
                JOptionPane.showMessageDialog(this, "No se encontró el medicamento con ID " + id, "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Mostrar datos en el formulario
            txtNombreEditar.setText(m.getNombre());
            txtDescripcionEditar.setText(m.getDescripcionPresentacion());
            txtPrecioEditar.setText(m.getPrecio().toString());
            precioOriginal = m.getPrecio();

            // Configurar estado
            lblEstadoEditar.setText("Estado: " + (m.isActivo() ? "Activo" : "Inactivo"));
            btnDesactivarReactivar.setText(m.isActivo() ? "Desactivar" : "Reactivar");

            // Cambiar a pestaña de edición
            tabbedPane.setSelectedIndex(2);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Ingrese un ID válido", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al cargar medicamento: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void cargarMedicamentoSeleccionadoParaEdicion() {
        int filaSeleccionada = tblResultados.getSelectedRow();
        if (filaSeleccionada == -1) return;

        int id = (Integer) modelResultados.getValueAt(filaSeleccionada, 0);
        txtIdEditar.setText(String.valueOf(id));
        cargarMedicamentoParaEdicion();
    }

    private void guardarCambiosMedicamento() {
        try {
            int id = Integer.parseInt(txtIdEditar.getText().trim());

            // Validar campos
            if (txtNombreEditar.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "El nombre del medicamento es obligatorio", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            BigDecimal precio;
            try {
                precio = new BigDecimal(txtPrecioEditar.getText().trim());
                if (precio.compareTo(BigDecimal.ZERO) <= 0) {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Ingrese un precio válido (mayor a cero)", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Crear objeto Medicamento con los cambios
            Medicamento medicamento = new Medicamento();
            medicamento.setId(id);
            medicamento.setNombre(txtNombreEditar.getText().trim());
            medicamento.setDescripcionPresentacion(txtDescripcionEditar.getText().trim());
            medicamento.setPrecio(precio);

            // Llamar al servicio
            String resultado = servicioMedicamento.modificarMedicamento(medicamento);

            if (resultado.startsWith("OK:")) {
                JOptionPane.showMessageDialog(this, resultado.substring(4), "Éxito", JOptionPane.INFORMATION_MESSAGE);
                buscarMedicamentos(); // Actualizar lista
            } else {
                JOptionPane.showMessageDialog(this, resultado.substring(7), "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Ingrese un ID válido", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al guardar cambios: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    // RF-MDC-005: Validación de Precios en Tiempo Real
    private void validarPrecio(JTextField campoPrecio) {
        try {
            String texto = campoPrecio.getText().trim();
            if (!texto.isEmpty()) {
                BigDecimal precio = new BigDecimal(texto);
                if (precio.compareTo(BigDecimal.ZERO) <= 0) {
                    campoPrecio.setBackground(new Color(255, 200, 200));
                } else {
                    campoPrecio.setBackground(Color.WHITE);
                }
            } else {
                campoPrecio.setBackground(Color.WHITE);
            }
        } catch (NumberFormatException e) {
            campoPrecio.setBackground(new Color(255, 200, 200));
        }
    }

    private void restaurarPrecioOriginal() {
        txtPrecioEditar.setText(precioOriginal.toString());
        validarPrecio(txtPrecioEditar);
    }

    // RF-MDC-004: Desactivar/Reactivar Medicamento
    private void cambiarEstadoMedicamento() {
        try {
            int id = Integer.parseInt(txtIdEditar.getText().trim());
            Medicamento m = servicioMedicamento.obtenerMedicamentoPorId(id);

            if (m == null) {
                JOptionPane.showMessageDialog(this, "No se encontró el medicamento con ID " + id, "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String confirmacion = m.isActivo() ?
                    "¿Está seguro de desactivar este medicamento? No estará disponible para nuevas recetas." :
                    "¿Está seguro de reactivar este medicamento? Estará disponible para nuevas recetas.";

            int opcion = JOptionPane.showConfirmDialog(
                    this,
                    confirmacion,
                    "Confirmar cambio de estado",
                    JOptionPane.YES_NO_OPTION
            );

            if (opcion == JOptionPane.YES_OPTION) {
                String resultado;
                if (m.isActivo()) {
                    resultado = servicioMedicamento.desactivarMedicamento(id, rolUsuarioActual);
                } else {
                    resultado = servicioMedicamento.reactivarMedicamento(id, rolUsuarioActual);
                }

                if (resultado.startsWith("OK:")) {
                    JOptionPane.showMessageDialog(this, resultado.substring(4), "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    // Recargar datos
                    cargarMedicamentoParaEdicion();
                    buscarMedicamentos();
                } else {
                    JOptionPane.showMessageDialog(this, resultado.substring(7), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Ingrese un ID válido", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al cambiar estado: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    // --- Métodos auxiliares ---
    private void limpiarFormularioRegistro() {
        txtNombre.setText("");
        txtDescripcion.setText("");
        txtPrecio.setText("");
        txtPrecio.setBackground(Color.WHITE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new VentanaMedicamento().setVisible(true);
        });
    }
}