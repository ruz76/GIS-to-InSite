/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.vsb.gis.ruz76.collada;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.PrintStream;

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
            main.convertLayerPartWalls();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
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
                System.out.println(ex.getMessage());
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
                System.out.println(ex.getMessage());
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
        while ((line = br.readLine()) != null) {
            String[] items = line.split(cvsSplitBy);
            try {
                double hfloor = Double.parseDouble(items[11]);
                double hodsazeni = Double.parseDouble(items[13]);
                double h = Double.parseDouble(items[12]);
                wall.convertWall(items[0].replace("\"", ""), items[2], hfloor + hodsazeni, h);
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }

        }

    }

}

