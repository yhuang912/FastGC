// Copyright (C) 2010 by Yan Huang <yhuang@virginia.edu>

package YaoGC.AESComponents;

import YaoGC.*;

public class AddRoundKey extends CompositeCircuit {

    public AddRoundKey() {
	super(256, 128, 128, "AddRoundKey");
    }

    public State startExecuting(State key, int start, State state) {
    	for (int i = 0; i < 128; i++) {
	    inputWires[i].value = state.wires[i].value;
	    inputWires[i].invd = state.wires[i].invd;
	    inputWires[i].setLabel(state.wires[i].lbl);
	    inputWires[i].setReady();

	    inputWires[i+128].value = key.wires[i+start].value;
	    inputWires[i+128].invd = key.wires[i+start].invd;
	    inputWires[i+128].setLabel(key.wires[i+start].lbl);
	    inputWires[i+128].setReady();
	}

	return State.fromWires(outputWires);
    }

    protected void createSubCircuits() throws Exception {
	for (int i = 0; i < 128; i++) 
	    subCircuits[i] = new XOR_2_1();

	super.createSubCircuits();
    }

    protected void connectWires() {
	for (int i = 0; i < 128; i++) {
	    inputWires[X(i)].connectTo(subCircuits[i].inputWires, 0);
	    inputWires[Y(i)].connectTo(subCircuits[i].inputWires, 1);
	}
    }

    protected void defineOutputWires() {
	for (int i = 0; i < 128; i++)
	    outputWires[i] = subCircuits[i].outputWires[0];
    }

    private static int X(int i) {
	return i + 128;
    }

    private static int Y(int i) {
	return i;
    }
}