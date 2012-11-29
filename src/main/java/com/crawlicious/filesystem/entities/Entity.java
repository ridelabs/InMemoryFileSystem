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

import com.crawlicious.filesystem.exceptions.ChildParentCycleException;
import com.crawlicious.filesystem.exceptions.EntityMustBeContainedException;
import com.crawlicious.filesystem.exceptions.EntityNotContainableException;
import com.crawlicious.filesystem.exceptions.EntityNotContainerException;
import com.crawlicious.filesystem.exceptions.IllegalFileSystemOperationException;
import com.crawlicious.filesystem.exceptions.NotATextFileException;
import com.crawlicious.filesystem.exceptions.PathExistsException;
import com.crawlicious.filesystem.exceptions.PathNotFoundException;

public abstract class Entity {
	public static enum Type {ALL_DRIVES, DRIVE, FOLDER, TEXT_FILE, ZIP_FILE, };	  	
    public static final String PATH_SEPARATOR = "/";
	   
    private Type type;
    private Entity parent;    
    private String name;
    
	public Entity(Type type, String name) {
		this.type = type;
		this.name = name;
	}

    public String getPath() {
    	// seek up the chain to get path...
    	if (parent == null) {
    		return getName() + PATH_SEPARATOR;
    	} else {
    		return parent.getPath() + getName() + ( isContainer() ? PATH_SEPARATOR : "");
    	}
    }
    
    public String getName() {
        return name;
    }
    
    public Type getType() {
    	return type;
    }
    
    // override where not containable
    public boolean mustBeContained() {
		return true;
	}
    
    // override where is a container
    public boolean isContainer() {
		return false;
	}
    
    // override in text file
	public void write(String data) throws NotATextFileException {
		throw new NotATextFileException(this.getPath());
	}

    // override in text file
	public void setText(String data) throws NotATextFileException {
		throw new NotATextFileException(this.getPath());
	}

    // override in text file
	public String getText() throws NotATextFileException {
		throw new NotATextFileException(this.getPath());
	}
	
	// override where has children
	public Collection<Entity> getChildren() throws IllegalFileSystemOperationException {
		throw new IllegalFileSystemOperationException("no children");
	}
    
    /**********************************************
     * addChild
     * This is package so that no outsider can add a child, they must use setParent
     * the problem was that if you pass the parent as a constructor arg (first approach), then the object is not 
     * built yet, so you won't be able to check things like mustBeContained() or something, but we didn't want 2 public ways
     * of adding items, so you must call set Parent, which may then call addChild, iffff it is allowed, or fail if necessary
     * @param child
     * @throws PathExistsException
     * @throws EntityNotContainableException
     * @throws EntityNotContainerException
     */
    
    /* ONLY entities may call/override !!!!*/ 
    /*package*/ void addChild(Entity child)  throws PathExistsException, EntityNotContainableException, EntityNotContainerException  {
    	throw new EntityNotContainerException(child.getName());
    }
    
    public void setParent(Entity parent) throws ChildParentCycleException, EntityMustBeContainedException, PathExistsException, EntityNotContainableException, EntityNotContainerException {
    	if (this == parent) {
    		throw new ChildParentCycleException(this.getName());
    	} else if (parent == null && this.mustBeContained()) {
    		throw new EntityMustBeContainedException(this.getName());
    	}
    	parent.addChild(this); // try this first!
    	this.parent = parent;
    }
    
    public Entity getParent() {
    	return parent;
    }
    
	public void removeChild(Entity child) throws EntityNotContainerException, PathNotFoundException {
    	throw new EntityNotContainerException(name); 
    }
    
    public Entity getChild(String name) throws EntityNotContainerException {
    	throw new EntityNotContainerException(name);
    }

    public abstract int getSize();
	
	
    
}
