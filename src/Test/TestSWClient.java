// Copyright (C) 2010 by Yan Huang <yhuang@virginia.edu>

package Test;

import java.util.*;
import java.security.SecureRandom;

import jargs.gnu.CmdLineParser;

import Utils.*;
import Program.*;

class TestSWClient {
    static String clientCodons;
    static boolean autogen;
    static int n;
    static String matrixFile;

    static SecureRandom rnd = new SecureRandom();

    private static void printUsage() {
	System.out.println("Usage: java TestSWClient [{-c, --codons} codons] [{-L, --max-bit-length} L] [{-a, --autogen}] [{-n, --protein-length} length] [{-s, --server} servername]");
    }

    private static void process_cmdline_args(String[] args) {
	CmdLineParser parser = new CmdLineParser();
	CmdLineParser.Option optionCodons = parser.addStringOption('c', "codons");
	CmdLineParser.Option optionMatrixFile = parser.addStringOption('m', "matrix-file");
	CmdLineParser.Option optionServerIPname = parser.addStringOption('s', "server");
	CmdLineParser.Option optionAuto = parser.addBooleanOption('a', "autogen");
	CmdLineParser.Option optionCodonLength = parser.addIntegerOption('n', "protein-length");
	CmdLineParser.Option optionIterCount = parser.addIntegerOption('r', "iteration");

	try {
	    parser.parse(args);
	}
	catch (CmdLineParser.OptionException e) {
	    System.err.println(e.getMessage());
	    printUsage();
	    System.exit(2);
	}

	clientCodons = (String) parser.getOptionValue(optionCodons, new String("A"));
	matrixFile = (String) parser.getOptionValue(optionMatrixFile, new String("matrices/Simple"));
	autogen = (Boolean) parser.getOptionValue(optionAuto, false);
	n = ((Integer) parser.getOptionValue(optionCodonLength, new Integer(100))).intValue();
	ProgClient.serverIPname = (String) parser.getOptionValue(optionServerIPname, new String("localhost"));
	Program.iterCount = ((Integer) parser.getOptionValue(optionIterCount, new Integer(1))).intValue();
    }

    static void generateData() throws Exception {
	StringBuilder sb = new StringBuilder("");
	for (int i = 0; i < n; i++) {
	    int r = rnd.nextInt(SmithWatermanCommon.codons.length());
	    sb.append(SmithWatermanCommon.codons.charAt(r));
	}

	clientCodons = sb.toString();
    }

    public static void main(String[] args) throws Exception {
	StopWatch.pointTimeStamp("Starting program");
	process_cmdline_args(args);
	SmithWatermanCommon.loadScoreMatrix(matrixFile);

	if (autogen)
	    generateData();

	System.out.println(clientCodons);
	SmithWatermanClient edclient = new SmithWatermanClient(clientCodons);
	edclient.run();
    }
}