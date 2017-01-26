/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.vsb.gis.ruz76.collada;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.LineNumberReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 * @author jencek
 */
public class Teren {

    private String getHeader() {
        String header = "Format type:keyword version: 1.1.0\n";
        header += "begin_<terrain> Untitled Terrain\n";
        header += "begin_<reference> \n";
        header += "cartesian\n";
        header += "longitude 18.1563992610027\n";
        header += "latitude 49.8294600880967\n";
        header += "visible no\n";
        header += "terrain\n";
        header += "end_<reference>\n";
        header += "begin_<Material> Wet earth\n";
        header += "Material 0\n";
        header += "DielectricHalfspace\n";
        header += "begin_<Color>\n";
        header += "ambient 0.350000 0.600000 0.350000 1.000000\n";
        header += "diffuse 0.350000 0.600000 0.350000 1.000000\n";
        header += "specular 0.350000 0.600000 0.350000 1.000000\n";
        header += "emission 0.000000 0.000000 0.000000 0.000000\n";
        header += "shininess 5.000000\n";
        header += "end_<Color>\n";
        header += "begin_<DielectricLayer> Wet earth\n";
        header += "conductivity 2.000e-002\n";
        header += "permittivity 25.000000\n";
        header += "roughness 0.000e+000\n";
        header += "thickness 0.000e+000\n";
        header += "end_<DielectricLayer>\n";
        header += "end_<Material>\n";
        header += "begin_<structure_group>\n";
        header += "begin_<structure>\n";
        header += "begin_<sub_structure>\n";
        return header;
    }

    private String getFooter() {
        String footer = "end_<sub_structure>\n";
        footer += "end_<structure>\n";
        footer += "end_<structure_group>\n";
        footer += "end_<terrain>\n";
        return footer;
    }

    public long getLinesCunt(String fileName) throws Exception {
        try (LineNumberReader lnr = new LineNumberReader(new FileReader(new File(fileName)))) {
            while (lnr.skip(Long.MAX_VALUE) > 0) {
            };
            return lnr.getLineNumber();
        }
    }

    public void makeRegular(String fileName) throws Exception {
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            BufferedWriter bw = new BufferedWriter(new FileWriter(fileName.split("\\.")[0] + "_reg.txt"));
            String line;
            System.out.println("header");
            int i;
            for (i = 0; i < 6; i++) {
                line = br.readLine();
                bw.write(line + "\n");
                System.out.println(line);
            }
            System.out.println("body");
            int row = 0;
            int cellsize = 10;
            while ((line = br.readLine()) != null) {

                String items[] = line.split(" ");
                int novaluecount = 0;
                String lastval = "";
                for (int col = 0; col < items.length; col++) {
                    if (items[col].equalsIgnoreCase("-9999")) {
                        novaluecount++;
                    } else {
                        double h = Double.parseDouble(items[col].replaceAll(",", "."));
                        for (int j = 0; j < novaluecount; j++) {
                            if (col == (items.length - 1)) {
                                bw.write(items[col]);
                            } else {
                                bw.write(items[col] + " ");
                            }
                        }
                        novaluecount = 0;
                        if (col == (items.length - 1)) {
                            bw.write(items[col]);
                        } else {
                            bw.write(items[col] + " ");
                        }
                        lastval = items[col];
                    }
                }
                for (int j = 0; j < novaluecount; j++) {
                    if (j == (novaluecount - 1)) {
                        bw.write(lastval);
                    } else {
                        bw.write(lastval + " ");
                    }
                }
                bw.write("\n");
            }
            System.out.println("end of body");
            System.out.println("Terrain regulared successfully");
            bw.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void reduceTer(String fileName) {
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            BufferedWriter bw = new BufferedWriter(new FileWriter(fileName + "_reduced"));
            String line;
            System.out.println("header");
            int i;
            for (i = 0; i < 6; i++) {
                line = br.readLine();
                bw.write(line + "\n");
            }
            int row = 0;
            System.out.println("body");
            while ((line = br.readLine()) != null) {
                //System.out.println(line);
                if (row > 0) {
                    bw.write("\n");
                }
                String items[] = line.split(" ");
                System.out.println("Cols: " + items.length + "\n");
                for (int col = 0; col < items.length; col++) {
                    //bw.write(items[col] + " ");
                    if (col == (items.length - 1)) {
                        bw.write(items[col] + "");
                        //bw.write("256");
                    } else {
                        bw.write(items[col] + " ");
                        //bw.write("256 ");
                    }
                    //col += 9;
                    for (i = 0; i < 10; i++) {
                        col++;
                        if (col == (items.length - 1)) {
                            bw.write("-9999");
                        } else {
                            bw.write("-9999 ");
                        }
                    }
                }
                for (i = 0; i < 10; i++) {
                    line = br.readLine();
                    for (int col = 0; col < items.length; col++) {
                        if (col == (items.length - 1)) {
                            bw.write("-9999");
                        } else {
                            bw.write("-9999 ");
                        }
                    }
                    row++;
                }
                row++;
            }
            System.out.println("Rows: " + row + "\n");
            System.out.println("end of body");
            System.out.println("Terrain reduced successfully");
            bw.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void convertToTer(String fileName, String landuse) {
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            BufferedReader brlanduse = new BufferedReader(new FileReader(landuse));
            BufferedWriter bw = new BufferedWriter(new FileWriter(fileName.split("\\.")[0] + ".ter"));
            bw.write(getHeader());
            String line;
            String linelanduse;
            System.out.println("header");
            int i;
            for (i = 0; i < 6; i++) {
                line = br.readLine();
                linelanduse = brlanduse.readLine();
                System.out.println(line);

            }
            System.out.println("body");
            int row = 0;
            int cellsize = 10;
            ArrayList rows = new ArrayList();
            ArrayList rowslanduse = new ArrayList();
            while ((line = br.readLine()) != null) {
                //System.out.println(line);
                linelanduse = brlanduse.readLine();
                ArrayList cols = new ArrayList();
                ArrayList colslanduse = new ArrayList();
                String items[] = line.split(" ");
                String itemslanduse[] = linelanduse.split(" ");
                for (int col = 0; col < items.length; col++) {
                    //if (!items[col].equalsIgnoreCase("-9999")) {
                    double h = Double.parseDouble(items[col].replaceAll(",", "."));
                    int landusevalue = 0;
                    if (!itemslanduse[col].equalsIgnoreCase("")) landusevalue = Integer.parseInt(itemslanduse[col]);
                    cols.add(h);
                    colslanduse.add(landusevalue);
                    //}
                }
                /*if (row == 0) {
                 rows.add(row, cols);
                 } else {
                 rows.add(row - 1, cols);
                 }*/
                rows.add(cols);
                rowslanduse.add(colslanduse);
                row++;
            }

            for (row = 0; row < rows.size() - 1; row++) {
                ArrayList cols = (ArrayList) rows.get(row);
                ArrayList colslanduse = (ArrayList) rowslanduse.get(row);
                ArrayList cols2 = (ArrayList) rows.get(row + 1);
                for (int col = 0; col < cols.size() - 1; col++) {
                    if (col == 17) {
                        int a = 0;
                    }
                    double h1 = (double) cols.get(col);
                    double h2 = (double) cols.get(col + 1);
                    double h3 = (double) cols2.get(col);
                    double h4 = (double) cols2.get(col + 1);
                    if (h2 == -9999) {
                        h2 = h1;
                    }
                    if (h3 == -9999) {
                        h3 = h1;
                    }
                    if (h4 == -9999) {
                        h4 = h1;
                    }
                    if (h1 != -9999) {
                        int landusevalue = (int) colslanduse.get(col);
                        String pixel = getPixel(5 + col * cellsize, 1045 - row * cellsize, cellsize, h1, h2, h3, h4, landusevalue);
                        bw.write(pixel);
                    }
                }
            }

            bw.write(getFooter());
            System.out.println("end of body");
            System.out.println("Terrain converted successfully");
            bw.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getPixel(double x, double y, int cellsize, double h1, double h2, double h3, double h4, int landusevalue) {
        String pixel = "begin_<face>\n";
        pixel += "Material " + getMaterialId(landusevalue) + "\n";
        pixel += "nVertices 3\n";
        pixel += x + " " + y + " " + h1 + "\n";
        double pomx = x + cellsize;
        pixel += pomx + " " + y + " " + h2 + "\n";
        double pomy = y - cellsize;
        pixel += x + " " + pomy + " " + h3 + "\n";
        pixel += "end_<face>\n";
        pixel += "begin_<face>\n";
        pixel += "Material " + getMaterialId(landusevalue) + "\n";
        pixel += "nVertices 3\n";
        pixel += pomx + " " + pomy + " " + h4 + "\n";
        pixel += x + " " + pomy + " " + h3 + "\n";
        pixel += pomx + " " + y + " " + h2 + "\n";
        pixel += "end_<face>\n";
        return pixel;
    }

    private int getMaterialId(int landusevalue) {
        //TODO assign materials to landuse
        return landusevalue;
    }
}
