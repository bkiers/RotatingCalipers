## RotatingCalipers

A Java implementation of the Rotating Calipers algorithm to find the minimum
bounding rectangle of a set of points.

A demo is deployed here: [computational-geometry.appspot.com/rotating-calipers](http://computational-geometry.appspot.com/rotating-calipers)

### How to use it

The implementation is pretty straight forward: everything resides in a single class
([RotatingCalipers](https://github.com/bkiers/RotatingCalipers/blob/master/src/main/cg/RotatingCalipers.java)).
Simply copy the class in your project, and invoke one of the following methods:

#### get all bounding rectangles

```java
getAllBoundingRectangles(int[] xs, int[] ys) : List<Point2D.Double[]>
getAllBoundingRectangles(List<Point> points) : List<Point2D.Double[]>
```

#### get the minimum bounding rectangle

```java
getMinimumBoundingRectangle(int[] xs, int[] ys) : Point2D.Double[]
getMinimumBoundingRectangle(List<Point> points) : Point2D.Double[]
```

#### get the area of a rectangle

```java
getArea(Point2D.Double[] rectangle) : double
```

### Example

![-][1]

```java
int[] xs = {-300, 200, 100, -400};
int[] ys = {-150, 200, -100, 0};

Point2D.Double[] minimum = RotatingCalipers.getMinimumBoundingRectangle(xs, ys);

int number = 1;

for(Point2D.Double corner : minimum) {
    System.out.printf("corner[%d] (%.1f, %.1f)%n", number++, corner.x, corner.y);
}

System.out.printf("%narea: %.1f", RotatingCalipers.getArea(minimum));
```

which will print:

```
corner[1] (280.0, -40.0)
corner[2] (200.0, 200.0)
corner[3] (-400.0, -0.0)
corner[4] (-320.0, -240.0)

area: 160000.0
```

 [1] https://raw.github.com/bkiers/RotatingCalipers/master/img/rectangle.png
