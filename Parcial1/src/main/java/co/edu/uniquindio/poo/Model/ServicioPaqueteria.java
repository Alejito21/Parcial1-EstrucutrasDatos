package co.edu.uniquindio.poo.Model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.TreeSet;

// Clase central del sistema: alberga las estructuras de datos y las operaciones de la jornada
public class ServicioPaqueteria {

    // Registro completo de la jornada en orden de llegada (auditoría; incluye los ya entregados)
    private final ArrayList<Paquete> registroJornada;

    // Necesidad 1: paquetes pendientes en el orden exacto en que fueron registrados
    private final LinkedHashSet<Paquete> paquetesPendientes;

    // Necesidades 2 y 7: consulta rápida y asociación código -> Paquete
    private final HashMap<String, Paquete> paquetesPorCodigo;

    // Necesidad 3: municipios destino sin duplicados (en orden de aparición)
    private final LinkedHashSet<String> municipios;

    // Necesidad 4: municipios ordenados alfabéticamente y sin duplicados
    private final TreeSet<String> municipiosOrdenados;

    // Necesidad 5: atención por mayor prioridad (prioridad, tiempo estimado, código);
    // los entregados se descartan de forma perezosa al llegar a la cima
    private final PriorityQueue<Paquete> colaPrioridad;

    // Necesidad 6: historial de entregas en orden real de entrega (lista enlazada propia)
    private final ListaEnlazada<Paquete> historialEntregas;

    // Agrupación municipio -> paquetes destinados a ese municipio
    private final HashMap<String, List<Paquete>> paquetesPorMunicipio;

    // Repartidores indexados por su identificación
    private final HashMap<String, Repartidor> repartidores;

    // Asignaciones vigentes: código del paquete -> repartidor que lo lleva
    private final HashMap<String, Repartidor> asignaciones;

    public ServicioPaqueteria() {
        this.registroJornada = new ArrayList<>();
        this.paquetesPendientes = new LinkedHashSet<>();
        this.paquetesPorCodigo = new HashMap<>();
        this.municipios = new LinkedHashSet<>();
        this.municipiosOrdenados = new TreeSet<>();
        this.colaPrioridad = new PriorityQueue<>(Comparadores.DESPACHO);
        this.historialEntregas = new ListaEnlazada<>();
        this.paquetesPorMunicipio = new HashMap<>();
        this.repartidores = new HashMap<>();
        this.asignaciones = new HashMap<>();
    }

    // ===================== REGISTRO Y CONSULTA DE PAQUETES =====================

    // Registra un paquete en todas las estructuras; retorna false si su código ya estaba registrado
    public boolean registrarPaquete(Paquete paquete) {
        if (paquete == null) {
            throw new IllegalArgumentException("El paquete no puede ser nulo");
        }
        if (paquetesPorCodigo.containsKey(paquete.getCodigo())) {
            return false;
        }
        registroJornada.add(paquete);
        paquetesPendientes.add(paquete);
        paquetesPorCodigo.put(paquete.getCodigo(), paquete);
        municipios.add(paquete.getDestino());
        municipiosOrdenados.add(paquete.getDestino());
        colaPrioridad.offer(paquete);
        paquetesPorMunicipio
                .computeIfAbsent(paquete.getDestino(), m -> new ArrayList<>())
                .add(paquete);
        return true;
    }

    // Indica si existe un paquete registrado con el código dado
    public boolean existePaquete(String codigo) {
        return paquetesPorCodigo.containsKey(codigo);
    }

    // Busca un paquete por su código usando el HashMap
    public Paquete buscarPaquete(String codigo) {
        return paquetesPorCodigo.get(codigo);
    }

    // Todos los paquetes registrados en la jornada, en orden de llegada
    public List<Paquete> obtenerRegistroJornada() {
        return Collections.unmodifiableList(registroJornada);
    }

    // ===================== REPARTIDORES =====================

    // Registra un repartidor; retorna false si su identificación ya estaba registrada
    public boolean registrarRepartidor(Repartidor repartidor) {
        if (repartidor == null) {
            throw new IllegalArgumentException("El repartidor no puede ser nulo");
        }
        if (repartidores.containsKey(repartidor.getId())) {
            return false;
        }
        repartidores.put(repartidor.getId(), repartidor);
        return true;
    }

    // Busca un repartidor por su identificación
    public Repartidor buscarRepartidor(String id) {
        return repartidores.get(id);
    }

    // Todos los repartidores registrados (solo lectura)
    public Collection<Repartidor> obtenerRepartidores() {
        return Collections.unmodifiableCollection(repartidores.values());
    }

    // Busca el primer repartidor disponible cuya zona coincide con el municipio dado
    public Repartidor buscarRepartidorDisponible(String zona) {
        for (Repartidor r : repartidores.values()) {
            if (r.isDisponible() && r.getZona().equalsIgnoreCase(zona)) {
                return r;
            }
        }
        return null;
    }

    // Asigna un paquete pendiente a un repartidor disponible de su misma zona
    public boolean asignarPaquete(String codigo, String idRepartidor) {
        Paquete paquete = paquetesPorCodigo.get(codigo);
        Repartidor repartidor = repartidores.get(idRepartidor);
        if (paquete == null || repartidor == null) {
            return false;
        }
        if (!paquetesPendientes.contains(paquete) || asignaciones.containsKey(codigo)) {
            return false;
        }
        if (!repartidor.isDisponible()) {
            return false;
        }
        if (!repartidor.getZona().equalsIgnoreCase(paquete.getDestino())) {
            return false;
        }
        repartidor.setDisponible(false);
        asignaciones.put(codigo, repartidor);
        return true;
    }

    // Devuelve el repartidor asignado actualmente a un paquete
    public Repartidor obtenerRepartidorAsignado(String codigo) {
        return asignaciones.get(codigo);
    }

    // ===================== ENTREGAS E HISTORIAL =====================

    // Registra la entrega de un paquete asignado y lo agrega al historial
    public boolean registrarEntrega(String codigo) {
        Repartidor repartidor = asignaciones.remove(codigo);
        if (repartidor == null) {
            return false;
        }
        Paquete paquete = paquetesPorCodigo.get(codigo);
        paquetesPendientes.remove(paquete);
        historialEntregas.agregar(paquete);
        repartidor.setDisponible(true);
        return true;
    }

    // Historial de entregas en el orden real en que ocurrieron
    public ListaEnlazada<Paquete> getHistorialEntregas() {
        return historialEntregas;
    }

    // ===================== MUNICIPIOS Y AGRUPACIÓN =====================

    // Municipios con al menos un paquete registrado, sin repetidos, en orden de aparición
    public Set<String> getMunicipios() {
        return Collections.unmodifiableSet(municipios);
    }

    // Municipios con al menos un paquete registrado, sin repetidos y en orden alfabético
    public Set<String> getMunicipiosOrdenados() {
        return Collections.unmodifiableSet(municipiosOrdenados);
    }

    // Paquetes destinados a un municipio, en orden de registro
    public List<Paquete> obtenerPaquetesPorMunicipio(String municipio) {
        return Collections.unmodifiableList(paquetesPorMunicipio.getOrDefault(municipio, new ArrayList<>()));
    }

    // ===================== ATENCIÓN POR LLEGADA Y POR PRIORIDAD =====================

    // Primer paquete pendiente según el orden de llegada (el primero registrado)
    public Paquete obtenerSiguientePorLlegada() {
        Iterator<Paquete> it = paquetesPendientes.iterator();
        return it.hasNext() ? it.next() : null;
    }

    // Paquetes pendientes en el orden exacto de registro
    public List<Paquete> ordenPorLlegada() {
        return new ArrayList<>(paquetesPendientes);
    }

    // Siguiente paquete que debe despacharse según prioridad, tiempo estimado y código
    public Paquete obtenerSiguientePrioritario() {
        limpiarCimaPrioridad();
        return colaPrioridad.peek();
    }

    // Descarta de la cima de la cola los paquetes que ya no están pendientes (eliminación perezosa)
    private void limpiarCimaPrioridad() {
        while (!colaPrioridad.isEmpty() && !paquetesPendientes.contains(colaPrioridad.peek())) {
            colaPrioridad.poll();
        }
    }

    // Orden completo de despacho prioritario sin alterar la cola original
    public List<Paquete> ordenDespachoPrioritario() {
        PriorityQueue<Paquete> copia = new PriorityQueue<>(colaPrioridad);
        List<Paquete> orden = new ArrayList<>();
        while (!copia.isEmpty()) {
            Paquete p = copia.poll();
            if (paquetesPendientes.contains(p)) {
                orden.add(p);
            }
        }
        return orden;
    }

    // ===================== ORDENAMIENTOS =====================

    // Todos los paquetes registrados ordenados por su orden natural (código ascendente)
    public List<Paquete> obtenerPaquetesOrdenadosPorCodigo() {
        List<Paquete> lista = new ArrayList<>(registroJornada);
        Collections.sort(lista);
        return lista;
    }

    // Todos los paquetes registrados ordenados según el criterio (Comparator) recibido
    public List<Paquete> obtenerPaquetesOrdenados(Comparator<Paquete> criterio) {
        List<Paquete> lista = new ArrayList<>(registroJornada);
        lista.sort(criterio);
        return lista;
    }

    // ===================== RECURSIVIDAD =====================

    // Peso total de los paquetes registrados con destino al municipio dado (recorre en orden de llegada)
    public double pesoTotalDestino(String destino) {
        return pesoTotalDestino(registroJornada, destino, 0);
    }

    // FUNCIÓN RECURSIVA
    private double pesoTotalDestino(List<Paquete> paquetes, String destino, int indice) {
        if (indice == paquetes.size()) {
            return 0.0;
        }
        Paquete p = paquetes.get(indice);
        double aporte = p.getDestino().equalsIgnoreCase(destino) ? p.getPeso() : 0.0;
        return aporte + pesoTotalDestino(paquetes, destino, indice + 1);
    }

    // Cantidad de paquetes registrados con prioridad mayor o igual a la mínima (recorre en orden de llegada)
    public int contarPrioridadMinima(int prioridadMinima) {
        return contarPrioridadMinima(registroJornada, prioridadMinima, 0);
    }

    // FUNCIÓN RECURSIVA
    private int contarPrioridadMinima(List<Paquete> paquetes, int prioridadMinima, int indice) {
        if (indice == paquetes.size()) {
            return 0;
        }
        int cuenta = paquetes.get(indice).getPrioridad() >= prioridadMinima ? 1 : 0;
        return cuenta + contarPrioridadMinima(paquetes, prioridadMinima, indice + 1);
    }

    // ===================== BÚSQUEDA BINARIA =====================

    // Búsqueda binaria por código (requiere la lista ordenada por código)
    public int busquedaBinariaPorCodigo(List<Paquete> ordenados, String codigo) {
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

    // ===================== DIVIDE Y VENCERÁS =====================

    // Paquete de mayor peso entre todos los registrados, usando divide y vencerás
    public Paquete mayorPeso() {
        return mayorPeso(registroJornada);
    }

    // Paquete de mayor peso dentro de la lista dada, usando divide y vencerás
    public Paquete mayorPeso(List<Paquete> paquetes) {
        if (paquetes.isEmpty()) {
            throw new IllegalArgumentException("No hay paquetes para analizar");
        }
        return mayorPeso(paquetes, 0, paquetes.size() - 1);
    }

    // FUNCIÓN RECURSIVA
    private Paquete mayorPeso(List<Paquete> paquetes, int inicio, int fin) {
        if (inicio == fin) {                                               // caso base
            return paquetes.get(inicio);
        }
        int mitad = (inicio + fin) / 2;                                    // dividir
        Paquete izquierdo = mayorPeso(paquetes, inicio, mitad);            // subproblema izquierdo
        Paquete derecho = mayorPeso(paquetes, mitad + 1, fin);             // subproblema derecho
        return derecho.getPeso() > izquierdo.getPeso() ? derecho : izquierdo;   // combinar
    }
}
