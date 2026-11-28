# Tipos de datos abstractos del proyecto

Especificamos los TDA que usa el localizador de servicios. Separamos qué hace cada tipo (modelo abstracto y operaciones) de cómo lo representamos en Java (sección final). Las operaciones de búsqueda puntual y eliminación se especificarán en la siguiente entrega.

Notación: `n` es la cantidad de entradas, `c` la capacidad por hoja, `d` la profundidad máxima y `k` la cantidad de resultados de una consulta.

---

## 1. TDA Region

**Modelo abstracto.** Un rectángulo semiabierto `[minX, maxX) × [minY, maxY)` en el plano. Incluye los mínimos y excluye los máximos. El eje x crece hacia la derecha y el eje y hacia abajo.

**Invariante.** Los cuatro límites son finitos, `minX < maxX` y `minY < maxY`. Una región no cambia después de crearse.

| Operación | Precondición | Postcondición |
|---|---|---|
| `Region(minX, minY, maxX, maxY)` | Límites finitos; mínimos estrictamente menores que máximos. Si no se cumple: `IllegalArgumentException`. | Crea la región. |
| `contiene(x, y) → boolean` | Ninguna. | `true` si `minX ≤ x < maxX` y `minY ≤ y < maxY`; `false` en otro caso, incluso con NaN. |
| `intersecta(otra) → boolean` | `otra ≠ null`; si no: `IllegalArgumentException`. | `true` si ambas regiones comparten área. Tocarse solo en un borde **no** cuenta. |
| `centroX()`, `centroY() → double` | Ninguna. | Coordenadas del punto medio, usadas para dividir. |

Todas las operaciones son O(1).

---

## 2. TDA EntradaEspacial\<T\>

**Modelo abstracto.** Un par inmutable formado por un punto `(x, y)` y un dato de tipo `T`, identificado por un `id`.

**Invariante.** `id` no es nulo ni vacío; `x` e `y` son finitos; `dato` no es nulo. No cambia después de crearse.

| Operación | Precondición | Postcondición |
|---|---|---|
| `EntradaEspacial(id, x, y, dato)` | La invariante; si no se cumple: `IllegalArgumentException`. | Crea la entrada. |
| `id()`, `x()`, `y()`, `dato()` | Ninguna. | Devuelven el valor correspondiente. |

La entrada no comprueba si el punto está dentro del plano; eso lo comprueba el quadtree al insertar.

---

## 3. TDA PRQuadtree\<T\>

**Modelo abstracto.** Una colección de entradas espaciales cuyos puntos pertenecen a una región límite fija `L`. Se permiten varias entradas en la misma ubicación (multiconjunto). El TDA permite agregar entradas, recuperar las que caen dentro de una zona rectangular y recorrerlas todas.

**Parámetros de creación.** Región límite `L`, capacidad por hoja `c > 0` y profundidad máxima `d ≥ 0`. Valores predeterminados: `c = 4`, `d = 12`.

**Invariantes.**
1. Todo punto almacenado cumple `L.contiene(x, y)`.
2. `tamanio()` es igual a la cantidad de entradas almacenadas.
3. Cada entrada aparece exactamente una vez en el árbol.

### Operaciones

#### `PRQuadtree(L, c, d)`
- **Precondición:** `L ≠ null`, `c > 0`, `d ≥ 0`. Si no se cumple: `IllegalArgumentException`.
- **Postcondición:** árbol vacío; `tamanio() = 0`.
- **Complejidad:** O(1).

#### `insertar(e)`
- **Precondición:** `e ≠ null` y `L.contiene(e.x(), e.y())`. Si no se cumple: `IllegalArgumentException` y el árbol no cambia.
- **Postcondición:** `e` forma parte de la colección; `tamanio()` aumenta en 1. Las consultas cuya zona contenga el punto de `e` lo devolverán.
- **Complejidad:** O(d) para descender. Cuando ocurre una subdivisión se suma O(c · d) en el peor caso, porque los puntos pueden caer repetidamente en el mismo cuadrante.

#### `consultar(zona) → List<EntradaEspacial<T>>`
- **Precondición:** `zona ≠ null`; si no: `IllegalArgumentException`. La zona puede estar total o parcialmente fuera de `L`.
- **Postcondición:** devuelve una lista nueva con **exactamente** las entradas cuyo punto cumple `zona.contiene(x, y)`, sin repetidos ni orden garantizado. Nunca devuelve `null`; sin coincidencias devuelve una lista vacía. No modifica el árbol ni la zona.
- **Complejidad:** O(n) en el peor caso (zona que cubre todo el plano). Cuando la zona es pequeña y los puntos están repartidos, el costo es proporcional a los nodos que se cruzan con la zona más `k`.

#### `recorrer() → List<EntradaEspacial<T>>`
- **Precondición:** ninguna.
- **Postcondición:** lista nueva con todas las entradas. Modificarla no altera el árbol.
- **Complejidad:** O(n + cantidad de nodos).

#### Operaciones de observación

| Operación | Postcondición | Complejidad |
|---|---|---|
| `tamanio() → int` | Cantidad de entradas. | O(1) |
| `estaVacio() → boolean` | `tamanio() == 0`. | O(1) |
| `cantidadNodos() → int` | Nodos internos más hojas; un árbol nuevo tiene 1. | O(nodos) |
| `altura() → int` | Profundidad de la hoja más profunda; sin subdivisiones vale 0. | O(nodos) |
| `limite()`, `capacidad()`, `profundidadMaxima()` | Parámetros de creación. | O(1) |

### Operaciones planificadas (próxima entrega)
- **Búsqueda** por coordenadas e identificador.
- **Eliminación** por coordenadas e identificador.

Las especificaremos sin romper las operaciones anteriores.

---

## 4. Representación elegida

Implementamos el TDA PRQuadtree con nodos de la clase interna `NodoQuadtree<T>`, que no es visible para la aplicación.

- **Hoja:** guarda una cubeta (`ArrayList`) con sus entradas y no tiene hijos.
- **Nodo interno:** tiene exactamente cuatro hijos (noroeste, noreste, suroeste, sureste) y no guarda entradas.
- **División:** el punto medio de la región parte cada eje en dos. Un punto sobre la línea media va a la mitad derecha (este) o inferior (sur).

**Invariantes de la representación.**
1. Las entradas se guardan solo en hojas.
2. La región de cada hijo es uno de los cuatro cuadrantes de la región del padre, y los cuatro cubren al padre sin superponerse.
3. Toda entrada de una hoja está contenida en la región de esa hoja.
4. La raíz tiene profundidad 0 y cada hijo tiene la profundidad del padre más 1.
5. Una hoja con más de `c` entradas solo existe en la profundidad máxima `d`, o cuando su región ya es tan pequeña que la precisión de `double` no permite partirla. Así se admiten puntos coincidentes sin subdividir indefinidamente.

**Inserción con subdivisión.** Descendemos hasta la hoja cuya región contiene el punto y agregamos la entrada. Si la hoja supera `c` y no está en la profundidad `d`, creamos sus cuatro hijos y redistribuimos todas sus entradas. Si un hijo vuelve a superar `c`, repetimos el proceso en él.

**Consulta con poda.** Recorremos el árbol desde la raíz. Si la región del nodo no se cruza con la zona, la descartamos sin revisar su contenido. En las hojas que se cruzan comparamos cada entrada con la zona.

---

## 5. TDA de la aplicación: LocalizadorServicios

Usa un `PRQuadtree<Servicio>` sobre el plano `[0,1024) × [0,1024)`.

| Operación | Precondición | Postcondición |
|---|---|---|
| `registrar(id, nombre, tipo, x, y)` | Datos válidos, `id` no registrado y punto dentro del plano. Si no: `IllegalArgumentException` sin cambios. | El servicio queda registrado y su `id` reservado. |
| `consultarZona(zona)` | Igual que `PRQuadtree.consultar`. | Servicios dentro de la zona. |
| `servicios()` | Ninguna. | Todos los servicios, en una lista nueva. |

La unicidad de identificadores se comprueba con un conjunto aparte, porque el quadtree admite entradas en la misma ubicación y no indexa por identificador.
