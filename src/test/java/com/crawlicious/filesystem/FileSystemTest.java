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

import java.util.Arrays;
import java.util.HashSet;

import org.junit.Test;
//import static org.junit.Assert.fail;

import com.crawlicious.filesystem.FileSystem;
import com.crawlicious.filesystem.entities.Entity.Type;
import com.crawlicious.filesystem.exceptions.ChildParentCycleException;
import com.crawlicious.filesystem.exceptions.IllegalFileSystemOperationException;
import com.crawlicious.filesystem.exceptions.PathExistsException;
import com.crawlicious.filesystem.exceptions.PathNotFoundException;

public class FileSystemTest {

	@Test
	public void testFs() throws PathNotFoundException, PathExistsException, IllegalFileSystemOperationException, ChildParentCycleException {
		FileSystem fs = new FileSystem();
		assertEquals(0, fs.getDrives().size());
		fs.create(Type.DRIVE, null, "c");
		assertEquals(1, fs.getDrives().size());
		
		assertEquals("c:", fs.getDrives().iterator().next());
		
		fs.create(Type.DRIVE, null, "d");
		
		fs.create(Type.FOLDER, "c:/", "Documents");
		fs.create(Type.FOLDER, "c:/", "Music");
		fs.create(Type.FOLDER, "c:/", "Pictures");
		fs.create(Type.TEXT_FILE, "c:/Music", "dubstep.txt");
		String dubstepString = "Gemini\nPendulum\nKlaypex\nMarty Party\nNero\nButch Clancy\nDeadmau5\n";
		fs.writeToFile("c:/Music/dubstep.txt", dubstepString);
		fs.create(Type.TEXT_FILE, "c:/Music", "classical.txt");
		String classicalString = "Amadeus\nBeethoven\nBach\nBrahms\nSchubert\n";
		fs.writeToFile("c:/Music/classical.txt", classicalString);
		fs.create(Type.ZIP_FILE, "c:/Music", "backups.zip");
		fs.create(Type.TEXT_FILE, "c:/Music/backups.zip", "oldstuff.txt");		
		String oldstuffString = "Holly\nElvis\nSinatra\nJohnny Cash\nNeal Diamond\n";
		fs.writeToFile("c:/Music/backups.zip/oldstuff.txt", oldstuffString);
		fs.create(Type.ZIP_FILE, "c:/Music/backups.zip", "ancientstuff.zip");
		fs.create(Type.TEXT_FILE, "c:/Music/backups.zip/ancientstuff.zip", "punk.txt");
		String punkString = "DK\nBlack Flag\nFugazi\nBad Religion\nGreen Day\nRamones\nThe Clash\n";
		fs.writeToFile("c:/Music/backups.zip/ancientstuff.zip/punk.txt", punkString);
		
		fs.printTree();
		
		// test read & write
		assertEquals(dubstepString, fs.readFromFile("c:/Music/dubstep.txt"));
		assertEquals(classicalString, fs.readFromFile("c:/Music/classical.txt"));
		assertEquals(oldstuffString, fs.readFromFile("c:/Music/backups.zip/oldstuff.txt"));
		assertEquals(punkString, fs.readFromFile("c:/Music/backups.zip/ancientstuff.zip/punk.txt"));
		
		// size roll up
		// NOTE: nested zip is / 4 because in a perfect world, you can just keep embedding & compressing data, but not in real life :-)
		// NOTE2: +1 because of integer division, it would be better to use a float here...
		assertEquals(1 + dubstepString.length() + classicalString.length() + (oldstuffString.length()/2) + (punkString.length()/4), fs.getFileSize("c:/"));
		assertEquals(1 + dubstepString.length() + classicalString.length() + (oldstuffString.length()/2) + (punkString.length()/4), fs.getFileSize("c:/Music"));
		assertEquals(dubstepString.length(), fs.getFileSize("c:/Music/dubstep.txt"));
		assertEquals(1 + (oldstuffString.length()/2) + (punkString.length()/4), fs.getFileSize("c:/Music/backups.zip")); 								

		// test path not found in listing
		try {
			fs.list("c:/missing.txt");
			fail("Should throw PathNotFoundException!");
		} catch (PathNotFoundException e) {
			// pass
		}
		
		// test list children		
		assertEquals(0, fs.list("d:/").size());
		assertEquals(3, fs.list("c:/").size());	
		assertEquals(new HashSet<String>(Arrays.asList(new String[] {"dubstep.txt", "classical.txt", "backups.zip"})), fs.list("c:/Music"));
		assertEquals(new HashSet<String>(Arrays.asList(new String[] {})), fs.list("d:/"));				
		
		// test move			
		try {
			fs.move("c:/Music/classical.txt", "d:/stuff");
			fail("Should throw PathNotFoundException!");
		} catch (PathNotFoundException e) {
			// pass
		}
		fs.create(Type.FOLDER, "d:/", "stuff");
		assertEquals(new HashSet<String>(Arrays.asList(new String[] {"stuff"})), fs.list("d:/"));
		assertEquals(new HashSet<String>(Arrays.asList(new String[] {})), fs.list("d:/stuff"));
		fs.move("c:/Music/classical.txt", "d:/stuff");
		assertEquals(new HashSet<String>(Arrays.asList(new String[] {"classical.txt"})), fs.list("d:/stuff"));
		assertEquals(new HashSet<String>(Arrays.asList(new String[] {"dubstep.txt", "backups.zip"})), fs.list("c:/Music"));
		assertEquals(classicalString, fs.readFromFile("d:/stuff/classical.txt"));
		
		// test delete
		fs.delete("d:/stuff/classical.txt");
		assertEquals(new HashSet<String>(Arrays.asList(new String[] {})), fs.list("d:/stuff"));
		try {
			fs.readFromFile("d:/stuff/classical.txt");
			fail("Should throw PathNotFoundException!");
		} catch (PathNotFoundException e) {
			// pass
		}
	}
	
}
