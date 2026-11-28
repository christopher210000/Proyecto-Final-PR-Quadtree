package hn.unah.quadtree.aplicacion;

import hn.unah.quadtree.modelo.EntradaEspacial;
import hn.unah.quadtree.modelo.Servicio;
import hn.unah.quadtree.modelo.TipoServicio;

import java.util.Comparator;
import java.util.List;

/*
 * Aplicacion
 *
 * Punto de entrada del programa. Arma un plano de ejemplo con 13 servicios,
 * hace varias consultas por zona (incluyendo bordes y zonas fuera del plano)
 * y muestra en consola los resultados y cuántos nodos y entradas revisó el árbol.
 * Al final prueba algunos registros inválidos.
 *
 * No recibe argumentos.
 * Dependencias: LocalizadorServicios, EntradaEspacial, Servicio, TipoServicio.
 * Lo usan: AplicacionTest y LocalizadorServiciosTest (plano de ejemplo).
 */
public final class Aplicacion {
    private Aplicacion() { }

    public static String mensajeInicio() {
        return "Localizador de servicios - PR Quadtree";
    }

    // Crea el plano de ejemplo. El grupo del noroeste tiene más de 4 servicios
    // para que el árbol se tenga que dividir.
    static LocalizadorServicios crearPlanoDeEjemplo() {
        LocalizadorServicios loc = new LocalizadorServicios();
        loc.registrar("H-01", "Hospital Norte", TipoServicio.HOSPITAL, 100, 120);
        loc.registrar("F-01", "Farmacia Central", TipoServicio.FARMACIA, 110, 130);
        loc.registrar("F-02", "Farmacia La Esperanza", TipoServicio.FARMACIA, 120, 140);
        loc.registrar("F-03", "Farmacia El Prado", TipoServicio.FARMACIA, 130, 100);
        loc.registrar("C-01", "Centro de Salud Las Colinas", TipoServicio.CENTRO_SALUD, 105, 125);
        loc.registrar("F-05", "Farmacia Esquina Cero", TipoServicio.FARMACIA, 0, 0);
        loc.registrar("H-02", "Hospital del Este", TipoServicio.HOSPITAL, 700, 150);
        loc.registrar("H-03", "Hospital del Sur", TipoServicio.HOSPITAL, 300, 900);
        loc.registrar("F-04", "Farmacia Punto Medio", TipoServicio.FARMACIA, 512, 512);
        loc.registrar("C-02", "Centro de Salud El Pedregal", TipoServicio.CENTRO_SALUD, 800, 800);
        loc.registrar("C-03", "Centro de Salud La Ribera", TipoServicio.CENTRO_SALUD, 511.5, 600);
        loc.registrar("C-04", "Centro de Salud Extremo", TipoServicio.CENTRO_SALUD, 1023.9, 1023.9);
        // Mismo punto que C-01: se permite.
        loc.registrar("F-06", "Farmacia Las Colinas", TipoServicio.FARMACIA, 105, 125);
        return loc;
    }

    public static void main(String[] args) {
        System.out.println(mensajeInicio());
        System.out.println("Plano: [0,1024) x [0,1024); capacidad 4; profundidad máxima 12");
        System.out.println();

        LocalizadorServicios loc = crearPlanoDeEjemplo();
        System.out.printf("Servicios registrados: %d | nodos: %d | altura: %d%n",
                loc.cantidadServicios(), loc.cantidadNodos(), loc.altura());

        consultar(loc, "Zona noroeste pequeña", 0, 0, 256, 256);
        consultar(loc, "Zona que cruza los cuatro cuadrantes", 400, 400, 600, 700);
        consultar(loc, "Cuadrante [0,512): excluye el punto medio 512", 0, 0, 512, 512);
        consultar(loc, "Cuadrante [512,1024): incluye el punto medio 512", 512, 512, 1024, 1024);
        consultar(loc, "Zona parcialmente fuera del plano", 900, 900, 1500, 1500);
        consultar(loc, "Zona totalmente fuera del plano", 2000, 2000, 3000, 3000);
        consultar(loc, "Todo el plano", 0, 0, 1024, 1024);

        System.out.println();
        System.out.println("Validaciones:");
        intentar("ID repetido H-01", () ->
                loc.registrar("H-01", "Otro hospital", TipoServicio.HOSPITAL, 50, 50));
        intentar("Punto en x = 1024 (máximo excluido)", () ->
                loc.registrar("X-01", "Fuera del plano", TipoServicio.FARMACIA, 1024, 10));
        intentar("Coordenada NaN", () ->
                loc.registrar("X-02", "Sin coordenada", TipoServicio.FARMACIA, Double.NaN, 10));
    }

    // Hace una consulta y la imprime ordenada por id.
    private static void consultar(LocalizadorServicios loc, String titulo,
                                  double minX, double minY, double maxX, double maxY) {
        List<EntradaEspacial<Servicio>> resultado = loc.consultarZona(minX, minY, maxX, maxY);
        resultado.sort(Comparator.comparing(EntradaEspacial::id));
        System.out.println();
        System.out.printf("%s [%s, %s) x [%s, %s)%n", titulo,
                fmt(minX), fmt(maxX), fmt(minY), fmt(maxY));
        System.out.printf("  Encontrados: %d | nodos visitados: %d | entradas revisadas: %d de %d%n",
                resultado.size(), loc.nodosVisitadosUltimaConsulta(),
                loc.entradasRevisadasUltimaConsulta(), loc.cantidadServicios());
        for (EntradaEspacial<Servicio> e : resultado) {
            System.out.printf("  - %-5s %-16s %-30s (%s, %s)%n", e.id(),
                    e.dato().tipo().descripcion(), e.dato().nombre(), fmt(e.x()), fmt(e.y()));
        }
    }

    // Ejecuta un registro y muestra si se aceptó o por qué se rechazó.
    private static void intentar(String descripcion, Runnable accion) {
        try {
            accion.run();
            System.out.println("  " + descripcion + ": aceptado");
        } catch (IllegalArgumentException ex) {
            System.out.println("  " + descripcion + ": rechazado -> " + ex.getMessage());
        }
    }

    // Muestra los números enteros sin ".0".
    private static String fmt(double v) {
        return v == Math.rint(v) ? String.valueOf((long) v) : String.valueOf(v);
    }
}
