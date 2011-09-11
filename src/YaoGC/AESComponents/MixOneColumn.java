// Copyright (C) 2010 by Yan Huang <yhuang@virginia.edu>

package YaoGC.AESComponents;

import YaoGC.*;

public class MixOneColumn extends CompositeCircuit {

    public MixOneColumn() {
	super(32, 32, 12, "MixOneColumn");
    }

    protected void createSubCircuits() throws Exception {
	for (int i = 0; i < 4; i++) {
	    subCircuits[  M02(i)] = new MUL0x02();
	    subCircuits[XOR21(i)] = new XOR_2L_L(8);
	    subCircuits[XOR41(i)] = new XOR_4L_L(8);
	}

	super.createSubCircuits();
    }

    protected void connectWires() {
	for (int i = 0; i < 4; i++) {
	    for (int j = 0; j < 8; j++)
		inputWires[i*8+j].connectTo(subCircuits[M02(i)].inputWires, j);

	    for (int j = 0; j < 8; j++) {
		subCircuits[M02( i     )].outputWires[j].connectTo(subCircuits[XOR21(i)].inputWires, j);
		subCircuits[M02((i+1)%4)].outputWires[j].connectTo(subCircuits[XOR21(i)].inputWires, j+8);
	    }

	    for (int j = 0; j < 8; j++) {
		subCircuits[XOR21(i)].outputWires[j].connectTo(subCircuits[XOR41(i)].inputWires, j);
		inputWires[((i+1)%4)*8+j].connectTo(subCircuits[XOR41(i)].inputWires, j+8);
		inputWires[((i+2)%4)*8+j].connectTo(subCircuits[XOR41(i)].inputWires, j+16);
		inputWires[((i+3)%4)*8+j].connectTo(subCircuits[XOR41(i)].inputWires, j+24);
	    }
	}
    }

    protected void defineOutputWires() {
	for (int i = 0; i < 4; i++) {
	    System.arraycopy(subCircuits[XOR41(i)].outputWires, 0, outputWires, i*8, 8);
	}
    }

    private static int M02(int n) {
	return n;
    }

    private static int XOR21(int n) {
	return n+4;
    }

    private static int XOR41(int n) {
	return n+8;
    }
}