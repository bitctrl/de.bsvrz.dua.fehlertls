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

import java.util.HashSet;
import java.util.Set;

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
 * TODO
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
	 * 1. mehr als ein Steuermodul angeschlossen ist und<br>
	 * 2. mehr als ein angeschlossenes Steuermodul keine Daten liefert<br>
	 */
	@Override
	public boolean kannFehlerHierPublizieren(long zeitStempel) {
		boolean kannHierPublizieren = false;

		if(this.kinder.size() > 1){
			int steuerModuleOhneDaten = 0;
			for(AbstraktGeraet steuerModul:this.kinder){
				
				boolean alleDeKeineDaten = true;
				for(De de:steuerModul.getDes()){
					if(de.isInTime()){
						alleDeKeineDaten = false;
						break;
					}
				}
				
				if(alleDeKeineDaten){
					steuerModuleOhneDaten++;
				}
				
				if(steuerModuleOhneDaten > 1){
					kannHierPublizieren = true;
					break;
				}
			}
		}
		
		return kannHierPublizieren;
	}
	

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void publiziereFehler(long zeitStempel) {
		Set<AbstraktGeraet> steuerModuleOhneDaten = new HashSet<AbstraktGeraet>();

		for(AbstraktGeraet steuerModul:this.kinder){
				
			boolean alleDeKeineDaten = true;
			for(De de:steuerModul.getDes()){
				if(de.isInTime()){
					alleDeKeineDaten = false;
					break;
				}
			}
			
			if(alleDeKeineDaten){
				steuerModuleOhneDaten.add(steuerModul);
			}			
		}
		
		if(steuerModuleOhneDaten.size() == this.kinder.size()){
			AbstraktGeraet[] steuerModulArray = steuerModuleOhneDaten.toArray(new AbstraktGeraet[0]);
			String steuerModule = steuerModulArray[0].getObjekt().toString();
			for(int i = 1; i < steuerModulArray.length; i++){
				steuerModule += ", " + steuerModulArray[i]; //$NON-NLS-1$
			}			
			
			MessageSender.getInstance().sendMessage(
					MessageType.APPLICATION_DOMAIN,
					DeFaApplikation.getAppName(),
					MessageGrade.ERROR,
					this.objekt,
					new MessageCauser(DAV.getLocalUser(), Konstante.LEERSTRING, DeFaApplikation.getAppName()),
					"Inselbus " + this.objekt + " gestört: Für die DE der Steuermodule " //$NON-NLS-1$ //$NON-NLS-2$
					+ steuerModule + " sind keine Daten verfügbar. Inselbus " + this.objekt + " instand setzen");//$NON-NLS-1$ //$NON-NLS-2$
			
			for(AbstraktGeraet steuerModulOhneDaten:steuerModuleOhneDaten){
				for(De de:steuerModulOhneDaten.getDes()){
					de.publiziereFehlerUrsache(zeitStempel, TlsFehlerAnalyse.INSELBUS_DEFEKT);
				}					
			}
		}else{
			MessageSender.getInstance().sendMessage(
					MessageType.APPLICATION_DOMAIN,
					DeFaApplikation.getAppName(),
					MessageGrade.ERROR,
					this.objekt,
					new MessageCauser(DAV.getLocalUser(), Konstante.LEERSTRING, DeFaApplikation.getAppName()),
					"Modem am Inselbus " + this.objekt +//$NON-NLS-1$
					" oder Inselbus selbst defekt. Modem oder Inselbus instand setzen");//$NON-NLS-1$
			
			for(De de:this.getDes()){
				de.publiziereFehlerUrsache(zeitStempel, TlsFehlerAnalyse.INSELBUS_MODEM_ODER_INSELBUS_DEFEKT);
			}
		}
	}

}
