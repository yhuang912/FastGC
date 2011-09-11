// Copyright (C) 2010 by Yan Huang <yhuang@virginia.edu>

package YaoGC.AESComponents;

import YaoGC.*;

class AffineTransform extends CompositeCircuit {
    private static final byte A  = 0;
    private static final byte B  = 1;
    private static final byte C  = 2;
    private static final byte D  = 3;
    private static final byte q0 = 4;
    private static final byte q1  = 5;
    private static final byte q2  = 6;
    private static final byte q3  = 7;
    private static final byte q4  = 8;
    private static final byte q5  = 9;
    private static final byte q6  = 10;
    private static final byte q7 = 11;
    private static final byte a0bar = 12;
    private static final byte a1bar = 13;
    private static final byte a5bar = 14;
    private static final byte a6bar = 15;

    public AffineTransform() {
	super(8, 8, 16, "AffineTransform");
    }

    protected void createSubCircuits() throws Exception {
	subCircuits[A]  = new XOR_2_1();
	subCircuits[B]  = new XOR_2_1();
	subCircuits[C]  = new XOR_2_1();
	subCircuits[D]  = new XOR_2_1();
	subCircuits[q0] = new XOR_L_1(3);
	subCircuits[q1] = new XOR_L_1(3);
	subCircuits[q2] = new XOR_L_1(3);
	subCircuits[q3] = new XOR_L_1(3);
	subCircuits[q4] = new XOR_L_1(3);
	subCircuits[q5] = new XOR_L_1(3);
	subCircuits[q6] = new XOR_L_1(3);
	subCircuits[q7] = new XOR_L_1(3);

	subCircuits[a0bar]  = new XOR_2_1();
	subCircuits[a1bar]  = new XOR_2_1();
	subCircuits[a5bar]  = new XOR_2_1();
	subCircuits[a6bar]  = new XOR_2_1();

	super.createSubCircuits();
    }

    protected void connectWires() {
	inputWires[0].connectTo(subCircuits[A].inputWires, 0);
	inputWires[1].connectTo(subCircuits[A].inputWires, 1);

	inputWires[2].connectTo(subCircuits[B].inputWires, 0);
	inputWires[3].connectTo(subCircuits[B].inputWires, 1);

	inputWires[4].connectTo(subCircuits[C].inputWires, 0);
	inputWires[5].connectTo(subCircuits[C].inputWires, 1);

	inputWires[6].connectTo(subCircuits[D].inputWires, 0);
	inputWires[7].connectTo(subCircuits[D].inputWires, 1);

	inputWires[0].connectTo(subCircuits[a0bar].inputWires, 0);
	subCircuits[a0bar].inputWires[1].fixWire(1);

	inputWires[1].connectTo(subCircuits[a1bar].inputWires, 0);
	subCircuits[a1bar].inputWires[1].fixWire(1);

	inputWires[5].connectTo(subCircuits[a5bar].inputWires, 0);
	subCircuits[a5bar].inputWires[1].fixWire(1);

	inputWires[6].connectTo(subCircuits[a6bar].inputWires, 0);
	subCircuits[a6bar].inputWires[1].fixWire(1);

	subCircuits[a0bar].outputWires[0].connectTo(subCircuits[q0].inputWires, 0);
	subCircuits[C].outputWires[0].connectTo(subCircuits[q0].inputWires, 1);
	subCircuits[D].outputWires[0].connectTo(subCircuits[q0].inputWires, 2);

	subCircuits[a5bar].outputWires[0].connectTo(subCircuits[q1].inputWires, 0);
	subCircuits[A].outputWires[0].connectTo(subCircuits[q1].inputWires, 1);
	subCircuits[D].outputWires[0].connectTo(subCircuits[q1].inputWires, 2);

	inputWires[2].connectTo(subCircuits[q2].inputWires, 0);
	subCircuits[A].outputWires[0].connectTo(subCircuits[q2].inputWires, 1);
	subCircuits[D].outputWires[0].connectTo(subCircuits[q2].inputWires, 2);

	inputWires[7].connectTo(subCircuits[q3].inputWires, 0);
	subCircuits[A].outputWires[0].connectTo(subCircuits[q3].inputWires, 1);
	subCircuits[B].outputWires[0].connectTo(subCircuits[q3].inputWires, 2);

	inputWires[4].connectTo(subCircuits[q4].inputWires, 0);
	subCircuits[A].outputWires[0].connectTo(subCircuits[q4].inputWires, 1);
	subCircuits[B].outputWires[0].connectTo(subCircuits[q4].inputWires, 2);

	subCircuits[a1bar].outputWires[0].connectTo(subCircuits[q5].inputWires, 0);
	subCircuits[B].outputWires[0].connectTo(subCircuits[q5].inputWires, 1);
	subCircuits[C].outputWires[0].connectTo(subCircuits[q5].inputWires, 2);

	subCircuits[a6bar].outputWires[0].connectTo(subCircuits[q6].inputWires, 0);
	subCircuits[B].outputWires[0].connectTo(subCircuits[q6].inputWires, 1);
	subCircuits[C].outputWires[0].connectTo(subCircuits[q6].inputWires, 2);

	inputWires[3].connectTo(subCircuits[q7].inputWires, 0);
	subCircuits[C].outputWires[0].connectTo(subCircuits[q7].inputWires, 1);
	subCircuits[D].outputWires[0].connectTo(subCircuits[q7].inputWires, 2);
    }

    protected void defineOutputWires() {
	outputWires[0] = subCircuits[q0].outputWires[0];
	outputWires[1] = subCircuits[q1].outputWires[0];
	outputWires[2] = subCircuits[q2].outputWires[0];
	outputWires[3] = subCircuits[q3].outputWires[0];
	outputWires[4] = subCircuits[q4].outputWires[0];
	outputWires[5] = subCircuits[q5].outputWires[0];
	outputWires[6] = subCircuits[q6].outputWires[0];
	outputWires[7] = subCircuits[q7].outputWires[0];
    }
}