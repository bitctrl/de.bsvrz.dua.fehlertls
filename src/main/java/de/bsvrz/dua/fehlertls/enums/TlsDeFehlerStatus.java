/*
 * Segment Datenübernahme und Aufbereitung (DUA), Fehleranalyse fehlende Messdaten TLS
 * Copyright (C) 2007 BitCtrl Systems GmbH 
 * Copyright 2016 by Kappich Systemberatung Aachen
 * 
 * This file is part of de.bsvrz.dua.fehlertls.
 * 
 * de.bsvrz.dua.fehlertls is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * de.bsvrz.dua.fehlertls is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with de.bsvrz.dua.fehlertls.  If not, see <http://www.gnu.org/licenses/>.

 * Contact Information:
 * Kappich Systemberatung
 * Martin-Luther-Straße 14
 * 52062 Aachen, Germany
 * phone: +49 241 4090 436 
 * mail: <info@kappich.de>
 */

package de.bsvrz.dua.fehlertls.enums;

import de.bsvrz.sys.funclib.bitctrl.daf.AbstractDavZustand;

import java.util.HashMap;
import java.util.Map;

/**
 * Korrespondiert mit dem DAV-Enumerationstyp <code>att.tlsDEFehlerStatus</code>.
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
	public static final TlsDeFehlerStatus OK = new TlsDeFehlerStatus(
			"ok", 0, "DE in Ordnung"); //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * Störung vom E/A-Konzentrator erkannt.
	 */
	public static final TlsDeFehlerStatus STOER_EAK = new TlsDeFehlerStatus(
			"StörEAK", 1, "Störung vom E/A-Konzentrator erkannt"); //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * Störung vom SM erkannt.
	 */
	public static final TlsDeFehlerStatus STOER_SM = new TlsDeFehlerStatus(
			"StörSM", 2, "Störung vom SM erkannt"); //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * der Text der die Natur des DE-Fehlers illustriert.
	 */
	private String text = null;

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
	private TlsDeFehlerStatus(String name, int kode, String text) {
		super(kode, name);
		this.text = text;
		werteBereich.put(kode, this);
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
	 * Erfragt den Wert dieses DAV-Enumerationstypen mit dem übergebenen Code.
	 * 
	 * @param kode
	 *            der Kode des Zustands
	 * @return der Enumerations-Wert
	 */
	public static TlsDeFehlerStatus getZustand(final int kode) {
		return werteBereich.get(kode);
	}

}
