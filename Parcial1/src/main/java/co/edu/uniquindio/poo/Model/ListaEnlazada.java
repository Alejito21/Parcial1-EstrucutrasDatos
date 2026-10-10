package co.edu.uniquindio.poo.Model;

import java.util.Iterator;
import java.util.Objects;

// Lista simplemente enlazada genérica que cumple el contrato ColeccionPropia y es Iterable
public class ListaEnlazada <T> implements ColeccionPropia<T> {
    private Nodo<T> cabeza;
    private Nodo<T> cola;
    private int tam;

    public ListaEnlazada() {
        this.cabeza = null;
        this.cola = null;
        this.tam = 0;
    }

    // Inserta el elemento al final en O(1) usando la referencia al último nodo
    @Override
    public void agregar(T elemento) {
        Nodo<T> nuevo = new Nodo<>(elemento);
        // Si la lista esta vacía
        if (cabeza == null) {
            cabeza = nuevo;
        } else {
            cola.setSiguiente(nuevo);
        }
        cola = nuevo;
        tam++;
    }

    // Elimina la primera aparición del elemento; retorna false si no existe
    @Override
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
        // Si se eliminó el último, la cola pasa a ser el anterior (null si la lista quedó vacía)
        if (actual == cola) {
            cola = anterior;
        }
        tam--;
        return true;
    }

    // Obtiene el elemento ubicado en la posición dada
    @Override
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
    @Override
    public int tamano(){
        return tam;
    }

    // Saber si esta vacia
    @Override
    public boolean estaVacio() {
        return tam == 0;
    }

    // Retorna el iterador que recorre la lista sin exponer los nodos
    @Override
    public Iterator<T> iterator() {
        return new IteradorLista<>(cabeza);
    }

    // Representación en texto de la lista: [a -> b -> c]
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        for (T valor : this) {
            if (sb.length() > 1) sb.append(" -> ");
            sb.append(valor);
        }
        return sb.append("]").toString();
    }
}
