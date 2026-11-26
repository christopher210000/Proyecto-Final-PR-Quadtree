package hn.unah.quadtree.aplicacion;

/** Punto de entrada inicial; todavía no ejecuta operaciones espaciales. */
public final class Aplicacion {
    private Aplicacion() { }
    public static String mensajeInicio() {
        return "Localizador de servicios - PR Quadtree";
    }
    public static void main(String[] args) {
        System.out.println(mensajeInicio());
        System.out.println("Entorno inicial listo. Operaciones espaciales pendientes de implementar.");
    }
}
