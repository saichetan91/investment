package common.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class NodeScriptExecutor {
    public static void executeScript(String scriptPath, String... args) {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder();
            String[] command = new String[2 + args.length];
            command[0] = "node";
            command[1] = scriptPath;
            System.arraycopy(args, 0, command, 2, args.length);
            processBuilder.command(command);
            processBuilder.redirectErrorStream(true);

            Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new RuntimeException("Script execution failed with exit code: " + exitCode);
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Failed to execute Node.js script", e);
        }
    }
}