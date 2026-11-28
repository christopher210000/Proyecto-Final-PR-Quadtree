/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package hn.unah.quadtree.estructura;

import hn.unah.quadtree.aplicacion.LocalizadorServicios;
import hn.unah.quadtree.modelo.EntradaEspacial;
import hn.unah.quadtree.modelo.Region;
import hn.unah.quadtree.modelo.Servicio;
import hn.unah.quadtree.modelo.TipoServicio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/*
 * ComparacionLinealTest
 *
 * Pruebas de paridad de resultados y comparación experimental entre PRQuadtree
 * y búsqueda lineal (recorrido secuencial sobre todos los elementos).
 * Complementa PRQuadtreeTest y LocalizadorServiciosTest midiendo comparaciones
 * realizadas y tiempo de consulta en distintos escenarios.
 */
class ComparacionLinealTest {

    // Dominio espacial estándar del plano [0, 1024) x [0, 1024).
    private static final Region PLANO = new Region(0, 0, 1024, 1024);

    // Contenedor inmutable para registrar resultados, comparaciones y tiempo (en nanosegundos).
    record MetricasConsulta<T>(
            List<EntradaEspacial<T>> resultados,
            long comparaciones,
            long nanos
    ) {}

    // Búsqueda lineal base: evalúa cada registro con zona.contiene(x, y).
    static <T> MetricasConsulta<T> buscarLineal(List<EntradaEspacial<T>> registros, Region zona) {
        long t0 = System.nanoTime();
        List<EntradaEspacial<T>> encontrados = new ArrayList<>();
        long comparaciones = 0;
        for (EntradaEspacial<T> entrada : registros) {
            comparaciones++;
            if (zona.contiene(entrada.x(), entrada.y())) {
                encontrados.add(entrada);
            }
        }
        long t1 = System.nanoTime();
        return new MetricasConsulta<>(encontrados, comparaciones, t1 - t0);
    }

    // Consulta sobre el PRQuadtree recuperando las entradas revisadas en la última consulta.
    static <T> MetricasConsulta<T> buscarEnQuadtree(PRQuadtree<T> arbol, Region zona) {
        long t0 = System.nanoTime();
        List<EntradaEspacial<T>> encontrados = arbol.consultar(zona);
        long t1 = System.nanoTime();
        return new MetricasConsulta<>(encontrados, arbol.entradasRevisadasUltimaConsulta(), t1 - t0);
    }

    // Replica el plano de ejemplo de 13 servicios sin depender de la visibilidad de paquete de Aplicacion.
    private static LocalizadorServicios crearPlanoEjemploParaPrueba() {
        LocalizadorServicios loc = new LocalizadorServicios();
        loc.registrar("H-01", "Hospital Norte", TipoServicio.HOSPITAL, 100, 120);
        loc.registrar("F-01", "Farmacia Central", TipoServicio.FARMACIA, 110, 130);
        loc.registrar("F-02", "Farmacia La Esperanza", TipoServicio.FARMACIA, 120, 140);
        loc.registrar("F-03", "Farmacia El Prado", TipoServicio.FARMACIA, 130, 100);
        loc.registrar("C-01", "Centro de Salud Las Colinas", TipoServicio.CENTRO_SALUD, 105, 125);
        loc.registrar("F-05", "Farmacia Esquina Cero", TipoServicio.FARMACIA, 0, 0);
        loc.registrar("H-02", "Hospital del Este", TipoServicio.HOSPITAL, 700, 150);
        loc.registrar("H-03", "Hospital del Sur", TipoServicio.HOSPITAL, 300, 900);
        loc.registrar("F-04", "Farmacia Punto Medio", TipoServicio.FARMACIA, 512, 512);
        loc.registrar("C-02", "Centro de Salud El Pedregal", TipoServicio.CENTRO_SALUD, 800, 800);
        loc.registrar("C-03", "Centro de Salud La Ribera", TipoServicio.CENTRO_SALUD, 511.5, 600);
        loc.registrar("C-04", "Centro de Salud Extremo", TipoServicio.CENTRO_SALUD, 1023.9, 1023.9);
        // Coordenada repetida permitida por el índice espacial con distinto identificador.
        loc.registrar("F-06", "Farmacia Las Colinas", TipoServicio.FARMACIA, 105, 125);
        return loc;
    }

    // Genera servicios distribuidos de forma uniforme en [0, 1024) x [0, 1024) con semilla fija.
    private static List<EntradaEspacial<Servicio>> generarServiciosUniformes(int cantidad, long semilla) {
        Random rnd = new Random(semilla);
        TipoServicio[] tipos = TipoServicio.values();
        List<EntradaEspacial<Servicio>> lista = new ArrayList<>(cantidad);
        for (int i = 0; i < cantidad; i++) {
            double x = rnd.nextDouble() * 1024.0;
            double y = rnd.nextDouble() * 1024.0;
            TipoServicio tipo = tipos[i % tipos.length];
            Servicio servicio = new Servicio(tipo.descripcion() + " #" + i, tipo);
            lista.add(new EntradaEspacial<>("SRV-" + i, x, y, servicio));
        }
        return lista;
    }

    // Genera servicios agrupados alrededor de 4 focos para provocar mayor densidad local.
    private static List<EntradaEspacial<Servicio>> generarServiciosAgrupados(int cantidad, long semilla) {
        Random rnd = new Random(semilla);
        TipoServicio[] tipos = TipoServicio.values();
        double[][] focos = {{140, 140}, {840, 200}, {260, 820}, {790, 790}};
        List<EntradaEspacial<Servicio>> lista = new ArrayList<>(cantidad);
        for (int i = 0; i < cantidad; i++) {
            double[] foco = focos[i % focos.length];
            // Se acota estrictamente por debajo de 1024 para mantenerse dentro del dominio semiabierto.
            double x = Math.max(0.0, Math.min(1023.999999, foco[0] + rnd.nextGaussian() * 20.0));
            double y = Math.max(0.0, Math.min(1023.999999, foco[1] + rnd.nextGaussian() * 20.0));
            TipoServicio tipo = tipos[i % tipos.length];
            Servicio servicio = new Servicio(tipo.descripcion() + " Clúster #" + i, tipo);
            lista.add(new EntradaEspacial<>("CLU-" + i, x, y, servicio));
        }
        return lista;
    }

    @Test
    @DisplayName("Paridad exacta contra búsqueda lineal en el plano de ejemplo")
    void paridadEnPlanoDeEjemplo() {
        LocalizadorServicios loc = crearPlanoEjemploParaPrueba();
        List<EntradaEspacial<Servicio>> base = loc.servicios();

        // Incluye consultas internas, de borde/cuadrantes, parcialmente externas, vacías y totales.
        Region[] consultas = {
                new Region(0, 0, 256, 256),
                new Region(400, 400, 600, 700),
                new Region(0, 0, 512, 512),
                new Region(512, 512, 1024, 1024),
                new Region(900, 900, 1500, 1500),
                new Region(2000, 2000, 3000, 3000),
                new Region(0, 0, 1024, 1024)
        };

        for (Region zona : consultas) {
            MetricasConsulta<Servicio> lineal = buscarLineal(base, zona);
            List<EntradaEspacial<Servicio>> qt = loc.consultarZona(zona);

            Set<String> esperados = lineal.resultados().stream()
                    .map(EntradaEspacial::id)
                    .collect(Collectors.toSet());
            Set<String> obtenidos = qt.stream()
                    .map(EntradaEspacial::id)
                    .collect(Collectors.toSet());

            // Verifica tanto la cantidad como el conjunto exacto de identificadores recuperados.
            assertEquals(esperados.size(), qt.size());
            assertEquals(esperados, obtenidos);
        }
    }

    @Test
    @DisplayName("Regiones con límites inválidos o nulos lanzan IllegalArgumentException")
    void regionesInvalidasLanzanExcepcion() {
        // Límites invertidos (min >= max), no finitos o comparación nula deben ser rechazados.
        assertThrows(IllegalArgumentException.class, () -> new Region(500, 100, 200, 300));
        assertThrows(IllegalArgumentException.class, () -> new Region(100, 400, 300, 200));
        assertThrows(IllegalArgumentException.class, () -> new Region(Double.NaN, 0, 10, 10));
        assertThrows(IllegalArgumentException.class, () -> PLANO.intersecta(null));
    }

    @Test
    @DisplayName("Ejecuta batería comparativa uniforme vs agrupada y reporta métricas")
    void bateriaComparativaUniformeYAgrupada() {
        int[] tamanios = {100, 1000, 10000};
        Region[] zonas = {
                new Region(120, 120, 168, 168),   // Zona pequeña (48x48)
                new Region(100, 100, 420, 420),   // Zona mediana (320x320)
                new Region(0, 0, 1024, 1024)      // Zona completa (1024x1024)
        };

        for (String distribucion : List.of("UNIFORME", "AGRUPADA")) {
            for (int n : tamanios) {
                List<EntradaEspacial<Servicio>> datos = "UNIFORME".equals(distribucion)
                        ? generarServiciosUniformes(n, 2026L + n)
                        : generarServiciosAgrupados(n, 4026L + n);

                // Parámetros base del proyecto: capacidad 4 y profundidad máxima 12.
                PRQuadtree<Servicio> arbol = new PRQuadtree<>(PLANO, 4, 12);
                datos.forEach(arbol::insertar);

                for (Region zona : zonas) {
                    // Ejecución inicial de calentamiento antes de tomar la medición impresa.
                    buscarLineal(datos, zona);
                    buscarEnQuadtree(arbol, zona);

                    MetricasConsulta<Servicio> lineal = buscarLineal(datos, zona);
                    MetricasConsulta<Servicio> qt = buscarEnQuadtree(arbol, zona);

                    Set<String> idsLineal = lineal.resultados().stream()
                            .map(EntradaEspacial::id)
                            .collect(Collectors.toSet());
                    Set<String> idsQt = qt.resultados().stream()
                            .map(EntradaEspacial::id)
                            .collect(Collectors.toSet());

                    // Garantiza que ambas estrategias devuelvan exactamente los mismos elementos.
                    assertEquals(idsLineal, idsQt);
                    assertTrue(arbol.nodosVisitadosUltimaConsulta() >= 1);

                    // Imprime resumen por escenario para recolectar evidencia experimental.
                    System.out.printf(
                            "%-8s | N=%-5d | Zona=%s | Hits=%-4d | Nodos=%-4d | RevQT=%-5d vs RevLin=%-5d | msQT=%.4f vs msLin=%.4f%n",
                            distribucion, n, zona, idsQt.size(),
                            arbol.nodosVisitadosUltimaConsulta(),
                            qt.comparaciones(), lineal.comparaciones(),
                            qt.nanos() / 1_000_000.0, lineal.nanos() / 1_000_000.0
                    );
                }
            }
        }
    }
}
