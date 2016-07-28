/*
 * Copyright 2016 by Kappich Systemberatung Aachen
 * 
 * This file is part of de.bsvrz.dua.fehlertls.tests.
 * 
 * de.bsvrz.dua.fehlertls.tests is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * de.bsvrz.dua.fehlertls.tests is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with de.bsvrz.dua.fehlertls.tests.  If not, see <http://www.gnu.org/licenses/>.

 * Contact Information:
 * Kappich Systemberatung
 * Martin-Luther-Straße 14
 * 52062 Aachen, Germany
 * phone: +49 241 4090 436 
 * mail: <info@kappich.de>
 */

package de.bsvrz.dua.fehlertls.tests;

import de.bsvrz.dav.daf.main.*;
import de.bsvrz.dav.daf.main.config.SystemObject;
import de.bsvrz.dua.fehlertls.enums.TlsFehlerAnalyse;

import java.util.*;

/**
 * Assoziiert mit DE-Daten von <code>atg.tlsFehlerAnalyse</code>,
 * <code>asp.analyse</code>.
 * 
 * @author BitCtrl Systems GmbH, Thierfelder
 */
public final class AtgTlsFehlerAnalyse implements ClientReceiverInterface {

	/**
	 * statische Instanzen dieser Klasse.
	 */
	private static Map<SystemObject, AtgTlsFehlerAnalyse> instanzen = new HashMap<SystemObject, AtgTlsFehlerAnalyse>();

	/**
	 * Listenermenge.
	 */
	private Set<IAtgTlsFehlerAnalyseListener> listenerMenge = Collections
			.synchronizedSet(new HashSet<IAtgTlsFehlerAnalyseListener>());

	/**
	 * aktueller Fehler.
	 */
	private TlsFehlerAnalyse aktuellerFehler = null;

	/**
	 * Erfragt eine statische Instanz dieser Klasse.
	 * 
	 * @param obj
	 *            ein DE-Objekt
	 * @param dav
	 * @return eine statische Instanz dieser Klasse
	 * @throws Exception
	 *             wird weitergereicht
	 */
	public static AtgTlsFehlerAnalyse getInstanz(SystemObject obj, final ClientDavInterface dav)
			throws Exception {
		AtgTlsFehlerAnalyse instanz = instanzen.get(obj);

		if (instanz == null) {
			instanz = new AtgTlsFehlerAnalyse(obj, dav);
			instanzen.put(obj, instanz);
		}

		return instanz;
	}

	/**
	 * Standardkonstruktor.
	 * 
	 * @param obj
	 *            ein DE-Objekt
	 * @param dav
	 * @throws Exception
	 *             wird weitergereicht
	 */
	private AtgTlsFehlerAnalyse(SystemObject obj, final ClientDavInterface dav) throws Exception {
		DataDescription datenBeschreibung = new DataDescription(dav.getDataModel().getAttributeGroup(
						"atg.tlsFehlerAnalyse"), //$NON-NLS-1$
				dav.getDataModel().getAspect("asp.analyse")); //$NON-NLS-1$
		dav.subscribeReceiver(this, obj, datenBeschreibung,
				ReceiveOptions.normal(), ReceiverRole.receiver());
	}

	/**
	 * Fuegt Listener hinzu.
	 * 
	 * @param listener
	 *            neuer Listener
	 */
	public synchronized void addListener(
			IAtgTlsFehlerAnalyseListener listener) {
		if (this.listenerMenge.add(listener) && this.aktuellerFehler != null) {
			listener.aktualisiereTlsFehlerAnalyse(this.aktuellerFehler);
		}
	}

	public void update(ResultData[] results) {
		if (results != null) {
			for (ResultData result : results) {
				if (result != null && result.getData() != null) {
					synchronized (this) {
						this.aktuellerFehler = TlsFehlerAnalyse
								.getZustand(result.getData().getUnscaledValue(
										"TlsFehlerAnalyse").intValue()); //$NON-NLS-1$
						for (IAtgTlsFehlerAnalyseListener listener : this.listenerMenge) {
							listener
									.aktualisiereTlsFehlerAnalyse(this.aktuellerFehler);
						}
					}
				}
			}
		}
	}

}
