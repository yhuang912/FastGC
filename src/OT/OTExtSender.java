// Copyright (C) 2010 by Yan Huang <yhuang@virginia.edu>

package OT;

import java.util.*;
import java.math.*;
import java.io.*;
import java.security.SecureRandom;

import Cipher.Cipher;
import Utils.*;

public class OTExtSender extends Sender {
    static class SecurityParameter {
	public static final int k1 = 80;    // number of columns in T
	public static final int k2 = 80;
    }

    private static SecureRandom rnd = new SecureRandom();
    private Receiver rcver;
    private BigInteger s;
    private BigInteger[] keys;

    public OTExtSender(int numOfPairs, int msgBitLength,
		       ObjectInputStream in, ObjectOutputStream out) throws Exception {
	super(numOfPairs, msgBitLength, in, out);
	
	initialize();
    }

    public void execProtocol(BigInteger[][] msgPairs) throws Exception {
	BigInteger[][] cphPairs = (BigInteger[][]) ois.readObject();
	int bytelength;

	BitMatrix Q = new BitMatrix(numOfPairs, SecurityParameter.k1);

	for (int i = 0; i < SecurityParameter.k1; i++) {
	    if (s.testBit(i))
		Q.data[i] = Cipher.decrypt(keys[i], cphPairs[i][1], numOfPairs);
	    else
		Q.data[i] = Cipher.decrypt(keys[i], cphPairs[i][0], numOfPairs);
	}

	BitMatrix tQ = Q.transpose();
	
	BigInteger[][] y = new BigInteger[numOfPairs][2];
	for (int i = 0; i < numOfPairs; i++) {
	    y[i][0] = Cipher.encrypt(i, tQ.data[i],        msgPairs[i][0], msgBitLength);
	    y[i][1] = Cipher.encrypt(i, tQ.data[i].xor(s), msgPairs[i][1], msgBitLength);
	}

	bytelength = (msgBitLength-1)/8 + 1;
	for (int i = 0; i < numOfPairs; i++) {
	    Utils.writeBigInteger(y[i][0], bytelength, oos);
	    Utils.writeBigInteger(y[i][1], bytelength, oos);
	}
	oos.flush();
    }

    private void initialize() throws Exception {
	oos.writeInt(SecurityParameter.k1);
	oos.writeInt(SecurityParameter.k2);
	oos.writeInt(msgBitLength);
	oos.flush();

	rcver = new NPOTReceiver(SecurityParameter.k1, ois, oos);

	s = new BigInteger(SecurityParameter.k1, rnd);

	rcver.execProtocol(s);
	keys = rcver.getData();
    }
}