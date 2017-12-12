package test;

import core.Coord;
import movement.ObstacleRect;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ObstacleRectTest {

    ObstacleRect rect;

    @Before
    public void setUp() {
        rect = new ObstacleRect(50, 50, 10, 10, 0);
    }

	@Test
	public void intersectTest() {
        assertEquals(rect.willIntersect(new Coord(45,45)), true);
        assertEquals(rect.willIntersect(new Coord(30,30)), false);
        System.out.println(rect.willIntersect(new Coord(40,40)));
	}

	@Test
    public void intersectDistanceTest() {
        assertEquals(10, rect.intersectDistance(new Coord(30, 50), new Coord(50, 50), 20), 0.05);
        assertEquals(5, rect.intersectDistance(new Coord(37, 36), new Coord(43, 44), 20), 0.05);
    }
}
