package co.edu.uniquindio.poo.Model;

public class Repartidor {
    private String id;
    private String nombre;
    private String zona;
    private boolean disponible;

    public Repartidor(String id, String nombre, String zona, boolean disponible) {
        this.id = id;
        this.nombre = nombre;
        this.zona = zona;
        this.disponible = disponible;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getZona() {
        return zona;
    }

    public void setZona(String zona) {
        this.zona = zona;
    }

    public boolean isDisponible() {
        return disponible;
    }

    public void setDisponible(boolean disponible) {
        this.disponible = disponible;
    }
}
