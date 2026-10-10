package co.edu.uniquindio.poo.Model;

// Contrato común para las estructuras internas de QuindíoExpress: define qué operaciones ofrecen
public interface ColeccionPropia<T> extends Iterable<T> {

    // Agrega un elemento a la colección
    void agregar(T elemento);

    // Elimina la primera aparición del elemento; retorna false si no existe
    boolean eliminar(T elemento);

    // Obtiene el elemento ubicado en la posición dada
    T obtener(int posicion);

    // Cantidad de elementos almacenados
    int tamano();

    // Indica si la colección no tiene elementos
    boolean estaVacio();
}
 