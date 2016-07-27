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
import de.bsvrz.dav.daf.main.Data;
import de.bsvrz.dav.daf.main.config.SystemObject;
import de.bsvrz.dua.fehlertls.de.DeFaException;
import de.bsvrz.dua.fehlertls.enums.TlsFehlerAnalyse;
import de.bsvrz.sys.funclib.bitctrl.dua.DUAKonstanten;
import de.bsvrz.sys.funclib.debug.Debug;
import de.bsvrz.sys.funclib.operatingMessage.MessageGrade;
import de.bsvrz.sys.funclib.operatingMessage.MessageTemplate;
import de.bsvrz.sys.funclib.operatingMessage.MessageType;
import de.bsvrz.sys.funclib.operatingMessage.OperatingMessage;

/**
 * TLS-Hierarchieelement EAK.
 * 
 * @author BitCtrl Systems GmbH, Thierfelder
 * 
 * @version $Id$
 */
public class Eak extends AbstraktGeraet {
	
	private static final MessageTemplate TEMPLATE = new MessageTemplate(
			MessageGrade.ERROR,
			MessageType.APPLICATION_DOMAIN,
			MessageTemplate.fixed("EAK "),
			MessageTemplate.object(),
			MessageTemplate.fixed(" am Steuermodul "),
			MessageTemplate.variable("sm"),
			MessageTemplate.fixed(" defekt. "),
			MessageTemplate.fixed("EAK "),
			MessageTemplate.object(),
			MessageTemplate.fixed(" am Steuermodul "),
			MessageTemplate.variable("sm"),
			MessageTemplate.fixed(" instand setzen. "),
			MessageTemplate.ids()
	).withIdFactory(message -> message.getObject().getPidOrId() + " [DUA-FT-FU]");
	
	/**
	 * Standardkonstruktor.
	 * 
	 * @param dav
	 *            Datenverteiler-Verbindund
	 * @param objekt
	 *            ein Systemobjekt vom Typ <code>typ.eak</code>
	 * @param vater
	 *            das in der TLS-Hierarchie ueber diesem Geraet liegende Geraet
	 */
	protected Eak(ClientDavInterface dav, SystemObject objekt,
			AbstraktGeraet vater) {
		super(dav, objekt, vater);
		for (SystemObject deObj : this.objekt
				.getNonMutableSet("De").getElements()) { //$NON-NLS-1$
			if (deObj.isValid()) {
				try {
					Data deKonfig = deObj.getConfigurationData(dav.getDataModel().getAttributeGroup("atg.de"));
					if(deKonfig != null) {
						if(deKonfig.getUnscaledValue("Cluster").intValue() == DUAKonstanten.NEIN){
							De de = new De(dav, deObj, this);
							this.kinder.add(de);
						} else {
							Debug
							.getLogger()
							.info(
									"DE "
											+ deObj
											+ " ist als Sammelkanal konfiguriert und wird daher ignoriert.");							
						}
					} else {
						Debug
								.getLogger()
								.warning(
										"DE "
												+ deObj
												+ " besitzt keine Konfigurationsdaten (innerhalb von ATG \"atg.de\") und wird ignoriert.");
					}
				} catch (DeFaException e) {
					Debug
							.getLogger()
							.warning(
									"De "	+ deObj + " konnte nicht initialisiert werden. ", e); //$NON-NLS-1$ //$NON-NLS-2$
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
		OperatingMessage message = TEMPLATE.newMessage(objekt);
		message.addId("[DUA-FT-FU05]");
		message.put("sm", getVater().getObjekt());
		this.publiziere(message);

		for (De de : this.getErfassteDes()) {
			de.publiziereFehlerUrsache(zeitStempel,
					TlsFehlerAnalyse.EAK_AN_SM_DEFEKT);
		}
	}

}
