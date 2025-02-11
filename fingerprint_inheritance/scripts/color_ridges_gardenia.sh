#!/bin/bash


#USAGE:
#from the main folder execute "scripts/color_ridges_gardenia.sh -p <path-that contains-FP-folders> -o <FPOne-name> -t <FPTwo-name>"

#File-names:
# The gardenia results should be stored in the a folder name "comparison_results" the same folder that contains also the FP folders
# The gardenia results should be named FP1-FP2.txt (or viceversa)

#Rendering
#To render the results just run the DrawColoredRidges main from the ImageProcessing project giving it as input the fingerprint folder path that you want to render

while getopts ":p:o:t:" opt; do
    case $opt in
        p)
            FPFOLDER=$OPTARG
            ;;
        o)
            FP1=$OPTARG
            ;;
        t)
            FP2=$OPTARG
            ;;
        \?)
            echo "Invalid option: -$OPTARG"
            exit 1
            ;;
        :)
            echo "Option -$OPTARG requires an argument." >&2
            exit 1
            ;;
    esac
done


echo -e "\nProducing colored ridges for the fingerprints $FP1 and $FP2\n"

bin/fp.out $FPFOLDER$FP1"/" -sourceAFIS -boundingBox  -erosion 50 > /dev/null
bin/fp.out $FPFOLDER$FP2"/" -sourceAFIS -boundingBox  -erosion 50 > /dev/null

COMPFILE=$FPFOLDER"comparison_results/"$FP1"-"$FP2".txt"
if test -f "$COMPFILE"; then
    echo "Operation file found."
else
    COMPFILE=$FPFOLDER"comparison_results/"$FP2"-"$FP1".txt"
    if test -f "$COMPFILE"; then
      echo "Comparison file found."
    else
      echo "Comparison file does not exist"
       exit 1
    fi
fi

python3 scripts/utilities/gardenia_results_parser.py $COMPFILE

mv "ridges_color"$FP1".json" $FPFOLDER$FP1"/inheritance/ridges_color.json"
mv "ridges_color"$FP2".json" $FPFOLDER$FP2"/inheritance/ridges_color.json"


echo "The end:)"
