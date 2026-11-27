
package proyecto_ed;

import java.util.Objects;

/**
 *
 * @author USUARIO
 */
// [IMPLEMENTACIÓN PRINCIPAL] Entrada puntual: asocia coordenadas (x, y) con los datos del servicio (T).
public class SpatialEntry<T> implements SpatialItem {
    // Coordenadas inmutables para evitar que un objeto cambie de posición sin reinsertarse en el árbol.
    private final double x;
    private final double y;

    // Objeto de dominio asociado al punto (ej. nombre, categoría, horario, disponibilidad).
    private final T data;

    // [CONSTRUCTOR Y VALIDACIONES] Garantiza coordenadas numéricas válidas y datos no nulos.
    public SpatialEntry(double x, double y, T data) {
        if (Double.isNaN(x) || Double.isNaN(y) || Double.isInfinite(x) || Double.isInfinite(y)) {
            throw new IllegalArgumentException("Las coordenadas deben ser números finitos.");
        }
        this.data = Objects.requireNonNull(data, "Los datos del servicio no pueden ser nulos.");
        this.x = x;
        this.y = y;
    }

    // [IMPLEMENTACIÓN SpatialItem] Expone la coordenada X para ubicar el punto en los cuadrantes.
    @Override
    public double getX() {
        return x;
    }

    // [IMPLEMENTACIÓN SpatialItem] Expone la coordenada Y para ubicar el punto en los cuadrantes.
    @Override
    public double getY() {
        return y;
    }

    // [ACCESO A DATOS] Recupera el servicio almacenado en esta entrada espacial.
    public T getData() {
        return data;
    }

    // [CÁLCULO DE DISTANCIA - RÁPIDO] Distancia euclidiana al cuadrado (dx^2 + dy^2).
    // Se usa en filtros y ordenamientos para evitar el costo de Math.sqrt en cada comparación.
    public double distanceSquaredTo(double targetX, double targetY) {
        double dx = this.x - targetX;
        double dy = this.y - targetY;
        return dx * dx + dy * dy;
    }

    // [CÁLCULO DE DISTANCIA - EXACTA] Distancia euclidiana real hasta un punto objetivo.
    // Útil para mostrar la distancia final al usuario en los resultados del localizador.
    public double distanceTo(double targetX, double targetY) {
        return Math.sqrt(distanceSquaredTo(targetX, targetY));
    }

    // [FILTRO POR RADIO] Comprueba si el servicio está dentro de un círculo de búsqueda.
    // Complementa a Region.intersects(...) tras obtener los candidatos de un cuadro delimitador.
    public boolean isWithinRadius(double targetX, double targetY, double radius) {
        if (radius < 0) {
            throw new IllegalArgumentException("El radio no puede ser negativo.");
        }
        return distanceSquaredTo(targetX, targetY) <= radius * radius;
    }

    // [PERTENENCIA A REGIÓN] Delega a Region.contains(x, y) para saber si cae en un cuadrante o área.
    public boolean isInside(Region region) {
        return region != null && region.contains(x, y);
    }

    // [IGUALDAD Y HASH] Necesario para eliminar o buscar entradas exactas dentro de listas o nodos.
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SpatialEntry<?> that)) return false;
        return Double.compare(that.x, x) == 0
                && Double.compare(that.y, y) == 0
                && Objects.equals(data, that.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, data);
    }

    @Override
    public String toString() {
        return String.format("SpatialEntry(%.4f, %.4f) -> %s", x, y, data);
    }
}
