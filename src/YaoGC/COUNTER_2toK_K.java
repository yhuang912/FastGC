// Copyright (C) 2010 by Yan Huang <yhuang@virginia.edu>

package YaoGC;

public class COUNTER_2toK_K extends CompositeCircuit {

    public COUNTER_2toK_K(int k) {
	super(1<<(k-1), k, (1<<(k-1))-1, "COUNTER_" + (1<<(k-1)) + "_" + k);
    }

    protected void createSubCircuits() throws Exception {
	int i = 0;
	for (int level = 0; level < outDegree-1; level++){
	    int l = outDegree - level - 1;
	    for (int x = 0; x < (1<<level); x++) {
		subCircuits[i] = new ADD_2L_Lplus1(l);
		i++;
	    }
	}

	super.createSubCircuits();
    }

    protected void connectWires() throws Exception {
	int i = 0, j = 0;
	for (int level = 0; level < outDegree-1; level++) {
	    if (level == outDegree-2) {
		for (int x = 0; x < (1<<level); x++) {
		    ((ADD_2L_Lplus1) subCircuits[i]).
		        connectWiresToXY(inputWires, j, inputWires, j+1);
		    i++; j += 2;
		}
	    }
	    else {
		for (int x = 0; x < (1<<level); x++) {
		    ((ADD_2L_Lplus1) subCircuits[i]).
			connectWiresToXY(subCircuits[2*i+1].outputWires, 0, 
					 subCircuits[2*i+2].outputWires, 0);
		    i++;
		}
	    }
	}
    }

    protected void defineOutputWires() {
	System.arraycopy(subCircuits[0].outputWires, 0, outputWires, 0, outDegree);
    }
}