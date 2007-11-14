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

import java.util.Date;

import de.bsvrz.dav.daf.main.ClientDavInterface;
import de.bsvrz.dav.daf.main.ClientReceiverInterface;
import de.bsvrz.dav.daf.main.ClientSenderInterface;
import de.bsvrz.dav.daf.main.Data;
import de.bsvrz.dav.daf.main.DataDescription;
import de.bsvrz.dav.daf.main.OneSubscriptionPerSendData;
import de.bsvrz.dav.daf.main.ReceiveOptions;
import de.bsvrz.dav.daf.main.ReceiverRole;
import de.bsvrz.dav.daf.main.ResultData;
import de.bsvrz.dav.daf.main.SenderRole;
import de.bsvrz.dav.daf.main.config.SystemObject;
import de.bsvrz.dua.fehlertls.de.DeFaException;
import de.bsvrz.dua.fehlertls.de.DeTypLader;
import de.bsvrz.dua.fehlertls.enums.TlsFehlerAnalyse;
import de.bsvrz.dua.fehlertls.fehlertls.DeFaApplikation;
import de.bsvrz.dua.fehlertls.parameter.IParameterTlsFehlerAnalyseListener;
import de.bsvrz.dua.fehlertls.parameter.ParameterTlsFehlerAnalyse;
import de.bsvrz.sys.funclib.bitctrl.dua.ObjektWecker;
import de.bsvrz.sys.funclib.bitctrl.dua.schnittstellen.IObjektWeckerListener;
import de.bsvrz.sys.funclib.bitctrl.konstante.Konstante;
import de.bsvrz.sys.funclib.debug.Debug;
import de.bsvrz.sys.funclib.operatingMessage.MessageCauser;
import de.bsvrz.sys.funclib.operatingMessage.MessageGrade;
import de.bsvrz.sys.funclib.operatingMessage.MessageSender;
import de.bsvrz.sys.funclib.operatingMessage.MessageType;

/**
 * TODO
 * 
 * @author BitCtrl Systems GmbH, Thierfelder
 *
 */
public class De
extends AbstraktGeraet
implements ClientReceiverInterface,
		   ClientSenderInterface,
		   IObjektWeckerListener{
	
	/**
	 * Debug-Logger
	 */
	private static final Debug LOGGER = Debug.getLogger();
	
	/**
	 * weckt alle Objekte dieser Art, wenn fuer sie ein Fehler
	 * detektiert wurde
	 */
	private static ObjektWecker FEHLER_WECKER = new ObjektWecker();
	
	/**
	 * weckt alle Objekte dieser Art, wenn fuer sie ein Fehler
	 * analysiert werden soll
	 */
	private static ObjektWecker ANALYSE_WECKER = new ObjektWecker();
	
	/**
	 * <code>atg.tlsFehlerAnalyse</code>, <code>asp.analyse</code>
	 */
	private static DataDescription FEHLER_DATEN_BESCHREIBUNG = null;
	
	/**
	 * Der zusätzliche Zeitverzug, der nach dem erwarteten Empfangszeitpunkt noch bis zur
	 * Erkennung eines nicht gelieferten Messwertes abgewartet werden muss
	 */
	private static long ZEIT_VERZUG_FEHLER_ERKENNUNG = Long.MIN_VALUE;
	
	/**
	 * Der zusätzliche Zeitverzug, der nach der Fehlererkennung bis zur Fehlerermittlung
	 * abgewartet werden muss
	 */
	private static long ZEIT_VERZUG_FEHLER_ERMITTLUNG = Long.MIN_VALUE;
	
	/**
	 * dieses Objekt wird alarmiert, wenn ein Fehler für dieses Objekt
	 * erkannt wurde (also ein erwarteter Wert ausgefallen ist)
	 */
	private FehlerAlarmPunkt fehlerAlarmPunkt = new FehlerAlarmPunkt();
	
	/**
	 * als letztes wurde fuer diesen Zeitstempel (Datenzeit) ein Nutzdatum
	 * von diesem DE erwartet
	 */
	private long letzterErwarteterDatenZeitpunkt = -1;
	
	/**
	 * Zeitstempel des letzten fuer dieses DE publizierten Fehlers
	 */
	private long zeitStempelLetzterPublizierterFehler = -1;

	
	/**
	 * erfragt, ob dieses DE zur Zeit "in Time" ist
	 */
	private boolean inTime = true;
	
		
	/**
	 * Standardkonstruktor
	 * 
	 * @param dav Datenverteiler-Verbindund
	 * @param objekt ein Systemobjekt vom Typ <code>typ.de</code>
	 * @param vater das in der TLS-Hierarchie ueber diesem Geraet liegende
	 * Geraet 
	 */
	protected De(ClientDavInterface dav, SystemObject objekt, AbstraktGeraet vater)
	throws DeFaException{
		super(dav, objekt, vater);
		
		/**
		 * stelle statisch die Parameter der TLS-Fehler-Analyse
		 * zu Verfügung
		 */
		if(ZEIT_VERZUG_FEHLER_ERKENNUNG == Long.MIN_VALUE){
			ZEIT_VERZUG_FEHLER_ERKENNUNG = -1;
			FEHLER_DATEN_BESCHREIBUNG = new DataDescription(
					dav.getDataModel().getAttributeGroup("atg.tlsFehlerAnalyse"), //$NON-NLS-1$
					dav.getDataModel().getAspect("asp.analyse")); //$NON-NLS-1$

			ParameterTlsFehlerAnalyse.getInstanz(dav, DeFaApplikation.getTlsFehlerAnalyseObjekt()).
				addListener(new IParameterTlsFehlerAnalyseListener(){

					public void aktualisiereParameterTlsFehlerAnalyse(
							long zeitverzugFehlerErkennung,
							long zeitverzugFehlerErmittlung) {
						ZEIT_VERZUG_FEHLER_ERKENNUNG = zeitverzugFehlerErkennung;
						ZEIT_VERZUG_FEHLER_ERMITTLUNG = zeitverzugFehlerErmittlung;
					}
					
				});
		}
				
		for(DataDescription messWertBeschreibung:DeTypLader.getDeTyp(objekt.getType()).getDeFaMesswertDataDescriptions(dav)){
			dav.subscribeReceiver(this, objekt, messWertBeschreibung,
					ReceiveOptions.normal(), ReceiverRole.receiver());
			LOGGER.info("Ueberwache " + this.objekt.getPid() + ", " + messWertBeschreibung);  //$NON-NLS-1$//$NON-NLS-2$
		}
		try {
			dav.subscribeSender(this, objekt, FEHLER_DATEN_BESCHREIBUNG,
					SenderRole.source());
		} catch (OneSubscriptionPerSendData e) {
			throw new DeFaException(e);
		}		
	}

	
	/**
	 * {@inheritDoc}
	 */
	public void update(ResultData[] erwarteteResultate) {
		if(erwarteteResultate != null){
			for(ResultData erwartetesResultat:erwarteteResultate){
				if(erwartetesResultat != null){
					
					if(ZEIT_VERZUG_FEHLER_ERKENNUNG > 0 &&
						ZEIT_VERZUG_FEHLER_ERMITTLUNG > 0){
						
						/**
						 * Nutzdatum empfangen
						 */
						if(erwartetesResultat.getData() != null){
							this.inTime = true;
							
							DeErfassungsZustand.Zustand erfassungsZustand =
								DeErfassungsZustand.getInstanz(erwartetesResultat.getObject()).getZustand();
							
							if(erfassungsZustand.isErfasst()){
								this.letzterErwarteterDatenZeitpunkt = erwartetesResultat.getDataTime() + 
																	   2 * erfassungsZustand.getErfassungsIntervallDauer();
								long nachsterErwarteterZeitpunkt = this.letzterErwarteterDatenZeitpunkt + 
																   ZEIT_VERZUG_FEHLER_ERKENNUNG;
								FEHLER_WECKER.setWecker(this.fehlerAlarmPunkt, nachsterErwarteterZeitpunkt);									
							}		
						}
						
					}else{
						LOGGER.info("Es wurden noch keine DeFa-Parameter empfangen"); //$NON-NLS-1$
					}
					
				}
			}
		}
	}


	/**
	 * {@inheritDoc} 
	 */
	@Override
	public Art getGeraeteArt() {
		return Art.DE;
	}


	/**
	 * {@inheritDoc}<br>
	 * 
	 * Diese Methode wird aufgerufen, wenn fuer dieses DE ein Fehlergrund ermittelt
	 * und publiziert werden soll. 
	 */
	public void alarm() {
		System.out.println(new Date() + ", UrsachenAlarm fuer " + De.this.objekt);
		if(!this.inTime){
			System.out.println("2" + De.this.objekt);
			versucheFehlerPublikation(this.letzterErwarteterDatenZeitpunkt);
		}
	}	
	
	
	/**
	 * Publiziert eine erkannte Fehlerursache an diesem DE
	 * 
	 * @param fehlerZeit die Zeit mit der der Fehler assoziiert ist
	 * (Die Zeit, zu der ausgefallene Datensatz erwartet wurde)
	 * @param tlsFehler die Fehlerursache
	 */
	public final void publiziereFehlerUrsache(final long fehlerZeit, final TlsFehlerAnalyse tlsFehler){
		this.zeitStempelLetzterPublizierterFehler = fehlerZeit;
		
		Data datum = DAV.createData(FEHLER_DATEN_BESCHREIBUNG.getAttributeGroup());
		try {
			DAV.sendData(new ResultData(this.objekt, FEHLER_DATEN_BESCHREIBUNG, fehlerZeit, datum));
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("Datum " + datum + " konnte fuer " + //$NON-NLS-1$ //$NON-NLS-2$
					this.objekt + " nicht publiziert werden"); //$NON-NLS-1$
		}
		
		DeErfassungsZustand.Zustand erfassungsZustand =
			DeErfassungsZustand.getInstanz(this.getObjekt()).getZustand();
		
		if(erfassungsZustand.isErfasst()){
			this.letzterErwarteterDatenZeitpunkt = fehlerZeit + 
												   2 * erfassungsZustand.getErfassungsIntervallDauer();
			long nachsterErwarteterZeitpunkt = this.letzterErwarteterDatenZeitpunkt + 
											   ZEIT_VERZUG_FEHLER_ERKENNUNG;
			FEHLER_WECKER.setWecker(this.fehlerAlarmPunkt, nachsterErwarteterZeitpunkt);									
		}
	}
	
	
	/**
	 * Erfragt, ob dieses DE im Moment Daten im Sinne der DeFa hat (Also ob
	 * Daten vorhanden sind, und ob diese rechtzeitig angekommen sind)
	 * 
	 * @return ob dieses DE im Moment Daten im Sinne der DeFa hat
	 */
	public final boolean isInTime(){
		return this.inTime;
	}
	
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean kannFehlerHierPublizieren(long zeitStempel) {
		return zeitStempel > this.zeitStempelLetzterPublizierterFehler;
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public void publiziereFehler(long zeitStempel) {
		this.publiziereFehlerUrsache(zeitStempel, TlsFehlerAnalyse.UNBEKANNT);
	}
	
	
	/**
	 * {@inheritDoc}
	 */
	public void dataRequest(SystemObject object,
			DataDescription dataDescription, byte state) {
		// wird ignoriert (Anmeldung als Quelle)		
	}


	/**
	 * {@inheritDoc}
	 */
	public boolean isRequestSupported(SystemObject object,
			DataDescription dataDescription) {
		return false;
	}
	
	
	/**
	 * Dieses Objekt wird alarmiert, wenn ein Fehler für dieses Objekt
	 * erkannt wurde (also ein erwarteter Wert ausgefallen ist)
	 * 
	 * @author BitCtrl Systems GmbH, Thierfelder
	 *
	 */
	private class FehlerAlarmPunkt
	implements IObjektWeckerListener{

		/**
		 * {@inheritDoc}
		 */
		public void alarm() {
			/**
			 * Ueberpruefe Bedingungen nach Afo-9.0 DUA BW C1C2-21 (S. 45)
			 */
			System.out.println(new Date() + ", FehlerAlarm fuer " + De.this.objekt);
			
			DeErfassungsZustand.Zustand zustand = DeErfassungsZustand.getInstanz(De.this.objekt).getZustand();
			if(zustand.isErfasst()){
				De.this.inTime = false;
				ANALYSE_WECKER.setWecker(De.this, De.this.letzterErwarteterDatenZeitpunkt + ZEIT_VERZUG_FEHLER_ERMITTLUNG);								
			}else{
				if(zustand.isInitialisiert()){
					MessageSender.getInstance().sendMessage(
							MessageType.APPLICATION_DOMAIN,
							DeFaApplikation.getAppName(),
							MessageGrade.ERROR,
							De.this.objekt,
							new MessageCauser(DAV.getLocalUser(), Konstante.LEERSTRING, DeFaApplikation.getAppName()),
							zustand.getGrund());
					LOGGER.info(zustand.getGrund());
				}else{
					LOGGER.warning("DE " + De.this.objekt + " ist (noch) nicht vollstaendig initialisiert");  //$NON-NLS-1$//$NON-NLS-2$
				}
			}			
		}	
	}
}
