package cz.vsb.gis.ruz76.collada;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

import java.io.PrintStream;

/**
 * Created by jencek on 19.5.17.
 */
public class Wall {
    Coordinate c;
    PointMover pm;
    GeometryFactory gf = new GeometryFactory();
    WKTReader reader = new WKTReader(gf);
    PrintStream ps;

    public Wall(PrintStream ps) {
        this.ps = ps;
    }

    void printWall(double w, double h) {

        String content = "";
        content+= "begin_<face>\n";
        content+= "Material 0\n";
        content+= "nVertices 4\n";

        ps.print(content);
        ps.println(c.x + " " + c.y + " " + c.z);
        pm.move(c, w);
        ps.println(c.x + " " + c.y + " " + c.z);
        c.z = c.z + h;
        ps.println(c.x + " " + c.y + " " + c.z);
        pm.move(c, -w);
        ps.println(c.x + " " + c.y + " " + c.z);

        ps.print("end_<face>\n");

        pm.move(c, w);
        c.z = c.z - h;
    }
    void convertPartSimple(String polylineWKT, double hfloor, double h) throws ParseException {
        MultiLineString mpoly = (MultiLineString) reader.read(polylineWKT);
        Coordinate cords[] =  mpoly.getCoordinates();
        pm = new PointMover(cords[0], cords[cords.length-1]);
        c = (Coordinate) cords[cords.length-1].clone();
        c.z = hfloor;
        printWall(cords[0].distance(cords[cords.length-1]), h);
    }

    void convertWall(String polylineWKT, String pattern, double hfloor, double h) throws ParseException {
        switch (pattern) {
            case "":
                convertPartSimple(polylineWKT, hfloor, h);
                break;
            default:
                convertPartSimple(polylineWKT, hfloor, h);
                break;
        }
    }

    void printWallTest(double w, double h, String gid, String polylineWKT) {


        ps.println(c.x + " " + c.y + " " + c.z + " " + gid + " " + polylineWKT);
        pm.move(c, w);
        ps.println(c.x + " " + c.y + " " + c.z + " " + gid + " " + polylineWKT);
        c.z = c.z + h;
        ps.println(c.x + " " + c.y + " " + c.z + " " + gid + " " + polylineWKT);
        pm.move(c, -w);
        ps.println(c.x + " " + c.y + " " + c.z + " " + gid + " " + polylineWKT);

        pm.move(c, w);
        c.z = c.z - h;
    }

    void convertPartSimpleTest(String polylineWKT, double hfloor, double h, String gid) throws ParseException {
        MultiLineString mpoly = (MultiLineString) reader.read(polylineWKT);
        Coordinate cords[] =  mpoly.getCoordinates();
        if (cords[0].x > 204 && cords[0].x < 205) {
            System.out.println("Fuj");
        }
        pm = new PointMover(cords[0], cords[cords.length-1]);
        c = (Coordinate) cords[cords.length-1].clone();
        c.z = hfloor;
        printWallTest(cords[0].distance(cords[cords.length-1]), h, gid, polylineWKT);
    }

    void convertWallTest(String polylineWKT, String pattern, double hfloor, double h, String gid) throws ParseException {
        switch (pattern) {
            case "":
                convertPartSimpleTest(polylineWKT, hfloor, h, gid);
                break;
            default:
                convertPartSimpleTest(polylineWKT, hfloor, h, gid);
                break;
        }
    }
}
