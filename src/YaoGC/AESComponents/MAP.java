// Copyright (C) 2010 by Yan Huang <yhuang@virginia.edu>

package YaoGC.AESComponents;

import YaoGC.*;

class MAP extends CompositeCircuit {
    private static final byte A  = 0;
    private static final byte B  = 1;
    private static final byte C  = 2;
    private static final byte L0 = 3;
    private static final byte L1 = 4;
    private static final byte L3 = 5;
    private static final byte H0 = 6;
    private static final byte H1 = 7;
    private static final byte H2 = 8;

    public MAP() {
	super(8, 8, 9, "MAP");
    }

    protected void createSubCircuits() throws Exception {
	subCircuits[A]  = new XOR_2_1();
	subCircuits[B]  = new XOR_2_1();
	subCircuits[C]  = new XOR_2_1();
	subCircuits[L0] = new XOR_L_1(3);
	subCircuits[L1] = new XOR_2_1();
	subCircuits[L3] = new XOR_2_1();
	subCircuits[H0] = new XOR_2_1();
	subCircuits[H1] = new XOR_2_1();
	subCircuits[H2] = new XOR_L_1(3);

	super.createSubCircuits();
    }

    protected void connectWires() {
	inputWires[1].connectTo(subCircuits[A].inputWires, 0);
	inputWires[7].connectTo(subCircuits[A].inputWires, 1);

	inputWires[5].connectTo(subCircuits[B].inputWires, 0);
	inputWires[7].connectTo(subCircuits[B].inputWires, 1);

	inputWires[4].connectTo(subCircuits[C].inputWires, 0);
	inputWires[6].connectTo(subCircuits[C].inputWires, 1);

	subCircuits[C].outputWires[0].connectTo(subCircuits[L0].inputWires, 0);
	inputWires[0].connectTo(subCircuits[L0].inputWires, 1);
	inputWires[5].connectTo(subCircuits[L0].inputWires, 2);

	inputWires[1].connectTo(subCircuits[L1].inputWires, 0);
	inputWires[2].connectTo(subCircuits[L1].inputWires, 1);

	inputWires[2].connectTo(subCircuits[L3].inputWires, 0);
	inputWires[4].connectTo(subCircuits[L3].inputWires, 1);

	subCircuits[C].outputWires[0].connectTo(subCircuits[H0].inputWires, 0);
	inputWires[5].connectTo(subCircuits[H0].inputWires, 1);

	subCircuits[A].outputWires[0].connectTo(subCircuits[H1].inputWires, 0);
	subCircuits[C].outputWires[0].connectTo(subCircuits[H1].inputWires, 1);

	subCircuits[B].outputWires[0].connectTo(subCircuits[H2].inputWires, 0);
	inputWires[2].connectTo(subCircuits[H2].inputWires, 1);
	inputWires[3].connectTo(subCircuits[H2].inputWires, 2);
    }

    protected void defineOutputWires() {
	outputWires[0] = subCircuits[L0].outputWires[0];
	outputWires[1] = subCircuits[L1].outputWires[0];
	outputWires[2] = subCircuits[A ].outputWires[0];
	outputWires[3] = subCircuits[L3].outputWires[0];
	outputWires[4] = subCircuits[H0].outputWires[0];
	outputWires[5] = subCircuits[H1].outputWires[0];
	outputWires[6] = subCircuits[H2].outputWires[0];
	outputWires[7] = subCircuits[B ].outputWires[0];
    }
}