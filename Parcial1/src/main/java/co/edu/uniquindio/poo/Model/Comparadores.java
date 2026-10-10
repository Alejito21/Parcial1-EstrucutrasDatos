package co.edu.uniquindio.poo.Model;

import java.util.Comparator;

// Se tenia previamente otra version de comparadores, esta es la versión que brinda Claude como solución

// Criterios alternativos de comparación de paquetes, independientes del orden natural
public class Comparadores {

    // Clase utilitaria: no se instancia
    private Comparadores() {}

    // Ordena por prioridad de mayor a menor (5 primero, 1 al final)
    public static final Comparator<Paquete> POR_PRIORIDAD_DESC =
            Comparator.comparingInt(Paquete::getPrioridad).reversed();

    // Ordena por peso de mayor a menor
    public static final Comparator<Paquete> POR_PESO_DESC =
            Comparator.comparingDouble(Paquete::getPeso).reversed();

    // Ordena por tiempo estimado de menor a mayor
    public static final Comparator<Paquete> POR_TIEMPO_ASC =
            Comparator.comparingInt(Paquete::getTiempo);

    // Criterio de despacho: mayor prioridad, luego menor tiempo estimado y luego código
    public static final Comparator<Paquete> DESPACHO =
            POR_PRIORIDAD_DESC
                    .thenComparing(POR_TIEMPO_ASC)
                    .thenComparing(Comparator.naturalOrder());
}
