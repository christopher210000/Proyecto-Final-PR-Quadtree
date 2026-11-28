# Nuestro plan de trabajo para el avance
## Nuestro objetivo
Presentaremos investigación, implementación parcial con primeras pruebas JUnit pasando, informe parcial y bitácora de IA actualizada. No presentaremos como terminadas funciones que aún no hemos desarrollado.
## Primera jornada
Coordinaremos el contrato, las reglas y el repositorio. Distribuiremos la investigación y el informe con Christopher, los modelos y operaciones con Brayan y Elvin y las pruebas con Miguel desde el inicio. Integraremos una primera ejecución antes de terminar la jornada.
## Segunda jornada
Completaremos inserción, subdivisión y consulta; resolveremos los fallos, ejecutaremos las pruebas desde una descarga limpia, reuniremos capturas reales y ensayaremos la demostración. Priorizaremos el núcleo sobre la interfaz.
## Nuestras responsabilidades individuales
- Christopher: coordinaré, investigaré, justificaré la elección e integraré el informe y los aportes del equipo.
- Brayan: implementaré Region y EntradaEspacial; acordaré NodoQuadtree con Elvin y revisaré contención, intersección y bordes.
- Elvin: implementaré inserción, subdivisión, redistribución y consulta rectangular, sin dedicar tiempo a Swing en esta etapa.
- Miguel: implementaré pruebas de las operaciones acordadas, compararé las consultas con búsqueda lineal y registraré sus resultados reales.
## Nuestras pruebas prioritarias
Comprobaremos árbol vacío, un punto, subdivisión al superar cuatro entradas, consulta sin coincidencias y consulta total, cruces entre cuadrantes, coordenadas en 512, mínimo 0 aceptado y máximo 1024 rechazado, puntos coincidentes, profundidad máxima, nulos y valores no finitos.
## Nuestro criterio para integrar
Ejecutaremos mvn test, revisaremos los cambios con otro integrante y registraremos qué funciona y qué falta. Explicaremos el código que aportemos y documentaremos los usos de IA con sus correcciones.
