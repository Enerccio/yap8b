package com.github.enerccio.pico8.modules;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;

public interface Module {

    String getCommand();

    String getDescription();

    Options createOptions();

    boolean run(Options options, CommandLine commandLine) throws Exception;

}
