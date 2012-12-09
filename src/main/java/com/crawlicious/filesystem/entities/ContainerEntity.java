/**************************************************************
 * 11/29/12 12:05:06 Thu
 * Copyright Eric Harrison (ericjharrison at gmail dot com)
 * For demonstrating an in memory filesystem
 * Apache License applies, you may play with and
 * modify, but leave this copyright in place
 *
 **************************************************************/


package com.crawlicious.filesystem.entities;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.crawlicious.filesystem.exceptions.ChildParentCycleException;
import com.crawlicious.filesystem.exceptions.EntityMustBeContainedException;
import com.crawlicious.filesystem.exceptions.EntityNotContainableException;
import com.crawlicious.filesystem.exceptions.EntityNotContainerException;
import com.crawlicious.filesystem.exceptions.PathExistsException;
import com.crawlicious.filesystem.exceptions.PathNotFoundException;

public abstract class ContainerEntity extends Entity {
    private Map<String, Entity> children = new HashMap<String, Entity>();

    public ContainerEntity(ContainerEntity parent, Type type, String name) throws EntityNotContainableException, EntityNotContainerException, PathExistsException, EntityMustBeContainedException, ChildParentCycleException {
        super(parent, type, name);
    }

    @Override
    public int getSize() {
//        System.out.println("I am " + getName() + " and getting size...");
        int size = 0;
        for (Entity child : children.values()) {
            size += child.getSize();
        }
        return size;
    }

    @Override
    public boolean isContainer() {
        return true;
    }

    @Override
    public void removeChild(Entity child) throws EntityNotContainerException, PathNotFoundException {
        if (! children.containsKey(child.getName())) {
            throw new PathNotFoundException(getPath() + PATH_SEPARATOR + child.getName());
        }
        children.remove(child.getName());
    }

    /*package*/ void addChild(Entity child) throws PathExistsException, EntityNotContainableException {
        if (children.containsKey(child.getName())) {
            throw new PathExistsException(getPath() + PATH_SEPARATOR + child.getName());
        } else if (! child.mustBeContained() && ! this.isMaster() ) {
            throw new EntityNotContainableException(child.getName());
        }
//        System.out.println("HI, i am " + this + " and putting into children, name=" + child.getName() + " object=" + child);
        children.put(child.getName(), child);
    }

    public Entity getChild(String name) {
        return children.get(name);
    }

    /*package*/ boolean isMaster() { return false; }

    @Override
    public Collection<Entity> getChildren() {
        return children.values();
    }

}
