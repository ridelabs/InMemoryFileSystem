/**************************************************************
 * 11/29/12 12:05:06 Thu
 * Copyright Eric Harrison (ericjharrison at gmail dot com)
 * For demonstrating an in memory filesystem
 * Apache License applies, you may play with and 
 * modify, but leave this copyright in place
 *
 **************************************************************/


package com.crawlicious.filesystem.cli;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;


import com.crawlicious.filesystem.FileSystem;
import com.crawlicious.filesystem.entities.Entity;
import com.crawlicious.filesystem.entities.Entity.Type;

public class Handlers {
	private Map<String, Handler> handlers = new HashMap<String, Handler>();
	
	public String syntax(String message) {
		return "[mkdrive drive | mkdir folder | write file text... | mv source dest | cat file | ls folder | rm file | quit]\n" + message;
	}
	
	private String[] parseParentChild(String path) throws IOException {
		if (path.endsWith(Entity.PATH_SEPARATOR)) {
			path = path.substring(0, path.length()-1);
		}
		int loc = path.lastIndexOf(Entity.PATH_SEPARATOR);
		if (loc > -1) {
			return new String[] {path.substring(0, loc), path.substring(1+loc, path.length())};
		}
		throw new IOException("Failed to understand your path: " + path);
	}
	
	public Handler get(String command) {
		return this.handlers.get(command);
	}
	
	public Handlers(final FileSystem fs) {
		handlers.put("mkdrive", new Handler() {
			public String handle(LinkedList<String> args) throws IOException {
				if (args.size() != 1) {
					return syntax("missing drivename");
				}
				fs.create(Type.DRIVE, null, args.get(0));
				return "created drive: " + args.get(0);
			}}
		);
		handlers.put("mkdir", new Handler() {
			public String handle(LinkedList<String> args) throws IOException {
				if (args.size() != 1) {
					return syntax("not enough args");
				}
				String[] parentChild = parseParentChild(args.get(0));
				fs.create(Type.FOLDER, parentChild[0], parentChild[1]);
				return "created folder, parent=" + parentChild[0] + " folder=" + parentChild[1];
			}}
		);
		handlers.put("mv", new Handler() {
			public String handle(LinkedList<String> args) throws IOException {
				if (args.size() != 2) {
					return syntax("not enough args");
				}
				fs.move(args.get(0), args.get(1));
				return "moved " + args.get(0) + " to " + args.get(1);
			}}
		);
		handlers.put("cat", new Handler() {
			public String handle(LinkedList<String> args) throws IOException {
				if (args.size() != 1) {
					return syntax("not enough args");
				}
				return fs.readFromFile(args.get(0));
			}}
		);
		handlers.put("rm", new Handler() {
			public String handle(LinkedList<String> args) throws IOException {
				if (args.size() != 1) {
					return syntax("not enough args");
				}
				fs.delete(args.get(0));
				return "deleted " + args.get(0);
			}}
		);
		handlers.put("write", new Handler() {
			public String handle(LinkedList<String> args) throws IOException {
				if (args.size() < 2) {
					return syntax("not enough args");
				}
				StringBuilder builder = new StringBuilder();
				for (int i=1; i<args.size(); i++) {
					builder.append(args.get(i)).append(" ");
				}
				String[] parentChild = parseParentChild(args.get(0));
				fs.create(Type.TEXT_FILE, parentChild[0], parentChild[1]);
				fs.writeToFile(args.get(0), builder.toString());
				return "created file " + args.get(0) + " text=" + builder.toString();
			}}
		);
		handlers.put("ls", new Handler() {
			public String handle(LinkedList<String> args) throws IOException {
				if (args.size() != 1) {
					return syntax("not enough args");
				}
				return fs.list(args.get(0)) + "";
			}}
		);
	}
}
