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

package de.bsvrz.dua.fehlertls;

/**
 * Zustaende, die eine DE bzgl. der DeFa annehmen kann
 * 
 * @author BitCtrl Systems GmbH, Thierfelder
 * 
 * @version $Id$
 */
public final class DeStatus {

	/**
	 * Sende irgendwelche Nutzdaten.
	 */
	public static final DeStatus NUTZ_DATEN = new DeStatus("Nutzdaten"); //$NON-NLS-1$

	/**
	 * Sende DE-Kanal aktiviert UND DE-Fehler an.
	 */
	public static final DeStatus KANAL_AKTIVIERT_DE_FEHLER_AN = new DeStatus(
			"DE-Kanal aktiviert UND DE-Fehler an"); //$NON-NLS-1$

	/**
	 * Sende DE-Kanal aktiviert UND DE-Fehler aus.
	 */
	public static final DeStatus KANAL_AKTIVIERT_DE_FEHLER_AUS = new DeStatus(
			"DE-Kanal aktiviert UND DE-Fehler aus"); //$NON-NLS-1$

	/**
	 * Sende DE-Kanal passiviert UND DE-Fehler an.
	 */
	public static final DeStatus KANAL_PASSIVIERT_DE_FEHLER_AN = new DeStatus(
			"DE-Kanal passiviert UND DE-Fehler an"); //$NON-NLS-1$

	/**
	 * Sende DE-Kanal passiviert UND DE-Fehler AUS.
	 */
	public static final DeStatus KANAL_PASSIVIERT_DE_FEHLER_AUS = new DeStatus(
			"DE-Kanal passiviert UND DE-Fehler aus"); //$NON-NLS-1$

	/**
	 * Sende zyklisch an.
	 */
	public static final DeStatus ZYKLISCH_AN = new DeStatus("Zyklisch an"); //$NON-NLS-1$

	/**
	 * Sende zyklisch aus (nur UFD-DE).
	 */
	public static final DeStatus ZYKLISCH_AUS = new DeStatus("Zyklisch aus"); //$NON-NLS-1$

	/**
	 * Name des Status.
	 */
	private String name = null;

	/**
	 * Standardkonstruktor.
	 * 
	 * @param name
	 *            der Name des Status
	 */
	private DeStatus(String name) {
		this.name = name;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object obj) {
		boolean gleich = false;

		if (obj != null && obj instanceof DeStatus) {
			DeStatus that = (DeStatus) obj;
			gleich = this.name.equals(that.name);
		}

		return gleich;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return name;
	}

}
