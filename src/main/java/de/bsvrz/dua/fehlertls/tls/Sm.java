/**
 * Segment 4 Datenübernahme und Aufbereitung (DUA), SWE 4.DeFa DE Fehleranalyse fehlende Messdaten
 * Copyright (C) 2007-2015 BitCtrl Systems GmbH
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
import de.bsvrz.dav.daf.main.config.SystemObject;
import de.bsvrz.dua.fehlertls.enums.TlsFehlerAnalyse;
import de.bsvrz.sys.funclib.operatingMessage.MessageGrade;

/**
 * TLS-Hierarchieelement Steuermodul.
 *
 * @author BitCtrl Systems GmbH, Thierfelder
 *
 * @version $Id$
 */
public class Sm extends TlsHierarchieElement {

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
	protected Sm(final ClientDavInterface dav, final SystemObject objekt,
			final TlsHierarchieElement vater) {
		super(dav, objekt, vater);
		for (final SystemObject eak : getObjekt().getNonMutableSet("Eak")
				.getElements()) {
			if (eak.isValid()) {
				addKind(new Eak(dav, eak, this));
			}
		}
	}

	@Override
	public Art getGeraeteArt() {
		return Art.SM;
	}

	@Override
	public void publiziereFehler(final long zeitStempel) {
		getEinzelPublikator().publiziere(
				MessageGrade.ERROR,
				getObjekt(),
				"Modem am Steuermodul " + getObjekt()
				+ " oder Steuermodul defekt. "
				+ "Modem am Steuermodul " + getObjekt()
				+ " oder Steuermodul instand setzen");

		for (final De de : this.getErfassteDes()) {
			de.publiziereFehlerUrsache(zeitStempel,
					TlsFehlerAnalyse.SM_MODEM_ODER_SM_DEFEKT);
		}
	}

}
