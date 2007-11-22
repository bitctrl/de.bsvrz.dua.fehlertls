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
import java.util.GregorianCalendar;

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
import de.bsvrz.dua.fehlertls.tls.DeErfassungsZustand.Zustand;
import de.bsvrz.sys.funclib.bitctrl.dua.ObjektWecker;
import de.bsvrz.sys.funclib.bitctrl.dua.schnittstellen.IObjektWeckerListener;
import de.bsvrz.sys.funclib.bitctrl.konstante.Konstante;
import de.bsvrz.sys.funclib.debug.Debug;
import de.bsvrz.sys.funclib.operatingMessage.MessageCauser;
import de.bsvrz.sys.funclib.operatingMessage.MessageGrade;
import de.bsvrz.sys.funclib.operatingMessage.MessageSender;
import de.bsvrz.sys.funclib.operatingMessage.MessageType;

/**
 * Repraesentiert ein DE fuer die DeFa. Liest alle generischen DE-Parameter
 * und meldet sich auf alle Daten an, auf die von dem DE gewartet werden soll.
 * 
 * @author BitCtrl Systems GmbH, Thierfelder
 *
 */
public class De
extends AbstraktGeraet
implements ClientReceiverInterface,
		   ClientSenderInterface,
		   IObjektWeckerListener,
		   IDeErfassungsZustandListener,
		   IParameterTlsFehlerAnalyseListener{
	
	/**
	 * Die Zeit, die mindestens zwischen Daten, Fehlererkennung und
	 * Fehleranalyse vergehen muss
	 */
	private static final long STANDARD_ZEIT_ABSTAND = 1000L;
	
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
	private long zeitVerzugFehlerErkennung = Long.MIN_VALUE;
	
	/**
	 * Der zusätzliche Zeitverzug, der nach der Fehlererkennung bis zur Fehlerermittlung
	 * abgewartet werden muss
	 */
	private long zeitVerzugFehlerErmittlung = Long.MIN_VALUE;
	
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
	 * aktueller Erfassungszustand bzgl. der DeFa
	 */
	private DeErfassungsZustand.Zustand aktuellerZustand = null;
	
	/**
	 * zur einmaligen Publikation von Fehlermeldungen
	 */
	private EinmaligeNachrichtenPublikator einzelPublikator = new EinmaligeNachrichtenPublikator();

		
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
		
		if(FEHLER_DATEN_BESCHREIBUNG == null){
			FEHLER_DATEN_BESCHREIBUNG = new DataDescription(
					dav.getDataModel().getAttributeGroup("atg.tlsFehlerAnalyse"), //$NON-NLS-1$
					dav.getDataModel().getAspect("asp.analyse")); //$NON-NLS-1$
		}
		
		ParameterTlsFehlerAnalyse.getInstanz(dav, DeFaApplikation.getTlsFehlerAnalyseObjekt()).
				addListener(this);
						
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
		
		new DeErfassungsZustand(DAV, this.getObjekt()).addListener(this);
	}

	
	/**
	 * {@inheritDoc}
	 */
	public void update(ResultData[] erwarteteResultate) {
		if(erwarteteResultate != null){
			for(ResultData erwartetesResultat:erwarteteResultate){
				if(erwartetesResultat != null){

					/**
					 * Nutzdatum empfangen
					 */
					if(erwartetesResultat.getData() != null){
						this.inTime = true;

						this.versucheErwartung();
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
	 * Publiziert eine erkannte Fehlerursache an diesem DE
	 * 
	 * @param fehlerZeit die Zeit mit der der Fehler assoziiert ist
	 * (Die Zeit, zu der ausgefallene Datensatz erwartet wurde)
	 * @param tlsFehler die Fehlerursache
	 */
	public final void publiziereFehlerUrsache(final long fehlerZeit, final TlsFehlerAnalyse tlsFehler){
		this.zeitStempelLetzterPublizierterFehler = fehlerZeit;
		
		Data datum = DAV.createData(FEHLER_DATEN_BESCHREIBUNG.getAttributeGroup());
		datum.getUnscaledValue("TlsFehlerAnalyse").set(tlsFehler.getCode()); //$NON-NLS-1$
		try {
			DAV.sendData(new ResultData(this.objekt, FEHLER_DATEN_BESCHREIBUNG, fehlerZeit, datum));
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("Datum " + datum + " konnte fuer " + //$NON-NLS-1$ //$NON-NLS-2$
					this.objekt + " nicht publiziert werden"); //$NON-NLS-1$
		}
		
		this.versucheErwartung();
	}
	
	
	/**
	 * Erfragt den aktuellen Erfassungszustand dieses DE
	 * 
	 * @return der aktuellen Erfassungszustand dieses DE
	 */
	public final synchronized DeErfassungsZustand.Zustand getZustand(){
		return this.aktuellerZustand;
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
	public synchronized void aktualisiereParameterTlsFehlerAnalyse(
			long zeitverzugFehlerErkennung,
			long zeitverzugFehlerErmittlung) {
		this.zeitVerzugFehlerErkennung = zeitverzugFehlerErkennung;
		this.zeitVerzugFehlerErmittlung = zeitverzugFehlerErmittlung;
		this.versucheErwartung();
	}
	

	/**
	 * {@inheritDoc}
	 */
	public synchronized void aktualisiereErfassungsZustand(Zustand zustand) {
		this.aktuellerZustand = zustand;
		this.versucheErwartung();
	}
	
	
	/**
	 * Initiiert die Erwartung eines Nutzdatums dieses DE, wenn dies
	 * aufgrund der aktuellen Parameter bzw. Online-Daten des DE moeglich
	 * bzw. notwendig ist
	 */
	private final synchronized void versucheErwartung(){
		if(this.zeitVerzugFehlerErkennung >= 0){
			
			if(this.aktuellerZustand != null &&
			   this.aktuellerZustand.getErfassungsIntervallDauer() > 0){
								
				this.letzterErwarteterDatenZeitpunkt = getNaechstenIntervallZeitstempel(
						System.currentTimeMillis(), this.aktuellerZustand.getErfassungsIntervallDauer()); 
				long nachsterErwarteterZeitpunkt = this.letzterErwarteterDatenZeitpunkt + 
												   zeitVerzugFehlerErkennung;
				
				FEHLER_WECKER.setWecker(this, nachsterErwarteterZeitpunkt + STANDARD_ZEIT_ABSTAND);
			}else{
				if(this.aktuellerZustand != null && this.aktuellerZustand.isInitialisiert()){
					if(this.aktuellerZustand.getErfassungsIntervallDauer() <= 0){
						FEHLER_WECKER.setWecker(this, ObjektWecker.AUS);
						this.einzelPublikator.publiziere(this.aktuellerZustand.getGrund());
					}
				}else{
					LOGGER.warning("DE " + De.this.objekt + " ist (noch) nicht vollstaendig initialisiert");  //$NON-NLS-1$//$NON-NLS-2$
				}
			}
			
		}else{
			LOGGER.warning("Kann keine Daten fuer " + this.objekt + //$NON-NLS-1$
					" erwarten, da noch keine (sinnvollen) " + //$NON-NLS-1$
					"Parameter zur TLS-Fehleranalyse empfangen wurden"); //$NON-NLS-1$
		}
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
	 * {@inheritDoc}
	 */
	public void alarm() {
		/**
		 * Ueberpruefe Bedingungen nach Afo-9.0 DUA BW C1C2-21 (S. 45)
		 */
		
		DeErfassungsZustand.Zustand zustand = De.this.aktuellerZustand;
		
		if(zustand.isErfasst()){
			De.this.inTime = false;
			final long fehlerZeit = De.this.letzterErwarteterDatenZeitpunkt;
			
			ANALYSE_WECKER.setWecker(
				new IObjektWeckerListener(){

					public void alarm() {
						if(!De.this.inTime){
							versucheFehlerPublikation(fehlerZeit);
						}
					}
						
				},
				fehlerZeit + zeitVerzugFehlerErkennung + zeitVerzugFehlerErmittlung + 2 * STANDARD_ZEIT_ABSTAND);								
		}else{
			if(zustand.isInitialisiert()){
				De.this.einzelPublikator.publiziere(zustand.getGrund());
			}else{
				LOGGER.warning(De.this.objekt + " ist (noch) nicht vollstaendig initialisiert");  //$NON-NLS-1$
			}
		}			
	}
	
	
	/**
	 * Erfragt den ersten Zeitstempel, der sich echt (> 500ms) nach dem Zeitstempel
	 * <code>jetzt</code> (angenommenr Jetzt-Zeitpunkt) befindet und der
	 * zur uebergebenen Erfassungsintervalllange passt 
	 * 
	 * @param jetzt angenommener Jetzt-Zeitpunkt (in ms)
	 * @param intervallLaenge eine Erfassungsintervalllaenge (in ms)
	 * @return der ersten Zeitstempel, der sich echt nach dem Zeitstempel
	 * <code>jetzt</code> befindet und der zur uebergebenen Erfassungsintervalllange
	 * passt (in ms)
	 */
	private static final long getNaechstenIntervallZeitstempel(final long jetzt,
															  final long intervallLaenge){
		final long jetztPlus = jetzt + 500L;
		GregorianCalendar stundenAnfang = new GregorianCalendar();
		stundenAnfang.setTimeInMillis(jetztPlus);
		stundenAnfang.set(Calendar.MINUTE, 0);
		stundenAnfang.set(Calendar.SECOND, 0);
		stundenAnfang.set(Calendar.MILLISECOND, 0);			
		
		long naechsterIntervallZeitstempel;
		if(intervallLaenge >= Konstante.STUNDE_IN_MS){
			stundenAnfang.set(Calendar.HOUR_OF_DAY, 0);
			final long msNachTagesAnfang = jetztPlus - stundenAnfang.getTimeInMillis();
			final long intervalleSeitTagesAnfang = msNachTagesAnfang / intervallLaenge;
			naechsterIntervallZeitstempel = stundenAnfang.getTimeInMillis() +
											(intervalleSeitTagesAnfang + 1) * intervallLaenge;
		}else{
			final long msNachStundenAnfang = jetztPlus - stundenAnfang.getTimeInMillis();
			final long intervalleSeitStundenAnfang = msNachStundenAnfang / intervallLaenge;
			naechsterIntervallZeitstempel = stundenAnfang.getTimeInMillis() +
											(intervalleSeitStundenAnfang + 1) * intervallLaenge; 
		}
				
		return naechsterIntervallZeitstempel;
	}
	
	
	/**
	 * Klasse zur einmaligen Publikation von Fehlermeldungen
	 * 
	 * @author BitCtrl Systems GmbH, Thierfelder
	 *
	 */
	private class EinmaligeNachrichtenPublikator{
		
		/**
		 * letzte fuer dieses DE publizierte einmalige Betriebsmeldung
		 */
		private String letzteEinmaligeNachricht = Konstante.LEERSTRING;
		
		
		/**
		 * Publiziert eine Fehlermeldung einmalig
		 * 
		 * @param text der Text der Fehlermeldung
		 */
		protected final void publiziere(final String text){
			synchronized (De.this) {
				if( !this.letzteEinmaligeNachricht.equals(text) ){
					this.letzteEinmaligeNachricht = text;
					MessageSender.getInstance().sendMessage(
							MessageType.APPLICATION_DOMAIN,
							DeFaApplikation.getAppName(),
							MessageGrade.ERROR,
							De.this.objekt,
							new MessageCauser(DAV.getLocalUser(), Konstante.LEERSTRING, DeFaApplikation.getAppName()),
							text);
					LOGGER.info(De.this.objekt + ", " + text); //$NON-NLS-1$
				}else{
					LOGGER.info(De.this.objekt + ", Keine doppelte Ausgabe von: " + text); //$NON-NLS-1$
				}				
			}
		}
	}
}
