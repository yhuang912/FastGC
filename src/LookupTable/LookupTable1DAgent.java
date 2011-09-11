// Copyright (C) 2010 by Yan Huang <yhuang@virginia.edu>

package LookupTable;

import java.math.BigInteger;

public abstract class LookupTable1DAgent {
    protected int nCols;
    protected int nCIBits, nBits;

    protected boolean extCase = false;

    public abstract BigInteger[] execute(BigInteger[] ciBitLabels);
    public abstract void setTable(int[] tab);
}