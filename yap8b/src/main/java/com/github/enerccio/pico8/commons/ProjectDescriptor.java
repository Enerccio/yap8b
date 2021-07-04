package com.github.enerccio.pico8.commons;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class ProjectDescriptor {

    public static final String FILENAME = "project.p8j";

    private String name;
    private String author;
    private String version;
    private String description;

    private String main;
    private LinkedHashMap<DependencyType, String> dependencies = new LinkedHashMap<>();
    private List<File> libraryPaths = new ArrayList<>();

    public ProjectDescriptor() {
        for (DependencyType type : DependencyType.values()) {
            dependencies.put(type, null);
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMain() {
        return main;
    }

    public void setMain(String main) {
        this.main = main;
    }

    public LinkedHashMap<DependencyType, String> getDependencies() {
        return dependencies;
    }

    public void setDependencies(LinkedHashMap<DependencyType, String> dependencies) {
        this.dependencies = dependencies;
    }

    public List<File> getLibraryPaths() {
        return libraryPaths;
    }

    public void setLibraryPaths(List<File> libraryPaths) {
        this.libraryPaths = libraryPaths;
    }
}
