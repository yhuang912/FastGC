// Copyright (C) 2010 by Yan Huang <yhuang@virginia.edu>

package YaoGC;

public abstract class CompositeCircuit extends Circuit {
    protected Circuit[] subCircuits;
    protected int nSubCircuits;

    public CompositeCircuit(int inDegree, int outDegree, int nSubCircuits, String name) {
	super(inDegree, outDegree, name);

	this.nSubCircuits = nSubCircuits;

	subCircuits = new Circuit[nSubCircuits];
    }

    public void build() throws Exception {
	createInputWires();
	createSubCircuits();
	connectWires();
	defineOutputWires();
	fixInternalWires();
    }

    protected void createSubCircuits() throws Exception {
	for (int i = 0; i < nSubCircuits; i++)
	    subCircuits[i].build();
    }

    abstract protected void connectWires() throws Exception;
    abstract protected void defineOutputWires();
    protected void fixInternalWires() {}

    protected void compute() {}
    protected void execute() {}
}
