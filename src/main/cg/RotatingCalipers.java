/*
 * Copyright (c) 2010, Bart Kiers
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */
package cg;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.*;

public final class RotatingCalipers {

    protected enum Corner { UPPER_RIGHT, UPPER_LEFT, LOWER_LEFT, LOWER_RIGHT }

    public static double getArea(Point2D.Double[] rectangle) {

        double deltaXAB = rectangle[0].x - rectangle[1].x;
        double deltaYAB = rectangle[0].y - rectangle[1].y;

        double deltaXBC = rectangle[1].x - rectangle[2].x;
        double deltaYBC = rectangle[1].y - rectangle[2].y;

        double lengthAB = Math.sqrt((deltaXAB * deltaXAB) + (deltaYAB * deltaYAB));
        double lengthBC = Math.sqrt((deltaXBC * deltaXBC) + (deltaYBC * deltaYBC));

        return lengthAB * lengthBC;
    }

    public static List<Point2D.Double[]> getAllBoundingRectangles(int[] xs, int[] ys) throws IllegalArgumentException {

        if(xs.length != ys.length) {
            throw new IllegalArgumentException("xs and ys don't have the same size");
        }

        List<Point> points = new ArrayList<Point>();

        for(int i = 0; i < xs.length; i++) {
            points.add(new Point(xs[i], ys[i]));
        }

        return getAllBoundingRectangles(points);
    }

    public static List<Point2D.Double[]> getAllBoundingRectangles(List<Point> points) throws IllegalArgumentException {

        List<Point2D.Double[]> rectangles = new ArrayList<Point2D.Double[]>();

        List<Point> convexHull = GrahamScan.getConvexHull(points);

        Caliper I = new Caliper(convexHull, getIndex(convexHull, Corner.UPPER_RIGHT), 90);
        Caliper J = new Caliper(convexHull, getIndex(convexHull, Corner.UPPER_LEFT), 180);
        Caliper K = new Caliper(convexHull, getIndex(convexHull, Corner.LOWER_LEFT), 270);
        Caliper L = new Caliper(convexHull, getIndex(convexHull, Corner.LOWER_RIGHT), 0);

        while(L.currentAngle < 90.0) {

            rectangles.add(new Point2D.Double[]{
                    L.getIntersection(I),
                    I.getIntersection(J),
                    J.getIntersection(K),
                    K.getIntersection(L)
            });

            double smallestTheta = getSmallestTheta(I, J, K, L);

            I.rotateBy(smallestTheta);
            J.rotateBy(smallestTheta);
            K.rotateBy(smallestTheta);
            L.rotateBy(smallestTheta);
        }

        return rectangles;
    }

    public static Point2D.Double[] getMinimumBoundingRectangle(int[] xs, int[] ys) throws IllegalArgumentException {

        if(xs.length != ys.length) {
            throw new IllegalArgumentException("xs and ys don't have the same size");
        }

        List<Point> points = new ArrayList<Point>();

        for(int i = 0; i < xs.length; i++) {
            points.add(new Point(xs[i], ys[i]));
        }

        return getMinimumBoundingRectangle(points);
    }

    public static Point2D.Double[] getMinimumBoundingRectangle(List<Point> points) throws IllegalArgumentException {

        List<Point2D.Double[]> rectangles = getAllBoundingRectangles(points);

        Point2D.Double[] minimum = null;
        double area = Long.MAX_VALUE;

        for (Point2D.Double[] rectangle : rectangles) {

            double tempArea = getArea(rectangle);

            if (minimum == null || tempArea < area) {
                minimum = rectangle;
                area = tempArea;
            }
        }

        return minimum;
    }

    private static double getSmallestTheta(Caliper I, Caliper J, Caliper K, Caliper L) {

        double thetaI = I.getDeltaAngleNextPoint();
        double thetaJ = J.getDeltaAngleNextPoint();
        double thetaK = K.getDeltaAngleNextPoint();
        double thetaL = L.getDeltaAngleNextPoint();

        if(thetaI <= thetaJ && thetaI <= thetaK && thetaI <= thetaL) {
            return thetaI;
        }
        else if(thetaJ <= thetaK && thetaJ <= thetaL) {
            return thetaJ;
        }
        else if(thetaK <= thetaL) {
            return thetaK;
        }
        else {
            return thetaL;
        }
    }

    protected static int getIndex(List<Point> convexHull, Corner corner) {

        int index = 0;
        Point point = convexHull.get(index);

        for(int i = 1; i < convexHull.size() - 1; i++) {

            Point temp = convexHull.get(i);
            boolean change = false;

            switch(corner) {
                case UPPER_RIGHT:
                    change = (temp.x > point.x || (temp.x == point.x && temp.y > point.y));
                    break;
                case UPPER_LEFT:
                    change = (temp.y > point.y || (temp.y == point.y && temp.x < point.x));
                    break;
                case LOWER_LEFT:
                    change = (temp.x < point.x || (temp.x == point.x && temp.y < point.y));
                    break;
                case LOWER_RIGHT:
                    change = (temp.y < point.y || (temp.y == point.y && temp.x > point.x));
                    break;
            }

            if(change) {
                index = i;
                point = temp;
            }
        }

        return index;
    }

    protected static class Caliper {

        final static double SIGMA = 0.00000000001;

        final List<Point> convexHull;
        int pointIndex;
        double currentAngle;

        Caliper(List<Point> convexHull, int pointIndex, double currentAngle) {
            this.convexHull = convexHull;
            this.pointIndex = pointIndex;
            this.currentAngle = currentAngle;
        }

        double getAngleNextPoint() {

            Point p1 = convexHull.get(pointIndex);
            Point p2 = convexHull.get((pointIndex + 1) % convexHull.size());

            double deltaX = p2.x - p1.x;
            double deltaY = p2.y - p1.y;

            double angle = Math.atan2(deltaY, deltaX) * 180 / Math.PI;

            return angle < 0 ? 360 + angle : angle;
        }

        double getConstant() {

            Point p = convexHull.get(pointIndex);

            return p.y - (getSlope() * p.x);
        }

        double getDeltaAngleNextPoint() {

            double angle = getAngleNextPoint();

            angle = angle < 0 ? 360 + angle - currentAngle : angle - currentAngle;

            return angle < 0 ? 360 : angle;
        }

        Point2D.Double getIntersection(Caliper that) {

            // the x-intercept of 'this' and 'that': x = ((c2 - c1) / (m1 - m2))
            double x;
            // the y-intercept of 'this' and 'that', given 'x': (m*x) + c
            double y;

            if(this.isVertical()) {
                x = convexHull.get(pointIndex).x;
            }
            else if(this.isHorizontal()) {
                x = that.convexHull.get(that.pointIndex).x;
            }
            else {
                x = (that.getConstant() -  this.getConstant()) / (this.getSlope() - that.getSlope());
            }

            if(this.isVertical()) {
                y = that.getConstant();
            }
            else if(this.isHorizontal()) {
                y = this.getConstant();
            }
            else {
                y = (this.getSlope() * x) + this.getConstant();
            }

            return new Point2D.Double(x, y);
        }

        double getSlope() {
            return Math.tan(Math.toRadians(currentAngle));
        }

        boolean isHorizontal() {
            return (Math.abs(currentAngle) < SIGMA) || (Math.abs(currentAngle - 180.0) < SIGMA);
        }

        boolean isVertical() {
            return (Math.abs(currentAngle - 90.0) < SIGMA) || (Math.abs(currentAngle - 270.0) < SIGMA);
        }

        void rotateBy(double angle) {

            if(this.getDeltaAngleNextPoint() == angle) {
                pointIndex++;
            }

            this.currentAngle += angle;
        }
    }

    /**
     * For a documented (and unit tested version) of the class below, see:
     * <a href="https://github.com/bkiers/GrahamScan">github.com/bkiers/GrahamScan</a>
     */
    private static class GrahamScan {

        protected static enum Turn { CLOCKWISE, COUNTER_CLOCKWISE, COLLINEAR }

        protected static boolean areAllCollinear(List<Point> points) {

            if(points.size() < 2) {
                return true;
            }

            final Point a = points.get(0);
            final Point b = points.get(1);

            for(int i = 2; i < points.size(); i++) {

                Point c = points.get(i);

                if(getTurn(a, b, c) != Turn.COLLINEAR) {
                    return false;
                }
            }

            return true;
        }

        public static List<Point> getConvexHull(List<Point> points) throws IllegalArgumentException {

            List<Point> sorted = new ArrayList<Point>(getSortedPointSet(points));

            if(sorted.size() < 3) {
                throw new IllegalArgumentException("can only create a convex hull of 3 or more unique points");
            }

            if(areAllCollinear(sorted)) {
                throw new IllegalArgumentException("cannot create a convex hull from collinear points");
            }

            Stack<Point> stack = new Stack<Point>();
            stack.push(sorted.get(0));
            stack.push(sorted.get(1));

            for (int i = 2; i < sorted.size(); i++) {

                Point head = sorted.get(i);
                Point middle = stack.pop();
                Point tail = stack.peek();

                Turn turn = getTurn(tail, middle, head);

                switch(turn) {
                    case COUNTER_CLOCKWISE:
                        stack.push(middle);
                        stack.push(head);
                        break;
                    case CLOCKWISE:
                        i--;
                        break;
                    case COLLINEAR:
                        stack.push(head);
                        break;
                }
            }

            stack.push(sorted.get(0));

            return new ArrayList<Point>(stack);
        }

        protected static Point getLowestPoint(List<Point> points) {

            Point lowest = points.get(0);

            for(int i = 1; i < points.size(); i++) {

                Point temp = points.get(i);

                if(temp.y < lowest.y || (temp.y == lowest.y && temp.x < lowest.x)) {
                    lowest = temp;
                }
            }

            return lowest;
        }

        protected static Set<Point> getSortedPointSet(List<Point> points) {

            final Point lowest = getLowestPoint(points);

            TreeSet<Point> set = new TreeSet<Point>(new Comparator<Point>() {
                @Override
                public int compare(Point a, Point b) {

                    if(a == b || a.equals(b)) {
                        return 0;
                    }

                    double thetaA = Math.atan2((long)a.y - lowest.y, (long)a.x - lowest.x);
                    double thetaB = Math.atan2((long)b.y - lowest.y, (long)b.x - lowest.x);

                    if(thetaA < thetaB) {
                        return -1;
                    }
                    else if(thetaA > thetaB) {
                        return 1;
                    }
                    else {
                        double distanceA = Math.sqrt((((long)lowest.x - a.x) * ((long)lowest.x - a.x)) +
                                (((long)lowest.y - a.y) * ((long)lowest.y - a.y)));
                        double distanceB = Math.sqrt((((long)lowest.x - b.x) * ((long)lowest.x - b.x)) +
                                (((long)lowest.y - b.y) * ((long)lowest.y - b.y)));

                        if(distanceA < distanceB) {
                            return -1;
                        }
                        else {
                            return 1;
                        }
                    }
                }
            });

            set.addAll(points);

            return set;
        }

        protected static Turn getTurn(Point a, Point b, Point c) {

            double crossProduct = (((long)b.x - a.x) * ((long)c.y - a.y)) -
                    (((long)b.y - a.y) * ((long)c.x - a.x));

            if(crossProduct > 0) {
                return Turn.COUNTER_CLOCKWISE;
            }
            else if(crossProduct < 0) {
                return Turn.CLOCKWISE;
            }
            else {
                return Turn.COLLINEAR;
            }
        }
    }
}