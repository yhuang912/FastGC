// Copyright (C) 2010 by Yan Huang <yhuang@virginia.edu>

package YaoGC.AESComponents;

import YaoGC.*;

class Mult_GF16 extends CompositeCircuit {
    private static final byte A  = 0;
    private static final byte B  = 1;
    private static final byte C  = 2;
    private static final byte q0 = 3;
    private static final byte q1 = 4;
    private static final byte q2 = 5;
    private static final byte q3 = 6;

    private static final byte and00 = 7;
    private static final byte and31 = 8;
    private static final byte and22 = 9;
    private static final byte and13 = 10;
    private static final byte and10 = 11;
    private static final byte andA1 = 12;
    private static final byte andB2 = 13;
    private static final byte andC3 = 14;
    private static final byte and20 = 15;
    private static final byte and11 = 16;
    private static final byte andA2 = 17;
    private static final byte andB3 = 18;
    private static final byte and30 = 19;
    private static final byte and21 = 20;
    private static final byte and12 = 21;
    private static final byte andA3 = 22;

    private static final byte b0 = 0;
    private static final byte b1 = 1;
    private static final byte b2 = 2;
    private static final byte b3 = 3;
    private static final byte a0 = 4;
    private static final byte a1 = 5;
    private static final byte a2 = 6;
    private static final byte a3 = 7;

    public Mult_GF16() {
	super(8, 4, 23, "Mult_GF16");
    }

    protected void createSubCircuits() throws Exception {
	subCircuits[A]  = new XOR_2_1();
	subCircuits[B]  = new XOR_2_1();
	subCircuits[C]  = new XOR_2_1();
	subCircuits[q0] = new XOR_L_1(4);
	subCircuits[q1] = new XOR_L_1(4);
	subCircuits[q2] = new XOR_L_1(4);
	subCircuits[q3] = new XOR_L_1(4);

	subCircuits[and00] = AND_2_1.newInstance();
	subCircuits[and31] = AND_2_1.newInstance();
	subCircuits[and22] = AND_2_1.newInstance();
	subCircuits[and13] = AND_2_1.newInstance();
	subCircuits[and10] = AND_2_1.newInstance();
	subCircuits[andA1] = AND_2_1.newInstance();
	subCircuits[andB2] = AND_2_1.newInstance();
	subCircuits[andC3] = AND_2_1.newInstance();
	subCircuits[and20] = AND_2_1.newInstance();
	subCircuits[and11] = AND_2_1.newInstance();
	subCircuits[andA2] = AND_2_1.newInstance();
	subCircuits[andB3] = AND_2_1.newInstance();
	subCircuits[and30] = AND_2_1.newInstance();
	subCircuits[and21] = AND_2_1.newInstance();
	subCircuits[and12] = AND_2_1.newInstance();
	subCircuits[andA3] = AND_2_1.newInstance();

	super.createSubCircuits();
    }

    protected void connectWires() {
	inputWires[a0].connectTo(subCircuits[A].inputWires, 0);
	inputWires[a3].connectTo(subCircuits[A].inputWires, 1);

	inputWires[a2].connectTo(subCircuits[B].inputWires, 0);
	inputWires[a3].connectTo(subCircuits[B].inputWires, 1);

	inputWires[a1].connectTo(subCircuits[C].inputWires, 0);
	inputWires[a2].connectTo(subCircuits[C].inputWires, 1);

	inputWires[a0].connectTo(subCircuits[and00].inputWires, 0);
	inputWires[b0].connectTo(subCircuits[and00].inputWires, 1);
	
	inputWires[a3].connectTo(subCircuits[and31].inputWires, 0);
	inputWires[b1].connectTo(subCircuits[and31].inputWires, 1);

	inputWires[a2].connectTo(subCircuits[and22].inputWires, 0);
	inputWires[b2].connectTo(subCircuits[and22].inputWires, 1);

	inputWires[a1].connectTo(subCircuits[and13].inputWires, 0);
	inputWires[b3].connectTo(subCircuits[and13].inputWires, 1);

	subCircuits[and00].outputWires[0].connectTo(subCircuits[q0].inputWires, 0);
	subCircuits[and31].outputWires[0].connectTo(subCircuits[q0].inputWires, 1);
	subCircuits[and22].outputWires[0].connectTo(subCircuits[q0].inputWires, 2);
	subCircuits[and13].outputWires[0].connectTo(subCircuits[q0].inputWires, 3);

	inputWires[a1].connectTo(subCircuits[and10].inputWires, 0);
	inputWires[b0].connectTo(subCircuits[and10].inputWires, 1);

	subCircuits[A].outputWires[0].connectTo(subCircuits[andA1].inputWires, 0);
	inputWires[b1].connectTo(subCircuits[andA1].inputWires, 1);

	subCircuits[B].outputWires[0].connectTo(subCircuits[andB2].inputWires, 0);
	inputWires[b2].connectTo(subCircuits[andB2].inputWires, 1);

	subCircuits[C].outputWires[0].connectTo(subCircuits[andC3].inputWires, 0);
	inputWires[b3].connectTo(subCircuits[andC3].inputWires, 1);

	subCircuits[and10].outputWires[0].connectTo(subCircuits[q1].inputWires, 0);
	subCircuits[andA1].outputWires[0].connectTo(subCircuits[q1].inputWires, 1);
	subCircuits[andB2].outputWires[0].connectTo(subCircuits[q1].inputWires, 2);
	subCircuits[andC3].outputWires[0].connectTo(subCircuits[q1].inputWires, 3);

	inputWires[a2].connectTo(subCircuits[and20].inputWires, 0);
	inputWires[b0].connectTo(subCircuits[and20].inputWires, 1);

	inputWires[a1].connectTo(subCircuits[and11].inputWires, 0);
	inputWires[b1].connectTo(subCircuits[and11].inputWires, 1);

	subCircuits[A].outputWires[0].connectTo(subCircuits[andA2].inputWires, 0);
	inputWires[b2].connectTo(subCircuits[andA2].inputWires, 1);

	subCircuits[B].outputWires[0].connectTo(subCircuits[andB3].inputWires, 0);
	inputWires[b3].connectTo(subCircuits[andB3].inputWires, 1);

	subCircuits[and20].outputWires[0].connectTo(subCircuits[q2].inputWires, 0);
	subCircuits[and11].outputWires[0].connectTo(subCircuits[q2].inputWires, 1);
	subCircuits[andA2].outputWires[0].connectTo(subCircuits[q2].inputWires, 2);
	subCircuits[andB3].outputWires[0].connectTo(subCircuits[q2].inputWires, 3);

	inputWires[a3].connectTo(subCircuits[and30].inputWires, 0);
	inputWires[b0].connectTo(subCircuits[and30].inputWires, 1);

	inputWires[a2].connectTo(subCircuits[and21].inputWires, 0);
	inputWires[b1].connectTo(subCircuits[and21].inputWires, 1);

	inputWires[a1].connectTo(subCircuits[and12].inputWires, 0);
	inputWires[b2].connectTo(subCircuits[and12].inputWires, 1);

	subCircuits[A].outputWires[0].connectTo(subCircuits[andA3].inputWires, 0);
	inputWires[b3].connectTo(subCircuits[andA3].inputWires, 1);

	subCircuits[and30].outputWires[0].connectTo(subCircuits[q3].inputWires, 0);
	subCircuits[and21].outputWires[0].connectTo(subCircuits[q3].inputWires, 1);
	subCircuits[and12].outputWires[0].connectTo(subCircuits[q3].inputWires, 2);
	subCircuits[andA3].outputWires[0].connectTo(subCircuits[q3].inputWires, 3);
    }

    protected void defineOutputWires() {
	outputWires[0] = subCircuits[q0].outputWires[0];
	outputWires[1] = subCircuits[q1].outputWires[0];
	outputWires[2] = subCircuits[q2].outputWires[0];
	outputWires[3] = subCircuits[q3].outputWires[0];
    }
}