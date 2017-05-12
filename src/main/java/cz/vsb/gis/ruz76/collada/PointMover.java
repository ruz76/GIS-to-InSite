package cz.vsb.gis.ruz76.collada;

import com.vividsolutions.jts.geom.Coordinate;

/**
 * Created by jencek on 12.5.17.
 */
public class PointMover {
    private Coordinate c1;
    private Coordinate c2;
    private double angle = 0;
    public PointMover(Coordinate c1, Coordinate c2) {
        this.c1 = c1;
        this.c2 = c2;
        angle = Math.atan((c2.y - c1.y) / (c2.x - c1.x));
    }
    public void move(Coordinate c, double distance) {
        c.x = c.x + (distance * Math.cos(angle));
        c.y= c.y + (distance * Math.sin(angle));
    }
}
