package cz.vsb.gis.ruz76.collada;

import com.vividsolutions.jts.algorithm.Angle;
import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jts.geom.util.AffineTransformation;
import com.vividsolutions.jts.io.WKTReader;

/**
 * Created by jencek on 25.1.17.
 */
public class Detail {
    GeometryFactory gf = new GeometryFactory();
    void convert(String polygonWKT) throws Exception {
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
        System.out.println("dx: " + dx);
        System.out.println("dy: " + dy);

        double angle = Math.atan( dy / dx);
        AffineTransformation af = new AffineTransformation();
        af = af.rotate(-angle, coords[position].x, coords[position].y);

        System.out.println("Uhel: " + angle);

        poly =  (Polygon) af.transform((Geometry) poly);
        System.out.println("Po transformaci polygon:");
        System.out.println(poly);

        double stepx = (coords[position+1].x - coords[position].x) / 10;
        double stepy = (coords[position+1].y - coords[position].y) / 5;
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 5; j++) {
                double cx = coords[position].x + (stepx * i);
                double cy = coords[position].y + (stepy * j);
                System.out.println("POINT(" + cx + " "  + cy + ")");
            }
        }
    }
    public static void main(String[] args) throws Exception {
        Detail d = new Detail();
        d.convert("MULTIPOLYGON (((10 10, 30 20, 25 30, 5 20, 10 10)))");
    }
}


