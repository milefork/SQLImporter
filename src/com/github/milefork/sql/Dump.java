package com.github.milefork.sql;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Scanner;


public class Dump {
	
	public boolean good=false;
	private Connection c;
	private ArrayList<String> dump;
	
	public Dump(String className, String connUrl, String file) {
		try {
			c = new Connection(className, connUrl);
		} catch (SQLException e) {
			System.err.println("SQL Connection Error: "+e.getMessage());
			return;
		}
		
		if(loadFile(file)) {
			try {
				if(!c.getConnection().isClosed()) {
					System.out.println("Processing "+dump.size() +" Lines.");
					long start = System.nanoTime();
					Statement s = c.getConnection().createStatement();
					s.execute("BEGIN;");
					for(int i=0;i<dump.size();i++) {
						printStatus(i);
						s.execute(dump.get(i));
					}
					c.getConnection().close();
					dump = null;
					long duration = System.nanoTime() - start;
					System.out.printf("Done. Time elapsed: %.2f hours:%.2f minutes:%.2f seconds", duration/3600000000000.0,duration/60000000000.0, duration/1000000000.0);
				}
				else {
					c = new Connection(className, file);
				}
			}
			catch (SQLException e) {
				System.err.println("SQL Error importing Dump: "+e.getMessage());
			}
		}
		
	}
	
	private void printStatus(int p) {
		System.out.println("Excecuting "+p+" / "+dump.size());
	}
	
	private boolean loadFile(String fileName) {
		ArrayList<String> q = new ArrayList<String>();
		String command="";
		try {
			FileInputStream instr = new FileInputStream(new File(fileName));
			Scanner s = new Scanner(instr,"UTF-8");
			dump = new ArrayList<String>();
			while (s.hasNextLine()) {
				q.add(s.nextLine());
			}
			s.close();
			boolean cont=false;
			for (String x : q) {
				if(!x.isEmpty()) {
					if(x.contains("/*")) {
						continue;
					}
					if(x.contains("--")) {
						continue;
					}
					if(x.contains(";")) {
						cont=true;
					}
					command+=x;
					if(cont) {
						dump.add(command);
						command="";
						cont=false;
					}
				}
			}
			q = null;
			
			
		} catch (FileNotFoundException e) {
			System.err.println("Error reading dump file: "+e.getMessage());
			return false;
		}
		return true;
	}
}
