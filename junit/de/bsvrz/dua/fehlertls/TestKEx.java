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

import de.bsvrz.dav.daf.main.ClientDavInterface;
import de.bsvrz.dav.daf.main.ClientSenderInterface;
import de.bsvrz.dav.daf.main.Data;
import de.bsvrz.dav.daf.main.DataDescription;
import de.bsvrz.dav.daf.main.ResultData;
import de.bsvrz.dav.daf.main.SenderRole;
import de.bsvrz.dav.daf.main.config.AttributeGroup;
import de.bsvrz.dav.daf.main.config.SystemObject;
import de.bsvrz.dua.fehlertls.de.DeFaException;
import de.bsvrz.dua.fehlertls.de.DeTypLader;
import de.bsvrz.dua.fehlertls.de.IDeTyp;
import de.bsvrz.sys.funclib.bitctrl.app.Pause;
import de.bsvrz.sys.funclib.bitctrl.daf.DaVKonstanten;
import de.bsvrz.sys.funclib.bitctrl.dua.DUAKonstanten;

/**
 * Simuliert KEx
 * 
 * @author BitCtrl Systems GmbH, Thierfelder
 * 
 */
public class TestKEx
implements ClientSenderInterface{
	
	/**
	 * statische Instanz dieser Klasse
	 */
	private static TestKEx INSTANZ = null;
	
	/**
	 * statische Datenverteiler-Verbindung
	 */
	private static ClientDavInterface DAV = null; 
	
	
	/**
	 * Erfragt die statische Instanz dieser Klasse
	 *
	 * @param dav Datenverteiler-Verbindung
	 * @return die statische Instanz dieser Klasse
	 * @param Exception wird weitergereicht
	 */
	public static final TestKEx getInstanz(final ClientDavInterface dav)
	throws Exception{
		if(INSTANZ == null){
			INSTANZ = new TestKEx(dav);
		}
		return INSTANZ;
	}
	
	
	/**
	 * Standardkonstruktor 
	 * 
	 * @param dav Datenverteiler-Verbindung
	 * @param Exception wird weitergereicht
	 */
	private TestKEx(final ClientDavInterface dav)
	throws Exception{
		DAV = dav;
		
		for(SystemObject de:dav.getDataModel().getType("typ.de").getElements()){ //$NON-NLS-1$
			IDeTyp deTyp = DeTypLader.getDeTyp(de.getType());
			for(DataDescription datenBeschreibung:deTyp.getDeFaMesswertDataDescriptions(dav)){
				dav.subscribeSender(this, de, datenBeschreibung, SenderRole.source());
			}
						
			dav.subscribeSender(this, de, 
					new DataDescription(deTyp.getDeFaIntervallParameterDataDescription(dav).getAttributeGroup(),
							DAV.getDataModel().getAspect(DaVKonstanten.ASP_PARAMETER_VORGABE)), SenderRole.sender());
		}
		dav.subscribeSender(this, dav.getDataModel().getObject("DeFa"),  //$NON-NLS-1$
				new DataDescription(
						dav.getDataModel().getAttributeGroup("atg.parameterTlsFehlerAnalyse"),  //$NON-NLS-1$
						dav.getDataModel().getAspect(DaVKonstanten.ASP_PARAMETER_VORGABE)), SenderRole.sender());
		
		/**
		 * Warten bis alle Anmeldungen durchgefuehrt sein sollten
		 */
		Pause.warte(1000);
	}
	
	
	/**
	 * Setzt die Parameter der DeFa
	 * 
	 * @param zeitverzugFehlerErkennung Der zusätzliche Zeitverzug, der nach dem erwarteten Empfangszeitpunkt noch
	 * bis zur Erkennung eines nicht gelieferten Messwertes abgewartet werden muss
	 * @param zeitverzugFehlerErmittlung Der zusätzliche Zeitverzug, der nach der Fehlererkennung bis zur
	 * Fehlerermittlung abgewartet werden muss
	 */
	public final void setAnalyseParameter(long zeitverzugFehlerErkennung, long zeitverzugFehlerErmittlung){
		Data datum = DAV.createData(DAV.getDataModel().getAttributeGroup("atg.parameterTlsFehlerAnalyse")); //$NON-NLS-1$
		
		datum.getTimeValue("ZeitverzugFehlerErkennung").setMillis(zeitverzugFehlerErkennung); //$NON-NLS-1$
		datum.getTimeValue("ZeitverzugFehlerErmittlung").setMillis(zeitverzugFehlerErmittlung); //$NON-NLS-1$
		
		ResultData resultat = new ResultData(DAV.getDataModel().getObject("DeFa"), //$NON-NLS-1$
				new DataDescription(
						DAV.getDataModel().getAttributeGroup("atg.parameterTlsFehlerAnalyse"),  //$NON-NLS-1$
						DAV.getDataModel().getAspect(DaVKonstanten.ASP_PARAMETER_VORGABE)),
				System.currentTimeMillis(),
				datum);	
		
		try {
			DAV.sendData(resultat);
		} catch (Exception e) {
			throw new RuntimeException(resultat.toString(), e);
		}
	}
	
	
	/**
	 * Sendet ein Nutzdatum fuer ein DE
	 * 
	 * @param de das DE
	 * @param zeitStempel der Zeitstempel des Nutzdatums
	 */
	public final void sendeDatum(SystemObject de, long zeitStempel){
		Data datum = null;
		AttributeGroup atg = null;
		
		if(de.isOfType("typ.deUfd")){ //$NON-NLS-1$
			switch(DAVTest.R.nextInt(3)){
			case 0:
				atg = DAV.getDataModel().getAttributeGroup("atg.tlsUfdErgebnisMeldungHelligkeitHK"); //$NON-NLS-1$
				datum = DAV.createData(atg);
				datum.getUnscaledValue("Helligkeit").set(0); //$NON-NLS-1$
				break;
			case 1:
				atg = DAV.getDataModel().getAttributeGroup("atg.tlsUfdErgebnisMeldungNiederschlag"); //$NON-NLS-1$
				datum = DAV.createData(atg);
				datum.getUnscaledValue("Niederschlag").set(0); //$NON-NLS-1$
				break;
			case 2:
				atg = DAV.getDataModel().getAttributeGroup("atg.tlsUfdErgebnisMeldungLuftTemperaturLT"); //$NON-NLS-1$
				datum = DAV.createData(atg);
				datum.getUnscaledValue("Lufttemperatur").set(0); //$NON-NLS-1$
				break;
			}
		}else{
			atg = DAV.getDataModel().getAttributeGroup("atg.tlsSveErgebnisMeldungVersion0Bis1"); //$NON-NLS-1$
			datum = DAV.createData(atg);
			datum.getUnscaledValue("vKfzReise").set(0); //$NON-NLS-1$
			datum.getUnscaledValue("vPkwReise").set(0); //$NON-NLS-1$
			datum.getUnscaledValue("vLkwReise").set(0); //$NON-NLS-1$
			datum.getUnscaledValue("kKfz").set(0); //$NON-NLS-1$
			datum.getUnscaledValue("kPkw").set(0); //$NON-NLS-1$
			datum.getUnscaledValue("kLkw").set(0); //$NON-NLS-1$
		}
		
		ResultData sendeDatum = 
			new ResultData(de, 
					new DataDescription(
							atg,
							DAV.getDataModel().getAspect(DUAKonstanten.ASP_TLS_ANTWORT)),
							zeitStempel, datum);
		
		try {
			System.out.println(sendeDatum);
			DAV.sendData(sendeDatum);
		} catch (Exception e) {
			throw new RuntimeException(sendeDatum.toString(), e);
		}
	}
	
	
	/**
	 * Setzt die Betriebsparameter eines DE
	 * 
	 * @param de ein DE
	 * @param zyklus der Abfragezyklus (in ms) (-1 == nicht zyklusche Abfrage)
	 */
	public final void setBetriebsParameter(SystemObject de, long zyklus){
		Data datenSatz;
		try {
			datenSatz = DAV.createData(DeTypLader.getDeTyp(de.getType()).
					getDeFaIntervallParameterDataDescription(DAV).getAttributeGroup());
		} catch (DeFaException e1) {
			throw new RuntimeException(e1);
		}
		
		if(de.isOfType("typ.deLve")){ //$NON-NLS-1$
			datenSatz.getUnscaledValue("VersionKurzZeitDaten").set(0); //$NON-NLS-1$
			datenSatz.getUnscaledValue("IntervallDauerKurzZeitDaten").set(zyklus >= 0?zyklus / (15L * 1000L):15L * 1000L); //$NON-NLS-1$
			datenSatz.getUnscaledValue("VersionLangZeitDaten").set(10); //$NON-NLS-1$
			datenSatz.getUnscaledValue("IntervallDauerLangZeit").set(129); //$NON-NLS-1$
			datenSatz.getUnscaledValue("alpha1").set(1); //$NON-NLS-1$
			datenSatz.getUnscaledValue("alpha2").set(1); //$NON-NLS-1$
			datenSatz.getUnscaledValue("LängenGrenzWert").set(400); //$NON-NLS-1$
			datenSatz.getUnscaledValue("ArtMittelWertBildung").set(0); //$NON-NLS-1$
			datenSatz.getUnscaledValue("StartMittelWertBildung").set(0); //$NON-NLS-1$	
		}else{
			datenSatz.getUnscaledValue("Erfassungsperiodendauer").set(zyklus >= 0?zyklus / 1000L:60); //$NON-NLS-1$
			datenSatz.getUnscaledValue("Übertragungsverfahren").set(zyklus >= 0?1:0); //$NON-NLS-1$
		}

		ResultData neuerParameter = null; 
		try{
			neuerParameter = new ResultData(de, new DataDescription(DeTypLader.getDeTyp(de.getType()).
					getDeFaIntervallParameterDataDescription(DAV).getAttributeGroup(),
					DAV.getDataModel().getAspect(DaVKonstanten.ASP_PARAMETER_VORGABE)), System.currentTimeMillis(), datenSatz);
			DAV.sendData(neuerParameter);
		} catch (Exception e) {
			throw new RuntimeException(neuerParameter.toString(), e);
		}
	}


	/**
	 * {@inheritDoc}
	 */
	public void dataRequest(SystemObject object,
			DataDescription dataDescription, byte state) {
		// Quellenanmeldung
	}


	/**
	 * {@inheritDoc}
	 */
	public boolean isRequestSupported(SystemObject object,
			DataDescription dataDescription) {
		return false;
	}
	
}
