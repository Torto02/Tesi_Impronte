#USAGE EXAMPLE (from main project folder): . produce_colored.sh /ImageProcessing/tests/012_3_2/



echo -e "\nProducing coloring files for the fingerprint" $@ "\n"

bin/fp.out $@ -sourceAFIS -boundingBox -fp_name digit1 -erosion 10 > /dev/null
clingo $@inheritance/digit1.asp asp/coloredergraph.lp > $@inheritance/colored.out
python3 scripts/utilities/edgeSCC_parser.py $@inheritance/colored.out
mv scc_color.json $@inheritance/scc_color.json

echo -e "The end:)"
