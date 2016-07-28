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

import com.bitctrl.Constants;
import de.bsvrz.dav.daf.main.*;
import de.bsvrz.dav.daf.main.config.SystemObject;
import de.bsvrz.dua.fehlertls.tests.fehlertls.TestDeFaApplikation1;

/**
 * Assoziiert mit <code>typ.tlsFehlerAnalyse</code>.
 * 
 * @author BitCtrl Systems GmbH, Thierfelder
 */
public final class TypTlsFehlerAnalyse implements ClientSenderInterface {

	/**
	 * ein Systemobjekt.
	 */
	private SystemObject objekt = null;

	/**
	 * Statische Verbindung zum Datenverteiler.
	 */
	private static ClientDavInterface dav = null;

	/**
	 * statische Instanz.
	 */
	private static TypTlsFehlerAnalyse instanz = null;

	/**
	 * Erfragt eine statische Instanz dieser Klasse.
	 * 
	 * @param dav1
	 *            Datenverteiler-Verbindung
	 * @return eine statische Instanz dieser Klasse.
	 * @throws Exception
	 *             wird weitergereicht
	 */
	public static TypTlsFehlerAnalyse getInstanz(ClientDavInterface dav1)
			throws Exception {
		if (instanz == null) {
			dav = dav1;
			instanz = new TypTlsFehlerAnalyse(dav);

		}
		return instanz;
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
	public void setParameter(long verzugErkennung, long verzugErmittlung) {
		Data data = dav.createData(dav.getDataModel().getAttributeGroup(
				"atg.parameterTlsFehlerAnalyse"));

		data.getItem("Urlasser").getReferenceValue("BenutzerReferenz")
				.setSystemObject(null);
		data.getItem("Urlasser").getTextValue("Ursache").setText("");
		data.getItem("Urlasser").getTextValue("Veranlasser").setText("");

		data.getTimeValue("ZeitverzugFehlerErkennung").setMillis(
				verzugErkennung);
		data.getTimeValue("ZeitverzugFehlerErmittlung").setMillis(
				verzugErmittlung);

		ResultData resultat = new ResultData(this.objekt, new DataDescription(
				dav.getDataModel().getAttributeGroup(
						"atg.parameterTlsFehlerAnalyse"), dav.getDataModel()
						.getAspect("asp.parameterSoll")), System
				.currentTimeMillis(), data);
		try {
			dav.sendData(resultat);
			if (TestDeFaApplikation1.DEBUG) {
				System.out.println("Sende Parameter:\n" + resultat);
			}
		} catch (DataNotSubscribedException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (SendSubscriptionNotConfirmed e) {
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
	private TypTlsFehlerAnalyse(ClientDavInterface dav)
			throws OneSubscriptionPerSendData {
		this.objekt = dav.getDataModel().getObject("DeFa");
		dav.subscribeSender(this, this.objekt, new DataDescription(dav
				.getDataModel().getAttributeGroup(
						"atg.parameterTlsFehlerAnalyse"), dav.getDataModel()
				.getAspect("asp.parameterSoll")), SenderRole.source());
		try {
			Thread.sleep(2L * Constants.MILLIS_PER_SECOND);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void dataRequest(SystemObject object,
			DataDescription dataDescription, byte state) {
		//
	}

	public boolean isRequestSupported(SystemObject object,
			DataDescription dataDescription) {
		return false;
	}

}
