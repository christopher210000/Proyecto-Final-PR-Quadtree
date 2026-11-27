
package proyecto_ed;

import java.util.Objects;

public class BoundedSpatialEntry<T> implements SpatialItem {
    private final double x;
    private final double y;
    private final Region bounds;
    private final T data;

    public BoundedSpatialEntry(double x, double y, double coverageRadius, T data) {
        validateFiniteCoordinates(x, y);
        if (coverageRadius <= 0 || Double.isNaN(coverageRadius) || Double.isInfinite(coverageRadius)) {
            throw new IllegalArgumentException("El radio de cobertura debe ser un número positivo y finito.");
        }
        this.x = x;
        this.y = y;
        this.bounds = Region.fromCenterAndRadius(x, y, coverageRadius);
        this.data = Objects.requireNonNull(data, "Los datos del servicio no pueden ser nulos.");
    }

    public BoundedSpatialEntry(double x, double y, Region bounds, T data) {
        validateFiniteCoordinates(x, y);
        this.x = x;
        this.y = y;
        this.bounds = Objects.requireNonNull(bounds, "La región de cobertura no puede ser nula.");
        this.data = Objects.requireNonNull(data, "Los datos del servicio no pueden ser nulos.");
    }

    private static void validateFiniteCoordinates(double x, double y) {
        if (Double.isNaN(x) || Double.isNaN(y) || Double.isInfinite(x) || Double.isInfinite(y)) {
            throw new IllegalArgumentException("Las coordenadas deben ser números finitos.");
        }
    }

    @Override
    public double getX() {
        return x;
    }

    @Override
    public double getY() {
        return y;
    }

    public Region getBounds() {
        return bounds;
    }

    public T getData() {
        return data;
    }

    public boolean intersects(Region area) {
        return area != null && bounds.intersects(area);
    }

    public boolean coversPoint(double px, double py) {
        return bounds.contains(px, py);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BoundedSpatialEntry<?> that)) return false;
        return Double.compare(that.x, x) == 0
                && Double.compare(that.y, y) == 0
                && Objects.equals(bounds, that.bounds)
                && Objects.equals(data, that.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, bounds, data);
    }

    @Override
    public String toString() {
        return String.format("BoundedEntry(%.4f, %.4f) %s -> %s", x, y, bounds, data);
    }
}