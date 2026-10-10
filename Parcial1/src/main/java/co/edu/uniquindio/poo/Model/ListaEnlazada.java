package co.edu.uniquindio.poo.Model;

import java.util.Iterator;
import java.util.Objects;

//Creamos la Lista Enlazada que sea Iterable
public class ListaEnlazada <T> implements Iterable<T> {
    private Nodo<T> cabeza;
    private int tam;

    public ListaEnlazada() {
        this.cabeza = null;
        this.tam = 0;
    }

    //Insertar el elemento al final
    public void agregar(T elemento) {
        Nodo<T> nuevo = new Nodo<>(elemento);
        // Si la lista esta vacía
        if (cabeza == null) {
            cabeza = nuevo;
        } else {
            Nodo<T> ultimo = cabeza;
            while (ultimo.getSiguiente() != null) {
                ultimo = ultimo.getSiguiente();
            }
            ultimo.setSiguiente(nuevo);
        }
        tam++;
    }

    // Eliminar el elemento
    public boolean eliminar(T elemento) {
        Nodo<T> anterior = null;
        Nodo<T> actual = cabeza;

        while (actual != null && !Objects.equals(actual.getValor(), elemento)) {
            anterior = actual;
            actual = actual.getSiguiente();
        }
        //Lista vacía o el elemetno no existe
        if (actual == null) {
            return false;
        }

        // Eliminar el primer elemento
        if (anterior == null) {
            cabeza = actual.getSiguiente();

        // Eliminar elemento intermedio o el ultimo
        } else {
            anterior.setSiguiente(actual.getSiguiente());
        }
        tam--;
        return true;
    }

    // Obtener elemento en base a la posicion dada
    public T obtener(int posicion) {
        if (posicion < 0 || posicion >= tam) {
            throw new IndexOutOfBoundsException(" La Posición " + posicion + " esta fuera del rango [0, " + tam + ")");
        }
        Nodo<T> actual = cabeza;
        for (int i = 0; i < posicion; i++) {
            actual = actual.getSiguiente();
        }
        return actual.getValor();
    }

    //Consultar tamano de la lista
    public int tamano(){
        return tam;
    }

    // Saber si esta vacia
    public boolean estaVacio() {
        return tam == 0;
    }

    // Metodo al implementar Iterable
    @Override
    public Iterator<T> iterator() {
        return new IteradorLista<>(cabeza) {
        };
    }

    public Nodo<T> getCabeza() {
        return cabeza;
    }

    public void setCabeza(Nodo<T> cabeza) {
        this.cabeza = cabeza;
    }

    public int getTam() {
        return tam;
    }

    public void setTam(int tam) {
        this.tam = tam;
    }
}
