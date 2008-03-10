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
import de.bsvrz.dua.fehlertls.enums.TlsFehlerAnalyse;
import de.bsvrz.dua.fehlertls.fehlertls.DeFaApplikation;
import de.bsvrz.sys.funclib.operatingMessage.MessageCauser;
import de.bsvrz.sys.funclib.operatingMessage.MessageGrade;
import de.bsvrz.sys.funclib.operatingMessage.MessageSender;
import de.bsvrz.sys.funclib.operatingMessage.MessageType;

/**
 * TLS-Hierarchieelement Steuermodul
 * 
 * @author BitCtrl Systems GmbH, Thierfelder
 *
 */
public class Sm
extends AbstraktGeraet{

		
	/**
	 * Standardkonstruktor
	 * 
	 * @param dav Datenverteiler-Verbindund
	 * @param objekt ein Systemobjekt vom Typ <code>typ.steuerModul</code>
	 * @param vater das in der TLS-Hierarchie ueber diesem Geraet liegende
	 * Geraet 
	 */
	protected Sm(ClientDavInterface dav, SystemObject objekt, AbstraktGeraet vater) {
		super(dav, objekt, vater);
		for(SystemObject eak:this.objekt.getNonMutableSet("Eak").getElements()){ //$NON-NLS-1$
			if(eak.isValid()){
				this.kinder.add(new Eak(dav, eak, this));
			}
		}
	}

	
	/**
	 * {@inheritDoc} 
	 */
	@Override
	public Art getGeraeteArt() {
		return Art.SM;
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
				"Modem am Steuermodul " + this.objekt + " oder Steuermodul defekt. " + //$NON-NLS-1$ //$NON-NLS-2$
					"Modem am Steuermodul " + this.objekt + " oder Steuermodul instand setzen");//$NON-NLS-1$ //$NON-NLS-2$
		
		for(De de:this.getErfassteDes()){
			de.publiziereFehlerUrsache(zeitStempel, TlsFehlerAnalyse.SM_MODEM_ODER_SM_DEFEKT);
		}
	}

}
