// Copyright (C) 2010 by Yan Huang <yh8h@virginia.edu>

package Test;

import java.util.*;
import java.security.SecureRandom;

import jargs.gnu.CmdLineParser;

import Utils.*;
import Program.*;

class TestSWServer {
    static String serverCodons;
    static boolean autogen;
    static int n;
    static String matrixFile;

    static SecureRandom rnd = new SecureRandom();

    private static void printUsage() {
	System.out.println("Usage: java TestSWServer [{-c, --codons} codons] [{-L, --max-bit-length} L] [{-a, --autogen}] [{-n, --protein-length} length]");
    }

    private static void process_cmdline_args(String[] args) {
	CmdLineParser parser = new CmdLineParser();
	CmdLineParser.Option optionCodons = parser.addStringOption('c', "codons");
	CmdLineParser.Option optionMatrixFile = parser.addStringOption('m', "matrix-file");
	CmdLineParser.Option optionAuto = parser.addBooleanOption('a', "autogen");
	CmdLineParser.Option optionCodonLength = parser.addIntegerOption('n', "protein-length");

	try {
	    parser.parse(args);
	}
	catch (CmdLineParser.OptionException e) {
	    System.err.println(e.getMessage());
	    printUsage();
	    System.exit(2);
	}

	serverCodons = (String) parser.getOptionValue(optionCodons, new String("A"));
	matrixFile = (String) parser.getOptionValue(optionMatrixFile, new String("matrices/blosum20x20"));
	autogen = (Boolean) parser.getOptionValue(optionAuto, false);
	n = ((Integer) parser.getOptionValue(optionCodonLength, new Integer(100))).intValue();
    }

    static void generateData() throws Exception {
	StringBuilder sb = new StringBuilder("");
	for (int i = 0; i < n; i++) {
	    int r = rnd.nextInt(SmithWatermanCommon.codons.length());
	    sb.append(SmithWatermanCommon.codons.charAt(r));
	}

	serverCodons = sb.toString();
    }

    public static void main(String[] args) throws Exception {

	StopWatch.pointTimeStamp("Starting program");
	process_cmdline_args(args);
	SmithWatermanCommon.loadScoreMatrix(matrixFile);

	if (autogen)
	    generateData();

	System.out.println(serverCodons);
	SmithWatermanServer edserver = new SmithWatermanServer(serverCodons);
	edserver.run();
    }
}