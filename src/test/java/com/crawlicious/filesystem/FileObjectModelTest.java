/**************************************************************
 * 11/29/12 12:05:06 Thu
 * Copyright Eric Harrison (ericjharrison at gmail dot com)
 * For demonstrating an in memory filesystem
 * Apache License applies, you may play with and 
 * modify, but leave this copyright in place
 *
 **************************************************************/


package com.crawlicious.filesystem;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.crawlicious.filesystem.entities.Drive;
import com.crawlicious.filesystem.entities.Folder;
import com.crawlicious.filesystem.entities.TextFile;
import com.crawlicious.filesystem.entities.ZipFile;
import com.crawlicious.filesystem.exceptions.ChildParentCycleException;
import com.crawlicious.filesystem.exceptions.EntityMustBeContainedException;
import com.crawlicious.filesystem.exceptions.EntityNotContainableException;
import com.crawlicious.filesystem.exceptions.EntityNotContainerException;
import com.crawlicious.filesystem.exceptions.NotATextFileException;
import com.crawlicious.filesystem.exceptions.PathExistsException;

public class FileObjectModelTest {
    
    @Test
    public void testDrive() throws PathExistsException, EntityMustBeContainedException, EntityNotContainerException, EntityNotContainableException, ChildParentCycleException {
    	Drive c = new Drive("C");
    	// make sure we get :'s
    	assertEquals("C:", c.getName());
    	assertEquals("C:/", c.getPath());
    	Drive d = new Drive("D:");
    	// make sure we don't get extra :'s
    	assertEquals("D:", d.getName());
    	assertEquals("D:/", d.getPath());
    	// test not containable
    	try {
    		c.setParent(d);
    		fail("should not be containable");
    	} catch (EntityNotContainableException e) {
    		// pass
    	}
    	// test that right drive gets the file
    	assertEquals(0, c.getChildren().size());
    	assertEquals(0, d.getChildren().size());
    	Folder f1 = new Folder("Documents");
    	f1.setParent(c);    	
    	assertEquals(1, c.getChildren().size());
    	assertEquals(0, d.getChildren().size());
    	assertEquals(c.getChildren().iterator().next(), f1);  
    	// test no duplicates
    	try {
    		TextFile t = new TextFile("Documents"); // duplicate name!!
    		t.setParent(c);
    		fail("should not allow dupe paths");
    	} catch (PathExistsException e) {
    		// pass
    	}
    }
    
    @Test 
    public void testTextFile() throws EntityMustBeContainedException, PathExistsException, EntityNotContainableException, EntityNotContainerException, ChildParentCycleException, NotATextFileException {
    	Drive c = new Drive("Hungry");
    	Folder items = new Folder("items");
    	items.setParent(c);
    	
    	TextFile hat = new TextFile("hat.txt");
    	hat.write("I want to eat\na hat now\n");
    	// test text
    	TextFile glove = new TextFile("glove");
    	String gloveMessage = "i'd rather have mittens!";
    	glove.write(gloveMessage);    	
    	assertEquals(gloveMessage, glove.getText());
    	// test appending
    	glove.write("the end");
    	assertEquals(gloveMessage+"the end", glove.getText());
    	// test overwrite
    	glove.setText("sleeping");
    	assertEquals("sleeping", glove.getText());
    	
    	try {
    		hat.setParent(hat);
    		fail("should not allow cyclical paths");
    	} catch (ChildParentCycleException e) {
    		// pass
    	}
    	
    	try {
    		hat.setParent(glove);
    		fail("should not allow text files to contain stuff");    		
    	} catch (EntityNotContainerException e) {
    		// pass
    	}
    	    	
    	Folder drinks = new Folder("Drinks");
    	drinks.setParent(c);    	
    	assertEquals(2, c.getChildren().size());
 
    }
    
    @Test 
    public void testZipFolderDrivePathsSize() throws EntityMustBeContainedException, PathExistsException, EntityNotContainableException, EntityNotContainerException, ChildParentCycleException {
    	/*
    	 * Hungry
    	 *   - Food
    	 *     - pizza.txt
    	 *   - Drinks\
    	 *     - cold.zip
    	 *       - ice.zip
    	 *         - water.txt
    	 *     - hot.txt  
    	 *       
    	 */
    	Drive hungry = new Drive("Hungry");
    	Folder food = new Folder("Food");
    	food.setParent(hungry); 
    	TextFile pizza = new TextFile("pizza.txt");
    	String pizzaMessage = "I want to eat\nPizza now\n";
    	pizza.write(pizzaMessage);
    	pizza.setParent(food);
    	// test path
    	assertEquals("Hungry:/Food/pizza.txt", pizza.getPath());
    	assertEquals("Hungry:/Food/", food.getPath());
    	assertEquals("Hungry:/", hungry.getPath());
    	// test file size
    	assertEquals(pizzaMessage.length(), pizza.getSize());
    	
    	// test zip & nesting
    	Folder drinks = new Folder("Drinks");    	
    	drinks.setParent(hungry);    	
    	assertEquals(2, hungry.getChildren().size());
    	
    	ZipFile cold = new ZipFile("cold.zip");
    	cold.setParent(drinks);
    	
    	ZipFile ice = new ZipFile("ice.zip");
    	ice.setParent(cold);
    	
    	TextFile water = new TextFile("water.txt");
    	String waterMessage = "i like the clean cold water\nin my canteen\nbye!\n";
    	water.write(waterMessage);
    	water.setParent(ice);
    	
    	TextFile hot = new TextFile("hot.txt");
    	String hotMessage = "Hot water is great for washing\n\nand hot cocoa\n\nand melting stuff\n";
    	hot.setText(hotMessage);
    	hot.setParent(drinks);
    	
    	// test paths
    	assertEquals("Hungry:/", hungry.getPath());
    	assertEquals("Hungry:/Drinks/", drinks.getPath());
    	assertEquals("Hungry:/Drinks/cold.zip/", cold.getPath());
    	assertEquals("Hungry:/Drinks/cold.zip/ice.zip/", ice.getPath());
    	assertEquals("Hungry:/Drinks/cold.zip/ice.zip/water.txt", water.getPath());
    	assertEquals("Hungry:/Drinks/hot.txt", hot.getPath());
    	
    	// test sizes of all user facing entities
    	assertEquals(((water.getSize()/4)+hot.getSize()) + pizzaMessage.length(), hungry.getSize());
    	assertEquals((water.getSize()/4)+hot.getSize(), drinks.getSize());
    	assertEquals(water.getSize()/4, cold.getSize()); // would be cool if you could nest zip files to make them arbitrarily small LOL!
    	assertEquals(water.getSize()/2, ice.getSize());
    	assertEquals(waterMessage.length(), water.getSize());
    	assertEquals(hotMessage.length(), hot.getSize());
    	
    }

}
