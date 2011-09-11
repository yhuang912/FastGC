// Copyright (C) 2010 by Yan Huang <yhuang@virginia.edu>

package LookupTable;

import java.io.*;
import java.math.*;

import Cipher.Cipher;
import YaoGC.*;
import Utils.*;

public class LookupTableSender extends LookupTableAgent {
    public static ObjectOutputStream oos;

    private int[][] table;

    private BigInteger[][] EGTable;

    private BigInteger[] outputLabelPairs;

    public LookupTableSender(int[][] tab) {
	table = tab;

	init();
    }

    public BigInteger[] execute(BigInteger[] riBitPairs, BigInteger[] ciBitPairs) {
	if (riBitPairs.length != nRIBits || ciBitPairs.length != nCIBits) {
	    (new Exception("index bit length unmatch: " + 
			   riBitPairs.length + " != " + nRIBits + "\t" +
			   ciBitPairs.length + " != " + nCIBits)).printStackTrace();
	    System.exit(1);
	}

	generateOutputLabelPairs();
	
	if (!extCase) {
	    garbleEGTable();
	    encryptEGTable(riBitPairs, ciBitPairs);
	    permuteEGTable(riBitPairs, ciBitPairs);
	    sendEGTable();
	}
	else {
	    garbleEGTable_EXT();
	    encryptEGTable_EXT(riBitPairs, ciBitPairs);
	    permuteEGTable(riBitPairs, ciBitPairs);
	    sendEGTable_EXT();
	}

	return outputLabelPairs;
    }

    private void init() {
	nRows = table.length;
	nRIBits = bitLength(nRows-1);
	nCols = table[0].length;
	nCIBits = bitLength(nCols-1);

	int max = Integer.MIN_VALUE, min = Integer.MAX_VALUE;
	for (int i = 0; i < nRows; i++)
	    for (int j = 0; j < nCols; j++) {
		if (table[i][j] < min)
		    min = table[i][j];
		if (table[i][j] > max)
		    max = table[i][j];
	    }
	nBits = bitLength(max-min+1);

	if (nRows != (1<<nRIBits) || nCols != (1<<nCIBits)) {
	    nRows = 1<<nRIBits;
	    nCols = 1<<nCIBits;
	    extCase = true;
	}

	EGTable = new BigInteger[nRows][nCols];
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
	oos.writeInt(nRows);
	oos.writeInt(nRIBits);
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
	for (int i = 0; i < nRows; i++)
	    for (int j = 0; j < nCols; j++) {
		EGTable[i][j] = garble(table[i][j], nBits, outputLabelPairs);
	    }
    }

    private void garbleEGTable_EXT() {
	for (int i = 0; i < nRows; i++)
	    for (int j = 0; j < nCols; j++) {
		if (i < table.length && j < table[0].length)
		    EGTable[i][j] = garble(table[i][j], nBits, outputLabelPairs);
		else
		    EGTable[i][j] = null;
	    }
    }

    private void encryptEGTable(BigInteger[] ribp, BigInteger[] cibp) {
	for (int i = 0; i < nRows; i++) {
	    BigInteger rowKey = garble(i, nRIBits, ribp);
	    for (int j = 0; j < nCols; j++) {
		BigInteger colKey = garble(j, nCIBits, cibp);
		BigInteger key = rowKey.shiftLeft(nCIBits*Wire.labelBitLength).xor(colKey);
		BigInteger msg = garble(table[i][j], nBits, outputLabelPairs);

		EGTable[i][j] = Cipher.encrypt(key, msg, nBits*Wire.labelBitLength);
	    }	
	}
    }

    private void encryptEGTable_EXT(BigInteger[] ribp, BigInteger[] cibp) {
	for (int i = 0; i < nRows; i++) {
	    if (i < table.length) {
		BigInteger rowKey = garble(i, nRIBits, ribp);
		for (int j = 0; j < nCols; j++) {
		    if (j < table[0].length) {
			BigInteger colKey = garble(j, nCIBits, cibp);
			BigInteger key = rowKey.shiftLeft(nCIBits*Wire.labelBitLength).xor(colKey);
			BigInteger msg = garble(table[i][j], nBits, outputLabelPairs);
			
			EGTable[i][j] = Cipher.encrypt(key, msg, nBits*Wire.labelBitLength);
		    }
		}
	    }
	}
    }

    /* 
     * assume nRow and nCol are powers of 2.
     */
    private void permuteEGTable(BigInteger[] ribp, BigInteger[] cibp) {
	for (int i = 0; i < nRIBits; i++) {
	    if (ribp[i].testBit(0)) {
		swapMultRows(i);
	    }
	}

	for (int i = 0; i < nCIBits; i++) {
	    if (cibp[i].testBit(0)) {
		swapMultCols(i);
	    }
	}
    }

    private void sendEGTable() {
    	int nBytes = (nBits-1)/8 + 1;

    	try {
    	    for (int i = 0; i < nRows; i++) 
    		for (int j = 0; j < nCols; j++)
    		    Utils.writeBigInteger(EGTable[i][j], nBytes*Wire.labelBitLength, oos);
    	    oos.flush();
    	} catch(Exception e) {
    	    e.printStackTrace();
    	    System.exit(1);
    	}
    }

    private void sendEGTable_EXT() {
    	int nBytes = (nBits-1)/8 + 1;

    	try {
    	    for (int i = 0; i < nRows; i++) 
    		for (int j = 0; j < nCols; j++)
		    if (EGTable[i][j] != null) {
			oos.writeBoolean(true);
			Utils.writeBigInteger(EGTable[i][j], nBytes*Wire.labelBitLength, oos);
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
     * assume nRows is a power of 2.
     */
    private void swapMultRows(int n) {
	int bound = nRows >> 1;

	for (int i = 0; i < bound; i++) {
	    int x = insertBit(i, n, 0);
	    int y = insertBit(i, n, 1);
	    swapRows(x, y);
	}
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

    private void swapRows(int x, int y) {
	BigInteger[] temp = EGTable[x];
	EGTable[x] = EGTable[y];
	EGTable[y] = temp;
    }

    private void swapCols(int x, int y) {
	for (int i = 0; i < nRows; i++) {
	    BigInteger temp = EGTable[i][x];
	    EGTable[i][x] = EGTable[i][y];
	    EGTable[i][y] = temp;
	}
    }

    private static int bitLength(int x) {
	return BigInteger.valueOf(x).bitLength();
    }

}