/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 *
 * @author leydi
 */
public class IdiomaManager {

    private static ResourceBundle bundle;
    private static Locale idiomaActual = new Locale("es", "ES");

    public static void cargarIdioma(Locale locale) {
        bundle = ResourceBundle.getBundle("idiomas.mensajes", locale);
    }

    public static ResourceBundle getBundle() {
        if (bundle == null) {
            cargarIdioma(idiomaActual);
        }
        return bundle;
    }

    public static Locale getIdiomaActual() {
        return idiomaActual;
    }

    public static String getTexto(String clave) {
        return getBundle().getString(clave);
    }
}
