package com.github.enerccio.pico8.builder;

import com.github.enerccio.pico8.modules.Module;
import com.github.enerccio.pico8.modules.init.BuildModule;
import com.github.enerccio.pico8.modules.init.InitModule;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;

public class Main {

    public static final Module[] MODULES = new Module[] {
            new InitModule(),
            new BuildModule()
    };

    public static void main(String[] args) throws Exception {
        if (args.length > 0) {
            String moduleName = args[0];
            String[] rest = new String[args.length - 1];
            System.arraycopy(args, 1, rest, 0, rest.length);

            for (Module module : MODULES) {
                if (moduleName.equals(module.getCommand())) {
                    CommandLineParser parser = new DefaultParser();
                    Options options = module.createOptions();
                    CommandLine commandLine = parser.parse(options, rest);
                    boolean success = module.run(options, commandLine);

                    if (success) {
                        System.exit(0);
                    } else {
                        System.exit(1);
                    }
                }
            }
        }

        System.out.println("yap8b [command] <command args...>");
        System.out.println();
        System.out.println("List of commands: ");
        for (Module module : MODULES) {
            System.out.println(module.getCommand() + "\t\t" + module.getDescription());
        }
    }

}
