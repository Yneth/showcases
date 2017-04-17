package ua.abond.netty.game.physics.collision;

import ua.abond.netty.game.physics.Rect;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class QuadTree<T> {
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

    public boolean add(QuadNode<T> node) {
        Objects.requireNonNull(node.getRect(), "Passed rect cannot be null.");

        if (!contains(node)) {
            return false;
        }
        if (values.size() >= loadFactor && nodes == null && level < maxLevel) {
            subdivide();
        }
        return doAdd(node);
    }

    public boolean remove(QuadNode<T> node) {
        Objects.requireNonNull(node, "Passed rect cannot be null.");

        int index = getIndex(node.getRect());
        if (index < 0) {
            return doRemove(node);
        }
        return nodes[index].remove(node);
//        if (nodes == null) {
//            if (values.isEmpty()) {
//                return false;
//            }
//            return doRemove(node);
//        }
//        for (int i = 0; i < nodes.length; i++) {
//            QuadTree<T> value = nodes[i];
//            if (value.contains(node)) {
//                return value.remove(node);
//            }
//        }
//        return false;
    }

    public boolean update(QuadNode<T> oldValue, QuadNode<T> newValue) {
        return remove(oldValue) && add(newValue);
    }

    public List<QuadNode<T>> query(Rect that) {
        return doQuery(that, new ArrayList<>());
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

    public boolean contains(QuadNode<T> node) {
        Objects.requireNonNull(node, "Passed rect cannot be null.");
        return node.getRect().isInside(boundaries);
    }

    public boolean contains(Rect rect) {
        Objects.requireNonNull(rect, "Passed rect cannot be null.");
        return rect.isInside(boundaries);
    }

    private boolean doRemove(QuadNode<T> node) {
        for (int i = 0; i < values.size(); i++) {
            QuadNode<T> value = values.get(i);
            if (node.getRect().equals(value.getRect())) {
                values.remove(i);
                return true;
            }
        }
        return false;
    }

    private boolean doAdd(QuadNode<T> node) {
        int index = getIndex(node.getRect());
        if (index >= 0) {
            return nodes[index].add(node);
        }
        return values.add(node);
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
