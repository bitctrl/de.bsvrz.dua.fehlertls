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

package de.bsvrz.dua.fehlertls.tls;

import de.bsvrz.dav.daf.main.*;
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
import de.bsvrz.sys.funclib.operatingMessage.MessageTemplate;
import de.bsvrz.sys.funclib.operatingMessage.MessageType;
import de.bsvrz.sys.funclib.operatingMessage.OperatingMessage;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Repraesentiert ein DE fuer die DeFa. Liest alle generischen DE-Parameter und
 * meldet sich auf alle Daten an, auf die von dem DE gewartet werden soll.
 * 
 * @author BitCtrl Systems GmbH, Thierfelder
 */
public class De extends AbstraktGeraet implements ClientReceiverInterface,
		ClientSenderInterface, IObjektWeckerListener,
		IDeErfassungsZustandListener, IParameterTlsFehlerAnalyseListener {

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
	
	private static final MessageTemplate MESSAGE_TEMPLATE = new MessageTemplate(
			MessageGrade.WARNING,
			MessageType.APPLICATION_DOMAIN,
			MessageTemplate.object(),
			MessageTemplate.fixed(": "),
			MessageTemplate.variable("reason"),
			MessageTemplate.fixed(". "),
			MessageTemplate.ids()
	).withIdFactory(message -> message.getObject().getPidOrId() + " [DUA-FT-FU]");

	/**
	 * Standardkonstruktor.
	 * 
	 * @param dav
	 *            Datenverteiler-Verbindund
	 * @param objekt
	 *            ein Systemobjekt vom Typ <code>typ.de</code>
	 * @param vater
	 *            das in der TLS-Hierarchie ueber diesem Geraet liegende Geraet
	 * @throws DeFaException
	 *             wird nach oben weitergereicht
	 */
	protected De(ClientDavInterface dav, SystemObject objekt,
			AbstraktGeraet vater) throws DeFaException {
		super(dav, objekt, vater);

		if (fehlerDatenBeschreibung == null) {
			fehlerDatenBeschreibung = new DataDescription(dav.getDataModel()
					.getAttributeGroup("atg.tlsFehlerAnalyse"), //$NON-NLS-1$
					dav.getDataModel().getAspect("asp.analyse")); //$NON-NLS-1$
		}

		ParameterTlsFehlerAnalyse.getInstanz(dav,
				DeFaApplikation.getTlsFehlerAnalyseObjekt()).addListener(this);

		for (DataDescription messWertBeschreibung : DeTypLader.getDeTyp(
				objekt.getType()).getDeFaMesswertDataDescriptions(dav)) {
			dav.subscribeReceiver(this, objekt, messWertBeschreibung,
					ReceiveOptions.normal(), ReceiverRole.receiver());
			Debug
					.getLogger()
					.info(
							"Ueberwache " + this.objekt.getPid() + ", " + messWertBeschreibung); //$NON-NLS-1$//$NON-NLS-2$
		}

		try {
			dav.subscribeSender(this, objekt, fehlerDatenBeschreibung,
					SenderRole.source());
		} catch (OneSubscriptionPerSendData ignored) {
			// Wird ignoriert, da ein DE theoretisch in mehreren EAK vorkommen kann und dann hier versucht wird 2 Quellen anzumelden
		}

		new DeErfassungsZustand(sDav, this.getObjekt()).addListener(this);
	}

	public void update(ResultData[] erwarteteResultate) {
		if (erwarteteResultate != null) {
			for (ResultData erwartetesResultat : erwarteteResultate) {
				if (erwartetesResultat != null) {

					/**
					 * Nutzdatum empfangen
					 */
					if (erwartetesResultat.getData() != null) {
						this.inTime = true;

						this.versucheErwartung();
					}
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

		Data datum = sDav.createData(fehlerDatenBeschreibung
				.getAttributeGroup());
		datum.getUnscaledValue("TlsFehlerAnalyse").set(tlsFehler.getCode()); //$NON-NLS-1$
		try {
			sDav.sendData(new ResultData(this.objekt, fehlerDatenBeschreibung,
					fehlerZeit, datum));
		} catch (DataNotSubscribedException e) {
			Debug.getLogger().error("Datum " + datum + " konnte fuer " + //$NON-NLS-1$ //$NON-NLS-2$
					this.objekt + " nicht publiziert werden. Grund:\n" + e.getLocalizedMessage()); //$NON-NLS-1$
		} catch (SendSubscriptionNotConfirmed e) {
			Debug.getLogger().error("Datum " + datum + " konnte fuer " + //$NON-NLS-1$ //$NON-NLS-2$
					this.objekt + " nicht publiziert werden. Grund:\n" + e.getLocalizedMessage()); //$NON-NLS-1$
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

	@Override
	public boolean kannFehlerHierPublizieren(long zeitStempel) {
		return zeitStempel > this.zeitStempelLetzterPublizierterFehler;
	}

	@Override
	public void publiziereFehler(long zeitStempel) {
		this.publiziereFehlerUrsache(zeitStempel, TlsFehlerAnalyse.UNBEKANNT);
	}

	public synchronized void aktualisiereParameterTlsFehlerAnalyse(
			long zeitverzugFehlerErkennung, long zeitverzugFehlerErmittlung) {
		this.zeitVerzugFehlerErkennung = zeitverzugFehlerErkennung;
		this.zeitVerzugFehlerErmittlung = zeitverzugFehlerErmittlung;
		this.versucheErwartung();
	}

	public synchronized void aktualisiereErfassungsZustand(Zustand zustand) {
		this.aktuellerZustand = zustand;
		this.versucheErwartung();
	}

	/**
	 * Initiiert die Erwartung eines Nutzdatums dieses DE, wenn dies aufgrund
	 * der aktuellen Parameter bzw. Online-Daten des DE moeglich bzw. notwendig
	 * ist.
	 */
	private synchronized void versucheErwartung() {
		if (this.zeitVerzugFehlerErkennung >= 0) {

			if (this.aktuellerZustand != null
					&& this.aktuellerZustand.getErfassungsIntervallDauer() > 0) {

				this.letzterErwarteterDatenZeitpunkt = getNaechstenIntervallZeitstempel(
						System.currentTimeMillis(), this.aktuellerZustand
								.getErfassungsIntervallDauer());
				long nachsterErwarteterZeitpunkt = this.letzterErwarteterDatenZeitpunkt
						+ zeitVerzugFehlerErkennung;

				Debug.getLogger().info(
						"Plane Erwartung fuer "
								+ De.this.getObjekt()
								+ ": "
								+ DUAKonstanten.ZEIT_FORMAT_GENAU.format(new Date(
										nachsterErwarteterZeitpunkt
										+ STANDARD_ZEIT_ABSTAND)));
				fehlerWecker.setWecker(this, nachsterErwarteterZeitpunkt
						+ STANDARD_ZEIT_ABSTAND);
			} else {
				if (this.aktuellerZustand != null
						&& this.aktuellerZustand.isInitialisiert()) {
					if (this.aktuellerZustand.getErfassungsIntervallDauer() <= 0) {
						Debug.getLogger().info(
								"Erwartung fuer "
										+ De.this.getObjekt()
										+ " ausgeplant.");
						fehlerWecker.setWecker(this, ObjektWecker.AUS);
						this.publiziere(getMessage(this.aktuellerZustand.getGrund()));
					}
				} else {
					if(this.aktuellerZustand == null) {
						Debug
						.getLogger()
						.warning(
								"Aktueller Erfassungszustand von " + De.this.objekt + " ist (noch) nicht bekannt"); //$NON-NLS-1$//$NON-NLS-2$						
					} else {
						Debug
						.getLogger()
						.warning(
								"DE "	+ De.this.objekt + " ist (noch) nicht vollstaendig initialisiert:\n" + this.aktuellerZustand); //$NON-NLS-1$//$NON-NLS-2$						
					}
				}
			}

		} else {
			Debug.getLogger().warning("Kann keine Daten fuer " + this.objekt + //$NON-NLS-1$
					" erwarten, da noch keine (sinnvollen) " + //$NON-NLS-1$
					"Parameter zur TLS-Fehleranalyse empfangen wurden"); //$NON-NLS-1$
		}
	}

	private OperatingMessage getMessage(final String grund) {
		OperatingMessage message = MESSAGE_TEMPLATE.newMessage(objekt);
		message.put("reason", grund);
		message.addId("[DUA-FT-FU06]");
		return message;
	}

	public void dataRequest(SystemObject object,
			DataDescription dataDescription, byte state) {
		// wird ignoriert (Anmeldung als Quelle)
	}

	public boolean isRequestSupported(SystemObject object,
			DataDescription dataDescription) {
		return false;
	}

	public void alarm() {
		/**
		 * Ueberpruefe Bedingungen nach Afo-9.0 DUA BW C1C2-21 (S. 45)
		 */

		DeErfassungsZustand.Zustand zustand = De.this.aktuellerZustand;

		if (zustand.isErfasst()) {
			De.this.inTime = false;
			final long fehlerZeit = De.this.letzterErwarteterDatenZeitpunkt;

			DateFormat format = new SimpleDateFormat(DUAKonstanten.ZEIT_FORMAT_GENAU_STR);
			
			Debug.getLogger().info(
					"Plane Fehlerpublikation fuer "
							+ De.this.getObjekt()
							+ ": "
							+ format.format(new Date(
									fehlerZeit + zeitVerzugFehlerErkennung
											+ zeitVerzugFehlerErmittlung + 2
											* STANDARD_ZEIT_ABSTAND))
							+ "\nFehlerzeit: "
							+ format.format(new Date(
									fehlerZeit)) + "\nVerzug (Erkennung): "
							+ zeitVerzugFehlerErkennung
							+ "\nVerzug (Ermittlung): "
							+ zeitVerzugFehlerErmittlung + "\nZusatzverzug: "
							+ (2 * STANDARD_ZEIT_ABSTAND));
			
			analyseWecker.setWecker(new IObjektWeckerListener() {

				public void alarm() {
					if (!De.this.inTime) {
						versucheFehlerPublikation(fehlerZeit);
					}
				}

			}, fehlerZeit + zeitVerzugFehlerErkennung
					+ zeitVerzugFehlerErmittlung + 2 * STANDARD_ZEIT_ABSTAND);
		} else {
			if (zustand.isInitialisiert()) {
				De.this.publiziere(getMessage(zustand.getGrund()));
			} else {
				Debug
						.getLogger()
						.warning(
								De.this.objekt
										+ " ist (noch) nicht vollstaendig initialisiert"); //$NON-NLS-1$
			}
		}
	}

	/**
	 * Erfragt den ersten Zeitstempel, der sich echt (&gt; 500ms) nach dem
	 * Zeitstempel <code>jetzt</code> (angenommenr Jetzt-Zeitpunkt) befindet
	 * und der zur uebergebenen Erfassungsintervalllange passt.
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
		if (intervallLaenge >= (long) (60 * 60 * 1000)) {
			stundenAnfang.set(Calendar.HOUR_OF_DAY, 0);
			final long msNachTagesAnfang = jetztPlus
					- stundenAnfang.getTimeInMillis();
			final long intervalleSeitTagesAnfang = msNachTagesAnfang
					/ intervallLaenge;
			naechsterIntervallZeitstempel = stundenAnfang.getTimeInMillis()
					+ (intervalleSeitTagesAnfang + 1) * intervallLaenge;
		} else {
			final long msNachStundenAnfang = jetztPlus
					- stundenAnfang.getTimeInMillis();
			final long intervalleSeitStundenAnfang = msNachStundenAnfang
					/ intervallLaenge;
			naechsterIntervallZeitstempel = stundenAnfang.getTimeInMillis()
					+ (intervalleSeitStundenAnfang + 1) * intervallLaenge;
		}

		return naechsterIntervallZeitstempel;
	}

}
