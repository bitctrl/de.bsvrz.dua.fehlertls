/*
 * Segment 4 Datenübernahme und Aufbereitung (DUA), SWE 4.DeFa DE Fehleranalyse fehlende Messdaten
 * Copyright (C) 2007-2015 BitCtrl Systems GmbH
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

import de.bsvrz.dav.daf.main.ClientDavInterface;
import de.bsvrz.dav.daf.main.Data;
import de.bsvrz.dav.daf.main.DataDescription;

/**
 * Interface, dass von allen Klassen implementiert werden muss, die einen
 * konkreten DE-Typ für die SWE "DE Fehleranalyse fehlende Messdaten"
 * beschreiben.
 *
 * @author BitCtrl Systems GmbH, Thierfelder
 */
public interface IDeTyp {

	/**
	 * Erfragt alle Datenidentifikationen, die bzgl. dieses DE-Typs Messwerte
	 * (Nutzdaten) enthalten und auf die sich von der SWE "DE Fehleranalyse
	 * fehlende Messdaten" angemeldet werden sollte
	 *
	 * @param dav
	 *            Datenverteiler-Verbindung
	 * @return die Datenidentifikationen, die bzgl. dieses DE-Typs zyklische
	 *         Messwerte enthalten
	 * @throws DeFaException
	 *             wird geworfen, wenn z.B. eine <code>DataDescription</code>
	 *             nicht erzeugt werden konnte, oder wenn es sonst Probleme gab
	 */
	DataDescription[] getDeFaMesswertDataDescriptions(final ClientDavInterface dav) throws DeFaException;

	/**
	 * Erfragt die Datenidentifikation, in der sich die Parameter für die
	 * Ermittlung der Erfassungsintervalldauer dieses DE-Typs befinden.
	 *
	 * @param dav
	 *            Datenverteiler-Verbindung
	 * @return die Parameter-Datenidentifikation für die
	 *         Erfassungsintervalldauer (ueblicherweise Betriebsparameter)
	 * @throws DeFaException
	 *             wird geworfen, wenn z.B. die <code>DataDescription</code>
	 *             nicht erzeugt werden konnte, oder wenn es sonst Probleme gab
	 */
	DataDescription getDeFaIntervallParameterDataDescription(final ClientDavInterface dav) throws DeFaException;

	/**
	 * Liest aus einem Parameterdatensatz die aktuelle Erfassungsintervalldauer
	 * aus.
	 *
	 * @param parameter
	 *            der Parameterdatensatz, welcher der Datenidentifikation
	 *            entspricht, die über die Methode
	 *            <code>getDeFaIntervallParameterDataDescription()</code>
	 *            erfragt werden kann
	 * @return die entsprechende Erassungsintervalldauer (in ms) wenn eine
	 *         zyklische Erfassung parametriert ist oder -1 sonst
	 */
	long getErfassungsIntervall(final Data parameter);

}
