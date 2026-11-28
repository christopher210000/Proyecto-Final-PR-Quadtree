package hn.unah.quadtree.modelo;

/*
 * TipoServicio
 *
 * Tipos de servicio de salud que maneja el localizador. Cada uno tiene un
 * texto para mostrarlo en consola.
 * Dependencias: ninguna.
 * Lo usan: Servicio, LocalizadorServicios, Aplicacion.
 */
public enum TipoServicio {
    HOSPITAL("Hospital"),
    FARMACIA("Farmacia"),
    CENTRO_SALUD("Centro de salud");

    private final String descripcion;

    TipoServicio(String descripcion) {
        this.descripcion = descripcion;
    }

    public String descripcion() {
        return descripcion;
    }
}
