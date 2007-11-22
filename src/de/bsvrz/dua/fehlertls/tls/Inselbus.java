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

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import de.bsvrz.dav.daf.main.ClientDavInterface;
import de.bsvrz.dav.daf.main.Data;
import de.bsvrz.dav.daf.main.config.SystemObject;
import de.bsvrz.dua.fehlertls.enums.TlsFehlerAnalyse;
import de.bsvrz.dua.fehlertls.fehlertls.DeFaApplikation;
import de.bsvrz.sys.funclib.bitctrl.konstante.Konstante;
import de.bsvrz.sys.funclib.debug.Debug;
import de.bsvrz.sys.funclib.operatingMessage.MessageCauser;
import de.bsvrz.sys.funclib.operatingMessage.MessageGrade;
import de.bsvrz.sys.funclib.operatingMessage.MessageSender;
import de.bsvrz.sys.funclib.operatingMessage.MessageType;

/**
 * TLS-Hierarchieelement Inselbus
 * 
 * @author BitCtrl Systems GmbH, Thierfelder
 *
 */
public class Inselbus 
extends AbstraktGeraet{
	
	/**
	 * Debug-Logger
	 */
	private static final Debug LOGGER = Debug.getLogger();
	

	/**
	 * Standardkonstruktor
	 * 
	 * @param dav Datenverteiler-Verbindund
	 * @param objekt ein Systemobjekt vom Typ <code>typ.anschlussPunkt</code> 
	 * (unterhalb eines Objektes vom Typ <code>typ.kri</code>)
	 * @param vater das in der TLS-Hierarchie ueber diesem Geraet liegende
	 * Geraet 
	 */
	protected Inselbus(ClientDavInterface dav, SystemObject objekt, AbstraktGeraet vater) {
		super(dav, objekt, vater);
		
		/**
		 * Initialisiere Steuermodule
		 */
		for(SystemObject komPartner:
				this.objekt.getNonMutableSet("AnschlussPunkteKommunikationsPartner").getElements()){ //$NON-NLS-1$
			Data konfigDatum = komPartner.getConfigurationData(TlsHierarchie.KONFIG_ATG);
			if(konfigDatum != null){
				SystemObject steuerModul = konfigDatum.getReferenceValue
							("KommunikationsPartner").getSystemObject(); //$NON-NLS-1$
				if(steuerModul != null){
					if(steuerModul.isOfType("typ.steuerModul")){ //$NON-NLS-1$
						this.kinder.add(new Sm(dav, steuerModul, this));						
					}else{
						LOGGER.warning("An " + komPartner +  //$NON-NLS-1$
								" (Inselbus: " + this.objekt +  //$NON-NLS-1$
								") duerfen nur Steuermodule definiert sein. Aber: " + //$NON-NLS-1$
								steuerModul + " (Typ: " + steuerModul.getType() + //$NON-NLS-1$ 
								")"); //$NON-NLS-1$				
					}
				}else{
					LOGGER.warning("An " + komPartner +  //$NON-NLS-1$
							" (Inselbus: " + this.objekt +  //$NON-NLS-1$
							") ist kein Steuermodul definiert"); //$NON-NLS-1$				
				}
			}else{
				LOGGER.warning("Konfiguration von " + komPartner +  //$NON-NLS-1$
						" (Inselbus: " + this.objekt +  //$NON-NLS-1$
						") konnte nicht ausgelesen werden. " + //$NON-NLS-1$
						"Das assoziierte Steuermodul wird ignoriert"); //$NON-NLS-1$
			}
		}

	}

	/**
	 * {@inheritDoc} 
	 */
	@Override
	public Art getGeraeteArt() {
		return Art.INSELBUS;
	}

	
	/**
	 * {@inheritDoc}<br>
	 * 
	 * Gibt <code>true</code> zurueck, wenn:<br> 
	 * 1. mehr als ein (wenigstens teilweise erfasstes) Steuermodul angeschlossen
	 * ist <b>und</b><br>
	 * 2. mehr als ein (wenigstens teilweise erfasstes) angeschlossenes Steuermodul
	 * keine Daten liefert<br>
	 */
	@Override
	public boolean kannFehlerHierPublizieren(long zeitStempel) {
		boolean kannHierPublizieren = false;
		
		/**
		 * ermittle alle Steuermodule, die unterhalb dieses Inselbusses liegen
		 * und wenigstens ein erfasstes DE haben (mit ihren erfassten DE)
		 */
		Map<Sm, Set<De>> erfassteSteuerModuleMitErfasstenDes = new HashMap<Sm, Set<De>>();

		for(De erfassteDe:this.getErfassteDes()){
			Sm steuerModulVonDe = (Sm)erfassteDe.getVater().getVater();
			Set<De> erfassteDesAmSteuerModul = erfassteSteuerModuleMitErfasstenDes.get(steuerModulVonDe);
			if(erfassteDesAmSteuerModul == null){
				erfassteDesAmSteuerModul = new HashSet<De>();
				erfassteSteuerModuleMitErfasstenDes.put(steuerModulVonDe, erfassteDesAmSteuerModul);
			}
			erfassteDesAmSteuerModul.add(erfassteDe);
		}

		/**
		 * Ermittle alle erfassten Steuermodule, die teilweise ausgefallen sind
		 */
		Map<Sm, Set<De>> timeOutSteuerModuleMitTimeOutDes = new HashMap<Sm, Set<De>>();
		for(Sm erfasstesSm:erfassteSteuerModuleMitErfasstenDes.keySet()){
			for(De erfassteDe:erfassteSteuerModuleMitErfasstenDes.get(erfasstesSm)){
				if(!erfassteDe.isInTime()){	
					Set<De> alleTimeOutDesVonSteuerModul = timeOutSteuerModuleMitTimeOutDes.get(erfasstesSm);	
					if(alleTimeOutDesVonSteuerModul == null){
						alleTimeOutDesVonSteuerModul = new HashSet<De>();
						timeOutSteuerModuleMitTimeOutDes.put(erfasstesSm, alleTimeOutDesVonSteuerModul);
					}
					alleTimeOutDesVonSteuerModul.add(erfassteDe);
				}
			}
		}

		/**
		 * Ermittle alle erfassten Steuermodule, die vollstaendig ausgefallen sind
		 */
		Set<Sm> totalAusfallSteuerModule = new HashSet<Sm>();
		for(Sm timeOutSteuerModul:timeOutSteuerModuleMitTimeOutDes.keySet()){
			/**
			 * ist das Steuermodul vollstaendig aufgefallen?
			 */
			int erfassteDes = erfassteSteuerModuleMitErfasstenDes.get(timeOutSteuerModul).size();
			int timeoutDes = timeOutSteuerModuleMitTimeOutDes.get(timeOutSteuerModul).size();
			if(erfassteDes == timeoutDes){
				totalAusfallSteuerModule.add(timeOutSteuerModul);
			}			
		}
		
		if(totalAusfallSteuerModule.size() == erfassteSteuerModuleMitErfasstenDes.keySet().size() ||
			totalAusfallSteuerModule.size() > 1){
			kannHierPublizieren = true;
		}
				
		return kannHierPublizieren;
	}
	

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void publiziereFehler(long zeitStempel) {
		/**
		 * ermittle alle Steuermodule, die unterhalb dieses Inselbusses liegen
		 * und wenigstens ein erfasstes DE haben (mit ihren erfassten DE)
		 */
		Map<Sm, Set<De>> erfassteSteuerModuleMitErfasstenDes = new HashMap<Sm, Set<De>>();
		
		for(De erfassteDe:this.getErfassteDes()){
			Sm steuerModulVonDe = (Sm)erfassteDe.getVater().getVater();
			Set<De> erfassteDesAmSteuerModul = erfassteSteuerModuleMitErfasstenDes.get(steuerModulVonDe);
			if(erfassteDesAmSteuerModul == null){
				erfassteDesAmSteuerModul = new HashSet<De>();
				erfassteSteuerModuleMitErfasstenDes.put(steuerModulVonDe, erfassteDesAmSteuerModul);
			}
			erfassteDesAmSteuerModul.add(erfassteDe);
		}

		/**
		 * Ermittle alle erfassten Steuermodule, die teilweise ausgefallen sind
		 */
		Map<Sm, Set<De>> timeOutSteuerModuleMitTimeOutDes = new HashMap<Sm, Set<De>>();
		for(Sm erfasstesSm:erfassteSteuerModuleMitErfasstenDes.keySet()){
			for(De erfassteDe:erfassteSteuerModuleMitErfasstenDes.get(erfasstesSm)){
				if(!erfassteDe.isInTime()){	
					Set<De> alleTimeOutDesVonSteuerModul = timeOutSteuerModuleMitTimeOutDes.get(erfasstesSm);	
					if(alleTimeOutDesVonSteuerModul == null){
						alleTimeOutDesVonSteuerModul = new HashSet<De>();
						timeOutSteuerModuleMitTimeOutDes.put(erfasstesSm, alleTimeOutDesVonSteuerModul);
					}
					alleTimeOutDesVonSteuerModul.add(erfassteDe);
				}
			}
		}

		/**
		 * Ermittle alle erfassten Steuermodule, die vollstaendig ausgefallen sind
		 */
		Set<Sm> totalAusfallSteuerModule = new HashSet<Sm>();
		for(Sm timeOutSteuerModul:timeOutSteuerModuleMitTimeOutDes.keySet()){
			/**
			 * ist das Steuermodul vollstaendig aufgefallen?
			 */
			int erfassteDes = erfassteSteuerModuleMitErfasstenDes.get(timeOutSteuerModul).size();
			int timeoutDes = timeOutSteuerModuleMitTimeOutDes.get(timeOutSteuerModul).size();
			if(erfassteDes == timeoutDes){
				totalAusfallSteuerModule.add(timeOutSteuerModul);
			}			
		}
		
		if(totalAusfallSteuerModule.size() == erfassteSteuerModuleMitErfasstenDes.keySet().size()){
			MessageSender.getInstance().sendMessage(
					MessageType.APPLICATION_DOMAIN,
					DeFaApplikation.getAppName(),
					MessageGrade.ERROR,
					this.objekt,
					new MessageCauser(DAV.getLocalUser(), Konstante.LEERSTRING, DeFaApplikation.getAppName()),
					"Modem am Inselbus " + this.objekt +//$NON-NLS-1$
			" oder Inselbus selbst defekt. Modem oder Inselbus instand setzen");//$NON-NLS-1$

			for(AbstraktGeraet steuerModulOhneDaten:totalAusfallSteuerModule){
				for(De de:timeOutSteuerModuleMitTimeOutDes.get(steuerModulOhneDaten)){
					de.publiziereFehlerUrsache(zeitStempel, TlsFehlerAnalyse.INSELBUS_MODEM_ODER_INSELBUS_DEFEKT);
				}					
			}
		}else{
			/**
			 * Nach Pid und Name sortierte Ausgabe der Steuermodule wegen JUnit-Tests
			 */
			SortedSet<AbstraktGeraet> totalAusfallSteuerModuleSortiert = new TreeSet<AbstraktGeraet>(
					new Comparator<AbstraktGeraet>(){

						public int compare(AbstraktGeraet o1, AbstraktGeraet o2) {
							return o1.getObjekt().toString().compareTo(o2.getObjekt().toString());
						}
						
					});
			totalAusfallSteuerModuleSortiert.addAll(totalAusfallSteuerModule);
			AbstraktGeraet[] steuerModulArray = totalAusfallSteuerModuleSortiert.toArray(new AbstraktGeraet[0]);
			String steuerModule = steuerModulArray[0].getObjekt().toString();
			for(int i = 1; i < steuerModulArray.length; i++){
				steuerModule += ", " + steuerModulArray[i].getObjekt().toString(); //$NON-NLS-1$
			}

			
			MessageSender.getInstance().sendMessage(
					MessageType.APPLICATION_DOMAIN,
					DeFaApplikation.getAppName(),
					MessageGrade.ERROR,
					this.objekt,
					new MessageCauser(DAV.getLocalUser(), Konstante.LEERSTRING, DeFaApplikation.getAppName()),
					"Inselbus " + this.objekt + " gestört: Für die DE der Steuermodule " //$NON-NLS-1$ //$NON-NLS-2$
					+ steuerModule + " sind keine Daten verfügbar. Inselbus " + this.objekt + " instand setzen");//$NON-NLS-1$ //$NON-NLS-2$

			for(AbstraktGeraet steuerModulOhneDaten:totalAusfallSteuerModule){
				for(De de:timeOutSteuerModuleMitTimeOutDes.get(steuerModulOhneDaten)){
					de.publiziereFehlerUrsache(zeitStempel, TlsFehlerAnalyse.INSELBUS_DEFEKT);
				}					
			}
		}
	}

}
