#!/bin/bash

### script che aggiunge "/n" alla matrice dei ridges

if [ "$(command -v fold)" == "" ]; then
	echo "Errore: fold non è installato"
	exit 127
fi

### help

if [ "$1" == "--help" ]; then
	echo
	echo "Script ridges"
	echo
	echo "Parametri"
	echo "1) path del file 059B-valleys-thinned-skeleton-bounded"
	echo "2) larghezza dello skeleton"
	echo
	exit 0
fi

### test numero dei parametri

if [ "$#" -ne 2 ]; then
	echo "Errore: usare due parametri (--help per ulteriori informazioni)"
	exit 1
fi

### test unzip Sourceafis

if [ "$(basename "$1")" == "059B-valleys-thinned-skeleton-bounded.txt" ]; then
	fold -w"$2" "$1" >temp-skeleton.txt

	### sostituisce 0 con spazi (migliora leggibilità)
  ### su linux usare sed -i"" (senza spazio)
	sed -i "" "s/0/ /g" temp-skeleton.txt

	### elimina trailing whitespaces
  ### su linux usare sed -i"" (senza spazio)
	sed -i "" -E "s/[ '$'\t'']+$//" temp-skeleton.txt
	exit 0
fi

exit 2
