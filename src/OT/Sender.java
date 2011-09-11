// Copyright (C) 2010 by Yan Huang <yhuang@virginia.edu>

package OT;

import java.math.*;
import java.io.*;

public abstract class Sender {
    protected int numOfPairs;
    protected int msgBitLength;
    protected BigInteger[][] msgPairs;

    protected ObjectInputStream ois;
    protected ObjectOutputStream oos;

    public Sender(int numOfPairs, int msgBitLength, 
		  ObjectInputStream in, ObjectOutputStream out) {
	this.numOfPairs = numOfPairs;
	this.msgBitLength = msgBitLength;
	ois = in;
	oos = out;
    }

    public void execProtocol(BigInteger[][] msgPairs) throws Exception {
	if (msgPairs.length != numOfPairs)
	    throw new Exception("Message pair length error: " + 
				msgPairs.length + " != " + numOfPairs);

	this.msgPairs = msgPairs;
    }
}