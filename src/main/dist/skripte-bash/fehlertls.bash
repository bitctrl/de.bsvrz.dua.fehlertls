#!/bin/bash

cd `dirname $0`

source ../../../skripte-bash/einstellungen.sh

################################################################################
# SWE-Spezifische Parameter	(überprüfen und anpassen)                          #
################################################################################

ger="B27KR1,EfrKR1,EngKR1,FreKR1,HdhKR1,HeiKR1,HerKR1,KarKR1,KirKR1,LudKR1,LudKR2,ManKR1,NeuKR1,OffKR1,RotKR1,SinKR1,UlmKR1,UlmKR2,WalKR1,WalKR2,WanKR1"

################################################################################
# Folgende Parameter müssen überprüft und evtl. angepasst werden               #
################################################################################

# Parameter für den Java-Interpreter, als Standard werden die Einstellungen aus # einstellungen.sh verwendet.
#jvmArgs="-Dfile.encoding=ISO-8859-1"

# Parameter für den Datenverteiler, als Standard werden die Einstellungen aus # einstellungen.sh verwendet.
#dav1="-datenverteiler=localhost:8083 -benutzer=Tester -authentifizierung=passwd -debugFilePath=.."

jconPort="10422"

if [ "$testlauf" ]; then
	jvmArgs=$jvmArgs" -Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.port="$jconPort" -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false"
fi

################################################################################
# Ab hier muss nichts mehr angepasst werden                                    #
################################################################################

# Applikation starten
$java $jvmArgs -jar ../de.bsvrz.dua.fehlertls-runtime.jar \
	$dav1 \
	-geraet=$ger \
	-debugLevelFileText=all \
	-debugLevelStdErrText=:error \
	-debugSetLoggerAndLevel=:none \
	-debugSetLoggerAndLevel=de.bsvrz.iav:config \
	&
