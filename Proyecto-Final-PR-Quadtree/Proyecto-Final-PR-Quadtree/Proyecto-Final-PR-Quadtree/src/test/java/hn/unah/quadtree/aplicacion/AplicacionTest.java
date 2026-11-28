package hn.unah.quadtree.aplicacion;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

/** Solo comprueba la configuración de pruebas; no valida el quadtree. */
class AplicacionTest {
    @Test
    @DisplayName("El punto de entrada identifica el proyecto")
    void mensajeDeInicioIdentificaProyecto() {
        String esperado = "Localizador de servicios - PR Quadtree";
        String resultado = Aplicacion.mensajeInicio();
        assertEquals(esperado, resultado);
    }
}
