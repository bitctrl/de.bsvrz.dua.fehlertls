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

package de.bsvrz.dua.fehlertls.enums;

import java.util.HashMap;
import java.util.Map;

import de.bsvrz.sys.funclib.bitctrl.daf.AbstractDavZustand;

/**
 * Korrespondiert mit dem DAV-Enumerationstyp <code>att.tlsDEFehlerStatus</code>
 * .
 *
 * @author BitCtrl Systems GmbH, Thierfelder
 *
 * @version $Id$
 */
public final class TlsDeFehlerStatus extends AbstractDavZustand {

	/**
	 * serialVersionUID.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Der Wertebereich dieses DAV-Enumerationstypen.
	 */
	private static Map<Integer, TlsDeFehlerStatus> werteBereich = new HashMap<Integer, TlsDeFehlerStatus>();

	/**
	 * DE in Ordnung.
	 */
	public static final TlsDeFehlerStatus OK = new TlsDeFehlerStatus("ok", 0,
			"DE in Ordnung");

	/**
	 * St�rung vom E/A-Konzentrator erkannt.
	 */
	public static final TlsDeFehlerStatus STOER_EAK = new TlsDeFehlerStatus(
			"St�rEAK", 1, "St�rung vom E/A-Konzentrator erkannt");

	/**
	 * St�rung vom SM erkannt.
	 */
	public static final TlsDeFehlerStatus STOER_SM = new TlsDeFehlerStatus(
			"St�rSM", 2, "St�rung vom SM erkannt");

	/**
	 * der Text der die Natur des DE-Fehlers illustriert.
	 */
	private final String text;

	/**
	 * Standardkonstruktor.
	 *
	 * @param kode
	 *            der Kode
	 * @param name
	 *            die Bezeichnung
	 * @param text
	 *            der Text der die Natur des DE-Fehlers illustriert
	 */
	private TlsDeFehlerStatus(final String name, final int kode,
			final String text) {
		super(kode, name);
		this.text = text;
		TlsDeFehlerStatus.werteBereich.put(kode, this);
	}

	/**
	 * Erfragt den Text der die Natur des DE-Fehlers illustriert.
	 *
	 * @return der Text der die Natur des DE-Fehlers illustriert
	 */
	public String getText() {
		return this.text;
	}

	/**
	 * Erfragt den Wert dieses DAV-Enumerationstypen mit dem �bergebenen Code.
	 *
	 * @param kode
	 *            der Kode des Zustands
	 * @return der Enumerations-Wert
	 */
	public static TlsDeFehlerStatus getZustand(final int kode) {
		return TlsDeFehlerStatus.werteBereich.get(kode);
	}

}
