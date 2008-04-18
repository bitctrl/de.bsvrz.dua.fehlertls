#!/bin/bash

source ../../../skripte-bash/einstellungen.sh

################################################################################
# SWE-Spezifische Parameter	(überprüfen und anpassen)                          #
################################################################################

ger=""

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
java $jvmArgs -jar ../de.bsvrz.dua.fehlertls-runtime.jar \
	$dav1 \
	-geraet=$ger \
	-debugLevelFileText=all \
	-debugLevelStdErrText=:error \
	-debugSetLoggerAndLevel=:none \
	-debugSetLoggerAndLevel=de.bsvrz.iav:config \
	&
