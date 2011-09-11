// Copyright (C) 2010 by Yan Huang <yhuang@virginia.edu>

package LookupTable;

import java.io.*;
import java.math.*;

import Cipher.Cipher;
import YaoGC.*;
import Utils.*;

public class LookupTable1DReceiver extends LookupTable1DAgent {
    public static ObjectInputStream ois;

    private BigInteger[] EGTable;
    private BigInteger[] outputLabels;

    public LookupTable1DReceiver() {

	init();
    }

    public void setTable(int[] tab) {}

    public BigInteger[] execute(BigInteger[] colLabels) {
	if (!extCase)
	    receiveEGTable();
	else
	    receiveEGTable_EXT();

	decryptTabelEntry(colLabels);

	return outputLabels;
    }

    private void init() {
	try {
	    receiveParams();
	}
	catch (Exception e) {
	    e.printStackTrace();
	    System.exit(1);
	}

	EGTable = new BigInteger[nCols];
	outputLabels = new BigInteger[nBits];
    }

    private void receiveParams() throws Exception {
	nCols   = ois.readInt();
	nCIBits = ois.readInt();
	nBits   = ois.readInt();
	extCase = ois.readBoolean();
    }

    private void receiveEGTable() {
    	int nBytes = (nBits-1)/8 + 1;
    	try {
	    for (int j = 0; j < nCols; j++)
		EGTable[j] = Utils.readBigInteger(nBytes*Wire.labelBitLength, ois);
    	} catch (Exception e) {
    	    e.printStackTrace();
    	    System.exit(1);
    	}
    }

    private void receiveEGTable_EXT() {
    	int nBytes = (nBits-1)/8 + 1;
    	try {
	    for (int j = 0; j < nCols; j++) {
		boolean temp = ois.readBoolean();
		if (temp)
		    EGTable[j] = Utils.readBigInteger(nBytes*Wire.labelBitLength, ois);
		else
		    EGTable[j] = null;
		}
    	} catch (Exception e) {
    	    e.printStackTrace();
    	    System.exit(1);
    	}
    }

    private void decryptTabelEntry(BigInteger[] cLabels) {
	int col = 0;

	for (int i = 0; i < nCIBits; i++)
	    if (cLabels[i].testBit(0))
		col |= (1<<i);

	BigInteger target = EGTable[col];

	BigInteger colKey = BigInteger.ZERO;
	for (int k = nCIBits-1; k >= 0; k--)
	    colKey = colKey.shiftLeft(Wire.labelBitLength).xor(cLabels[k]);

	BigInteger res = Cipher.decrypt(colKey, target, nBits*Wire.labelBitLength);

	BigInteger mask = BigInteger.ONE.shiftLeft(Wire.labelBitLength).
	    subtract(BigInteger.ONE);
	for (int k = nBits-1; k >= 0; k--)
	    outputLabels[k] = res.shiftRight(Wire.labelBitLength*k).and(mask);
    }
}