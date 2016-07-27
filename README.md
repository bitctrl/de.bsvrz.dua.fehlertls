[![Build Status](https://travis-ci.org/bitctrl/de.bsvrz.dua.fehlertls.svg?branch=develop)](https://travis-ci.org/bitctrl/de.bsvrz.dua.fehlertls)
[![Build Status](https://api.bintray.com/packages/bitctrl/maven/de.bsvrz.dua.fehlertls/images/download.svg)](https://bintray.com/bitctrl/maven/de.bsvrz.dua.fehlertls)

# Segment 4 Datenübernahme und Aufbereitung (DUA), SWE 4.DeFa DE Fehleranalyse fehlende Messdaten

Version: ${version}

## Übersicht

Die SWE DE Fehleranalyse fehlende Messdaten dient zur Ermittlung der Fehlerursache bei fehlenden
Messwerten an DE (Datenendgerät gemäß TLS). Im Rahmen der Erfassung von Daten über eine externe
TLS-Schnittstelle kann aus einer Reihe von Gründen ein erwarteter Messwert eines DE z. T. nicht
ermittelt werden. Der fehlende Messwert muss dabei nicht zwangsläufig durch den Detektor verursacht
werden. Fehlende Messwerte sind häufig auch durch Kommunikationsstörungen in der langen Kommunikationskette
zwischen Detektor - EAK - SM - KRI - UZ und VRZ bedingt. Diese SWE versucht die Störung innerhalb dieser
Kommunikationskette zu lokalisieren und über Betriebsmeldungen bzw. Fehlerstatusausgaben pro DE verfügbar
zu machen.

## Versionsgeschichte

### 2.0.0

Release-Datum: 31.05.2016

#### Neue Abhängigkeiten

Die SWE benötigt nun das Distributionspaket de.bsvrz.sys.funclib.bitctrl.dua
in Mindestversion 1.5.0 und de.bsvrz.sys.funclib.bitctrl in Mindestversion 1.4.0,
sowie die Kernsoftware in Mindestversion 3.8.0.

#### Änderungen

Folgende Änderungen gegenüber vorhergehenden Versionen wurden durchgeführt:

- Die Betriebsmeldungen wurden gemäß den Anwenderforderungen um IDs ergänzt
  und werden jetzt auch lokal über den Debug-Level INFO ausgegeben.

#### Fehlerkorrekturen

Folgende Fehler gegenüber vorhergehenden Versionen wurden korrigiert:

- Nerz-F–172: Wenn ein DE von mehreren EAK referenziert wird, führt dies nicht
  mehr zu einem Fehler in der SWE.

### 1.6.0

- Umstellung auf Java 8 und UTF-8

### 1.5.0

- Umstellung auf Funclib-Bitctrl-Dua

### 1.4.0

 - neue Kommadozeilenoption '-ignoriereSammelkanaele=<ja|nein>' (Standard: nein) 
   zum Ignorieren der Sammelkanäle (DEKanal==255)

### 1.3.0

- Auflösung der TlsHierarchie berücksichtigt Anschlusspunkte für beliebige Geräte (Kri2B).

### 1.2.4

  - Senden von reinen Betriebsmeldungen in DUA um die Umsetzung von Objekt-PID/ID nach
    Betriebsmeldungs-ID erweitert.  

## 1.2.3

  - FIX: SWE Fehleranalyse abgestuerzt mit java.lang.ArrayIndexOutOfBoundsException 
         innerhalb von Inselbus.java. Die fragliche Stelle im Code wurde identifiziert
         und angepasst. Der Fehler sollte nicht mehr auftreten. 
  

### 1.2.2

  - FIX: Sämtliche Konstruktoren DataDescription(atg, asp, sim) ersetzt durch
         DataDescription(atg, asp)

### 1.2.0

  - Cluster-DE aus der Ueberwachung ausgeschlossen.
  - Startparameter "-param" hinzu. Hier kann das Objekt (dessen Pid) vom Typ
    typ.tlsFehlerAnalyse uebergebene werden, ueber dessen Parameter eine
    Instanz gesteuert wird
  - Bash-Startskript hinzu 

### 1.1.0

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
	
	

# Disclaimer

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


## Kontakt

BitCtrl Systems GmbH
Weißenfelser Straße 67
04229 Leipzig
Phone: +49 341-490670
mailto: info@bitctrl.de

