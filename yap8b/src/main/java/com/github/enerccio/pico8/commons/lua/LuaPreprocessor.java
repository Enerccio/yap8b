package com.github.enerccio.pico8.commons.lua;

import com.github.enerccio.pico8.commons.PathUtils;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

public class LuaPreprocessor {
    public static final String INCLUDE_DIRECTIVE = "-- include ";

    private final Map<File, List<String>> loaded = new HashMap<>();
    private final Set<File> chain = new HashSet<>();

    public List<String> preprocess(File inputFile) throws IOException {
        if (loaded.containsKey(inputFile))
            return loaded.get(inputFile);

        if (!inputFile.exists()) {
            throw new IOException("Failed to find source file: " + inputFile.exists());
        }

        if (chain.contains(inputFile)) {
            throw new IOException("Recursive preprocessor call!");
        }
        chain.add(inputFile);

        try {

            try {
                String data = FileUtils.readFileToString(inputFile, "UTF-8");
                String[] lines = data.split(Pattern.quote(System.lineSeparator()));

                List<String> preprocessedLines = new ArrayList<>();
                for (String line : lines) {
                    if (!resolveDirective(inputFile.getParentFile(), line, preprocessedLines)) {
                        preprocessedLines.add(line);
                    }
                }
                loaded.put(inputFile, preprocessedLines);
                return preprocessedLines;
            } catch (IOException exception) {
                throw new IOException("Failed to preprocess file " + inputFile.getAbsolutePath(), exception);
            }
        } finally {
            chain.remove(inputFile);
        }
    }

    private boolean resolveDirective(File parentFolder, String line, List<String> preprocessedLines) throws IOException {
        if (line.startsWith(INCLUDE_DIRECTIVE)) {
            resolveInclude(parentFolder, line.substring(INCLUDE_DIRECTIVE.length()), preprocessedLines);
            return true;
        }
        return false;
    }

    private void resolveInclude(File parentFolder, String path, List<String> preprocessedLines) throws IOException {
        File includedFile = PathUtils.resolveFile(parentFolder, path);
        List<String> includedLines = preprocess(includedFile);
        preprocessedLines.addAll(includedLines);
    }

}
