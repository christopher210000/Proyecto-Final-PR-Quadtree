
package proyecto_ed;

import java.util.Objects;

public class Region {
    // Esquina superior izquierda o mínima en X e Y, más ancho y alto.
    private final double minX;
    private final double minY;
    private final double width;
    private final double height;

    public Region(double minX, double minY, double width, double height) {
        if (Double.isNaN(minX) || Double.isNaN(minY)
                || Double.isNaN(width) || Double.isNaN(height)
                || Double.isInfinite(minX) || Double.isInfinite(minY)
                || Double.isInfinite(width) || Double.isInfinite(height)) {
            throw new IllegalArgumentException("Los parámetros de la región deben ser finitos.");
        }
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("El ancho y el alto de la región deben ser positivos.");
        }
        this.minX = minX;
        this.minY = minY;
        this.width = width;
        this.height = height;
    }

    public static Region fromCenterAndRadius(double centerX, double centerY, double radius) {
        if (radius <= 0 || Double.isNaN(radius) || Double.isInfinite(radius)) {
            throw new IllegalArgumentException("El radio debe ser positivo y finito.");
        }
        return new Region(centerX - radius, centerY - radius, radius * 2.0, radius * 2.0);
    }

    public double getMinX() {
        return minX;
    }

    public double getMinY() {
        return minY;
    }

    public double getMaxX() {
        return minX + width;
    }

    public double getMaxY() {
        return minY + height;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

    public boolean contains(double x, double y) {
        return x >= minX && x <= getMaxX()
                && y >= minY && y <= getMaxY();
    }

    public boolean intersects(Region other) {
        if (other == null) {
            return false;
        }
        return !(other.minX > getMaxX()
                || other.getMaxX() < minX
                || other.minY > getMaxY()
                || other.getMaxY() < minY);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Region region)) return false;
        return Double.compare(region.minX, minX) == 0
                && Double.compare(region.minY, minY) == 0
                && Double.compare(region.width, width) == 0
                && Double.compare(region.height, height) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(minX, minY, width, height);
    }

    @Override
    public String toString() {
        return String.format("Region[x=%.4f..%.4f, y=%.4f..%.4f]", minX, getMaxX(), minY, getMaxY());
    }
}
