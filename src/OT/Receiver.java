// Copyright (C) 2010 by Yan Huang <yhuang@virginia.edu>

package OT;

import java.math.*;
import java.io.*;

public abstract class Receiver {
    protected BigInteger choices;
    protected int numOfChoices;
    protected ObjectInputStream ois;
    protected ObjectOutputStream oos;

    protected BigInteger[] data = null;

    public Receiver(int numOfChoices, ObjectInputStream in, ObjectOutputStream out) {
	this.numOfChoices = numOfChoices;
	ois = in;
	oos = out;
    }

    public void execProtocol(BigInteger choices) throws Exception {
	this.choices = choices;
    }

    public BigInteger[] getData() {
	return data;
    }
}
