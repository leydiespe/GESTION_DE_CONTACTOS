/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package modelo;

import java.io.File;
import java.io.FileReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.io.*;
import vista.ventana;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.Future;
import java.util.concurrent.locks.Lock;
import java.util.stream.Collectors;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

/**
 *
 * @author leydi
 */
public class personaDAO {

    private ventana vista;

    private static final String ARCHIVO = "datosContactos.csv";
    private static final String SEPARADOR = ",";
    // Executor para tareas en background (compartido)
    private final ExecutorService bgExecutor = Executors.newFixedThreadPool(4);

// Lock global para sincronizar exportaciones al archivo
    private final Object exportLock = new Object();

// Locks por contacto para edición segura (clave: email u otra clave única)
    private static final ReentrantLock LOCK = new ReentrantLock();

    /**
     * Guarda una persona en el archivo CSV. Si el archivo no existe, lo crea
     * automáticamente.
     */
    public void guardarPersona(persona p) {
        try (FileWriter fw = new FileWriter(ARCHIVO, true); BufferedWriter bw = new BufferedWriter(fw); PrintWriter out = new PrintWriter(bw)) {

            // Formato: nombre,telefono,email,categoria,favorito
            out.println(p.getNombre() + SEPARADOR
                    + p.getTelefono() + SEPARADOR
                    + p.getEmail() + SEPARADOR
                    + p.getCategoria() + SEPARADOR
                    + p.isFavorito());

        } catch (IOException e) {
            System.out.println("Error al guardar persona: " + e.getMessage());
        }
    }

    /**
     * Lee todas las personas desde el archivo CSV. Retorna una lista de objetos
     * persona.
     */
    public List<persona> leerPersonas() {
        List<persona> lista = new ArrayList<>();
        File file = new File(ARCHIVO);

        if (!file.exists()) {
            System.out.println("El archivo no existe aún. Se creará cuando se guarde una persona.");
            return lista;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] datos = linea.split(SEPARADOR);
                if (datos.length == 5) {
                    String nombre = datos[0];
                    String telefono = datos[1];
                    String email = datos[2];
                    String categoria = datos[3];
                    boolean favorito = Boolean.parseBoolean(datos[4]);
                    lista.add(new persona(nombre, telefono, email, categoria, favorito));
                }
            }
        } catch (IOException e) {
            System.out.println("Error al leer archivo: " + e.getMessage());
        }

        return lista;
    }

    public String[][] obtenerDatosTabla() {
        List<persona> lista = leerPersonas();
        String[][] datos = new String[lista.size()][3];

        for (int i = 0; i < lista.size(); i++) {
            persona p = lista.get(i);
            datos[i][0] = p.getNombre();
            datos[i][1] = p.getTelefono();
            datos[i][2] = p.getEmail();
        }
        return datos;
    }

    public void modificarPersona(int index, persona p) {
        List<persona> lista = leerPersonas();
        if (index >= 0 && index < lista.size()) {
            lista.set(index, p);
            sobrescribirArchivo(lista);
        }
    }

    // Eliminar persona según índice
    public void eliminarPersona(int index) {
        List<persona> lista = leerPersonas();
        if (index >= 0 && index < lista.size()) {
            lista.remove(index);
            sobrescribirArchivo(lista);
        }
    }

    // Sobrescribe todo el archivo
    private void sobrescribirArchivo(List<persona> lista) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(ARCHIVO))) {
            for (persona p : lista) {
                pw.println(p.getNombre() + SEPARADOR
                        + p.getTelefono() + SEPARADOR
                        + p.getEmail() + SEPARADOR
                        + p.getCategoria() + SEPARADOR
                        + p.isFavorito());
            }
        } catch (IOException e) {
            System.out.println("Error al sobrescribir archivo: " + e.getMessage());
        }
    }

    // Guarda una copia de los contactos mostrados en un nuevo CSV
    public void guardarDatosTablaCSV() {
        String nuevoArchivo = "c:/datos_guardados.csv";
        List<persona> lista = leerPersonas();

        try (PrintWriter pw = new PrintWriter(new FileWriter(nuevoArchivo))) {
            for (persona p : lista) {
                pw.println(p.getNombre() + SEPARADOR
                        + p.getTelefono() + SEPARADOR
                        + p.getEmail());
            }
            System.out.println("Datos guardados en: " + nuevoArchivo);
        } catch (IOException e) {
            System.out.println("Error al guardar datos de la tabla: " + e.getMessage());
        }
    }

    /**
     * Verifica si ya existe un contacto con mismo nombre+telefono+email. Se
     * puede cambiar la clave a email si ese campo es único.
     */
    public boolean existePersona(persona p) {
        List<persona> lista = leerPersonas();
        String nombre = (p.getNombre() != null) ? p.getNombre().trim().toLowerCase() : "";
        String tel = (p.getTelefono() != null) ? p.getTelefono().trim() : "";
        String email = (p.getEmail() != null) ? p.getEmail().trim().toLowerCase() : "";
        for (persona q : lista) {
            if (nombre.equals(q.getNombre().trim().toLowerCase())
                    && tel.equals(q.getTelefono().trim())
                    && email.equals(q.getEmail().trim().toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    
    // Busca contactos que contengan la cadena 'texto' en nombre, teléfono o email.
     
    public List<persona> buscarPersonas(String texto) {
        if (texto == null || texto.trim().isEmpty()) {
            return leerPersonas();
        }
        String qnorm = texto.trim().toLowerCase();
        List<persona> todos = leerPersonas();
        return todos.stream()
                .filter(p -> (p.getNombre() != null && p.getNombre().toLowerCase().contains(qnorm))
                || (p.getTelefono() != null && p.getTelefono().toLowerCase().contains(qnorm))
                || (p.getEmail() != null && p.getEmail().toLowerCase().contains(qnorm)))
                .collect(Collectors.toList());
    }
   //exportacion en segundo plano 
    public void exportarContactosCSV(List<persona> lista) {
        synchronized (LOCK) {
            try (FileWriter fw = new FileWriter(ARCHIVO, false); BufferedWriter bw = new BufferedWriter(fw); PrintWriter out = new PrintWriter(bw)) {

                for (persona p : lista) {
                    out.println(p.getNombre() + ","
                            + p.getTelefono() + ","
                            + p.getEmail() + ","
                            + p.getCategoria() + ","
                            + p.isFavorito());
                }

                System.out.println("Exportación completada con éxito.");

            } catch (IOException e) {
                System.err.println("Error al exportar contactos: " + e.getMessage());
            }
        }
    }

}
