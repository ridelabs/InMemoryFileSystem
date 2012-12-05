/**************************************************************
 * 11/29/12 12:05:06 Thu
 * Copyright Eric Harrison (ericjharrison at gmail dot com)
 * For demonstrating an in memory filesystem
 * Apache License applies, you may play with and
 * modify, but leave this copyright in place
 *
 **************************************************************/


package com.crawlicious.filesystem.exceptions;

public class EntityMustBeContainedException extends IllegalFileSystemOperationException {
    public EntityMustBeContainedException(String name) {
        super("Entity must be contained: " + name);
    }
}
