package ua.abond.netty.game.physics;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class RectTest {

    @Test
    public void isInsideWhenInTheBotRightCorner() throws Exception {
        // GIVEN
        Rect rect = new Rect(0, 0, 100, 100);
        Rect part = new Rect(100, 100, 0, 0);

        // WHEN
        boolean inside = part.isInside(rect);

        // THEN
        assertTrue(inside);
    }

    @Test
    public void isInsideWhenInTheTopRightCorner() throws Exception {
        // GIVEN
        Rect rect = new Rect(0, 0, 100, 100);
        Rect part = new Rect(100, 0, 0, 0);

        // WHEN
        boolean inside = part.isInside(rect);

        // THEN
        assertTrue(inside);
    }

    @Test
    public void isInsideWhenInTheTopLeftCorner() throws Exception {
        // GIVEN
        Rect rect = new Rect(0, 0, 100, 100);
        Rect part = new Rect(0, 0, 0, 0);

        // WHEN
        boolean inside = part.isInside(rect);

        // THEN
        assertTrue(inside);
    }

    @Test
    public void isInsideWhenInTheBotLeftCorner() throws Exception {
        // GIVEN
        Rect rect = new Rect(0, 0, 100, 100);
        Rect part = new Rect(0, 100, 0, 0);

        // WHEN
        boolean inside = part.isInside(rect);

        // THEN
        assertTrue(inside);
    }

    @Test
    public void isInsideInTheCenter() throws Exception {
        // GIVEN
        Rect rect = new Rect(0, 0, 100, 100);
        Rect part = new Rect(45, 45, 10, 10);

        // WHEN
        boolean inside = part.isInside(rect);

        // THEN
        assertTrue(inside);
    }
}