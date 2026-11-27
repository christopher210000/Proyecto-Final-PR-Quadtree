package hn.unah.quadtree.estructura;

import hn.unah.quadtree.modelo.EntradaEspacial;
import hn.unah.quadtree.modelo.Region;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/*
 * PRQuadtreeTest
 *
 * Pruebas de inserción, subdivisión, redistribución, consulta rectangular y
 * recorrido. Cada prueba crea un árbol nuevo sobre [0,1024) x [0,1024) con
 * capacidad 4 y profundidad 12, salvo las que necesitan otros valores.
 * Prueba: PRQuadtree (y de paso NodoQuadtree y Region).
 */
class PRQuadtreeTest {
    private static final Region PLANO = new Region(0, 0, 1024, 1024);
    private PRQuadtree<String> arbol;

    @BeforeEach
    void crearArbol() {
        arbol = new PRQuadtree<>(PLANO, 4, 12);
    }

    private static EntradaEspacial<String> e(String id, double x, double y) {
        return new EntradaEspacial<>(id, x, y, "dato-" + id);
    }

    private static Set<String> ids(List<EntradaEspacial<String>> entradas) {
        return entradas.stream().map(EntradaEspacial::id).collect(Collectors.toSet());
    }

    // ---- Árbol vacío y un solo registro ----

    @Test
    @DisplayName("Un árbol nuevo está vacío, tiene un nodo y altura 0")
    void arbolVacio() {
        assertTrue(arbol.estaVacio());
        assertEquals(0, arbol.tamanio());
        assertEquals(1, arbol.cantidadNodos());
        assertEquals(0, arbol.altura());
        assertTrue(arbol.consultar(PLANO).isEmpty());
        assertTrue(arbol.recorrer().isEmpty());
    }

    @Test
    @DisplayName("Con un solo punto, la consulta total lo devuelve")
    void unSoloPunto() {
        arbol.insertar(e("A", 10, 20));
        assertEquals(1, arbol.tamanio());
        assertEquals(Set.of("A"), ids(arbol.consultar(PLANO)));
    }

    // ---- Subdivisión y redistribución ----

    @Test
    @DisplayName("Con 4 puntos no se subdivide; el quinto provoca la subdivisión")
    void subdivideAlSuperarCapacidad() {
        arbol.insertar(e("A", 100, 100));
        arbol.insertar(e("B", 900, 100));
        arbol.insertar(e("C", 100, 900));
        arbol.insertar(e("D", 900, 900));
        assertEquals(1, arbol.cantidadNodos());
        assertEquals(0, arbol.altura());

        arbol.insertar(e("E", 200, 200));
        assertEquals(5, arbol.cantidadNodos());
        assertEquals(1, arbol.altura());
        assertEquals(5, arbol.tamanio());
        assertEquals(Set.of("A", "B", "C", "D", "E"), ids(arbol.recorrer()));
    }

    @Test
    @DisplayName("Si todos los puntos caen en un cuadrante, se subdivide en cascada")
    void subdivisionEnCascada() {
        // Los 5 puntos están en [0,128) y se separan hasta la profundidad 4: 1 + 4*4 = 17 nodos.
        arbol.insertar(e("A", 10, 10));
        arbol.insertar(e("B", 20, 20));
        arbol.insertar(e("C", 70, 10));
        arbol.insertar(e("D", 10, 70));
        arbol.insertar(e("E", 70, 70));
        assertEquals(4, arbol.altura());
        assertEquals(17, arbol.cantidadNodos());
        assertEquals(5, arbol.recorrer().size());
    }

    @Test
    @DisplayName("Puntos coincidentes se conservan al llegar a la profundidad máxima")
    void puntosCoincidentesRespetanProfundidadMaxima() {
        PRQuadtree<String> t = new PRQuadtree<>(PLANO, 1, 3);
        t.insertar(e("A", 300, 300));
        t.insertar(e("B", 300, 300));
        t.insertar(e("C", 300, 300));
        assertEquals(3, t.tamanio());
        assertEquals(3, t.altura());
        assertEquals(13, t.cantidadNodos()); // 1 + 4 + 4 + 4
        assertEquals(Set.of("A", "B", "C"), ids(t.consultar(new Region(299, 299, 301, 301))));
    }

    @Test
    @DisplayName("Con profundidad máxima 0 la raíz nunca se subdivide")
    void profundidadMaximaCero() {
        PRQuadtree<String> t = new PRQuadtree<>(PLANO, 1, 0);
        for (int i = 0; i < 10; i++) {
            t.insertar(e("P" + i, i * 100, i * 100));
        }
        assertEquals(1, t.cantidadNodos());
        assertEquals(10, t.consultar(PLANO).size());
    }

    // ---- Límites del dominio ----

    @Test
    @DisplayName("El mínimo 0 se acepta y el máximo 1024 se rechaza")
    void limitesDelDominio() {
        arbol.insertar(e("O", 0, 0));
        arbol.insertar(e("M", 1023.999, 1023.999));
        assertThrows(IllegalArgumentException.class, () -> arbol.insertar(e("X", 1024, 0)));
        assertThrows(IllegalArgumentException.class, () -> arbol.insertar(e("Y", 0, 1024)));
        assertThrows(IllegalArgumentException.class, () -> arbol.insertar(e("Z", -0.001, 5)));
        assertEquals(2, arbol.tamanio());
    }

    @Test
    @DisplayName("Un punto en 512 pertenece a la mitad derecha o inferior")
    void puntoMedioVaALaMitadDerechaEInferior() {
        for (int i = 0; i < 5; i++) { // fuerza la subdivisión de la raíz
            arbol.insertar(e("P" + i, 512, 512));
        }
        assertTrue(arbol.consultar(new Region(0, 0, 512, 512)).isEmpty());
        assertEquals(5, arbol.consultar(new Region(512, 512, 1024, 1024)).size());
        assertTrue(arbol.consultar(new Region(0, 512, 512, 1024)).isEmpty());
        assertTrue(arbol.consultar(new Region(512, 0, 1024, 512)).isEmpty());
    }

    // ---- Consulta rectangular ----

    @Test
    @DisplayName("Consulta que cruza cuadrantes devuelve solo los puntos de la zona")
    void consultaQueCruzaCuadrantes() {
        arbol.insertar(e("NO", 500, 500));
        arbol.insertar(e("NE", 520, 500));
        arbol.insertar(e("SO", 500, 520));
        arbol.insertar(e("SE", 520, 520));
        arbol.insertar(e("LEJOS", 10, 10));
        assertEquals(Set.of("NO", "NE", "SO", "SE"), ids(arbol.consultar(new Region(490, 490, 530, 530))));
    }

    @Test
    @DisplayName("Consulta sin coincidencias o fuera del plano devuelve lista vacía")
    void consultaSinCoincidencias() {
        arbol.insertar(e("A", 100, 100));
        assertTrue(arbol.consultar(new Region(600, 600, 700, 700)).isEmpty());
        assertTrue(arbol.consultar(new Region(2000, 2000, 3000, 3000)).isEmpty());
        assertTrue(arbol.consultar(new Region(-50, -50, 0, 0)).isEmpty()); // solo toca el borde
    }

    @Test
    @DisplayName("Consulta parcialmente fuera del plano procesa la parte que coincide")
    void consultaParcialmenteExterior() {
        arbol.insertar(e("O", 0, 0));
        arbol.insertar(e("A", 40, 40));
        arbol.insertar(e("B", 60, 60));
        Region zona = new Region(-100, -100, 50, 50);
        assertEquals(Set.of("O", "A"), ids(arbol.consultar(zona)));
        assertEquals(new Region(-100, -100, 50, 50), zona);
    }

    @Test
    @DisplayName("La consulta descarta regiones que no se cruzan con la zona")
    void laConsultaPodaRegiones() {
        Random r = new Random(7);
        for (int i = 0; i < 1000; i++) {
            arbol.insertar(e("P" + i, r.nextDouble() * 1024, r.nextDouble() * 1024));
        }
        arbol.consultar(new Region(0, 0, 64, 64));
        assertTrue(arbol.entradasRevisadasUltimaConsulta() < arbol.tamanio() / 10,
                "revisó " + arbol.entradasRevisadasUltimaConsulta() + " de " + arbol.tamanio());
    }

    @Test
    @DisplayName("La consulta coincide con una búsqueda lineal sobre datos aleatorios")
    void coincideConBusquedaLineal() {
        Random r = new Random(42);
        List<EntradaEspacial<String>> todos = new ArrayList<>();
        for (int i = 0; i < 2000; i++) {
            EntradaEspacial<String> en = e("P" + i, r.nextInt(1024) + r.nextDouble(), r.nextInt(1024));
            todos.add(en);
            arbol.insertar(en);
        }
        for (int q = 0; q < 100; q++) {
            double x1 = r.nextDouble() * 1200 - 100;
            double y1 = r.nextDouble() * 1200 - 100;
            Region zona = new Region(x1, y1, x1 + 1 + r.nextDouble() * 400, y1 + 1 + r.nextDouble() * 400);
            Set<String> esperado = todos.stream()
                    .filter(p -> zona.contiene(p.x(), p.y()))
                    .map(EntradaEspacial::id).collect(Collectors.toSet());
            List<EntradaEspacial<String>> obtenido = arbol.consultar(zona);
            assertEquals(esperado.size(), obtenido.size(), "duplicados o faltantes en " + zona);
            assertEquals(esperado, ids(obtenido), "zona " + zona);
        }
    }

    // ---- Recorrido ----

    @Test
    @DisplayName("recorrer devuelve una lista nueva que no expone las cubetas")
    void recorrerDevuelveCopia() {
        arbol.insertar(e("A", 1, 1));
        List<EntradaEspacial<String>> lista = arbol.recorrer();
        lista.clear();
        assertEquals(1, arbol.recorrer().size());
        assertEquals(1, arbol.consultar(PLANO).size());
    }

    // ---- Entradas inválidas ----

    @Test
    @DisplayName("El constructor rechaza parámetros inválidos")
    void constructorInvalido() {
        assertThrows(IllegalArgumentException.class, () -> new PRQuadtree<String>(null, 4, 12));
        assertThrows(IllegalArgumentException.class, () -> new PRQuadtree<String>(PLANO, 0, 12));
        assertThrows(IllegalArgumentException.class, () -> new PRQuadtree<String>(PLANO, 4, -1));
    }

    @Test
    @DisplayName("Insertar o consultar con null lanza IllegalArgumentException")
    void nulosRechazados() {
        assertThrows(IllegalArgumentException.class, () -> arbol.insertar(null));
        assertThrows(IllegalArgumentException.class, () -> arbol.consultar(null));
        assertFalse(arbol.tamanio() > 0);
    }
}
