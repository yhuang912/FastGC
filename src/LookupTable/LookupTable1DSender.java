// Copyright (C) 2010 by Yan Huang <yhuang@virginia.edu>

package LookupTable;

import java.io.*;
import java.math.*;

import Cipher.Cipher;
import YaoGC.*;
import Utils.*;

public class LookupTable1DSender extends LookupTable1DAgent {
    public static ObjectOutputStream oos;

    private int[] table;

    private BigInteger[] EGTable;

    private BigInteger[] outputLabelPairs;

    public LookupTable1DSender() {
	init();
    }

    public void setTable(int[] tab) {
	table = tab;
    }

    public BigInteger[] execute(BigInteger[] ciBitPairs) {
	if (ciBitPairs.length != nCIBits) {
	    (new Exception("index bit length unmatch: " + 
			   ciBitPairs.length + " != " + nCIBits)).printStackTrace();
	    System.exit(1);
	}

	generateOutputLabelPairs();
	
	if (!extCase) {
	    garbleEGTable();
	    encryptEGTable(ciBitPairs);
	    permuteEGTable(ciBitPairs);
	    sendEGTable();
	}
	else {
	    garbleEGTable_EXT();
	    encryptEGTable_EXT(ciBitPairs);
	    permuteEGTable(ciBitPairs);
	    sendEGTable_EXT();
	}

	return outputLabelPairs;
    }

    private void init() {
	nCols = 32;
	nCIBits = bitLength(nCols-1);
	nBits = 4;
	
	if (nCols != (1<<nCIBits)) {
	    nCols = 1<<nCIBits;
	    extCase = true;
	}

	EGTable = new BigInteger[nCols];
	outputLabelPairs = new BigInteger[nBits];

	try {
	    sendParams();
	}
	catch (Exception e) {
	    e.printStackTrace();
	    System.exit(1);
	}
    }

    private void sendParams() throws Exception {
	oos.writeInt(nCols);
	oos.writeInt(nCIBits);
	oos.writeInt(nBits);
	oos.writeBoolean(extCase);
	oos.flush();
    }

    private void generateOutputLabelPairs() {
	for (int i = 0; i < nBits; i++) {
	    BigInteger[] lp = Wire.newLabelPair();
	    outputLabelPairs[i] = lp[0];
	}
    }

    private void garbleEGTable() {
	for (int j = 0; j < nCols; j++) {
	    EGTable[j] = garble(table[j], nBits, outputLabelPairs);
	}
    }

    private void garbleEGTable_EXT() {
	for (int j = 0; j < nCols; j++) {
	    if (j < table.length)
		EGTable[j] = garble(table[j], nBits, outputLabelPairs);
	    else
		EGTable[j] = null;
	}
    }

    private void encryptEGTable(BigInteger[] cibp) {
	for (int j = 0; j < nCols; j++) {
	    BigInteger colKey = garble(j, nCIBits, cibp);
	    BigInteger msg = garble(table[j], nBits, outputLabelPairs);

	    EGTable[j] = Cipher.encrypt(colKey, msg, nBits*Wire.labelBitLength);
	}	
    }

    private void encryptEGTable_EXT(BigInteger[] cibp) {
	for (int j = 0; j < nCols; j++) {
	    if (j < table.length) {
		BigInteger colKey = garble(j, nCIBits, cibp);
		BigInteger msg = garble(table[j], nBits, outputLabelPairs);
			
		EGTable[j] = Cipher.encrypt(colKey, msg, nBits*Wire.labelBitLength);
	    }
	}
    }

    /* 
     * assume nRow and nCol are powers of 2.
     */
    private void permuteEGTable(BigInteger[] cibp) {
	for (int i = 0; i < nCIBits; i++) {
	    if (cibp[i].testBit(0)) {
		swapMultCols(i);
	    }
	}
    }

    private void sendEGTable() {
    	int nBytes = (nBits-1)/8 + 1;

    	try {
	    for (int j = 0; j < nCols; j++)
		Utils.writeBigInteger(EGTable[j], nBytes*Wire.labelBitLength, oos);
    	    oos.flush();
    	} catch(Exception e) {
    	    e.printStackTrace();
    	    System.exit(1);
    	}
    }

    private void sendEGTable_EXT() {
    	int nBytes = (nBits-1)/8 + 1;

    	try {
	    for (int j = 0; j < nCols; j++)
		if (EGTable[j] != null) {
		    oos.writeBoolean(true);
		    Utils.writeBigInteger(EGTable[j], nBytes*Wire.labelBitLength, oos);
		}
		else
		    oos.writeBoolean(false);
    	    oos.flush();
    	} catch(Exception e) {
    	    e.printStackTrace();
    	    System.exit(1);
    	}
    }

    private static BigInteger garble(int number, int bitlen, BigInteger[] lblpairs) {
	BigInteger res = BigInteger.ZERO;

	for (int k = bitlen-1; k >= 0; k--) {
	    res = res.shiftLeft(Wire.labelBitLength);
	    if ((number & (1 << k)) == 0)
		res = res.xor(lblpairs[k]);
	    else
		res = res.xor(Wire.conjugate(lblpairs[k]));
	}

	return res;
    }

    private static int insertBit(int n, int bitPos, int bitVal) {
	int mask = (1<<bitPos)-1;
	int lowBits  = n & mask;
	mask = ~mask;
	int highBits = n & mask;

	return (highBits<<1) | (bitVal<<bitPos) | lowBits;
    }

    /*
     * assume nCols is a power of 2.
     */
    private void swapMultCols(int n) {
	int bound = nCols >> 1;

	for (int i = 0; i < bound; i++) {
	    int x = insertBit(i, n, 0);
	    int y = insertBit(i, n, 1);
	    swapCols(x, y);
	}
    }

    private void swapCols(int x, int y) {
	BigInteger temp = EGTable[x];
	EGTable[x] = EGTable[y];
	EGTable[y] = temp;
    }

    private static int bitLength(int x) {
	return BigInteger.valueOf(x).bitLength();
    }

}