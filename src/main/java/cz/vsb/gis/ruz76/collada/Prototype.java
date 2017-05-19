package cz.vsb.gis.ruz76.collada;

import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jts.io.WKTReader;

/**
 * Created by jencek on 12.5.17.
 */
public class Prototype {

    double hfloor = 263.5;
    double h = 33;
    double toprow = 1.8;
    double middlerow = (h - toprow) / 10d;
    double middlerow_window = middlerow * (3d/5d);
    double middlerow_wall = middlerow * (2d/5d);
    double window_w = 1.5;
    double wall_w = 1.5;
    double wall2_w = 0.5;
    double wall3_w = 0.3;
    Coordinate c;
    PointMover pm;

    GeometryFactory gf = new GeometryFactory();

    void printWindow() {
        //System.out.println("Window");

        //Wall under window
        String content = "";
        content+= "begin_<face>\n";
        content+= "Material 0\n";
        content+= "nVertices 4\n";

        System.out.print(content);

        System.out.println(c.x + " " + c.y + " " + c.z);
        pm.move(c, window_w);
        System.out.println(c.x + " " + c.y + " " + c.z);
        c.z = c.z + middlerow_wall;
        System.out.println(c.x + " " + c.y + " " + c.z);
        pm.move(c, -window_w);
        System.out.println(c.x + " " + c.y + " " + c.z);

        System.out.print("end_<face>\n");

        //Window
        content = "";
        content+= "begin_<face>\n";
        content+= "Material 1\n";
        content+= "nVertices 4\n";

        System.out.print(content);
        System.out.println(c.x + " " + c.y + " " + c.z);
        pm.move(c, window_w);
        System.out.println(c.x + " " + c.y + " " + c.z);
        c.z = c.z + middlerow_window;
        System.out.println(c.x + " " + c.y + " " + c.z);
        pm.move(c, -window_w);
        System.out.println(c.x + " " + c.y + " " + c.z);

        System.out.print("end_<face>\n");

        pm.move(c, window_w);
        c.z = c.z - middlerow_wall - middlerow_window;

    }

    void printWall(double w, double h) {
        //System.out.println("Wall");

        //Wall between windows
        String content = "";
        content+= "begin_<face>\n";
        content+= "Material 0\n";
        content+= "nVertices 4\n";

        System.out.print(content);
        System.out.println(c.x + " " + c.y + " " + c.z);
        pm.move(c, w);
        System.out.println(c.x + " " + c.y + " " + c.z);
        c.z = c.z + h;
        System.out.println(c.x + " " + c.y + " " + c.z);
        pm.move(c, -w);
        System.out.println(c.x + " " + c.y + " " + c.z);

        System.out.print("end_<face>\n");

        pm.move(c, w);
        c.z = c.z - h;
    }

    void printRectangle(Polygon poly, double h) {
        String content = "";
        content+= "begin_<face>\n";
        content+= "Material 0\n";
        content+= "nVertices 4\n";

        System.out.print(content);
        for (int i = 0; i<4; i++) {
            System.out.println(poly.getCoordinates()[i].x + " " + poly.getCoordinates()[i].y + " " + h);
        }

        System.out.print("end_<face>\n");
    }

    void printStructure1() {
        for (int i=0; i <2; i++) {
            printWindow();
        }
        printWall(wall2_w, middlerow_wall + middlerow_window);
    }

    void printStructure2() {
        for (int i=0; i <2; i++) {
            printWindow();
        }
    }

    void printStructure3() {
        for (int i=0; i <4; i++) {
            printWindow();
        }
        printWall(wall2_w, middlerow_wall + middlerow_window);
    }

    void convertPart(Coordinate c1, Coordinate c2) {
        double w = c1.distance(c2);
        pm = new PointMover(c1, c2);
        for (int j = 0; j < 10; j++) {
            c = (Coordinate) c2.clone();
            c.z = hfloor + (j * (middlerow_wall + middlerow_window));
            printWall(wall3_w, middlerow_wall + middlerow_window);
            printStructure1();
            for (int i = 0; i < 15; i++) {
                printStructure3();
            }
            printStructure2();
            printStructure2();
            printWindow();
            w = c1.distance(c);
            printWall(w, middlerow_wall + middlerow_window);
        }

        w = c1.distance(c2);
        c = (Coordinate) c2.clone();
        c.z = hfloor + (10 * (middlerow_wall + middlerow_window));
        printWall(w, toprow);
    }

    void convertPartSimple(Coordinate c1, Coordinate c2) {
        pm = new PointMover(c1, c2);
        c = (Coordinate) c2.clone();
        c.z = hfloor;
        printWall(c1.distance(c2), h);
    }

    void convert() throws Exception {

        WKTReader reader = new WKTReader(gf);
        Polygon poly = (Polygon) reader.read("POLYGON ((587.761817244754639 445.681958949193358, 601.999417270824779 438.636258932063356, 552.939117196423467 339.8866587493103, 538.610117168398574 347.005558762932196,  587.761817244754639 445.681958949193358))");

        /*
        System.out.println(poly.getCoordinates()[1].y + " " + hfloor);
        double top = hfloor + h;
        System.out.println(poly.getCoordinates()[1].y + " " + top);
        System.out.println(poly.getCoordinates()[2].y + " " + hfloor);
        System.out.println(poly.getCoordinates()[2].y + " " + top);
        */

        convertPart(poly.getCoordinates()[1], poly.getCoordinates()[2]);
        convertPart(poly.getCoordinates()[0], poly.getCoordinates()[3]);
        convertPartSimple(poly.getCoordinates()[1], poly.getCoordinates()[0]);
        convertPartSimple(poly.getCoordinates()[2], poly.getCoordinates()[3]);
        printRectangle(poly, hfloor);
        printRectangle(poly, hfloor + h);
    }
}
