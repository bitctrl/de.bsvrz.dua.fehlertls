/**
 * Segment 4 Daten�bernahme und Aufbereitung (DUA), SWE 4.DeFa DE Fehleranalyse fehlende Messdaten
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
 * Wei�enfelser Stra�e 67<br>
 * 04229 Leipzig<br>
 * Phone: +49 341-490670<br>
 * mailto: info@bitctrl.de
 */

package de.bsvrz.dua.fehlertls.online;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import de.bsvrz.dav.daf.main.ClientDavInterface;
import de.bsvrz.dav.daf.main.ClientReceiverInterface;
import de.bsvrz.dav.daf.main.DataDescription;
import de.bsvrz.dav.daf.main.ReceiveOptions;
import de.bsvrz.dav.daf.main.ReceiverRole;
import de.bsvrz.dav.daf.main.ResultData;
import de.bsvrz.dav.daf.main.config.SystemObject;
import de.bsvrz.dua.fehlertls.enums.TlsDeFehlerStatus;
import de.bsvrz.sys.funclib.bitctrl.dua.DUAKonstanten;

/**
 * Korrespondiert mit der Online-Attributgruppe <code>atg.tlsGloDeFehler</code>.
 * 
 * @author BitCtrl Systems GmbH, Thierfelder
 * 
 * @version $Id$
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
				dav.getDataModel().getAspect(DUAKonstanten.ASP_TLS_ANTWORT),
				(short) 0), ReceiveOptions.normal(), ReceiverRole.receiver());
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
