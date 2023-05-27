package org.slozic.concurrency;

import java.io.File;
import java.util.Arrays;

public class DirectoryWatcher implements Runnable {

    @Override
    public void run() {
        File inputDirectory = new File("./src/main/resources/test");
        System.out.println("DirectoryWatcher started");
        while (true) {
            if (inputDirectory.listFiles().length != 0) {
                System.out.println("DirectoryWatcher found new files...");
                Arrays.stream(inputDirectory.listFiles()).forEach(file -> new Thread(new DirectoryWorker(file)).start());
            }
            System.out.println("DirectoryWatcher awaiting for new files...");
            JavaConcurrencyDemo.sleep(2000);
        }
    }
}
