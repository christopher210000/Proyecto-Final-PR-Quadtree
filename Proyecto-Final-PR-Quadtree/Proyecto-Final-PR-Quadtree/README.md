# Localizador de servicios mediante PR Quadtree
ISC-211 · Universidad Nacional Autónoma de Honduras

## Nuestro objetivo
Desarrollaremos en Java un PR Quadtree propio para localizar servicios dentro de una zona rectangular de un mapa simulado. Para el avance nos concentraremos en inserción, subdivisión y consulta, junto con sus primeras pruebas unitarias.

## Nuestro equipo
- Christopher Escoto Calix: asumiré la coordinación, investigación, justificación e integración del informe.
- Brayan Jose Vasquez Duron: desarrollaré regiones, entradas espaciales y el diseño de nodos; revisaré los límites con Elvin.
- Elvin Josue Posadas Marquez: desarrollaré las operaciones del quadtree y su integración con la aplicación; abordaré la visualización después del núcleo.
- Miguel Angel Chinchilla Coello: desarrollaré pruebas JUnit 5, verificaré resultados y prepararé experimentos.

## Nuestro estado actual
Contamos con `Region`, `EntradaEspacial`, `NodoQuadtree` y `PRQuadtree` con inserción, subdivisión, redistribución, consulta rectangular y recorrido. La aplicación (`LocalizadorServicios` y `Aplicacion`) registra servicios con identificador único y muestra consultas por consola, incluidas zonas sobre los límites y fuera del plano. Hay 21 pruebas JUnit 5 pasando, entre ellas una comparación con búsqueda lineal sobre 2000 puntos aleatorios.

Pendiente: búsqueda y eliminación, visualización en Swing, experimentos de rendimiento con `System.nanoTime()` y ampliación de las pruebas de `Region` y `EntradaEspacial`.

## Nuestro entorno
Trabajaremos con JDK 17 y Maven 3.9.x instalado en PATH. Necesitaremos internet en la primera descarga de dependencias. Comprobaremos las versiones con `java -version` y `mvn -version`. No incluimos Maven Wrapper.

## Cómo ejecutaremos el proyecto
Conservamos la organización actual del repositorio: los archivos Maven están dentro de una carpeta adicional.
Desde una copia recién clonada:
```sh
git clone https://github.com/christopher210000/Proyecto-Final-PR-Quadtree.git
cd Proyecto-Final-PR-Quadtree
cd Proyecto-Final-PR-Quadtree
mvn test
mvn package
java -jar target/localizador-quadtree-1.0-SNAPSHOT.jar
```
Si ya estamos en la carpeta que contiene `pom.xml`, ejecutaremos directamente los últimos tres comandos.

## Nuestras reglas de implementación
1. Usaremos el dominio `[0,1024) × [0,1024)`: incluiremos el mínimo y excluiremos el máximo.
2. Haremos crecer x hacia la derecha e y hacia abajo. Asignaremos la igualdad con el punto medio a la mitad derecha o inferior.
3. Usaremos capacidad inicial de 4 puntos por hoja y profundidad máxima inicial de 12; ambos valores serán configurables. Admitiremos capacidad mayor que cero y profundidad máxima mayor o igual que cero, con raíz a profundidad cero.
4. Guardaremos puntos solamente en hojas. Crearemos cuatro hijos en cada subdivisión y redistribuiremos todos los puntos de la hoja.
5. Al alcanzar la profundidad máxima, permitiremos superar la capacidad para conservar puntos coincidentes sin subdividir indefinidamente.
6. Usaremos coordenadas double finitas. Rechazaremos datos nulos, identificadores vacíos, NaN, infinitos y puntos fuera del dominio con IllegalArgumentException, según corresponda a cada operación.
7. Construiremos rectángulos con mínimos estrictamente menores que máximos y límites finitos. No consideraremos intersección el simple contacto entre bordes.
8. Devolveremos una lista vacía para consultas válidas sin coincidencias, incluso si quedan fuera del plano. En consultas parcialmente exteriores procesaremos la zona coincidente sin modificar el rectángulo recibido.
9. Permitiremos servicios distintos en la misma ubicación. Validaremos identificadores únicos en la aplicación antes de insertar y contabilizaremos ese recorrido por separado del descenso espacial.
10. Mantendremos inmutables coordenadas y datos de cada entrada. Más adelante moveremos registros mediante eliminación y reinserción.
11. Implementaremos el núcleo sin bibliotecas de quadtree. Usaremos ArrayList únicamente para cubetas y resultados, no para sustituir el índice espacial. Documentaremos cualquier ajuste exigido por el docente.
12. Dejaremos eliminación y Swing para después del avance. No implementaremos compactación inicialmente.

## Nuestro contrato de trabajo
Implementamos estas firmas de forma compartida; ya están presentes en el código:
- `Region(double minX, double minY, double maxX, double maxY)`.
- `Region.contiene(double x, double y)` → boolean.
- `Region.intersecta(Region otra)` → boolean; rechazaremos null.
- `EntradaEspacial<T>(String id, double x, double y, T dato)`, con accesores `id()`, `x()`, `y()` y `dato()`.
- `NodoQuadtree<T>` como detalle interno, sin API pública para la aplicación.
- `PRQuadtree<T>(Region limite, int capacidad, int profundidadMaxima)`.
- `insertar(EntradaEspacial<T> entrada)` → void.
- `consultar(Region zona)` → `List<EntradaEspacial<T>>`, sin orden garantizado ni retorno null.
- `recorrer()` → una lista nueva, sin exponer las cubetas internas.
Definiremos búsqueda y eliminación posteriormente sin romper estas operaciones. La aplicación evitará reinserciones del mismo identificador mediante su validación global.

## Cómo nos distribuiremos los archivos
- Desde coordinación integraremos README, documentación y demostración con Christopher.
- Desarrollaremos Region y EntradaEspacial con Brayan; coordinaremos NodoQuadtree entre Brayan y Elvin.
- Desarrollaremos PRQuadtree con Elvin.
- Prepararemos las pruebas en src/test/java con Miguel desde el inicio.

## Nuestro flujo Git
Trabajaremos en ramas `feature/modelos`, `feature/quadtree`, `test/pruebas` y `docs/informe`. Haremos cambios pequeños, actualizaremos las ramas desde main y revisaremos cada integración con otro integrante. Ejecutaremos `mvn test` antes de integrar. No subiremos target, credenciales ni archivos personales del IDE.

## Cómo comprobaremos el avance
- [x] Implementaremos regiones y entradas (pruebas propias pendientes).
- [x] Integraremos inserción, subdivisión, redistribución y consulta.
- [x] Pasaremos pruebas espaciales, además de la inicial.
- [ ] Contrastaremos consultas con búsqueda lineal, comparando identificadores sin depender del orden.
- [x] Mostraremos por consola más de cuatro puntos y consultas sobre límites.
- [ ] Presentaremos investigación, informe parcial y bitácora actualizada.
- [ ] Ejecutaremos desde una descarga limpia en otro equipo.

Mantendremos los detalles en docs/TDA.md, docs/PLAN_48_HORAS.md, docs/FUENTES.md y docs/BITACORA_IA.md.
