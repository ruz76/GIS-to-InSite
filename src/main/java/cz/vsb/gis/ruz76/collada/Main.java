/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.vsb.gis.ruz76.collada;

import java.io.BufferedReader;
import java.io.FileReader;

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
        try {
            //t.reduceTer("teren_budovy_v2.txt");
            //main.convertLayer();
            t.convertToTer("teren_v2_10_reg.txt", "vyuziti_5_clip_10.txt");
            //p.saveToSetup("KampusGenerovano");
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

}
