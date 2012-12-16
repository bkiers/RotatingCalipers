package cg;

import org.junit.Test;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.*;

import static cg.RotatingCalipers.Caliper;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class CaliperTest {

    @Test
    public void getAngleNextPointTest() {

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

        java.util.List<Point> convexHull = Arrays.asList(e, d, c, b, a, e);

        Caliper I = new Caliper(convexHull, 1, 90.0);
        Caliper J = new Caliper(convexHull, 2, 180.0);
        Caliper K = new Caliper(convexHull, 4, 270.0);
        Caliper L = new Caliper(convexHull, 0, 0.0);

        int thetaI = (int)I.getAngleNextPoint(); // angle d -> c
        int thetaJ = (int)J.getAngleNextPoint(); // angle c -> b
        int thetaK = (int)K.getAngleNextPoint(); // angle a -> e
        int thetaL = (int)L.getAngleNextPoint(); // angle e -> d

        assertThat(thetaI, is(161));
        assertThat(thetaJ, is(225));
        assertThat(thetaK, is(323));
        assertThat(thetaL, is(80));
    }

    @Test
    public void getConstantTest() {

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

        java.util.List<Point> convexHull = Arrays.asList(e, d, c, b, a, e);

        final double smallValue = 0.000000000001;

        Caliper caliper = new Caliper(convexHull, 4, 45.0);
        double expected = 0.0;
        assertTrue(Math.abs(caliper.getConstant() - expected) < smallValue);

        caliper = new Caliper(convexHull, 4, 0.0);
        expected = 1.0;
        assertTrue(Math.abs(caliper.getConstant() - expected) < smallValue);
    }

    @Test
    public void getIntersectionTest() {

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

        java.util.List<Point> convexHull = Arrays.asList(e, d, c, b, a, e);

        Caliper I = new Caliper(convexHull, 1, 90.0);
        Caliper J = new Caliper(convexHull, 2, 180.0);
        Caliper K = new Caliper(convexHull, 4, 270.0);
        Caliper L = new Caliper(convexHull, 0, 0.0);

        Point2D.Double IJ = I.getIntersection(J);
        Point2D.Double JK = J.getIntersection(K);
        Point2D.Double KL = K.getIntersection(L);
        Point2D.Double LI = L.getIntersection(I);

        assertThat(IJ, is(new Point2D.Double(6.0, 5.0)));
        assertThat(JK, is(new Point2D.Double(1.0, 5.0)));
        assertThat(KL, is(new Point2D.Double(1.0, -2.0)));
        assertThat(LI, is(new Point2D.Double(6.0, -2.0)));
    }
}
