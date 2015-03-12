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

package de.bsvrz.dua.fehlertls.tls;

/**
 * Hoert auf Veraenderungen im Erfassungszustand eines DE bezueglich der DeFa.
 * Dieser Zustand kann die Werte <code>erfasst</code> und
 * <code>nicht erfasst</code> annehmen
 * 
 * @author BitCtrl Systems GmbH, Thierfelder
 * 
 * @version $Id$
 */
public interface IDeErfassungsZustandListener {

	/**
	 * Aktualisiert den Erfassungszustand eines DE bezueglich der DeFa.
	 * 
	 * @param zustand
	 *            Erfassungszustand eines DE bezueglich der DeFa
	 */
	void aktualisiereErfassungsZustand(
			DeErfassungsZustand.Zustand zustand);

}
