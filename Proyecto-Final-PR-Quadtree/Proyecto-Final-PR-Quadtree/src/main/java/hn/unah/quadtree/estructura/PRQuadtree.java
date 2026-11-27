package hn.unah.quadtree.estructura;

import hn.unah.quadtree.modelo.EntradaEspacial;
import hn.unah.quadtree.modelo.Region;

import java.util.ArrayList;
import java.util.List;

/*
 * PRQuadtree
 *
 * Índice espacial que guarda puntos dentro de una región fija y permite
 * buscar cuáles caen dentro de un rectángulo sin revisar todos.
 * Los puntos solo se guardan en las hojas. Cuando una hoja pasa de
 * "capacidad" puntos se divide en 4 y reparte sus puntos entre los hijos.
 * Al llegar a la profundidad máxima ya no se divide y la hoja puede pasar de
 * la capacidad; así los puntos repetidos no dividen el árbol para siempre.
 *
 * Constructor: limite (Region del plano), capacidad (> 0) y
 * profundidadMaxima (>= 0). Si no se indican, se usan 4 y 12.
 * TDA: PRQuadtree<T> (docs/TDA.md).
 * Dependencias: Region, EntradaEspacial, NodoQuadtree.
 * Lo usa: LocalizadorServicios.
 *
 * Además guarda cuántos nodos y entradas revisó la última consulta, para
 * comparar después con la búsqueda lineal.
 */
public final class PRQuadtree<T> {
    public static final int CAPACIDAD_PREDETERMINADA = 4;
    public static final int PROFUNDIDAD_MAXIMA_PREDETERMINADA = 12;

    private final Region limite;
    private final int capacidad;
    private final int profundidadMaxima;
    private final NodoQuadtree<T> raiz;
    private int tamanio;

    private int nodosVisitadosUltimaConsulta;
    private int entradasRevisadasUltimaConsulta;

    public PRQuadtree(Region limite) {
        this(limite, CAPACIDAD_PREDETERMINADA, PROFUNDIDAD_MAXIMA_PREDETERMINADA);
    }

    public PRQuadtree(Region limite, int capacidad, int profundidadMaxima) {
        if (limite == null) {
            throw new IllegalArgumentException("El límite del árbol no puede ser nulo");
        }
        if (capacidad <= 0) {
            throw new IllegalArgumentException("La capacidad debe ser mayor que cero");
        }
        if (profundidadMaxima < 0) {
            throw new IllegalArgumentException("La profundidad máxima no puede ser negativa");
        }
        this.limite = limite;
        this.capacidad = capacidad;
        this.profundidadMaxima = profundidadMaxima;
        this.raiz = new NodoQuadtree<>(limite, 0);
    }

    // Inserta una entrada. Rechaza null y puntos fuera del límite.
    // Se permiten varias entradas en el mismo punto.
    public void insertar(EntradaEspacial<T> entrada) {
        if (entrada == null) {
            throw new IllegalArgumentException("La entrada no puede ser nula");
        }
        if (!limite.contiene(entrada.x(), entrada.y())) {
            throw new IllegalArgumentException("El punto (" + entrada.x() + ", " + entrada.y()
                    + ") está fuera del dominio " + limite);
        }

        // Bajamos por un solo cuadrante en cada nivel hasta llegar a una hoja.
        NodoQuadtree<T> nodo = raiz;
        while (!nodo.esHoja()) {
            nodo = nodo.hijoPara(entrada.x(), entrada.y());
        }
        nodo.agregar(entrada);
        tamanio++;

        if (debeSubdividirse(nodo)) {
            subdividir(nodo);
        }
    }

    // Una hoja se divide si pasa de la capacidad y todavía no está en la profundidad máxima.
    private boolean debeSubdividirse(NodoQuadtree<T> nodo) {
        return nodo.cantidadEntradas() > capacidad
                && nodo.profundidad() < profundidadMaxima
                && nodo.puedeSubdividirse();
    }

    // Divide la hoja y reparte sus entradas en los 4 hijos. Si un hijo queda
    // otra vez lleno (todos cayeron en el mismo cuadrante) se divide también.
    private void subdividir(NodoQuadtree<T> hoja) {
        List<EntradaEspacial<T>> entradas = hoja.dividir();
        for (EntradaEspacial<T> e : entradas) {
            hoja.hijoPara(e.x(), e.y()).agregar(e);
        }
        for (int i = 0; i < 4; i++) {
            NodoQuadtree<T> hijo = hoja.hijo(i);
            if (debeSubdividirse(hijo)) {
                subdividir(hijo);
            }
        }
    }

    // Devuelve las entradas que están dentro de la zona. La zona puede salirse
    // del plano; si no hay coincidencias devuelve una lista vacía.
    public List<EntradaEspacial<T>> consultar(Region zona) {
        if (zona == null) {
            throw new IllegalArgumentException("La zona de consulta no puede ser nula");
        }
        nodosVisitadosUltimaConsulta = 0;
        entradasRevisadasUltimaConsulta = 0;
        List<EntradaEspacial<T>> resultado = new ArrayList<>();
        consultar(raiz, zona, resultado);
        return resultado;
    }

    // Parte recursiva de la consulta. Si la región del nodo no toca la zona
    // la descarta sin revisar sus puntos.
    private void consultar(NodoQuadtree<T> nodo, Region zona, List<EntradaEspacial<T>> resultado) {
        nodosVisitadosUltimaConsulta++;
        if (!nodo.region().intersecta(zona)) {
            return;
        }
        if (nodo.esHoja()) {
            for (EntradaEspacial<T> e : nodo.entradas()) {
                entradasRevisadasUltimaConsulta++;
                if (zona.contiene(e.x(), e.y())) {
                    resultado.add(e);
                }
            }
            return;
        }
        for (int i = 0; i < 4; i++) {
            consultar(nodo.hijo(i), zona, resultado);
        }
    }

    // Devuelve todas las entradas en una lista nueva.
    public List<EntradaEspacial<T>> recorrer() {
        List<EntradaEspacial<T>> resultado = new ArrayList<>(tamanio);
        recorrer(raiz, resultado);
        return resultado;
    }

    private void recorrer(NodoQuadtree<T> nodo, List<EntradaEspacial<T>> resultado) {
        if (nodo.esHoja()) {
            resultado.addAll(nodo.entradas());
            return;
        }
        for (int i = 0; i < 4; i++) {
            recorrer(nodo.hijo(i), resultado);
        }
    }

    public int tamanio() {
        return tamanio;
    }

    public boolean estaVacio() {
        return tamanio == 0;
    }

    // Cuenta todos los nodos, internos y hojas. Un árbol nuevo tiene 1.
    public int cantidadNodos() {
        return cantidadNodos(raiz);
    }

    private int cantidadNodos(NodoQuadtree<T> nodo) {
        if (nodo.esHoja()) {
            return 1;
        }
        int total = 1;
        for (int i = 0; i < 4; i++) {
            total += cantidadNodos(nodo.hijo(i));
        }
        return total;
    }

    // Profundidad de la hoja más profunda. Sin divisiones es 0.
    public int altura() {
        return altura(raiz);
    }

    private int altura(NodoQuadtree<T> nodo) {
        if (nodo.esHoja()) {
            return nodo.profundidad();
        }
        int max = 0;
        for (int i = 0; i < 4; i++) {
            max = Math.max(max, altura(nodo.hijo(i)));
        }
        return max;
    }

    public Region limite() {
        return limite;
    }

    public int capacidad() {
        return capacidad;
    }

    public int profundidadMaxima() {
        return profundidadMaxima;
    }

    // Nodos revisados en la última consulta, contando los que se descartaron.
    public int nodosVisitadosUltimaConsulta() {
        return nodosVisitadosUltimaConsulta;
    }

    // Entradas comparadas con la zona en la última consulta.
    public int entradasRevisadasUltimaConsulta() {
        return entradasRevisadasUltimaConsulta;
    }
}
