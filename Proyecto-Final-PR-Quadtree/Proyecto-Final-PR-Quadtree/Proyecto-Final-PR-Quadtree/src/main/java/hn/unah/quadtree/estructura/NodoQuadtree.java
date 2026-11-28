package hn.unah.quadtree.estructura;

import hn.unah.quadtree.modelo.EntradaEspacial;
import hn.unah.quadtree.modelo.Region;

import java.util.ArrayList;
import java.util.List;

/*
 * NodoQuadtree
 *
 * Nodo del PR Quadtree. Solo se usa dentro del paquete estructura.
 * Un nodo es hoja (guarda entradas en una lista y no tiene hijos) o es
 * interno (tiene 4 hijos y no guarda entradas).
 *
 * Hijos: 0 noroeste, 1 noreste, 2 suroeste, 3 sureste. Como y crece hacia
 * abajo, "sur" son los valores de y más grandes. Un punto justo en la línea
 * media va al lado derecho o al de abajo.
 *
 * Parámetros: region que cubre el nodo y su profundidad (la raíz es 0).
 * Dependencias: Region, EntradaEspacial.
 * Lo usa: PRQuadtree.
 */
final class NodoQuadtree<T> {
    static final int NOROESTE = 0;
    static final int NORESTE = 1;
    static final int SUROESTE = 2;
    static final int SURESTE = 3;

    private final Region region;
    private final int profundidad;
    private List<EntradaEspacial<T>> cubeta;
    private NodoQuadtree<T>[] hijos;

    NodoQuadtree(Region region, int profundidad) {
        this.region = region;
        this.profundidad = profundidad;
        this.cubeta = new ArrayList<>();
    }

    Region region() {
        return region;
    }

    int profundidad() {
        return profundidad;
    }

    boolean esHoja() {
        return hijos == null;
    }

    // Entradas de la hoja. Solo se llama en hojas.
    List<EntradaEspacial<T>> entradas() {
        return cubeta;
    }

    int cantidadEntradas() {
        return esHoja() ? cubeta.size() : 0;
    }

    void agregar(EntradaEspacial<T> entrada) {
        cubeta.add(entrada);
    }

    NodoQuadtree<T> hijo(int indice) {
        return hijos[indice];
    }

    // Devuelve el hijo donde cae el punto. Solo se llama en nodos internos.
    NodoQuadtree<T> hijoPara(double x, double y) {
        return hijos[indiceCuadrante(x, y)];
    }

    // Calcula el número de cuadrante (0 a 3) del punto.
    int indiceCuadrante(double x, double y) {
        boolean este = x >= region.centroX();
        boolean sur = y >= region.centroY();
        return (sur ? SUROESTE : NOROESTE) + (este ? 1 : 0);
    }

    // Revisa que el punto medio todavía separe la región en dos partes.
    // Con regiones muy pequeñas el redondeo de double podría impedirlo.
    boolean puedeSubdividirse() {
        double cx = region.centroX();
        double cy = region.centroY();
        return region.minX() < cx && cx < region.maxX()
                && region.minY() < cy && cy < region.maxY();
    }

    // Convierte la hoja en nodo interno con 4 hijos vacíos y devuelve las
    // entradas que tenía para que PRQuadtree las reparta.
    List<EntradaEspacial<T>> dividir() {
        double cx = region.centroX();
        double cy = region.centroY();
        int p = profundidad + 1;

        @SuppressWarnings("unchecked")
        NodoQuadtree<T>[] nuevos = (NodoQuadtree<T>[]) new NodoQuadtree<?>[4];
        nuevos[NOROESTE] = new NodoQuadtree<>(new Region(region.minX(), region.minY(), cx, cy), p);
        nuevos[NORESTE] = new NodoQuadtree<>(new Region(cx, region.minY(), region.maxX(), cy), p);
        nuevos[SUROESTE] = new NodoQuadtree<>(new Region(region.minX(), cy, cx, region.maxY()), p);
        nuevos[SURESTE] = new NodoQuadtree<>(new Region(cx, cy, region.maxX(), region.maxY()), p);

        List<EntradaEspacial<T>> anteriores = cubeta;
        cubeta = null;
        hijos = nuevos;
        return anteriores;
    }
}
