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
 * Diese Exception wird geworfen, wenn es Probleme bei Abläufen innerhalb einer
 * einen DE-Typ beschreibenden Klasse gibt. Oder insbesondere auch, wenn diese
 * Klasse nicht ermittelt oder instanziiert werden konnte.
 * 
 * @author BitCtrl Systems GmbH, Thierfelder
 * 
 * @version $Id$
 */
public class DeFaException extends Exception {

	/**
	 * Standardkonstruktor.
	 * 
	 * @param ex
	 *            die Exception, die in diese DeFaException umgewandelt werden
	 *            soll
	 */
	public DeFaException(final Throwable ex) {
		super(ex);
	}

	/**
	 * Konstruktor.
	 * 
	 * @param nachricht
	 *            die Nachricht, die diese Ausnahme flankiert
	 */
	public DeFaException(final String nachricht) {
		super(nachricht);
	}

}
