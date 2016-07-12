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

package de.bsvrz.dua.fehlertls.online;

import de.bsvrz.dua.fehlertls.enums.TlsDeFehlerStatus;

/**
 * Hoert auf Veraenderungen der Online-Attributgruppe
 * <code>atg.tlsGloDeFehler</code>.
 * 
 * @author BitCtrl Systems GmbH, Thierfelder
 * 
 * @version $Id$
 */
public interface ITlsGloDeFehlerListener {

	/**
	 * Informiert ueber neue Daten der Attributgruppe
	 * <code>atg.tlsGloDeFehler</code>.
	 * 
	 * @param aktiv
	 *            indiziert, dass der TLS-Kanalstatus auf <code>aktiv</code>
	 *            steht
	 * @param deFehlerStatus
	 *            TLS-DE-Fehler-Status
	 */
	void aktualisiereTlsGloDeFehler(boolean aktiv,
			TlsDeFehlerStatus deFehlerStatus);

}
