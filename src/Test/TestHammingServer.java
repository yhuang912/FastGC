// Copyright (C) 2010 by Yan Huang <yhuang@virginia.edu>

package Test;

import java.util.*;
import java.math.*;
import java.security.SecureRandom;

import jargs.gnu.CmdLineParser;

import Utils.*;
import Program.*;

class TestHammingServer {
    static BigInteger bits;
    static int n;
    
    static SecureRandom rnd = new SecureRandom();

    private static void printUsage() {
	System.out.println("Usage: java TestHammingServer [{-n, --bit-length} length]");
    }

    private static void process_cmdline_args(String[] args) {
	CmdLineParser parser = new CmdLineParser();
	CmdLineParser.Option optionBitLength = parser.addIntegerOption('n', "bit-length");

	try {
	    parser.parse(args);
	}
	catch (CmdLineParser.OptionException e) {
	    System.err.println(e.getMessage());
	    printUsage();
	    System.exit(2);
	}

	n = ((Integer) parser.getOptionValue(optionBitLength, new Integer(100))).intValue();
    }

    private static void generateData() throws Exception {
	bits = new BigInteger(n, rnd);
    }

    public static void main(String[] args) throws Exception {

	StopWatch.pointTimeStamp("Starting program");
	process_cmdline_args(args);

	generateData();
	    
	HammingDistanceServer hammingserver = new HammingDistanceServer(bits, n);
	hammingserver.run();
    }
}