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

package de.bsvrz.dua.fehlertls.enums;

import java.util.HashMap;
import java.util.Map;

import de.bsvrz.sys.funclib.bitctrl.daf.AbstractDavZustand;

/**
 * Korrespondiert mit dem DAV-Enumerationstyp <code>att.tlsFehlerAnalyse</code>.
 *
 * @author BitCtrl Systems GmbH, Thierfelder
 *
 * @version $Id$
 */
public final class TlsFehlerAnalyse extends AbstractDavZustand {

	/**
	 * serialVersionUID.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Der Wertebereich dieses DAV-Enumerationstypen.
	 */
	private static Map<Integer, TlsFehlerAnalyse> werteBereich = new HashMap<Integer, TlsFehlerAnalyse>();

	/**
	 * Fehlerursache konnte nicht ermittelt werden.
	 */
	public static final TlsFehlerAnalyse UNBEKANNT = new TlsFehlerAnalyse(
			"unbekannte Ursache", 0);

	/**
	 * Verbindung zum KRI xxx oder KRI selbst defekt. Verbindung zum KRI oder
	 * KRI instand setzen
	 */
	public static final TlsFehlerAnalyse KRI_DEFEKT = new TlsFehlerAnalyse(
			"KRI oder Verbindung zum KRI defekt", 1);

	/**
	 * Modem am Inselbus xxx oder Inselbus selbst defekt. Modem oder Inselbus
	 * instand setzen
	 */
	public static final TlsFehlerAnalyse INSELBUS_MODEM_ODER_INSELBUS_DEFEKT = new TlsFehlerAnalyse(
			"Modem-Inselbus oder Inselbus defekt", 2);

	/**
	 * Inselbus xxx gestört: Für die DE der Steuermodule x1, x2,.. sind keine
	 * Daten verfügbar. Inselbus xxx instand setzen
	 */
	public static final TlsFehlerAnalyse INSELBUS_DEFEKT = new TlsFehlerAnalyse(
			"Inselbus defekt", 3);

	/**
	 * Modem am Steuermodul x oder Steuermodul defekt. Modem am Steuermodul x
	 * oder Steuermodul instand setzen
	 */
	public static final TlsFehlerAnalyse SM_MODEM_ODER_SM_DEFEKT = new TlsFehlerAnalyse(
			"Modem-Steuermodul oder Steuermodul defekt", 4);

	/**
	 * EAK x am Steuermodul y defekt. EAK x am Steuermodul y instand setzen
	 */
	public static final TlsFehlerAnalyse EAK_AN_SM_DEFEKT = new TlsFehlerAnalyse(
			"EAK am Steuermodul defekt", 5);

	/**
	 * Standardkonstruktor.
	 *
	 * @param kode
	 *            der Kode
	 * @param name
	 *            die Bezeichnung
	 */
	private TlsFehlerAnalyse(final String name, final int kode) {
		super(kode, name);
		TlsFehlerAnalyse.werteBereich.put(kode, this);
	}

	/**
	 * Erfragt den Wert dieses DAV-Enumerationstypen mit dem übergebenen Code.
	 *
	 * @param kode
	 *            der Kode des Zustands
	 * @return der Enumerations-Wert
	 */
	public static TlsFehlerAnalyse getZustand(final int kode) {
		return TlsFehlerAnalyse.werteBereich.get(kode);
	}

}
