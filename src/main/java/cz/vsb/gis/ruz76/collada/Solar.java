package cz.vsb.gis.ruz76.collada;

import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jts.geom.util.AffineTransformation;
import com.vividsolutions.jts.io.WKTReader;

import java.io.*;

/**
 * Created by jencek on 25.1.17.
 */
public class Solar {
    GeometryFactory gf = new GeometryFactory();

    private String convert(String polygonWKT, double base) throws Exception {
        WKTReader reader = new WKTReader(gf);
        MultiPolygon mpoly = (MultiPolygon) reader.read(polygonWKT);
        Polygon poly = null;
        if (mpoly.getGeometryN(0).getGeometryType().equalsIgnoreCase("MULTIPOLYGON")) {
            MultiPolygon mpoly2 = (MultiPolygon) mpoly.getGeometryN(0);
            poly = (Polygon) mpoly2.getGeometryN(0);
        } else {
            poly = (Polygon) mpoly.getGeometryN(0);
        }
        System.out.println("Input polygon:");
        System.out.println(poly);


        //poly = gf.createPolygon(af.transform((Geometry) poly).getCoordinates());


        Coordinate[] coords = poly.getBoundary().getCoordinates();
        double size = 0;
        int position = 0;
        for (int j = 0; j < coords.length - 1; j++) {
            //System.out.println(coords[j]);
            double cursize = coords[j].distance(coords[j+1]);
            if (size < cursize) {
                size = cursize;
                position = j;
            }
        }
        System.out.println(size);
        System.out.println(position);

        //double angle = Math.atan(coords[position+1].x - coords[position].x / coords[position+1].y - coords[position].y);
        double dy = Math.abs(coords[position+1].y - coords[position].y);
        double dx = Math.abs(coords[position+1].x - coords[position].x);

        System.out.println("Y:" + coords[position+1].y + " "  + coords[position].y);
        System.out.println("dx: " + dx);
        System.out.println("dy: " + dy);

        double angle = Math.atan(dy / dx);

        if (coords[position+1].y < coords[position].y) angle = -angle;

        AffineTransformation af = new AffineTransformation();
        af = af.rotate(angle, coords[position].x, coords[position].y);

        System.out.println("Uhel: " + angle);

        poly =  (Polygon) af.transform((Geometry) poly);
        System.out.println("Po transformaci polygon:");
        System.out.println(poly);

        double stepx = Math.abs((coords[position+1].x - coords[position].x)) / 90;
        double stepy = stepx * 2; //(coords[position+1].y - coords[position].y) / 5;
        if (coords[position+1].x < coords[position].x) stepx = -stepx;

        String ele = "";
        for (int i = 0; i < 90; i++) {
            for (int j = 0; j < 5; j++) {
                double cx = coords[position].x + (stepx * i);
                double cy = coords[position].y + 0.5 + (2.5 * stepy * j);
                double cx2 = cx + stepx - (stepx / 10);
                double cy2 = cy + stepy - (stepy / 10);
                //System.out.println("POLYGON((" + cx + " "  + cy + ", " + cx2 + " " + cy + ", " + cx2 + " " + cy2 + ", " + cx + " " + cy2 + ", " + cx + " " + cy + "))");
                polygonWKT = "POLYGON((" + cx + " "  + cy + ", " + cx2 + " " + cy + ", " + cx2 + " " + cy2 + ", " + cx + " " + cy2 + ", " + cx + " " + cy + "))";
                //ele += polygonWKT + ";" + i + ";" + j + "\n";
                Polygon partpoly = (Polygon) reader.read(polygonWKT);
                AffineTransformation af2 = new AffineTransformation();
                af2 = af2.rotate(-angle, coords[position].x, coords[position].y);
                partpoly =  (Polygon) af2.transform((Geometry) partpoly);
                //ele += partpoly + ";" + i + ";" + j + "\n";
                ele += create3D(partpoly, base, 1.0);
            }
        }

        return ele;

    }

    private String create3D(Polygon poly, double base, double HEIGHT) {
        Coordinate[] coords = poly.getBoundary().getCoordinates();
        String content = "";
        double MOVE = 0.1;
        //double HEIGHT = 1.0;
        base = base + MOVE;
        double head = base + HEIGHT;
        content+= createFace(coords, 0, 1, 2, base, base, head); //top
        content+= createFace(coords, 0, 2, 3, base, head, head); //top
        content+= createFace(coords, 0, 1, 2, base - MOVE, base - MOVE, head - MOVE); //bottom
        content+= createFace(coords, 0, 2, 3, base - MOVE, head - MOVE, head - MOVE); //bottom
        content+= createFace(coords, 0, 1, 1, base - MOVE, base - MOVE, base); //side1
        content+= createFace(coords, 0, 1, 0, base - MOVE, base, base); //side1
        content+= createFace(coords, 2, 3, 3, head - MOVE, head - MOVE, head); //side1-mirror
        content+= createFace(coords, 2, 3, 2, head - MOVE, head, head); //side1-mirror
        content+= createFace(coords, 1, 2, 2, base - MOVE, head - MOVE, head); //side2
        content+= createFace(coords, 1, 2, 1, base - MOVE, head, base); //side2
        content+= createFace(coords, 0, 3, 3, base - MOVE, head - MOVE, head); //side2-mirror
        content+= createFace(coords, 0, 3, 0, base - MOVE, head, base); //side2-mirror
        return content;
    }

    private String create3D_Inverse(Polygon poly, double base, double HEIGHT) {
        Coordinate[] coords = poly.getBoundary().getCoordinates();
        String content = "";
        double MOVE = 0.1;
        //double HEIGHT = 1.0;
        base = base + HEIGHT;
        double head = base + MOVE;
        content+= createFace(coords, 0, 1, 2, base, base, head); //top
        content+= createFace(coords, 0, 2, 3, base, head, head); //top
        content+= createFace(coords, 0, 1, 2, base - MOVE, base - MOVE, head - MOVE); //bottom
        content+= createFace(coords, 0, 2, 3, base - MOVE, head - MOVE, head - MOVE); //bottom
        content+= createFace(coords, 0, 1, 1, base - MOVE, base - MOVE, base); //side1
        content+= createFace(coords, 0, 1, 0, base - MOVE, base, base); //side1
        content+= createFace(coords, 2, 3, 3, head - MOVE, head - MOVE, head); //side1-mirror
        content+= createFace(coords, 2, 3, 2, head - MOVE, head, head); //side1-mirror
        content+= createFace(coords, 1, 2, 2, base - MOVE, head - MOVE, head); //side2
        content+= createFace(coords, 1, 2, 1, base - MOVE, head, base); //side2
        content+= createFace(coords, 0, 3, 3, base - MOVE, head - MOVE, head); //side2-mirror
        content+= createFace(coords, 0, 3, 0, base - MOVE, head, base); //side2-mirror
        return content;
    }

    private String createFace(Coordinate[] coords, int id1, int id2, int id3, double z1, double z2, double z3) {
        String content = "";
        content+= "begin_<face>\n";
        content+= "Material 1\n";
        content+= "nVertices 3\n";
        content+= coords[id1].x + " ";
        content+= coords[id1].y + " ";
        content+= z1 + "\n";
        content+= coords[id2].x + " ";
        content+= coords[id2].y + " ";
        content+= z2 + "\n";
        content+= coords[id3].x + " ";
        content+= coords[id3].y + " ";
        content+= z3 + "\n";
        content+= "end_<face>\n";
        return content;
    }

    String convertLayer() throws Exception {
        String csvFile = "solary.csv";
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ";";
        String output = "";
        br = new BufferedReader(new FileReader(csvFile));
        while ((line = br.readLine()) != null) {
            String[] country = line.split(cvsSplitBy);
            try {
                WKTReader reader = new WKTReader(gf);
                MultiPolygon mpoly = (MultiPolygon) reader.read(country[0]);
                Polygon poly = null;
                if (mpoly.getGeometryN(0).getGeometryType().equalsIgnoreCase("MULTIPOLYGON")) {
                    MultiPolygon mpoly2 = (MultiPolygon) mpoly.getGeometryN(0);
                    poly = (Polygon) mpoly2.getGeometryN(0);
                } else {
                    poly = (Polygon) mpoly.getGeometryN(0);
                }
                System.out.println("Input polygon:");
                System.out.println(poly);
                output += create3D_Inverse(poly, Double.parseDouble(country[1]), Double.parseDouble(country[2]));
                //break;
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }

        }
        return output;

    }

    public static void main(String[] args) throws Exception {
        Solar d = new Solar();
        //d.convert("MULTIPOLYGON (((10 10, 30 20, 25 30, 5 20, 10 10)))");
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("ele.csv"))) {
            /*String ele = d.convert("MULTIPOLYGON(((499.314117111382 243.82605855912,491.503217100282 227.807058535516,486.696317088092 230.150358540937,419.404816959868 262.953858618857,413.362716946751 265.899258625228,421.166216958489 281.907058648532,427.312516970385 278.911958644167,437.702916989918 273.848758637905,494.586817101226 246.129658567254,499.314117111382 243.82605855912)))", 279.8);
            bw.write(ele);
            ele = d.convert("MULTIPOLYGON(((523.341117146949 293.10005865409,515.494117135124 277.008058622014,511.038917128521 279.223658630624,498.863017104857 285.278758642264,496.348117100773 286.529458653415,443.989516997477 312.567458712496,437.649616983137 315.720258713933,445.504716997035 331.833758744644,451.947617007594 328.627558737993,490.286717084993 309.548858694034,518.961217142059 295.27965866169,523.341117146949 293.10005865409)))", 279.8);
            bw.write(ele);
            */
            //ele = d.convert("MULTIPOLYGON(((476.22181704524 377.615158830537,538.444117165986 346.674058760516,538.610117168399 347.005558762932,547.499117184663 342.64135875809,539.467817174795 326.171358717605,535.412717164494 328.20125872572,523.293317142816 334.267958731856,514.714317122591 338.562358753989,507.721717110835 342.062658756273,504.653617103759 343.598458756227,493.635017084074 349.114058769075,479.937417056994 355.970758787356,468.366717034369 361.762758803321,461.717217020574 365.091358811129,469.461217035598 380.976958841318,476.22181704524 377.615158830537)))", 279.8);
            //bw.write(ele);
            String ele = d.convertLayer();
            bw.write(ele);
            bw.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}

