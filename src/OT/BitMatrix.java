// Copyright (C) 2010 by Yan Huang <yhuang@virginia.edu>

package OT;

import java.math.*;
import java.util.*;
import java.security.SecureRandom;

class BitMatrix {
    private int nRows;
    private int nCols;
    BigInteger[] data;   // column vectors of the matrix

    public BitMatrix(int rows, int cols) {
	nRows = rows;
	nCols = cols;
	data = new BigInteger[nCols];
    }

    public void initialize(SecureRandom rnd) {
	for (int i = 0; i < nCols; i++)
	    data[i] = new BigInteger(nRows, rnd);
    }

    public BitMatrix transpose() {
	BitMatrix t = new BitMatrix(nCols, nRows);

	for (int i = 0; i < nRows; i++)
	    t.data[i] = BigInteger.ZERO;

	for (int j = 0; j < nCols; j++)
	    for (int i = 0; i < nRows; i++)
		if (data[j].testBit(i))
		    t.data[i] = t.data[i].setBit(j);

	return t;
    }
}