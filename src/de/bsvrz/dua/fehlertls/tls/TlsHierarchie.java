/**
 * Segment 4 Daten�bernahme und Aufbereitung (DUA), SWE 4.DeFa DE Fehleranalyse fehlende Messdaten
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
 * Wei�enfelser Stra�e 67<br>
 * 04229 Leipzig<br>
 * Phone: +49 341-490670<br>
 * mailto: info@bitctrl.de
 */

package de.bsvrz.dua.fehlertls.tls;

import java.util.HashSet;
import java.util.Set;

import de.bsvrz.dav.daf.main.ClientDavInterface;
import de.bsvrz.dav.daf.main.Data;
import de.bsvrz.dav.daf.main.config.AttributeGroup;
import de.bsvrz.dav.daf.main.config.ConfigurationObject;
import de.bsvrz.dav.daf.main.config.SystemObject;
import de.bsvrz.sys.funclib.debug.Debug;

/**
 * Initialisiert alle Objekte im Teilmodel TLS, die (inklusive und) unterhalb
 * der uebergebenen Objekte vom Typ <code>typ.ger�t</code> konfiguriert sind.
 * 
 * @author BitCtrl Systems GmbH, Thierfelder
 * 
 * @version $Id$
 */
public final class TlsHierarchie extends AbstraktGeraet {


	/**
	 * Datenverteiler-Verbindung.
	 */
	private static ClientDavInterface sDav = null;

	/**
	 * Konfigurierende Eigenschaften eines Kommunikationspartners an einem
	 * Anschlusspunkt.
	 */
	public static AttributeGroup konfigAtg = null;

	/**
	 * statische Wurzel der TLS-Hierarchie. Unterhalb dieser Wurzel haengen die
	 * Geraete, mit der diese TLS-Hierarchie initialisiert wurde.
	 */
	private static TlsHierarchie wurzel = null;

	/**
	 * Standardkonstruktor.
	 * 
	 * @param dav
	 *            Datenverteiler-Verbindund
	 */
	private TlsHierarchie(ClientDavInterface dav) {
		super(dav, null, null);
	}

	/**
	 * Erfragt die statische Wurzel der TLS-Hierarchie. Unterhalb dieser Wurzel
	 * haengen die Geraete, mit der diese TLS-Hierarchie initialisiert wurde.
	 * 
	 * @return die statische Wurzel der TLS-Hierarchie
	 */
	public static TlsHierarchie getWurzel() {
		if (wurzel == null) {
			throw new RuntimeException(
					"TLS-Hierarchie wurde noch nicht initialisiert"); //$NON-NLS-1$
		}
		return wurzel;
	}

	/**
	 * Standardkonstruktor.
	 * 
	 * @param dav
	 *            Datenverteiler-Verbindund
	 * @param geraete
	 *            Geraete, die in der Kommandozeile uebergeben wurden
	 */
	public static void initialisiere(ClientDavInterface dav,
			Set<SystemObject> geraete) {
		if (wurzel == null) {
			wurzel = new TlsHierarchie(dav);
			sDav = dav;
			konfigAtg = dav.getDataModel().getAttributeGroup(
					"atg.anschlussPunktKommunikationsPartner"); //$NON-NLS-1$

			for (SystemObject geraet : geraete) {
				initialisiere((ConfigurationObject) geraet);
			}
		}
	}

	/**
	 * Initialisiert ein einzelnes Objekt vom Typ <code>typ.ger�t</code>.
	 * 
	 * @param geraet
	 *            ein Objekt vom Typ <code>typ.ger�t</code>
	 */
	private static void initialisiere(ConfigurationObject geraet) {
		if (geraet.isOfType("typ.steuerModul")) { //$NON-NLS-1$
			wurzel.kinder.add(new Sm(sDav, geraet, wurzel));
		} else if (geraet.isOfType("typ.kri")) { //$NON-NLS-1$
			wurzel.kinder.add(new Kri(sDav, geraet, wurzel));
		} else if (geraet.isOfType("typ.uz") || //$NON-NLS-1$
				geraet.isOfType("typ.viz") || //$NON-NLS-1$
				geraet.isOfType("typ.vrz")) { //$NON-NLS-1$
			for (SystemObject anschlussPunktSysObj : geraet.getNonMutableSet(
					"AnschlussPunkteGer�t").getElements()) { //$NON-NLS-1$
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
						if (unterGeraet.isOfType("typ.steuerModul")) { //$NON-NLS-1$
							steuerModulZaehler++;
						}
					}

					if (unterGeraete.size() > 0) {
						if (unterGeraete.size() == steuerModulZaehler) {
							wurzel.kinder.add(new Inselbus(sDav,
									anschlussPunktSysObj, wurzel));
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
