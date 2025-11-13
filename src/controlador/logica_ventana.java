package controlador;

import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import vista.ventana;
import modelo.*;
import java.util.Locale;
import controlador.logica_ventana;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

/**
 *
 * @author leydi
 */
public class logica_ventana implements ListSelectionListener {

    private ventana vista;
    public personaDAO dao;

    public logica_ventana(ventana vista) {
        this.vista = vista;
        this.dao = new personaDAO();
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        int index = vista.lst_contactos.getSelectedIndex();
        if (index >= 0) {
            cargarDatosContacto(index);
        }
    }

    public void cargarDatosContacto(int index) {
        personaDAO dao = new personaDAO();
        List<persona> personas = dao.leerPersonas();
        if (index < personas.size()) {
            persona p = personas.get(index);
            vista.txt_nombre.setText(p.getNombre());
            vista.txt_telefono.setText(p.getTelefono());
            vista.txt_email.setText(p.getEmail());
            vista.cmb_categoria.setSelectedItem(p.getCategoria());
            vista.chb_favorito.setSelected(p.isFavorito());
        }
    }

    public void cargarContactosEnLista() {
        // Crear el DAO para acceder al CSV
        personaDAO dao = new personaDAO();

        // Leer todas las personas del archivo
        List<persona> personas = dao.leerPersonas();

        // Crear modelo para el JList
        DefaultListModel<String> listModel = new DefaultListModel<>();

        // Si hay personas, agregarlas al modelo
        for (persona p : personas) {
            String texto = p.getNombre() + " | " + p.getTelefono() + " | " + p.getEmail()
                    + " | " + p.getCategoria() + (p.isFavorito() ? " ⭐" : "");
            listModel.addElement(texto);
        }

        // Cargar en el componente de la vista
        vista.lst_contactos.setModel(listModel);
        limpiarCampos();
    }

    public void limpiarCampos() {
        vista.txt_nombre.setText("");
        vista.txt_telefono.setText("");
        vista.txt_email.setText("");
        vista.cmb_categoria.setSelectedIndex(0);
        vista.chb_favorito.setSelected(false);
        vista.txt_buscarpornombre.setText("");
        vista.progresoBarra.setValue(0);
    }

    public void modificarPersonaSeleccionada(int index, String nombre, String telefono, String email, String categoria, boolean favorito) {
        List<persona> lista = dao.leerPersonas();
        if (index >= 0 && index < lista.size()) {
            persona p = new persona(nombre, telefono, email, categoria, favorito);
            dao.modificarPersona(index, p);
            cargarContactosEnLista();
            limpiarCampos();
        }
    }

// Eliminar persona seleccionada
    public void eliminarPersonaSeleccionada(int index) {
        dao.eliminarPersona(index);
        cargarContactosEnLista();
        limpiarCampos();
    }

    public void filtrarLista(String texto) {
        List<persona> personas = dao.leerPersonas();
        DefaultListModel<String> modeloFiltrado = new DefaultListModel<>();

        for (persona p : personas) {
            if (p.getNombre().toLowerCase().contains(texto.toLowerCase())) {
                String textoPersona = p.getNombre() + " | " + p.getTelefono() + " | "
                        + p.getEmail() + " | " + p.getCategoria()
                        + (p.isFavorito() ? " ⭐" : "");
                modeloFiltrado.addElement(textoPersona);
            }
        }

        vista.lst_contactos.setModel(modeloFiltrado);
    }

    public void filtrarTabla(javax.swing.JTable tabla, String texto) {
        personaDAO dao = new personaDAO();
        List<persona> lista = dao.leerPersonas();

        // Filtra los contactos que contengan el texto
        List<persona> filtrados = new ArrayList<>();
        for (persona p : lista) {
            if (p.getNombre().toLowerCase().contains(texto.toLowerCase())) {
                filtrados.add(p);
            }
        }

        // Crea el modelo de tabla con los resultados filtrados
        String[] columnas = {"Nombre", "Teléfono", "Email"};
        String[][] datos = new String[filtrados.size()][3];
        for (int i = 0; i < filtrados.size(); i++) {
            persona p = filtrados.get(i);
            datos[i][0] = p.getNombre();
            datos[i][1] = p.getTelefono();
            datos[i][2] = p.getEmail();
        }

        javax.swing.table.DefaultTableModel modelo
                = new javax.swing.table.DefaultTableModel(datos, columnas);
        tabla.setModel(modelo);
    }

// --- ORDENAR CONTACTOS POR NOMBRE ---
    public void ordenarTabla(javax.swing.JTable tabla) {
        personaDAO dao = new personaDAO();
        List<persona> lista = dao.leerPersonas();

        // Ordenar por nombre alfabéticamente
        lista.sort((p1, p2) -> p1.getNombre().compareToIgnoreCase(p2.getNombre()));

        // Crear modelo ordenado
        String[] columnas = {"Nombre", "Teléfono", "Email"};
        String[][] datos = new String[lista.size()][3];
        for (int i = 0; i < lista.size(); i++) {
            persona p = lista.get(i);
            datos[i][0] = p.getNombre();
            datos[i][1] = p.getTelefono();
            datos[i][2] = p.getEmail();
        }

        javax.swing.table.DefaultTableModel modelo
                = new javax.swing.table.DefaultTableModel(datos, columnas);
        tabla.setModel(modelo);
    }

    public void actualizarEstadisticas(List<persona> listaPersonas, JList<String> lst_estadistica) {
        DefaultListModel<String> modeloEstadistica = new DefaultListModel<>();

        int total = listaPersonas.size();
        long favoritos = listaPersonas.stream().filter(p -> p.isFavorito()).count();

        modeloEstadistica.addElement("Total de contactos: " + total);
        modeloEstadistica.addElement("Contactos favoritos: " + favoritos);
        modeloEstadistica.addElement("Contactos por categoría:");

        listaPersonas.stream()
                .map(p -> p.getCategoria())
                .distinct()
                .forEach(cat -> {
                    long count = listaPersonas.stream().filter(c -> c.getCategoria().equals(cat)).count();
                    modeloEstadistica.addElement("  - " + cat + ": " + count);
                });

        lst_estadistica.setModel(modeloEstadistica);
    }

    public enum Operacion {
        AGREGAR, MODIFICAR, ELIMINAR
    }

    public void procesarContacto(Operacion op, int index, String nombre, String telefono, String email, String categoria, boolean favorito) {
        javax.swing.SwingWorker<Void, Integer> worker = new javax.swing.SwingWorker<>() {

            @Override
            protected Void doInBackground() throws Exception {
                int pasos = 100; // cantidad de pasos de progreso
                for (int i = 0; i <= pasos; i++) {
                    Thread.sleep(5); // Simula procesamiento
                    publish(i);
                }
                
                switch (op) {
                    case AGREGAR:
                        dao.guardarPersona(new persona(nombre, telefono, email, categoria, favorito));
                        break;
                    case MODIFICAR:
                        if (index >= 0) {
                            dao.modificarPersona(index, new persona(nombre, telefono, email, categoria, favorito));
                        }
                        break;
                    case ELIMINAR:
                        if (index >= 0) {
                            dao.eliminarPersona(index);
                        }
                        break;
                }
                //Exportar lista actualizada a CSV ---
                List<persona> lista = dao.leerPersonas();
                dao.exportarContactosCSV(lista);
                
                return null;
            }

            @Override
            protected void process(List<Integer> chunks) {
                int valor = chunks.get(chunks.size() - 1);
                vista.progresoBarra.setValue(valor);
            }

            @Override
            protected void done() {
                try {
                    // Actualizar lista y limpiar interfaz
                    cargarContactosEnLista();
                    limpiarCampos();
                    vista.progresoBarra.setValue(0);

                    SwingUtilities.invokeLater(() -> {
                        String mensaje = "";
                        switch (op) {
                            case AGREGAR:
                                mensaje = " Contacto guardado con éxito.";
                                break;
                            case MODIFICAR:
                                mensaje = " Contacto modificado correctamente.";
                                break;
                            case ELIMINAR:
                                mensaje = " Contacto eliminado correctamente.";
                                break;
                        }

                        JOptionPane.showMessageDialog(vista, mensaje, "Notificación", JOptionPane.INFORMATION_MESSAGE);
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(vista, "Error al procesar contacto: " + e.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };

        worker.execute();
    }

    private static java.util.Locale idiomaActual = java.util.Locale.getDefault();

    public static Locale getIdiomaActual() {
        return idiomaActual;
    }

    public static void setIdiomaActual(Locale nuevoLocale) {
        idiomaActual = nuevoLocale;
    }
}
