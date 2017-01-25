/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.vsb.gis.ruz76.collada;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.io.WKTReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 *
 * @author jencek
 */
public class Building {
    GeometryFactory gf = new GeometryFactory();
    
    void saveToObject(String mesh, String triangles, String filename) throws IOException {
        /*Header*/
        Path path = Paths.get("header.object");
        Charset charset = StandardCharsets.UTF_8;
        String content = new String(Files.readAllBytes(path), charset);
        content = content.replaceAll("#a1", filename);
        content += "\n";
        /*Body*/
        String triangles_items[] = triangles.split(" ");
        String mesh_items[] = mesh.split(" ");
        for (int i=0; i<triangles_items.length; i=i+6) {
            content+= "begin_<face>\n";
            content+= "Material 0\n";
            content+= "nVertices 3\n";
            int index = Integer.parseInt(triangles_items[i]) * 3;
            content+= mesh_items[index] + " ";
            content+= mesh_items[index + 1] + " ";
            content+= mesh_items[index + 2] + "\n";
            index = Integer.parseInt(triangles_items[i+2]) * 3;
            content+= mesh_items[index] + " ";
            content+= mesh_items[index + 1] + " ";
            content+= mesh_items[index + 2] + "\n";
            index = Integer.parseInt(triangles_items[i+4]) * 3;
            content+= mesh_items[index] + " ";
            content+= mesh_items[index + 1] + " ";
            content+= mesh_items[index + 2] + "\n";
            content+= "end_<face>\n";
        }
        /*Footer*/
        path = Paths.get("footer.object");
        content += new String(Files.readAllBytes(path), charset);
        /*Write to file*/
        Path pathsave = Paths.get("output/" + filename + ".object");
        Files.write(pathsave, content.getBytes(charset));
    }
    
    
    void saveToCollada(String number_of_points, String mesh, String number_of_triangles, String triangles, String filename) throws IOException {
        Path path = Paths.get("Template.dae");
        Charset charset = StandardCharsets.UTF_8;
        String content = new String(Files.readAllBytes(path), charset);
        content = content.replaceAll("#a1", number_of_points);
        content = content.replaceAll("#b2", mesh);
        content = content.replaceAll("#c3", number_of_triangles);
        content = content.replaceAll("#d4", triangles);
        Path pathsave = Paths.get("output/" + filename + ".dae");
        Files.write(pathsave, content.getBytes(charset));
    }

    void convert(String polygonWKT, String hfloor, String h, String filename) throws Exception {
        WKTReader reader = new WKTReader(gf);
        //Polygon poly = (Polygon) reader.read("POLYGON((10 10, 20 10, 20 20, 10 20, 10 10))");
        //Polygon poly = (Polygon) reader.read("POLYGON((10 10, 20 10, 20 15, 25 15, 25 20, 20 20, 20 30, 10 30, 10 10))");
        MultiPolygon mpoly = (MultiPolygon) reader.read(polygonWKT);
        Polygon poly = null;
        if (mpoly.getGeometryN(0).getGeometryType().equalsIgnoreCase("MULTIPOLYGON")) {
            MultiPolygon mpoly2 = (MultiPolygon) mpoly.getGeometryN(0);
            poly = (Polygon) mpoly2.getGeometryN(0);
        } else {
            poly = (Polygon) mpoly.getGeometryN(0);
        }
        System.out.println(filename + ": Input polygon:");
        System.out.println(poly);
        //System.out.println("Output mesh:");
        //String mesh1 = getPointsMesh(poly, 350);
        //String mesh2 = getPointsMesh(poly, 370);
        String mesh1 = getPointsMesh(poly, Double.parseDouble(hfloor));
        String mesh2 = getPointsMesh(poly, Double.parseDouble(hfloor) + Double.parseDouble(h));
        //System.out.println(" ");
        //System.out.println("Output triangles:");
        Coordinate[] coords = poly.getBoundary().getCoordinates();
        String walls = getPointsTrianglesId(coords.length - 1);
        Triangulator t = new Triangulator();
        Geometry ears = t.triangulate(poly);
        String roof = "";
        String floor = "";
        for (int i = 0; i < ears.getNumGeometries(); i++) {
            Polygon triangle = (Polygon) ears.getGeometryN(i);
            triangle.normalize();
            triangle = (Polygon) triangle.reverse();
            Coordinate[] triangle_coords = triangle.getBoundary().getCoordinates();
            for (int j = 0; j < triangle_coords.length - 1; j++) {
                for (int k = 0; k < coords.length - 1; k++) {
                    if (triangle_coords[j].equals2D(coords[k])) {
                        floor += k + " 0 ";
                        int p1 = k + coords.length - 1;
                        roof += p1 + " 0 ";
                    }
                }
            }
            //System.out.println(ears.getGeometryN(i));
        }
        System.out.print(floor);
        System.out.print(roof);
        System.out.println();
        String number_of_points = String.valueOf((coords.length - 1) * 3 * 2);
        String number_of_triangles = String.valueOf((coords.length - 1) * 2 + (ears.getNumGeometries() * 2));
        saveToCollada(number_of_points, mesh1 + mesh2, number_of_triangles, walls + floor + roof, filename);
        saveToObject(mesh1 + mesh2, walls + floor + roof, filename);
        //0 0 4 0 1 0 1 0 4 0 5 0 1 0 5 0 2 0 2 0 5 0 6 0 2 0 6 0 3 0 3 0 6 0 7 0 3 0 7 0 0 0 0 0 7 0 4 0 0 0 1 0 2 0 3 0 2 0 0 0 4 0 5 0 6 0 6 0 7 0 4 0
    }

    String getCoord(Coordinate coord) {
        return coord.x + " " + coord.y + " " + coord.z;
    }

    String getPointsMesh(Polygon inputPoly, double z) {
        if (inputPoly.getNumGeometries() > 1) {
            throw new IllegalArgumentException("Can't deal with holes yet");
        }
        Coordinate[] coords = inputPoly.getBoundary().getCoordinates();
        String mesh = "";
        for (int i = 0; i < coords.length - 1; i++) {
            coords[i].z = z;
            mesh += getCoord(coords[i]);
            mesh += " ";
        }
        return mesh;
    }

    String getPointsTrianglesId(int count) {
        String triangles = "";
        for (int i = 0; i < count; i++) {
            int p1 = i + count;
            int p2 = i + 1;
            int p3 = i + count + 1;
            if (i == count - 1) {
                p2 = 0;
                p3 = count;
            }
            triangles += i + " 0 " + p2 + " 0 " + p1 + " 0 ";
            triangles += p2 + " 0 " + p3 + " 0 " + p1 + " 0 ";
        }
        return triangles;
    }
}
