package com.github.enerccio.pico8.commons.p8file;

import java.util.ArrayList;
import java.util.List;

public class Pico8MusicBlob extends ByteBlob {

    public Pico8MusicBlob() {
        super(5, 64, 256);
    }

    @Override
    public List<String> serialize() {
        List<String> src = new ArrayList<>();
        for (int j=0; j<height; j++) {
            StringBuilder builder = new StringBuilder();
            for (int i=0; i<width; i++) {
                byte value = get(i, j);
                byte most = (byte) (value % 16);
                byte least = (byte) (value / 16);
                builder.append(String.format("%x", most));
                builder.append(String.format("%x", least));
                if (i == 0) {
                    builder.append(" ");
                }
            }
            src.add(builder.toString());
        }
        return src;
    }

    @Override
    public void deserialize(List<String> lines) {
        byte[] values = new byte[2];

        for (int j=0; j<height; j++) {
            if (j < lines.size()) {
                String line = lines.get(j);
                int ix = 0;
                for (int i = 0; i < width; i++) {
                    for (int x = 0; x < 2; x++) {
                        char c = line.charAt(ix++);

                        switch (c) {
                            case 'a':
                            case 'A':
                                values[x] = 10;
                                break;
                            case 'b':
                            case 'B':
                                values[x] = 11;
                                break;
                            case 'c':
                            case 'C':
                                values[x] = 12;
                                break;
                            case 'd':
                            case 'D':
                                values[x] = 13;
                                break;
                            case 'e':
                            case 'E':
                                values[x] = 14;
                                break;
                            case 'f':
                            case 'F':
                                values[x] = 15;
                                break;
                            default:
                                values[x] = Byte.parseByte(Character.toString(c));
                        }
                    }

                    set(i, j, (byte) (values[0] * 16 + values[1]));

                    if (i == 0) {
                        ix++;
                    }
                }
            }
        }
    }

}
