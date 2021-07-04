package com.github.enerccio.pico8.modules.init;

import com.github.enerccio.pico8.commons.ProjectDescriptor;
import com.github.enerccio.pico8.modules.BaseModule;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.Scanner;

public class InitModule extends BaseModule {
    private final Gson gson = new GsonBuilder().setPrettyPrinting().serializeNulls().create();

    @Override
    public String getCommand() {
        return "init";
    }

    @Override
    public String getDescription() {
        return "Creates project.json project description file.";
    }

    @Override
    protected void fillOptions(Options options) {

    }

    @Override
    protected boolean doRun(CommandLine commandLine) throws Exception {
        File projectFolder = getProjectFolder();
        if (!projectFolder.exists() || !projectFolder.isDirectory()) {
            System.out.println("Missing project folder or project folder is a file: " + projectFolder);
            return false;
        }

        ProjectDescriptor descriptor = new ProjectDescriptor();

        Scanner sc = new Scanner(System.in);

        System.out.print("Name of the project: ");
        descriptor.setName(sc.nextLine().trim());

        System.out.print("Author: ");
        descriptor.setAuthor(sc.nextLine().trim());

        System.out.print("Version: ");
        descriptor.setVersion(sc.nextLine().trim());

        System.out.print("Description: ");
        descriptor.setDescription(sc.nextLine().trim());

        File descriptorFile = new File(projectFolder, ProjectDescriptor.FILENAME);
        FileUtils.writeStringToFile(descriptorFile, gson.toJson(descriptor), "UTF-8");

        System.out.println("Project file " + descriptorFile.getAbsolutePath() + " generated!");
        return true;
    }

}
