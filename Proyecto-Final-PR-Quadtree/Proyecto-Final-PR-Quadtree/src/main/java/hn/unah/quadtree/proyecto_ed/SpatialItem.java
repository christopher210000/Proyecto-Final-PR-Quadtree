
package proyecto_ed;

/**
 *
 * @author USUARIO
 */
// [IMPLEMENTACIÓN BASE] Contrato común para cualquier objeto que tenga posición en el plano 2D.
public interface SpatialItem {
    // Coordenada horizontal (ej. longitud o posición X en el plano del mapa).
    double getX();

    // Coordenada vertical (ej. latitud o posición Y en el plano del mapa).
    double getY();
}

