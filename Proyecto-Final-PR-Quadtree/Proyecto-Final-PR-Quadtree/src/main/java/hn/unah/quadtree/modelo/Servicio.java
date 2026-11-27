package hn.unah.quadtree.modelo;

/*
 * Servicio
 *
 * Datos de un servicio de salud: nombre y tipo. La ubicación no va aquí,
 * va en la EntradaEspacial que lo contiene.
 *
 * Parámetros: nombre (no vacío) y tipo (no nulo). Es inmutable.
 * Dependencias: TipoServicio.
 * Lo usan: LocalizadorServicios, Aplicacion.
 */
public record Servicio(String nombre, TipoServicio tipo) {

    public Servicio {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre del servicio no puede ser nulo ni vacío");
        }
        if (tipo == null) {
            throw new IllegalArgumentException("El tipo de servicio no puede ser nulo");
        }
    }
}
