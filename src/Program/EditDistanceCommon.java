// Copyright (C) 2010 by Yan Huang <yhuang@virginia.edu>

package Program;

import java.math.*;

import Utils.*;
import YaoGC.*;

public class EditDistanceCommon extends ProgCommon {
    static int sdnaLen;
    static int cdnaLen;
    public static int sigma;

    static int bitLength(int x) {
	return BigInteger.valueOf(x).bitLength();
    }

    private static void setInitialState(State s, int n) throws Exception {
	s.width  = bitLength(n);
	s.values = new int[s.width];
	s.invd   = new boolean[s.width];
	s.lbls   = new BigInteger[s.width];

	for (int i = 0; i < s.width; i++) {
	    s.values[i] = (n>>i) & 1;
	    s.invd[i] = false;
	}
    }

    protected static void initCircuits() {
	ccs = new OptEDCORE_3Lplus2S_L[EditDistanceCommon.bitLength(Math.max(sdnaLen, cdnaLen))];
	for (int i = 0; i < EditDistanceCommon.ccs.length; i++)
	    ccs[i] = new OptEDCORE_3Lplus2S_L(i+1, sigma);
    }

    public static State execCircuit(BigInteger[] sdnalbs, BigInteger[] cdnalbs) throws Exception {
	int sdnaLen = sdnalbs.length/sigma;
	int cdnaLen = cdnalbs.length/sigma;

	State[][] D = new State[sdnaLen+1][cdnaLen+1];

	for (int i = 0; i < sdnaLen+1; i++) {
	    D[i][0] = new State();
	    setInitialState(D[i][0], i);
	}
	Circuit.oos.flush();

	for (int j = 0; j < cdnaLen+1; j++) {
	    D[0][j] = new State();
	    setInitialState(D[0][j], j);
	}
	Circuit.oos.flush();

	for (int l = 2; l <= sdnaLen + cdnaLen; l++) {
	    for (int m = 1; m <= l; m++) {
		int i = m;
		int j = l - m;
		if (i >= 1 && i <= sdnaLen && j >= 1 && j <= cdnaLen) {
		    int W = bitLength(Math.max(i, j));
		    
		    int[] vals = new int[3*W+2*sigma];
		    boolean[] invd = new boolean[3*W+2*sigma];
		    BigInteger[] lbls = new BigInteger[3*W+2*sigma];

		    for (int s = 0; s < sigma; s++) {
			vals[s] = Wire.UNKNOWN_SIG;
			invd[s] = false;
			lbls[s] = cdnalbs[sigma*(j-1)+s];

			vals[s+sigma] = Wire.UNKNOWN_SIG;
			invd[s+sigma] = false;
			lbls[s+sigma] = sdnalbs[sigma*(i-1)+s];
		    }

		    System.arraycopy(D[i-1][j-1].values, 0, vals, sigma*2, D[i-1][j-1].width);
		    System.arraycopy(D[i-1][j-1].invd,   0, invd, sigma*2, D[i-1][j-1].width);
		    System.arraycopy(D[i-1][j-1].lbls,   0, lbls, sigma*2, D[i-1][j-1].width);
		    if (D[i-1][j-1].width < W) { // it must be the case that (D[i-1][j-1].wdith + 1 == W)
			vals[W+sigma*2-1] = 0;
		    }

		    System.arraycopy(D[i][j-1].values, 0, vals, W+sigma*2, D[i][j-1].width);
		    System.arraycopy(D[i][j-1].invd,   0, invd, W+sigma*2, D[i][j-1].width);
		    System.arraycopy(D[i][j-1].lbls,   0, lbls, W+sigma*2, D[i][j-1].width);
		    if (D[i][j-1].width < W) { // it must be the case that (D[i][j-1].wdith + 1 == W)
			vals[2*W+sigma*2-1] = 0;
		    }

		    System.arraycopy(D[i-1][j].values, 0, vals, 2*W+sigma*2, D[i-1][j].width);
		    System.arraycopy(D[i-1][j].invd,   0, invd, 2*W+sigma*2, D[i-1][j].width);
		    System.arraycopy(D[i-1][j].lbls,   0, lbls, 2*W+sigma*2, D[i-1][j].width);
		    if (D[i-1][j].width < W) { // it must be the case that (D[i-1][j].wdith + 1 == W)
			vals[3*W+sigma*2-1] = 0;
		    }

		    EditDistanceCommon.ccs[W-1].startExecuting(vals, invd, lbls);

		    vals = new int[W];
		    invd = new boolean[W];
		    lbls = new BigInteger[W];
		    for (int k = 0; k < W; k++) {
			vals[k] = EditDistanceCommon.ccs[W-1].outputWires[k].value;
			invd[k] = EditDistanceCommon.ccs[W-1].outputWires[k].invd;
			lbls[k] = EditDistanceCommon.ccs[W-1].outputWires[k].lbl;
		    }

		    D[i][j] = new State();
		    D[i][j].set(W, vals, invd, lbls);

		    D[i-1][j-1] = null;
		}
	    }
	}

	StopWatch.taskTimeStamp("circuit garbling");

	return D[sdnaLen][cdnaLen];
    }

    static class State {
	public int          width;
	public int[]        values;
	public boolean[]    invd;
	public BigInteger[] lbls;

	public void set(int w, int[] vals, boolean[] invd, BigInteger[] lbs) {
	    this.width  = w;
	    this.values = vals;
	    this.invd   = invd;
	    this.lbls   = lbs;
	}
    }

}