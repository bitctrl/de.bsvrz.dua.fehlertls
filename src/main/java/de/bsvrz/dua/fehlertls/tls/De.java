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

package de.bsvrz.dua.fehlertls.tls;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import com.bitctrl.Constants;

import de.bsvrz.dav.daf.main.ClientDavInterface;
import de.bsvrz.dav.daf.main.ClientReceiverInterface;
import de.bsvrz.dav.daf.main.ClientSenderInterface;
import de.bsvrz.dav.daf.main.Data;
import de.bsvrz.dav.daf.main.DataDescription;
import de.bsvrz.dav.daf.main.DataNotSubscribedException;
import de.bsvrz.dav.daf.main.OneSubscriptionPerSendData;
import de.bsvrz.dav.daf.main.ReceiveOptions;
import de.bsvrz.dav.daf.main.ReceiverRole;
import de.bsvrz.dav.daf.main.ResultData;
import de.bsvrz.dav.daf.main.SendSubscriptionNotConfirmed;
import de.bsvrz.dav.daf.main.SenderRole;
import de.bsvrz.dav.daf.main.config.SystemObject;
import de.bsvrz.dua.fehlertls.de.DeFaException;
import de.bsvrz.dua.fehlertls.de.DeTypLader;
import de.bsvrz.dua.fehlertls.enums.TlsFehlerAnalyse;
import de.bsvrz.dua.fehlertls.fehlertls.DeFaApplikation;
import de.bsvrz.dua.fehlertls.parameter.IParameterTlsFehlerAnalyseListener;
import de.bsvrz.dua.fehlertls.parameter.ParameterTlsFehlerAnalyse;
import de.bsvrz.dua.fehlertls.tls.DeErfassungsZustand.Zustand;
import de.bsvrz.sys.funclib.bitctrl.dua.DUAKonstanten;
import de.bsvrz.sys.funclib.bitctrl.dua.ObjektWecker;
import de.bsvrz.sys.funclib.bitctrl.dua.schnittstellen.IObjektWeckerListener;
import de.bsvrz.sys.funclib.debug.Debug;
import de.bsvrz.sys.funclib.operatingMessage.MessageGrade;

/**
 * Repraesentiert ein DE fuer die DeFa. Liest alle generischen DE-Parameter und
 * meldet sich auf alle Daten an, auf die von dem DE gewartet werden soll.
 *
 * @author BitCtrl Systems GmbH, Thierfelder
 *
 * @version $Id$
 */
public class De extends TlsHierarchieElement implements
ClientReceiverInterface, ClientSenderInterface, IObjektWeckerListener,
IDeErfassungsZustandListener, IParameterTlsFehlerAnalyseListener {

	private static final Debug LOGGER = Debug.getLogger();

	/**
	 * Die Zeit, die mindestens zwischen Daten, Fehlererkennung und
	 * Fehleranalyse vergehen muss.
	 */
	private static final long STANDARD_ZEIT_ABSTAND = 1000L;

	/**
	 * weckt alle Objekte dieser Art, wenn fuer sie ein Fehler detektiert wurde.
	 */
	private static ObjektWecker fehlerWecker = new ObjektWecker();

	/**
	 * weckt alle Objekte dieser Art, wenn fuer sie ein Fehler analysiert
	 * werden. soll.
	 */
	private static ObjektWecker analyseWecker = new ObjektWecker();

	/**
	 * <code>atg.tlsFehlerAnalyse</code>, <code>asp.analyse</code>.
	 */
	private static DataDescription fehlerDatenBeschreibung = null;

	/**
	 * Der zusätzliche Zeitverzug, der nach dem erwarteten Empfangszeitpunkt
	 * noch bis zur Erkennung eines nicht gelieferten Messwertes abgewartet
	 * werden muss.
	 */
	private long zeitVerzugFehlerErkennung = Long.MIN_VALUE;

	/**
	 * Der zusätzliche Zeitverzug, der nach der Fehlererkennung bis zur
	 * Fehlerermittlung abgewartet werden muss.
	 */
	private long zeitVerzugFehlerErmittlung = Long.MIN_VALUE;

	/**
	 * als letztes wurde fuer diesen Zeitstempel (Datenzeit) ein Nutzdatum von
	 * diesem DE erwartet.
	 */
	private long letzterErwarteterDatenZeitpunkt = -1;

	/**
	 * Zeitstempel des letzten fuer dieses DE publizierten Fehlers.
	 */
	private long zeitStempelLetzterPublizierterFehler = -1;

	/**
	 * erfragt, ob dieses DE zur Zeit "in Time" ist.
	 */
	private boolean inTime = true;

	/**
	 * aktueller Erfassungszustand bzgl. der DeFa.
	 */
	private DeErfassungsZustand.Zustand aktuellerZustand = null;

	private final boolean initialisiert;

	/**
	 * Standardkonstruktor.
	 *
	 * @param dav
	 *            Datenverteiler-Verbindung
	 * @param objekt
	 *            ein Systemobjekt vom Typ <code>typ.de</code>
	 * @param vater
	 *            das in der TLS-Hierarchie ueber diesem Geraet liegende Geraet
	 * @throws DeFaException
	 *             wird nach oben weitergereicht
	 */
	protected De(final ClientDavInterface dav, final SystemObject objekt,
			final TlsHierarchieElement vater) throws DeFaException {
		super(dav, objekt, vater);

		if (De.fehlerDatenBeschreibung == null) {
			De.fehlerDatenBeschreibung = new DataDescription(dav.getDataModel()
					.getAttributeGroup("atg.tlsFehlerAnalyse"), //$NON-NLS-1$
					dav.getDataModel().getAspect("asp.analyse")); //$NON-NLS-1$
		}

		for (DataDescription messWertBeschreibung : DeTypLader.getDeTyp(
				objekt.getType()).getDeFaMesswertDataDescriptions(dav)) {
			dav.subscribeReceiver(this, objekt, messWertBeschreibung,
					ReceiveOptions.normal(), ReceiverRole.receiver());
			De.LOGGER
					.info("Ueberwache " + this.objekt.getPid() + ", " + messWertBeschreibung); //$NON-NLS-1$//$NON-NLS-2$
		}

		try {
			dav.subscribeSender(this, objekt, De.fehlerDatenBeschreibung,
					SenderRole.source());
		} catch (OneSubscriptionPerSendData e) {
			throw new IllegalStateException("Quellenanmeldung für DE" + objekt
					+ " nicht möglich", e);
		}

		new DeErfassungsZustand(TlsHierarchieElement.sDav, this.getObjekt())
				.addListener(this);
		ParameterTlsFehlerAnalyse.getInstanz(dav,
				DeFaApplikation.getTlsFehlerAnalyseObjekt()).addListener(this);
		initialisiert = true;
	}

	@Override
	public void update(final ResultData[] resultate) {
		if (resultate != null) {
			for (ResultData resultat : resultate) {
				if ((resultat != null) && (resultat.getData() != null)) {
					this.inTime = true;
					this.versucheErwartung();
				}
			}
		}
	}

	@Override
	public Art getGeraeteArt() {
		return Art.DE;
	}

	/**
	 * Publiziert eine erkannte Fehlerursache an diesem DE.
	 *
	 * @param fehlerZeit
	 *            die Zeit mit der der Fehler assoziiert ist (Die Zeit, zu der
	 *            ausgefallene Datensatz erwartet wurde)
	 * @param tlsFehler
	 *            die Fehlerursache
	 */
	public final void publiziereFehlerUrsache(final long fehlerZeit,
			final TlsFehlerAnalyse tlsFehler) {
		this.zeitStempelLetzterPublizierterFehler = fehlerZeit;

		Data datum = TlsHierarchieElement.sDav
				.createData(De.fehlerDatenBeschreibung.getAttributeGroup());
		datum.getUnscaledValue("TlsFehlerAnalyse").set(tlsFehler.getCode());
		try {
			TlsHierarchieElement.sDav.sendData(new ResultData(this.objekt,
					De.fehlerDatenBeschreibung, fehlerZeit, datum));
		} catch (DataNotSubscribedException e) {
			De.LOGGER.error("Datum " + datum + " konnte fuer " + this.objekt
					+ " nicht publiziert werden. Grund:\n"
					+ e.getLocalizedMessage());
		} catch (SendSubscriptionNotConfirmed e) {
			De.LOGGER.error("Datum " + datum + " konnte fuer " + this.objekt
					+ " nicht publiziert werden. Grund:\n"
					+ e.getLocalizedMessage());
		}

		this.versucheErwartung();
	}

	/**
	 * Erfragt den aktuellen Erfassungszustand dieses DE.
	 *
	 * @return der aktuellen Erfassungszustand dieses DE
	 */
	public final synchronized DeErfassungsZustand.Zustand getZustand() {
		return this.aktuellerZustand;
	}

	/**
	 * Erfragt, ob dieses DE im Moment Daten im Sinne der DeFa hat (Also ob
	 * Daten vorhanden sind, und ob diese rechtzeitig angekommen sind).
	 *
	 * @return ob dieses DE im Moment Daten im Sinne der DeFa hat
	 */
	public final boolean isInTime() {
		return this.inTime;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean kannFehlerHierPublizieren(final long zeitStempel) {
		return zeitStempel > this.zeitStempelLetzterPublizierterFehler;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void publiziereFehler(final long zeitStempel) {
		this.publiziereFehlerUrsache(zeitStempel, TlsFehlerAnalyse.UNBEKANNT);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public synchronized void aktualisiereParameterTlsFehlerAnalyse(
			final long zeitverzugFehlerErkennung,
			final long zeitverzugFehlerErmittlung) {
		this.zeitVerzugFehlerErkennung = zeitverzugFehlerErkennung;
		this.zeitVerzugFehlerErmittlung = zeitverzugFehlerErmittlung;

		this.versucheErwartung();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public synchronized void aktualisiereErfassungsZustand(final Zustand zustand) {
		this.aktuellerZustand = zustand;
		this.versucheErwartung();
	}

	/**
	 * Initiiert die Erwartung eines Nutzdatums dieses DE, wenn dies aufgrund
	 * der aktuellen Parameter bzw. Online-Daten des DE moeglich bzw. notwendig
	 * ist.
	 */
	private synchronized void versucheErwartung() {

		if (!initialisiert) {
			return;
		}

		if (this.zeitVerzugFehlerErkennung >= 0) {

			if ((this.aktuellerZustand != null)
					&& (this.aktuellerZustand.getErfassungsIntervallDauer() > 0)) {

				this.letzterErwarteterDatenZeitpunkt = De
						.getNaechstenIntervallZeitstempel(System
								.currentTimeMillis(), this.aktuellerZustand
								.getErfassungsIntervallDauer());
				long nachsterErwarteterZeitpunkt = this.letzterErwarteterDatenZeitpunkt
						+ zeitVerzugFehlerErkennung;

				De.LOGGER.info("Plane Erwartung fuer "
						+ De.this.getObjekt()
						+ ": "
						+ DUAKonstanten.ZEIT_FORMAT_GENAU.format(new Date(
								nachsterErwarteterZeitpunkt
								+ De.STANDARD_ZEIT_ABSTAND)));
				De.fehlerWecker.setWecker(this, nachsterErwarteterZeitpunkt
						+ De.STANDARD_ZEIT_ABSTAND);
			} else {
				if ((this.aktuellerZustand != null)
						&& this.aktuellerZustand.isInitialisiert()) {
					if (this.aktuellerZustand.getErfassungsIntervallDauer() <= 0) {
						De.LOGGER.info("Erwartung fuer " + De.this.getObjekt()
								+ " ausgeplant.");
						De.fehlerWecker.setWecker(this, ObjektWecker.AUS);
						this.einzelPublikator.publiziere(MessageGrade.WARNING,
								this.objekt, this.objekt + ": "
										+ this.aktuellerZustand.getGrund());
					}
				} else {
					if (this.aktuellerZustand == null) {
						De.LOGGER.info("Aktueller Erfassungszustand von "
								+ De.this.objekt + " ist (noch) nicht bekannt");
					} else {
						De.LOGGER
								.info("DE "
										+ De.this.objekt
										+ " ist (noch) nicht vollstaendig initialisiert:\n"
										+ this.aktuellerZustand);
					}
				}
			}

		} else {
			De.LOGGER.warning("Kann keine Daten fuer " + this.objekt + //$NON-NLS-1$
					" erwarten, da noch keine (sinnvollen) " + //$NON-NLS-1$
					"Parameter zur TLS-Fehleranalyse empfangen wurden"); //$NON-NLS-1$
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void dataRequest(final SystemObject object,
			final DataDescription dataDescription, final byte state) {
		if (state == ClientSenderInterface.STOP_SENDING_NOT_A_VALID_SUBSCRIPTION) {
			De.LOGGER.error("SWE wird beendet, weil die Quellenanmeldung für "
					+ object + ": " + dataDescription + " ungültig ist");
			System.exit(-1);
		} else if (state == ClientSenderInterface.STOP_SENDING_NO_RIGHTS) {
			De.LOGGER
					.error("SWE wird beendet, weil sie keine Rechte für die Quellenanmeldung von "
							+ object + ": " + dataDescription + " hat");
			System.exit(-1);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isRequestSupported(final SystemObject object,
			final DataDescription dataDescription) {
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void alarm() {
		/**
		 * Ueberpruefe Bedingungen nach Afo-9.0 DUA BW C1C2-21 (S. 45)
		 */

		DeErfassungsZustand.Zustand zustand = De.this.aktuellerZustand;

		if (zustand.isErfasst()) {
			De.this.inTime = false;
			final long fehlerZeit = De.this.letzterErwarteterDatenZeitpunkt;

			De.LOGGER.info("Plane Fehlerpublikation fuer "
					+ De.this.getObjekt()
					+ ": "
					+ DUAKonstanten.ZEIT_FORMAT_GENAU.format(new Date(
							fehlerZeit + zeitVerzugFehlerErkennung
							+ zeitVerzugFehlerErmittlung
									+ (2 * De.STANDARD_ZEIT_ABSTAND)))
									+ "\nFehlerzeit: "
									+ DUAKonstanten.ZEIT_FORMAT_GENAU.format(new Date(
											fehlerZeit)) + "\nVerzug (Erkennung): "
											+ zeitVerzugFehlerErkennung + "\nVerzug (Ermittlung): "
											+ zeitVerzugFehlerErmittlung + "\nZusatzverzug: "
											+ (2 * De.STANDARD_ZEIT_ABSTAND));

			De.analyseWecker.setWecker(new IObjektWeckerListener() {

				@Override
				public void alarm() {
					if (!De.this.inTime) {
						versucheFehlerPublikation(fehlerZeit);
					}
				}

			}, fehlerZeit + zeitVerzugFehlerErkennung
			+ zeitVerzugFehlerErmittlung
					+ (2 * De.STANDARD_ZEIT_ABSTAND));
		} else {
			if (zustand.isInitialisiert()) {
				De.this.einzelPublikator.publiziere(MessageGrade.WARNING,
						this.objekt, this.objekt + ": " + zustand.getGrund());
			} else {
				De.LOGGER.warning(De.this.objekt
						+ " ist (noch) nicht vollstaendig initialisiert"); //$NON-NLS-1$
			}
		}
	}

	/**
	 * Erfragt den ersten Zeitstempel, der sich echt (> 500ms) nach dem
	 * Zeitstempel <code>jetzt</code> (angenommenr Jetzt-Zeitpunkt) befindet und
	 * der zur uebergebenen Erfassungsintervalllange passt.
	 *
	 * @param jetzt
	 *            angenommener Jetzt-Zeitpunkt (in ms)
	 * @param intervallLaenge
	 *            eine Erfassungsintervalllaenge (in ms)
	 * @return der ersten Zeitstempel, der sich echt nach dem Zeitstempel
	 *         <code>jetzt</code> befindet und der zur uebergebenen
	 *         Erfassungsintervalllange passt (in ms)
	 */
	private static long getNaechstenIntervallZeitstempel(final long jetzt,
			final long intervallLaenge) {
		final long jetztPlus = jetzt + 500L;
		GregorianCalendar stundenAnfang = new GregorianCalendar();
		stundenAnfang.setTimeInMillis(jetztPlus);
		stundenAnfang.set(Calendar.MINUTE, 0);
		stundenAnfang.set(Calendar.SECOND, 0);
		stundenAnfang.set(Calendar.MILLISECOND, 0);

		long naechsterIntervallZeitstempel;
		if (intervallLaenge >= Constants.MILLIS_PER_HOUR) {
			stundenAnfang.set(Calendar.HOUR_OF_DAY, 0);
			final long msNachTagesAnfang = jetztPlus
					- stundenAnfang.getTimeInMillis();
			final long intervalleSeitTagesAnfang = msNachTagesAnfang
					/ intervallLaenge;
			naechsterIntervallZeitstempel = stundenAnfang.getTimeInMillis()
					+ ((intervalleSeitTagesAnfang + 1) * intervallLaenge);
		} else {
			final long msNachStundenAnfang = jetztPlus
					- stundenAnfang.getTimeInMillis();
			final long intervalleSeitStundenAnfang = msNachStundenAnfang
					/ intervallLaenge;
			naechsterIntervallZeitstempel = stundenAnfang.getTimeInMillis()
					+ ((intervalleSeitStundenAnfang + 1) * intervallLaenge);
		}

		return naechsterIntervallZeitstempel;
	}

}
