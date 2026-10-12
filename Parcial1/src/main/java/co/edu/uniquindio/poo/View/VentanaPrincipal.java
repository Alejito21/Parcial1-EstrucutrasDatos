package co.edu.uniquindio.poo.View;

import co.edu.uniquindio.poo.Main.Main;
import co.edu.uniquindio.poo.Model.Comparadores;
import co.edu.uniquindio.poo.Model.ListaEnlazada;
import co.edu.uniquindio.poo.Model.Paquete;
import co.edu.uniquindio.poo.Model.Repartidor;
import co.edu.uniquindio.poo.Model.ServicioPaqueteria;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

// Interfaz gráfica para probar de forma interactiva las operaciones del sistema durante la sustentación
public class VentanaPrincipal extends JFrame {

    private static final String[] COLUMNAS_PAQUETE = {"Código", "Destino", "Peso (kg)", "Prioridad", "Tiempo (min)", "Estado"};

    private ServicioPaqueteria servicio;

    // Lista propia con String para probar sus casos borde en la pestaña "Lista enlazada"
    private ListaEnlazada<String> listaDemo = new ListaEnlazada<>();

    // Acciones que vuelven a leer el servicio y actualizan cada pestaña
    private final List<Runnable> refrescos = new ArrayList<>();

    // Consola inferior donde queda cada método llamado y su resultado
    private final JTextArea consola = new JTextArea();

    public VentanaPrincipal() {
        super("QuindíoExpress - Gestión de paquetes");
        servicio = crearServicio();

        JTabbedPane pestanas = new JTabbedPane();
        pestanas.addTab("Paquetes", panelPaquetes());
        pestanas.addTab("Repartidores", panelRepartidores());
        pestanas.addTab("Asignación y entregas", panelEntregas());
        pestanas.addTab("Atención", panelAtencion());
        pestanas.addTab("Municipios", panelMunicipios());
        pestanas.addTab("Ordenamientos", panelOrdenamientos());
        pestanas.addTab("Algoritmos", panelAlgoritmos());
        pestanas.addTab("Lista enlazada", panelListaEnlazada());

        JSplitPane division = new JSplitPane(JSplitPane.VERTICAL_SPLIT, pestanas, panelConsola());
        division.setResizeWeight(0.75);
        add(division);

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);
        refrescar();
        log("Jornada cargada: " + servicio.obtenerRegistroJornada().size() + " paquetes y "
                + servicio.obtenerRepartidores().size() + " repartidores");
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Si falla se usa el aspecto por defecto de Swing
        }
        SwingUtilities.invokeLater(() -> new VentanaPrincipal().setVisible(true));
    }

    // Crea el servicio con los mismos 30 paquetes y 7 repartidores que usa Main
    private static ServicioPaqueteria crearServicio() {
        ServicioPaqueteria s = new ServicioPaqueteria();
        Main.cargarPaquetes(s);
        Main.cargarRepartidores(s);
        return s;
    }

    // Vuelve a leer el servicio y actualiza todas las pestañas
    private void refrescar() {
        refrescos.forEach(Runnable::run);
    }

    // ===================== CONSOLA =====================

    // Consola de operaciones con los botones para limpiarla y reiniciar la jornada
    private JPanel panelConsola() {
        consola.setEditable(false);
        consola.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        JButton limpiar = boton("Limpiar consola", e -> consola.setText(""));
        JButton reiniciar = boton("Reiniciar jornada", e -> {
            servicio = crearServicio();
            refrescar();
            log("--- Jornada reiniciada con los datos iniciales ---");
        });
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Consola de operaciones"));
        panel.add(new JScrollPane(consola), BorderLayout.CENTER);
        panel.add(fila(limpiar, reiniciar), BorderLayout.SOUTH);
        return panel;
    }

    // ===================== PAQUETES (necesidades 1, 2 y 7) =====================

    // Registro de paquetes y consulta por código
    private JPanel panelPaquetes() {
        DefaultTableModel modelo = modeloTabla(COLUMNAS_PAQUETE);
        JTable tabla = new JTable(modelo);
        JLabel resumen = new JLabel();

        JTextField codigo = new JTextField(6);
        JComboBox<String> destino = new JComboBox<>();
        destino.setEditable(true);
        JTextField peso = new JTextField(4);
        JComboBox<String> prioridad = new JComboBox<>(new String[]{"Por defecto (3)", "1", "2", "3", "4", "5"});
        JTextField tiempo = new JTextField(4);

        JButton registrar = boton("Registrar", e -> {
            try {
                String c = codigo.getText().trim();
                String d = String.valueOf(destino.getEditor().getItem()).trim();
                double p = Double.parseDouble(peso.getText().trim());
                int t = Integer.parseInt(tiempo.getText().trim());
                Paquete nuevo = prioridad.getSelectedIndex() == 0
                        ? new Paquete(c, d, p, t)
                        : new Paquete(c, d, p, Integer.parseInt((String) prioridad.getSelectedItem()), t);
                boolean ok = servicio.registrarPaquete(nuevo);
                log("registrarPaquete(" + nuevo + ") -> " + ok + (ok ? "" : "  (el código ya estaba registrado)"));
                refrescar();
                seleccionarFila(tabla, c);
            } catch (NumberFormatException ex) {
                aviso("El peso y el tiempo deben ser números (use punto decimal)");
            } catch (IllegalArgumentException ex) {
                aviso(ex.getMessage());
            }
        });

        JTextField buscar = new JTextField(6);
        JLabel resultado = new JLabel(" ");
        JButton botonBuscar = boton("Buscar", e -> {
            String c = buscar.getText().trim();
            boolean existe = servicio.existePaquete(c);
            Paquete p = servicio.buscarPaquete(c);
            log("existePaquete(\"" + c + "\") -> " + existe);
            log("buscarPaquete(\"" + c + "\") -> " + p);
            resultado.setText(p == null ? "No existe un paquete con código " + c : p.toString());
            seleccionarFila(tabla, c);
        });

        refrescos.add(() -> {
            llenarTablaPaquetes(modelo, servicio.obtenerRegistroJornada());
            recargar(destino, servicio.getMunicipiosOrdenados());
            resumen.setText("Registrados: " + servicio.obtenerRegistroJornada().size()
                    + "   |   Pendientes: " + servicio.ordenPorLlegada().size()
                    + "   |   Entregados: " + servicio.getHistorialEntregas().tamano());
        });

        JPanel arriba = columna(
                titulado("Registrar paquete  —  registrarPaquete(paquete)",
                        fila(new JLabel("Código"), codigo, new JLabel("Destino"), destino, new JLabel("Peso"), peso,
                                new JLabel("Prioridad"), prioridad, new JLabel("Tiempo"), tiempo, registrar)),
                titulado("Consultar por código (HashMap)  —  existePaquete(codigo) / buscarPaquete(codigo)",
                        fila(new JLabel("Código"), buscar, botonBuscar, resultado)));

        JPanel centro = titulado("Registro de la jornada  —  obtenerRegistroJornada()", new JScrollPane(tabla));
        centro.add(resumen, BorderLayout.SOUTH);
        return bordes(arriba, centro);
    }

    // ===================== REPARTIDORES =====================

    // Registro y búsqueda de repartidores
    private JPanel panelRepartidores() {
        DefaultTableModel modelo = modeloTabla("ID", "Nombre", "Zona", "Disponible");
        JTable tabla = new JTable(modelo);

        JTextField id = new JTextField(4);
        JTextField nombre = new JTextField(12);
        JComboBox<String> zona = new JComboBox<>();
        zona.setEditable(true);
        JCheckBox disponible = new JCheckBox("Disponible", true);

        JButton registrar = boton("Registrar", e -> {
            try {
                Repartidor r = new Repartidor(id.getText().trim(), nombre.getText().trim(),
                        String.valueOf(zona.getEditor().getItem()).trim(), disponible.isSelected());
                boolean ok = servicio.registrarRepartidor(r);
                log("registrarRepartidor(" + r + ") -> " + ok + (ok ? "" : "  (la identificación ya estaba registrada)"));
                refrescar();
                seleccionarFila(tabla, r.getId());
            } catch (IllegalArgumentException ex) {
                aviso(ex.getMessage());
            }
        });

        JTextField buscarId = new JTextField(4);
        JButton botonBuscarId = boton("Buscar", e -> {
            String i = buscarId.getText().trim();
            log("buscarRepartidor(\"" + i + "\") -> " + servicio.buscarRepartidor(i));
            seleccionarFila(tabla, i);
        });

        JComboBox<String> zonaBuscar = new JComboBox<>();
        JButton botonDisponible = boton("Buscar disponible", e -> {
            String z = (String) zonaBuscar.getSelectedItem();
            Repartidor r = servicio.buscarRepartidorDisponible(z);
            log("buscarRepartidorDisponible(\"" + z + "\") -> " + r);
            if (r != null) {
                seleccionarFila(tabla, r.getId());
            }
        });

        refrescos.add(() -> {
            modelo.setRowCount(0);
            for (Repartidor r : repartidoresOrdenados()) {
                modelo.addRow(new Object[]{r.getId(), r.getNombre(), r.getZona(), r.isDisponible() ? "Sí" : "No"});
            }
            recargar(zona, servicio.getMunicipiosOrdenados());
            recargar(zonaBuscar, servicio.getMunicipiosOrdenados());
        });

        JPanel arriba = columna(
                titulado("Registrar repartidor  —  registrarRepartidor(repartidor)",
                        fila(new JLabel("ID"), id, new JLabel("Nombre"), nombre, new JLabel("Zona"), zona, disponible, registrar)),
                titulado("Buscar  —  buscarRepartidor(id) / buscarRepartidorDisponible(zona)",
                        fila(new JLabel("ID"), buscarId, botonBuscarId, Box.createHorizontalStrut(30),
                                new JLabel("Zona"), zonaBuscar, botonDisponible)));
        return bordes(arriba, titulado("Repartidores registrados", new JScrollPane(tabla)));
    }

    // ===================== ASIGNACIÓN, ENTREGAS E HISTORIAL (necesidad 6) =====================

    // Asignación de paquetes, registro de entregas e historial en la lista enlazada propia
    private JPanel panelEntregas() {
        JComboBox<Paquete> paqueteAsignar = new JComboBox<>();
        JComboBox<Repartidor> repartidor = new JComboBox<>();
        JButton asignar = boton("Asignar", e -> {
            Paquete p = (Paquete) paqueteAsignar.getSelectedItem();
            Repartidor r = (Repartidor) repartidor.getSelectedItem();
            if (p == null || r == null) {
                return;
            }
            String motivo = motivoRechazo(p, r);
            boolean ok = servicio.asignarPaquete(p.getCodigo(), r.getId());
            log("asignarPaquete(\"" + p.getCodigo() + "\", \"" + r.getId() + "\") -> " + ok + (ok ? "" : "  (" + motivo + ")"));
            refrescar();
        });

        JComboBox<Paquete> paqueteEntregar = new JComboBox<>();
        JButton entregar = boton("Registrar entrega", e -> {
            Paquete p = (Paquete) paqueteEntregar.getSelectedItem();
            if (p == null) {
                return;
            }
            Repartidor r = servicio.obtenerRepartidorAsignado(p.getCodigo());
            boolean ok = servicio.registrarEntrega(p.getCodigo());
            log("registrarEntrega(\"" + p.getCodigo() + "\") -> " + ok
                    + (ok ? "  (" + r.getId() + " vuelve a estar disponible)" : "  (el paquete no tiene repartidor asignado)"));
            refrescar();
        });

        DefaultListModel<String> asignaciones = new DefaultListModel<>();
        DefaultListModel<String> historial = new DefaultListModel<>();
        JLabel tamano = new JLabel();
        JSpinner posicion = new JSpinner(new SpinnerNumberModel(0, -5, 99, 1));
        JButton obtener = boton("obtener(posición)", e -> {
            int i = (Integer) posicion.getValue();
            try {
                log("getHistorialEntregas().obtener(" + i + ") -> " + servicio.getHistorialEntregas().obtener(i));
            } catch (IndexOutOfBoundsException ex) {
                log("getHistorialEntregas().obtener(" + i + ") -> excepción:" + ex.getMessage());
            }
        });

        refrescos.add(() -> {
            List<Paquete> pendientes = servicio.ordenPorLlegada();
            recargar(paqueteAsignar, pendientes);
            recargar(paqueteEntregar, pendientes);
            recargar(repartidor, repartidoresOrdenados());

            asignaciones.clear();
            for (Paquete p : pendientes) {
                Repartidor r = servicio.obtenerRepartidorAsignado(p.getCodigo());
                if (r != null) {
                    asignaciones.addElement(p.getCodigo() + "  ->  " + r.getId() + " - " + r.getNombre());
                }
            }

            // Recorrido de la lista enlazada propia con su Iterator (for-each)
            historial.clear();
            int n = 0;
            for (Paquete p : servicio.getHistorialEntregas()) {
                historial.addElement("[" + n++ + "] " + p);
            }
            tamano.setText("tamano() = " + servicio.getHistorialEntregas().tamano()
                    + "    estaVacio() = " + servicio.getHistorialEntregas().estaVacio());
        });

        JPanel arriba = columna(
                titulado("Asignar paquete pendiente  —  asignarPaquete(codigo, idRepartidor)",
                        fila(new JLabel("Paquete"), paqueteAsignar, new JLabel("Repartidor"), repartidor, asignar)),
                titulado("Entregar  —  registrarEntrega(codigo)",
                        fila(new JLabel("Paquete"), paqueteEntregar, entregar)));

        JPanel panelHistorial = titulado("Historial de entregas (ListaEnlazada propia, recorrida con Iterator)",
                new JScrollPane(new JList<>(historial)));
        panelHistorial.add(fila(tamano, Box.createHorizontalStrut(30), new JLabel("Posición"), posicion, obtener),
                BorderLayout.SOUTH);

        JPanel centro = new JPanel(new GridLayout(1, 2, 8, 0));
        centro.add(titulado("Asignaciones vigentes  —  obtenerRepartidorAsignado(codigo)", new JScrollPane(new JList<>(asignaciones))));
        centro.add(panelHistorial);
        return bordes(arriba, centro);
    }

    // Explica por qué asignarPaquete rechazaría la asignación (mismas condiciones que revisa el servicio)
    private String motivoRechazo(Paquete p, Repartidor r) {
        if (!servicio.ordenPorLlegada().contains(p)) {
            return "el paquete ya fue entregado";
        }
        if (servicio.obtenerRepartidorAsignado(p.getCodigo()) != null) {
            return "el paquete ya tiene repartidor asignado";
        }
        if (!r.isDisponible()) {
            return "el repartidor no está disponible";
        }
        if (!r.getZona().equalsIgnoreCase(p.getDestino())) {
            return "la zona del repartidor (" + r.getZona() + ") no coincide con el destino (" + p.getDestino() + ")";
        }
        return "";
    }

    // ===================== ATENCIÓN POR LLEGADA Y POR PRIORIDAD (necesidades 1 y 5) =====================

    // Compara la atención por orden de llegada con la atención por prioridad
    private JPanel panelAtencion() {
        DefaultListModel<String> llegada = new DefaultListModel<>();
        DefaultListModel<String> prioridad = new DefaultListModel<>();
        JLabel siguienteLlegada = new JLabel();
        JLabel siguientePrioritario = new JLabel();

        JButton consultarLlegada = boton("obtenerSiguientePorLlegada()", e ->
                log("obtenerSiguientePorLlegada() -> " + servicio.obtenerSiguientePorLlegada()));
        JButton consultarPrioritario = boton("obtenerSiguientePrioritario()", e ->
                log("obtenerSiguientePrioritario() -> " + servicio.obtenerSiguientePrioritario()));

        refrescos.add(() -> {
            recargarLista(llegada, servicio.ordenPorLlegada());
            recargarLista(prioridad, servicio.ordenDespachoPrioritario());
            siguienteLlegada.setText("Siguiente: " + servicio.obtenerSiguientePorLlegada());
            siguientePrioritario.setText("Siguiente: " + servicio.obtenerSiguientePrioritario());
        });

        JPanel izquierda = titulado("Por llegada (LinkedHashSet)  —  ordenPorLlegada()", new JScrollPane(new JList<>(llegada)));
        izquierda.add(columna(fila(siguienteLlegada), fila(consultarLlegada)), BorderLayout.NORTH);

        JPanel derecha = titulado("Por prioridad (PriorityQueue + Comparadores.DESPACHO)  —  ordenDespachoPrioritario()",
                new JScrollPane(new JList<>(prioridad)));
        derecha.add(columna(fila(siguientePrioritario), fila(consultarPrioritario)), BorderLayout.NORTH);

        JPanel centro = new JPanel(new GridLayout(1, 2, 8, 0));
        centro.add(izquierda);
        centro.add(derecha);
        JLabel nota = new JLabel("Solo aparecen los paquetes pendientes. Los entregados se descartan de la cola de prioridad "
                + "de forma perezosa cuando llegan a la cima.");
        return bordes(fila(nota), centro);
    }

    // ===================== MUNICIPIOS (necesidades 3 y 4) =====================

    // Municipios sin repetidos, ordenados y paquetes agrupados por municipio
    private JPanel panelMunicipios() {
        DefaultListModel<String> aparicion = new DefaultListModel<>();
        DefaultListModel<String> ordenados = new DefaultListModel<>();
        JList<String> listaAparicion = new JList<>(aparicion);
        JList<String> listaOrdenados = new JList<>(ordenados);
        DefaultTableModel modelo = modeloTabla(COLUMNAS_PAQUETE);
        JPanel panelPaquetes = titulado("Seleccione un municipio  —  obtenerPaquetesPorMunicipio(municipio)",
                new JScrollPane(new JTable(modelo)));
        String[] municipioActual = {null};

        Runnable mostrarPaquetes = () -> {
            if (municipioActual[0] == null) {
                return;
            }
            List<Paquete> paquetes = servicio.obtenerPaquetesPorMunicipio(municipioActual[0]);
            llenarTablaPaquetes(modelo, paquetes);
            ((TitledBorder) panelPaquetes.getBorder()).setTitle(
                    "obtenerPaquetesPorMunicipio(\"" + municipioActual[0] + "\")  —  " + paquetes.size() + " paquetes");
            panelPaquetes.repaint();
        };

        for (JList<String> lista : List.of(listaAparicion, listaOrdenados)) {
            lista.addListSelectionListener(e -> {
                String m = lista.getSelectedValue();
                if (e.getValueIsAdjusting() || m == null || m.equals(municipioActual[0])) {
                    return;
                }
                municipioActual[0] = m;
                (lista == listaAparicion ? listaOrdenados : listaAparicion).clearSelection();
                mostrarPaquetes.run();
                log("obtenerPaquetesPorMunicipio(\"" + m + "\") -> " + codigos(servicio.obtenerPaquetesPorMunicipio(m)));
            });
        }

        refrescos.add(() -> {
            aparicion.clear();
            servicio.getMunicipios().forEach(aparicion::addElement);
            ordenados.clear();
            servicio.getMunicipiosOrdenados().forEach(ordenados::addElement);
            mostrarPaquetes.run();
        });

        JPanel listas = new JPanel(new GridLayout(1, 2, 8, 0));
        listas.add(titulado("getMunicipios() — LinkedHashSet", new JScrollPane(listaAparicion)));
        listas.add(titulado("getMunicipiosOrdenados() — TreeSet", new JScrollPane(listaOrdenados)));
        listas.setPreferredSize(new Dimension(520, 0));

        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        panel.add(listas, BorderLayout.WEST);
        panel.add(panelPaquetes, BorderLayout.CENTER);
        return panel;
    }

    // ===================== ORDENAMIENTOS =====================

    // Ordena todos los paquetes con Comparable o con los Comparator de Comparadores
    private JPanel panelOrdenamientos() {
        Map<String, Supplier<List<Paquete>>> criterios = new LinkedHashMap<>();
        criterios.put("obtenerPaquetesOrdenadosPorCodigo()  —  Comparable (código)",
                () -> servicio.obtenerPaquetesOrdenadosPorCodigo());
        criterios.put("obtenerPaquetesOrdenados(POR_PRIORIDAD_DESC)  —  prioridad mayor a menor",
                () -> servicio.obtenerPaquetesOrdenados(Comparadores.POR_PRIORIDAD_DESC));
        criterios.put("obtenerPaquetesOrdenados(POR_PESO_DESC)  —  peso mayor a menor",
                () -> servicio.obtenerPaquetesOrdenados(Comparadores.POR_PESO_DESC));
        criterios.put("obtenerPaquetesOrdenados(POR_TIEMPO_ASC)  —  tiempo menor a mayor",
                () -> servicio.obtenerPaquetesOrdenados(Comparadores.POR_TIEMPO_ASC));
        criterios.put("obtenerPaquetesOrdenados(DESPACHO)  —  prioridad, tiempo y código",
                () -> servicio.obtenerPaquetesOrdenados(Comparadores.DESPACHO));

        JComboBox<String> criterio = new JComboBox<>(criterios.keySet().toArray(new String[0]));
        DefaultTableModel modelo = modeloTabla(COLUMNAS_PAQUETE);
        Runnable ordenar = () -> llenarTablaPaquetes(modelo, criterios.get((String) criterio.getSelectedItem()).get());
        criterio.addActionListener(e -> {
            ordenar.run();
            String nombre = (String) criterio.getSelectedItem();
            log(nombre.substring(0, nombre.indexOf("  —")) + " -> "
                    + codigos(criterios.get(nombre).get()));
        });
        refrescos.add(ordenar);

        return bordes(titulado("Criterio de ordenamiento (todos los paquetes de la jornada)", fila(criterio)),
                titulado("Resultado", new JScrollPane(new JTable(modelo))));
    }

    // ===================== RECURSIVIDAD, BÚSQUEDA BINARIA Y DIVIDE Y VENCERÁS =====================

    // Funciones recursivas, búsqueda binaria y divide y vencerás
    private JPanel panelAlgoritmos() {
        JComboBox<String> destino = new JComboBox<>();
        JLabel resultadoPeso = new JLabel(" ");
        JButton calcularPeso = boton("pesoTotalDestino(destino)", e -> {
            String d = (String) destino.getSelectedItem();
            double total = servicio.pesoTotalDestino(d);
            resultadoPeso.setText(total + " kg");
            log("pesoTotalDestino(\"" + d + "\") -> " + total + " kg");
        });

        JSpinner prioridadMinima = new JSpinner(new SpinnerNumberModel(4, Paquete.PRIORIDAD_MINIMA, Paquete.PRIORIDAD_MAXIMA, 1));
        JLabel resultadoConteo = new JLabel(" ");
        JButton contar = boton("contarPrioridadMinima(prioridad)", e -> {
            int p = (Integer) prioridadMinima.getValue();
            int cuenta = servicio.contarPrioridadMinima(p);
            resultadoConteo.setText(cuenta + " paquetes");
            log("contarPrioridadMinima(" + p + ") -> " + cuenta);
        });

        JLabel resultadoMayor = new JLabel(" ");
        JButton mayor = boton("mayorPeso()", e -> {
            Paquete p = servicio.mayorPeso();
            resultadoMayor.setText(p.toString());
            log("mayorPeso() -> " + p);
        });

        DefaultListModel<String> ordenados = new DefaultListModel<>();
        JList<String> listaOrdenados = new JList<>(ordenados);
        JTextField codigo = new JTextField(6);
        JLabel resultadoBusqueda = new JLabel(" ");
        JButton buscar = boton("Buscar", e -> {
            String c = codigo.getText().trim();
            int posicion = servicio.busquedaBinariaPorCodigo(servicio.obtenerPaquetesOrdenadosPorCodigo(), c);
            log("busquedaBinariaPorCodigo(ordenados, \"" + c + "\") -> " + posicion);
            resultadoBusqueda.setText(posicion == -1 ? "No encontrado (-1)" : "Posición " + posicion);
            if (posicion == -1) {
                listaOrdenados.clearSelection();
            } else {
                listaOrdenados.setSelectedIndex(posicion);
                listaOrdenados.ensureIndexIsVisible(posicion);
            }
        });

        refrescos.add(() -> {
            recargar(destino, servicio.getMunicipiosOrdenados());
            ordenados.clear();
            List<Paquete> lista = servicio.obtenerPaquetesOrdenadosPorCodigo();
            for (int i = 0; i < lista.size(); i++) {
                ordenados.addElement("[" + i + "] " + lista.get(i));
            }
        });

        JPanel arriba = columna(
                titulado("Funciones recursivas",
                        columna(fila(new JLabel("Destino"), destino, calcularPeso, resultadoPeso),
                                fila(new JLabel("Prioridad mínima"), prioridadMinima, contar, resultadoConteo))),
                titulado("Divide y vencerás: paquete de mayor peso",
                        fila(mayor, resultadoMayor)),
                titulado("Búsqueda binaria  —  busquedaBinariaPorCodigo(lista ordenada por código, codigo)",
                        fila(new JLabel("Código"), codigo, buscar, resultadoBusqueda)));
        return bordes(arriba, titulado("Lista ordenada por código sobre la que se busca", new JScrollPane(listaOrdenados)));
    }

    // ===================== LISTA ENLAZADA PROPIA =====================

    // Prueba la ListaEnlazada genérica con String y dibuja sus nodos
    private JPanel panelListaEnlazada() {
        DibujoLista dibujo = new DibujoLista();
        JLabel estado = new JLabel();
        JTextField valor = new JTextField(6);
        JSpinner posicion = new JSpinner(new SpinnerNumberModel(0, -5, 99, 1));

        Runnable actualizar = () -> {
            dibujo.mostrar(listaDemo);
            estado.setText("toString() = " + listaDemo + "     tamano() = " + listaDemo.tamano()
                    + "     estaVacio() = " + listaDemo.estaVacio());
        };

        JButton agregar = boton("agregar", e -> {
            String v = valor.getText().trim();
            if (v.isEmpty()) {
                return;
            }
            listaDemo.agregar(v);
            log("agregar(\"" + v + "\") -> " + listaDemo);
            actualizar.run();
        });
        JButton eliminar = boton("eliminar", e -> {
            String v = valor.getText().trim();
            boolean ok = listaDemo.eliminar(v);
            log("eliminar(\"" + v + "\") -> " + ok + "  " + listaDemo);
            actualizar.run();
        });
        JButton obtener = boton("obtener(posición)", e -> {
            int i = (Integer) posicion.getValue();
            try {
                log("obtener(" + i + ") -> " + listaDemo.obtener(i));
            } catch (IndexOutOfBoundsException ex) {
                log("obtener(" + i + ") -> excepción:" + ex.getMessage());
            }
        });
        JButton ejemplo = boton("Cargar A, B, C, D", e -> {
            listaDemo = new ListaEnlazada<>();
            for (String v : List.of("A", "B", "C", "D")) {
                listaDemo.agregar(v);
            }
            log("Lista de ejemplo -> " + listaDemo);
            actualizar.run();
        });
        JButton vaciar = boton("Nueva lista vacía", e -> {
            listaDemo = new ListaEnlazada<>();
            log("Nueva lista -> " + listaDemo);
            actualizar.run();
        });
        actualizar.run();

        JPanel arriba = columna(
                titulado("Operaciones de ColeccionPropia sobre ListaEnlazada<String>",
                        columna(fila(new JLabel("Valor"), valor, agregar, eliminar, Box.createHorizontalStrut(20),
                                        new JLabel("Posición"), posicion, obtener),
                                fila(ejemplo, vaciar))),
                fila(estado));
        JScrollPane scroll = new JScrollPane(dibujo);
        scroll.setBorder(null);
        return bordes(arriba, titulado("Nodos (recorridos con el Iterator de la lista)", scroll));
    }

    // Dibuja la lista enlazada como nodos unidos por flechas, marcando cabeza y cola
    private static class DibujoLista extends JPanel {
        private static final int ALTO = 40;
        private static final int FLECHA = 40;
        private final List<String> valores = new ArrayList<>();

        DibujoLista() {
            setBackground(Color.WHITE);
            setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
        }

        // Copia los valores de la lista usando su Iterator y vuelve a dibujar
        void mostrar(ListaEnlazada<String> lista) {
            valores.clear();
            for (String v : lista) {
                valores.add(v);
            }
            FontMetrics fm = getFontMetrics(getFont());
            int ancho = 30;
            for (String v : valores) {
                ancho += anchoNodo(fm, v) + FLECHA;
            }
            setPreferredSize(new Dimension(ancho + 60, 140));
            revalidate();
            repaint();
        }

        private static int anchoNodo(FontMetrics fm, String v) {
            return Math.max(50, fm.stringWidth(v) + 24);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            FontMetrics fm = g2.getFontMetrics();
            int x = 30;
            int y = 40;
            if (valores.isEmpty()) {
                g2.drawString("cabeza = null, cola = null  (lista vacía)", x, y + 25);
                return;
            }
            for (int i = 0; i < valores.size(); i++) {
                String v = valores.get(i);
                int ancho = anchoNodo(fm, v);
                g2.setColor(new Color(225, 238, 255));
                g2.fillRoundRect(x, y, ancho, ALTO, 12, 12);
                g2.setColor(new Color(40, 80, 160));
                g2.drawRoundRect(x, y, ancho, ALTO, 12, 12);
                g2.setColor(Color.BLACK);
                g2.drawString(v, x + (ancho - fm.stringWidth(v)) / 2, y + 26);

                int etiqueta = y + ALTO + 22;
                if (i == 0) {
                    g2.drawString("cabeza", x, etiqueta);
                    etiqueta += 20;
                }
                if (i == valores.size() - 1) {
                    g2.drawString("cola", x, etiqueta);
                }

                int x1 = x + ancho;
                int x2 = x1 + FLECHA;
                int medio = y + ALTO / 2;
                g2.drawLine(x1, medio, x2 - 2, medio);
                g2.fillPolygon(new int[]{x2, x2 - 8, x2 - 8}, new int[]{medio, medio - 5, medio + 5}, 3);
                x = x2;
            }
            g2.setColor(Color.GRAY);
            g2.drawString("null", x + 6, y + 26);
        }
    }

    // ===================== UTILIDADES DE LA INTERFAZ =====================

    // Escribe una línea en la consola de operaciones
    private void log(String texto) {
        consola.append("> " + texto + "\n");
        consola.setCaretPosition(consola.getDocument().getLength());
    }

    // Muestra un dato rechazado en un diálogo y en la consola
    private void aviso(String mensaje) {
        log("Rechazado: " + mensaje);
        JOptionPane.showMessageDialog(this, mensaje, "Dato inválido", JOptionPane.WARNING_MESSAGE);
    }

    // Repartidores registrados ordenados por identificación
    private List<Repartidor> repartidoresOrdenados() {
        return servicio.obtenerRepartidores().stream()
                .sorted(Comparator.comparing(Repartidor::getId))
                .toList();
    }

    // Llena una tabla de paquetes, indicando si cada uno está pendiente, asignado o entregado
    private void llenarTablaPaquetes(DefaultTableModel modelo, List<Paquete> paquetes) {
        Set<Paquete> pendientes = new HashSet<>(servicio.ordenPorLlegada());
        modelo.setRowCount(0);
        for (Paquete p : paquetes) {
            Repartidor r = servicio.obtenerRepartidorAsignado(p.getCodigo());
            String estado = !pendientes.contains(p) ? "Entregado"
                    : r != null ? "Asignado a " + r.getId()
                    : "Pendiente";
            modelo.addRow(new Object[]{p.getCodigo(), p.getDestino(), p.getPeso(), p.getPrioridad(), p.getTiempo(), estado});
        }
    }

    // Selecciona en la tabla la fila cuya primera columna coincide con la llave dada
    private static void seleccionarFila(JTable tabla, String llave) {
        for (int i = 0; i < tabla.getRowCount(); i++) {
            if (tabla.getValueAt(i, 0).equals(llave)) {
                tabla.setRowSelectionInterval(i, i);
                tabla.scrollRectToVisible(tabla.getCellRect(i, 0, true));
                return;
            }
        }
        tabla.clearSelection();
    }

    // Reemplaza los elementos de un combo conservando la selección si sigue existiendo
    private static <T> void recargar(JComboBox<T> combo, Collection<? extends T> elementos) {
        Object seleccionado = combo.getSelectedItem();
        combo.removeAllItems();
        for (T e : elementos) {
            combo.addItem(e);
        }
        if (seleccionado != null) {
            combo.setSelectedItem(seleccionado);
        }
    }

    // Reemplaza los elementos de una lista numerándolos desde 1
    private static void recargarLista(DefaultListModel<String> modelo, List<?> elementos) {
        modelo.clear();
        for (int i = 0; i < elementos.size(); i++) {
            modelo.addElement((i + 1) + ". " + elementos.get(i));
        }
    }

    // Solo los códigos de una lista de paquetes, para la consola
    private static List<String> codigos(List<Paquete> paquetes) {
        return paquetes.stream().map(Paquete::getCodigo).toList();
    }

    // Modelo de tabla de solo lectura
    private static DefaultTableModel modeloTabla(String... columnas) {
        return new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };
    }

    private static JButton boton(String texto, ActionListener accion) {
        JButton b = new JButton(texto);
        b.addActionListener(accion);
        return b;
    }

    // Componentes en una fila, alineados a la izquierda
    private static JPanel fila(Component... componentes) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        for (Component c : componentes) {
            panel.add(c);
        }
        return panel;
    }

    // Componentes apilados verticalmente
    private static JPanel columna(Component... componentes) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        for (Component c : componentes) {
            if (c instanceof JComponent jc) {
                jc.setAlignmentX(Component.LEFT_ALIGNMENT);
            }
            panel.add(c);
        }
        return panel;
    }

    // Panel con borde y título que contiene el componente dado
    private static JPanel titulado(String titulo, Component contenido) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(titulo));
        panel.add(contenido, BorderLayout.CENTER);
        return panel;
    }

    // Pestaña con controles arriba y contenido principal en el centro
    private static JPanel bordes(Component arriba, Component centro) {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        panel.add(arriba, BorderLayout.NORTH);
        panel.add(centro, BorderLayout.CENTER);
        return panel;
    }
}
