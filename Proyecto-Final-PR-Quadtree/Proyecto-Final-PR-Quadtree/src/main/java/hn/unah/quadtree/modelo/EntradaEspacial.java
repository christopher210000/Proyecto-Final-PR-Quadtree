package hn.unah.quadtree.modelo;

/*
 * EntradaEspacial
 *
 * Es lo que se guarda en el quadtree: un identificador, un punto (x, y) y un
 * dato de cualquier tipo T (en la aplicación, un Servicio).
 *
 * Parámetros: id (no vacío), x e y (finitos) y dato (no nulo).
 * TDA: EntradaEspacial<T> (docs/TDA.md). Es inmutable.
 * No revisa si el punto está dentro del plano; eso lo hace PRQuadtree al insertar.
 * Dependencias: ninguna.
 * La usan: NodoQuadtree, PRQuadtree, LocalizadorServicios, Aplicacion.
 */
public record EntradaEspacial<T>(String id, double x, double y, T dato) {

    public EntradaEspacial {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("El identificador no puede ser nulo ni vacío");
        }
        if (!Double.isFinite(x) || !Double.isFinite(y)) {
            throw new IllegalArgumentException("Las coordenadas deben ser finitas");
        }
        if (dato == null) {
            throw new IllegalArgumentException("El dato no puede ser nulo");
        }
    }
}
