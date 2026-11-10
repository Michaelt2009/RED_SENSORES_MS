# Preguntas de Criterio: Análisis de Estructuras de Datos

---

## 1. Historial: `ArrayList` vs. `LinkedList`

* **`LinkedList`**
    * **Ventaja:** La eliminación del elemento inicial (`remove(0)`) opera en tiempo constante $\mathcal{O}(1)$ mediante el ajuste de punteros del nodo raíz.
    * **Comparativa:** Repetir la operación $k$ veces mantiene una complejidad óptima frente al costo $\mathcal{O}(n \cdot k)$ de `ArrayList`, el cual requiere desplazar en memoria los $n-1$ elementos restantes en cada iteración.
* **`ArrayList` (Elección de diseño)**
    * **Requisito:** Se requiere acceso aleatorio indexado $\mathcal{O}(1)$ para implementar la extracción de rangos (`subList`) en la función `obtenerEventosRecientes`.
    * **Mitigación del costo:** Para evitar penalizaciones por eliminaciones repetidas, `limpiarHistorialAntiguo` borra el bloque completo mediante la instrucción `subList(0, sobran).clear()`, ejecutando un único desplazamiento masivo amortizado en $\mathcal{O}(n)$.

---

## 2. Navegación: `Deque` vs. `Queue`

| Tipo de Interfaz | Operaciones Permitidas | Direccionalidad |
| :--- | :--- | :--- |
| **`Queue`** | Exclusivamente `FIFO` (push al final, pop al frente) | Unidireccional ("Solo Adelante") |
| **`Deque`** | Inserción y extracción en ambos extremos (`addFirst`, `addLast`, `pollFirst`, `pollLast`) | Bidireccional ("Adelante" y "Atrás") |

### Lógica de Control en Código
* **Avance ("Adelante"):** `pollFirst()` $\rightarrow$ `addLast()`
* **Retroceso ("Atrás"):** `addFirst(pollLast())` $\rightarrow$ `peekLast()`

> **Relación con `NodoDoble`:** Un nodo doblemente enlazado contiene referencias explícitas a `siguiente` y `anterior`. Esta topología permite mutaciones y recorridos en ambos extremos en tiempo constante $\mathcal{O}(1)$, constituyendo la base estructural sobre la cual se define el contrato formal de un `Deque`.

---

## 3. Ciclo de Monitoreo: `Queue` vs. `Stack`

### Política FIFO (`Queue`)
Garantiza **equidad de muestreo** (*round-robin scheduling*). El sensor con mayor tiempo en espera en el frente es procesado e inmediatamente insertado en la cola final, logrando una frecuencia de consulta uniforme para todos los nodos del sistema.

### Política LIFO (`Stack`)
Produce un fallo de **inanición** (*starvation*). El elemento extraído y reinsertado en el tope de la pila vuelve a ser procesado inmediatamente en la siguiente iteración, bloqueando indefinidamente la ejecución de los demás sensores.

---

## 4. Topología de Red: `Map` vs. Matriz de Adyacencia

### Métricas de Eficiencia
* **Matriz de Adyacencia:** Requiere una asignación fija de $\mathcal{O}(V^2)$ celdas. En grafos con baja densidad de aristas (2–3 conexiones por nodo), representa un desperdicio significativo de memoria.
* **`Map<String, List<String>>`:** Guarda exclusivamente las adyacencias reales, reduciendo la complejidad espacial a $\mathcal{O}(V + E)$.

### Ventajas de Implementación
1. **Tipado Directo:** La clave del `Map` utiliza la identidad nativa del sensor (`String`), eliminando la necesidad de mapeos intermedios a índices numéricos discretos.
2. **Dinámica de Nodos:** La adición de una nueva estación equivale a una inserción de clave $\mathcal{O}(1)$, y la consulta de vecinos se realiza mediante `getOrDefault()`.
3. **Abstracción con `NodoGrafo`:** Cada entrada del mapa abstrae un nodo donde la clave identifica la estación fuente y la lista asociada actúa como su lista de adyacencia explícita.

---

## 5. Buffer de Ingesta Multihilo

### Diagnóstico: `ArrayDeque`
No provee garantías de concurrencia (*not thread-safe*). En escenarios con productores y consumidores simultáneos, los punteros internos de cabeza y cola se corrompen, ocasionando:
* Pérdida o duplicación en el procesamiento de eventos.
* Condición de carrera (*race conditions*).
* Excepciones en tiempo de ejecución (`ConcurrentModificationException`).

### Solución: `ConcurrentLinkedQueue`
Proporciona acceso concurrente seguro mediante una arquitectura *lock-free* basada en instrucciones atómicas de hardware **CAS** (*Compare-And-Swap*).