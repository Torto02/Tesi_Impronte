#!/bin/sh
FILES=../examples2/*.tif
count=0
for f in $FILES
do 
	echo $f
	echo $count
	./bin/fp.out $f -sourceAFIS -boundingBox -erosion 50
	let count++
done
