package structures.management;

import model.LecturaSensor;
import java.util.*;
import java.util.stream.Collectors;

/**
 * GestorDeRed - Módulo Integrador de Estructuras de Datos
 * VERSION ESTUDIANTE
 */
public class GestorDeRed {
    // 1. Historial de Eventos: Registro lineal de todas las actividades
    private List<String> historialEventos;

    // 2. Navegador de Alertas: Permite moverse adelante y atrás entre alertas
    private Deque<String> navegadorAlertas;
    private String alertaActual;

    // 3. Ciclo de Monitoreo: Gestión de la rotación de sensores a consultar
    private Queue<String> colaMonitoreo;

    // 4. Topología de la Red: Grafo de conexiones entre estaciones (Lista de adyacencia)
    private Map<String, List<String>> topologiaRed;

    // 5. Buffer de Ingesta: Cola de espera para procesar lecturas entrantes
    private Queue<LecturaSensor> bufferIngesta;

    public GestorDeRed() {
        this.historialEventos = new ArrayList<>();
        this.navegadorAlertas = new ArrayDeque<>();
        this.colaMonitoreo = new ArrayDeque<>();
        this.topologiaRed = new HashMap<>();
        this.bufferIngesta = new ArrayDeque<>();
    }

    // =========================================================================
    // SECCIÓN 1: HISTORIAL Y EVENTOS
    // =========================================================================

    public void agregarEvento(String evento) {
        // TODO 1: Implementar la adición del evento al historial.
        // Debe registrarse la fecha/hora actual junto con la descripción del evento.
        String registro = "[" + new java.util.Date() + "] " + evento;
        historialEventos.add(registro);
    }

    public List<String> obtenerEventosRecientes(int limite) {
        if (historialEventos.size() <= limite) return new ArrayList<>(historialEventos);
        return historialEventos.subList(historialEventos.size() - limite, historialEventos.size());
    }

    public void limpiarHistorialAntiguo(int mantener) {
        // TODO 10 (Santiago): Conservar solo los últimos 'mantener' eventos
        while (historialEventos.size() > mantener) {
            historialEventos.remove(0);
        }
    }

    // =========================================================================
    // SECCIÓN 2: NAVEGACIÓN DE ALERTAS
    // =========================================================================

    public void registrarAlerta(String alerta) {
        navegadorAlertas.addLast(alerta);
        alertaActual = alerta;
    }

    public String navegarAlSiguiente() {
        // TODO 2: Lógica de desplazamiento hacia adelante usando Deque.
        // Extrae del frente y reubica al final para avanzar en ciclo continuo.
        if (navegadorAlertas.isEmpty()) {
            alertaActual = null;
            return null;
        }
        String siguiente = navegadorAlertas.pollFirst();
        navegadorAlertas.addLast(siguiente);
        alertaActual = siguiente;
        return alertaActual;
    }

    public String navegarAlAnterior() {
        // TODO 3: Lógica de desplazamiento hacia atrás usando Deque.
        // Extrae del final y reubica al frente para retroceder en ciclo continuo.
        if (navegadorAlertas.isEmpty()) {
            alertaActual = null;
            return null;
        }
        String anterior = navegadorAlertas.pollLast();
        navegadorAlertas.addFirst(anterior);
        alertaActual = anterior;
        return alertaActual;
    }

    public String getAlertaActual() { return alertaActual; }

    // =========================================================================
    // SECCIÓN 3: CICLO DE MONITOREO
    // =========================================================================

    public void programarConsulta(String idSensor) {
        // TODO 4: Evitar duplicados con contains()
        if (idSensor != null && !colaMonitoreo.contains(idSensor)) {
            colaMonitoreo.offer(idSensor);
        }
    }

    public String obtenerSiguienteSensor() {
        // TODO 5: Rotación del ciclo (extraer del frente y volver a insertar al final)
        if (colaMonitoreo.isEmpty()) {
            return null;
        }
        String sensor = colaMonitoreo.poll();
        colaMonitoreo.offer(sensor);
        return sensor;
    }

    // =========================================================================
    // SECCIÓN 4: TOPOLOGÍA DE RED (Retos 6, 7 y 9 - Santiago)
    // =========================================================================

    public void registrarConexion(String sensorA, String sensorB) {
        // TODO 6 (Santiago): Registrar conexión bidireccional evitando duplicados
        topologiaRed.computeIfAbsent(sensorA, k -> new ArrayList<>());
        topologiaRed.computeIfAbsent(sensorB, k -> new ArrayList<>());

        if (!topologiaRed.get(sensorA).contains(sensorB)) {
            topologiaRed.get(sensorA).add(sensorB);
        }
        if (!topologiaRed.get(sensorB).contains(sensorA)) {
            topologiaRed.get(sensorB).add(sensorA);
        }
    }

    public List<String> obtenerVecinos(String idSensor) {
        // TODO 7 (Santiago): Consultar vecinos con getOrDefault
        return topologiaRed.getOrDefault(idSensor, Collections.emptyList());
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
        return bufferIngesta.poll();
    }

    public List<LecturaSensor> filtrarAlertasCriticas(double limitePm25) {
        return bufferIngesta.stream()
                .filter(l -> l.getPm25() > limitePm25)
                .collect(Collectors.toList());
    }
}