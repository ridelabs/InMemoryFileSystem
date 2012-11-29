/**************************************************************
 * 11/29/12 12:05:06 Thu
 * Copyright Eric Harrison (ericjharrison at gmail dot com)
 * For demonstrating an in memory filesystem
 * Apache License applies, you may play with and 
 * modify, but leave this copyright in place
 *
 **************************************************************/


package com.crawlicious.filesystem.entities;

public class ZipFile extends ContainerEntity {

	public ZipFile(String name) {
		super(Entity.Type.ZIP_FILE, name);
	}

	@Override
	public int getSize() {
		return super.getSize() / 2;
	}

}
