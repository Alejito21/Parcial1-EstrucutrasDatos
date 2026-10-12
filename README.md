# QuindíoExpress – Gestión de paquetes

Sistema para gestionar los paquetes de una jornada de **QuindíoExpress**: registro, consulta por código, asignación a repartidores, entregas y despacho por prioridad. Es el proyecto del **Parcial 1 de Estructuras de Datos** de la **Universidad del Quindío**.

## Autores

- Jerónimo Valderrama Román
- Alejandro Rivera Pérez

## Estructura del proyecto

Paquete `co.edu.uniquindio.poo.Model`:

- **`Paquete`**: código, destino, peso, prioridad (1 a 5, por defecto 3) y tiempo estimado. Valida sus datos y su orden natural es por código.
- **`Repartidor`**: identificación, nombre, zona y si está disponible.
- **`ServicioPaqueteria`**: la clase central. Guarda todas las estructuras y tiene las operaciones de la jornada.
- **`Comparadores`**: criterios de ordenamiento (`POR_PRIORIDAD_DESC`, `POR_PESO_DESC`, `POR_TIEMPO_ASC` y `DESPACHO`).
- **`ColeccionPropia<T>`**: interfaz con las operaciones de nuestras estructuras (`agregar`, `eliminar`, `obtener`, `tamano`, `estaVacio`).
- **`ListaEnlazada<T>`**: lista simplemente enlazada genérica que implementa `ColeccionPropia`.
- **`Nodo<T>`**: nodo de la lista (valor y referencia al siguiente).
- **`IteradorLista<T>`**: iterador para recorrer la lista sin exponer los nodos.

Paquete `co.edu.uniquindio.poo.Main`:

- **`Main`**: carga 30 paquetes y 7 repartidores y muestra en consola cada funcionalidad.

Paquete `co.edu.uniquindio.poo.View`:

- **`VentanaPrincipal`**: interfaz gráfica (Swing) para probar todas las operaciones de forma interactiva. Usa los mismos datos de `Main`.

## Estructuras de datos utilizadas

| Necesidad | Estructura | Operación principal | Complejidad | Por qué |
|---|---|---|---|---|
| 1. Pendientes en orden de registro | `LinkedHashSet<Paquete>` | `add` / `remove` / `iterator` | O(1) | Conserva el orden y permite quitar un paquete entregado aunque no sea el primero. |
| 2. Consultar por código | `HashMap<String, Paquete>` | `get` / `containsKey` | O(1) | Acceso directo por la llave, sin recorrer. |
| 3. Municipios sin duplicados | `LinkedHashSet<String>` | `add` | O(1) | Sin repetidos y en orden de aparición. |
| 4. Municipios ordenados | `TreeSet<String>` | `add` | O(log n) | Orden alfabético y sin repetidos. |
| 5. Atención por prioridad | `PriorityQueue<Paquete>` con `Comparadores.DESPACHO` | `peek` / `offer` / `poll` | O(log n) | Mayor prioridad, luego menor tiempo, luego código. |
| 6. Historial de entregas | `ListaEnlazada<Paquete>` propia | `agregar` | O(1) | Inserción al final con referencia a la cola y recorrido con `Iterator`. |
| 7. Asociar código con `Paquete` | `HashMap<String, Paquete>` (el mismo mapa de la 2) | `put` | O(1) | Además rechaza códigos repetidos. |

## Decisiones de diseño

**Eliminación perezosa en la cola de prioridad.** Sacar un paquete del medio de una `PriorityQueue` con `remove` es O(n), porque primero hay que buscarlo. Por eso `registrarEntrega` ya no lo saca de la cola: solo lo quita de `paquetesPendientes`.

- El `LinkedHashSet` es la fuente de verdad: si un paquete está ahí, sigue pendiente (`contains` en O(1)).
- La `PriorityQueue` puede tener paquetes ya entregados. `limpiarCimaPrioridad()` los descarta con `poll` cuando llegan a la cima, antes de que `obtenerSiguientePrioritario()` haga `peek`.
- `ordenDespachoPrioritario()` recorre una copia de la cola y solo incluye los paquetes que siguen pendientes.

**HashMap en vez de ArrayList para buscar por código.** Con un `ArrayList` habría que recorrer la lista en cada búsqueda (O(n)). Con 30 paquetes no se nota, pero pensando en crecer a 500.000 sí. El `HashMap` busca en O(1) sin importar cuántos paquetes haya.

## Otras funcionalidades

- **Ordenamientos**:
  - Con `Comparable`: `Paquete.compareTo` (por código), usado en `obtenerPaquetesOrdenadosPorCodigo()`.
  - Con `Comparator`: `obtenerPaquetesOrdenados(criterio)`, que recibe uno de los criterios de `Comparadores`.
- **Funciones recursivas**:
  - `pesoTotalDestino(destino)`: peso total de los paquetes que van a un municipio.
  - `contarPrioridadMinima(prioridadMinima)`: cuántos paquetes tienen al menos esa prioridad.
- **Búsqueda binaria**: `busquedaBinariaPorCodigo(ordenados, codigo)` sobre la lista ordenada por código. Retorna la posición o -1.
- **Divide y vencerás**: `mayorPeso()` parte la lista en mitades, busca el más pesado de cada una y se queda con el mayor.

## Cómo ejecutarlo

**Requisitos**

- JDK 24 o superior (el `pom.xml` compila con `maven.compiler.source/target` 24).
- Maven 3.

**Comandos** (desde la carpeta `Parcial1/`, donde está el `pom.xml`)

```bash
# Compilar
mvn compile

# Ejecutar Main
mvn exec:java -Dexec.mainClass=co.edu.uniquindio.poo.Main.Main

# Abrir la interfaz gráfica
mvn exec:java -Dexec.mainClass=co.edu.uniquindio.poo.View.VentanaPrincipal
```

Si ya está compilado, también se puede ejecutar directo con Java:

```bash
java -cp target/classes co.edu.uniquindio.poo.Main.Main
```

## Interfaz gráfica

Pensada para la sustentación. Tiene una pestaña por tema y, abajo, una **consola de operaciones** que muestra cada método llamado y lo que retornó (por ejemplo `asignarPaquete("PQ105", "R01") -> false (la zona del repartidor no coincide con el destino)`).

- **Paquetes**: registrar paquetes (con sus validaciones) y consultar por código.
- **Repartidores**: registrar, buscar por ID y buscar un disponible por zona.
- **Asignación y entregas**: asignar, entregar, ver las asignaciones vigentes y el historial (`ListaEnlazada`).
- **Atención**: orden por llegada frente al orden por prioridad.
- **Municipios**: municipios sin repetidos, ordenados y paquetes por municipio.
- **Ordenamientos**: todos los criterios de `Comparable` y `Comparadores`.
- **Algoritmos**: funciones recursivas, búsqueda binaria y divide y vencerás.
- **Lista enlazada**: prueba la `ListaEnlazada<String>` y dibuja sus nodos (cabeza, cola y `null`).

El botón **Reiniciar jornada** vuelve a los 30 paquetes iniciales.
