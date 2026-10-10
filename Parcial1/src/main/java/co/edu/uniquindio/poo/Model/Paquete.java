package co.edu.uniquindio.poo.Model;

public class Paquete {
    private String codigo;
    private String destino;
    private float peso;
    private int prioridad;
    private int tiempo;


    //Constructor con pioridad base(5)
    public Paquete(String codigo, String destino, float peso, int tiempo) {
        this.codigo = codigo;
        this.destino = destino;
        this.peso = peso;
        this.prioridad = 5;
        this.tiempo = tiempo;
    }

    //Constructor completo
    public Paquete(String codigo, String destino, float peso, int prioridad, int tiempo) {
        this.codigo = codigo;
        this.destino = destino;
        this.peso = peso;
        this.prioridad = prioridad;
        this.tiempo = tiempo;
    }


    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getDestino() {
        return destino;
    }

    public void setDestino(String destino) {
        this.destino = destino;
    }

    public float getPeso() {
        return peso;
    }

    public void setPeso(float peso) {
        this.peso = peso;
    }

    public int getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(int prioridad) {
        this.prioridad = prioridad;
    }

    public int getTiempo() {
        return tiempo;
    }

    public void setTiempo(int tiempo) {
        this.tiempo = tiempo;
    }
}
