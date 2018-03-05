/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dk.easv.dal;

import dk.easv.bll.bot.IBot;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *
 * @author jeppjleemoritzled
 */
public class FileHelper {
    private static String getFilenameNoExtension(Path path) {
        String fileName = path.getFileName().toFile().getName();
        if (fileName.indexOf(".") > 0) {
            fileName = fileName.substring(0, fileName.lastIndexOf("."));
        }
        return fileName;
    }
    
    public static ObservableList<IBot> loadBotList() throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException
    {
        ObservableList<IBot> bots = FXCollections.observableArrayList();

        Path dir = FileSystems.getDefault().getPath("./src/dk/easv/bll/bot");
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir, "*.java")) {
            for (Path path : stream) {
                String classPathAndName = "dk.easv.bll.bot." + getFilenameNoExtension(path);
                URL[] urls = {path.toFile().toURI().toURL()};
                ClassLoader cl = new URLClassLoader(urls);
                Class clazz = cl.loadClass(classPathAndName);
                if (!clazz.isInterface()) {
                    IBot bot = (IBot) clazz.newInstance();
                    bots.add(bot);
                }
            }
        }
        return bots;
    }
}
