package co.edu.uniquindio.poo.Model;

public class Repartidor {
    private final String id;
    private String nombre;
    private String zona;
    private boolean disponible;

    // Crea un repartidor validando que identificación, nombre y zona no sean nulos ni vacíos
    public Repartidor(String id, String nombre, String zona, boolean disponible) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("La identificación no puede ser nula ni vacía");
        }
        this.id = id;
        setNombre(nombre);
        setZona(zona);
        this.disponible = disponible;
    }

    // Representación en texto del repartidor
    @Override
    public String toString() {
        return id + " - " + nombre + " (" + zona + ", " + (disponible ? "disponible" : "no disponible") + ")";
    }

    // La identificación no tiene setter: es la llave del HashMap de repartidores
    public String getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    // Asigna el nombre validando que no sea nulo ni vacío
    public void setNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre no puede ser nulo ni vacío");
        }
        this.nombre = nombre;
    }

    public String getZona() {
        return zona;
    }

    // Asigna la zona validando que no sea nula ni vacía
    public void setZona(String zona) {
        if (zona == null || zona.isBlank()) {
            throw new IllegalArgumentException("La zona no puede ser nula ni vacía");
        }
        this.zona = zona;
    }

    public boolean isDisponible() {
        return disponible;
    }

    public void setDisponible(boolean disponible) {
        this.disponible = disponible;
    }
}