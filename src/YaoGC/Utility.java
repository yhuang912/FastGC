// Copyright (C) 2010 by Yan Huang <yhuang@virginia.edu>

package YaoGC;

import java.math.*;

public class Utility {
    /*
     * Extract the length number of least significant bits of BigInteger x.
     */
    public static int[] bigIntegerToBitArray(BigInteger x, int length) throws Exception {
	int[] result = new int[length];

	for (int i = 0; i < result.length; i++) {
	    result[i] = x.testBit(i) ? 1 : 0;
	}

	return result;
    }

    public static BigInteger bitArrayToUnsignedBigInteger(int[] bits) {
	BigInteger result = BigInteger.ZERO;

	for (int i = 0; i < bits.length; i++) 
	    if (bits[i] == 1)
		result = result.setBit(i);

	return result;
    }

    public static BigInteger bitArrayToSignedBigInteger(int[] bits) {
	BigInteger result = BigInteger.ZERO;

	for (int i = 0; i < bits.length; i++) 
	    if (bits[i] == 1)
		result = result.setBit(i);

	int signbit = bits[bits.length-1];
	if (signbit == 1) {
	    result = result.not().add(BigInteger.ONE.shiftLeft(bits.length)).not();
	}

	return result;
    }
}