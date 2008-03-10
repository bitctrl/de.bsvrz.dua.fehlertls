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

import com.bitctrl.Constants;

import de.bsvrz.dav.daf.main.ClientDavInterface;
import de.bsvrz.dav.daf.main.config.SystemObject;
import de.bsvrz.dua.fehlertls.de.DeFaException;
import de.bsvrz.dua.fehlertls.enums.TlsFehlerAnalyse;
import de.bsvrz.dua.fehlertls.fehlertls.DeFaApplikation;
import de.bsvrz.sys.funclib.debug.Debug;
import de.bsvrz.sys.funclib.operatingMessage.MessageCauser;
import de.bsvrz.sys.funclib.operatingMessage.MessageGrade;
import de.bsvrz.sys.funclib.operatingMessage.MessageSender;
import de.bsvrz.sys.funclib.operatingMessage.MessageType;

/**
 * TLS-Hierarchieelement EAK
 * 
 * @author BitCtrl Systems GmbH, Thierfelder
 *
 */
public class Eak
extends AbstraktGeraet{
	
	/**
	 * Debug-Logger
	 */
	private static final Debug LOGGER = Debug.getLogger();
	
	
	/**
	 * Standardkonstruktor
	 * 
	 * @param dav Datenverteiler-Verbindund
	 * @param objekt ein Systemobjekt vom Typ <code>typ.eak</code>
	 * @param vater das in der TLS-Hierarchie ueber diesem Geraet liegende
	 * Geraet 
	 */
	protected Eak(ClientDavInterface dav, SystemObject objekt,
			AbstraktGeraet vater) {
		super(dav, objekt, vater);		
		for(SystemObject deObj:this.objekt.getNonMutableSet("De").getElements()){ //$NON-NLS-1$
			if(deObj.isValid()){
				try {
					De de = new De(dav, deObj, this);
					this.kinder.add(de);
				} catch (DeFaException e) {
					e.printStackTrace();
					LOGGER.warning("De " + deObj + " konnte nicht initialisiert werden. ", e); //$NON-NLS-1$ //$NON-NLS-2$
				}
			}
		}
	}
	

	/**
	 * {@inheritDoc} 
	 */
	@Override
	public Art getGeraeteArt() {
		return Art.EAK;
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public void publiziereFehler(long zeitStempel) {
		MessageSender.getInstance().sendMessage(
				MessageType.APPLICATION_DOMAIN,
				DeFaApplikation.getAppName(),
				MessageGrade.ERROR,
				this.objekt,
				new MessageCauser(DAV.getLocalUser(), Constants.EMPTY_STRING, DeFaApplikation.getAppName()),
				"EAK " + this.objekt + " am Steuermodul " + this.getVater().getObjekt() + " defekt." + //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
						" EAK " + this.objekt + " am Steuermodul " + this.getVater().getObjekt() + " instand setzen");  //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
		
		for(De de:this.getErfassteDes()){
			de.publiziereFehlerUrsache(zeitStempel, TlsFehlerAnalyse.EAK_AN_SM_DEFEKT);
		}
	}

}
