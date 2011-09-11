// Copyright (C) 2010 by Yan Huang <yhuang@virginia.edu>

package Program;

public abstract class Program {
    public static int iterCount;

    public void run() throws Exception {
	init();

	for (int i = 0; i < iterCount; i++) {
	    execute();
	    verify_result();
	}
    }

    protected void init() throws Exception {
	createCircuits();

	initializeOT();
    }

    abstract protected void createCircuits() throws Exception;

    abstract protected void initializeOT() throws Exception;

    protected void execute() throws Exception {
	execTransfer();

	execCircuit();

	interpretResult();
    }

    abstract protected void execTransfer() throws Exception;

    abstract protected void execCircuit() throws Exception;

    abstract protected void interpretResult() throws Exception;

    abstract protected void verify_result() throws Exception;
}