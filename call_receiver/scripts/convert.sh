#!/bin/sh

#Convierte los videos en formato h.263 a frames. Luego junta los frames creando videos avi usando el encoder de mplayer

CURRENT=0
for i in `ls calls/*`
do
   echo "converting $i to video"
   rm -rf temporal
   mkdir ./temporal
   cp $i temporal/input.263
   cd temporal
   ../decoder/tmndec input.263 output

   OUT="output"$CURRENT".avi"
   echo output is $OUT
   mencoder  "mf://*.tga" -mf fps=5 -o $OUT -ovc xvid -xvidencopts bitrate=3000 > /dev/null 2> /dev/null
   cp $OUT ../videos/$OUT
   CURRENT=`expr $CURRENT + 1`

   cd ..
done
