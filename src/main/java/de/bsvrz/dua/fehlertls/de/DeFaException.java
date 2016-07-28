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

package de.bsvrz.dua.fehlertls.de;

/**
 * Diese Exception wird geworfen, wenn es Probleme bei Abläufen innerhalb einer
 * einen DE-Typ beschreibenden Klasse gibt. Oder insbesondere auch, wenn diese
 * Klasse nicht ermittelt oder instanziiert werden konnte.
 * 
 * @author BitCtrl Systems GmbH, Thierfelder
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
