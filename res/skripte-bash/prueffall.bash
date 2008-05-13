#!/bin/bash
source ../../../skripte-bash/einstellungen.sh

echo =================================================
echo =
echo =       Pruefungen SE4 - DUA, SWE 4.DeFa 
echo =
echo =================================================
echo 

index=0
declare -a tests
declare -a testTexts

#########################
# Name der Applikation #
#########################
appname=fehlertls

########################
#     Testroutinen     #
########################

testTexts[$index]="Automatischer JUnit-Test (Test Kurzzeitintervall)"
tests[$index]="fehlertls.DeFaApplikationTest2"
index=$(($index+1))

testTexts[$index]="Automatischer JUnit-Test (Test Langzeitintervall)"
tests[$index]="fehlertls.DeFaApplikationTest3"
index=$(($index+1))

testTexts[$index]="Automatischer JUnit-Test (Extra)"
tests[$index]="fehlertls.DeFaApplikationTest"
index=$(($index+1))


########################
#      ClassPath       #
########################
cp="../../de.bsvrz.sys.funclib.bitctrl/de.bsvrz.sys.funclib.bitctrl-runtime.jar"
cp=$cp":../de.bsvrz.dua."$appname"-runtime.jar"
cp=$cp":../de.bsvrz.dua.plformal-runtime.jar"
cp=$cp":../de.bsvrz.dua.pllogufd-runtime.jar"
cp=$cp":../de.bsvrz.dua."$appname"-test.jar"
cp=$cp":../../junit-4.1.jar"

########################
#     Ausfuehrung      #
########################

for ((i=0; i < ${#tests[@]}; i++));
do
	echo "================================================="
	echo "="
	echo "= Test Nr. "$(($i+1))":"
	echo "="
	echo "= "${testTexts[$i]}
	echo "="
	echo "================================================="
	echo 
	java -cp $cp $jvmArgs org.junit.runner.JUnitCore "de.bsvrz.dua."$appname"."${tests[$i]}
	sleep 10
done


