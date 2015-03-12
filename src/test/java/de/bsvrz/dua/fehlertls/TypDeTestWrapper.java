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

package de.bsvrz.dua.fehlertls;

import java.util.HashMap;
import java.util.Map;

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
import de.bsvrz.dav.daf.main.config.AttributeGroup;
import de.bsvrz.dav.daf.main.config.SystemObject;
import de.bsvrz.dua.fehlertls.de.DeFaException;
import de.bsvrz.dua.fehlertls.de.DeTypLader;
import de.bsvrz.dua.fehlertls.de.IDeTyp;
import de.bsvrz.dua.fehlertls.enums.TlsDeFehlerStatus;
import de.bsvrz.dua.fehlertls.fehlertls.DeFaApplikationTest2;
import de.bsvrz.sys.funclib.bitctrl.daf.DaVKonstanten;
import de.bsvrz.sys.funclib.bitctrl.dua.DUAKonstanten;

/**
 * Korrespondiert mit <code>atg.test</code>.
 * 
 * @author BitCtrl Systems GmbH, Thierfelder
 * 
 * @version $Id$
 */
public final class TypDeTestWrapper implements ClientSenderInterface {

	/**
	 * Alle statischen Instanzen dieser Klasse.
	 */
	private static Map<String, TypDeTestWrapper> instanzen = null;

	/**
	 * Statische Verbindung zum Datenverteiler.
	 */
	private static ClientDavInterface dav = null;

	/**
	 * ein Systemobjekt.
	 */
	private SystemObject objekt = null;

	/**
	 * Standardkonstruktor.
	 * 
	 * @param obj
	 *            ein Systemobjekt
	 * @throws OneSubscriptionPerSendData
	 *             wenn bereits eine lokale Sendeanmeldung fuer die gleichen
	 *             Daten von einem anderen Anwendungsobjekt vorliegt
	 * @throws DeFaException
	 *             wenn es Probleme bei Abläufen innerhalb einer einen DE-Typ
	 *             beschreibenden Klasse gibt. Oder insbesondere auch, wenn
	 *             diese Klasse nicht ermittelt oder instanziiert werden konnte
	 */
	private TypDeTestWrapper(SystemObject obj)
			throws OneSubscriptionPerSendData, DeFaException {
		this.objekt = obj;
		dav.subscribeSender(this, this.objekt, new DataDescription(dav
				.getDataModel().getAttributeGroup("atg.test"), dav
				.getDataModel().getAspect(DUAKonstanten.ASP_TLS_ANTWORT)),
				SenderRole.source());
		dav.subscribeSender(this, obj, new DataDescription(dav.getDataModel()
				.getAttributeGroup("atg.tlsGloDeFehler"), //$NON-NLS-1$
				dav.getDataModel().getAspect(DUAKonstanten.ASP_TLS_ANTWORT)),
				SenderRole.source());

		IDeTyp deTyp = DeTypLader.getDeTyp(this.objekt.getType());
		dav.subscribeSender(this, this.objekt, new DataDescription(deTyp
				.getDeFaIntervallParameterDataDescription(dav)
				.getAttributeGroup(), dav.getDataModel().getAspect(
				DaVKonstanten.ASP_PARAMETER_VORGABE)), SenderRole.sender());
	}

	/**
	 * Initialisiert diese Klasse.
	 * 
	 * @param dav1
	 *            Datenverteiler-Verbindung
	 * @throws OneSubscriptionPerSendData
	 *             wenn bereits eine lokale Sendeanmeldung fuer die gleichen
	 *             Daten von einem anderen Anwendungsobjekt vorliegt
	 * @throws DeFaException
	 *             wenn es Probleme bei Abläufen innerhalb einer einen DE-Typ
	 *             beschreibenden Klasse gibt. Oder insbesondere auch, wenn
	 *             diese Klasse nicht ermittelt oder instanziiert werden konnte
	 */
	public static void init(ClientDavInterface dav1)
			throws OneSubscriptionPerSendData, DeFaException {
		dav = dav1;
		instanzen = new HashMap<String, TypDeTestWrapper>();
		for (SystemObject obj : dav.getDataModel().getType("typ.deTest")
				.getElements()) {
			instanzen.put(obj.getPid(), new TypDeTestWrapper(obj));
		}
		try {
			Thread.sleep(2L * Constants.MILLIS_PER_SECOND);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Erfragt eine statische Instanz dieser Klasse.
	 * 
	 * @param name
	 *            der Name des DE.
	 * @return eine statische Instanz dieser Klasse.
	 */
	public static TypDeTestWrapper getInstanz(String name) {
		return instanzen.get(name);
	}

	/**
	 * Sendet ein Resultdatum mit oder ohne Nutzdaten.
	 * 
	 * @param nutzDaten
	 *            Sollen Nutzdaten enthalten sein?
	 * @param zeitStempel
	 *            Zeitstempel des Resultdatums.
	 */
	public void sendDeData(boolean nutzDaten, long zeitStempel) {
		Data data = null;

		if (nutzDaten) {
			data = dav.createData(dav.getDataModel().getAttributeGroup(
					"atg.test"));
			data.getUnscaledValue("Code").set(0);
		}

		try {
			ResultData resultat = new ResultData(this.objekt,
					new DataDescription(dav.getDataModel().getAttributeGroup(
							"atg.test"), dav.getDataModel().getAspect(
							"asp.tlsAntwort")), zeitStempel, data);
			dav.sendData(resultat);
			if (DeFaApplikationTest2.DEBUG) {
				System.out.println("Sende Daten:\n" + resultat);
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
	 * Setzt die Betriebsparameter eines DE.
	 * 
	 * @param zyklus
	 *            der Abfragezyklus (in ms) (-1 == nicht zyklusche Abfrage)
	 */
	public void setBetriebsParameter(long zyklus) {
		Data datenSatz;

		try {
			datenSatz = dav.createData(DeTypLader.getDeTyp(
					this.objekt.getType())
					.getDeFaIntervallParameterDataDescription(dav)
					.getAttributeGroup());
		} catch (DeFaException e1) {
			throw new RuntimeException(e1);
		}

		datenSatz
				.getTimeValue("Erfassungsperiodendauer").setMillis(zyklus >= 0 ? zyklus : 1L); //$NON-NLS-1$
		datenSatz
				.getUnscaledValue("Übertragungsverfahren").set(zyklus >= 0 ? 1 : 0); //$NON-NLS-1$

		ResultData neuerParameter = null;
		try {
			neuerParameter = new ResultData(this.objekt, new DataDescription(
					DeTypLader.getDeTyp(this.objekt.getType())
							.getDeFaIntervallParameterDataDescription(dav)
							.getAttributeGroup(), dav.getDataModel().getAspect(
							DaVKonstanten.ASP_PARAMETER_VORGABE)), System
					.currentTimeMillis(), datenSatz);
			dav.sendData(neuerParameter);
			System.out.println("Sende Betriebsparameter:\n" + neuerParameter); //$NON-NLS-1$
		} catch (Exception e) {
			throw new RuntimeException(neuerParameter.toString(), e);
		}
	}

	/**
	 * Setzt den DE-Fehler.
	 * 
	 * @param fehlerStatus
	 *            Zustand des DE-Fehlers 0 = ok, 1 = StörEAK, 2 = StörSM
	 * @param passiviert
	 *            ob der Kanal passiviert ist
	 */
	private void setDeFehlerStatus(int fehlerStatus, boolean passiviert) {
		AttributeGroup atg = dav.getDataModel().getAttributeGroup(
				"atg.tlsGloDeFehler"); //$NON-NLS-1$
		Data datum = dav.createData(atg);
		datum.getUnscaledValue("DEFehlerStatus").set(fehlerStatus); //$NON-NLS-1$
		datum.getUnscaledValue("DEKanalStatus").set(passiviert ? 1 : 0); //$NON-NLS-1$
		datum.getUnscaledValue("DEProjektierungsStatus").set(0); //$NON-NLS-1$
		datum.getUnscaledValue("HerstellerDefinierterCode").set(0); //$NON-NLS-1$
		datum.getUnscaledValue("Hersteller").set(0); //$NON-NLS-1$
		ResultData resultat = new ResultData(this.objekt, new DataDescription(
				dav.getDataModel().getAttributeGroup("atg.tlsGloDeFehler"), //$NON-NLS-1$
				dav.getDataModel().getAspect(DUAKonstanten.ASP_TLS_ANTWORT)),
				System.currentTimeMillis(), datum);

		try {
			dav.sendData(resultat);
			if (DeFaApplikationTest2.DEBUG) {
				System.out.println("Sende DE-Fehler:\n" + resultat);
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
	 * Setzt ein DE in einen bestimmten Zustand bzgl der DeFa.
	 * 
	 * @param status
	 *            der neue Zustand
	 */
	public void setDe(DeStatus status) {
		if (status.equals(DeStatus.KANAL_AKTIVIERT_DE_FEHLER_AN)) {
			this
					.setDeFehlerStatus(TlsDeFehlerStatus.STOER_EAK.getCode(),
							false);
		} else if (status.equals(DeStatus.KANAL_AKTIVIERT_DE_FEHLER_AUS)) {
			this.setDeFehlerStatus(0, false);
		} else if (status.equals(DeStatus.KANAL_PASSIVIERT_DE_FEHLER_AN)) {
			this.setDeFehlerStatus(TlsDeFehlerStatus.STOER_EAK.getCode(), true);
		} else if (status.equals(DeStatus.KANAL_PASSIVIERT_DE_FEHLER_AUS)) {
			this.setDeFehlerStatus(0, true);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void dataRequest(SystemObject object,
			DataDescription dataDescription, byte state) {
		// 
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isRequestSupported(SystemObject object,
			DataDescription dataDescription) {
		return false;
	}

}
