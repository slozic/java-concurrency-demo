package org.slozic.concurrency;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class DirectoryWorker implements Runnable {
    private File outputDirectory = new File("./src/main/output/");
    private File file;

    public DirectoryWorker(final File file) {
        this.file = file;
    }

    @Override
    public void run() {
        try {
            //do work
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(outputDirectory + file.getName()));
            Files.lines(Path.of(file.getCanonicalPath())).map(s -> s + "\n").forEach(str -> {
                try {
                    bufferedWriter.write(str);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            file.delete();
        }
        System.out.println("DirectoryWorker finished processing..." + Thread.currentThread().getName());
    }
}
