/**
 * Segment 4 Datenübernahme und Aufbereitung (DUA), SWE 4.DeFa DE Fehleranalyse fehlende Messdaten
 * Copyright (C) 2007 BitCtrl Systems GmbH 
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 *
 * Contact Information:<br>
 * BitCtrl Systems GmbH<br>
 * Weißenfelser Straße 67<br>
 * 04229 Leipzig<br>
 * Phone: +49 341-490670<br>
 * mailto: info@bitctrl.de
 */

package de.bsvrz.dua.fehlertls.de;


/**
 * Interface, dass von allen Klassen implementiert werden muss, 
 * die einen konkreten DE-Typ für die SWE "DE Fehleranalyse fehlende
 * Messdaten" beschreiben.
 * 
 * @author BitCtrl Systems GmbH, Thierfelder
 *
 */
public interface IDeTyp {
	
	/**
	 * Aspekt <code>asp.tlsAntwort</code> für Antworten von TLS-Daten eines
	 * DE-Blocks nach Abruf, nach Pufferabfrage oder spontan
	 */
	public static final String ASP_TLS_ANTWORT = "asp.tlsAntwort"; //$NON-NLS-1$
	

	/**
	 * Erfragt alle Datenidentifikationen (mit allen Metainformationen), die bzgl. dieses
	 * DE-Typs Messwerte enthalten und auf die sich von der SWE "DE Fehleranalyse fehlende
	 * Messdaten" angemeldet werden sollte 
	 *
	 * @return ein ggf. leeres Feld mit allen Datenidentifikationen mit allen Metainformationen,
	 * die bzgl. dieses DE-Typs Messwerte enthalten und auf die sich von der SWE "DE Fehleranalyse
	 * fehlende Messdaten" angemeldet werden sollte 
	 */
	public AnalyseDatenBeschreibung[] getAnalyseDatenBeschreibung();

}
