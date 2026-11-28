package hn.unah.quadtree.modelo;

/*
 * Region
 *
 * Representa un rectángulo del plano. Se usa para el límite del quadtree,
 * para las regiones de cada nodo y para las zonas de consulta.
 * El rectángulo incluye el mínimo y excluye el máximo: [minX, maxX) x [minY, maxY).
 * x crece hacia la derecha y y hacia abajo.
 *
 * Constructor: minX, minY, maxX, maxY. Deben ser finitos y cada mínimo
 * debe ser menor que su máximo.
 * TDA: Region (docs/TDA.md). Es inmutable.
 * Dependencias: ninguna.
 * La usan: NodoQuadtree, PRQuadtree, LocalizadorServicios.
 */
public final class Region {
    private final double minX;
    private final double minY;
    private final double maxX;
    private final double maxY;

    public Region(double minX, double minY, double maxX, double maxY) {
        if (!Double.isFinite(minX) || !Double.isFinite(minY)
                || !Double.isFinite(maxX) || !Double.isFinite(maxY)) {
            throw new IllegalArgumentException("Los límites de la región deben ser finitos");
        }
        if (!(minX < maxX) || !(minY < maxY)) {
            throw new IllegalArgumentException(
                    "Los mínimos de la región deben ser estrictamente menores que los máximos");
        }
        this.minX = minX;
        this.minY = minY;
        this.maxX = maxX;
        this.maxY = maxY;
    }

    // Indica si el punto está dentro (el máximo no cuenta).
    public boolean contiene(double x, double y) {
        return x >= minX && x < maxX && y >= minY && y < maxY;
    }

    // Indica si las dos regiones comparten área. Tocarse en un borde no cuenta.
    public boolean intersecta(Region otra) {
        if (otra == null) {
            throw new IllegalArgumentException("La región a comparar no puede ser nula");
        }
        return minX < otra.maxX && otra.minX < maxX
                && minY < otra.maxY && otra.minY < maxY;
    }

    public double minX() { return minX; }
    public double minY() { return minY; }
    public double maxX() { return maxX; }
    public double maxY() { return maxY; }

    // Punto medio en x. Se divide antes de sumar para no desbordar con valores grandes.
    public double centroX() { return minX / 2.0 + maxX / 2.0; }

    // Punto medio en y.
    public double centroY() { return minY / 2.0 + maxY / 2.0; }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Region)) {
            return false;
        }
        Region r = (Region) o;
        return Double.compare(minX, r.minX) == 0 && Double.compare(minY, r.minY) == 0
                && Double.compare(maxX, r.maxX) == 0 && Double.compare(maxY, r.maxY) == 0;
    }

    @Override
    public int hashCode() {
        int h = Double.hashCode(minX);
        h = 31 * h + Double.hashCode(minY);
        h = 31 * h + Double.hashCode(maxX);
        return 31 * h + Double.hashCode(maxY);
    }

    @Override
    public String toString() {
        return "[" + minX + ", " + maxX + ") × [" + minY + ", " + maxY + ")";
    }
}
