/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.vsb.gis.ruz76.collada;

import com.vividsolutions.jts.algorithm.Angle;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.io.WKTReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Based on code from https://sourceforge.net/p/jts-topo-suite/mailman/jts-topo-suite-user/?viewmonth=201004&page=1
 * @author jencek
 */
public class Triangulator {
    private static final double EPS = 1.0E-4;

    GeometryFactory gf = new GeometryFactory();

    public static void main(String[] args) throws Exception {
        new Triangulator().demo();
    }

    /**
     * Demonstrate the ear-clipping algorithm
     * @throws Exception
     */
    private void demo() throws Exception {
        WKTReader reader = new WKTReader(gf);
        //Polygon poly = (Polygon )reader.read("POLYGON((0 0, 5 0, 2 3, 5 5, 10 0, 10 -5, 5 -2, 0 -5, 0 0))");
        Polygon poly = (Polygon )reader.read("POLYGON((10 10, 20 10, 20 20, 10 20, 10 10))");
        System.out.println("Input polygon:");
        System.out.println(poly);

        Geometry ears = triangulate(poly);
        final int n = ears.getNumGeometries();

        System.out.println();
        System.out.println("Found " + n + " ears:");
        for (int i = 0; i < n; i++) {
            System.out.println(ears.getGeometryN(i));
        }
    }

    /**
     * Brute force approach to ear clipping
     *
     * @param inputPoly input polygon
     * @return GeometryCollection of triangular polygons
     */
    protected Geometry triangulate(Polygon inputPoly) {
        if (inputPoly.getNumGeometries() > 1) {
            throw new IllegalArgumentException("Can't deal with holes yet");
        }

        List<Polygon> ears = new ArrayList<Polygon>();
        Polygon workingPoly = (Polygon) inputPoly.clone();

        Coordinate[] coords = workingPoly.getBoundary().getCoordinates();
        int N = coords.length - 1;

        boolean finished = false;
        int k0 = 0;
        do {
            int k1 = (k0 + 1) % N;
            int k2 = (k0 + 2) % N;
            LineString ls = gf.createLineString(new Coordinate[] {coords[k0], coords[k2]});

            if (workingPoly.covers(ls)) {
                Polygon ear = gf.createPolygon(gf.createLinearRing(new Coordinate[]{coords[k0], coords[k1], coords[k2], coords[k0]}), null);
                ears.add(ear);
                if (workingPoly.difference(ear).getGeometryType().equalsIgnoreCase("MULTIPOLYGON")) {
                    workingPoly = (Polygon) workingPoly.difference(ear).getGeometryN(0);
                } else {
                    workingPoly = (Polygon) workingPoly.difference(ear);
                }
                coords = workingPoly.getBoundary().getCoordinates();
                coords = removeColinearVertices(coords);
                N = coords.length - 1;
                k0 = 0;
                if (N == 3) {  // triangle
                    ears.add(gf.createPolygon(gf.createLinearRing(coords), null));
                    finished = true;
                }
            } else {
                k0++ ;
            }
        } while (!finished);

        return gf.createGeometryCollection(ears.toArray(new Geometry[0]));
    }

    /**
     * Remove co-linear vertices. TopologyPreservingSimplifier could be
     * used for this but that seems like over-kill.
     *
     * @param coords polygon vertices
     * @return coordinates with any co-linear vertices removed
     */
    private Coordinate[] removeColinearVertices(Coordinate[] coords) {
        final int N = coords.length - 1;
        List<Coordinate> coordList = new ArrayList<Coordinate>();

        for (int j = 1; j <= N; j++) {
            int i = (j - 1) % N;
            int k = (j + 1) % N;
            if (Math.abs(Math.PI - Angle.angleBetween(coords[i], coords[j], coords[k])) > EPS) {
                coordList.add(coords[j]);
            }
        }

        coordList.add(new Coordinate(coordList.get(0)));
        return coordList.toArray(new Coordinate[0]);
    }
}
