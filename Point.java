/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.vsb.gis.ruz76.collada;

import com.vividsolutions.jts.geom.Coordinate;

/**
 *
 * @author jencek
 */
public class Point {
    double x;
    double y;
    double z;
    Point(double x, double y) {
        this.x = x;
        this.y = y;
        this.z = 0;
    }
    Point(Coordinate coord) {
        x = coord.x;
    }
    void print() {
        System.out.print(x + " " + y + " " + z);
    }
}
