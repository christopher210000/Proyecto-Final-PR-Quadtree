# Nuestra bitácora de uso de IA

## Apoyo en infraestructura y documentación

- Fecha de la sesión: 19/11/2026.
- Herramienta utilizada: Arena.ai, Agent Mode.
- Propósito: solicitamos apoyo para preparar la base Maven con JUnit 5, el punto de entrada y una prueba de configuración, organizar el contrato del proyecto, verificar enlaces y distribuir las tareas.
- Prompt principal resumido: pedimos preparar los pasos A y B, comprobar fuentes de investigación y organizar el trabajo del avance. Después solicitamos revisar el repositorio público y redactar la documentación en primera persona.
- Alcance: recibimos apoyo en configuración, documentación y organización. Todavía no incorporamos operaciones del quadtree, interfaz ni mediciones.
- Revisión: reservamos el registro de decisiones aceptadas, corregidas o descartadas hasta completar nuestra revisión. No afirmamos haber validado personalmente lo que todavía no ejecutamos.
- Evidencia externa: contamos con una comprobación de compilación y prueba inicial realizada en el entorno de asistencia; repetiremos la ejecución en nuestros equipos.

## Avance 2: núcleo del quadtree

- Integrante: Elvin Josue Posadas Marquez.
- Fecha: 27/11/2026.
- Herramienta: Claude Code (Claude Opus 5.5).
- Para qué la usamos: le pedimos una primera versión de la inserción, la subdivisión y la consulta rectangular, siguiendo las reglas y el contrato que ya teníamos en el README. Para que compilara también generó `Region` y `EntradaEspacial`, que Brayan va a revisar. Después le pedimos un borrador del TDA (`docs/TDA.md`).
- Cómo lo comprobamos: corrimos `mvn test` (21 pruebas pasan) y la demostración por consola. Revisamos que la salida cumpla nuestras reglas: el punto 512 va a la mitad derecha o inferior, x = 1024 se rechaza y una zona fuera del plano devuelve una lista vacía.
- Pendiente: cada uno revisa su parte y anotamos aquí lo que cambiemos.

## Cómo registraremos los siguientes usos

Anotaremos integrante, fecha, herramienta, propósito, prompt principal, decisiones aceptadas, correcciones o descartes y el motivo de cada decisión. Añadiremos la forma en que verificamos el resultado.
