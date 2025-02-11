#!/bin/bash

### script che ordina i file impronte in asp (utile per ordinare per lunghezza, ratio, ecc...)

if test $# -ne 2 || ! test -e "$1"; then
	echo "ERROR: asp_sort.sh meno di 2 parametri passati o file non trovato"
	echo "1° parametro: percorso del file da ordinare"
	echo "2° parametro: colonna del file da ordinare"
	exit 1
fi

if test "$(file "$1")" != "$1: ASCII text"; then
	echo "il formato del file deve essere ASCII text"
	exit 2
fi

sort -t , -k 1,1 -k "$2","$2n" "$1"

exit 0
