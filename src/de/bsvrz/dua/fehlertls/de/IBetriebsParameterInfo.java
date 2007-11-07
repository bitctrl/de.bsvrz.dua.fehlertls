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

import de.bsvrz.dav.daf.main.Data;

/**
 * Schnittstellen zu den Format-Informationen der Betriebsparameter eines DE-Typs
 * 
 * @author BitCtrl Systems GmbH, Thierfelder
 *
 */
public interface IBetriebsParameterInfo {
	
	/**
	 * Erfragt die PID der Attributgruppe, in der die Betriebsparameter stehen
	 * 
	 * @return die PID der Attributgruppe, in der die Betriebsparameter stehen
	 */
	public String getBetriebsParameterAtgPid();
	
	
	/**
	 * Liest aus einem Betriebsparameterdatensatz die aktuelle Erfassungsintervalldauer aus.
	 * 
	 * @param parameter ein Parameterdatensatz, welcher Daten der Attributgruppe enthaelt,
	 * die ueber die Methode <code>getBetriebsParameterAtgPid()</code> erfragt werden kann
	 * @return die entsprechende Erassungsintervalldauer (in ms), wenn das DE auf zyklischen Abruf
	 * parametriert ist und -1 sonst
	 */
	public long getErfassungsIntervall(final Data parameter);

}
