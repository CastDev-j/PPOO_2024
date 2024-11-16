import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class BoletaCalificacionesCRUD extends JFrame {
    private JTextField campoAlumno, campoProfesor, campoCalificacion;
    private JComboBox<String> comboMaterias;
    private JTable tablaAlumnos, tablaMaterias;
    private DefaultTableModel modeloTablaAlumnos, modeloTablaMaterias;
    private List<Alumno> alumnos = new ArrayList<>();
    private List<Materia> materias = new ArrayList<>();

    public BoletaCalificacionesCRUD() {
        setTitle("CRUD Boleta de Calificaciones");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        inicializarMaterias();

        generarAlumnosGenericos();

        JPanel panelFormularioAlumnos = new JPanel(new GridLayout(4, 2, 5, 5));
        campoAlumno = new JTextField();
        campoProfesor = new JTextField();
        campoCalificacion = new JTextField();
        comboMaterias = new JComboBox<>(materias.stream().map(Materia::getNombre).toArray(String[]::new));

        panelFormularioAlumnos.add(new JLabel("Alumno:"));
        panelFormularioAlumnos.add(campoAlumno);
        panelFormularioAlumnos.add(new JLabel("Materia:"));
        panelFormularioAlumnos.add(comboMaterias);
        panelFormularioAlumnos.add(new JLabel("Profesor:"));
        panelFormularioAlumnos.add(campoProfesor);
        panelFormularioAlumnos.add(new JLabel("Calificación:"));
        panelFormularioAlumnos.add(campoCalificacion);

        JButton botonAgregarAlumno = new JButton("Agregar Alumno");
        JPanel panelBotones = new JPanel();
        panelBotones.add(botonAgregarAlumno);

        modeloTablaAlumnos = new DefaultTableModel(new String[]{"ID", "Alumno", "Materia", "Profesor", "Calificación", "Eliminar"}, 0);
        tablaAlumnos = new JTable(modeloTablaAlumnos) {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                if (column == 4) { 
                    double calificacion = (double) getValueAt(row, column);
                    if (calificacion < 70) {
                        c.setBackground(new Color(255, 200, 200)); 
                    } else {
                        c.setBackground(new Color(200, 255, 200)); 
                    }
                } else {
                    c.setBackground(Color.WHITE); 
                }
                return c;
            }
        };

        JScrollPane scrollAlumnos = new JScrollPane(tablaAlumnos);

        botonAgregarAlumno.addActionListener(e -> agregarAlumno());
        tablaAlumnos.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int fila = tablaAlumnos.rowAtPoint(e.getPoint());
                int columna = tablaAlumnos.columnAtPoint(e.getPoint());
                if (columna == 5) {
                    eliminarAlumno(fila);
                }
            }
        });

        modeloTablaMaterias = new DefaultTableModel(new String[]{"Materia", "Cantidad Alumnos", "Promedio"}, 0);
        tablaMaterias = new JTable(modeloTablaMaterias) {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                if (column == 2) { 
                    String promedioTexto = getValueAt(row, column).toString();
                    try {
                        double promedio = Double.parseDouble(promedioTexto);
                        if (promedio < 70) {
                            c.setBackground(new Color(255, 200, 200)); 
                        } else {
                            c.setBackground(new Color(200, 255, 200));
                        }
                    } catch (NumberFormatException ex) {
                        c.setBackground(Color.WHITE); 
                    }
                } else {
                    c.setBackground(Color.WHITE);
                }
                return c;
            }
        };

        JScrollPane scrollMaterias = new JScrollPane(tablaMaterias);

        JButton botonActualizarMaterias = new JButton("Actualizar Resumen Materias");
        botonActualizarMaterias.addActionListener(e -> actualizarResumenMaterias());

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollAlumnos, scrollMaterias);
        splitPane.setResizeWeight(0.5);

        JPanel panelSuperior = new JPanel(new BorderLayout());
        panelSuperior.add(panelFormularioAlumnos, BorderLayout.CENTER);
        panelSuperior.add(panelBotones, BorderLayout.SOUTH);
        add(panelSuperior, BorderLayout.NORTH);
        add(splitPane, BorderLayout.CENTER);
        add(botonActualizarMaterias, BorderLayout.SOUTH);

        actualizarTablaAlumnos();
        actualizarResumenMaterias();
    }

    private void inicializarMaterias() {
        String[] nombresMaterias = {"Matemáticas", "Física", "Química", "Historia", "Literatura", "Inglés", "Biología", "Geografía", "Arte", "Programación"};
        for (String nombre : nombresMaterias) {
            materias.add(new Materia(nombre));
        }
    }

    private void generarAlumnosGenericos() {
        Random random = new Random();
        for (int i = 1; i <= 100; i++) {
            String nombre = "Alumno " + i;
            String materia = materias.get(random.nextInt(materias.size())).getNombre();
            String profesor = "Profesor " + (random.nextInt(10) + 1);
            double calificacion = 50 + random.nextInt(51);
            alumnos.add(new Alumno(nombre, materia, profesor, calificacion));
        }
    }

    private void agregarAlumno() {
        String alumno = campoAlumno.getText().trim();
        String materia = (String) comboMaterias.getSelectedItem();
        String profesor = campoProfesor.getText().trim();
        String calificacionTexto = campoCalificacion.getText().trim();

        if (alumno.isEmpty() || profesor.isEmpty() || calificacionTexto.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Todos los campos deben estar llenos.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            double calificacion = Double.parseDouble(calificacionTexto);
            if (calificacion < 0 || calificacion > 100) {
                JOptionPane.showMessageDialog(this, "La calificación debe estar entre 0 y 100.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Alumno nuevoAlumno = new Alumno(alumno, materia, profesor, calificacion);
            alumnos.add(nuevoAlumno);
            limpiarCampos();
            actualizarTablaAlumnos();
            actualizarResumenMaterias();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "La calificación debe ser un número válido.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limpiarCampos() {
        campoAlumno.setText("");
        campoProfesor.setText("");
        campoCalificacion.setText("");
    }

    private void eliminarAlumno(int index) {
        alumnos.remove(index);
        actualizarTablaAlumnos();
        actualizarResumenMaterias();
    }

    private void actualizarTablaAlumnos() {
        modeloTablaAlumnos.setRowCount(0);
        for (int i = 0; i < alumnos.size(); i++) {
            Alumno alumno = alumnos.get(i);
            modeloTablaAlumnos.addRow(new Object[]{
                    i,
                    alumno.getNombre(),
                    alumno.getMateria(),
                    alumno.getProfesor(),
                    alumno.getCalificacion(),
                    "Eliminar"
            });
        }
    }

    private void actualizarResumenMaterias() {
        modeloTablaMaterias.setRowCount(0);
        for (Materia materia : materias) {
            List<Double> calificacionesMateria = alumnos.stream()
                    .filter(a -> a.getMateria().equals(materia.getNombre()))
                    .map(Alumno::getCalificacion)
                    .collect(Collectors.toList());

            int cantidadAlumnos = calificacionesMateria.size();
            double promedio = cantidadAlumnos > 0 ? calificacionesMateria.stream().mapToDouble(Double::doubleValue).average().orElse(0) : 0;

            modeloTablaMaterias.addRow(new Object[]{materia.getNombre(), cantidadAlumnos, String.format("%.2f", promedio)});
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new BoletaCalificacionesCRUD().setVisible(true));
    }
}

class Alumno {
    private String nombre;
    private String materia;
    private String profesor;
    private double calificacion;

    public Alumno(String nombre, String materia, String profesor, double calificacion) {
        this.nombre = nombre;
        this.materia = materia;
        this.profesor = profesor;
        this.calificacion = calificacion;
    }

    public String getNombre() {
        return nombre;
    }

    public String getMateria() {
        return materia;
    }

    public String getProfesor() {
        return profesor;
    }

    public double getCalificacion() {
        return calificacion;
    }
}

class Materia {
    private String nombre;

    public Materia(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }
}
