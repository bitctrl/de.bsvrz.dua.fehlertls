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

package de.bsvrz.dua.fehlertls;

/**
 * Zustaende, die eine DE bzgl. der DeFa annehmen kann
 *
 * @author BitCtrl Systems GmbH, Thierfelder
 */
public enum DeStatus {

	/** Sende irgendwelche Nutzdaten. */
	NUTZ_DATEN("Nutzdaten"),

	/** Sende DE-Kanal aktiviert UND DE-Fehler an. */
	KANAL_AKTIVIERT_DE_FEHLER_AN("DE-Kanal aktiviert UND DE-Fehler an"),

	/** Sende DE-Kanal aktiviert UND DE-Fehler aus. */
	KANAL_AKTIVIERT_DE_FEHLER_AUS("DE-Kanal aktiviert UND DE-Fehler aus"),

	/** Sende DE-Kanal passiviert UND DE-Fehler an. */
	KANAL_PASSIVIERT_DE_FEHLER_AN("DE-Kanal passiviert UND DE-Fehler an"),

	/** Sende DE-Kanal passiviert UND DE-Fehler AUS. */
	KANAL_PASSIVIERT_DE_FEHLER_AUS("DE-Kanal passiviert UND DE-Fehler aus"),

	/** Sende zyklisch an. */
	ZYKLISCH_AN("Zyklisch an"),

	/** Sende zyklisch aus (nur UFD-DE). */
	ZYKLISCH_AUS("Zyklisch aus");

	/**
	 * Name des Status.
	 */
	private final String description;

	private DeStatus(final String description) {
		this.description = description;
	}

	@Override
	public String toString() {
		return description;
	}
}
