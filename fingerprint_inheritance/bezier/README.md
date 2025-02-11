# Curve di Bèzier per la rappresentazione di impronte digitali
## Funzionamento
Lo script bezierCurve.py permette di creare e salvare in un file le curve di Bèzier per la rappresentazione di un'impronta digitale. Come file di input utilizza "ridges.json" situato nella cartella /inheritance all'interno di una transparency. Di default viene utilizzata la transparency _012_3_2.tif, ma è possibile utilizzarne altre andando a modificare nel codice il percorso. Dopo aver eseguito bezierCurve.py per generare l'impronta digitale occorre estrarre le informazioni nel file curve.txt attraverso lo script FingerPrintExtraction.py.
L'esecuzione del file bezierCurve.py può durare circa 45 minuti a seconda della complessità dell'impronta.
## Moduli necessari
- import numpy as np
- import bezier
- import matplotlib.pyplot as plt
- import json
- import math
- import time
- from scipy.spatial import distance
- import pickle
- from matplotlib.colors import ListedColormap
- import sys

## Modulo di Bèzier
https://bezier.readthedocs.io/en/stable/python/reference/bezier.curve.html
## Esecuzione
Assicurarsi di aver estratto la cartella SourceAFIS e le relative transparency

Generazione file "ridges.json"
```sh
$ make
$ bin/fp.out ./Sourceafis/transparency_012_3_2.tif -sourceAFIS -boundingBox
```
Per generare il file curve.txt con le curve di Bèzier
```sh
$ python3 bezierCurve.py
```
Per estrarre le informazioni in curve.txt e ricostruire l'impronta
```sh
$ python3 FingerPrintExtraction.py 
```
## Output bezierCurve.py
- curve.txt : contiene le curve di Bèzier
- FingerPrintOriginal.png : Impronta iniziale
- FingerPrintFinal.png : Impronta finale
- FingerPrintOFDifference.png : Bitmap con differenza tra le due impronte
- matrice_iniziale.txt : bitmap 0 e 1 iniziale
- matrice_finale.txt  : bitmap 0 e 1 finale
- matrice_differenza.txt

## Output FingerPrintExtraction.py 
- FingerPrintExracted.png : Impronta estratta
- ExtractionInfo.txt : Informazioni estrazione
- matrice_estratta.txt :  : bitmap 0 e 1 estratta

## To do
- Inserire file dell'impronta da elaborare da linea di comando