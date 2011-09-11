// Copyright (C) 2010 by Yan Huang <yhuang@virginia.edu>

package YaoGC;

public class OptEDCORE_3Lplus2S_L extends CompositeCircuit {
    private final int L;
    private final int S;

    private final static int  MINA = 0;
    private final static int  MINB = 1;
    private final static int  ADD1 = 2;
    private final static int   MUX = 3;
    private final static int   EDT = 4;

    public OptEDCORE_3Lplus2S_L(int l, int s) {
	super(3*l+2*s, l, 5, "OptEDCORE_" + (3*l+2*s) + "_" + l);
	L = l;
	S = s;
    }

    protected void createSubCircuits() throws Exception {
	subCircuits[MINA]  = new MIN_2L_L(L);
	subCircuits[MINB]  = new MIN_2L_L(L);
	subCircuits[ADD1]  = new ADD1_Lplus1_L(L);
	subCircuits[MUX]   = new MUX_3_1();
	subCircuits[EDT]   = new EDT_2L_1(S);

	super.createSubCircuits();
    }

    protected void connectWires() {
	for (int i = 0; i < 2*S; i++)
	    inputWires[i].connectTo(subCircuits[EDT].inputWires, i);

	for (int i = 0; i < L; i++) {
	    inputWires[i+    2*S].connectTo(subCircuits[MINB].inputWires, i+L);
	    inputWires[i+  L+2*S].connectTo(subCircuits[MINA].inputWires, i  );
	    inputWires[i+2*L+2*S].connectTo(subCircuits[MINA].inputWires, i+L);
	}

	subCircuits[EDT].outputWires[0].connectTo(subCircuits[MUX].inputWires, MUX_3_1.X);

	for (int i = 0; i < L; i++) 
	    subCircuits[MINA].outputWires[i].connectTo(subCircuits[MINB].inputWires, i);

	((CompositeCircuit) subCircuits[MINB]).subCircuits[MIN_2L_L.GT].outputWires[0].connectTo(subCircuits[MUX].inputWires, MUX_3_1.C);

	subCircuits[MUX].outputWires[0].connectTo(subCircuits[ADD1].inputWires, 0);
	for (int i = 0; i < L; i++) {
	    subCircuits[MINB].outputWires[i].connectTo(subCircuits[ADD1].inputWires, i+1);
	}
    }

    protected void defineOutputWires() {
	System.arraycopy(subCircuits[ADD1].outputWires, 0, outputWires, 0, L);
    }

    protected void fixInternalWires() {
    	Wire internalWire = subCircuits[MUX].inputWires[MUX_3_1.Y];
    	internalWire.fixWire(1);
    }
}
