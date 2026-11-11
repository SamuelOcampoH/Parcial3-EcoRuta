
package co.edu.uniquindio.ecoruta;

public record Aportante(String identificacion, String nombre, String correo) {
    public Aportante {
        if (identificacion == null || identificacion.isBlank()) {
            throw new IllegalArgumentException("La identificación no puede ser nula ni vacía.");
        }
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre no puede ser nulo ni vacío.");
        }
    }

    @Override
    public String toString() {
        return "[" + identificacion + "] " + nombre + " (" + correo + ")";
    }
}