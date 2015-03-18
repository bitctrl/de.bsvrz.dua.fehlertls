/*
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

package de.bsvrz.dua.fehlertls;

import java.util.Random;

import de.bsvrz.dav.daf.main.ClientDavInterface;
import de.bsvrz.dav.daf.main.ClientSenderInterface;
import de.bsvrz.dav.daf.main.Data;
import de.bsvrz.dav.daf.main.DataDescription;
import de.bsvrz.dav.daf.main.DataNotSubscribedException;
import de.bsvrz.dav.daf.main.ResultData;
import de.bsvrz.dav.daf.main.SendSubscriptionNotConfirmed;
import de.bsvrz.dav.daf.main.SenderRole;
import de.bsvrz.dav.daf.main.config.AttributeGroup;
import de.bsvrz.dav.daf.main.config.SystemObject;
import de.bsvrz.dua.fehlertls.de.DeFaException;
import de.bsvrz.dua.fehlertls.de.DeTypLader;
import de.bsvrz.dua.fehlertls.de.IDeTyp;
import de.bsvrz.dua.fehlertls.enums.TlsDeFehlerStatus;
import de.bsvrz.sys.funclib.bitctrl.daf.DaVKonstanten;
import de.bsvrz.sys.funclib.bitctrl.dua.DUAKonstanten;

/**
 * Simuliert KEx.
 *
 * @author BitCtrl Systems GmbH, Thierfelder
 *
 * @version $Id$
 */
public final class TestKEx implements ClientSenderInterface {

	/**
	 * Randomizer.
	 */
	private static final Random RANDOM = new Random(System.currentTimeMillis());

	/**
	 * alle DEs.
	 */
	private final SystemObject iB1SM1UFD1DE1;

	/**
	 * iB1SM1UFD1DE2.
	 */
	private final SystemObject iB1SM1UFD1DE2;

	/**
	 * iB1SM1LVE1DE1.
	 */
	private final SystemObject iB1SM1LVE1DE1;

	/**
	 * iB2SM3LVE1DE1.
	 */
	private final SystemObject iB2SM3LVE1DE1;

	/**
	 * iB2SM1LVE1DE1.
	 */
	private final SystemObject iB2SM1LVE1DE1;

	/**
	 * DE.
	 */
	private final SystemObject iB2SM1UFD1DE1;

	/**
	 * DE.
	 */
	private final SystemObject iB2SM1UFD1DE2;

	/**
	 * DE.
	 */
	private final SystemObject iB2SM2LVE1DE1;

	/**
	 * DE.
	 */
	private final SystemObject iB2SM2LVE1DE2;

	/**
	 * statische Instanz dieser Klasse.
	 */
	private static TestKEx instanz;

	/**
	 * statische Datenverteiler-Verbindung.
	 */
	private static ClientDavInterface sDav;

	/**
	 * Erfragt die statische Instanz dieser Klasse.
	 *
	 * @param dav
	 *            Datenverteiler-Verbindung
	 * @return die statische Instanz dieser Klasse
	 * @throws Exception
	 *             wird weitergereicht
	 */
	public static TestKEx getInstanz(final ClientDavInterface dav)
			throws Exception {
		if (TestKEx.instanz == null) {
			TestKEx.instanz = new TestKEx(dav);
		}
		return TestKEx.instanz;
	}

	/**
	 * Standardkonstruktor.
	 *
	 * @param dav
	 *            Datenverteiler-Verbindung
	 * @throws Exception
	 *             wird weitergereicht
	 */
	private TestKEx(final ClientDavInterface dav) throws Exception {
		TestKEx.sDav = dav;

		iB1SM1UFD1DE1 = dav.getDataModel()
				.getObject("kri1.ib1.sm1.eakufd1.de1");
		iB1SM1UFD1DE2 = dav.getDataModel()
				.getObject("kri1.ib1.sm1.eakufd1.de2");
		iB1SM1LVE1DE1 = dav.getDataModel()
				.getObject("kri1.ib1.sm1.eaklve1.de1");

		iB2SM3LVE1DE1 = dav.getDataModel()
				.getObject("kri1.ib2.sm3.eaklve1.de1");
		iB2SM1LVE1DE1 = dav.getDataModel()
				.getObject("kri1.ib2.sm1.eaklve1.de1");
		iB2SM1UFD1DE1 = dav.getDataModel()
				.getObject("kri1.ib2.sm1.eakufd1.de1");
		iB2SM1UFD1DE2 = dav.getDataModel()
				.getObject("kri1.ib2.sm1.eakufd1.de2");
		iB2SM2LVE1DE1 = dav.getDataModel()
				.getObject("kri1.ib2.sm2.eaklve1.de1");
		iB2SM2LVE1DE2 = dav.getDataModel()
				.getObject("kri1.ib2.sm2.eaklve1.de2");

		for (final SystemObject de : dav.getDataModel().getType("typ.de")
				.getElements()) {
			if (!"typ.deTest".equals(de.getType().getPid())) {
				final IDeTyp deTyp = DeTypLader.getDeTyp(de.getType());
				for (final DataDescription datenBeschreibung : deTyp
						.getDeFaMesswertDataDescriptions(dav)) {
					dav.subscribeSender(this, de, datenBeschreibung,
							SenderRole.source());
				}

				dav.subscribeSender(this, de, new DataDescription(deTyp
						.getDeFaIntervallParameterDataDescription(dav)
						.getAttributeGroup(), TestKEx.sDav.getDataModel()
						.getAspect(DaVKonstanten.ASP_PARAMETER_VORGABE)),
						SenderRole.sender());
				dav.subscribeSender(
						this,
						de,
						new DataDescription(dav.getDataModel()
								.getAttributeGroup("atg.tlsGloDeFehler"), dav
								.getDataModel().getAspect(
										DUAKonstanten.ASP_TLS_ANTWORT)),
										SenderRole.source());
			}
		}
		dav.subscribeSender(
				this,
				dav.getDataModel().getObject("DeFa"),
				new DataDescription(dav.getDataModel().getAttributeGroup(
						"atg.parameterTlsFehlerAnalyse"), dav.getDataModel()
						.getAspect(DaVKonstanten.ASP_PARAMETER_VORGABE)),
						SenderRole.sender());

		/**
		 * Warten bis alle Anmeldungen durchgefuehrt sein sollten
		 */
		try {
			Thread.sleep(1000L);
		} catch (final InterruptedException e) {
			//
		}
	}

	/**
	 * Setzt die Parameter der DeFa.
	 *
	 * @param zeitverzugFehlerErkennung
	 *            Der zusätzliche Zeitverzug, der nach dem erwarteten
	 *            Empfangszeitpunkt noch bis zur Erkennung eines nicht
	 *            gelieferten Messwertes abgewartet werden muss
	 * @param zeitverzugFehlerErmittlung
	 *            Der zusätzliche Zeitverzug, der nach der Fehlererkennung bis
	 *            zur Fehlerermittlung abgewartet werden muss
	 * @throws SendSubscriptionNotConfirmed
	 *             die Sendeanmeldung wurde nicht bestätigt
	 * @throws DataNotSubscribedException
	 *             es ist keine Datenanmeldung vorhanden
	 */
	public void setAnalyseParameter(final long zeitverzugFehlerErkennung,
			final long zeitverzugFehlerErmittlung)
					throws DataNotSubscribedException, SendSubscriptionNotConfirmed {
		final Data datum = TestKEx.sDav.createData(TestKEx.sDav.getDataModel()
				.getAttributeGroup("atg.parameterTlsFehlerAnalyse"));

		datum.getTimeValue("ZeitverzugFehlerErkennung").setMillis(
				zeitverzugFehlerErkennung);
		datum.getTimeValue("ZeitverzugFehlerErmittlung").setMillis(
				zeitverzugFehlerErmittlung);

		final ResultData resultat = new ResultData(TestKEx.sDav.getDataModel()
				.getObject("DeFa"),
				new DataDescription(TestKEx.sDav.getDataModel()
						.getAttributeGroup("atg.parameterTlsFehlerAnalyse"),
						TestKEx.sDav.getDataModel().getAspect(
								DaVKonstanten.ASP_PARAMETER_VORGABE)),
								System.currentTimeMillis(), datum);

		TestKEx.sDav.sendData(resultat);
	}

	/**
	 * Setzt den DE-Fehler.
	 *
	 * @param de
	 *            das DE
	 * @param fehlerStatus
	 *            Zustand des DE-Fehlers 0 = ok, 1 = StörEAK, 2 = StörSM
	 * @param passiviert
	 *            ob der Kanal passiviert ist
	 * @throws SendSubscriptionNotConfirmed
	 *             die Sendeanmeldung wurde nicht bestätigt
	 * @throws DataNotSubscribedException
	 *             es ist keine Datenanmeldung vorhanden
	 */
	public void setDeFehlerStatus(final SystemObject de,
			final int fehlerStatus, final boolean passiviert)
					throws DataNotSubscribedException, SendSubscriptionNotConfirmed {
		final AttributeGroup atg = TestKEx.sDav.getDataModel()
				.getAttributeGroup("atg.tlsGloDeFehler");
		final Data datum = TestKEx.sDav.createData(atg);
		datum.getUnscaledValue("DEFehlerStatus").set(fehlerStatus);
		datum.getUnscaledValue("DEKanalStatus").set(passiviert ? 1 : 0);
		datum.getUnscaledValue("DEProjektierungsStatus").set(0);
		datum.getUnscaledValue("HerstellerDefinierterCode").set(0);
		datum.getUnscaledValue("Hersteller").set(0);
		final ResultData resultat = new ResultData(de, new DataDescription(
				TestKEx.sDav.getDataModel().getAttributeGroup(
						"atg.tlsGloDeFehler"), TestKEx.sDav.getDataModel()
						.getAspect(DUAKonstanten.ASP_TLS_ANTWORT)),
						System.currentTimeMillis(), datum);

		TestKEx.sDav.sendData(resultat);
	}

	/**
	 * Sendet ein Nutzdatum fuer ein DE.
	 *
	 * @param de
	 *            das DE
	 * @param zeitStempel
	 *            der Zeitstempel des Nutzdatums
	 * @throws SendSubscriptionNotConfirmed
	 *             die Sendeanmeldung wurde nicht bestätigt
	 * @throws DataNotSubscribedException
	 *             es ist keine Datenanmeldung vorhanden
	 * */
	public void sendeDatum(final SystemObject de, final long zeitStempel)
			throws DataNotSubscribedException, SendSubscriptionNotConfirmed {
		Data datum = null;
		AttributeGroup atg = null;

		if (de.isOfType("typ.deUfd")) {
			switch (TestKEx.RANDOM.nextInt(3)) {
			case 0:
				atg = TestKEx.sDav.getDataModel().getAttributeGroup(
						"atg.tlsUfdErgebnisMeldungHelligkeitHK");
				datum = TestKEx.sDav.createData(atg);
				datum.getUnscaledValue("Helligkeit").set(0);
				break;
			case 1:
				atg = TestKEx.sDav.getDataModel().getAttributeGroup(
						"atg.tlsUfdErgebnisMeldungNiederschlag");
				datum = TestKEx.sDav.createData(atg);
				datum.getUnscaledValue("Niederschlag").set(0);
				break;
			case 2:
				atg = TestKEx.sDav.getDataModel().getAttributeGroup(
						"atg.tlsUfdErgebnisMeldungLuftTemperaturLT");
				datum = TestKEx.sDav.createData(atg);
				datum.getUnscaledValue("Lufttemperatur").set(0);
				break;
			default:
			}
		} else {
			atg = TestKEx.sDav.getDataModel().getAttributeGroup(
					"atg.tlsLveErgebnisMeldungVersion0Bis4");
			datum = TestKEx.sDav.createData(atg);

			datum.getTimeValue("T").setMillis(0);
			datum.getUnscaledValue("qKfz").set(0);
			datum.getUnscaledValue("qLkwÄ").set(0);
			datum.getUnscaledValue("vPkwÄ").set(0);
			datum.getUnscaledValue("vLkwÄ").set(0);
			datum.getUnscaledValue("tNetto").set(0);
			datum.getUnscaledValue("b").set(0);
			datum.getUnscaledValue("sKfz").set(0);
			datum.getUnscaledValue("vKfz").set(0);
			datum.getArray("qPkwÄGeschwKlasse").setLength(0);
			datum.getArray("qLkwÄGeschwKlasse").setLength(0);
		}

		final ResultData sendeDatum = new ResultData(de, new DataDescription(
				atg, TestKEx.sDav.getDataModel().getAspect(
						DUAKonstanten.ASP_TLS_ANTWORT)), zeitStempel, datum);

		TestKEx.sDav.sendData(sendeDatum);
	}

	/**
	 * Setzt die Betriebsparameter eines DE.
	 *
	 * @param de
	 *            ein DE
	 * @param zyklus
	 *            der Abfragezyklus (in ms) (-1 == nicht zyklusche Abfrage)
	 * @throws DeFaException
	 *             Allgemeiner Fehler beim Bewerten der TLS-Daten
	 * @throws SendSubscriptionNotConfirmed
	 *             die Sendeanmeldung wurde nicht bestätigt
	 * @throws DataNotSubscribedException
	 *             es ist keine Datenanmeldung vorhanden
	 */
	public void setBetriebsParameter(final SystemObject de, final long zyklus)
			throws DeFaException, DataNotSubscribedException,
			SendSubscriptionNotConfirmed {
		Data datenSatz;
		try {
			datenSatz = TestKEx.sDav.createData(DeTypLader
					.getDeTyp(de.getType())
					.getDeFaIntervallParameterDataDescription(TestKEx.sDav)
					.getAttributeGroup());
		} catch (final DeFaException e1) {
			throw new RuntimeException(e1);
		}

		if (de.isOfType("typ.deLve")) {
			datenSatz.getUnscaledValue("VersionKurzZeitDaten").set(0);
			datenSatz.getUnscaledValue("IntervallDauerKurzZeitDaten").set(
					zyklus >= 0 ? zyklus / (15L * 1000L) : 15L * 1000L);
			datenSatz.getUnscaledValue("VersionLangZeitDaten").set(10);
			datenSatz.getUnscaledValue("IntervallDauerLangZeit").set(129);
			datenSatz.getUnscaledValue("alpha1").set(1);
			datenSatz.getUnscaledValue("alpha2").set(1);
			datenSatz.getUnscaledValue("LängenGrenzWert").set(400);
			datenSatz.getUnscaledValue("ArtMittelWertBildung").set(0);
			datenSatz.getUnscaledValue("StartMittelWertBildung").set(0);
		} else {
			datenSatz.getUnscaledValue("Erfassungsperiodendauer").set(
					zyklus >= 0 ? zyklus / 1000L : 60);
			datenSatz.getUnscaledValue("Übertragungsverfahren").set(
					zyklus >= 0 ? 1 : 0);
		}

		final ResultData neuerParameter = new ResultData(de,
				new DataDescription(DeTypLader.getDeTyp(de.getType())
						.getDeFaIntervallParameterDataDescription(TestKEx.sDav)
						.getAttributeGroup(), TestKEx.sDav.getDataModel()
						.getAspect(DaVKonstanten.ASP_PARAMETER_VORGABE)),
				System.currentTimeMillis(), datenSatz);
		TestKEx.sDav.sendData(neuerParameter);
		System.out.println("Sende Betriebsparameter:\n" + neuerParameter);
	}

	/**
	 * Setzt ein DE in einen bestimmten Zustand bzgl der DeFa.
	 *
	 * @param de
	 *            das DE
	 * @param zeitStempel
	 *            der Zeitstempel der Zustandsaenderung
	 * @param status
	 *            der neue Zustand
	 * @throws SendSubscriptionNotConfirmed
	 *             die Sendeanmeldung wurde nicht bestätigt
	 * @throws DataNotSubscribedException
	 *             es liegt keine Datenanmeldung vor
	 * @throws DeFaException
	 *             allgemeiner Fehler beim Bewerten der TLS-Daten
	 */
	public void setDe(final SystemObject de, final long zeitStempel,
			final DeStatus status) throws DataNotSubscribedException,
			SendSubscriptionNotConfirmed, DeFaException {
		if (status.equals(DeStatus.KANAL_AKTIVIERT_DE_FEHLER_AN)) {
			this.setDeFehlerStatus(de, TlsDeFehlerStatus.STOER_EAK.getCode(),
					false);
		} else if (status.equals(DeStatus.KANAL_AKTIVIERT_DE_FEHLER_AUS)) {
			this.setDeFehlerStatus(de, 0, false);
		} else if (status.equals(DeStatus.KANAL_PASSIVIERT_DE_FEHLER_AN)) {
			this.setDeFehlerStatus(de, TlsDeFehlerStatus.STOER_EAK.getCode(),
					true);
		} else if (status.equals(DeStatus.KANAL_PASSIVIERT_DE_FEHLER_AUS)) {
			this.setDeFehlerStatus(de, 0, true);
		} else if (status.equals(DeStatus.NUTZ_DATEN)) {
			this.sendeDatum(de, zeitStempel);
		} else if (status.equals(DeStatus.ZYKLISCH_AN)) {
			if (de.isOfType("typ.deUfd")) {
				this.setBetriebsParameter(de, 60L * 1000L);
			} else {
				this.setBetriebsParameter(de, 15L * 1000L);
			}
		} else if (status.equals(DeStatus.ZYKLISCH_AUS)) {
			this.setBetriebsParameter(de, -1L);
		}
	}

	/**
	 * Erfragt alle Test-Des.
	 *
	 * @return alle Test-Des
	 */
	public SystemObject[] getAlleDes() {
		return new SystemObject[] { iB1SM1UFD1DE1, iB1SM1UFD1DE2,
				iB1SM1LVE1DE1, iB2SM3LVE1DE1, iB2SM1LVE1DE1, iB2SM1UFD1DE1,
				iB2SM1UFD1DE2, iB2SM2LVE1DE1, iB2SM2LVE1DE2 };
	}

	/**
	 * Erfragt alle Test-LVE-Des.
	 *
	 * @return alle Test-LVE-Des
	 */
	public SystemObject[] getAlleLveDes() {
		return new SystemObject[] { iB1SM1LVE1DE1, iB2SM3LVE1DE1,
				iB2SM1LVE1DE1, iB2SM2LVE1DE1, iB2SM2LVE1DE2 };
	}

	/**
	 * Erfragt alle Test-UFD-Des.
	 *
	 * @return alle Test-UFD-Des
	 */
	public SystemObject[] getAlleUFDDes() {
		return new SystemObject[] { iB1SM1UFD1DE1, iB1SM1UFD1DE2,
				iB2SM1UFD1DE1, iB2SM1UFD1DE2 };
	}

	@Override
	public void dataRequest(final SystemObject object,
			final DataDescription dataDescription, final byte state) {
		// Quellenanmeldung
	}

	@Override
	public boolean isRequestSupported(final SystemObject object,
			final DataDescription dataDescription) {
		return false;
	}

	/**
	 * liefert das Systemobjekt für die Test-DE B1SM1UFD1DE1.
	 *
	 * @return das Systemobjekt
	 */
	public SystemObject getiB1SM1UFD1DE1() {
		return iB1SM1UFD1DE1;
	}

	/**
	 * liefert das Systemobjekt für die Test-DE B1SM1UFD1DE2.
	 *
	 * @return das Systemobjekt
	 */
	public SystemObject getiB1SM1UFD1DE2() {
		return iB1SM1UFD1DE2;
	}

	/**
	 * liefert das Systemobjekt für die Test-DE B1SM1LVE1DE1.
	 *
	 * @return das Systemobjekt
	 */
	public SystemObject getiB1SM1LVE1DE1() {
		return iB1SM1LVE1DE1;
	}

	/**
	 * liefert das Systemobjekt für die Test-DE B2SM3LVE1DE1.
	 *
	 * @return das Systemobjekt
	 */
	public SystemObject getiB2SM3LVE1DE1() {
		return iB2SM3LVE1DE1;
	}

	/**
	 * liefert das Systemobjekt für die Test-DE B2SM1LVE1DE1.
	 *
	 * @return das Systemobjekt
	 */
	public SystemObject getiB2SM1LVE1DE1() {
		return iB2SM1LVE1DE1;
	}

	/**
	 * liefert das Systemobjekt für die Test-DE B2SM1UFD1DE1.
	 *
	 * @return das Systemobjekt
	 */
	public SystemObject getiB2SM1UFD1DE1() {
		return iB2SM1UFD1DE1;
	}

	/**
	 * liefert das Systemobjekt für die Test-DE B2SM1UFD1DE2.
	 *
	 * @return das Systemobjekt
	 */
	public SystemObject getiB2SM1UFD1DE2() {
		return iB2SM1UFD1DE2;
	}

	/**
	 * liefert das Systemobjekt für die Test-DE B2SM2LVE1DE1.
	 *
	 * @return das Systemobjekt
	 */
	public SystemObject getiB2SM2LVE1DE1() {
		return iB2SM2LVE1DE1;
	}

	/**
	 * liefert das Systemobjekt für die Test-DE B2SM2LVE1DE2.
	 *
	 * @return das Systemobjekt
	 */
	public SystemObject getiB2SM2LVE1DE2() {
		return iB2SM2LVE1DE2;
	}

}
