#USAGE EXAMPLE: . execute_subfolder.sh /mnt/c/Users/Francesco/Desktop/Ago_fp

 

echo -e "\nExecution of fp.out in all the folders in " $@/ "\n"
for dir in $@/*; do
    if [ -d "$dir" ]; then
		echo -n "$(basename $dir)"
		../bin/./fp.out $dir -sourceAFIS -boundingBox  >/dev/null
		echo -e " done.\n"
	fi
done

echo -e "The end:)"