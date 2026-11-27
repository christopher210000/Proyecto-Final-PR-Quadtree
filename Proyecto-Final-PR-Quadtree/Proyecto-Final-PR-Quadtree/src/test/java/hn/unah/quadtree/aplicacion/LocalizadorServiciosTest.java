package hn.unah.quadtree.aplicacion;

import hn.unah.quadtree.modelo.TipoServicio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/*
 * LocalizadorServiciosTest
 *
 * Pruebas de la unión entre el quadtree y la aplicación: ids repetidos,
 * datos inválidos y consultas sobre el plano de ejemplo de Aplicacion.
 * Prueba: LocalizadorServicios y Aplicacion.crearPlanoDeEjemplo.
 */
class LocalizadorServiciosTest {

    @Test
    @DisplayName("Rechaza identificadores repetidos sin alterar el índice")
    void rechazaIdRepetido() {
        LocalizadorServicios loc = new LocalizadorServicios();
        loc.registrar("H-01", "Hospital Norte", TipoServicio.HOSPITAL, 10, 10);
        assertThrows(IllegalArgumentException.class,
                () -> loc.registrar("H-01", "Otro", TipoServicio.FARMACIA, 20, 20));
        assertEquals(1, loc.cantidadServicios());
    }

    @Test
    @DisplayName("Un punto fuera del plano no deja el identificador reservado")
    void puntoFueraNoReservaId() {
        LocalizadorServicios loc = new LocalizadorServicios();
        assertThrows(IllegalArgumentException.class,
                () -> loc.registrar("F-01", "Farmacia", TipoServicio.FARMACIA, 1024, 0));
        assertFalse(loc.existe("F-01"));
        loc.registrar("F-01", "Farmacia", TipoServicio.FARMACIA, 1023, 0);
        assertTrue(loc.existe("F-01"));
    }

    @Test
    @DisplayName("Rechaza identificador vacío, nombre vacío, tipo nulo y coordenadas no finitas")
    void datosInvalidos() {
        LocalizadorServicios loc = new LocalizadorServicios();
        assertThrows(IllegalArgumentException.class,
                () -> loc.registrar(" ", "Farmacia", TipoServicio.FARMACIA, 1, 1));
        assertThrows(IllegalArgumentException.class,
                () -> loc.registrar("A", "", TipoServicio.FARMACIA, 1, 1));
        assertThrows(IllegalArgumentException.class,
                () -> loc.registrar("A", "Farmacia", null, 1, 1));
        assertThrows(IllegalArgumentException.class,
                () -> loc.registrar("A", "Farmacia", TipoServicio.FARMACIA, Double.NaN, 1));
        assertThrows(IllegalArgumentException.class,
                () -> loc.registrar("A", "Farmacia", TipoServicio.FARMACIA, 1, Double.POSITIVE_INFINITY));
        assertEquals(0, loc.cantidadServicios());
    }

    @Test
    @DisplayName("El plano de ejemplo se subdivide y responde consultas sobre los límites")
    void planoDeEjemplo() {
        LocalizadorServicios loc = Aplicacion.crearPlanoDeEjemplo();
        assertEquals(13, loc.cantidadServicios());
        assertTrue(loc.cantidadNodos() > 1);
        assertEquals(13, loc.consultarZona(0, 0, 1024, 1024).size());
        assertTrue(loc.consultarZona(0, 0, 512, 512).stream().noneMatch(e -> e.id().equals("F-04")));
        assertTrue(loc.consultarZona(512, 512, 1024, 1024).stream().anyMatch(e -> e.id().equals("F-04")));
        assertTrue(loc.consultarZona(2000, 2000, 3000, 3000).isEmpty());
    }
}
