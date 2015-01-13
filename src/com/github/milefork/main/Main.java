package com.github.milefork.main;


import org.apache.commons.cli.*;

import com.github.milefork.sql.Dump;

public class Main {
	
	private static final String version = "1.0";
	private static Options opt;

	public static void main(String[] args) {
		createOptions();
		printHelp();
		CommandLineParser p = new GnuParser();
		try {
			CommandLine l = p.parse(opt, args);
			if(l.hasOption("v")) {
				printVersion();
			}
			if(l.hasOption("i")) {
				String ar [] = l.getOptionValues("i");
				callImport(ar[0], ar[1]);
			}
		}
		catch (ParseException e) {
			System.err.println("Parse failed. Reason: "+e.getMessage());
		}
	}
	
	private static void callImport(String connUrl, String file) {
		if(connUrl.contains("mysql")) {
			Dump d = new Dump("com.mysql.jdbc.Driver", connUrl, file);
			if(d.good)
				System.out.println("Dump imported successfully");
			d = null;
		}
	}
	
	private static void printVersion() {
		System.out.println();
		System.out.println("Current Version: "+version);
	}
	
	private static void printHelp() {
		HelpFormatter form = new HelpFormatter();
		form.printHelp("sqlimporter", opt);
	}
	
	private static void createOptions() {
		opt = new Options();
		Option vers = new Option("v",false, "prints the version");
		Option imp = OptionBuilder.withArgName("connectionString> <file").hasArgs(2).withValueSeparator(' ').withDescription("imports SQL Dump file using the JDBC Connection string").create("i");
		
		opt.addOption(imp);
		opt.addOption(vers);
	}

}
