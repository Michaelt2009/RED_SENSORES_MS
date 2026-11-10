package structures.management;

import model.LecturaSensor;
import java.util.*;
import java.util.stream.Collectors;

/**
 * GestorDeRed - Módulo Integrador de Estructuras de Datos
 * VERSION ESTUDIANTE
 *
 * Parte de Santiago: TODOs 6, 7, 8, 9 y 10.
 * (Los TODOs 1-5 son de su compañero y se pegan en este mismo archivo.)
 */
public class GestorDeRed {

    // Historial de eventos (lo usa el TODO 10)
    private List<String> historialEventos;

    // 4. Topología de la Red: grafo como lista de adyacencia
    //    clave = id de estación, valor = lista de estaciones conectadas
    private Map<String, List<String>> topologiaRed;

    // 5. Buffer de Ingesta: cola FIFO de lecturas pendientes
    private Queue<LecturaSensor> bufferIngesta;

    public GestorDeRed() {
        this.historialEventos = new ArrayList<>();
        this.topologiaRed = new HashMap<>();
        this.bufferIngesta = new ArrayDeque<>();
    }

    // =========================================================================
    // TODO 10: LIMPIEZA DE HISTORIAL
    // =========================================================================

    public void limpiarHistorialAntiguo(int mantener) {
        // TODO 10 (Santiago): Conservar solo los últimos 'mantener' eventos
        int sobran = historialEventos.size() - mantener;
        if (sobran > 0) {
            // subList es una vista; clear() borra ese rango con un solo desplazamiento, O(n)
            // (remove(0) en bucle sería O(n·k))
            historialEventos.subList(0, sobran).clear();
        }
    }

    // =========================================================================
    // SECCIÓN 4: TOPOLOGÍA DE RED (Retos 6, 7 y 9 - Santiago)
    // =========================================================================

    public void registrarConexion(String sensorA, String sensorB) {
        // TODO 6 (Santiago): Registrar conexión bidireccional evitando duplicados
        // computeIfAbsent: si la estación no existe en el mapa, crea su lista vacía
        topologiaRed.computeIfAbsent(sensorA, k -> new ArrayList<>());
        topologiaRed.computeIfAbsent(sensorB, k -> new ArrayList<>());

        // Conexión en ambos sentidos, sin repetir vecinos
        if (!topologiaRed.get(sensorA).contains(sensorB)) {
            topologiaRed.get(sensorA).add(sensorB);
        }
        if (!topologiaRed.get(sensorB).contains(sensorA)) {
            topologiaRed.get(sensorB).add(sensorA);
        }
    }

    public List<String> obtenerVecinos(String idSensor) {
        // TODO 7 (Santiago): Consultar vecinos con getOrDefault
        // Si la estación no existe devuelve lista vacía en vez de null
        return topologiaRed.getOrDefault(idSensor, Collections.emptyList());
    }

    /** Registra una estación sin conexiones (necesario para que pueda detectarse como aislada). */
    public void registrarEstacion(String idSensor) {
        if (idSensor != null) {
            topologiaRed.computeIfAbsent(idSensor, k -> new ArrayList<>());
        }
    }

    public List<String> detectarEstacionesAisladas() {
        // TODO 9 (Santiago): Identificar estaciones sin conexiones
        List<String> aisladas = new ArrayList<>();
        for (Map.Entry<String, List<String>> entry : topologiaRed.entrySet()) {
            if (entry.getValue().isEmpty()) {
                aisladas.add(entry.getKey());
            }
        }
        return aisladas;
    }

    // =========================================================================
    // SECCIÓN 5: BUFFER DE INGESTA
    // =========================================================================

    public void recibirLectura(LecturaSensor lectura) {
        bufferIngesta.add(lectura);
    }

    public LecturaSensor procesarSiguienteLectura() {
        // TODO 8 (Santiago): Procesar FIFO con poll()
        // poll() saca la lectura más antigua, o devuelve null si la cola está vacía
        return bufferIngesta.poll();
    }

    public List<LecturaSensor> filtrarAlertasCriticas(double limitePm25) {
        return bufferIngesta.stream()
                .filter(l -> l.getPm25() > limitePm25)
                .collect(Collectors.toList());
    }
}