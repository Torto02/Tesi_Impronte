#!/bin/bash

### script che genera i file fp.asp ed esegue clingo in batch

###
### codici errore
###
###	127: comando non trovato
### 3: parametri
### 4: directory
### 5: unzip
###

### ATTENZIONE: versione dello standard c++11 (Makefile CFLAGS)

### test g++

if [ "$(command -v g++)" == "" ]; then
	echo "Errore: g++ non è installato"
	exit 127
fi

### test xargs

if [ "$(command -v xargs)" == "" ]; then
	echo "Errore: xargs non è installato"
	exit 127
fi

### test clingo

if [ "$(command -v clingo)" == "" ]; then
	echo "Errore: clingo non è installato"
	exit 127
fi

### test numero dei parametri

if [ "$#" -gt 1 ]; then
	echo "Errore: usare massimo un parametro (--help per ulteriori informazioni)"
	exit 3
fi

### test esistenza dei parametri

if [ "$1" != "" ] && [ "$1" != "-c" ] && [ "$1" != "-g" ] && [ "$1" != "--help" ]; then
	echo "Errore: il parametro non esiste (--help per ulteriori informazioni)"
	exit 3
fi

### help

if [ "$1" == "--help" ]; then
	echo
	echo "Script ASP"
	echo
	echo "Parametri"
	echo "-c : make clean"
	echo "-g : salta generazione (i file c++ sono già stati generati)"
	echo
	exit 0
fi

### test root directory

if [ ! -d "./ImageProcessing" ] || [ ! -d "./asp" ] || [ ! -d "./include" ] || [ ! -d "./lib" ] || [ ! -d "./src" ]; then
	echo "Errore: eseguire script da root 'fingerprint_inheritance'"
	exit 4
fi

### test unzip Sourceafis

if [ ! -d "./Sourceafis" ]; then
	echo "Errore: eseguire unzip di './Sourceafis.zip'"
	exit 5
fi

### test unzip sottocartelle Sourceafis

fingerprint="$(find "./Sourceafis" -maxdepth 1 -name "transparency*tif" | sort)"
if [ "$fingerprint" == "" ]; then
	echo "Errore: eseguire unzip degli archivi nella cartella './Sourceafis'"
	exit 5
fi

### test OSX

if [ "$(uname)" == "Darwin" ]; then
	my_make() {
		make CC="g++-12" CFLAGS="-std=c++11 -g -Wall -Wfatal-errors"
	}
else
	my_make() {
		make CC="g++" CFLAGS="-std=c++11 -g -Wall -Wfatal-errors"
	}
fi

num_folders="$(echo "$fingerprint" | wc -l | xargs)"

### parametro -c: make clean

if [ "$1" == "-c" ]; then
	make clean
fi

### parametro != -g: make

if [ "$1" != "-g" ]; then
	my_make

	### genera l'output di main.cpp per ogni impronta

	echo "Generazione output per ogni impronta in corso..."
	for ((i = 1; i <= num_folders; ++i)); do
		bin/fp.out "$(echo "$fingerprint" | head -n "$i" | tail -n 1)" -sourceAFIS -boundingBox
		echo "$i"
	done
	echo "Generazione completata"

	### genera la copia di fp.asp ogni impronta

	echo "Copia dei file impronte asp in corso..."
	for ((i = 1; i <= num_folders; ++i)); do
		cp "$(echo "$fingerprint" | head -n "$i" | tail -n 1)/inheritance/fp.asp" "$(echo "$fingerprint" | head -n "$i" | tail -n 1)/inheritance/fp_copy.asp"
	done
	echo "Copia completata"

	### rinomina il nome della fingerprint nel file di ogni copia

	echo "Ridenominazione dei nomi delle impronte asp in corso..."
	for ((i = 1; i <= num_folders; ++i)); do
		### su linux usare sed -i"" (senza spazio)
		sed -i "" "s/fp/fp_copy/g" "$(echo "$fingerprint" | head -n "$i" | tail -n 1)/inheritance/fp_copy.asp"
	done
	echo "Ridenominazione completata."
fi

time="$(date +%s)"

### crea la prima riga

for ((i = 1; i <= num_folders; ++i)); do
	if ((i == 1)); then
		echo -e -n "           |" >out"${time}".txt
	fi
	echo -e -n " <$(echo "$fingerprint" | head -n "$i" | tail -n 1 | cut -c 27- | rev | cut -c 5- | rev)> |" >>out"${time}".txt
done

echo >>out"${time}".txt

### crea le righe rimanenti

for ((i = 1; i <= num_folders; ++i)); do
	echo -e -n " <$(echo "$fingerprint" | head -n "$i" | tail -n 1 | cut -c 27- | rev | cut -c 5- | rev)> |" >>out"${time}".txt
	for ((j = 1; j <= num_folders; ++j)); do
		### non esegue due volte lo stesso confronto
		if (("$i" <= "$j")); then
			echo -e -n "    $(clingo "$(echo "$fingerprint" | head -n "$i" | tail -n 1)/inheritance/fp.asp" "$(echo "$fingerprint" | head -n "$j" | tail -n 1)/inheritance/fp_copy.asp" "./asp/iso_n5.lp" -t6 --time-limit=90 | grep score | tail -n 1 | cut -c 7- | rev | cut -c 2- | rev)    |" >>out"${time}".txt
		else
			echo -e -n "    ---    |" >>out"${time}".txt
		fi
	done
	echo >>out"${time}".txt
done

echo "Elimina gli spazi da 'out.txt' prima di importare i dati su excel"

exit 0
