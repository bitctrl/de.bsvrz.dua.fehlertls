/**
 * Segment 4 Daten�bernahme und Aufbereitung (DUA), SWE 4.DeFa DE Fehleranalyse fehlende Messdaten
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
 * Wei�enfelser Stra�e 67<br>
 * 04229 Leipzig<br>
 * Phone: +49 341-490670<br>
 * mailto: info@bitctrl.de
 */

package de.bsvrz.dua.fehlertls.tls;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.bitctrl.Constants;

import de.bsvrz.dav.daf.main.ClientDavInterface;
import de.bsvrz.dav.daf.main.config.SystemObject;
import de.bsvrz.dua.fehlertls.de.DeFaException;
import de.bsvrz.dua.fehlertls.enums.TlsDeFehlerStatus;
import de.bsvrz.dua.fehlertls.online.ITlsGloDeFehlerListener;
import de.bsvrz.dua.fehlertls.online.TlsGloDeFehler;
import de.bsvrz.dua.fehlertls.parameter.IZyklusSteuerungsParameterListener;
import de.bsvrz.dua.fehlertls.parameter.ZyklusSteuerungsParameter;
import de.bsvrz.sys.funclib.bitctrl.dua.DUAKonstanten;
import de.bsvrz.sys.funclib.debug.Debug;

/**
 * Ueberwacht den Erfassungszustand eines DE bezueglich der DeFa. Dieser Zustand
 * kann die Werte <code>erfasst</code> und <code>nicht erfasst</code> annehmen
 *
 * @author BitCtrl Systems GmbH, Thierfelder
 *
 * @version $Id$
 */
public class DeErfassungsZustand implements ITlsGloDeFehlerListener,
		IZyklusSteuerungsParameterListener {

	private static final Debug LOGGER = Debug.getLogger();

	/**
	 * GRUND_PRAEFIX.
	 */
	protected static final String GRUND_PRAEFIX = "Keine TLS-Fehleranalyse moeglich. ";

	/**
	 * indiziert, dass der TLS-Kanalstatus auf <code>aktiv</code> steht.
	 */
	private Boolean aktiv;

	/**
	 * TLS-DE-Fehler-Status.
	 */
	private TlsDeFehlerStatus deFehlerStatus;

	/**
	 * die entsprechende Erassungsintervalldauer (in ms), wenn das DE auf
	 * zyklischen Abruf parametriert ist und -1 sonst.
	 */
	private Long erfassungsIntervallDauer;

	/**
	 * aktueller Erfassungszustand bzgl. der DeFa des mit dieser Instanz
	 * assoziierten DE.
	 */
	private Zustand aktuellerZustand;

	/**
	 * Menge aller Listener dieses Objektes.
	 */
	private final Set<IDeErfassungsZustandListener> listenerMenge = new HashSet<IDeErfassungsZustandListener>();

	/**
	 * das erfasste DE.
	 */
	private final SystemObject obj;

	/**
	 * Standardkonstruktor.
	 *
	 * @param dav
	 *            Datenverteiler-Verbindung
	 * @param objekt
	 *            ein durch diese Instanz zu ueberwachendes DE
	 * @throws DeFaException
	 *             wird geworfen, wenn es Probleme beim Laden oder Instanziieren
	 *             der Klasse gibt, die den erfragten DE-Typ beschreibt
	 */
	public DeErfassungsZustand(final ClientDavInterface dav,
			final SystemObject objekt) throws DeFaException {
		this.obj = objekt;
		this.aktuellerZustand = new Zustand();
		TlsGloDeFehler.getInstanz(dav, objekt).addListener(this);
		ZyklusSteuerungsParameter.getInstanz(dav, objekt).addListener(this);
		DeErfassungsZustand.LOGGER.info("DeFa-Zustand von " + objekt
				+ " wird ab sofort ueberwacht");
	}

	@Override
	public void aktualisiereTlsGloDeFehler(final boolean aktiv1,
			final TlsDeFehlerStatus deFehlerStatus1) {
		synchronized (this) {
			this.aktiv = aktiv1;
			this.deFehlerStatus = deFehlerStatus1;
			informiereListener();
		}
	}

	@Override
	public void aktualisiereZyklusSteuerungsParameter(
			final long erfassungsIntervallDauer1) {
		synchronized (this) {
			this.erfassungsIntervallDauer = erfassungsIntervallDauer1;
			informiereListener();
		}
	}

	/**
	 * Informiert alle Listener ueber eine Veraenderung des Erfassungszustandes
	 * dieses Objektes.
	 */
	private void informiereListener() {
		synchronized (this) {
			final Zustand neuerZustand = new Zustand();
			if (!neuerZustand.equals(this.aktuellerZustand)) {
				this.aktuellerZustand = neuerZustand;
				for (final IDeErfassungsZustandListener listener : this.listenerMenge) {
					listener.aktualisiereErfassungsZustand(this.aktuellerZustand);
				}
			}
		}
	}

	// /**
	// * Erfragt den Erfassungszustand des durch diese Instanz ueberwachten DE
	// in
	// * Bezug auf die DeFa.
	// *
	// * @return der Erfassungszustand des durch diese Instanz ueberwachten DE
	// in
	// * Bezug auf die DeFa
	// */
	// public final DeErfassungsZustand.Zustand getZustand() {
	// return new Zustand();
	// }

	/**
	 * Fuegt diesem Objekt einen neuen Listener hinzu und informiert diesen
	 * sofort ueber den aktuellen Zustand dieses Objektes.
	 *
	 * @param listener
	 *            ein neuer Listener
	 */
	public final synchronized void addListener(
			final IDeErfassungsZustandListener listener) {
		if (this.listenerMenge.add(listener)) {
			listener.aktualisiereErfassungsZustand(this.aktuellerZustand);
		}
	}

	/**
	 * Repraesentiert den Erfassungszustand dieses DE bezueglich der DeFa.
	 * Dieser Zustand kann die Werte <code>erfasst</code> und
	 * <code>nicht erfasst</code> annehmen
	 *
	 * @author BitCtrl Systems GmbH, Thierfelder
	 *
	 */
	public class Zustand {

		/**
		 * indiziert, das die Parameter dieses DE initialisiert wurden.
		 */
		private boolean initialisiert = true;

		/**
		 * die entsprechende Erassungsintervalldauer (in ms), wenn das DE auf
		 * zyklischen Abruf parametriert ist und -1 sonst.
		 */
		private long intervallDauer = -1;

		/**
		 * Grund fuer die Tatsache, dass dieser Zustand den Wert nicht
		 * <code>nicht erfasst</code> hat.
		 */
		private String grund;

		/**
		 * Standardkonstruktor.
		 */
		protected Zustand() {
			synchronized (DeErfassungsZustand.this) {
				if (DeErfassungsZustand.this.deFehlerStatus != null) {
					if (DeErfassungsZustand.this.deFehlerStatus == TlsDeFehlerStatus.OK) {
						if (DeErfassungsZustand.this.aktiv != null) {
							if (DeErfassungsZustand.this.aktiv) {
								if (DeErfassungsZustand.this.erfassungsIntervallDauer != null) {
									if (DeErfassungsZustand.this.erfassungsIntervallDauer >= 0) {
										this.intervallDauer = DeErfassungsZustand.this.erfassungsIntervallDauer;
									} else {
										this.grund = "TLS-Fehlerueberwachung nicht moeglich, da keine "
												+ "zyklische Abgabe von Meldungen eingestellt";
									}
								} else {
									this.initialisiert = false;
								}
							} else {
								this.grund = DeErfassungsZustand.GRUND_PRAEFIX
										+ "DE-Kanal ist passiviert";
							}
						} else {
							this.initialisiert = false;
						}
					} else {
						this.grund = DeErfassungsZustand.GRUND_PRAEFIX
								+ "DE-Fehler("
								+ DeErfassungsZustand.this.deFehlerStatus
										.toString()
								+ "): "
								+ DeErfassungsZustand.this.deFehlerStatus
										.getText();
					}
				} else {
					this.initialisiert = false;
				}
			}
		}

		/**
		 * Erfragt den Grund fuer die Tatsache, dass dieser Zustand den Wert
		 * nicht <code>nicht erfasst</code> hat<br>
		 * .
		 *
		 * @return Grund fuer die Tatsache, dass dieser Zustand den Wert nicht
		 *         <code>nicht erfasst</code> hat oder <code>null</code>, wenn
		 *         dieser Zustand auf <code>erfasst</code> steht bzw. die
		 *         Parameter noch nicht initialisiert wurden
		 */
		public final String getGrund() {
			return this.grund;
		}

		/**
		 * Erfragt, ob die Parameter dieses DE initialisiert bereits wurden (der
		 * Zustand kann auch auf <code>nicht erfasst</code> stehen, wenn noch
		 * keine Initialisierung stattgefunden hat).
		 *
		 * @return ob die Parameter dieses DE initialisiert bereits wurden
		 */
		public final boolean isInitialisiert() {
			return this.initialisiert;
		}

		/**
		 * Erfragt die aktuelle Erfassungsintervalldauer.
		 *
		 * @return die aktuelle Erfassungsintervalldauer
		 */
		public final long getErfassungsIntervallDauer() {
			return this.intervallDauer;
		}

		/**
		 * Erfragt den Erfassungszustand dieses DE bezueglich der DeFa. Dieser
		 * Zustand kann die Werte <code>erfasst</code> und
		 * <code>nicht erfasst</code> annehmen. Der Zustand <code>erfasst</code>
		 * wird angenommen wenn f�r dieses DE gilt:<br>
		 * 1.) es liegt aktuell kein DE-Fehler vor,<br>
		 * 2.) der DE-Kanalstatus hat den Wert <code>aktiv</code> und<br>
		 * 3.) die Erfassungsart ist auf
		 * <code>Zyklische Abgabe von Meldungen</code> gesetzt.<br>
		 * Sonst wird der Wert <code>nicht erfasst</code> angenommen
		 *
		 * @return ob dieses DE im Sinne der DeFa als <code>erfasst</code> gilt
		 */
		public final boolean isErfasst() {
			return this.intervallDauer >= 0;
		}

		@Override
		public boolean equals(final Object obj1) {
			boolean gleich = false;

			if ((obj1 != null) && (obj1 instanceof Zustand)) {
				final Zustand that = (Zustand) obj1;
				gleich = (this.initialisiert == that.initialisiert)
						&& (this.intervallDauer == that.intervallDauer);
				if (gleich) {
					if ((this.grund != null) && (that.grund != null)) {
						gleich &= this.grund.equals(that.grund);
					} else {
						gleich &= (this.grund == null) && (that.grund == null);
					}
				}
			}

			return gleich;
		}

		@Override
		public String toString() {
			String s = Constants.EMPTY_STRING;

			if (initialisiert) {
				if (this.intervallDauer >= 0) {
					s += "erfasst (Intervalldauer: "
							+ new SimpleDateFormat(
									DUAKonstanten.ZEIT_FORMAT_GENAU_STR)
									.format(new Date(this.intervallDauer))
							+ ")";
				} else {
					s += this.grund;
				}
			} else {
				s += "nicht initialisiert (T = " + intervallDauer + ", Grund: "
						+ this.grund + ")";
			}

			return s;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = (prime * result) + getOuterType().hashCode();
			result = (prime * result)
					+ ((grund == null) ? 0 : grund.hashCode());
			result = (prime * result) + (initialisiert ? 1231 : 1237);
			result = (prime * result)
					+ (int) (intervallDauer ^ (intervallDauer >>> 32));
			return result;
		}

		private DeErfassungsZustand getOuterType() {
			return DeErfassungsZustand.this;
		}

	}

}
