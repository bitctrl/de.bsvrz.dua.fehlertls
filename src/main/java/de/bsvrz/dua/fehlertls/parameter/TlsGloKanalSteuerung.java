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

package de.bsvrz.dua.fehlertls.parameter;

import de.bsvrz.dav.daf.main.*;
import de.bsvrz.dav.daf.main.config.SystemObject;
import de.bsvrz.sys.funclib.bitctrl.daf.DaVKonstanten;

import java.util.*;

/**
 * Korrespondiert mit der Attributgruppe <code>atg.tlsGloKanalSteuerung</code>
 * (Kanalsteuerung (FG alle / Typ 29)).
 * 
 * @author BitCtrl Systems GmbH, Thierfelder
 * 
 * @version $Id$
 */
public final class TlsGloKanalSteuerung implements ClientReceiverInterface {

	/**
	 * statische Instanzen dieser Klasse.
	 */
	private static Map<SystemObject, TlsGloKanalSteuerung> instanzen = Collections
			.synchronizedMap(new HashMap<SystemObject, TlsGloKanalSteuerung>());

	/**
	 * Menge aller Beobachterobjekte.
	 */
	private Set<ITlsGloKanalSteuerungsListener> listenerMenge = Collections
			.synchronizedSet(new HashSet<ITlsGloKanalSteuerungsListener>());

	/**
	 * Indiziert, dass der TLS-Kanalstatus auf <code>aktiv</code> steht.
	 */
	private Boolean aktiv = null;

	/**
	 * Erfragt eine statische Instanz dieser Klasse.
	 * 
	 * @param dav
	 *            Verbindung zum Datenverteiler
	 * @param objekt
	 *            ein Objekt vom Typ <code>typ.de</code>
	 * @return eine statische Instanz dieser Klasse oder <code>null</code>
	 */
	public static TlsGloKanalSteuerung getInstanz(ClientDavInterface dav,
			SystemObject objekt) {
		TlsGloKanalSteuerung instanz = null;

		synchronized (instanzen) {
			instanz = instanzen.get(objekt);
		}

		if (instanz == null) {
			instanz = new TlsGloKanalSteuerung(dav, objekt);
			synchronized (instanzen) {
				instanzen.put(objekt, instanz);
			}
		}

		return instanz;
	}

	/**
	 * Standardkonstruktor.
	 * 
	 * @param dav
	 *            Verbindung zum Datenverteiler
	 * @param objekt
	 *            ein Objekt vom Typ <code>typ.de</code>
	 */
	private TlsGloKanalSteuerung(ClientDavInterface dav, SystemObject objekt) {
		dav.subscribeReceiver(this, objekt, new DataDescription(
				dav.getDataModel()
						.getAttributeGroup("atg.tlsGloKanalSteuerung"), //$NON-NLS-1$
				dav.getDataModel().getAspect(DaVKonstanten.ASP_PARAMETER_SOLL)),
				ReceiveOptions.normal(), ReceiverRole.receiver());
	}

	/**
	 * Fuegt diesem Objekt einen Listener hinzu.
	 * 
	 * @param listener
	 *            eine neuer Listener
	 */
	public synchronized void addListener(
			final ITlsGloKanalSteuerungsListener listener) {
		if (listenerMenge.add(listener) && this.aktiv != null) {
			listener.aktualisiereTlsGloKanalSteuerung(this.aktiv);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void update(ResultData[] resultate) {
		if (resultate != null) {
			for (ResultData resultat : resultate) {
				if (resultat != null && resultat.getData() != null) {
					synchronized (this) {
						this.aktiv = resultat.getData().getUnscaledValue(
								"DEKanalStatus").intValue() == 0; //$NON-NLS-1$
						for (ITlsGloKanalSteuerungsListener listener : this.listenerMenge) {
							listener
									.aktualisiereTlsGloKanalSteuerung(this.aktiv);
						}
					}
				}
			}
		}
	}

}
