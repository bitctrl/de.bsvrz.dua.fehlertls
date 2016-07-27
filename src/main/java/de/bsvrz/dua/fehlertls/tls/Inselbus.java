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
import de.bsvrz.dua.fehlertls.enums.TlsFehlerAnalyse;
import de.bsvrz.sys.funclib.debug.Debug;
import de.bsvrz.sys.funclib.operatingMessage.MessageGrade;
import de.bsvrz.sys.funclib.operatingMessage.MessageTemplate;
import de.bsvrz.sys.funclib.operatingMessage.MessageType;
import de.bsvrz.sys.funclib.operatingMessage.OperatingMessage;

import java.util.*;

/**
 * TLS-Hierarchieelement Inselbus.
 * 
 * @author BitCtrl Systems GmbH, Thierfelder
 * 
 * @version $Id$
 */
public class Inselbus extends AbstraktGeraet {

	private static final MessageTemplate TEMPLATE_MODEM = new MessageTemplate(
			MessageGrade.ERROR,
			MessageType.APPLICATION_DOMAIN,
			MessageTemplate.fixed("Modem am Inselbus "),
			MessageTemplate.object(),
			MessageTemplate.fixed(" oder Inselbus selbst defekt. Modem oder Inselbus instand setzen. "),
			MessageTemplate.ids()
	).withIdFactory(message -> message.getObject().getPidOrId() + " [DUA-FT-FU]");
	
	private static final MessageTemplate TEMPLATE_INSELBUS = new MessageTemplate(
			MessageGrade.ERROR,
			MessageType.APPLICATION_DOMAIN,
			MessageTemplate.fixed("Inselbus "),
			MessageTemplate.object(),
			MessageTemplate.fixed(" gestört: Für die DE "),
			MessageTemplate.set("sm", ", ", "des Steuermoduls ", "der Steuermodule "),
			MessageTemplate.fixed(" sind keine Daten verfügbar. Inselbus "),
			MessageTemplate.object(),
			MessageTemplate.fixed(" instand setzen. "),
			MessageTemplate.ids()
	).withIdFactory(message -> message.getObject().getPidOrId() + " [DUA-FT-FU]");
	
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
	protected Inselbus(ClientDavInterface dav, SystemObject objekt,
			AbstraktGeraet vater) {
		super(dav, objekt, vater);

		/**
		 * Initialisiere Steuermodule
		 */
		for (SystemObject komPartner : this.objekt.getNonMutableSet(
				"AnschlussPunkteKommunikationsPartner").getElements()) { //$NON-NLS-1$
			if (komPartner.isValid()) {
				Data konfigDatum = komPartner
						.getConfigurationData(dav.getDataModel().getAttributeGroup(
								"atg.anschlussPunktKommunikationsPartner"));
				if (konfigDatum != null) {
					SystemObject steuerModul = konfigDatum.getReferenceValue(
							"KommunikationsPartner").getSystemObject(); //$NON-NLS-1$
					if (steuerModul != null) {
						if (steuerModul.isOfType("typ.steuerModul")) { //$NON-NLS-1$
							this.kinder.add(new Sm(dav, steuerModul, this));
						} else {
							Debug
									.getLogger()
									.warning(
											"An "	+ komPartner + //$NON-NLS-1$
													" (Inselbus: "
													+ this.objekt
													+ //$NON-NLS-1$
													") duerfen nur Steuermodule definiert sein. Aber: "
													+ //$NON-NLS-1$
													steuerModul
													+ " (Typ: " + steuerModul.getType() + //$NON-NLS-1$ 
													")"); //$NON-NLS-1$				
						}
					} else {
						Debug.getLogger().warning("An " + komPartner + //$NON-NLS-1$
								" (Inselbus: " + this.objekt + //$NON-NLS-1$
								") ist kein Steuermodul definiert"); //$NON-NLS-1$				
					}
				} else {
					Debug
							.getLogger()
							.warning("Konfiguration von " + komPartner + //$NON-NLS-1$
									" (Inselbus: " + this.objekt + //$NON-NLS-1$
									") konnte nicht ausgelesen werden. " + //$NON-NLS-1$
									"Das assoziierte Steuermodul wird ignoriert"); //$NON-NLS-1$
				}
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

	/**
	 * {@inheritDoc}<br>
	 * 
	 * Gibt <code>true</code> zurueck, wenn:<br>
	 * 1. mehr als ein (wenigstens teilweise erfasstes) Steuermodul
	 * angeschlossen ist <b>und</b><br>
	 * 2. mehr als ein (wenigstens teilweise erfasstes) angeschlossenes
	 * Steuermodul keine Daten liefert<br>
	 */
	@Override
	public boolean kannFehlerHierPublizieren(long zeitStempel) {
		boolean kannHierPublizieren = false;

		/**
		 * ermittle alle Steuermodule, die unterhalb dieses Inselbusses liegen
		 * und wenigstens ein erfasstes DE haben (mit ihren erfassten DE)
		 */
		Map<Sm, Set<De>> erfassteSteuerModuleMitErfasstenDes = new HashMap<Sm, Set<De>>();

		for (De erfassteDe : this.getErfassteDes()) {
			Sm steuerModulVonDe = (Sm) erfassteDe.getVater().getVater();
			Set<De> erfassteDesAmSteuerModul = erfassteSteuerModuleMitErfasstenDes
					.get(steuerModulVonDe);
			if (erfassteDesAmSteuerModul == null) {
				erfassteDesAmSteuerModul = new HashSet<De>();
				erfassteSteuerModuleMitErfasstenDes.put(steuerModulVonDe,
						erfassteDesAmSteuerModul);
			}
			erfassteDesAmSteuerModul.add(erfassteDe);
		}

		/**
		 * Ermittle alle erfassten Steuermodule, die teilweise ausgefallen sind
		 */
		Map<Sm, Set<De>> timeOutSteuerModuleMitTimeOutDes = new HashMap<Sm, Set<De>>();
		for (Sm erfasstesSm : erfassteSteuerModuleMitErfasstenDes.keySet()) {
			for (De erfassteDe : erfassteSteuerModuleMitErfasstenDes
					.get(erfasstesSm)) {
				if (!erfassteDe.isInTime()) {
					Set<De> alleTimeOutDesVonSteuerModul = timeOutSteuerModuleMitTimeOutDes
							.get(erfasstesSm);
					if (alleTimeOutDesVonSteuerModul == null) {
						alleTimeOutDesVonSteuerModul = new HashSet<De>();
						timeOutSteuerModuleMitTimeOutDes.put(erfasstesSm,
								alleTimeOutDesVonSteuerModul);
					}
					alleTimeOutDesVonSteuerModul.add(erfassteDe);
				}
			}
		}

		/**
		 * Ermittle alle erfassten Steuermodule, die vollstaendig ausgefallen
		 * sind
		 */
		Set<Sm> totalAusfallSteuerModule = new HashSet<Sm>();
		for (Sm timeOutSteuerModul : timeOutSteuerModuleMitTimeOutDes.keySet()) {
			/**
			 * ist das Steuermodul vollstaendig aufgefallen?
			 */
			int erfassteDes = erfassteSteuerModuleMitErfasstenDes.get(
					timeOutSteuerModul).size();
			int timeoutDes = timeOutSteuerModuleMitTimeOutDes.get(
					timeOutSteuerModul).size();
			if (erfassteDes == timeoutDes) {
				totalAusfallSteuerModule.add(timeOutSteuerModul);
			}
		}

		if (totalAusfallSteuerModule.size() == erfassteSteuerModuleMitErfasstenDes
				.keySet().size()
				|| totalAusfallSteuerModule.size() > 1) {
			kannHierPublizieren = true;
		}

		return kannHierPublizieren;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void publiziereFehler(long zeitStempel) {
		/**
		 * ermittle alle Steuermodule, die unterhalb dieses Inselbusses liegen
		 * und wenigstens ein erfasstes DE haben (mit ihren erfassten DE)
		 */
		Map<Sm, Set<De>> erfassteSteuerModuleMitErfasstenDes = new HashMap<Sm, Set<De>>();

		for (De erfassteDe : this.getErfassteDes()) {
			Sm steuerModulVonDe = (Sm) erfassteDe.getVater().getVater();
			Set<De> erfassteDesAmSteuerModul = erfassteSteuerModuleMitErfasstenDes
					.get(steuerModulVonDe);
			if (erfassteDesAmSteuerModul == null) {
				erfassteDesAmSteuerModul = new HashSet<De>();
				erfassteSteuerModuleMitErfasstenDes.put(steuerModulVonDe,
						erfassteDesAmSteuerModul);
			}
			erfassteDesAmSteuerModul.add(erfassteDe);
		}

		/**
		 * Ermittle alle erfassten Steuermodule, die teilweise ausgefallen sind
		 */
		Map<Sm, Set<De>> timeOutSteuerModuleMitTimeOutDes = new HashMap<Sm, Set<De>>();
		for (Sm erfasstesSm : erfassteSteuerModuleMitErfasstenDes.keySet()) {
			for (De erfassteDe : erfassteSteuerModuleMitErfasstenDes
					.get(erfasstesSm)) {
				if (!erfassteDe.isInTime()) {
					Set<De> alleTimeOutDesVonSteuerModul = timeOutSteuerModuleMitTimeOutDes
							.get(erfasstesSm);
					if (alleTimeOutDesVonSteuerModul == null) {
						alleTimeOutDesVonSteuerModul = new HashSet<De>();
						timeOutSteuerModuleMitTimeOutDes.put(erfasstesSm,
								alleTimeOutDesVonSteuerModul);
					}
					alleTimeOutDesVonSteuerModul.add(erfassteDe);
				}
			}
		}

		/**
		 * Ermittle alle erfassten Steuermodule, die vollstaendig ausgefallen
		 * sind
		 */
		Set<Sm> totalAusfallSteuerModule = new HashSet<Sm>();
		for (Sm timeOutSteuerModul : timeOutSteuerModuleMitTimeOutDes.keySet()) {
			/**
			 * ist das Steuermodul vollstaendig aufgefallen?
			 */
			int erfassteDes = erfassteSteuerModuleMitErfasstenDes.get(
					timeOutSteuerModul).size();
			int timeoutDes = timeOutSteuerModuleMitTimeOutDes.get(
					timeOutSteuerModul).size();
			if (erfassteDes == timeoutDes) {
				totalAusfallSteuerModule.add(timeOutSteuerModul);
			}
		}

		if (totalAusfallSteuerModule.size() == erfassteSteuerModuleMitErfasstenDes
				.keySet().size()) {
			OperatingMessage message = TEMPLATE_MODEM.newMessage(objekt);
			message.addId("[DUA-FT-FU02]");
			this.publiziere(message);
			
			for (AbstraktGeraet steuerModulOhneDaten : totalAusfallSteuerModule) {
				for (De de : timeOutSteuerModuleMitTimeOutDes
						.get(steuerModulOhneDaten)) {
					de
							.publiziereFehlerUrsache(
									zeitStempel,
									TlsFehlerAnalyse.INSELBUS_MODEM_ODER_INSELBUS_DEFEKT);
				}
			}
		} else {
			/**
			 * Nach Pid und Name sortierte Ausgabe der Steuermodule wegen
			 * JUnit-Tests
			 */
			SortedSet<AbstraktGeraet> totalAusfallSteuerModuleSortiert = new TreeSet<AbstraktGeraet>(
					new Comparator<AbstraktGeraet>() {

						public int compare(AbstraktGeraet o1, AbstraktGeraet o2) {
							return o1.getObjekt().toString().compareTo(
									o2.getObjekt().toString());
						}

					});
			totalAusfallSteuerModuleSortiert.addAll(totalAusfallSteuerModule);

			OperatingMessage message = TEMPLATE_INSELBUS.newMessage(objekt);

			if(totalAusfallSteuerModuleSortiert.isEmpty()) {
				message.add("sm", "/", true);
			}
			else {
				for(AbstraktGeraet abstraktGeraet : totalAusfallSteuerModuleSortiert) {
					message.add("sm", abstraktGeraet.getObjekt());
				}		
			}

			message.addId("[DUA-FT-FU03]");
			this.publiziere(message);

			for (AbstraktGeraet steuerModulOhneDaten : totalAusfallSteuerModule) {
				for (De de : timeOutSteuerModuleMitTimeOutDes
						.get(steuerModulOhneDaten)) {
					de.publiziereFehlerUrsache(zeitStempel,
							TlsFehlerAnalyse.INSELBUS_DEFEKT);
				}
			}
		}
	}

}
