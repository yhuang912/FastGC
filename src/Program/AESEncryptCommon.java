// Copyright (C) 2010 by Yan Huang <yhuang@virginia.edu>

package Program;

import YaoGC.*;
import YaoGC.AESComponents.*;
import LookupTable.*;
import Utils.*;

public class AESEncryptCommon extends ProgCommon {

    final static int Nb = 4;		// words in a block, always 4 for now
    static int Nk;			// key length in words
    static int Nr;			// number of rounds, = Nk + 6
    static short[] w;			// the expanded key

    protected static int[][] STable;
    static LookupTableAgent agent;

    private static AddRoundKey  ccARK;
    private static SBox		ccSBX;
    private static MixColumns	ccMXC;

    // AESencrypt: constructor for class. Mainly expands key
    public AESEncryptCommon(int NkIn) {
	Nk = NkIn;			// words in a key, = 4, or 6, or 8
	Nr = Nk + 6;			// corresponding number of rounds
	w = new short[4*Nb*(Nr+1)];	// room for expanded key
    }
    
    public void initServer(short[] key) {
	keyExpansion(key);		// length of w depends on Nr
	fillTable(key);
    }

    static void initCircuits() {
	ccs = new Circuit[3];
	ccs[0] = ccARK = new AddRoundKey();
	ccs[1] = ccSBX = new SBox();
	ccs[2] = ccMXC = new MixColumns();
    }

    private void fillTable(short[] key) {
	STable = new int[16][16];

	for (int i = 0; i < 16; i++)
	    for (int j = 0; j < 16; j++)
		STable[i][j] = SBox[i*16+j];
    }

    // KeyExpansion: expand key, byte-oriented code, but tracks words
    private void keyExpansion(short[] key) {
    	short[] temp = new short[4];

    	// first just copy key to w
    	int j = 0;
    	while (j < 4*Nk) {
    	    w[j] = key[j++];
    	}

    	// here j == 4*Nk;
    	int i;
    	while(j < 4*Nb*(Nr+1)) {
    	    i = j/4;		// j is always multiple of 4 here

    	    // handle everything word-at-a time, 4 bytes at a time
    	    for (int iTemp = 0; iTemp < 4; iTemp++)
    		temp[iTemp] = w[j-4+iTemp];
    	    if (i % Nk == 0) {
    		short ttemp, tRcon;
    		short oldtemp0 = temp[0];
    		for (int iTemp = 0; iTemp < 4; iTemp++) {
    		    if (iTemp == 3) ttemp = oldtemp0;
    		    else ttemp = temp[iTemp+1];
    		    if (iTemp == 0) tRcon = Rcon[i/Nk-1];
    		    else tRcon = 0;
    		    temp[iTemp] = (short)(SBox[ttemp & 0xff] ^ tRcon);
    		}
    	    }
    	    else if (Nk > 6 && (i%Nk) == 4) {
    		for (int iTemp = 0; iTemp < 4; iTemp++)
    		    temp[iTemp] = SBox[temp[iTemp] & 0xff];
    	    }
    	    for (int iTemp = 0; iTemp < 4; iTemp++)
    		w[j+iTemp] = (short)(w[j - 4*Nk + iTemp] ^ temp[iTemp]);
    	    j = j + 4;
    	}
    }

    // Cipher: actual AES encrytion
    public static State Cipher(State key, State msg) {
	State[] arrS;

	State state = AddRoundKey(key, msg, 0);	
	for (int round = 1; round < Nr; round++) {
	    arrS = SubBytes(state);
	    arrS = ShiftRows(arrS);
	    state = MixColumns(arrS);		
	    state = AddRoundKey(key, state, round);
	}

	arrS = SubBytes(state);
	arrS = ShiftRows(arrS);
	state = AddRoundKey(key, arrS, Nr);

	StopWatch.taskTimeStamp("circuit execution");

	return state;
    }

    private static State[] SubBytes(State state) {
    	State[] res = new State[16];
    	for (int i = 0; i < 16; i++) {
    	    res[i] = ccSBX.startExecuting(State.extractState(state, i*8, i*8+8));
    	}

    	return res;
    }

    // ShiftRows: simple circular shift of rows 1, 2, 3 by 1, 2, 3
    private static State[] ShiftRows(State[] state) {
    	State[] res = new State[16];
	short[] c = new short[] {0,5,10,15,4,9,14,3,8,13,2,7,12,1,6,11};
	for ( int i=0; i<16; i++) {
	    res[i] = state[c[i]];
	}

	return res;
    }

    // MixColumns: complex and sophisticated mixing of columns
    private static State MixColumns(State[] s) {
	return ccMXC.startExecuting(s);
    }

    // AddRoundKey: xor a portion of expanded key with state
    private static State AddRoundKey(State key, State state, int round) {
	return ccARK.startExecuting(key, round*128, state);
    }

    private static State AddRoundKey(State key, State[] arrS, int round) {
	return ccARK.startExecuting(key, round*128, State.flattenStateArray(arrS));
    }

    static int testBit(short[] w, int n) {
	int i = n / 8;
	int j = n % 8;

	int res = ((w[i] & (1<<j)) == 0) ? 0 : 1;
	return res;
    }

    private static short[] SBox = {		 	// SubBytes table
	0x63, 0x7c, 0x77, 0x7b, 0xf2, 0x6b, 0x6f, 0xc5, 0x30, 0x01, 0x67, 0x2b, 0xfe, 0xd7, 0xab, 0x76,
	0xca, 0x82, 0xc9, 0x7d, 0xfa, 0x59, 0x47, 0xf0, 0xad, 0xd4, 0xa2, 0xaf, 0x9c, 0xa4, 0x72, 0xc0,
	0xb7, 0xfd, 0x93, 0x26, 0x36, 0x3f, 0xf7, 0xcc, 0x34, 0xa5, 0xe5, 0xf1, 0x71, 0xd8, 0x31, 0x15,
	0x04, 0xc7, 0x23, 0xc3, 0x18, 0x96, 0x05, 0x9a, 0x07, 0x12, 0x80, 0xe2, 0xeb, 0x27, 0xb2, 0x75,
	0x09, 0x83, 0x2c, 0x1a, 0x1b, 0x6e, 0x5a, 0xa0, 0x52, 0x3b, 0xd6, 0xb3, 0x29, 0xe3, 0x2f, 0x84,
	0x53, 0xd1, 0x00, 0xed, 0x20, 0xfc, 0xb1, 0x5b, 0x6a, 0xcb, 0xbe, 0x39, 0x4a, 0x4c, 0x58, 0xcf,
	0xd0, 0xef, 0xaa, 0xfb, 0x43, 0x4d, 0x33, 0x85, 0x45, 0xf9, 0x02, 0x7f, 0x50, 0x3c, 0x9f, 0xa8,
	0x51, 0xa3, 0x40, 0x8f, 0x92, 0x9d, 0x38, 0xf5, 0xbc, 0xb6, 0xda, 0x21, 0x10, 0xff, 0xf3, 0xd2,
	0xcd, 0x0c, 0x13, 0xec, 0x5f, 0x97, 0x44, 0x17, 0xc4, 0xa7, 0x7e, 0x3d, 0x64, 0x5d, 0x19, 0x73,
	0x60, 0x81, 0x4f, 0xdc, 0x22, 0x2a, 0x90, 0x88, 0x46, 0xee, 0xb8, 0x14, 0xde, 0x5e, 0x0b, 0xdb,
	0xe0, 0x32, 0x3a, 0x0a, 0x49, 0x06, 0x24, 0x5c, 0xc2, 0xd3, 0xac, 0x62, 0x91, 0x95, 0xe4, 0x79,
	0xe7, 0xc8, 0x37, 0x6d, 0x8d, 0xd5, 0x4e, 0xa9, 0x6c, 0x56, 0xf4, 0xea, 0x65, 0x7a, 0xae, 0x08,
	0xba, 0x78, 0x25, 0x2e, 0x1c, 0xa6, 0xb4, 0xc6, 0xe8, 0xdd, 0x74, 0x1f, 0x4b, 0xbd, 0x8b, 0x8a,
	0x70, 0x3e, 0xb5, 0x66, 0x48, 0x03, 0xf6, 0x0e, 0x61, 0x35, 0x57, 0xb9, 0x86, 0xc1, 0x1d, 0x9e,
	0xe1, 0xf8, 0x98, 0x11, 0x69, 0xd9, 0x8e, 0x94, 0x9b, 0x1e, 0x87, 0xe9, 0xce, 0x55, 0x28, 0xdf,
	0x8c, 0xa1, 0x89, 0x0d, 0xbf, 0xe6, 0x42, 0x68, 0x41, 0x99, 0x2d, 0x0f, 0xb0, 0x54, 0xbb, 0x16};

    private static short[] Rcon = {1,2,4,8,16,32,64,128,27,54,108,216,171,77,154,47,94,188,99,198,151,53,106,212,179,125,250,239,197,145};
}
