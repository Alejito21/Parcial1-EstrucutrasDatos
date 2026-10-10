package co.edu.uniquindio.poo.Model;

import java.util.Objects;

// El orden natural del Paquete está dado por su código (ascendente)
public class Paquete implements Comparable<Paquete> {
    public static final int PRIORIDAD_MINIMA = 1;
    public static final int PRIORIDAD_MAXIMA = 5;
    public static final int PRIORIDAD_POR_DEFECTO = 3;

    private final String codigo;
    private String destino;
    private double peso;
    private int prioridad;
    private int tiempo;


    //Constructor con prioridad por defecto (3)
    public Paquete(String codigo, String destino, double peso, int tiempo) {
        this(codigo, destino, peso, PRIORIDAD_POR_DEFECTO, tiempo);
    }

    //Constructor completo
    public Paquete(String codigo, String destino, double peso, int prioridad, int tiempo) {
        if (codigo == null || codigo.isBlank()) {
            throw new IllegalArgumentException("El código no puede ser nulo ni vacío");
        }
        this.codigo = codigo;
        setDestino(destino);
        setPeso(peso);
        setPrioridad(prioridad);
        setTiempo(tiempo);
    }

    // Orden natural: compara los paquetes por su código en orden ascendente
    @Override
    public int compareTo(Paquete otro) {
        return this.codigo.compareTo(otro.codigo);
    }

    // Dos paquetes son iguales si tienen el mismo código
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Paquete)) return false;
        Paquete otro = (Paquete) o;
        return Objects.equals(codigo, otro.codigo);
    }

    // Calcula el hashCode a partir del código
    @Override
    public int hashCode() {
        return Objects.hashCode(codigo);
    }

    // Representación en texto del paquete
    @Override
    public String toString() {
        return codigo + " [" + destino + ", " + peso + " kg, prioridad " + prioridad + ", " + tiempo + " min]";
    }

    // El código no tiene setter: es la llave del HashMap y no debe cambiar después de registrarse
    public String getCodigo() {
        return codigo;
    }

    public String getDestino() {
        return destino;
    }

    // Asigna el destino validando que no sea nulo ni vacío
    public void setDestino(String destino) {
        if (destino == null || destino.isBlank()) {
            throw new IllegalArgumentException("El destino no puede ser nulo ni vacío");
        }
        this.destino = destino;
    }

    public double getPeso() {
        return peso;
    }

    // Asigna el peso validando que sea mayor que cero
    public void setPeso(double peso) {
        if (peso <= 0) {
            throw new IllegalArgumentException("El peso debe ser mayor que cero: " + peso);
        }
        this.peso = peso;
    }

    public int getPrioridad() {
        return prioridad;
    }

    // Asigna la prioridad validando que esté entre 1 y 5
    public void setPrioridad(int prioridad) {
        if (prioridad < PRIORIDAD_MINIMA || prioridad > PRIORIDAD_MAXIMA) {
            throw new IllegalArgumentException("La prioridad debe estar entre " + PRIORIDAD_MINIMA + " y " + PRIORIDAD_MAXIMA + ": " + prioridad);
        }
        this.prioridad = prioridad;
    }

    public int getTiempo() {
        return tiempo;
    }

    // Asigna el tiempo estimado validando que sea mayor que cero
    public void setTiempo(int tiempo) {
        if (tiempo <= 0) {
            throw new IllegalArgumentException("El tiempo estimado debe ser mayor que cero: " + tiempo);
        }
        this.tiempo = tiempo;
    }
}
