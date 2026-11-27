
package proyecto_ed;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class QuadNode<E extends SpatialItem> {
    private final Region boundary;
    private final int depth;
    private final List<E> entries;

    private QuadNode<E> northWest;
    private QuadNode<E> northEast;
    private QuadNode<E> southWest;
    private QuadNode<E> southEast;

    public QuadNode(Region boundary, int depth) {
        this.boundary = Objects.requireNonNull(boundary, "La región límite del nodo no puede ser nula.");
        if (depth < 0) {
            throw new IllegalArgumentException("La profundidad del nodo no puede ser negativa.");
        }
        this.depth = depth;
        this.entries = new ArrayList<>();
    }

    public Region getBoundary() {
        return boundary;
    }

    public int getDepth() {
        return depth;
    }

    public boolean isLeaf() {
        return northWest == null
                && northEast == null
                && southWest == null
                && southEast == null;
    }

    public List<E> getEntries() {
        return entries;
    }

    public List<E> getUnmodifiableEntries() {
        return Collections.unmodifiableList(entries);
    }

    public QuadNode<E> getNorthWest() {
        return northWest;
    }

    public void setNorthWest(QuadNode<E> northWest) {
        this.northWest = northWest;
    }

    public QuadNode<E> getNorthEast() {
        return northEast;
    }

    public void setNorthEast(QuadNode<E> northEast) {
        this.northEast = northEast;
    }

    public QuadNode<E> getSouthWest() {
        return southWest;
    }

    public void setSouthWest(QuadNode<E> southWest) {
        this.southWest = southWest;
    }

    public QuadNode<E> getSouthEast() {
        return southEast;
    }

    public void setSouthEast(QuadNode<E> southEast) {
        this.southEast = southEast;
    }
}