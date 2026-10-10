package co.edu.uniquindio.poo.Model;

import java.util.Iterator;
import java.util.NoSuchElementException;

// Iterador de la ListaEnlazada: avanza nodo a nodo sin que el código externo conozca los enlaces
public class IteradorLista <T> implements Iterator<T> {
    private Nodo<T> actual;

    public IteradorLista(Nodo<T> nodo) {
        this.actual = nodo;
    }

    // Indica si quedan elementos por recorrer
    @Override
    public boolean hasNext() {
        return actual != null;
    }

    // Retorna el elemento actual y avanza al siguiente nodo
    @Override
    public T next() {
        if(actual == null){
            throw new NoSuchElementException("No hay más elementos");
        }
        T valor = actual.getValor();
        actual = actual.getSiguiente();
        return valor;
    }
}
