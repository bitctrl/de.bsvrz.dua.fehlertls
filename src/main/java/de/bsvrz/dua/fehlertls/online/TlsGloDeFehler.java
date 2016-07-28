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

import de.bsvrz.dav.daf.main.*;
import de.bsvrz.dav.daf.main.config.SystemObject;
import de.bsvrz.dua.fehlertls.enums.TlsDeFehlerStatus;
import de.bsvrz.sys.funclib.bitctrl.dua.DUAKonstanten;

import java.util.*;

/**
 * Korrespondiert mit der Online-Attributgruppe <code>atg.tlsGloDeFehler</code>.
 * 
 * @author BitCtrl Systems GmbH, Thierfelder
 */
public final class TlsGloDeFehler implements ClientReceiverInterface {

	/**
	 * statische Instanzen dieser Klasse.
	 */
	private static Map<SystemObject, TlsGloDeFehler> instanzen = Collections
			.synchronizedMap(new HashMap<SystemObject, TlsGloDeFehler>());

	/**
	 * Menge aller Beobachterobjekte.
	 */
	private Set<ITlsGloDeFehlerListener> listenerMenge = Collections
			.synchronizedSet(new HashSet<ITlsGloDeFehlerListener>());

	/**
	 * indiziert, dass der TLS-Kanalstatus auf <code>aktiv</code> steht.
	 */
	private Boolean aktiv = null;

	/**
	 * TLS-DE-Fehler-Status.
	 */
	private TlsDeFehlerStatus deFehlerStatus = null;

	/**
	 * Erfragt eine statische Instanz dieser Klasse.
	 * 
	 * @param dav
	 *            Verbindung zum Datenverteiler
	 * @param objekt
	 *            ein Objekt vom Typ <code>typ.de</code>
	 * @return eine statische Instanz dieser Klasse oder <code>null</code>
	 */
	public static TlsGloDeFehler getInstanz(ClientDavInterface dav,
			SystemObject objekt) {
		TlsGloDeFehler instanz = null;

		synchronized (instanzen) {
			instanz = instanzen.get(objekt);
		}

		if (instanz == null) {
			instanz = new TlsGloDeFehler(dav, objekt);
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
	private TlsGloDeFehler(ClientDavInterface dav, SystemObject objekt) {
		dav.subscribeReceiver(this, objekt, new DataDescription(
				dav.getDataModel().getAttributeGroup("atg.tlsGloDeFehler"), //$NON-NLS-1$
				dav.getDataModel().getAspect(DUAKonstanten.ASP_TLS_ANTWORT)),
				ReceiveOptions.normal(), ReceiverRole.receiver());
	}

	/**
	 * Fuegt diesem Objekt einen Listener hinzu.
	 * 
	 * @param listener
	 *            eine neuer Listener
	 */
	public synchronized void addListener(
			final ITlsGloDeFehlerListener listener) {
		if (listenerMenge.add(listener) && this.aktiv != null) {
			listener
					.aktualisiereTlsGloDeFehler(this.aktiv, this.deFehlerStatus);
		}
	}

	public void update(ResultData[] resultate) {
		if (resultate != null) {
			for (ResultData resultat : resultate) {
				if (resultat != null && resultat.getData() != null) {
					synchronized (this) {
						this.aktiv = resultat.getData().getUnscaledValue(
								"DEKanalStatus").intValue() == 0; //$NON-NLS-1$
						this.deFehlerStatus = TlsDeFehlerStatus
								.getZustand(resultat
										.getData()
										.getUnscaledValue("DEFehlerStatus").intValue()); //$NON-NLS-1$
						for (ITlsGloDeFehlerListener listener : this.listenerMenge) {
							listener.aktualisiereTlsGloDeFehler(this.aktiv,
									this.deFehlerStatus);
						}
					}
				}
			}
		}
	}

}
