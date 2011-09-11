// Copyright (C) 2010 by Yan Huang <yhuang@virginia.edu>

package LookupTable;

import java.io.*;
import java.math.*;

import Cipher.Cipher;
import YaoGC.*;
import Utils.*;

public class LookupTableReceiver extends LookupTableAgent {
    public static ObjectInputStream ois;

    private BigInteger[][] EGTable;
    private BigInteger[] outputLabels;

    public LookupTableReceiver() {

	init();
    }

    public BigInteger[] execute(BigInteger[] rowLabels, BigInteger[] colLabels) {
	if (!extCase)
	    receiveEGTable();
	else
	    receiveEGTable_EXT();

	decryptTabelEntry(rowLabels, colLabels);

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

	EGTable = new BigInteger[nRows][nCols];
	outputLabels = new BigInteger[nBits];
    }

    private void receiveParams() throws Exception {
	nRows   = ois.readInt();
	nRIBits = ois.readInt();
	nCols   = ois.readInt();
	nCIBits = ois.readInt();
	nBits   = ois.readInt();
	extCase = ois.readBoolean();
    }

    private void receiveEGTable() {
    	int nBytes = (nBits-1)/8 + 1;
    	try {
    	    for (int i = 0; i < nRows; i++) 
    		for (int j = 0; j < nCols; j++)
    		    EGTable[i][j] = Utils.readBigInteger(nBytes*Wire.labelBitLength, ois);
    	} catch (Exception e) {
    	    e.printStackTrace();
    	    System.exit(1);
    	}
    }

    private void receiveEGTable_EXT() {
    	int nBytes = (nBits-1)/8 + 1;
    	try {
    	    for (int i = 0; i < nRows; i++) 
    		for (int j = 0; j < nCols; j++) {
		    boolean temp = ois.readBoolean();
		    if (temp)
			EGTable[i][j] = Utils.readBigInteger(nBytes*Wire.labelBitLength, ois);
		    else
			EGTable[i][j] = null;
		}
    	} catch (Exception e) {
    	    e.printStackTrace();
    	    System.exit(1);
    	}
    }

    private void decryptTabelEntry(BigInteger[] rLabels, BigInteger[] cLabels) {
	int row = 0, col = 0;

	for (int i = 0; i < nRIBits; i++)
	    if (rLabels[i].testBit(0))
		row |= (1<<i);
	
	for (int i = 0; i < nCIBits; i++)
	    if (cLabels[i].testBit(0))
		col |= (1<<i);

	BigInteger target = EGTable[row][col];

	BigInteger rowKey = BigInteger.ZERO;
	for (int k = nRIBits-1; k >= 0; k--)
	    rowKey = rowKey.shiftLeft(Wire.labelBitLength).xor(rLabels[k]);

	BigInteger colKey = BigInteger.ZERO;
	for (int k = nCIBits-1; k >= 0; k--)
	    colKey = colKey.shiftLeft(Wire.labelBitLength).xor(cLabels[k]);

	BigInteger key = rowKey.shiftLeft(nCIBits*Wire.labelBitLength).xor(colKey);

	BigInteger res = Cipher.decrypt(key, target, nBits*Wire.labelBitLength);

	BigInteger mask = BigInteger.ONE.shiftLeft(Wire.labelBitLength).
	    subtract(BigInteger.ONE);
	for (int k = nBits-1; k >= 0; k--)
	    outputLabels[k] = res.shiftRight(Wire.labelBitLength*k).and(mask);
    }
}