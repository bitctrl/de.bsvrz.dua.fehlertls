/*
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

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import de.bsvrz.dav.daf.main.ClientDavInterface;
import de.bsvrz.dav.daf.main.Data;
import de.bsvrz.dav.daf.main.config.SystemObject;
import de.bsvrz.dua.fehlertls.enums.TlsFehlerAnalyse;
import de.bsvrz.sys.funclib.debug.Debug;
import de.bsvrz.sys.funclib.operatingMessage.MessageGrade;

/**
 * TLS-Hierarchieelement Inselbus.
 *
 * @author BitCtrl Systems GmbH, Thierfelder
 */
public class AnschlussPunkt extends TlsHierarchieElement {

	private static final Debug LOGGER = Debug.getLogger();

	/**
	 * Standardkonstruktor.
	 *
	 * @param dav
	 *            Datenverteiler-Verbindund
	 * @param objekt
	 *            ein Systemobjekt vom Typ <code>typ.anschlussPunkt</code>
	 *            (unterhalb eines Objektes vom Typ <code>typ.kri</code>)
	 * @param vater
	 *            das in der TLS-Hierarchie ueber diesem Geraet liegende Geraet
	 */
	protected AnschlussPunkt(final ClientDavInterface dav, final SystemObject objekt,
			final TlsHierarchieElement vater) {
		super(dav, objekt, vater);

		/**
		 * Initialisiere Steuermodule
		 */
		for (final SystemObject komPartner : getObjekt().getNonMutableSet("AnschlussPunkteKommunikationsPartner")
				.getElements()) {
			if (komPartner.isValid()) {
				final Data konfigDatum = komPartner.getConfigurationData(TlsHierarchie.getWurzel().getApKonfigAtg());
				if (konfigDatum != null) {
					final SystemObject steuerModul = konfigDatum.getReferenceValue("KommunikationsPartner")
							.getSystemObject();
					if (steuerModul != null) {
						if (steuerModul.isOfType("typ.steuerModul")) {
							addKind(new Sm(dav, steuerModul, this));
						} else {
							AnschlussPunkt.LOGGER.warning("An " + komPartner + " (Inselbus: " + getObjekt()
									+ ") duerfen nur Steuermodule definiert sein. Aber: " + steuerModul + " (Typ: "
									+ steuerModul.getType() + ")");
						}
					} else {
						AnschlussPunkt.LOGGER.warning(
								"An " + komPartner + " (Inselbus: " + getObjekt() + ") ist kein Steuermodul definiert");
					}
				} else {
					AnschlussPunkt.LOGGER.warning("Konfiguration von " + komPartner + " (Inselbus: " + getObjekt()
							+ ") konnte nicht ausgelesen werden. " + "Das assoziierte Steuermodul wird ignoriert");
				}
			}
		}

	}

	@Override
	public Art getGeraeteArt() {
		return Art.INSELBUS;
	}

	/**
	 * Gibt <code>true</code> zurueck, wenn:<br>
	 * 1. mehr als ein (wenigstens teilweise erfasstes) Steuermodul
	 * angeschlossen ist <b>und</b><br>
	 * 2. mehr als ein (wenigstens teilweise erfasstes) angeschlossenes
	 * Steuermodul keine Daten liefert<br>
	 */
	@Override
	public boolean kannFehlerHierPublizieren(final long zeitStempel) {
		boolean kannHierPublizieren = false;

		/**
		 * ermittle alle Steuermodule, die unterhalb dieses Inselbusses liegen
		 * und wenigstens ein erfasstes DE haben (mit ihren erfassten DE)
		 */
		final Map<Sm, Set<De>> erfassteSteuerModuleMitErfasstenDes = new HashMap<>();

		for (final De erfassteDe : this.getErfassteDes()) {
			final Sm steuerModulVonDe = (Sm) erfassteDe.getVater().getVater();
			Set<De> erfassteDesAmSteuerModul = erfassteSteuerModuleMitErfasstenDes.get(steuerModulVonDe);
			if (erfassteDesAmSteuerModul == null) {
				erfassteDesAmSteuerModul = new HashSet<>();
				erfassteSteuerModuleMitErfasstenDes.put(steuerModulVonDe, erfassteDesAmSteuerModul);
			}
			erfassteDesAmSteuerModul.add(erfassteDe);
		}

		/**
		 * Ermittle alle erfassten Steuermodule, die teilweise ausgefallen sind
		 */
		final Map<Sm, Set<De>> timeOutSteuerModuleMitTimeOutDes = new HashMap<>();
		for (final Sm erfasstesSm : erfassteSteuerModuleMitErfasstenDes.keySet()) {
			for (final De erfassteDe : erfassteSteuerModuleMitErfasstenDes.get(erfasstesSm)) {
				if (!erfassteDe.isInTime()) {
					Set<De> alleTimeOutDesVonSteuerModul = timeOutSteuerModuleMitTimeOutDes.get(erfasstesSm);
					if (alleTimeOutDesVonSteuerModul == null) {
						alleTimeOutDesVonSteuerModul = new HashSet<>();
						timeOutSteuerModuleMitTimeOutDes.put(erfasstesSm, alleTimeOutDesVonSteuerModul);
					}
					alleTimeOutDesVonSteuerModul.add(erfassteDe);
				}
			}
		}

		/**
		 * Ermittle alle erfassten Steuermodule, die vollstaendig ausgefallen
		 * sind
		 */
		final Set<Sm> totalAusfallSteuerModule = new HashSet<>();
		for (final Sm timeOutSteuerModul : timeOutSteuerModuleMitTimeOutDes.keySet()) {
			/**
			 * ist das Steuermodul vollstaendig aufgefallen?
			 */
			final int erfassteDes = erfassteSteuerModuleMitErfasstenDes.get(timeOutSteuerModul).size();
			final int timeoutDes = timeOutSteuerModuleMitTimeOutDes.get(timeOutSteuerModul).size();
			if (erfassteDes == timeoutDes) {
				totalAusfallSteuerModule.add(timeOutSteuerModul);
			}
		}

		if ((totalAusfallSteuerModule.size() == erfassteSteuerModuleMitErfasstenDes.keySet().size())
				|| (totalAusfallSteuerModule.size() > 1)) {
			kannHierPublizieren = true;
		}

		return kannHierPublizieren;
	}

	@Override
	public void publiziereFehler(final long zeitStempel) {
		/**
		 * ermittle alle Steuermodule, die unterhalb dieses Inselbusses liegen
		 * und wenigstens ein erfasstes DE haben (mit ihren erfassten DE)
		 */
		final Map<Sm, Set<De>> erfassteSteuerModuleMitErfasstenDes = new HashMap<>();

		for (final De erfassteDe : this.getErfassteDes()) {
			final Sm steuerModulVonDe = (Sm) erfassteDe.getVater().getVater();
			Set<De> erfassteDesAmSteuerModul = erfassteSteuerModuleMitErfasstenDes.get(steuerModulVonDe);
			if (erfassteDesAmSteuerModul == null) {
				erfassteDesAmSteuerModul = new HashSet<>();
				erfassteSteuerModuleMitErfasstenDes.put(steuerModulVonDe, erfassteDesAmSteuerModul);
			}
			erfassteDesAmSteuerModul.add(erfassteDe);
		}

		/**
		 * Ermittle alle erfassten Steuermodule, die teilweise ausgefallen sind
		 */
		final Map<Sm, Set<De>> timeOutSteuerModuleMitTimeOutDes = new HashMap<>();
		for (final Sm erfasstesSm : erfassteSteuerModuleMitErfasstenDes.keySet()) {
			for (final De erfassteDe : erfassteSteuerModuleMitErfasstenDes.get(erfasstesSm)) {
				if (!erfassteDe.isInTime()) {
					Set<De> alleTimeOutDesVonSteuerModul = timeOutSteuerModuleMitTimeOutDes.get(erfasstesSm);
					if (alleTimeOutDesVonSteuerModul == null) {
						alleTimeOutDesVonSteuerModul = new HashSet<>();
						timeOutSteuerModuleMitTimeOutDes.put(erfasstesSm, alleTimeOutDesVonSteuerModul);
					}
					alleTimeOutDesVonSteuerModul.add(erfassteDe);
				}
			}
		}

		/**
		 * Ermittle alle erfassten Steuermodule, die vollstaendig ausgefallen
		 * sind
		 */
		final Set<Sm> totalAusfallSteuerModule = new HashSet<>();
		for (final Sm timeOutSteuerModul : timeOutSteuerModuleMitTimeOutDes.keySet()) {
			/**
			 * ist das Steuermodul vollstaendig aufgefallen?
			 */
			final int erfassteDes = erfassteSteuerModuleMitErfasstenDes.get(timeOutSteuerModul).size();
			final int timeoutDes = timeOutSteuerModuleMitTimeOutDes.get(timeOutSteuerModul).size();
			if (erfassteDes == timeoutDes) {
				totalAusfallSteuerModule.add(timeOutSteuerModul);
			}
		}

		if (totalAusfallSteuerModule.size() == erfassteSteuerModuleMitErfasstenDes.keySet().size()) {
			getEinzelPublikator().publiziere(MessageGrade.ERROR, getObjekt(), "Modem am Inselbus " + getObjekt()
					+ " oder Inselbus selbst defekt. Modem oder Inselbus instand setzen");

			for (final TlsHierarchieElement steuerModulOhneDaten : totalAusfallSteuerModule) {
				for (final De de : timeOutSteuerModuleMitTimeOutDes.get(steuerModulOhneDaten)) {
					de.publiziereFehlerUrsache(zeitStempel, TlsFehlerAnalyse.INSELBUS_MODEM_ODER_INSELBUS_DEFEKT);
				}
			}
		} else {
			/**
			 * Nach Pid und Name sortierte Ausgabe der Steuermodule wegen
			 * JUnit-Tests
			 */
			final SortedSet<TlsHierarchieElement> totalAusfallSteuerModuleSortiert = new TreeSet<>(
					new Comparator<TlsHierarchieElement>() {

						@Override
						public int compare(final TlsHierarchieElement o1, final TlsHierarchieElement o2) {
							return o1.getObjekt().toString().compareTo(o2.getObjekt().toString());
						}

					});
			totalAusfallSteuerModuleSortiert.addAll(totalAusfallSteuerModule);
			final TlsHierarchieElement[] steuerModulArray = totalAusfallSteuerModuleSortiert
					.toArray(new TlsHierarchieElement[0]);

			String steuerModule = "/";
			if (steuerModulArray.length > 0) {
				steuerModule = steuerModulArray[0].getObjekt().toString();
				for (int i = 1; i < steuerModulArray.length; i++) {
					steuerModule += ", " + steuerModulArray[i].getObjekt().toString();
				}
			}

			getEinzelPublikator().publiziere(MessageGrade.ERROR, getObjekt(),
					"Inselbus " + getObjekt() + " gestört: Für die DE der Steuermodule " + steuerModule
					+ " sind keine Daten verfügbar. Inselbus " + getObjekt() + " instand setzen");

			for (final TlsHierarchieElement steuerModulOhneDaten : totalAusfallSteuerModule) {
				for (final De de : timeOutSteuerModuleMitTimeOutDes.get(steuerModulOhneDaten)) {
					de.publiziereFehlerUrsache(zeitStempel, TlsFehlerAnalyse.INSELBUS_DEFEKT);
				}
			}
		}
	}

}
