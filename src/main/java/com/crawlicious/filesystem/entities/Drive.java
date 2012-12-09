/**************************************************************
 * 11/29/12 12:05:06 Thu
 * Copyright Eric Harrison (ericjharrison at gmail dot com)
 * For demonstrating an in memory filesystem
 * Apache License applies, you may play with and
 * modify, but leave this copyright in place
 *
 **************************************************************/


package com.crawlicious.filesystem.entities;

import com.crawlicious.filesystem.exceptions.ChildParentCycleException;
import com.crawlicious.filesystem.exceptions.EntityMustBeContainedException;
import com.crawlicious.filesystem.exceptions.EntityNotContainableException;
import com.crawlicious.filesystem.exceptions.EntityNotContainerException;
import com.crawlicious.filesystem.exceptions.PathExistsException;


public class Drive extends ContainerEntity {
    public Drive(AllDrives drives, String name) throws EntityNotContainableException, EntityNotContainerException, PathExistsException, EntityMustBeContainedException, ChildParentCycleException {
        super(drives, Entity.Type.DRIVE, fixName(name));
    }

    public static String fixName(String name) {
        // we add in the ':' if it is missing
        return name.endsWith(":") ? name : name + ":";
    }

    @Override
    public boolean mustBeContained() { // must NOT be in a container!
        return false;
    }
}
