package co.edu.uniquindio.poo.Main;

import co.edu.uniquindio.poo.Model.Comparadores;
import co.edu.uniquindio.poo.Model.ListaEnlazada;
import co.edu.uniquindio.poo.Model.Paquete;
import co.edu.uniquindio.poo.Model.ServicioPaqueteria;
import co.edu.uniquindio.poo.Model.Repartidor;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        ServicioPaqueteria servicio = new ServicioPaqueteria();
        cargarPaquetes(servicio);
        cargarRepartidores(servicio);

        titulo("Paquetes pendientes en orden de llegada");
        servicio.ordenPorLlegada().forEach(p -> System.out.println("  " + p));
        System.out.println("Siguiente por llegada: " + servicio.obtenerSiguientePorLlegada().getCodigo());

        titulo("Consulta por código (HashMap)");
        System.out.println("¿Existe PQ731? " + servicio.existePaquete("PQ731") + " -> " + servicio.buscarPaquete("PQ731"));
        System.out.println("¿Existe PQ000? " + servicio.existePaquete("PQ000"));

        titulo("Municipios");
        System.out.println("Sin repetidos (orden de aparición): " + servicio.getMunicipios());
        System.out.println("Ordenados alfabéticamente:          " + servicio.getMunicipiosOrdenados());

        titulo("Paquetes por municipio");
        for (String municipio : servicio.getMunicipiosOrdenados()) {
            System.out.println("  " + municipio + " -> " + codigos(servicio.obtenerPaquetesPorMunicipio(municipio)));
        }

        titulo("Despacho prioritario (prioridad, tiempo, código)");
        System.out.println("Siguiente a despachar: " + servicio.obtenerSiguientePrioritario());
        System.out.println("Orden completo: " + codigos(servicio.ordenDespachoPrioritario()));

        titulo("Ordenamientos");
        System.out.println("Orden natural (código):  " + codigos(servicio.obtenerPaquetesOrdenadosPorCodigo()));
        System.out.println("Prioridad (mayor-menor): " + codigos(servicio.obtenerPaquetesOrdenados(Comparadores.POR_PRIORIDAD_DESC)));
        System.out.println("Peso (mayor-menor):      " + codigos(servicio.obtenerPaquetesOrdenados(Comparadores.POR_PESO_DESC)));
        System.out.println("Tiempo (menor-mayor):    " + codigos(servicio.obtenerPaquetesOrdenados(Comparadores.POR_TIEMPO_ASC)));

        titulo("Recursividad");
        System.out.println("Peso total hacia Armenia: " + servicio.pesoTotalDestino("Armenia") + " kg");
        System.out.println("Paquetes con prioridad >= 4: " + servicio.contarPrioridadMinima(4));

        titulo("Búsqueda binaria (sobre la lista ordenada por código)");
        List<Paquete> ordenados = servicio.obtenerPaquetesOrdenadosPorCodigo();
        System.out.println("Posición de PQ731: " + servicio.busquedaBinariaPorCodigo(ordenados, "PQ731"));
        System.out.println("Posición de PQ000: " + servicio.busquedaBinariaPorCodigo(ordenados, "PQ000"));

        titulo("Divide y vencerás: paquete de mayor peso");
        System.out.println(servicio.mayorPeso());

        titulo("Asignación y entregas");
        System.out.println("Asignar PQ105 a R02 (Salento, disponible): " + servicio.asignarPaquete("PQ105", "R02"));
        System.out.println("Asignar PQ567 a R02 (ya no disponible):    " + servicio.asignarPaquete("PQ567", "R02"));
        System.out.println("Asignar PQ942 a R07 (no disponible):       " + servicio.asignarPaquete("PQ942", "R07"));
        System.out.println("Asignar PQ942 a R03 (zona distinta):       " + servicio.asignarPaquete("PQ942", "R03"));
        System.out.println("Asignar PQ942 a R01 (Armenia, disponible): " + servicio.asignarPaquete("PQ942", "R01"));
        System.out.println("Entregar PQ105: " + servicio.registrarEntrega("PQ105"));
        System.out.println("Entregar PQ942: " + servicio.registrarEntrega("PQ942"));

        // R01 vuelve a estar disponible tras entregar PQ942
        Repartidor libre = servicio.buscarRepartidorDisponible("Armenia");
        System.out.println("Repartidor disponible en Armenia: " + libre);
        System.out.println("Asignar PQ731 a " + libre.getId() + ": " + servicio.asignarPaquete("PQ731", libre.getId()));
        System.out.println("Entregar PQ731: " + servicio.registrarEntrega("PQ731"));
        System.out.println("Entregar PQ318 (sin asignación): " + servicio.registrarEntrega("PQ318"));

        titulo("Historial de entregas (lista enlazada propia, recorrida con Iterator)");
        StringBuilder historial = new StringBuilder();
        for (Paquete p : servicio.getHistorialEntregas()) {
            if (historial.length() > 0) historial.append(" -> ");
            historial.append(p.getCodigo());
        }
        System.out.println(historial);
        System.out.println("Primera entrega (obtener(0)): " + servicio.getHistorialEntregas().obtener(0).getCodigo());
        System.out.println("Cantidad de entregas: " + servicio.getHistorialEntregas().tamano());
        System.out.println("Siguiente prioritario después de las entregas: " + servicio.obtenerSiguientePrioritario());

        demostrarValidaciones(servicio);
        demostrarListaEnlazada();
    }

    // Muestra que el sistema rechaza códigos e identificaciones repetidas y datos inválidos
    private static void demostrarValidaciones(ServicioPaqueteria servicio) {
        titulo("Validaciones");
        System.out.println("Registrar PQ731 otra vez: " + servicio.registrarPaquete(new Paquete("PQ731", "Salento", 1.0, 10)));
        System.out.println("Registrar repartidor R01 otra vez: " + servicio.registrarRepartidor(new Repartidor("R01", "Otro Nombre", "Armenia", true)));
        intentar("Peso 0", () -> new Paquete("PQ001", "Armenia", 0, 3, 10));
        intentar("Prioridad 6", () -> new Paquete("PQ002", "Armenia", 2.0, 6, 10));
        intentar("Prioridad 0", () -> new Paquete("PQ003", "Armenia", 2.0, 0, 10));
        intentar("Tiempo -5", () -> new Paquete("PQ004", "Armenia", 2.0, 3, -5));
        intentar("Destino vacío", () -> new Paquete("PQ005", " ", 2.0, 3, 10));
        intentar("Destino nulo", () -> new Paquete("PQ006", null, 2.0, 3, 10));
        System.out.println("Prioridad por defecto: " + new Paquete("PQ007", "Armenia", 2.0, 10).getPrioridad());
    }

    // Prueba la lista enlazada propia con otro tipo de dato y en sus casos borde
    private static void demostrarListaEnlazada() {
        titulo("Lista enlazada propia (genérica, con String)");
        ListaEnlazada<String> lista = new ListaEnlazada<>();
        System.out.println("Vacía: " + lista + " tamaño=" + lista.tamano() + " estaVacio=" + lista.estaVacio());
        System.out.println("Eliminar en lista vacía: " + lista.eliminar("A"));

        lista.agregar("A");
        System.out.println("Un elemento: " + lista + " obtener(0)=" + lista.obtener(0));
        System.out.println("Eliminar el único: " + lista.eliminar("A") + " -> " + lista + " estaVacio=" + lista.estaVacio());

        lista.agregar("A");
        lista.agregar("B");
        lista.agregar("C");
        lista.agregar("D");
        System.out.println("Lista: " + lista);
        System.out.println("Eliminar primero (A): " + lista.eliminar("A") + " -> " + lista);
        System.out.println("Eliminar intermedio (C): " + lista.eliminar("C") + " -> " + lista);
        System.out.println("Eliminar último (D): " + lista.eliminar("D") + " -> " + lista);
        lista.agregar("E");
        System.out.println("Agregar después de eliminar el último: " + lista);
        System.out.println("Eliminar inexistente (Z): " + lista.eliminar("Z") + " -> " + lista);
        intentar("obtener(5)", () -> lista.obtener(5));
    }

    // Ejecuta una acción que debe fallar e imprime el mensaje de error
    private static void intentar(String descripcion, Runnable accion) {
        try {
            accion.run();
            System.out.println(descripcion + ": aceptado");
        } catch (IllegalArgumentException | IndexOutOfBoundsException e) {
            System.out.println(descripcion + ": rechazado -> " + e.getMessage());
        }
    }

    // Registra los 30 paquetes de la jornada (6 destinos, códigos no consecutivos)
    public static void cargarPaquetes(ServicioPaqueteria servicio) {
        servicio.registrarPaquete(new Paquete("PQ731", "Armenia", 4.5, 5, 45));
        servicio.registrarPaquete(new Paquete("PQ105", "Salento", 3.0, 5, 25));
        servicio.registrarPaquete(new Paquete("PQ942", "Armenia", 7.2, 4, 20));
        servicio.registrarPaquete(new Paquete("PQ318", "Calarcá", 12.0, 2, 35));
        servicio.registrarPaquete(new Paquete("PQ567", "Salento", 2.3, 1, 50));
        servicio.registrarPaquete(new Paquete("PQ829", "Armenia", 18.7, 3, 15));
        servicio.registrarPaquete(new Paquete("PQ420", "Montenegro", 5.1, 30));
        servicio.registrarPaquete(new Paquete("PQ214", "Quimbaya", 9.8, 4, 60));
        servicio.registrarPaquete(new Paquete("PQ663", "Circasia", 1.2, 2, 40));
        servicio.registrarPaquete(new Paquete("PQ087", "Calarcá", 6.4, 5, 30));
        servicio.registrarPaquete(new Paquete("PQ509", "Montenegro", 3.7, 1, 55));
        servicio.registrarPaquete(new Paquete("PQ842", "Quimbaya", 14.3, 3, 65));
        servicio.registrarPaquete(new Paquete("PQ376", "Armenia", 2.9, 4, 10));
        servicio.registrarPaquete(new Paquete("PQ158", "Circasia", 8.0, 25));
        servicio.registrarPaquete(new Paquete("PQ921", "Salento", 5.5, 4, 45));
        servicio.registrarPaquete(new Paquete("PQ043", "Calarcá", 10.6, 2, 20));
        servicio.registrarPaquete(new Paquete("PQ695", "Armenia", 0.8, 5, 15));
        servicio.registrarPaquete(new Paquete("PQ271", "Quimbaya", 4.2, 1, 70));
        servicio.registrarPaquete(new Paquete("PQ454", "Montenegro", 11.1, 5, 40));
        servicio.registrarPaquete(new Paquete("PQ788", "Circasia", 6.9, 3, 35));
        servicio.registrarPaquete(new Paquete("PQ132", "Armenia", 15.4, 2, 25));
        servicio.registrarPaquete(new Paquete("PQ615", "Salento", 3.3, 3, 50));
        servicio.registrarPaquete(new Paquete("PQ999", "Calarcá", 7.7, 4, 30));
        servicio.registrarPaquete(new Paquete("PQ302", "Montenegro", 2.1, 2, 45));
        servicio.registrarPaquete(new Paquete("PQ846", "Quimbaya", 9.0, 5, 60));
        servicio.registrarPaquete(new Paquete("PQ027", "Circasia", 4.8, 1, 20));
        servicio.registrarPaquete(new Paquete("PQ573", "Armenia", 13.2, 3, 30));
        servicio.registrarPaquete(new Paquete("PQ719", "Salento", 1.9, 2, 55));
        servicio.registrarPaquete(new Paquete("PQ484", "Calarcá", 5.6, 3, 25));
        servicio.registrarPaquete(new Paquete("PQ236", "Quimbaya", 8.4, 4, 50));
    }

    // Registra 7 repartidores (uno por zona y uno adicional no disponible en Armenia)
    public static void cargarRepartidores(ServicioPaqueteria servicio) {
        servicio.registrarRepartidor(new Repartidor("R01", "Laura Gómez Ríos", "Armenia", true));
        servicio.registrarRepartidor(new Repartidor("R02", "Carlos Mejía Ortiz", "Salento", true));
        servicio.registrarRepartidor(new Repartidor("R03", "Andrea López Cano", "Calarcá", true));
        servicio.registrarRepartidor(new Repartidor("R04", "Julián Torres Vélez", "Montenegro", true));
        servicio.registrarRepartidor(new Repartidor("R05", "Paula Henao Ruiz", "Quimbaya", true));
        servicio.registrarRepartidor(new Repartidor("R06", "Mateo Arango Gil", "Circasia", true));
        servicio.registrarRepartidor(new Repartidor("R07", "Sofía Castaño Paz", "Armenia", false));
    }

    // Imprime un encabezado de sección
    private static void titulo(String texto) {
        System.out.println();
        System.out.println("=== " + texto + " ===");
    }

    // Devuelve solo los códigos de una lista de paquetes, para imprimir de forma compacta
    private static List<String> codigos(List<Paquete> paquetes) {
        return paquetes.stream().map(Paquete::getCodigo).toList();
    }
}
