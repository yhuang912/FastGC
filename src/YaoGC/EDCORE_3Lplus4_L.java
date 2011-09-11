// Copyright (C) 2010 by Yan Huang <yhuang@virginia.edu>

package YaoGC;

public class EDCORE_3Lplus4_L extends CompositeCircuit {
    private final int L;

    private final static int  MINA = 0;
    private final static int  MINB = 1;
    private final static int ADD1A = 2;
    private final static int ADD1B = 3;
    private final static int   EDT = 4;

    public EDCORE_3Lplus4_L(int l) {
	super(3*l+4, l, 5, "EDCORE_" + (3*l+4) + "_" + l);
	L = l;
    }

    protected void createSubCircuits() throws Exception {
	subCircuits[MINA]  = new MIN_2L_L(L);
	subCircuits[MINB]  = new MIN_2L_L(L+1);   // because ADD1 can have (L+1)-bit output
	subCircuits[ADD1A] = new ADD1_Lplus1_Lplus1(L);
	subCircuits[ADD1B] = new ADD1_Lplus1_Lplus1(L);
	subCircuits[EDT]   = new EDT_4_1();

	super.createSubCircuits();
    }

    protected void connectWires() {
	for (int i = 0; i < 4; i++)
	    inputWires[i].connectTo(subCircuits[EDT].inputWires, i);

	for (int i = 0; i < L; i++) {
	    inputWires[i+    4].connectTo(subCircuits[ADD1B].inputWires, i+1);
	    inputWires[i+  L+4].connectTo(subCircuits[MINA ].inputWires, i  );
	    inputWires[i+2*L+4].connectTo(subCircuits[MINA ].inputWires, i+L);
	}

	subCircuits[EDT].outputWires[0].connectTo(subCircuits[ADD1B].inputWires, 0);

	for (int i = 0; i < L; i++) 
	    subCircuits[MINA].outputWires[i].connectTo(subCircuits[ADD1A].inputWires, i+1);

	for (int i = 0; i < L+1; i++) {
	    subCircuits[ADD1B].outputWires[i].connectTo(subCircuits[MINB].inputWires, i    );
	    subCircuits[ADD1A].outputWires[i].connectTo(subCircuits[MINB].inputWires, i+L+1);
	}
    }

    protected void defineOutputWires() {
	System.arraycopy(subCircuits[MINB].outputWires, 0, outputWires, 0, L);
    }

    protected void fixInternalWires() {
    	Wire internalWire = subCircuits[ADD1A].inputWires[0];
    	internalWire.fixWire(1);
    }
}