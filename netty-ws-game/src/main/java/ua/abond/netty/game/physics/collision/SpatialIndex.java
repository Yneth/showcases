package ua.abond.netty.game.physics.collision;

import ua.abond.netty.game.physics.Rect;
import ua.abond.netty.game.util.function.Callable2;

import java.util.List;

public interface SpatialIndex<T, N> {

    boolean add(T value);

    boolean remove(T value);

    boolean update(T oldValue, T newValue);

    boolean contains(T value);

    boolean contains(Rect boundaries);

    List<N> query(Rect boundaries);

    void query(Rect boundaries, List<N> out);

    void forEach(Callable2<T, T> fn);
}
