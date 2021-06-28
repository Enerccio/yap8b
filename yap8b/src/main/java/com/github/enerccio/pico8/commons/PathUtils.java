package com.github.enerccio.pico8.commons;

import java.io.File;

public class PathUtils {

    public static File resolveFile(File projectFolder, String resolution) {
        File rf = new File(resolution);
        if (rf.isAbsolute()) {
            return rf;
        }
        return new File(projectFolder, resolution);
    }

}
