/************************************************************************
 *
 *  getvlc.c, variable length code tables for tmndecode (H.263 decoder)
 *  Copyright (C) 1995, 1996  Telenor R&D, Norway
 *        Karl Olav Lillevold <Karl.Lillevold@nta.no>
 *  
 *  Contacts: 
 *  Karl Olav Lillevold               <Karl.Lillevold@nta.no>, or
 *  Robert Danielsen                  <Robert.Danielsen@nta.no>
 *
 *  Telenor Research and Development  http://www.nta.no/brukere/DVC/
 *  P.O.Box 83                        tel.:   +47 63 84 84 00
 *  N-2007 Kjeller, Norway            fax.:   +47 63 81 00 76
 *  
 ************************************************************************/

/*
 * Disclaimer of Warranty
 *
 * These software programs are available to the user without any
 * license fee or royalty on an "as is" basis.  Telenor Research and
 * Development disclaims any and all warranties, whether express,
 * implied, or statuary, including any implied warranties or
 * merchantability or of fitness for a particular purpose.  In no
 * event shall the copyright-holder be liable for any incidental,
 * punitive, or consequential damages of any kind whatsoever arising
 * from the use of these programs.
 *
 * This disclaimer of warranty extends to the user of these programs
 * and user's customers, employees, agents, transferees, successors,
 * and assigns.
 *
 * Telenor Research and Development does not represent or warrant that
 * the programs furnished hereunder are free of infringement of any
 * third-party patents.
 *
 * Commercial implementations of H.263, including shareware, are
 * subject to royalty fees to patent holders.  Many of these patents
 * are general enough such that they are unavoidable regardless of
 * implementation design.
 * */


/*
 * based on mpeg2decode, (C) 1994, MPEG Software Simulation Group
 * and mpeg2play, (C) 1994 Stefan Eckart
 *                         <stefan@lis.e-technik.tu-muenchen.de>
 *
 */


typedef struct {
  int val, len;
} VLCtab;

typedef struct {
  char run, level, len;
} DCTtab;


static VLCtab TMNMVtab0[] = {
{3,4}, {61,4}, {2,3}, {2,3}, {62,3}, {62,3}, 
{1,2}, {1,2}, {1,2}, {1,2}, {63,2}, {63,2}, {63,2}, {63,2}
};

static VLCtab TMNMVtab1[] = {
{12,10}, {52,10}, {11,10}, {53,10}, {10,9}, {10,9}, 
{54,9}, {54,9}, {9,9}, {9,9}, {55,9}, {55,9}, 
{8,9}, {8,9}, {56,9}, {56,9}, {7,7}, {7,7}, 
{7,7}, {7,7}, {7,7}, {7,7}, {7,7}, {7,7}, 
{57,7}, {57,7}, {57,7}, {57,7}, {57,7}, {57,7}, 
{57,7}, {57,7}, {6,7}, {6,7}, {6,7}, {6,7}, 
{6,7}, {6,7}, {6,7}, {6,7}, {58,7}, {58,7}, 
{58,7}, {58,7}, {58,7}, {58,7}, {58,7}, {58,7}, 
{5,7}, {5,7}, {5,7}, {5,7}, {5,7}, {5,7}, 
{5,7}, {5,7}, {59,7}, {59,7}, {59,7}, {59,7}, 
{59,7}, {59,7}, {59,7}, {59,7}, {4,6}, {4,6}, 
{4,6}, {4,6}, {4,6}, {4,6}, {4,6}, {4,6}, 
{4,6}, {4,6}, {4,6}, {4,6}, {4,6}, {4,6}, 
{4,6}, {4,6}, {60,6}, {60,6},{60,6},{60,6},
{60,6},{60,6},{60,6},{60,6},{60,6},{60,6},
{60,6},{60,6},{60,6},{60,6},{60,6},{60,6}
};

static VLCtab TMNMVtab2[] = {
{32,12}, {31,12}, {33,12}, {30,11}, {30,11}, {34,11}, 
{34,11}, {29,11}, {29,11}, {35,11}, {35,11}, {28,11}, 
{28,11}, {36,11}, {36,11}, {27,11}, {27,11}, {37,11}, 
{37,11}, {26,11}, {26,11}, {38,11}, {38,11}, {25,11}, 
{25,11}, {39,11}, {39,11}, {24,10}, {24,10}, {24,10}, 
{24,10}, {40,10}, {40,10}, {40,10}, {40,10}, {23,10}, 
{23,10}, {23,10}, {23,10}, {41,10}, {41,10}, {41,10}, 
{41,10}, {22,10}, {22,10}, {22,10}, {22,10}, {42,10}, 
{42,10}, {42,10}, {42,10}, {21,10}, {21,10}, {21,10}, 
{21,10}, {43,10}, {43,10}, {43,10}, {43,10}, {20,10}, 
{20,10}, {20,10}, {20,10}, {44,10}, {44,10}, {44,10}, 
{44,10}, {19,10}, {19,10}, {19,10}, {19,10}, {45,10}, 
{45,10}, {45,10}, {45,10}, {18,10}, {18,10}, {18,10}, 
{18,10}, {46,10}, {46,10}, {46,10}, {46,10}, {17,10}, 
{17,10}, {17,10}, {17,10}, {47,10}, {47,10}, {47,10}, 
{47,10}, {16,10}, {16,10}, {16,10}, {16,10}, {48,10}, 
{48,10}, {48,10}, {48,10}, {15,10}, {15,10}, {15,10}, 
{15,10}, {49,10}, {49,10}, {49,10}, {49,10}, {14,10}, 
{14,10}, {14,10}, {14,10}, {50,10}, {50,10}, {50,10}, 
{50,10}, {13,10}, {13,10}, {13,10}, {13,10}, {51,10}, 
{51,10}, {51,10}, {51,10}
};


static VLCtab MCBPCtab[] = {
{-1,0},
{255,9}, {52,9}, {36,9}, {20,9}, {49,9}, {35,8}, {35,8}, {19,8}, {19,8}, 
{50,8}, {50,8}, {51,7}, {51,7}, {51,7}, {51,7}, {34,7}, {34,7}, {34,7}, 
{34,7}, {18,7}, {18,7}, {18,7}, {18,7}, {33,7}, {33,7}, {33,7}, {33,7}, 
{17,7}, {17,7}, {17,7}, {17,7}, {4,6}, {4,6}, {4,6}, {4,6}, {4,6}, 
{4,6}, {4,6}, {4,6}, {48,6}, {48,6}, {48,6}, {48,6}, {48,6}, {48,6}, 
{48,6}, {48,6}, {3,5}, {3,5}, {3,5}, {3,5}, {3,5}, {3,5}, {3,5}, 
{3,5}, {3,5}, {3,5}, {3,5}, {3,5}, {3,5}, {3,5}, {3,5}, {3,5}, 
{32,4}, {32,4}, {32,4}, {32,4}, {32,4}, {32,4}, {32,4}, {32,4}, {32,4}, 
{32,4}, {32,4}, {32,4}, {32,4}, {32,4}, {32,4}, {32,4}, {32,4}, {32,4}, 
{32,4}, {32,4}, {32,4}, {32,4}, {32,4}, {32,4}, {32,4}, {32,4}, {32,4}, 
{32,4}, {32,4}, {32,4}, {32,4}, {32,4}, {16,4}, {16,4}, {16,4}, {16,4}, 
{16,4}, {16,4}, {16,4}, {16,4}, {16,4}, {16,4}, {16,4}, {16,4}, {16,4}, 
{16,4}, {16,4}, {16,4}, {16,4}, {16,4}, {16,4}, {16,4}, {16,4}, {16,4}, 
{16,4}, {16,4}, {16,4}, {16,4}, {16,4}, {16,4}, {16,4}, {16,4}, {16,4}, 
{16,4}, {2,3}, {2,3}, {2,3}, {2,3}, {2,3}, {2,3}, {2,3}, {2,3}, 
{2,3}, {2,3}, {2,3}, {2,3}, {2,3}, {2,3}, {2,3}, {2,3}, {2,3}, 
{2,3}, {2,3}, {2,3}, {2,3}, {2,3}, {2,3}, {2,3}, {2,3}, {2,3}, 
{2,3}, {2,3}, {2,3}, {2,3}, {2,3}, {2,3}, {2,3}, {2,3}, {2,3}, 
{2,3}, {2,3}, {2,3}, {2,3}, {2,3}, {2,3}, {2,3}, {2,3}, {2,3}, 
{2,3}, {2,3}, {2,3}, {2,3}, {2,3}, {2,3}, {2,3}, {2,3}, {2,3}, 
{2,3}, {2,3}, {2,3}, {2,3}, {2,3}, {2,3}, {2,3}, {2,3}, {2,3}, 
{2,3}, {2,3}, {1,3}, {1,3}, {1,3}, {1,3}, {1,3}, {1,3}, {1,3}, 
{1,3}, {1,3}, {1,3}, {1,3}, {1,3}, {1,3}, {1,3}, {1,3}, {1,3}, 
{1,3}, {1,3}, {1,3}, {1,3}, {1,3}, {1,3}, {1,3}, {1,3}, {1,3}, 
{1,3}, {1,3}, {1,3}, {1,3}, {1,3}, {1,3}, {1,3}, {1,3}, {1,3}, 
{1,3}, {1,3}, {1,3}, {1,3}, {1,3}, {1,3}, {1,3}, {1,3}, {1,3}, 
{1,3}, {1,3}, {1,3}, {1,3}, {1,3}, {1,3}, {1,3}, {1,3}, {1,3}, 
{1,3}, {1,3}, {1,3}, {1,3}, {1,3}, {1,3}, {1,3}, {1,3}, {1,3}, 
{1,3}, {1,3}, {1,3}, 
};



static VLCtab MCBPCtabintra[] = {
{-1,0},
{20,6}, {36,6}, {52,6}, {4,4}, {4,4}, {4,4}, 
{4,4}, {19,3}, {19,3}, {19,3}, {19,3}, {19,3}, 
{19,3}, {19,3}, {19,3}, {35,3}, {35,3}, {35,3}, 
{35,3}, {35,3}, {35,3}, {35,3}, {35,3}, {51,3}, 
{51,3}, {51,3}, {51,3}, {51,3}, {51,3}, {51,3}, 
{51,3},
};



static VLCtab CBPYtab[48] =
{ {-1,0}, {-1,0}, {9,6}, {6,6}, {7,5}, {7,5}, {11,5}, {11,5},
  {13,5}, {13,5}, {14,5}, {14,5}, {15,4}, {15,4}, {15,4}, {15,4}, 
  {3,4}, {3,4}, {3,4}, {3,4}, {5,4},{5,4},{5,4},{5,4},
  {1,4}, {1,4}, {1,4}, {1,4}, {10,4}, {10,4}, {10,4}, {10,4},
  {2,4}, {2,4}, {2,4}, {2,4}, {12,4}, {12,4}, {12,4}, {12,4}, 
  {4,4}, {4,4}, {4,4}, {4,4}, {8,4}, {8,4}, {8,4}, {8,4}, 
};


VLCtab DCT3Dtab0[] = {
{4225,7}, {4209,7}, {4193,7}, {4177,7}, {193,7}, {177,7}, 
{161,7}, {4,7}, {4161,6}, {4161,6}, {4145,6}, {4145,6}, 
{4129,6}, {4129,6}, {4113,6}, {4113,6}, {145,6}, {145,6}, 
{129,6}, {129,6}, {113,6}, {113,6}, {97,6}, {97,6}, 
{18,6}, {18,6}, {3,6}, {3,6}, {81,5}, {81,5}, 
{81,5}, {81,5}, {65,5}, {65,5}, {65,5}, {65,5}, 
{49,5}, {49,5}, {49,5}, {49,5}, {4097,4}, {4097,4}, 
{4097,4}, {4097,4}, {4097,4}, {4097,4}, {4097,4}, {4097,4}, 
{1,2}, {1,2}, {1,2}, {1,2}, {1,2}, {1,2}, 
{1,2}, {1,2}, {1,2}, {1,2}, {1,2}, {1,2}, 
{1,2}, {1,2}, {1,2}, {1,2}, {1,2}, {1,2}, 
{1,2}, {1,2}, {1,2}, {1,2}, {1,2}, {1,2}, 
{1,2}, {1,2}, {1,2}, {1,2}, {1,2}, {1,2}, 
{1,2}, {1,2}, {17,3}, {17,3}, {17,3}, {17,3}, 
{17,3}, {17,3}, {17,3}, {17,3}, {17,3}, {17,3}, 
{17,3}, {17,3}, {17,3}, {17,3}, {17,3}, {17,3}, 
{33,4}, {33,4}, {33,4}, {33,4}, {33,4}, {33,4}, 
{33,4}, {33,4}, {2,4}, {2,4},{2,4},{2,4},
{2,4}, {2,4},{2,4},{2,4},
};


VLCtab DCT3Dtab1[] = {
{9,10}, {8,10}, {4481,9}, {4481,9}, {4465,9}, {4465,9}, 
{4449,9}, {4449,9}, {4433,9}, {4433,9}, {4417,9}, {4417,9}, 
{4401,9}, {4401,9}, {4385,9}, {4385,9}, {4369,9}, {4369,9}, 
{4098,9}, {4098,9}, {353,9}, {353,9}, {337,9}, {337,9}, 
{321,9}, {321,9}, {305,9}, {305,9}, {289,9}, {289,9}, 
{273,9}, {273,9}, {257,9}, {257,9}, {241,9}, {241,9}, 
{66,9}, {66,9}, {50,9}, {50,9}, {7,9}, {7,9}, 
{6,9}, {6,9}, {4353,8}, {4353,8}, {4353,8}, {4353,8}, 
{4337,8}, {4337,8}, {4337,8}, {4337,8}, {4321,8}, {4321,8}, 
{4321,8}, {4321,8}, {4305,8}, {4305,8}, {4305,8}, {4305,8}, 
{4289,8}, {4289,8}, {4289,8}, {4289,8}, {4273,8}, {4273,8}, 
{4273,8}, {4273,8}, {4257,8}, {4257,8}, {4257,8}, {4257,8}, 
{4241,8}, {4241,8}, {4241,8}, {4241,8}, {225,8}, {225,8}, 
{225,8}, {225,8}, {209,8}, {209,8}, {209,8}, {209,8}, 
{34,8}, {34,8}, {34,8}, {34,8}, {19,8}, {19,8}, 
{19,8}, {19,8}, {5,8}, {5,8}, {5,8}, {5,8}, 
};

VLCtab DCT3Dtab2[] = {
{4114,11}, {4114,11}, {4099,11}, {4099,11}, {11,11}, {11,11}, 
{10,11}, {10,11}, {4545,10}, {4545,10}, {4545,10}, {4545,10}, 
{4529,10}, {4529,10}, {4529,10}, {4529,10}, {4513,10}, {4513,10}, 
{4513,10}, {4513,10}, {4497,10}, {4497,10}, {4497,10}, {4497,10}, 
{146,10}, {146,10}, {146,10}, {146,10}, {130,10}, {130,10}, 
{130,10}, {130,10}, {114,10}, {114,10}, {114,10}, {114,10}, 
{98,10}, {98,10}, {98,10}, {98,10}, {82,10}, {82,10}, 
{82,10}, {82,10}, {51,10}, {51,10}, {51,10}, {51,10}, 
{35,10}, {35,10}, {35,10}, {35,10}, {20,10}, {20,10}, 
{20,10}, {20,10}, {12,11}, {12,11}, {21,11}, {21,11}, 
{369,11}, {369,11}, {385,11}, {385,11}, {4561,11}, {4561,11}, 
{4577,11}, {4577,11}, {4593,11}, {4593,11}, {4609,11}, {4609,11}, 
{22,12}, {36,12}, {67,12}, {83,12}, {99,12}, {162,12}, 
{401,12}, {417,12}, {4625,12}, {4641,12}, {4657,12}, {4673,12}, 
{4689,12}, {4705,12}, {4721,12}, {4737,12}, {7167,7}, 
{7167,7}, {7167,7}, {7167,7}, {7167,7}, {7167,7}, {7167,7}, 
{7167,7}, {7167,7}, {7167,7}, {7167,7}, {7167,7}, {7167,7}, 
{7167,7}, {7167,7}, {7167,7}, {7167,7}, {7167,7}, {7167,7}, 
{7167,7}, {7167,7}, {7167,7}, {7167,7}, {7167,7}, {7167,7}, 
{7167,7}, {7167,7}, {7167,7}, {7167,7}, {7167,7}, {7167,7}, 
{7167,7}, };


