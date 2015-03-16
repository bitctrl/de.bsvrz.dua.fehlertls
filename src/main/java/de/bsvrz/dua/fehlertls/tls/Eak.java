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

import java.util.LinkedHashSet;
import java.util.Set;

import de.bsvrz.dav.daf.main.ClientDavInterface;
import de.bsvrz.dav.daf.main.Data;
import de.bsvrz.dav.daf.main.config.SystemObject;
import de.bsvrz.dua.fehlertls.de.DeFaException;
import de.bsvrz.dua.fehlertls.de.DeTypUnsupportedException;
import de.bsvrz.dua.fehlertls.enums.TlsFehlerAnalyse;
import de.bsvrz.sys.funclib.bitctrl.dua.DUAKonstanten;
import de.bsvrz.sys.funclib.debug.Debug;
import de.bsvrz.sys.funclib.operatingMessage.MessageGrade;

/**
 * TLS-Hierarchieelement EAK.
 *
 * @author BitCtrl Systems GmbH, Thierfelder
 *
 * @version $Id$
 */
public class Eak extends TlsHierarchieElement {

	private static final Debug LOGGER = Debug.getLogger();
	/*
	 * lokale Liste der PID von DE, die nicht berücksichtigt werden, weil keine
	 * Plugin verfügbar ist.
	 */
	private static Set<String> unsupportedDeTypes = new LinkedHashSet<String>();

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
	protected Eak(final ClientDavInterface dav, final SystemObject objekt,
			final TlsHierarchieElement vater) {
		super(dav, objekt, vater);
		for (final SystemObject deObj : getObjekt().getNonMutableSet("De")
				.getElements()) {
			if (deObj.isValid()
					&& (!Eak.unsupportedDeTypes.contains(deObj.getType()
							.getPid()))) {
				try {
					final Data deKonfig = deObj.getConfigurationData(dav
							.getDataModel().getAttributeGroup("atg.de"));
					if (deKonfig != null) {
						if (deKonfig.getUnscaledValue("Cluster").intValue() == DUAKonstanten.NEIN) {
							final De de = new De(dav, deObj, this);
							addKind(de);
						} else {
							Eak.LOGGER
									.info("DE "
											+ deObj
											+ " ist als Sammelkanal konfiguriert und wird daher ignoriert.");
						}
					} else {
						Eak.LOGGER
								.warning("DE "
								+ deObj
								+ " besitzt keine Konfigurationsdaten (innerhalb von ATG \"atg.de\") und wird ignoriert.");
					}
				} catch (final DeTypUnsupportedException e) {
					Eak.LOGGER.warning(e.getMessage(), deObj);
					Eak.unsupportedDeTypes.add(e.getDeTypPid());
				} catch (final DeFaException e) {
					Eak.LOGGER.warning("De " + deObj
							+ " konnte nicht initialisiert werden. ", e);
				}
			}
		}
	}

	@Override
	public Art getGeraeteArt() {
		return Art.EAK;
	}

	@Override
	public void publiziereFehler(final long zeitStempel) {
		getEinzelPublikator().publiziere(
				MessageGrade.ERROR,
				getObjekt(),
				"EAK " + getObjekt() + " am Steuermodul "
						+ this.getVater().getObjekt() + " defekt." + " EAK "
						+ getObjekt() + " am Steuermodul "
						+ this.getVater().getObjekt() + " instand setzen");

		for (final De de : this.getErfassteDes()) {
			de.publiziereFehlerUrsache(zeitStempel,
					TlsFehlerAnalyse.EAK_AN_SM_DEFEKT);
		}
	}

}
