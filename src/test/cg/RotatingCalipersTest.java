package cg;

import org.junit.Test;
import sun.misc.InvalidJarIndexException;

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
                      |         o
            6       | |         |
            5  J o----+---c-----+---
            4       | |         d
            3       | b         |
            2       | |         |
            1       | a         |
            0       '-|---------|--
           -1         |         |
           -2  -------|-------e-+--o L
           -3         o         |
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

    @Test
    public void Caliper_getIntersectionTest() {

        /*
                                I
                      |         o
            6       | |         |
            5  J o----+---c-----+---
            4       | |         d
            3       | b         |
            2       | |         |
            1       | a         |
            0       '-|---------|--
           -1         |         |
           -2  -------|-------e-+--o L
           -3         o         |
                      K

              -2 -1 0 1 2 3 4 5 6
        */
        Point a = new Point(1, 1);
        Point b = new Point(1, 3);
        Point c = new Point(3, 5);
        Point d = new Point(6, 4);
        Point e = new Point(5, -2);

        List<Point> convexHull = Arrays.asList(e, d, c, b, a, e);

        RotatingCalipers.Caliper I = new RotatingCalipers.Caliper(convexHull, 1, 90.0);
        RotatingCalipers.Caliper J = new RotatingCalipers.Caliper(convexHull, 2, 180.0);
        RotatingCalipers.Caliper K = new RotatingCalipers.Caliper(convexHull, 3, 270.0);
        RotatingCalipers.Caliper L = new RotatingCalipers.Caliper(convexHull, 5, 0.0);

        Point2D.Double IJ = I.getIntersection(J);
        Point2D.Double JK = J.getIntersection(K);
        Point2D.Double KL = K.getIntersection(L);
        Point2D.Double LI = L.getIntersection(I);

        System.out.println(IJ);
        System.out.println(JK);
        System.out.println(KL);
        System.out.println(LI);

        // TODO
//        assertThat(IJ, is(new Point2D.Double(6.0, 5.0)));
//        assertThat(JK, is(new Point2D.Double(1.0, 5.0)));
//        assertThat(KL, is(new Point2D.Double(1.0, -2.0)));
//        assertThat(LI, is(new Point2D.Double(6.0, -2.0)));
    }
}
