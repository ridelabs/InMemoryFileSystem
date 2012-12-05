package com.crawlicious.filesystem.cli;

import java.io.IOException;
import java.util.LinkedList;

public interface Handler {
    public String handle(LinkedList<String> args) throws IOException;
}