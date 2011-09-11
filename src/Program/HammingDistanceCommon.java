// Copyright (C) 2010 by Yan Huang <yhuang@virginia.edu>

package Program;

import java.math.*;

import Utils.*;
import YaoGC.*;

class HammingDistanceCommon extends ProgCommon {
    static int bitVecLen;

    static int bitLength(int x) {
    	return BigInteger.valueOf(x).bitLength();
    }

    protected static void initCircuits() {
	ccs = new Circuit[1];
	ccs[0] = new HAMMING_2L_K(bitVecLen, bitLength(bitVecLen)+1);
    }

    public static State execCircuit(BigInteger[] slbs, BigInteger[] clbs) throws Exception {
	BigInteger[] lbs = new BigInteger[2*bitVecLen];
	System.arraycopy(slbs, 0, lbs, 0, bitVecLen);
	System.arraycopy(clbs, 0, lbs, bitVecLen, bitVecLen);
	State in = State.fromLabels(lbs);

	State out = ccs[0].startExecuting(in);
	
	StopWatch.taskTimeStamp("circuit garbling");

	return out;
    }
}