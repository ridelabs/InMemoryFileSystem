/**************************************************************
 * 11/29/12 12:05:06 Thu
 * Copyright Eric Harrison (ericjharrison at gmail dot com)
 * For demonstrating an in memory filesystem
 * Apache License applies, you may play with and
 * modify, but leave this copyright in place
 *
 **************************************************************/


package com.crawlicious.filesystem.cli;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.LinkedList;


import com.crawlicious.filesystem.FileSystem;

public class Main {
    private Handlers handlers;

    private Main(final FileSystem fs) {
        handlers = new Handlers(fs);
    }

    private String runCommand(String command) {
        LinkedList<String> pieces = new LinkedList<String>(Arrays.asList(command.split("\\s+")));
        if (pieces.size() < 1) {
            return handlers.syntax("no command");
        }
        String target = pieces.remove(0);
        Handler handler = handlers.get(target);
        if (handler != null) {
            try {
                return handler.handle(pieces);
            } catch (IOException e) {
                e.printStackTrace();
                return handlers.syntax("error occured");
            }
        } else {
            return handlers.syntax("no such command");
        }
    }


    /**
     * @param args
     */
    public static void main(String[] args) {
        boolean quit = false;
        Main m = new Main(new FileSystem());
        while (!quit) {
            System.out.print("> ");
            System.out.flush();
            BufferedReader r = new BufferedReader(new InputStreamReader(System.in));
            try {
                String command = r.readLine();
                if (command == null || command.equals("quit")) quit = true;
                System.out.println(m.runCommand(command));
                System.out.flush();
            } catch (IOException e) {
                quit = true;
                e.printStackTrace();
            }
        }
    }

}
