// Copyright (C) 2010 by Yan Huang <yhuang@virginia.edu>

package YaoGC.AESComponents;

import YaoGC.*;

class Square extends CompositeCircuit {
    public Square() {
	super(4, 4, 4, "Square");
    }

    protected void createSubCircuits() throws Exception {
	subCircuits[0] = new XOR_2_1();
	subCircuits[1] = new XOR_2_1();
	subCircuits[2] = new XOR_2_1();
	subCircuits[3] = new XOR_2_1();

	super.createSubCircuits();
    }

    protected void connectWires() {
	inputWires[0].connectTo(subCircuits[0].inputWires, 0);
	inputWires[2].connectTo(subCircuits[0].inputWires, 1);

	inputWires[2].connectTo(subCircuits[1].inputWires, 0);
	subCircuits[1].inputWires[1].fixWire(0);

	inputWires[1].connectTo(subCircuits[2].inputWires, 0);
	inputWires[3].connectTo(subCircuits[2].inputWires, 1);

	inputWires[3].connectTo(subCircuits[3].inputWires, 0);
	subCircuits[3].inputWires[1].fixWire(0);
    }

    protected void defineOutputWires() {
	outputWires[0] = subCircuits[0].outputWires[0];
	outputWires[1] = subCircuits[1].outputWires[0];
	outputWires[2] = subCircuits[2].outputWires[0];
	outputWires[3] = subCircuits[3].outputWires[0];
    }
}