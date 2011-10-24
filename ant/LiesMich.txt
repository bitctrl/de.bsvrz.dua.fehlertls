*****************************************************************************************************
*  Segment 4 Datenübernahme und Aufbereitung (DUA), SWE 4.DeFa DE Fehleranalyse fehlende Messdaten  *
*****************************************************************************************************

Version: @Version@


Übersicht
=========

Die SWE DE Fehleranalyse fehlende Messdaten dient zur Ermittlung der Fehlerursache bei fehlenden
Messwerten an DE (Datenendgerät gemäß TLS). Im Rahmen der Erfassung von Daten über eine externe
TLS-Schnittstelle kann aus einer Reihe von Gründen ein erwarteter Messwert eines DE z. T. nicht
ermittelt werden. Der fehlende Messwert muss dabei nicht zwangsläufig durch den Detektor verursacht
werden. Fehlende Messwerte sind häufig auch durch Kommunikationsstörungen in der langen Kommunikationskette
zwischen Detektor – EAK – SM – KRI – UZ und VRZ bedingt. Diese SWE versucht die Störung innerhalb dieser
Kommunikationskette zu lokalisieren und über Betriebsmeldungen bzw. Fehlerstatusausgaben pro DE verfügbar
zu machen.


Versionsgeschichte
==================

1.2.4

  - Senden von reinen Betriebsmeldungen in DUA um die Umsetzung von Objekt-PID/ID nach
    Betriebsmeldungs-ID erweitert.  

1.2.3

  - FIX: SWE Fehleranalyse abgestuerzt mit java.lang.ArrayIndexOutOfBoundsException 
         innerhalb von Inselbus.java. Die fragliche Stelle im Code wurde identifiziert
         und angepasst. Der Fehler sollte nicht mehr auftreten. 
  

1.2.2

  - FIX: Sämtliche Konstruktoren DataDescription(atg, asp, sim) ersetzt durch
         DataDescription(atg, asp)

1.2.0

  - Cluster-DE aus der Ueberwachung ausgeschlossen.
  - Startparameter "-param" hinzu. Hier kann das Objekt (dessen Pid) vom Typ
    typ.tlsFehlerAnalyse uebergebene werden, ueber dessen Parameter eine
    Instanz gesteuert wird
  - Bash-Startskript hinzu 

1.1.0

  - Anpassung der Tests 

1.0.0

  - Erste Auslieferung

 
Diese SWE ist eine eigenständige Datenverteiler-Applikation, welche über die Klasse
de.bsvrz.dua.fehlertls.fehlertls.DeFaApplikation mit folgenden Parametern gestartet
werden kann (zusaetzlich zu den normalen Parametern jeder Datenverteiler-Applikation):
	-geraet=pid 
	(PID eines Objekts vom Typ "Gerät" (typ.gerät) aus dem Teilmodell "TLS", z. B.
	 uz.UZ.Xyz. Über diesen Aufrufparameter wird der Einstiegspunkt für die
	 Initialisierung festgelegt, ab dem die hierarchisch darunter liegenden DE
	 zur Überwachung ermittelt werden. Dazu dient ein Objekt vom Typ "Gerät"
	 (Steuermodul, KRI, UZ, VRZ, VIZ; i. d. R. eine UZ). Ab diesem Gerät wird 
	 die komplette TLS-Hierarchie bis hinunter zu den DE ermittelt. Der Zusammenhang
	 zwischen den einzelnen Ebenen wird zur Fehleranalyse verwendet)
	-param=pid 
	(PID eines Objekts vom Typ "typ.tlsFehlerAnalyse", ueber dessen Parameter diese
    Instanz gesteuert werden soll)
	
	
- Tests:

Alle Tests befinden sich unterhalb des Verzeichnisses junit und sind als JUnit-Tests ausführbar.
Die Tests untergliedern sich wie folgt:
	- DAV-Tests: die Tests, die die konkrete in Afo beschriebene Funktionalität der SWE testen (bei der
	  Durchführung dieser Tests wird jeweils implizit eine Instanz der DE Fehleranalyse fehlende Messdaten
	  gestartet)

Voraussetzungen für die DAV-Tests:
- Start der Test-Konfiguration (extra/testKonfig.zip)
- Anpassung der DAV-Start-Parameter (Variable CON_DATA) innerhalb von 
	junit/de.bsvrz.dua.fehlertls.DAVTest.java
- Die Parametrierung muss so eingestellt sein, dass alle Parameter der DE Fehleranalyse fehlende Messdaten
  und alle Parameter fuer DEs gespeichert werden

Inhalt/Verlauf DAV-Tests:

Da die Provokation aller moeglichen Zustaende fuer das System nicht moeglich ist,
wird im Folgenden versucht besonders prominente Fehlersituationen zu ueberpruefen.
In der dazu erstellten Test-Konfiguartion lassen sich alle theoretisch moeglichen
Fehlerausgaben ueberpruefen:

Die UFD-DE sind auf eine Intervalllaenge von 60s und die LVE-DE auf eine
Intervalllaenge von 15s eingestellt (nur bei zyklischem Abruf) 

Folgende Daten werden gesendet:
Zeitpunkte:                  0.1 0.2 0.3 1.0 1.1 1.2 1.3 2.0 2.1 2.2 2.3 3.0 3.1 3.2 3.3 4.0 4.1 4.2 4.3 5.0 5.1 5.2 5.3 6.0  

                          OK  D   D                       D       D   D       D   D   D
kri1.ib2.sm2.eaklve1.de2: |---!---!---!---|---!---!---!---|---!---!---!---|---!---!---!---|---!---!---!---|---!---!---!---|

                          OK  D           D               D       D   D       D   D   D
kri1.ib2.sm2.eaklve1.de1: |---!---!---!---|---!---!---!---|---!---!---!---|---!---!---!---|---!---!---!---|---!---!---!---|

                          OK                              D       D   D       D !OK
kri1.ib2.sm1.eaklve1.de1: |---!---!---!---|---!---!---!---|---!---!---!---|---!---!---!---|---!---!---!---|---!---!---!---|

                          !OK                  OK                         D
kri1.ib2.sm1.eakufd1.de2: |---!---!---!---|---!---!---!---|---!---!---!---|---!---!---!---|---!---!---!---|---!---!---!---|

                          !OK                  OK                         D
kri1.ib2.sm1.eakufd1.de1: |---!---!---!---|---!---!---!---|---!---!---!---|---!---!---!---|---!---!---!---|---!---!---!---|

                          !OK                  OK     D   D       D   D       D   D
kri1.ib2.sm3.eaklve1.de1: |---!---!---!---|---!---!---!---|---!---!---!---|---!---!---!---|---!---!---!---|---!---!---!---|

                          !OK                                                          OK D
kri1.ib1.sm1.eaklve1.de1: |---!---!---!---|---!---!---!---|---!---!---!---|---!---!---!---|---!---!---!---|---!---!---!---|

                          !OK                                                       
kri1.ib1.sm1.eakufd1.de1: |---!---!---!---|---!---!---!---|---!---!---!---|---!---!---!---|---!---!---!---|---!---!---!---|

                          !OK                                                       
kri1.ib1.sm1.eakufd1.de2: |---!---!---!---|---!---!---!---|---!---!---!---|---!---!---!---|---!---!---!---|---!---!---!---|


Legende: 
 OK: De wird von der DeFa erfasst (kein DE-Fehler, zyklischer Abruf, aktivierter Kanal)
!OK: De wird von der DeFa NICHT erfasst (zufaellig wird einer der Zustaende DE-Fehler, kein 
     zyklischer Abruf, passivierter Kanal geschaltet)
  D: sende Nutzdaten (fuer diese wird zufaellig eine innerhalb der Spezifikation
     definierte Attributgruppe)

Die dabei entstandenen Betriebsmeldungen bzw. Onlinedaten der DE-Fehleranalyse (pro DE)
werden überprüft und mit den erwarteten verglichen.
	
Die Tests wurden so bereits erfolgreich ausgeführt.



Disclaimer
==========

Segment 4 Datenübernahme und Aufbereitung (DUA), SWE 4.DeFa DE Fehleranalyse fehlende Messdaten
Copyright (C) 2007 BitCtrl Systems GmbH 

This program is free software; you can redistribute it and/or modify it under
the terms of the GNU General Public License as published by the Free Software
Foundation; either version 2 of the License, or (at your option) any later
version.

This program is distributed in the hope that it will be useful, but WITHOUT
ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
details.

You should have received a copy of the GNU General Public License along with
this program; if not, write to the Free Software Foundation, Inc., 51
Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.


Kontakt
=======

BitCtrl Systems GmbH
Weißenfelser Straße 67
04229 Leipzig
Phone: +49 341-490670
mailto: info@bitctrl.de

