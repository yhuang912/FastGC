// Copyright (C) 2010 by Yan Huang <yhuang@virginia.edu>

package YaoGC.AESComponents;

import YaoGC.*;

class InvMAP extends CompositeCircuit {
    private static final byte A  = 0;
    private static final byte B  = 1;
    private static final byte a0 = 2;
    private static final byte a1 = 3;
    private static final byte a2 = 4;
    private static final byte a3 = 5;
    private static final byte a4 = 6;
    private static final byte a5 = 7;
    private static final byte a6 = 8;
    private static final byte a7 = 9;

    private static final byte l0 = 0;
    private static final byte l1 = 1;
    private static final byte l2 = 2;
    private static final byte l3 = 3;
    private static final byte h0 = 4;
    private static final byte h1 = 5;
    private static final byte h2 = 6;
    private static final byte h3 = 7;

    public InvMAP() {
	super(8, 8, 10, "InvMAP");
    }

    protected void createSubCircuits() throws Exception {
	subCircuits[A]  = new XOR_2_1();
	subCircuits[B]  = new XOR_2_1();
	subCircuits[a0] = new XOR_2_1();
	subCircuits[a1] = new XOR_2_1();
	subCircuits[a2] = new XOR_2_1();
	subCircuits[a3] = new XOR_L_1(3);
	subCircuits[a4] = new XOR_L_1(3);
	subCircuits[a5] = new XOR_2_1();
	subCircuits[a6] = new XOR_L_1(4);
	subCircuits[a7] = new XOR_L_1(3);

	super.createSubCircuits();
    }

    protected void connectWires() {
	inputWires[l1].connectTo(subCircuits[A].inputWires, 0);
	inputWires[h3].connectTo(subCircuits[A].inputWires, 1);

	inputWires[h0].connectTo(subCircuits[B].inputWires, 0);
	inputWires[h1].connectTo(subCircuits[B].inputWires, 1);

	inputWires[l0].connectTo(subCircuits[a0].inputWires, 0);
	inputWires[h0].connectTo(subCircuits[a0].inputWires, 1);

	subCircuits[B].outputWires[0].connectTo(subCircuits[a1].inputWires, 0);
	inputWires[h3].connectTo(subCircuits[a1].inputWires, 1);

	subCircuits[A].outputWires[0].connectTo(subCircuits[a2].inputWires, 0);
	subCircuits[B].outputWires[0].connectTo(subCircuits[a2].inputWires, 1);

	subCircuits[B].outputWires[0].connectTo(subCircuits[a3].inputWires, 0);
	inputWires[l1].connectTo(subCircuits[a3].inputWires, 1);
	inputWires[h2].connectTo(subCircuits[a3].inputWires, 2);

	subCircuits[A].outputWires[0].connectTo(subCircuits[a4].inputWires, 0);
	subCircuits[B].outputWires[0].connectTo(subCircuits[a4].inputWires, 1);
	inputWires[l3].connectTo(subCircuits[a4].inputWires, 2);

	subCircuits[B].outputWires[0].connectTo(subCircuits[a5].inputWires, 0);
	inputWires[l2].connectTo(subCircuits[a5].inputWires, 1);

	subCircuits[A].outputWires[0].connectTo(subCircuits[a6].inputWires, 0);
	inputWires[l2].connectTo(subCircuits[a6].inputWires, 1);
	inputWires[l3].connectTo(subCircuits[a6].inputWires, 2);
	inputWires[h0].connectTo(subCircuits[a6].inputWires, 3);

	subCircuits[B].outputWires[0].connectTo(subCircuits[a7].inputWires, 0);
	inputWires[l2].connectTo(subCircuits[a7].inputWires, 1);
	inputWires[h3].connectTo(subCircuits[a7].inputWires, 2);
    }

    protected void defineOutputWires() {
	outputWires[0] = subCircuits[a0].outputWires[0];
	outputWires[1] = subCircuits[a1].outputWires[0];
	outputWires[2] = subCircuits[a2].outputWires[0];
	outputWires[3] = subCircuits[a3].outputWires[0];
	outputWires[4] = subCircuits[a4].outputWires[0];
	outputWires[5] = subCircuits[a5].outputWires[0];
	outputWires[6] = subCircuits[a6].outputWires[0];
	outputWires[7] = subCircuits[a7].outputWires[0];
    }
}