package com.github.enerccio.pico8.modules.init;

import com.github.enerccio.pico8.commons.DependencyType;
import com.github.enerccio.pico8.commons.PathUtils;
import com.github.enerccio.pico8.commons.Png2SpriteConverter;
import com.github.enerccio.pico8.commons.ProjectDescriptor;
import com.github.enerccio.pico8.commons.lua.LuaPreprocessor;
import com.github.enerccio.pico8.commons.p8file.ByteBlob;
import com.github.enerccio.pico8.commons.p8file.Pico8File;
import com.github.enerccio.pico8.modules.BaseModule;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.util.function.Function;
import java.util.function.Supplier;

public class BuildModule extends BaseModule {
    private static final Gson gson = new GsonBuilder().create();

    @Override
    public String getCommand() {
        return "build";
    }

    @Override
    public String getDescription() {
        return "Builds the p8 file in build folder";
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

        File descriptorFile = new File(projectFolder, ProjectDescriptor.FILENAME);
        if (!descriptorFile.exists()) {
            System.out.println("Missing .p8j descriptor file.");
            return false;
        }

        ProjectDescriptor descriptor = gson.fromJson(FileUtils.readFileToString(descriptorFile, "UTF-8"), ProjectDescriptor.class);

        File build = new File(projectFolder, "build");
        FileUtils.deleteDirectory(build);

        if (!build.mkdirs()) {
            System.out.println("Failed to create build folder.");
            return false;
        }

        System.out.println("Loading lua");
        Pico8File p8 = new Pico8File();
        p8.getLua().add(String.format("-- %s %s", descriptor.getName(), descriptor.getVersion()));
        p8.getLua().add(String.format("-- by %s", descriptor.getAuthor()));

        if (StringUtils.isNotBlank(descriptor.getMain())) {
            File luaMain = PathUtils.resolveFile(projectFolder, descriptor.getMain());
            if (!luaMain.exists() || luaMain.isDirectory()) {
                System.out.println("Failed to load lua input file " + luaMain);
                return false;
            }
            p8.getLua().addAll(new LuaPreprocessor().preprocess(luaMain));
        }

        if (descriptor.getDependencies().containsKey(DependencyType.__gfx__)) {
            System.out.println("Copying sprites");
            boolean success = loadImageToSection(descriptor, p8, projectFolder,
                    DependencyType.__gfx__);
            if (!success)
                return false;
        }

        if (descriptor.getDependencies().containsKey(DependencyType.__label__)) {
            System.out.println("Copying label");
            boolean success = loadImageToSection(descriptor, p8, projectFolder,
                    DependencyType.__label__);
            if (!success)
                return false;
        }

        if (descriptor.getDependencies().containsKey(DependencyType.__gff__)) {
            System.out.println("Copying sprite flags");
            boolean success = copyFromPico8(descriptor, projectFolder, p8,
                    DependencyType.__gff__);
            if (!success)
                return false;
        }

        if (descriptor.getDependencies().containsKey(DependencyType.__map__)) {
            System.out.println("Copying map");
            boolean success = copyFromPico8(descriptor, projectFolder, p8,
                    DependencyType.__map__);
            if (!success)
                return false;
        }

        if (descriptor.getDependencies().containsKey(DependencyType.__sfx__)) {
            System.out.println("Copying sound");
            boolean success = copyFromPico8(descriptor, projectFolder, p8,
                    DependencyType.__sfx__);
            if (!success)
                return false;
        }

        if (descriptor.getDependencies().containsKey(DependencyType.__music__)) {
            System.out.println("Copying music");
            boolean success = copyFromPico8(descriptor, projectFolder, p8,
                    DependencyType.__music__);
            if (!success)
                return false;
        }

        p8.saveTo(new File(build, descriptor.getName() + ".p8"));

        return true;
    }

    private boolean loadImageToSection(ProjectDescriptor descriptor, Pico8File pico8File, File projectFolder,
                                       DependencyType type) throws Exception {
        String dependency = descriptor.getDependencies().get(type);
        if (dependency == null)
            return true;

        File image = PathUtils.resolveFile(projectFolder, dependency);

        if (dependency.endsWith(".png")) {
            try (FileInputStream fis = new FileInputStream(image)) {
                BufferedImage img = Png2SpriteConverter.loadImage(fis);
                if (img.getWidth() > 128 || img.getHeight() > 128) {
                    System.out.println("Image dimensions too large to convert: " + dependency);
                    return false;
                }
                Png2SpriteConverter converter = new Png2SpriteConverter();
                byte[] data = converter.convert(img);
                for (int i=0; i<img.getHeight(); i++)
                    for (int j=0; j<img.getWidth(); j++) {
                        byte color = data[i * img.getWidth() + j];
                        Pico8File.GETTERS.get(type).apply(pico8File).set(i, j, color);
                    }
            }
        } else if (dependency.endsWith(".p8")) {
            return copyFromPico8(descriptor, projectFolder, pico8File, type);
        } else {
            System.out.println("Unsupported " + type + " file. Supported file types: .png, .p8");
        }
        return true;
    }

    private boolean copyFromPico8(ProjectDescriptor descriptor, File projectFolder, Pico8File targetPico,
                                  DependencyType type) throws Exception {
        String path = descriptor.getDependencies().get(type);
        if (path == null)
            return true;

        File source = PathUtils.resolveFile(projectFolder, path);
        if (!source.getAbsolutePath().endsWith(".p8")) {
            System.out.println("Warning, non p8 file selected as source: " + source);
        }
        Pico8File sourcePico = Pico8File.parse(source);
        ByteBlob sourceBlob = Pico8File.GETTERS.get(type).apply(sourcePico);
        ByteBlob targetBlob = Pico8File.GETTERS.get(type).apply(targetPico);
        targetBlob.deserialize(sourceBlob.serialize());
        return true;
    }

}
