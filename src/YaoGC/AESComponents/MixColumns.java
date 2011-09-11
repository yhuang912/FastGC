// Copyright (C) 2010 by Yan Huang <yhuang@virginia.edu>

package YaoGC.AESComponents;

import YaoGC.*;

public class MixColumns extends CompositeCircuit {

    public MixColumns() {
	super(128, 128, 4, "MixColumns");
    }

    public State startExecuting(State[] arrS) {
    	for (int i = 0; i < 16; i++) 
	    for (int j = 0; j < 8; j++) {
		inputWires[i*8+j].value = arrS[i].wires[j].value;
		inputWires[i*8+j].invd  = arrS[i].wires[j].invd;
		inputWires[i*8+j].setLabel(arrS[i].wires[j].lbl);
		inputWires[i*8+j].setReady();
	    }

	return State.fromWires(outputWires);
    }

    protected void createSubCircuits() throws Exception {
	for (int i = 0; i < 4; i++) {
	    subCircuits[i] = new MixOneColumn();
	}

	super.createSubCircuits();
    }

    protected void connectWires() {
	for (int i = 0; i < 4; i++)
	    for (int j = 0; j < 32; j++)
		inputWires[i*32+j].connectTo(subCircuits[i].inputWires, j);
    }

    protected void defineOutputWires() {
	for (int i = 0; i < 4; i++) {
	    System.arraycopy(subCircuits[i].outputWires, 0, outputWires, i*32,  32);
	}
    }
}