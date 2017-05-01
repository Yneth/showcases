package ua.abond.netty.game.physics.collision.spatial.quad;

import lombok.AllArgsConstructor;
import lombok.Data;
import ua.abond.netty.game.physics.Rect;

@Data
@AllArgsConstructor
public class QuadNode<T> {
    T element;
    Rect rect;
}
