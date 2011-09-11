// Copyright (C) 2010 by Yan Huang <yhuang@virginia.edu>

package YaoGC.AESComponents;

import YaoGC.*;

class MultE_GF16 extends CompositeCircuit {
    private static final byte A  = 0;
    private static final byte B  = 1;
    private static final byte q0 = 2;
    private static final byte q2 = 3;
    private static final byte q3 = 4;

    public MultE_GF16() {
	super(4, 4, 5, "MultE_GF16");
    }

    protected void createSubCircuits() throws Exception {
	subCircuits[A]  = new XOR_2_1();
	subCircuits[B]  = new XOR_2_1();
	subCircuits[q0] = new XOR_2_1();
	subCircuits[q2] = new XOR_2_1();
	subCircuits[q3] = new XOR_2_1();

	super.createSubCircuits();
    }

    protected void connectWires() {
	inputWires[0].connectTo(subCircuits[A].inputWires, 0);
	inputWires[1].connectTo(subCircuits[A].inputWires, 1);

	inputWires[2].connectTo(subCircuits[B].inputWires, 0);
	inputWires[3].connectTo(subCircuits[B].inputWires, 1);

	inputWires[1].connectTo(subCircuits[q0].inputWires, 0);
	subCircuits[B].outputWires[0].connectTo(subCircuits[q0].inputWires, 1);

	subCircuits[A].outputWires[0].connectTo(subCircuits[q2].inputWires, 0);
	inputWires[2].connectTo(subCircuits[q2].inputWires, 1);

	subCircuits[A].outputWires[0].connectTo(subCircuits[q3].inputWires, 0);
	subCircuits[B].outputWires[0].connectTo(subCircuits[q3].inputWires, 1);
    }

    protected void defineOutputWires() {
	outputWires[0] = subCircuits[q0].outputWires[0];
	outputWires[1] = subCircuits[A].outputWires[0];
	outputWires[2] = subCircuits[q2].outputWires[0];
	outputWires[3] = subCircuits[q3].outputWires[0];
    }
}