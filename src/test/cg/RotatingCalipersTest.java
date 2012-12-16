package cg;

import org.junit.Test;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class RotatingCalipersTest {

    @Test
    public void getMinimumBoundingRectangleTest() {

        /*
                                I
                      |         ^
            6       | |         |
            5  J <----+---c-----+---
            4       | |         d
            3       | b         |
            2       | |         |
            1       | a         |
            0       '-|---------|--
           -1         |         |
           -2  -------|-------e-+--> L
           -3        \/         |
                      K

              -2 -1 0 1 2 3 4 5 6
        */
        Point a = new Point(1, 1);
        Point b = new Point(1, 3);
        Point c = new Point(3, 5);
        Point d = new Point(6, 4);
        Point e = new Point(5, -2);

        List<Point> points = Arrays.asList(c, a, d, e, b);

        List<Point2D.Double[]> rectangle = RotatingCalipers.getAllBoundingRectangles(points);

        System.out.println(rectangle);
    }
}
