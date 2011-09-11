// Copyright (C) 2010 by Yan Huang <yhuang@virginia.edu>

package YaoGC;

import java.math.*;
import Cipher.*;

class E_AND_2_1 extends AND_2_1 {
    public E_AND_2_1() {
	super();
    }

    protected void execYao() {
	Wire inWireL = inputWires[0];
	Wire inWireR = inputWires[1];
	Wire outWire = outputWires[0];

	receiveGTT();
	
	int i0 = Wire.getLSB(inWireL.lbl);
	i0 = inWireL.invd ? (1 - i0) : i0;
	int i1 = Wire.getLSB(inWireR.lbl);
	i1 = inWireR.invd ? (1 - i1) : i1;
	
	BigInteger out = Cipher.decrypt(inWireL.lbl, inWireR.lbl, 
					outWire.serialNum, gtt[i0][i1]);
	
	outWire.setLabel(out);
    }
}
