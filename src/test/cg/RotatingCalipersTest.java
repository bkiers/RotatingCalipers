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

        int[] xs = {-300, 200, 100, -400};
        int[] ys = {-150, 200, -100, 0};

        Point2D.Double[] minimum = RotatingCalipers.getMinimumBoundingRectangle(xs, ys);

        int number = 1;

        for(Point2D.Double corner : minimum) {
            System.out.printf("corner[%d] (%.1f, %.1f)%n", number++, corner.x, corner.y);
        }

        System.out.printf("%narea: %.1f", RotatingCalipers.getArea(minimum));
    }
}
