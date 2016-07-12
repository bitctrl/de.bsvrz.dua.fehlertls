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
import de.bsvrz.dav.daf.main.config.AttributeGroup;
import de.bsvrz.dav.daf.main.config.ConfigurationObject;
import de.bsvrz.dav.daf.main.config.SystemObject;
import de.bsvrz.sys.funclib.debug.Debug;

import java.util.HashSet;
import java.util.Set;

/**
 * Initialisiert alle Objekte im Teilmodel TLS, die (inklusive und) unterhalb
 * der uebergebenen Objekte vom Typ <code>typ.gerät</code> konfiguriert sind.
 * 
 * @author BitCtrl Systems GmbH, Thierfelder
 * 
 * @version $Id$
 */
public final class TlsHierarchie extends AbstraktGeraet {

	/**
	 * Datenverteiler-Verbindung.
	 */
	private ClientDavInterface dav = null;

	/**
	 * Konfigurierende Eigenschaften eines Kommunikationspartners an einem
	 * Anschlusspunkt.
	 */
	public AttributeGroup konfigAtg = null;

	/**
	 * Standardkonstruktor.
	 * 
	 * @param dav
	 *            Datenverteiler-Verbindung
	 */
	private TlsHierarchie(ClientDavInterface dav) {
		super(dav, null, null);
		this.dav = dav;
		this.konfigAtg = dav.getDataModel().getAttributeGroup(
				"atg.anschlussPunktKommunikationsPartner");
	}

	/**
	 * Standardkonstruktor.
	 *  @param dav
	 *            Datenverteiler-Verbindung
	 * @param geraete
	 */
	public static TlsHierarchie getInstance(ClientDavInterface dav,
			Set<SystemObject> geraete) {
		TlsHierarchie wurzel;
		wurzel = new TlsHierarchie(dav);

		for (SystemObject geraet : geraete) {
			wurzel.initialisiere((ConfigurationObject) geraet);
		}
		return wurzel;
	}

	/**
	 * Initialisiert ein einzelnes Objekt vom Typ <code>typ.gerät</code>.
	 * 
	 * @param geraet
	 *            ein Objekt vom Typ <code>typ.gerät</code>
	 */
	private void initialisiere(ConfigurationObject geraet) {
		if (geraet.isOfType("typ.kri2_b")) { //$NON-NLS-1$
			// Vor SM prüfen, da Kri2b auch SM sind
			kinder.add(new Kri(dav, geraet, this));
		} else if (geraet.isOfType("typ.steuerModul")) { //$NON-NLS-1$
			kinder.add(new Sm(dav, geraet, this));
		} else if (geraet.isOfType("typ.kri")) { //$NON-NLS-1$
			kinder.add(new Kri(dav, geraet, this));
		} else if (geraet.isOfType("typ.uz") || //$NON-NLS-1$
				geraet.isOfType("typ.viz") || //$NON-NLS-1$
				geraet.isOfType("typ.vrz")) { //$NON-NLS-1$
			for (SystemObject anschlussPunktSysObj : geraet.getNonMutableSet(
					"AnschlussPunkteGerät").getElements()) { //$NON-NLS-1$
				if (anschlussPunktSysObj.isValid()) {
					ConfigurationObject anschlussPunktKonObj = (ConfigurationObject) anschlussPunktSysObj;

					Set<SystemObject> unterGeraete = new HashSet<SystemObject>();
					for (SystemObject komPartner : anschlussPunktKonObj
							.getNonMutableSet(
									"AnschlussPunkteKommunikationsPartner").getElements()) { //$NON-NLS-1$

						Data konfigDatum = komPartner
								.getConfigurationData(konfigAtg);
						if (konfigDatum != null) {
							SystemObject unterGeraet = konfigDatum
									.getReferenceValue("KommunikationsPartner").getSystemObject(); //$NON-NLS-1$
							if (unterGeraet != null) {
								unterGeraete.add(unterGeraet);
							} else {
								Debug.getLogger().warning("An " + komPartner + //$NON-NLS-1$
										" (Geraet: " + geraet + //$NON-NLS-1$
										") ist kein Geraet definiert"); //$NON-NLS-1$				
							}
						} else {
							Debug.getLogger().warning("Konfiguration von " + komPartner + //$NON-NLS-1$
									" (an Geraet: " + geraet + //$NON-NLS-1$
									") konnte nicht ausgelesen werden. " + //$NON-NLS-1$
									"Das assoziierte Geraet wird ignoriert"); //$NON-NLS-1$
						}
					}

					/**
					 * Iteriere ueber alle Untergeraete dieses Anschlusspunktes.
					 * Wenn ALLE Anschlusspunkte Steuermodule sein sollten, dann
					 * wird davon ausgegangen, dass es sich bei diesem
					 * Anschlusspunkt um einen Inselbus handelt
					 */
					int steuerModulZaehler = 0;
					for (SystemObject unterGeraet : unterGeraete) {
						if (unterGeraet.isOfType("typ.steuerModul") && !unterGeraet.isOfType("typ.kri2_b")) {
							steuerModulZaehler++;
						}
					}

					if (unterGeraete.size() > 0) {
						if (unterGeraete.size() == steuerModulZaehler) {
							kinder.add(new Inselbus(dav,
							                        anschlussPunktSysObj, this));
						} else {
							for (SystemObject unterGeraet : unterGeraete) {
								initialisiere((ConfigurationObject) unterGeraet);
							}
						}
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
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void publiziereFehler(long zeitStempel) {
		assert (false);
	}

}
