/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

/**
 *
 * @author leydi
 */
public class persona {
    private String nombre, telefono,email,categoria;
    private boolean favorito;

    public persona() {
    }

    
    
    
    public persona(String nombre, String telefono, String email, String categoria, boolean favorito) {
        this.nombre = nombre;
        this.telefono = telefono;
        this.email = email;
        this.categoria = categoria;
        this.favorito = favorito;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public boolean isFavorito() {
        return favorito;
    }

    public void setFavorito(boolean favorito) {
        this.favorito = favorito;
    }

    @Override
    public String toString() {
        return "persona{" + "nombre=" + nombre + ", telefono=" + telefono + ", email=" + email + ", categoria=" + categoria + ", favorito=" + favorito + '}';
    }
    public String datosContacto() {
		
		// Estructurar el siguiente formato: nombre;telefono;email;categoria;favorito
		// Por ejemplo: Daniela Poma;097145478;dpoma2024@gmail.com;amigo;true
		String contacto = String.format("%s;%s;%s;%s;%s", nombre, telefono, email, categoria, favorito); // Crea una cadena formateada con los valores de las variables
		return contacto; // Retorna la cadena formateada
	}
	//Método para proveer el formato de los campos que se van a imprimir en la lista
	public String formatoLista() {
		String contacto= String.format("%-40s%-40s%-40s%-40s", nombre, telefono, email, categoria);
		return contacto;
	}

    
}
