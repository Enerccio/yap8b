package com.github.enerccio.pico8.modules;

import com.github.enerccio.pico8.commons.EnviromentConfig;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

public abstract class BaseModule implements Module {

    @Override
    public final Options createOptions() {
        Options options = new Options();
        fillOptions(options);

        Option help = new Option("h", "Prints help for module");
        options.addOption(help);

        return options;
    }

    protected abstract void fillOptions(Options options);

    @Override
    public final boolean run(Options options, CommandLine commandLine) throws Exception {
        if (commandLine.hasOption('h')) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("yapb8b " + getCommand(), options);
            return false;
        }
        return doRun(commandLine);
    }

    protected abstract boolean doRun(CommandLine commandLine) throws Exception;

    protected File getProjectFolder() {
        Map<String, String> env = System.getenv();
        if (env.containsKey(EnviromentConfig.PROJECT_FOLDER)) {
            return new File(env.get(EnviromentConfig.PROJECT_FOLDER));
        }
        Path currentRelativePath = Paths.get("");
        return currentRelativePath.toFile();
    }

}
