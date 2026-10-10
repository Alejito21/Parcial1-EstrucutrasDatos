package co.edu.uniquindio.poo.Model;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class IteradorLista <T> implements Iterator<T> {
    private Nodo<T> actual;

    public IteradorLista(Nodo<T> nodo) {
        this.actual = nodo;
    }

    @Override
    public boolean hasNext() {
        return actual != null;
    }

    @Override
    public T next() {
        if(actual == null){
            throw new NoSuchElementException("No hay más elementos");
        }
        T valor = actual.getValor();
        actual = actual.getSiguiente();
        return valor;
    }


    public Nodo<T> getActual() {
        return actual;
    }

    public void setActual(Nodo<T> actual) {
        this.actual = actual;
    }
}
