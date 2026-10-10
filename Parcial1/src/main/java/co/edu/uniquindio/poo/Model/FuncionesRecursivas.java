package co.edu.uniquindio.poo.Model;

import java.util.List;

public class FuncionesRecursivas {

    public FuncionesRecursivas() {}

    // RECURSIVIDAD 1: Peso total de los paquetes de un destino
    public static double pesoTotalDestino(List<Paquete> paquetes, String destino, int indice) {
        if (indice == paquetes.size()) {                                   // caso base
            return 0.0;
        }
        Paquete p = paquetes.get(indice);
        double aporte = p.getDestino().equalsIgnoreCase(destino) ? p.getPeso() : 0.0;
        // Caso recursivo
        return aporte + pesoTotalDestino(paquetes, destino, indice + 1);
    }

    // RECURSIVIDAD 2: Cantidad de paquetes con prioridad >= mínima
    public static int contarPrioridadMinima(List<Paquete> paquetes, int prioridadMinima, int indice) {
        if (indice == paquetes.size()) {                                   // caso base
            return 0;
        }
        int cuenta = paquetes.get(indice).getPrioridad() >= prioridadMinima ? 1 : 0;
        return cuenta + contarPrioridadMinima(paquetes, prioridadMinima, indice + 1);
    }

    // BÚSQUEDA BINARIA por código (la lista debe estar ordenada por código)
    public static int busquedaBinariaPorCodigo(List<Paquete> ordenados, String codigo) {
        int izq = 0;
        int der = ordenados.size() - 1;
        while (izq <= der) {
            int medio = izq + (der - izq) / 2;
            int cmp = ordenados.get(medio).getCodigo().compareTo(codigo);
            if (cmp == 0) {
                return medio;
            } else if (cmp < 0) {
                izq = medio + 1;
            } else {
                der = medio - 1;
            }
        }
        return -1;
    }

    // DIVIDE Y VENCERÁS: Paquete de mayor peso
    public static Paquete mayorPeso(List<Paquete> paquetes) {
        if (paquetes.isEmpty()) {
            throw new IllegalArgumentException("No hay paquetes para analizar");
        }
        return mayorPeso(paquetes, 0, paquetes.size() - 1);
    }

    private static Paquete mayorPeso(List<Paquete> paquetes, int inicio, int fin) {
        if (inicio == fin) {                                               // caso base
            return paquetes.get(inicio);
        }
        int mitad = (inicio + fin) / 2;                                    // dividir
        Paquete izquierdo = mayorPeso(paquetes, inicio, mitad);            // subproblema izquierdo
        Paquete derecho = mayorPeso(paquetes, mitad + 1, fin);             // subproblema derecho
        return derecho.getPeso() > izquierdo.getPeso() ? derecho : izquierdo;   // combinar
    }
}
