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

import de.bsvrz.dav.daf.main.ClientDavInterface;
import de.bsvrz.dav.daf.main.Data;
import de.bsvrz.dav.daf.main.config.SystemObject;
import de.bsvrz.sys.funclib.debug.Debug;

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

}
