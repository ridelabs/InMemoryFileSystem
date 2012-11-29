/**************************************************************
 * 11/29/12 12:05:06 Thu
 * Copyright Eric Harrison (ericjharrison at gmail dot com)
 * For demonstrating an in memory filesystem
 * Apache License applies, you may play with and 
 * modify, but leave this copyright in place
 *
 **************************************************************/


package com.crawlicious.filesystem;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;


import com.crawlicious.filesystem.entities.AllDrives;
import com.crawlicious.filesystem.entities.Drive;
import com.crawlicious.filesystem.entities.Entity;
import com.crawlicious.filesystem.entities.Folder;
import com.crawlicious.filesystem.entities.TextFile;
import com.crawlicious.filesystem.entities.ZipFile;
import com.crawlicious.filesystem.entities.Entity.Type;
import com.crawlicious.filesystem.exceptions.ChildParentCycleException;
import com.crawlicious.filesystem.exceptions.EntityNotContainerException;
import com.crawlicious.filesystem.exceptions.IllegalFileSystemOperationException;
import com.crawlicious.filesystem.exceptions.NotATextFileException;
import com.crawlicious.filesystem.exceptions.PathExistsException;
import com.crawlicious.filesystem.exceptions.PathNotFoundException;

public class FileSystem {
	private AllDrives allDrives = new AllDrives();
	
	public void printTree()  throws IllegalFileSystemOperationException {
		System.out.println("----------- Printing the whole file system tree -------");
		printTree(allDrives);
		System.out.println("** Note that sizes of children will be compressed by their parent zip file");
	}
	
	public void printTree(Entity entity) throws IllegalFileSystemOperationException {
		System.out.println(entity.getPath() +  " [length = " + entity.getSize() + "]");
		if (entity.isContainer()) {
			for (Entity c : entity.getChildren()) {
				printTree(c);
			}
		}		
	}
	
	private Entity findEntity(String path) throws PathNotFoundException, EntityNotContainerException {
		if (path == null) {							
			throw new PathNotFoundException(path);
		}
		LinkedList<String> pieces = new LinkedList<String>(Arrays.asList(path.split(Entity.PATH_SEPARATOR)));
		if (pieces.get(0).equals(AllDrives.containerName)) {
			pieces.remove(0);
		}
//		System.out.println("finding entity, path=" + path);
		return findEntity(allDrives, pieces, path);
	}
		
	private Entity findEntity(Entity context, LinkedList<String> path, String originalPath) throws PathNotFoundException, EntityNotContainerException {
		// this trims down the path from the 0'th position, as we take branches in the tree hierarchy
		if (context == null) {
			throw new PathNotFoundException("Failed to find path");
		}
		String thisPathElement = path.remove(0);
		Entity child = context.getChild(thisPathElement);
		if (child == null) {
			throw new PathNotFoundException(originalPath);
		}
		if (path.size() < 1) { // we are at the end of the path
			return child;
		}
		return findEntity(child, path, originalPath);
	}
		
	public void create(Type type, String path, String name) throws PathNotFoundException, PathExistsException, IllegalFileSystemOperationException, ChildParentCycleException {
		// could introspect or dependency inject these, but it is late and I am ready to finish		
		Entity entity = null;		
		if (type == Type.DRIVE) { // we ignore path
			entity = new Drive(name);
			entity.setParent(allDrives);
		} else {
			if (type == Type.FOLDER) {
				entity = new Folder(name);				
			} else if (type == Type.TEXT_FILE) {
//				System.out.println("hi , we are making a text file with name=" + name);
				entity = new TextFile(name);			
			} else if (type == Type.ZIP_FILE) {
				entity = new ZipFile(name);
			} else {
				throw new IllegalFileSystemOperationException("Bad Type!");
			}
//			System.out.println("hi, for entity=" + entity.getName() + " we are setting parent=" + path + " path...");
			entity.setParent(findEntity(path));
		}
	}

    
    public void delete(String path) throws PathNotFoundException, EntityNotContainerException {
    	Entity child = findEntity(path);        
        child.getParent().removeChild(child);
    }
    
    public void move(String sourcePath, String destinationPath) throws PathNotFoundException, PathExistsException, IllegalFileSystemOperationException, ChildParentCycleException{
        Entity source = findEntity(sourcePath);        
        Entity dest = findEntity(destinationPath);
        Entity parent = source.getParent();
        try {
        	source.setParent(dest);
	        parent.removeChild(source);	        
        } catch (PathNotFoundException e) {
        	source.setParent(parent); throw e; // restore it
        } catch (PathExistsException e) {
        	source.setParent(parent); throw e; // restore it
        } catch (IllegalFileSystemOperationException e) {
        	source.setParent(parent); throw e; // restore it
        } catch (ChildParentCycleException e) {
        	source.setParent(parent); throw e; // restore it
        }
        
    }
    
    public void writeToFile(String path, String content) throws PathNotFoundException, NotATextFileException, EntityNotContainerException {
        findEntity(path).write(content);
    }
    
    public String readFromFile(String path) throws PathNotFoundException, NotATextFileException, EntityNotContainerException {
    	return findEntity(path).getText();
    }
    
    public int getFileSize(String path) throws EntityNotContainerException, PathNotFoundException {
    	return findEntity(path).getSize();
    }
    
    public Set<String> list(String path) throws EntityNotContainerException, IllegalFileSystemOperationException, PathNotFoundException {
        return entityCollectionToString(findEntity(path).getChildren());
    }
    
    public Set<String> getDrives() {
    	return entityCollectionToString(allDrives.getChildren());
    }
    
    private Set<String> entityCollectionToString(Collection<Entity> entities) {   
    	Set<String> items = new HashSet<String>(entities.size());
    	for (Entity e : entities) {
    		items.add(e.getName());
    	}
    	return items;
    }
    
    
}
