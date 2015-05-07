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

import com.bitctrl.Constants;

import de.bsvrz.dav.daf.main.ClientDavInterface;
import de.bsvrz.dav.daf.main.ClientSenderInterface;
import de.bsvrz.dav.daf.main.Data;
import de.bsvrz.dav.daf.main.DataDescription;
import de.bsvrz.dav.daf.main.DataNotSubscribedException;
import de.bsvrz.dav.daf.main.OneSubscriptionPerSendData;
import de.bsvrz.dav.daf.main.ResultData;
import de.bsvrz.dav.daf.main.SendSubscriptionNotConfirmed;
import de.bsvrz.dav.daf.main.SenderRole;
import de.bsvrz.dav.daf.main.config.SystemObject;
import de.bsvrz.dua.fehlertls.fehlertls.DeFaApplikationTest2;

/**
 * Assoziiert mit <code>typ.tlsFehlerAnalyse</code>.
 *
 * @author BitCtrl Systems GmbH, Thierfelder
 */
public final class TypTlsFehlerAnalyse implements ClientSenderInterface {

	/**
	 * ein Systemobjekt.
	 */
	private final SystemObject objekt;

	/**
	 * Statische Verbindung zum Datenverteiler.
	 */
	private static ClientDavInterface dav;

	/**
	 * statische Instanz.
	 */
	private static TypTlsFehlerAnalyse instanz;

	/**
	 * Erfragt eine statische Instanz dieser Klasse.
	 *
	 * @param dav1
	 *            Datenverteiler-Verbindung
	 * @return eine statische Instanz dieser Klasse.
	 * @throws Exception
	 *             wird weitergereicht
	 */
	public static TypTlsFehlerAnalyse getInstanz(final ClientDavInterface dav1) throws Exception {
		if (TypTlsFehlerAnalyse.instanz == null) {
			TypTlsFehlerAnalyse.dav = dav1;
			TypTlsFehlerAnalyse.instanz = new TypTlsFehlerAnalyse(TypTlsFehlerAnalyse.dav);

		}
		return TypTlsFehlerAnalyse.instanz;
	}

	/**
	 * Setzt die Parameter der TLS-Fehleranalyse.
	 *
	 * @param verzugErkennung
	 *            der zusaetzliche Zeitverzug, der nach dem erwarteten
	 *            Empfangszeitpunkt noch bis zur Erkennung eines nicht
	 *            gelieferten Messwertes abgewartet werden muss
	 * @param verzugErmittlung
	 *            der zusaetzliche Zeitverzug, der nach der Fehlererkennung bis
	 *            zur Fehlerermittlung abgewartet werden muss
	 */
	public void setParameter(final long verzugErkennung, final long verzugErmittlung) {
		final Data data = TypTlsFehlerAnalyse.dav
				.createData(TypTlsFehlerAnalyse.dav.getDataModel().getAttributeGroup("atg.parameterTlsFehlerAnalyse"));

		data.getItem("Urlasser").getReferenceValue("BenutzerReferenz").setSystemObject(null);
		data.getItem("Urlasser").getTextValue("Ursache").setText("");
		data.getItem("Urlasser").getTextValue("Veranlasser").setText("");

		data.getTimeValue("ZeitverzugFehlerErkennung").setMillis(verzugErkennung);
		data.getTimeValue("ZeitverzugFehlerErmittlung").setMillis(verzugErmittlung);

		final ResultData resultat = new ResultData(this.objekt,
				new DataDescription(
						TypTlsFehlerAnalyse.dav.getDataModel().getAttributeGroup("atg.parameterTlsFehlerAnalyse"),
						TypTlsFehlerAnalyse.dav.getDataModel().getAspect("asp.parameterVorgabe")),
				System.currentTimeMillis(), data);
		try {
			TypTlsFehlerAnalyse.dav.sendData(resultat);
			if (DeFaApplikationTest2.DEBUG) {
				System.out.println("Sende Parameter:\n" + resultat);
			}
		} catch (final DataNotSubscribedException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (final SendSubscriptionNotConfirmed e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	/**
	 * Standardkonstruktor.
	 *
	 * @param dav
	 *            Datenverteiler-Verbindung
	 * @throws OneSubscriptionPerSendData
	 *             wenn bereits eine lokale Sendeanmeldung fuer die gleichen
	 *             Daten von einem anderen Anwendungsobjekt vorliegt
	 */
	private TypTlsFehlerAnalyse(final ClientDavInterface dav) throws OneSubscriptionPerSendData {
		this.objekt = dav.getDataModel().getObject("DeFa");
		dav.subscribeSender(this, this.objekt,
				new DataDescription(dav.getDataModel().getAttributeGroup("atg.parameterTlsFehlerAnalyse"),
						dav.getDataModel().getAspect("asp.parameterVorgabe")),
				SenderRole.sender());
		try {
			Thread.sleep(2L * Constants.MILLIS_PER_SECOND);
		} catch (final InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void dataRequest(final SystemObject object, final DataDescription dataDescription, final byte state) {
		//
	}

	@Override
	public boolean isRequestSupported(final SystemObject object, final DataDescription dataDescription) {
		return false;
	}

}
