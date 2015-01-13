package com.github.milefork.sql;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Callable;


public class Dump {
	
	public boolean good=false;
	private Connection c;
	private ArrayList<String> dump;
	private int index;
	private int counter;
	private Timer t;
	
	public Dump(String className, String connUrl, String file) {
		process(className, connUrl, file);
	}
	
	private boolean establishConn(String className, String connUrl) {
		try {
			c = new Connection(className, connUrl);
			return true;
		} catch (SQLException e) {
			System.err.println("SQL Connection Error: "+e.getMessage());
			return false;
		}
	}
	
	private void process(String className, String connUrl, String file) {
		if(loadFile(file)) {
			try {
				if(establishConn(className, connUrl)) {
					System.out.println("Processing "+dump.size() +" Lines.");
					long start = System.nanoTime();
					Statement s = c.getConnection().createStatement();
					s.execute("BEGIN;");
					int siz = dump.size();
					counter = siz;
					prepareTimer(5000, new Callable<Integer>() {
						public Integer call() {
							return printStatus();
						}
					});
					for(int i=0;i<dump.size();i++) {
						index = i;
						
						s.execute(dump.get(i));
					}
					c.getConnection().close();
					t.cancel();
					t = null;
					dump = null;
					long duration = System.nanoTime() - start;
					System.out.printf("Done. Time elapsed: hours: %.0f minutes:%.0f seconds:%.0f \n", duration/3600000000000.0,(duration/60000000000.0)%60, (duration/1000000000.0)%60);
					good = true;
				}
				else {
					process(className, connUrl, file);
				}
			}
			catch (SQLException e) {
				System.err.println("SQL Error importing Dump: "+e.getMessage());
			}
		}
	}
	
	private int printStatus() {
		System.out.println("Excecuting "+index+" / "+counter);
		return 0;
	}
	private int printProcess() {
		System.out.println("Processing Line "+index+" / "+counter);
		return 0;
	}
	
	private boolean loadFile(String fileName) {
		ArrayList<String> q = new ArrayList<String>();
		String command="";
		try {
			FileInputStream instr = new FileInputStream(new File(fileName));
			Scanner s = new Scanner(instr,"UTF-8");
			dump = new ArrayList<String>();
			while (s.hasNextLine()) {
				command = s.nextLine();
				if(command.isEmpty()) {
					continue;
				}
				if(command.contains("/*")) {
					continue;
				}
				if(command.contains("--")) {
					continue;
				}
				q.add(command);
			}
			s.close();
			boolean cont=false;
			command = "";
			int size = q.size();
			counter = size;
			prepareTimer(5000, new Callable<Integer>() {
				public Integer call() {
					return printProcess();
				}
			});
			for(int i=0;i<size;i++) {
				String x = q.get(i);
				index = i;
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
			t.cancel();
			t=null;
			q.clear();
			q = null;
			
			
		} catch (FileNotFoundException e) {
			System.err.println("Error reading dump file: "+e.getMessage());
			return false;
		}
		return true;
	}
	
	private void prepareTimer(int trigger,Callable<Integer> func) {
		t = new Timer();
		t.schedule(new TimerTask() {
			
			@Override
			public void run() {
				try {
					func.call();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}, 0,trigger);
	}
}
