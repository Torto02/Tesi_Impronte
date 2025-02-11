#!/bin/bash


#USAGE:
#from the main folder execute "scripts/color_ridges_gardenia.sh -p <path-that contains-FP-folders> -o <FPOne-name> -t <FPTwo-name>"
#The argument -e sets the erosion value.

#File-names:
# The gardenia results should be stored in the a folder name "comparison_results" the same folder that contains also the FP folders
# The gardenia results should be named FP1-FP2.txt (or viceversa)

#Rendering
#To render the results just run the DrawColoredRidges main from the ImageProcessing project giving it as input the fingerprint folder path that you want to render
EROSION=60

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
        e)
            EROSION=$OPTARG
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

echo -e -n "\nCompiling the C++ code ... "
make

echo -e -n "\nProducing the inheritance-data for the the fingerprints $FP1 (with erosion $EROSION) ... "
bin/fp.out $FPFOLDER$FP1"/" -sourceAFIS -boundingBox  -erosion $EROSION > /dev/null
echo -e -n "done\n"

echo -e -n "\nProducing the inheritance-data for the the fingerprints $FP2 (with erosion $EROSION) ... "
bin/fp.out $FPFOLDER$FP2"/" -sourceAFIS -boundingBox  -erosion $EROSION > /dev/null
echo -e -n "done\n"


echo -e -n "\nProducing the String derived from the edges for $FP1 ... "
#INSERT JAVA EXECUTION HERE - MainPistore $FPFOLDER$FP1 (Maybe the FPFOLDER needs to be adapted)
echo -e -n "done\n"

echo -e -n "\nProducing the String derived from the edges for $FP1 ... "
#INSERT JAVA EXECUTION HERE - MainPistore $FPFOLDER$FP2
echo -e -n "done\n"


echo -e -n "\nProducing the Confromtation of the fingerprints with Gardenia ... "
#INSERT JAVA EXECUTION HERE - ProvaGardeniaMain $FPFOLDER $FP1 $FP2
echo -e -n "done\n"

COMPFILE=$FPFOLDER"comparison_results/"$FP1"-"$FP2".txt"
if ! test -f "$COMPFILE"; then
    COMPFILE=$FPFOLDER"comparison_results/"$FP2"-"$FP1".txt"
    if ! test -f "$COMPFILE"; then
      echo "Comparison file does not exist"
       exit 1
    fi
fi

echo -e -n "\nAssociating colors to matching ridegs of the Fingerprints ... "
python3 scripts/utilities/gardenia_results_parser.py $COMPFILE
echo -e -n "done\n"

mv "ridges_color"$FP1".json" $FPFOLDER$FP1"/inheritance/ridges_color.json"
mv "ridges_color"$FP2".json" $FPFOLDER$FP2"/inheritance/ridges_color.json"

echo -e -n "\nProducing graphical representation for $FP1 ... "
#INSERT JAVA EXECUTION HERE - DrawColoredRidges $FPFOLDER$FP1
echo -e -n "done\n"

echo -e -n "\nProducing graphical representation for $FP2 ... "
#INSERT JAVA EXECUTION HERE - DrawColoredRidges $FPFOLDER$FP2
echo -e -n "done\n"

echo -e "\nThe end:)"
