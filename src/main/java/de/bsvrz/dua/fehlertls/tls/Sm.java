/*
 * Segment Datenübernahme und Aufbereitung (DUA), Fehleranalyse fehlende Messdaten TLS
 * Copyright (C) 2007 BitCtrl Systems GmbH 
 * Copyright 2016 by Kappich Systemberatung Aachen
 * 
 * This file is part of de.bsvrz.dua.fehlertls.
 * 
 * de.bsvrz.dua.fehlertls is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * de.bsvrz.dua.fehlertls is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with de.bsvrz.dua.fehlertls.  If not, see <http://www.gnu.org/licenses/>.

 * Contact Information:
 * Kappich Systemberatung
 * Martin-Luther-Straße 14
 * 52062 Aachen, Germany
 * phone: +49 241 4090 436 
 * mail: <info@kappich.de>
 */

package de.bsvrz.dua.fehlertls.tls;

import de.bsvrz.dav.daf.main.ClientDavInterface;
import de.bsvrz.dav.daf.main.config.SystemObject;
import de.bsvrz.dua.fehlertls.enums.TlsFehlerAnalyse;
import de.bsvrz.sys.funclib.operatingMessage.MessageGrade;
import de.bsvrz.sys.funclib.operatingMessage.MessageTemplate;
import de.bsvrz.sys.funclib.operatingMessage.MessageType;
import de.bsvrz.sys.funclib.operatingMessage.OperatingMessage;

/**
 * TLS-Hierarchieelement Steuermodul.
 * 
 * @author BitCtrl Systems GmbH, Thierfelder
 * 
 * @version $Id$
 */
public class Sm extends AbstraktGeraet {
	
	private static final MessageTemplate TEMPLATE = new MessageTemplate(
			MessageGrade.ERROR,
			MessageType.APPLICATION_DOMAIN,
			MessageTemplate.fixed("Modem am Steuermodul "),
			MessageTemplate.object(),
			MessageTemplate.fixed(" oder Steuermodul defekt. Modem am Steuermodul "),
            MessageTemplate.object(),
			MessageTemplate.fixed(" oder Steuermodul instand setzen. "),
			MessageTemplate.ids()
	).withIdFactory(message -> message.getObject().getPidOrId() + " [DUA-FT-FU]");
	
	/**
	 * Standardkonstruktor.
	 * 
	 * @param dav
	 *            Datenverteiler-Verbindund
	 * @param objekt
	 *            ein Systemobjekt vom Typ <code>typ.steuerModul</code>
	 * @param vater
	 *            das in der TLS-Hierarchie ueber diesem Geraet liegende Geraet
	 */
	protected Sm(ClientDavInterface dav, SystemObject objekt,
			AbstraktGeraet vater) {
		super(dav, objekt, vater);
		for (SystemObject eak : this.objekt
				.getNonMutableSet("Eak").getElements()) { //$NON-NLS-1$
			if (eak.isValid()) {
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
		OperatingMessage message = TEMPLATE.newMessage(objekt);
		message.addId("[DUA-FT-FU04]");
		this.publiziere(message);

		for (De de : this.getErfassteDes()) {
			de.publiziereFehlerUrsache(zeitStempel,
					TlsFehlerAnalyse.SM_MODEM_ODER_SM_DEFEKT);
		}
	}

}
