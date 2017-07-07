/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.vsb.gis.ruz76.collada;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 *
 * @author jencek
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Main main = new Main();
        cz.vsb.gis.ruz76.collada.Teren t = new Teren();
        Project p = new Project();
        Prototype prototype = new Prototype();
        try {
            //t.reduceTer("teren_budovy_v2.txt");
            //main.convertLayer();
            //t.convertToTer("teren_v2_10_reg.txt", "vyuziti_5_clip_10.txt");
            //p.saveToSetup("KampusGenerovano");
            //prototype.convert();
            main.convertLayerPartFloorRoof();
            for (int i=100; i>0; i--) {
                main.convertLayerPartWalls(i);
                main.joinParts(i);
            }
            //main.convertLayerPartWallsTest();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    void convertLayer() throws Exception {
        String csvFile = "budovy_vsb.csv";
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ";";
        Building b = new Building();

        br = new BufferedReader(new FileReader(csvFile));
        while ((line = br.readLine()) != null) {
            String[] country = line.split(cvsSplitBy);
            try {
                b.convert(country[0], country[3], country[4], country[1] + "_" + country[2]);
                //break;
            } catch (Exception ex) {
                ex.printStackTrace();
            }

        }

    }

    void convertLayerPartFloorRoof() throws Exception {
        String csvFile = "budovy_vsb_all.csv";
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ";";
        Building b = new Building();

        br = new BufferedReader(new FileReader(csvFile));
        PrintStream ps = new PrintStream(new FileOutputStream("All.object.part1"));
        while ((line = br.readLine()) != null) {
            String[] items = line.split(cvsSplitBy);
            try {
                b.convertRoofandFloor(items[0], items[3], items[4], ps);
                //break;
            } catch (Exception ex) {
                ex.printStackTrace();
            }

        }

    }

    void convertLayerPartWalls() throws Exception {
        String csvFile = "budovy_vsb_all_lines.csv";
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ";";
        PrintStream ps = new PrintStream(new FileOutputStream("All.object.part2"));
        Wall wall = new Wall(ps);

        br = new BufferedReader(new FileReader(csvFile));
        line = br.readLine(); //Skip header
        while ((line = br.readLine()) != null) {
            String[] items = line.split(cvsSplitBy);
            try {
                double hfloor = Double.parseDouble(items[11]);
                double hodsazeni = Double.parseDouble(items[13]);
                double h = Double.parseDouble(items[12]);
                //Budovy bez výšky nexportujeme
                if (h > 0) {
                    wall.convertWall(items[0].replace("\"", ""), items[2], hfloor + hodsazeni, h);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }

        }

    }

    void convertLayerPartWalls(int percent) throws Exception {
        String csvFile = "budovy_vsb_all_lines.csv";
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ";";
        PrintStream ps = new PrintStream(new FileOutputStream("All.object.part2"));
        Wall wall = new Wall(ps);

        br = new BufferedReader(new FileReader(csvFile));
        line = br.readLine(); //Skip header
        while ((line = br.readLine()) != null) {
            String[] items = line.split(cvsSplitBy);
            try {
                double hfloor = Double.parseDouble(items[11]);
                double hodsazeni = Double.parseDouble(items[13]);
                double h = Double.parseDouble(items[12]);
                //Budovy bez výšky nexportujeme
                if (h > 0) {
                    wall.convertWallRandom(items[0].replace("\"", ""), items[4], hfloor + hodsazeni, h, percent);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }

        }

    }

    void joinParts(int percent) throws Exception {
        PrintStream ps = new PrintStream(new FileOutputStream("/home/jencek/Documents/Projekty/InSite/varianty/Random/" + percent + "/All.object"));
        String filecontent = new String(Files.readAllBytes(Paths.get("All_header.object")));
        ps.print(filecontent);
        filecontent = new String(Files.readAllBytes(Paths.get("All.object.part")));
        ps.print(filecontent);
        filecontent = new String(Files.readAllBytes(Paths.get("All.object.part2")));
        ps.print(filecontent);
        filecontent = new String(Files.readAllBytes(Paths.get("All_footer.object")));
        ps.print(filecontent);
        ps.close();
    }

    /*void convertLayerPartWallsTest() throws Exception {
        String csvFile = "budovy_vsb_all_lines.csv";
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ";";
        PrintStream ps = new PrintStream(new FileOutputStream("part2.csv"));
        Wall wall = new Wall(ps);

        br = new BufferedReader(new FileReader(csvFile));
        while ((line = br.readLine()) != null) {
            String[] items = line.split(cvsSplitBy);
            try {
                double hfloor = Double.parseDouble(items[11]);
                double hodsazeni = Double.parseDouble(items[13]);
                double h = Double.parseDouble(items[12]);
                wall.convertWallTest(items[0].replace("\"", ""), items[2], hfloor + hodsazeni, h, items[1]);
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }

        }

    }*/

}

