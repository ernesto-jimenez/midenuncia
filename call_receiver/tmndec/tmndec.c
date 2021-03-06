/************************************************************************
 *
 *  tmndec.c, main(), initialization, options for tmndecode (H.263 decoder)
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


#include <stdio.h>
#include <stdlib.h>
#include <ctype.h>
#include <fcntl.h>
#define GLOBAL
#include "config.h"
#include "tmndec.h"
#include "global.h"

#ifdef USE_TIME
#ifndef WIN32
#include <sys/time.h>
#else
#include <windows.h>
#endif
#endif
#ifdef WIN32
#include <io.h>
#endif



#ifdef WINDOWS
int initDisplay (int pels, int lines);
int closeDisplay ();
#endif

/* private prototypes */
static void initdecoder _ANSI_ARGS_((void));
static void options _ANSI_ARGS_((int *argcp, char **argvp[]));
static int getval _ANSI_ARGS_((char *argv[]));

/* private data */
static int loopflag;

int main(argc,argv)
int argc;
char *argv[];
{
  int first, framenum;
#ifdef USE_TIME
  int runtime;
#ifndef WIN32
  struct timeval tstart,tstop;
#else
  unsigned int startTime, stopTime;
#endif
#endif

#ifdef USE_TIME
  /* default is read frame rate from bitstream */
  framerate=99;
#endif

  options(&argc,&argv);

  /* pointer to name of output files */
#if (defined DISPLAY || defined WINDOWS)
  if (outtype==T_X11 || outtype == T_WIN)
    outputname = "";
  else
#endif
    outputname = argv[argc-1];

  ld = &base; 

  /* open MPEG input file(s) */
  if ((base.infile=open(argv[1],O_RDONLY|O_BINARY))<0) {
    sprintf(errortext,"Input file %s not found\n",argv[1]);
    error(errortext);
  }

  first = 1;

  do {
    if (base.infile!=0)
      lseek(base.infile,0l,0);
    initbits();
    framenum = 0;
    temp_ref = 0;
    prev_temp_ref -1; 

    while (getheader()) {
      if (first) {
        initdecoder();
#ifdef USE_TIME
#ifndef WIN32
        gettimeofday(&tstart,(struct timezone *)NULL);
        if (framerate > 0)
          gettimeofday(&tftarget,(struct timezone *)NULL);
#else
        startTime = timeGetTime();
        if (framerate > 0)
          targetTime = timeGetTime();
#endif
#endif
        first = 0;
      }
      getpicture(&framenum);
      
      framenum++;
    }

  } while (loopflag);

  close(base.infile);

#ifdef USE_TIME
#ifndef WIN32
  gettimeofday(&tstop,(struct timezone *)NULL);
  runtime = 100*(tstop.tv_sec-tstart.tv_sec)
    + (tstop.tv_usec-tstart.tv_usec)/10000;
#else
  stopTime = timeGetTime();
  runtime = (stopTime - startTime) / 10;
#endif
  if (!quiet && runtime!=0)
    printf("%d.%02d seconds, %d frames, %d.%02d fps\n",
           runtime/100, runtime%100,
           framenum, ((10000*framenum+runtime/2)/runtime)/100,
           ((10000*framenum+runtime/2)/runtime)%100);
#endif

#ifdef DISPLAY
  if (outtype==T_X11)
    exit_display();
#endif
#ifdef WINDOWS
  if (outtype == T_WIN)
    closeDisplay();
#endif

  return 0;
}

static void initdecoder()
{
  int i, cc, size;
  FILE *cleared;

  /* clip table */
  if (!(clp=(unsigned char *)malloc(1024)))
    error("malloc failed\n");

  clp += 384;

  for (i=-384; i<640; i++)
    clp[i] = (i<0) ? 0 : ((i>255) ? 255 : i);

  /* MPEG-1 = TMN parameters */
  matrix_coefficients = 5;


  switch (source_format) {
    case (SF_SQCIF):
      horizontal_size = 128;
      vertical_size = 96;
      break;
    case (SF_QCIF):
      horizontal_size = 176;
      vertical_size = 144;
      break;
    case (SF_CIF):
      horizontal_size = 352;
      vertical_size = 288;
      break;
    case (SF_4CIF):
      horizontal_size = 704;
      vertical_size = 576;
      break;
    case (SF_16CIF):
      horizontal_size = 1408;
      vertical_size = 1152;
      break;
    default:
      printf("ERROR: Illegal input format\n");
      exit(-1);
      break;
  }


  mb_width = horizontal_size/16;
  mb_height = vertical_size/16;
  coded_picture_width = horizontal_size;
  coded_picture_height = vertical_size;
  chrom_width =  coded_picture_width>>1;
  chrom_height = coded_picture_height>>1;
  blk_cnt = 6;

  for (cc=0; cc<3; cc++) {
    if (cc==0)
      size = coded_picture_width*coded_picture_height;
    else
      size = chrom_width*chrom_height;

    if (!(refframe[cc] = (unsigned char *)malloc(size)))
      error("malloc failed\n");

    if (!(oldrefframe[cc] = (unsigned char *)malloc(size)))
      error("malloc failed\n");

    if (!(bframe[cc] = (unsigned char *)malloc(size)))
      error("malloc failed\n");
  }

  for (cc=0; cc<3; cc++) {
    if (cc==0) {
      size = (coded_picture_width+64)*(coded_picture_height+64);
      if (!(edgeframeorig[cc] = (unsigned char *)malloc(size)))
        error("malloc failed\n");
      edgeframe[cc] = edgeframeorig[cc] + (coded_picture_width+64) * 32 + 32;
    }
    else {
      size = (chrom_width+32)*(chrom_height+32);
      if (!(edgeframeorig[cc] = (unsigned char *)malloc(size)))
        error("malloc failed\n");
      edgeframe[cc] = edgeframeorig[cc] + (chrom_width+32) * 16 + 16;
    }
  }

  if (expand) {
    for (cc=0; cc<3; cc++) {
      if (cc==0)
        size = coded_picture_width*coded_picture_height*4;
      else
        size = chrom_width*chrom_height*4;
      
      if (!(exnewframe[cc] = (unsigned char *)malloc(size)))
        error("malloc failed\n");
    }
  }

  /* Clear output file for concatenated storing */
  if (outtype == T_YUV_CONC) {
    if ((cleared = fopen(outputname,"wb")) == NULL) 
      error("couldn't clear outputfile\n");
    else
      fclose(cleared);
  }
  /* IDCT */
  if (refidct)
    init_idctref();
  else
    init_idct();

#ifdef DISPLAY
  if (outtype==T_X11) {
    init_display("");
  }
#endif
#ifdef WINDOWS
  if (outtype==T_WIN) {
    initDisplay(coded_picture_width, coded_picture_height);
  }
#endif


}

void error(text)
char *text;
{
  fprintf(stderr,text);
  exit(1);
}

/* trace output */
void printbits(code,bits,len)
int code,bits,len;
{
  int i;
  for (i=0; i<len; i++)
    printf("%d",(code>>(bits-1-i))&1);
}

/* option processing */
static void options(argcp,argvp)
int *argcp;
char **argvp[];
{
  while (*argcp>1 && (*argvp)[1][0]=='-')
  {
    while ((*argvp)[1][1])
    {
      switch (toupper((*argvp)[1][1]))
      {
#ifdef USE_TIME
      case 'F':
        framerate = getval(*argvp);
        break;
#endif
      case 'V':
        verbose = getval(*argvp);
        break;
      case 'O':
        outtype = getval(*argvp);
        break;
      case 'R':
        refidct = 1;
        break;
      case 'L':
        loopflag = 1;
        break;
      case 'X':
        expand = 1;
        break;
      case 'T':
        trace = 1;
        break;
      case 'Q':
        quiet = 1;
        break;
      default:
        fprintf(stderr,"undefined option -%c ignored\n",(*argvp)[1][1]);
      }

      (*argvp)[1]++;
    }

    (*argvp)++;
    (*argcp)--;
  }


  if (outtype != T_X11 && outtype != T_WIN) {
    loopflag = 0;  /* No looping for output to file */
#ifdef USE_TIME
    framerate = 0; /* No delay necessary when output to file */
#endif
  }
#ifdef DISPLAY
  if (outtype==T_X11)
  {
    (*argcp)++; /* fake outfile parameter */
  }
#endif
#ifdef WINDOWS
  if (outtype==T_WIN)
  {
    (*argcp)++; /* fake outfile parameter */
  }
#endif

  if (*argcp!=3 && *argcp!=4)
  {
    printf("\n%s\n",version);
    printf("Usage:   tmndecode {options} bitstream {outputfilename%%d}\n\
Options: -vn  verbose output (n: level)\n\
         -on  output format \n\
              n=0 : YUV\n\
              n=1 : SIF\n\
              n=2 : TGA\n\
              n=3 : PPM\n");
#ifdef DISPLAY
    printf("\
              n=4 : X11 Display\n");
#endif
    printf("\
              n=5 : YUV concatenated\n");
#ifdef WINDOWS
    printf("\
              n=6 : Windows 95/NT Display\n");
#endif
    printf("\
              You have to choose one output format!\n\
         -q   disable warnings to stderr\n\
         -r   use double precision reference IDCT\n\
         -t   enable low level tracing\n");
#ifdef DISPLAY
    printf("\
         -x   interpolate pictures to double size before display\n");
#endif
#ifdef USE_TIME
    printf("\
         -fn  frame rate\n\
              n=0  : as fast as possible\n\
              n=99 : read frame rate from bitstream (default)\n");
#endif    
#ifdef DISPLAY
    printf("\
         -l   loop sequence\n");
#endif
    exit(0);
  }
}

static int getval(argv)
char *argv[];
{
  int val;

  if (sscanf(argv[1]+2,"%d",&val)!=1)
    return 0;

  while (isdigit(argv[1][2]))
    argv[1]++;

  return val;
}



#ifdef USE_TIME
#ifndef WINDOWS

/* Unix version */
void doframerate(int pb)
{
  struct timeval tfdiff;
  const float REF_FRAME_RATE = 29.97;

  /* Compute desired frame rate */
  if (framerate <= 0)
    return;
  
  if (framerate != 99) {
    tftarget.tv_usec += 1000000 / framerate;
  }
  else {
    if (pb) {
      tftarget.tv_usec += 1000000 / (REF_FRAME_RATE/trb);
    }
    else {
      tftarget.tv_usec += 1000000 / (REF_FRAME_RATE/(trd-trb));
    }
  }

  /* this is where we should be */
  if (tftarget.tv_usec >= 1000000)
  {
    tftarget.tv_usec -= 1000000;
    tftarget.tv_sec++;
  }

  /* this is where we are */
  gettimeofday(&tfdiff,(struct timezone *)NULL);

  tfdiff.tv_usec = tftarget.tv_usec - tfdiff.tv_usec;
  tfdiff.tv_sec  = tftarget.tv_sec  - tfdiff.tv_sec;
  if (tfdiff.tv_usec < 0)
  {
    tfdiff.tv_usec += 1000000;
    tfdiff.tv_sec--;
  }

  /* See if we are already lagging behind */
  if (tfdiff.tv_sec < 0 || (tfdiff.tv_sec == 0 && tfdiff.tv_usec <= 0))
    return;
  
  /* Spin for awhile */
  select(0,NULL,NULL,NULL,&tfdiff);
}

#else 

/* Win32 version */
void doframerate(int pb)
{
  DWORD currentTime;
  int diffTime;
  const float REF_FRAME_RATE = (float)29.97;

  /* Compute desired frame rate */
  if (framerate <= 0)
    return;
  
  if (framerate != 99) {
    targetTime += 1000 / framerate;
  }
  else {
    if (pb) {
      targetTime += (int)(1000 / (REF_FRAME_RATE/trb));
    }
    else {
      targetTime += (int)(1000 / (REF_FRAME_RATE/(trd-trb)));
    }
  }

  /* this is where we are */
  currentTime = timeGetTime();

  
  diffTime = targetTime - currentTime;

  /* See if we are already lagging behind */
  if (diffTime <= 0)
    return;
  
  /* Spin for awhile */
  Sleep(diffTime);   
     /* this is not a very accurate timer */
}

#endif

#endif
