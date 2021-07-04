package com.github.enerccio.pico8.commons;

import java.io.File;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.regex.Pattern;

public class PathUtils {

    private static Set<File> libraryPath = new LinkedHashSet<>();

    public static void appendLibraryPath(String path) {
        String[] paths = path.split(Pattern.quote(";"));
        for (String folder : paths)
            appendLibraryPath(new File(folder));
    }

    public static void appendLibraryPath(File path) {
        if (path.exists() && path.isDirectory())
            libraryPath.add(path);
    }

    public static File resolveFile(File projectFolder, String resolution) {
        File rf = new File(resolution);
        if (rf.isAbsolute()) {
            return rf;
        }

        HashSet<File> paths = new LinkedHashSet<>();
        paths.add(projectFolder);
        paths.addAll(libraryPath);

        for (File parent : paths) {
            File f = new File(parent, resolution);
            if (f.exists() && f.isFile())
                return f;
        }

        return null;
    }

}
