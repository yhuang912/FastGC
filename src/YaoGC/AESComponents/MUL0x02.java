// Copyright (C) 2010 by Yan Huang <yh8h@virginia.edu>

package YaoGC.AESComponents;

import YaoGC.*;

class MUL0x02 extends CompositeCircuit {

    public MUL0x02() {
	super(8, 8, 8, "MUL0x02");
    }

    protected void createSubCircuits() throws Exception {
	subCircuits[0] = new XOR_2_1();
	subCircuits[1] = new XOR_2_1();
	subCircuits[2] = new XOR_2_1();
	subCircuits[3] = new XOR_2_1();
	subCircuits[4] = new XOR_2_1();
	subCircuits[5] = new XOR_2_1();
	subCircuits[6] = new XOR_2_1();
	subCircuits[7] = new XOR_2_1();

	super.createSubCircuits();
    }

    protected void connectWires() {
	inputWires[7].connectTo(subCircuits[0].inputWires, 0);
	subCircuits[0].inputWires[1].fixWire(0);

	inputWires[0].connectTo(subCircuits[1].inputWires, 0);
	inputWires[7].connectTo(subCircuits[1].inputWires, 1);

	inputWires[1].connectTo(subCircuits[2].inputWires, 0);
	subCircuits[2].inputWires[1].fixWire(0);

	inputWires[2].connectTo(subCircuits[3].inputWires, 0);
	inputWires[7].connectTo(subCircuits[3].inputWires, 1);

	inputWires[3].connectTo(subCircuits[4].inputWires, 0);
	inputWires[7].connectTo(subCircuits[4].inputWires, 1);

	inputWires[4].connectTo(subCircuits[5].inputWires, 0);
	subCircuits[5].inputWires[1].fixWire(0);

	inputWires[5].connectTo(subCircuits[6].inputWires, 0);
	subCircuits[6].inputWires[1].fixWire(0);

	inputWires[6].connectTo(subCircuits[7].inputWires, 0);
	subCircuits[7].inputWires[1].fixWire(0);
    }

    protected void defineOutputWires() {
	outputWires[7] = subCircuits[7].outputWires[0];
	outputWires[6] = subCircuits[6].outputWires[0];
	outputWires[5] = subCircuits[5].outputWires[0];
	outputWires[4] = subCircuits[4].outputWires[0];
	outputWires[3] = subCircuits[3].outputWires[0];
	outputWires[2] = subCircuits[2].outputWires[0];
	outputWires[1] = subCircuits[1].outputWires[0];
	outputWires[0] = subCircuits[0].outputWires[0];
    }
}