package com.github.enerccio.pico8.commons.p8file;

import java.util.ArrayList;
import java.util.List;

public class Pico8SingleByteBlob extends ByteBlob {

    public Pico8SingleByteBlob(int width, int height, int maxv) {
        super(width, height, maxv);
    }

    @Override
    public List<String> serialize() {
        List<String> src = new ArrayList<>();
        for (int j=0; j<height; j++) {
            StringBuilder builder = new StringBuilder();
            for (int i=0; i<width; i++) {
                byte value = get(i, j);
                builder.append(String.format("%x", value));
            }
            src.add(builder.toString());
        }
        return src;
    }

    @Override
    public void deserialize(List<String> lines) {
        for (int j=0; j<height; j++) {
            if (j < lines.size()) {
                String line = lines.get(j);
                for (int i = 0; i < width; i++) {
                    char c = line.charAt(i);
                    byte value;

                    switch (c) {
                        case 'a':
                        case 'A':
                            value = 10;
                            break;
                        case 'b':
                        case 'B':
                            value = 11;
                            break;
                        case 'c':
                        case 'C':
                            value = 12;
                            break;
                        case 'd':
                        case 'D':
                            value = 13;
                            break;
                        case 'e':
                        case 'E':
                            value = 14;
                            break;
                        case 'f':
                        case 'F':
                            value = 15;
                            break;
                        default:
                            value = Byte.parseByte(Character.toString(c));
                    }

                    set(i, j, value);
                }
            }
        }
    }

}
