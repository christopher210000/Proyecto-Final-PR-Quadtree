package hn.unah.quadtree.aplicacion;

import hn.unah.quadtree.estructura.PRQuadtree;
import hn.unah.quadtree.modelo.EntradaEspacial;
import hn.unah.quadtree.modelo.Region;
import hn.unah.quadtree.modelo.Servicio;
import hn.unah.quadtree.modelo.TipoServicio;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/*
 * LocalizadorServicios
 *
 * Une el quadtree con la aplicación. Registra servicios de salud en el plano
 * [0,1024) x [0,1024) y consulta cuáles están dentro de una zona.
 * Antes de insertar revisa que el id no se repita, usando un conjunto aparte
 * del quadtree (el árbol permite puntos repetidos y no busca por id).
 *
 * Constructor: sin parámetros usa capacidad 4 y profundidad 12; también se
 * pueden indicar capacidad y profundidadMaxima.
 * Dependencias: PRQuadtree, EntradaEspacial, Region, Servicio, TipoServicio.
 * Lo usa: Aplicacion.
 */
public final class LocalizadorServicios {
    public static final double TAMANIO_PLANO = 1024;
    public static final Region PLANO = new Region(0, 0, TAMANIO_PLANO, TAMANIO_PLANO);

    private final PRQuadtree<Servicio> indice;
    private final Set<String> identificadores = new HashSet<>();

    public LocalizadorServicios() {
        this(PRQuadtree.CAPACIDAD_PREDETERMINADA, PRQuadtree.PROFUNDIDAD_MAXIMA_PREDETERMINADA);
    }

    public LocalizadorServicios(int capacidad, int profundidadMaxima) {
        this.indice = new PRQuadtree<>(PLANO, capacidad, profundidadMaxima);
    }

    // Registra un servicio. Lanza IllegalArgumentException si los datos son
    // inválidos, si el id ya existe o si el punto está fuera del plano.
    public EntradaEspacial<Servicio> registrar(String id, String nombre, TipoServicio tipo,
                                               double x, double y) {
        EntradaEspacial<Servicio> entrada = new EntradaEspacial<>(id, x, y, new Servicio(nombre, tipo));
        if (identificadores.contains(id)) {
            throw new IllegalArgumentException("Ya existe un servicio con el identificador " + id);
        }
        indice.insertar(entrada);
        // El id se guarda hasta que la inserción sale bien.
        identificadores.add(id);
        return entrada;
    }

    // Servicios dentro de [minX, maxX) x [minY, maxY).
    public List<EntradaEspacial<Servicio>> consultarZona(double minX, double minY,
                                                         double maxX, double maxY) {
        return consultarZona(new Region(minX, minY, maxX, maxY));
    }

    public List<EntradaEspacial<Servicio>> consultarZona(Region zona) {
        return indice.consultar(zona);
    }

    // Todos los servicios registrados.
    public List<EntradaEspacial<Servicio>> servicios() {
        return indice.recorrer();
    }

    public boolean existe(String id) {
        return identificadores.contains(id);
    }

    public int cantidadServicios() {
        return indice.tamanio();
    }

    public int cantidadNodos() {
        return indice.cantidadNodos();
    }

    public int altura() {
        return indice.altura();
    }

    public int nodosVisitadosUltimaConsulta() {
        return indice.nodosVisitadosUltimaConsulta();
    }

    public int entradasRevisadasUltimaConsulta() {
        return indice.entradasRevisadasUltimaConsulta();
    }
}
