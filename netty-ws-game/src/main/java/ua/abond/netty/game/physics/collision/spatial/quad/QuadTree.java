package ua.abond.netty.game.physics.collision.spatial.quad;

import ua.abond.netty.game.physics.Rect;
import ua.abond.netty.game.physics.Vector2;
import ua.abond.netty.game.physics.collision.Collider;
import ua.abond.netty.game.physics.collision.SpatialIndex;
import ua.abond.netty.game.util.function.Callable2;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class QuadTree<T extends Collider> implements SpatialIndex<T, QuadNode<T>> {
    private final Rect boundaries;

    private final int level;
    private final int maxLevel;
    private final int loadFactor;

    private QuadTree<T>[] nodes;

    private List<QuadNode<T>> values;

    public QuadTree(float x, float y, float w, float h) {
        this(x, y, w, h, -1, 5);
    }

    public QuadTree(float x, float y, float w, float h, int maxLevel, int loadFactor) {
        this(new Rect(x, y, w, h), maxLevel, loadFactor);
    }

    private QuadTree(float x, float y, float w, float h, int maxLevel, int loadFactor, int level) {
        this(new Rect(x, y, w, h), maxLevel, loadFactor, level);
    }

    public QuadTree(Rect boundaries) {
        this(boundaries, -1, 5);
    }

    public QuadTree(Rect boundaries, int maxLevel, int loadFactor) {
        this(boundaries, maxLevel, loadFactor, 0);
    }

    private QuadTree(Rect boundaries, int maxLevel, int loadFactor, int level) {
        this.boundaries = boundaries;
        this.maxLevel = maxLevel;
        this.level = level;
        this.loadFactor = loadFactor;
        this.values = new ArrayList<>(5);
    }

    @Override
    public boolean add(T elem) {
        Objects.requireNonNull(elem, "Passed collider cannot be null.");

        QuadNode<T> node = node(elem);
        return doAdd(node);
    }

    private boolean doAdd(QuadNode<T> node) {
        if (!contains(node.element)) {
            return false;
        }
        if (values.size() >= loadFactor && nodes == null && level < maxLevel) {
            subdivide();
        }
        int index = getIndex(node.getRect());
        if (index >= 0) {
            return nodes[index].doAdd(node);
        }
        return values.add(node);
    }

    @Override
    public boolean remove(T value) {
        Objects.requireNonNull(value, "Passed value cannot be null.");

        QuadNode<T> node = node(value);
        return doRemove(node);
    }

    private boolean doRemove(QuadNode<T> node) {
        int index = getIndex(node.getRect());
        if (index < 0) {
            return values.remove(node);
        }
        return nodes[index].doRemove(node);
    }

    @Override
    public boolean update(T oldValue, T newValue) {
        return remove(oldValue) && add(newValue);
    }

    @Override
    public List<QuadNode<T>> query(Rect that) {
        return doQuery(that, new ArrayList<>());
    }

    @Override
    public void query(Rect rect, List<QuadNode<T>> out) {
        doQuery(rect, out);
    }

    private List<QuadNode<T>> doQuery(Rect that, List<QuadNode<T>> result) {
        int index = getIndex(that);
        if (index < 0) {
            queryAll(result);
        } else if (nodes != null) {
            nodes[index].doQuery(that, result);
        }
        return result;
    }

    private void queryAll(List<QuadNode<T>> result) {
        result.addAll(values);
        if (nodes == null) {
            return;
        }
        for (int i = 0; i < nodes.length; i++) {
            nodes[i].queryAll(result);
        }
    }

    @Override
    public void forEach(Callable2<T, T> fn) {
        forEach(this, fn);
    }

    private void forEach(QuadTree<T> parent, Callable2<T, T> fn) {
        List<QuadNode<T>> query = new ArrayList<>();
        for (int i = 0; i < values.size(); i++) {
            QuadNode<T> node = values.get(i);
            parent.query(node.rect, query);
            for (int j = 0; j < query.size(); j++) {
                fn.apply(node.element, query.get(j).element);
            }
            query.clear();
        }
        if (nodes == null) {
            return;
        }
        for (int i = 0; i < nodes.length; i++) {
            nodes[i].forEach(parent, fn);
        }
    }

    @Override
    public boolean contains(T elem) {
        Objects.requireNonNull(elem, "Passed rect cannot be null.");
        QuadNode<T> node = node(elem);
        return node.getRect().isInside(boundaries);
    }

    private QuadNode<T> node(T elem) {
        Vector2 position = elem.getPosition();
        int width = elem.width();
        int height = elem.height();
        Rect rect = new Rect(
                position.getX() - width * 0.5f, position.getY() - height * 0.5f, width, height
        );
        return new QuadNode<>(elem, rect);
    }

    @Override
    public boolean contains(Rect rect) {
        Objects.requireNonNull(rect, "Passed rect cannot be null.");
        return rect.isInside(boundaries);
    }

    private int getIndex(Rect rect) {
        if (nodes != null) {
            for (int i = 0; i < nodes.length; i++) {
                if (nodes[i].contains(rect)) {
                    return i;
                }
            }
        }
        return -1;
    }

    private void subdivide() {
        float subWidth = boundaries.getWidth() / 2f;
        float subHeight = boundaries.getHeight() / 2f;
        if ((subWidth - 1f) <= 0.01f || (subHeight - 1f) <= 0.01f)
            return;

        nodes = new QuadTree[4];
        int subLevel = level + 1;

        nodes[0] = new QuadTree<>(
                boundaries.getX(),
                boundaries.getY(),
                subWidth,
                subHeight,
                maxLevel,
                loadFactor,
                subLevel
        );
        nodes[1] = new QuadTree<>(
                boundaries.getX() + subWidth,
                boundaries.getY(),
                subWidth,
                subHeight,
                maxLevel,
                loadFactor,
                subLevel
        );
        nodes[2] = new QuadTree<>(
                boundaries.getX() + subWidth,
                boundaries.getY() + subHeight,
                subWidth,
                subHeight,
                maxLevel,
                loadFactor,
                subLevel
        );
        nodes[3] = new QuadTree<>(
                boundaries.getX(),
                boundaries.getY() + subHeight,
                subWidth,
                subHeight,
                maxLevel,
                loadFactor,
                subLevel
        );

        for (int i = 0; i < values.size(); i++) {
            QuadNode<T> rect = values.get(i);
            doAdd(rect);
            values.remove(i);
        }
    }
}
