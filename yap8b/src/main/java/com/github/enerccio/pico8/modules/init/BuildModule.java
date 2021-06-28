package com.github.enerccio.pico8.modules.init;

import com.github.enerccio.pico8.commons.DependencyType;
import com.github.enerccio.pico8.commons.PathUtils;
import com.github.enerccio.pico8.commons.Png2SpriteConverter;
import com.github.enerccio.pico8.commons.ProjectDescriptor;
import com.github.enerccio.pico8.commons.lua.LuaPreprocessor;
import com.github.enerccio.pico8.commons.p8file.Pico8File;
import com.github.enerccio.pico8.modules.BaseModule;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;

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
            String dependency = descriptor.getDependencies().get(DependencyType.__gfx__);
            if (dependency.endsWith(".png")) {
                File image = PathUtils.resolveFile(projectFolder, dependency);
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
                            p8.getSprites().set(i, j, color);
                        }
                }
            } else if (dependency.endsWith(".p8")) {

            } else {
                System.out.println("Unsupported __gfx__ file. Supported file types: .png, .p8");
            }
        }

        p8.saveTo(new File(build, descriptor.getName() + ".p8"));

        return true;
    }

}
