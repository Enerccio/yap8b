package com.github.enerccio.pico8.commons.p8file;

import com.github.enerccio.pico8.commons.DependencyType;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class Pico8File {
    public static final String HEADER_1 = "pico-8 cartridge // http://www.pico-8.com";
    public static final String HEADER_2 = "version 8";

    public static Pico8File parse(File file) throws Exception {
        try (InputStream is = new FileInputStream(file)) {
            return load(is);
        }
    }

    private static Pico8File load(InputStream inputStream) throws Exception {
        Pico8File p8 = new Pico8File();

        String data = IOUtils.toString(inputStream, "UTF-8");
        List<String> lines = Arrays.asList(data.split(Pattern.quote(System.lineSeparator())));

        String header = lines.get(0);
        String header2 = lines.get(1);

        if (!HEADER_1.equals(header)) {
            throw new IOException("Wrong header");
        }

        return p8;
    }
    
    private final List<String> lua = new ArrayList<>();
    private final Pico8SingleByteBlob sprites = new Pico8SingleByteBlob(128, 128, 16);
    private final Pico8DoubleByteBlob flags = new Pico8DoubleByteBlob(128, 2, 256);
    private final Pico8SingleByteBlob label = new Pico8SingleByteBlob(128, 128, 16);
    private final Pico8DoubleByteBlob map = new Pico8DoubleByteBlob(128, 32, 256);
    private final Pico8DoubleByteBlob sound = new Pico8DoubleByteBlob(84, 64, 256);
    private final Pico8MusicBlob music = new Pico8MusicBlob();

    public List<String> getLua() {
        return lua;
    }

    public Pico8SingleByteBlob getSprites() {
        return sprites;
    }

    public Pico8DoubleByteBlob getFlags() {
        return flags;
    }

    public Pico8SingleByteBlob getLabel() {
        return label;
    }

    public Pico8DoubleByteBlob getMap() {
        return map;
    }

    public Pico8DoubleByteBlob getSound() {
        return sound;
    }

    public Pico8MusicBlob getMusic() {
        return music;
    }

    public void saveTo(File file) throws Exception {
        List<String> data = new ArrayList<>();

        data.add(HEADER_1);
        data.add(HEADER_2);
        data.add("__lua__");
        data.addAll(lua);
        data.add(DependencyType.__gfx__.name());
        data.addAll(sprites.serialize());
        data.add(DependencyType.__gff__.name());
        data.addAll(flags.serialize());
        data.add(DependencyType.__label__.name());
        data.addAll(label.serialize());
        data.add(DependencyType.__map__.name());
        data.addAll(map.serialize());
        data.add(DependencyType.__sfx__.name());
        data.addAll(sound.serialize());
        data.add(DependencyType.__music__.name());
        data.addAll(music.serialize());

        FileUtils.writeStringToFile(file, String.join(System.lineSeparator(), data), "UTF-8");
    }
}
