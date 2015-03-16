/**
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

package de.bsvrz.dua.fehlertls.parameter;

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
import de.bsvrz.sys.funclib.bitctrl.daf.DaVKonstanten;

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
	private final Set<ITlsGloKanalSteuerungsListener> listenerMenge = Collections
			.synchronizedSet(new HashSet<ITlsGloKanalSteuerungsListener>());

	/**
	 * Indiziert, dass der TLS-Kanalstatus auf <code>aktiv</code> steht.
	 */
	private Boolean aktiv;

	/**
	 * Erfragt eine statische Instanz dieser Klasse.
	 *
	 * @param dav
	 *            Verbindung zum Datenverteiler
	 * @param objekt
	 *            ein Objekt vom Typ <code>typ.de</code>
	 * @return eine statische Instanz dieser Klasse oder <code>null</code>
	 */
	public static TlsGloKanalSteuerung getInstanz(final ClientDavInterface dav,
			final SystemObject objekt) {
		TlsGloKanalSteuerung instanz = null;

		synchronized (TlsGloKanalSteuerung.instanzen) {
			instanz = TlsGloKanalSteuerung.instanzen.get(objekt);
		}

		if (instanz == null) {
			instanz = new TlsGloKanalSteuerung(dav, objekt);
			synchronized (TlsGloKanalSteuerung.instanzen) {
				TlsGloKanalSteuerung.instanzen.put(objekt, instanz);
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
	private TlsGloKanalSteuerung(final ClientDavInterface dav,
			final SystemObject objekt) {
		dav.subscribeReceiver(
				this,
				objekt,
				new DataDescription(dav.getDataModel().getAttributeGroup(
						"atg.tlsGloKanalSteuerung"), //$NON-NLS-1$
						dav.getDataModel().getAspect(
								DaVKonstanten.ASP_PARAMETER_SOLL)),
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
		if (listenerMenge.add(listener) && (this.aktiv != null)) {
			listener.aktualisiereTlsGloKanalSteuerung(this.aktiv);
		}
	}

	@Override
	public void update(final ResultData[] resultate) {
		if (resultate != null) {
			for (final ResultData resultat : resultate) {
				if ((resultat != null) && (resultat.getData() != null)) {
					synchronized (this) {
						this.aktiv = resultat.getData()
								.getUnscaledValue("DEKanalStatus").intValue() == 0;
						for (final ITlsGloKanalSteuerungsListener listener : this.listenerMenge) {
							listener.aktualisiereTlsGloKanalSteuerung(this.aktiv);
						}
					}
				}
			}
		}
	}
}
