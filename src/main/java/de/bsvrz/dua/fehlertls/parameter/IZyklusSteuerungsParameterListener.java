/*
 * Segment 4 Daten�bernahme und Aufbereitung (DUA), SWE 4.DeFa DE Fehleranalyse fehlende Messdaten
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
 * Wei�enfelser Stra�e 67<br>
 * 04229 Leipzig<br>
 * Phone: +49 341-490670<br>
 * mailto: info@bitctrl.de
 */

package de.bsvrz.dua.fehlertls.parameter;

/**
 * Hoert auf Veraenderungen der Betriebsparameter zur Zyklussteuerung eines
 * allgemeinen Systemobjektes vom Typ <code>typ.de</code>.
 *
 * @author BitCtrl Systems GmbH, Thierfelder
 *
 * @version $Id: IZyklusSteuerungsParameterListener.java 53686 2015-03-16
 *          11:30:02Z peuker $
 */
public interface IZyklusSteuerungsParameterListener {

	/**
	 * Aktualisiert die Betriebsparameter zur Zyklussteuerung eines allgemeinen
	 * Systemobjektes vom Typ <code>typ.de</code>.
	 *
	 * @param erfassungsIntervallDauer
	 *            die entsprechende Erassungsintervalldauer (in ms), wenn das DE
	 *            auf zyklischen Abruf parametriert ist und -1 sonst
	 */
	void aktualisiereZyklusSteuerungsParameter(long erfassungsIntervallDauer);

}
