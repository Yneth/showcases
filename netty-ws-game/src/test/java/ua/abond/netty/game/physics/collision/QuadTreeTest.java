package ua.abond.netty.game.physics.collision;

import org.junit.Test;
import ua.abond.netty.game.physics.collision.collider.Rect;
import ua.abond.netty.game.physics.collision.spatial.quad.QuadNode;
import ua.abond.netty.game.physics.collision.spatial.quad.QuadTree;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class QuadTreeTest {

    @Test
    public void addShouldDivideQuadTree() throws Exception {
        // GIVEN
        Rect rect = new Rect(1, 1, 10, 10);
        QuadTree<Object> quadTree = new QuadTree<>(0, 0, 100, 100, 1, 1);

        // WHEN
        quadTree.add(new QuadNode<>(null, rect));
        boolean added = quadTree.add(new QuadNode<>(null, rect));

        // THEN
        assertTrue(added);
    }

    @Test
    public void addShouldBeFalseWhenItemNotInside() throws Exception {
        // GIVEN
        Rect rect = new Rect(-10, -10, 10, 10);
        QuadTree<Object> quadTree = new QuadTree<>(0, 0, 100, 100);

        // WHEN
        boolean added = quadTree.add(new QuadNode<>(null, rect));

        // THEN
        assertFalse(added);
    }

    @Test
    public void addShouldBeTrueWhenTopLeftCorner() throws Exception {
        // GIVEN
        QuadTree<Object> quadTree = new QuadTree<>(0, 0, 100, 100);

        // WHEN
        boolean added = quadTree.add(new QuadNode<>(null, new Rect(0, 0, 0, 0)));

        // THEN
        assertTrue(added);
    }

    @Test
    public void addShouldBeTrueWhenBotRightCorner() throws Exception {
        // GIVEN
        int size = 100;
        Rect rect = new Rect(size, size, 0, 0);
        QuadTree<Object> quadTree = new QuadTree<>(0, 0, size, size);

        // WHEN
        boolean added = quadTree.add(new QuadNode<>(null, rect));

        // THEN
        assertTrue(added);
    }

    @Test
    public void addShouldBeTrueWhenTopRightCorner() throws Exception {
        // GIVEN
        int width = 100;
        QuadTree<Object> quadTree = new QuadTree<>(0, 0, width, 100);

        // WHEN
        boolean added = quadTree.add(new QuadNode<>(null, new Rect(0, 0, 0, 0)));

        // THEN
        assertTrue(added);
    }

    @Test
    public void addShouldBeTrueWhenBotLeftCorner() throws Exception {
        // GIVEN
        int height = 100;
        QuadTree<Object> quadTree = new QuadTree<>(0, 0, 100, height);

        // WHEN
        boolean added = quadTree.add(new QuadNode<>(null, new Rect(0, height, 0, 0)));

        // THEN
        assertTrue(added);
    }

    @Test
    public void removeShouldBeFalseWhenDeletingNonExisting() throws Exception {
        // GIVEN
        Rect rect = new Rect(0, 0, 10, 10);
        QuadTree<Object> quadTree = new QuadTree<>(0, 0, 10, 10);

        // WHEN
        boolean removed = quadTree.remove(new QuadNode<>(null, rect));

        // THEN
        assertFalse(removed);
    }

    @Test
    public void removeShouldRemoveExistingElement() throws Exception {
        // GIVEN
        Rect rect = new Rect(0, 0, 1, 1);
        QuadTree<Object> quadTree = new QuadTree<>(0, 0, 10, 10);
        quadTree.add(new QuadNode<>(null, rect));

        // WHEN
        boolean removed = quadTree.remove(new QuadNode<>(null, rect));

        // THEN
        assertTrue(removed);
    }

    @Test
    public void removeShouldRemoveFromChild() throws Exception {
        // GIVEN
        Rect rect = new Rect(0, 0, 1, 1);
        QuadTree<Object> quadTree = new QuadTree<>(0, 0, 10, 10, -1, 1);
        quadTree.add(new QuadNode<>(null, rect));
        quadTree.add(new QuadNode<>(null, rect));

        // WHEN
        boolean removed = quadTree.remove(new QuadNode<>(null, rect));

        // THEN
        assertTrue(removed);
    }

    @Test
    public void removeShouldNotRemoveNonExistingElementFromChild() throws Exception {
        // GIVEN
        Rect rect = new Rect(0, 0, 1, 1);
        QuadTree<Object> quadTree = new QuadTree<>(0, 0, 10, 10, -1, 1);
        quadTree.add(new QuadNode<>(null, rect));
        quadTree.add(new QuadNode<>(null, rect));

        Rect missing = new Rect(0, 0, 0, 0);

        // WHEN
        boolean removed = quadTree.remove(new QuadNode<>(null, missing));

        // THEN
        assertFalse(removed);
    }

    @Test
    public void containsShouldBeTrueWhenItemInRange() throws Exception {
        // GIVEN
        Rect rect = new Rect(3, 3, 3, 3);
        QuadTree<Object> quadTree = new QuadTree<>(0, 0, 10, 10);

        // WHEN
        boolean contains = quadTree.contains(rect);

        // THEN
        assertTrue(contains);
    }

    @Test
    public void containsShouldBeFalseWhenVectorIsBelow() throws Exception {
        // GIVEN
        Rect rect = new Rect(0, 11, 0, 0);
        QuadTree<Object> quadTree = new QuadTree<>(0, 0, 10, 10);

        // WHEN
        boolean contains = quadTree.contains(rect);

        // THEN
        assertFalse(contains);
    }

    @Test
    public void containsShouldBeFalseWhenVectorIsUnder() throws Exception {
        // GIVEN
        Rect rect = new Rect(0, -10, 10, 10);
        QuadTree<Object> quadTree = new QuadTree<>(0, 0, 10, 10);

        // WHEN
        boolean contains = quadTree.contains(rect);

        // THEN
        assertFalse(contains);
    }

    @Test
    public void updateShouldReplaceOldValueWithNewOne() throws Exception {
        // GIVEN
        Rect old = new Rect(0, 0, 1, 1);
        Rect updated = new Rect(1, 1, 1, 1);

        QuadTree<Object> quadTree = new QuadTree<>(0, 0, 10, 10);
        quadTree.add(new QuadNode<>(null, old));

        // WHEN
        boolean update = quadTree.update(new QuadNode<>(null, old), new QuadNode<>(null, updated));

        // THEN
        assertTrue(update);
    }

    @Test
    public void queryShouldGetElementsFromTopLeftQuadrant() throws Exception {
        // GIVEN
        QuadNode<Object> leftTop = new QuadNode<>(null, new Rect(4, 4, 2, 2));
        QuadNode<Object> rightBot = new QuadNode<>(null, new Rect(94, 94, 2, 2));

        QuadTree<Object> quadTree = new QuadTree<>(0, 0, 100, 100, 1, 1);
        quadTree.add(leftTop);
        quadTree.add(rightBot);

        // WHEN
        List<QuadNode<Object>> query = quadTree.query(new Rect(0, 0, 50, 50));

        // THEN
        assertThat(query, hasSize(1));
        assertThat(query, contains(leftTop));
    }

    @Test
    public void queryShouldGetAllElements() throws Exception {
        // GIVEN
        QuadNode<Object> leftTop = new QuadNode<>(null, new Rect(4, 4, 2, 2));
        QuadNode<Object> rightBot = new QuadNode<>(null, new Rect(94, 94, 2, 2));
        QuadNode<Object> rightTop = new QuadNode<>(null, new Rect(94, 4, 2, 2));
        QuadNode<Object> leftBot = new QuadNode<>(null, new Rect(4, 94, 2, 2));

        QuadTree<Object> quadTree = new QuadTree<>(0, 0, 100, 100, 1, 1);
        quadTree.add(leftTop);
        quadTree.add(rightBot);
        quadTree.add(rightTop);
        quadTree.add(leftBot);

        // WHEN
        List<QuadNode<Object>> query = quadTree.query(new Rect(0, 0, 100, 100));

        // THEN
        assertThat(query, containsInAnyOrder(rightBot, leftTop, rightTop, leftBot));
    }

    @Test
    public void addShouldNotDivide() throws Exception {
        // GIVEN
        Rect rect = new Rect(0, 0, 10, 10);
        QuadTree<Object> quadTree = new QuadTree<>(0, 0, 100, 100, 0, 1);

        // WHEN
        quadTree.add(new QuadNode<>(null, rect));
        quadTree.add(new QuadNode<>(null, rect));

        // THEN
        List<Rect> values = getField(quadTree, "values", List.class);
        QuadTree[] nodes = getField(quadTree, "nodes", QuadTree[].class);
        assertThat(values, hasSize(2));
        assertThat(nodes, nullValue());
    }

    private <T> T getField(Object obj, String fieldName, Class<T> type) {
        return Arrays.stream(obj.getClass().getDeclaredFields())
                .filter(f -> f.getName().equals(fieldName))
                .findFirst()
                .map(f -> {
                    try {
                        f.setAccessible(true);
                        return type.cast(f.get(obj));
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                })
                .orElse(null);
    }
}