#!/bin/bash

#SBATCH --output=%x.o%j
#SBATCH --error=%x.e%j

#SBATCH --partition=vrt
#SBATCH --qos=vrt
#SBATCH --nodes=1

## Cores: 8
#SBATCH --cpus-per-task=1
#SBATCH --mem=4G

## Max time: 10 days
#SBATCH --time=7-00:00:00

##SBATCH --account=T_2021_HPCPROGPAR

echo "#SLURM_JOB_NODELIST: $SLURM_JOB_NODELIST"

./script_asp.sh -g
