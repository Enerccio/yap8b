package com.github.enerccio.pico8.commons.p8file;

import java.util.List;

public abstract class ByteBlob {

    protected final byte[] blob;

    protected final int width;
    protected final int height;
    protected final int maxv;

    public ByteBlob(int width, int height, int maxv) {
        this.width = width;
        this.height = height;
        this.maxv = maxv;
        this.blob = new byte[width * height];
    }

    public byte get(int i, int j) {
        return get(j * width + i);
    }

    public byte get(int i) {
        return blob[i];
    }

    public void set(int i, int j, byte value) {
        set(j * width + i, value);
    }

    public void set(int i, byte value) {
        if (value < 0 || value > maxv) {
            throw new IllegalArgumentException("value >= maxvalue");
        }
        blob[i] = value;
    }

    public abstract List<String> serialize();
    public abstract void deserialize(List<String> lines);

}
