package org.example.presentacion; // Asegúrate que este sea tu paquete real

import javax.swing.*;
import java.awt.*;

public class Main extends JFrame {

    public Main() {
        setTitle("Sistema de Gestión Hospitalaria");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // setSize(500, 450); // Aumentamos un poco la altura para el botón Salir
        setMinimumSize(new Dimension(500, 450)); // Mejor usar minimum size
        setLocationRelativeTo(null); // Centrar en pantalla

        // Panel principal con BorderLayout
        JPanel panelPrincipal = new JPanel(new BorderLayout(0, 15)); // Añadimos espacio vertical entre componentes del BorderLayout
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Margen

        // Título
        JLabel lblTitulo = new JLabel("MENÚ PRINCIPAL", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 24));
        panelPrincipal.add(lblTitulo, BorderLayout.NORTH);

        // Panel para los botones del menú con GridLayout (ahora 4x2 para los 8 módulos)
        JPanel panelMenuModulos = new JPanel(new GridLayout(4, 2, 15, 15));
        // No es necesario el borde superior aquí si el BorderLayout ya tiene vgap

        // Crear botones para cada módulo
        JButton btnPacientes = new JButton("Gestión de Pacientes");
        JButton btnMedicos = new JButton("Gestión de Médicos");
        JButton btnCitas = new JButton("Gestión de Citas");
        JButton btnDiagnosticos = new JButton("Gestión de Diagnósticos");
        JButton btnMedicamentos = new JButton("Gestión de Medicamentos");
        JButton btnUsuarios = new JButton("Gestión de Usuarios");
        JButton btnFacturas = new JButton("Gestión de Facturas");
        JButton btnReportes = new JButton("Gestión de Reportes");

        // Añadir botones de módulos al panel de menú
        panelMenuModulos.add(btnPacientes);
        panelMenuModulos.add(btnMedicos);
        panelMenuModulos.add(btnCitas);
        panelMenuModulos.add(btnDiagnosticos);
        panelMenuModulos.add(btnMedicamentos);
        panelMenuModulos.add(btnUsuarios);
        panelMenuModulos.add(btnFacturas);
        panelMenuModulos.add(btnReportes);

        panelPrincipal.add(panelMenuModulos, BorderLayout.CENTER);

        // Panel para el botón Salir
        JPanel panelSalir = new JPanel(new FlowLayout(FlowLayout.CENTER)); // Para centrar el botón Salir
        JButton btnSalir = new JButton("Salir");
        btnSalir.setPreferredSize(new Dimension(200, btnSalir.getPreferredSize().height));
        panelSalir.add(btnSalir);

        panelPrincipal.add(panelSalir, BorderLayout.SOUTH); // Añadir panel Salir abajo

        add(panelPrincipal);

        // --- Action Listeners para los botones ---
        btnPacientes.addActionListener(e -> mostrarMensajeModulo("Pacientes"));
        btnMedicos.addActionListener(e -> mostrarMensajeModulo("Médicos"));
        btnCitas.addActionListener(e -> mostrarMensajeModulo("Citas"));
        btnDiagnosticos.addActionListener(e -> {
            // Creamos una instancia de la ventana de diagnósticos
            // Pasamos 'this' porque Main es un JFrame y actúa como el 'owner' (padre)
            VentanaDiagnostico ventanaDiagnosticos = new VentanaDiagnostico();
            ventanaDiagnosticos.setVisible(true); // Hacemos visible la nueva ventana
            // El código aquí se ejecutará DESPUÉS de que VentanaDiagnosticos se cierre,
            // porque la hemos hecho modal.
            System.out.println("Ventana de diagnósticos cerrada.");
        });
        btnMedicamentos.addActionListener(e -> {
            VentanaMedicamento ventanaMedicamentos = new VentanaMedicamento();
            ventanaMedicamentos.setVisible(true);
        });
        btnUsuarios.addActionListener(e -> mostrarMensajeModulo("Usuarios del Sistema"));
        btnFacturas.addActionListener(e -> mostrarMensajeModulo("Facturas"));
        btnReportes.addActionListener(e -> mostrarMensajeModulo("Reportes"));

        btnSalir.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "¿Está seguro de que desea salir?",
                    "Confirmar Salida",
                    JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
        });

        // Hacer visible la ventana
        pack(); // Ajusta el tamaño de la ventana al contenido preferido
        setLocationRelativeTo(null); // Centrar de nuevo después de pack()
        setVisible(true);
    }

    private void mostrarMensajeModulo(String nombreModulo) {
        JOptionPane.showMessageDialog(this,
                "Funcionalidad de '" + nombreModulo + "' no implementada aún.",
                "Módulo: " + nombreModulo,
                JOptionPane.INFORMATION_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Main();
            }
        });
    }
}