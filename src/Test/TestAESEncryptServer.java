// Copyright (C) 2010 by Yan Huang <yhuang@virginia.edu>

package Test;

import java.util.*;
import java.security.SecureRandom;

import jargs.gnu.CmdLineParser;

import Utils.*;
import Program.*;

class TestAESEncryptServer {
    static boolean autogen;
    static String keyFile;
    static short[] key = {0x2b, 0x7e, 0x15, 0x16, 
			  0x28, 0xae, 0xd2, 0xa6, 
			  0xab, 0xf7, 0x15, 0x88, 
			  0x09, 0xcf, 0x4f, 0x3c};

    static SecureRandom rnd = new SecureRandom();

    private static void printUsage() {
	System.out.println("Usage: java TestAESEncryptServer [{-a, --autogen}]");
    }

    private static void process_cmdline_args(String[] args) {
	CmdLineParser parser = new CmdLineParser();
	CmdLineParser.Option optionKeyFile = parser.addStringOption('k', "key-file");
	CmdLineParser.Option optionAuto = parser.addBooleanOption('a', "autogen");

	try {
	    parser.parse(args);
	}
	catch (CmdLineParser.OptionException e) {
	    System.err.println(e.getMessage());
	    printUsage();
	    System.exit(2);
	}

	keyFile = (String) parser.getOptionValue(optionKeyFile, new String("key"));
	autogen = (Boolean) parser.getOptionValue(optionAuto, false);
    }

    static void generateData() throws Exception {
	key = new short[16];
	for (int i = 0; i < 16; i++) {
	    key[i] = (short) rnd.nextInt(0xff);
	}
    }

    public static void main(String[] args) throws Exception {

	StopWatch.pointTimeStamp("Starting program");
	process_cmdline_args(args);

	if (autogen)
	    generateData();

	AESEncryptServer aesEncryptServer = new AESEncryptServer(key, 4);
	aesEncryptServer.run();
    }
}