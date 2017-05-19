package cz.vsb.gis.ruz76.collada;

import com.vividsolutions.jts.geom.Coordinate;

/**
 * Created by jencek on 12.5.17.
 */
public class PointMover {
    private Coordinate c1;
    private Coordinate c2;
    private double angle = 0;
    private double directionX = 1;
    private double directionY = 1;
    public PointMover(Coordinate c1, Coordinate c2) {
        this.c1 = c1;
        this.c2 = c2;
        angle = Math.atan(Math.abs(c2.y - c1.y) / Math.abs(c2.x - c1.x));
        if (c1.x >= c2.x && c1.y >= c2.y) {
            directionX = 1;
            directionY = 1;
        }
        if (c1.x >= c2.x && c1.y < c2.y) {
            directionX = 1;
            directionY = -1;
        }
        if (c1.x < c2.x && c1.y >= c2.y) {
            directionX = -1;
            directionY = 1;
        }
        if (c1.x < c2.x && c1.y < c2.y) {
            directionX = -1;
            directionY = -1;
        }

    }
    public void move(Coordinate c, double distance) {
        c.x = c.x + (distance * Math.cos(angle) * directionX);
        c.y= c.y + (distance * Math.sin(angle) * directionY);
    }
}
