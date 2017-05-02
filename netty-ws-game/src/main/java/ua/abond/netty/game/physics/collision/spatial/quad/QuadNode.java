package ua.abond.netty.game.physics.collision.spatial.quad;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import ua.abond.netty.game.physics.Rect;

@Getter
@Setter
@EqualsAndHashCode(of = {"element"})
@AllArgsConstructor
public class QuadNode<T> {
    final T element;
    final Rect rect;
}
