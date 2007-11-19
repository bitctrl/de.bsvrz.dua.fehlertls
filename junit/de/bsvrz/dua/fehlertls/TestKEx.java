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

import java.util.Date;

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
	 * alle DEs
	 */
	public static SystemObject IB1_SM1_UFD1_DE1 = null;
	public static SystemObject IB1_SM1_UFD1_DE2 = null;
	public static SystemObject IB1_SM1_LVE1_DE1 = null;
	
	public static SystemObject IB2_SM3_LVE1_DE1 = null;
	public static SystemObject IB2_SM1_LVE1_DE1 = null;
	public static SystemObject IB2_SM1_UFD1_DE1 = null;
	public static SystemObject IB2_SM1_UFD1_DE2 = null;	
	public static SystemObject IB2_SM2_LVE1_DE1 = null;
	public static SystemObject IB2_SM2_LVE1_DE2 = null;
	
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

		IB1_SM1_UFD1_DE1 = dav.getDataModel().getObject("kri1.ib1.sm1.eakufd1.de1"); //$NON-NLS-1$
		IB1_SM1_UFD1_DE2 = dav.getDataModel().getObject("kri1.ib1.sm1.eakufd1.de2"); //$NON-NLS-1$
		IB1_SM1_LVE1_DE1 = dav.getDataModel().getObject("kri1.ib1.sm1.eaklve1.de1"); //$NON-NLS-1$
		
		IB2_SM3_LVE1_DE1 = dav.getDataModel().getObject("kri1.ib2.sm3.eaklve1.de1"); //$NON-NLS-1$
		IB2_SM1_LVE1_DE1 = dav.getDataModel().getObject("kri1.ib2.sm1.eaklve1.de1"); //$NON-NLS-1$
		IB2_SM1_UFD1_DE1 = dav.getDataModel().getObject("kri1.ib2.sm1.eakufd1.de1"); //$NON-NLS-1$
		IB2_SM1_UFD1_DE2 = dav.getDataModel().getObject("kri1.ib2.sm1.eakufd1.de2"); //$NON-NLS-1$
		IB2_SM2_LVE1_DE1 = dav.getDataModel().getObject("kri1.ib2.sm2.eaklve1.de1"); //$NON-NLS-1$
		IB2_SM2_LVE1_DE2 = dav.getDataModel().getObject("kri1.ib2.sm2.eaklve1.de2"); //$NON-NLS-1$
		
		for(SystemObject de:dav.getDataModel().getType("typ.de").getElements()){ //$NON-NLS-1$
			IDeTyp deTyp = DeTypLader.getDeTyp(de.getType());
			for(DataDescription datenBeschreibung:deTyp.getDeFaMesswertDataDescriptions(dav)){
				dav.subscribeSender(this, de, datenBeschreibung, SenderRole.source());
			}
						
			dav.subscribeSender(this, de, 
					new DataDescription(deTyp.getDeFaIntervallParameterDataDescription(dav).getAttributeGroup(),
							DAV.getDataModel().getAspect(DaVKonstanten.ASP_PARAMETER_VORGABE)), SenderRole.sender());
			dav.subscribeSender(this, de, 
					new DataDescription(
							dav.getDataModel().getAttributeGroup("atg.tlsGloDeFehler"), //$NON-NLS-1$
							dav.getDataModel().getAspect(DUAKonstanten.ASP_TLS_ANTWORT)), SenderRole.source());
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
	 * Setzt den DE-Fehler
	 * 
	 * @param de das DE
	 * @param fehlerStatus Zustand des DE-Fehlers 0 = ok, 1 = StörEAK, 2 = StörSM
	 * @param passiviert ob der Kanal passiviert ist 
	 */
	public final void setDeFehlerStatus(SystemObject de, int fehlerStatus, boolean passiviert){
		AttributeGroup atg = DAV.getDataModel().getAttributeGroup("atg.tlsGloDeFehler"); //$NON-NLS-1$
		Data datum = DAV.createData(atg);
		datum.getUnscaledValue("DEFehlerStatus").set(fehlerStatus); //$NON-NLS-1$
		datum.getUnscaledValue("DEKanalStatus").set(passiviert?1:0); //$NON-NLS-1$
		datum.getUnscaledValue("DEProjektierungsStatus").set(0); //$NON-NLS-1$
		datum.getUnscaledValue("HerstellerDefinierterCode").set(0); //$NON-NLS-1$
		datum.getUnscaledValue("Hersteller").set(0); //$NON-NLS-1$
		ResultData resultat = new ResultData(de,
				new DataDescription(
						DAV.getDataModel().getAttributeGroup("atg.tlsGloDeFehler"), //$NON-NLS-1$
						DAV.getDataModel().getAspect(DUAKonstanten.ASP_TLS_ANTWORT)),
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
			System.out.println("... " + DUAKonstanten.ZEIT_FORMAT_GENAU.format(new Date()) + //$NON-NLS-1$
					":\n" + sendeDatum + " ..."); //$NON-NLS-1$ //$NON-NLS-2$
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
			System.out.println("Sende Betriebsparameter:\n" + neuerParameter); //$NON-NLS-1$
		} catch (Exception e) {
			throw new RuntimeException(neuerParameter.toString(), e);
		}
	}
	
	
	/**
	 * Setzt ein DE in einen bestimmten Zustand bzgl der DeFa
	 * 
	 * @param de das DE
	 * @param zeitStempel der Zeitstempel der Zustandsaenderung
	 * @param status der neue Zustand
	 */
	public final void setDe(SystemObject de, long zeitStempel, DeStatus status){
		if(status.equals(DeStatus.KANAL_AKTIVIERT_DE_FEHLER_AN)){
			this.setDeFehlerStatus(de, DAVTest.R.nextInt(2) + 1, false);
		}else
		if(status.equals(DeStatus.KANAL_AKTIVIERT_DE_FEHLER_AUS)){
			this.setDeFehlerStatus(de, 0, false);
		}else
		if(status.equals(DeStatus.KANAL_PASSIVIERT_DE_FEHLER_AN)){
			this.setDeFehlerStatus(de, DAVTest.R.nextInt(2) + 1, true);
		}else
		if(status.equals(DeStatus.KANAL_PASSIVIERT_DE_FEHLER_AUS)){
			this.setDeFehlerStatus(de, 0, true);
		}else
		if(status.equals(DeStatus.NUTZ_DATEN)){
			this.sendeDatum(de, zeitStempel);
		}else
		if(status.equals(DeStatus.ZYKLISCH_AN)){
			this.setBetriebsParameter(de, 15L * 1000L);
		}else
		if(status.equals(DeStatus.ZYKLISCH_AUS)){
			this.setBetriebsParameter(de, -1L);
		}
	}
	
	
	/**
	 * Erfragt alle Test-Des
	 * 
	 * @return alle Test-Des
	 */
	public final SystemObject[] getAlleDes(){
		return new SystemObject[]{IB1_SM1_UFD1_DE1,
								  IB1_SM1_UFD1_DE2,
								  IB1_SM1_LVE1_DE1,
								  IB2_SM3_LVE1_DE1,
								  IB2_SM1_LVE1_DE1,
								  IB2_SM1_UFD1_DE1,
								  IB2_SM1_UFD1_DE2,
								  IB2_SM2_LVE1_DE1,
								  IB2_SM2_LVE1_DE2
		};
	}
	

	/**
	 * Erfragt alle Test-LVE-Des
	 * 
	 * @return alle Test-LVE-Des
	 */
	public final SystemObject[] getAlleLveDes(){
		return new SystemObject[]{IB1_SM1_LVE1_DE1,
								  IB2_SM3_LVE1_DE1,
								  IB2_SM1_LVE1_DE1,
								  IB2_SM2_LVE1_DE1,
								  IB2_SM2_LVE1_DE2
		};
	}

	
	/**
	 * Erfragt alle Test-UFD-Des
	 * 
	 * @return alle Test-UFD-Des
	 */
	public final SystemObject[] getAlleUFDDes(){
		return new SystemObject[]{IB1_SM1_UFD1_DE1,
								  IB1_SM1_UFD1_DE2,
								  IB2_SM1_UFD1_DE1,
								  IB2_SM1_UFD1_DE2
		};
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
