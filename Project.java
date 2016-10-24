/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.vsb.gis.ruz76.collada;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

/**
 *
 * @author jencek
 */
public class Project {

    void saveToSetup(String filename) throws IOException {
        /*Header*/
        Path path = Paths.get("header.setup");
        Charset charset = StandardCharsets.UTF_8;
        String content = new String(Files.readAllBytes(path), charset);
        //content = content.replaceAll("#a1", filename);
        content += "\n";
        /*Body*/
        File folder = new File("pom/");
        File[] listOfFiles = folder.listFiles();
        Arrays.sort(listOfFiles);
        int j = 0;
        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) {
                String curfilename = listOfFiles[i].getName();
                String curfilename_parts[] = curfilename.split("\\.");
                System.out.println(curfilename);
                if (curfilename_parts.length > 1) {
                    if (curfilename_parts[1].equalsIgnoreCase("ter")) {
                        content += "begin_<feature>\n";
                        content += "feature " + j + "\n";
                        content += "terrain\n";
                        content += "active\n";
                        content += "filename ./" + curfilename + "\n";
                        content += "end_<feature>\n";
                        j++;
                    }
                    if (curfilename_parts[1].equalsIgnoreCase("object")) {
                        content += "begin_<feature>\n";
                        content += "feature " + j + "\n";
                        content += "object\n";
                        content += "active\n";
                        content += "filename ./" + curfilename + "\n";
                        content += "end_<feature>\n";
                        j++;
                    }
                }

            }

        }
        /*Footer*/
        path = Paths.get("footer.setup");
        content += new String(Files.readAllBytes(path), charset);
        /*Write to file*/
        Path pathsave = Paths.get("pom/" + filename + ".setup");
        Files.write(pathsave, content.getBytes(charset));
    }

}
