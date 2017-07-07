package cz.vsb.gis.ruz76.collada;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

import java.io.*;
import java.util.*;

/**
 * Created by jencek on 19.5.17.
 */
public class Wall {
    Coordinate c;
    PointMover pm;
    GeometryFactory gf = new GeometryFactory();
    WKTReader reader = new WKTReader(gf);
    PrintStream ps;
    HashMap<String,ArrayList> patterns = new HashMap();
    HashMap<String,ArrayList> walls = new HashMap();
    String curPattern = "";
    double curLineWidth = 0;
    boolean DEBUG = false;

    public Wall(PrintStream ps) throws Exception {
        this.ps = ps;
        readPatterns();
        readWalls();
    }

    private void readWalls() throws Exception {
        File f = new File("walls/list.csv");
        BufferedReader b = new BufferedReader(new FileReader(f));
        String readLine = "";

        while ((readLine = b.readLine()) != null) {
            System.out.println(readLine);
            String items[] = readLine.split("\t");
            if (items.length >= 3) {
                ArrayList currentWall = new ArrayList();
                for (int i = 1; i < items.length; i++) {
                    currentWall.add(items[i].toLowerCase());
                }
                walls.put(items[0].toLowerCase(), currentWall);
            }

        }

    }

    private void readPatterns() throws Exception {
        File folder = new File("patterns");
        File[] listOfFiles = folder.listFiles();
        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) {
                System.out.println("File " + listOfFiles[i].getName());
                int pos = listOfFiles[i].getName().lastIndexOf(".");
                String fileName = "";
                if (pos > 0) {
                    fileName = listOfFiles[i].getName().substring(0, pos);
                }
                if (!fileName.equalsIgnoreCase("")) readPattern(fileName);
            }
        }

    }

    private void readPattern(String pattern) throws Exception {
        ArrayList currentPattern = new ArrayList();
        File f = new File("patterns/" + pattern + ".csv");

        BufferedReader b = new BufferedReader(new FileReader(f));

        String readLine = "";

        while ((readLine = b.readLine()) != null) {
            System.out.println(readLine);
            String items[] = readLine.split("\t");
            if (items.length == 3) {
                Pattern p = new Pattern();
                PatternItem pi = new PatternItem();
                pi.width = Double.parseDouble(items[0]);
                pi.height = Double.parseDouble(items[1]);
                pi.material = Integer.parseInt(items[2]);
                p.add(pi);
                currentPattern.add(p);
            }
            if (items.length == 6) {
                Pattern p = new Pattern();
                PatternItem pi = new PatternItem();
                pi.width = Double.parseDouble(items[0]);
                pi.height = Double.parseDouble(items[1]);
                pi.material = Integer.parseInt(items[2]);
                PatternItem pi2 = new PatternItem();
                pi2.width = Double.parseDouble(items[3]);
                pi2.height = Double.parseDouble(items[4]);
                pi2.material = Integer.parseInt(items[5]);
                p.add(pi);
                p.add(pi2);
                currentPattern.add(p);
            }

        }
        patterns.put(pattern, currentPattern);
    }

    private void printPattern(String pattern) {
        ArrayList currentPattern = patterns.get(pattern);
        double curw = 0;
        double curh = 0;
        double origx = c.x;
        double origy = c.y;
        double origz = c.z;
        Coordinate origc = (Coordinate) c.clone();
        for (int i=0; i<currentPattern.size(); i++) {
            Pattern p = (Pattern) currentPattern.get(i);
            for (int j=0; j<p.size(); j++) {
                PatternItem pi = (PatternItem) p.get(j);
                printRectangleFace(pi.width, pi.height, pi.material);
                curh = pi.height;
            }
            c.x = origx;
            c.y = origy;
            c.z = c.z + curh;
        }

        /*Vratime na zacatek x,y i z*/
        c = origc;
        if (currentPattern.size() == 0) {
            System.out.println("Prazdny " + pattern);
        }
        Pattern p = (Pattern) currentPattern.get(0);
        for (int j=0; j<p.size(); j++) {
            PatternItem pi = (PatternItem) p.get(j);
            /*Posuneme o tolik prvku kolik mame*/
            pm.move(c, pi.width);
        }

    }

    void printRectangleFace(double w, double h, int material) {
        String content = "";
        content+= "begin_<face>\n";
        content+= "Material " + material + "\n";
        content+= "nVertices 4\n";

        if (w == 100) {
            w = curLineWidth;
        }

        ps.print(content);
        if (DEBUG) {
            ps.println(c.x + " " + c.y + " " + c.z + " " + curPattern);
            pm.move(c, w);
            ps.println(c.x + " " + c.y + " " + c.z + " " + curPattern);
            c.z = c.z + h;
            ps.println(c.x + " " + c.y + " " + c.z + " " + curPattern);
            pm.move(c, -w);
            ps.println(c.x + " " + c.y + " " + c.z + " " + curPattern);
        } else {
            ps.println(c.x + " " + c.y + " " + c.z);
            pm.move(c, w);
            ps.println(c.x + " " + c.y + " " + c.z);
            c.z = c.z + h;
            ps.println(c.x + " " + c.y + " " + c.z);
            pm.move(c, -w);
            ps.println(c.x + " " + c.y + " " + c.z);
        }

        ps.print("end_<face>\n");

        pm.move(c, w);
        c.z = c.z - h; //vrati z do puvodni pozice
    }

    void printWall(double w, double h) {
        printRectangleFace(w, h, 0);
    }

    void printWindow(double w, double h) {
        printRectangleFace(w, h, 1);
    }

    void convertPartSimple(String polylineWKT, double hfloor, double h) throws ParseException {
        MultiLineString mpoly = (MultiLineString) reader.read(polylineWKT);
        Coordinate cords[] =  mpoly.getCoordinates();
        pm = new PointMover(cords[0], cords[cords.length-1]);
        curLineWidth = cords[0].distance(cords[cords.length-1]);
        c = (Coordinate) cords[cords.length-1].clone();
        c.z = hfloor;
        printWall(cords[0].distance(cords[cords.length-1]), h);

    }

    void convertPartRandomPattern(String polylineWKT, double hfloor, double h, int percent) throws ParseException {
        Random r = new Random();

        MultiLineString mpoly = (MultiLineString) reader.read(polylineWKT);
        Coordinate cords[] =  mpoly.getCoordinates();
        curLineWidth = cords[0].distance(cords[cords.length-1]);
        int cols = (int) Math.round(curLineWidth/1.5d);
        int rows = (int) Math.round(h/1.5d);

        int count = cols * rows;
        int count_percent = (int) Math.round((percent / 100d) * count);

        ArrayList positions = new ArrayList();
        for (int cc = 0; cc < count; cc++) {
            positions.add(cc);
        }
        Collections.shuffle(positions);

        if (hfloor > 0) {
            if (DEBUG) System.out.println("hfloor > 0");
        } else {
            if (DEBUG)  System.out.println("hfloor <= 0");
        }

        double face_width = curLineWidth / cols;
        double face_height = h / rows;
        for (int i=0; i<cols; i++) {
            for (int j=0; j<rows; j++) {
                pm = new PointMover(cords[0], cords[cords.length-1]);
                c = (Coordinate) cords[cords.length-1].clone();
                pm.move(c, i * face_width);
                c.z = hfloor + (j * face_height);
                if (isInSelection(positions, i * j, count_percent)) {
                   int material = r.nextInt(3);
                   int materialid = 1;
                   switch (material) {
                       case 0:
                           materialid = 3;
                           break;
                       case 1:
                           materialid = 5;
                           break;
                       case 2:
                           materialid = 7;
                           break;
                       default:
                           materialid = 1;
                   }
                    printRectangleFace(face_width, face_height, materialid);
                } else {
                    printRectangleFace(face_width, face_height, 1);
                }
            }
        }
        //printWall(cords[0].distance(cords[cords.length-1]), h);
    }

    boolean isInSelection(ArrayList al, int value, int bound) {
        for (int i = 0; i<bound; i++) {
            int valueinarray = (int) al.get(i);
            if (valueinarray == value) return true;
        }
        return false;
    }

    void convertPartPattern(String polylineWKT, String pattern, double hfloor, double h) throws ParseException {
        if (pattern.equalsIgnoreCase("ms6")) {
            System.out.println("Fuj");
        }
        MultiLineString mpoly = (MultiLineString) reader.read(polylineWKT);
        Coordinate cords[] =  mpoly.getCoordinates();
        pm = new PointMover(cords[0], cords[cords.length-1]);
        curLineWidth = cords[0].distance(cords[cords.length-1]);
        c = (Coordinate) cords[cords.length-1].clone();
        c.z = hfloor;
        ArrayList currentWallPattern = walls.get(pattern);
        for (int i=0; i<currentWallPattern.size(); i=i+2) {
            System.out.println(currentWallPattern.get(i));
            int repeat = Integer.parseInt((String) currentWallPattern.get(i));
            for (int j = 0; j < repeat; j++) {
                System.out.println(currentWallPattern.get(i+1));
                printPattern((String) currentWallPattern.get(i+1));
            }
        }

    }

    void convertWall(String polylineWKT, String pattern, double hfloor, double h) throws ParseException {
        if (pattern.equalsIgnoreCase("")) {
            curPattern = "none";
            convertPartSimple(polylineWKT, hfloor, h);
        } else {
            curPattern = pattern;
            if (pattern.equalsIgnoreCase("test1")) {
                System.out.println("Test");
            }
            convertPartPattern(polylineWKT, pattern, hfloor, h);
        }
    }

    void convertWallRandom(String polylineWKT, String id, double hfloor, double h, int percent) throws ParseException {
        //DEBUG = true;
        curPattern = id;
        if (curPattern.equalsIgnoreCase("")) curPattern = "None";
        convertPartRandomPattern(polylineWKT, hfloor, h, percent);
    }

    /*void printWallTest(double w, double h, String gid, String polylineWKT) {


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
            case "h1a":
                for (int i = 0; i<48; i++) {
                    printPattern("h1");
                }
                break;
            case "":
                convertPartSimpleTest(polylineWKT, hfloor, h, gid);
                break;

            default:
                convertPartSimpleTest(polylineWKT, hfloor, h, gid);
                break;
        }
    }*/
}
