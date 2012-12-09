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

public class ZipFile extends ContainerEntity {

    public ZipFile(ContainerEntity parent, String name) throws EntityNotContainableException, EntityNotContainerException, PathExistsException, EntityMustBeContainedException, ChildParentCycleException {
        super(parent, Entity.Type.ZIP_FILE, name);
    }

    @Override
    public int getSize() {
        return super.getSize() / 2;
    }

}
