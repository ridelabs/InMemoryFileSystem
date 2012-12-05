/**************************************************************
 * 11/29/12 12:05:06 Thu
 * Copyright Eric Harrison (ericjharrison at gmail dot com)
 * For demonstrating an in memory filesystem
 * Apache License applies, you may play with and
 * modify, but leave this copyright in place
 *
 **************************************************************/


package com.crawlicious.filesystem.entities;


public class AllDrives extends ContainerEntity {

    public static final String containerName = "AllDrives#";
    public AllDrives() {
        super(Type.ALL_DRIVES, containerName);
    }

    /*package*/ boolean isMaster() { return true; }

    @Override
    public int getSize() {
        return 0;
    }

    @Override
    public String getPath() {
        return "";
    }

}
