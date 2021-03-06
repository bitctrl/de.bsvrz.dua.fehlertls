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
import de.bsvrz.dav.daf.main.config.ConfigurationObject;
import de.bsvrz.dav.daf.main.config.SystemObject;
import de.bsvrz.sys.funclib.operatingMessage.OperatingMessage;

import java.util.HashSet;
import java.util.Set;

/**
 * Abstrakte Repraesentation einer Objektes vom Typ <code>typ.gerät</code>.
 * 
 * @author BitCtrl Systems GmbH, Thierfelder
 */
public abstract class AbstraktGeraet {

	/**
	 * ob die TLS-Hierarchie in einem Baum dargestellt werden soll.
	 */
	private static final boolean TLS_BAUM = false;

	/**
	 * Zuletzt versendeter Meldungstext
	 */
	private String _lastMessage;

	/**
	 * moegliche Geraetearten.
	 */
	public enum Art {
		/**
		 * KRI.
		 */
		KRI,

		/**
		 * INSELBUS.
		 */
		INSELBUS,

		/**
		 * SM.
		 */
		SM,

		/**
		 * EAK.
		 */
		EAK,

		/**
		 * DE.
		 */
		DE
	}

	/**
	 * statische Datenverteiler-Verbindund.
	 */
	protected static ClientDavInterface sDav = null;

	/**
	 * das Konfigurationsobjekt vom Typ <code>typ.gerät</code>.
	 */
	protected ConfigurationObject objekt = null;

	/**
	 * die in der TLS-Hierarchie unter diesem Geraet liegenden Geraete.
	 */
	protected Set<AbstraktGeraet> kinder = new HashSet<AbstraktGeraet>();

	/**
	 * das in der TLS-Hierarchie ueber diesem Geraet liegende Geraet.
	 */
	protected AbstraktGeraet vater = null;

	/**
	 * alle DEs, die sich unterhalb von diesem Element befinden.
	 */
	private Set<De> des = null;

	/**
	 * Erfragt die Geraeteart dieses Geraetes.
	 * 
	 * @return die Geraeteart dieses Geraetes
	 */
	public abstract Art getGeraeteArt();

	/**
	 * Diese Methode muss zurueckgeben, ob an diesem Knoten innerhalb der
	 * TLS-Hierarchie eine Publikation eines Fehlers moeglich "waere".<br>
	 * Das heisst fuer ein EAK z.B., dass alle angeschlossenen (und erfassten)
	 * DEs keine Daten liefern und also theoretisch die Fehlermeldung "Kein DE
	 * am EAK x des Steuermodul y liefert Daten" ausgegeben werden koennte.<br>
	 * <b>Achtung:</b> Dies impliziert nicht, dass das Element eine
	 * TLS-Hierarchie- Ebene hoeher (beiom EAK ein Steuermodul) keine
	 * Fehlermeldung publizieren kann (das ist nicht bekannt).
	 * 
	 * @param zeitStempel
	 *            der Zeitstempel des Fehlers
	 * @return ob an diesem Knoten innerhalb der TLS-Hierarchie eine Publikation
	 *         eines Fehlers moeglich "waere"
	 */
	public boolean kannFehlerHierPublizieren(long zeitStempel) {
		boolean kannHierPublizieren = false;

		if (this.getErfassteDes().size() > 0) {
			kannHierPublizieren = true;

			for (De de : this.getErfassteDes()) {
				if (de.isInTime()) {
					kannHierPublizieren = false;
					break;
				}
			}
		}

		return kannHierPublizieren;
	}

	/**
	 * Publiziert einen Fehler.
	 * 
	 * @param zeitStempel
	 *            der Zeitstempel des Fehlers
	 */
	public abstract void publiziereFehler(final long zeitStempel);

	/**
	 * Standardkonstruktor.
	 * 
	 * @param dav
	 *            Datenverteiler-Verbindund
	 * @param objekt
	 *            ein Systemobjekt vom Typ <code>typ.gerät</code>
	 * @param vater
	 *            das in der TLS-Hierarchie ueber diesem Geraet liegende Geraet
	 */
	protected AbstraktGeraet(ClientDavInterface dav, SystemObject objekt,
			AbstraktGeraet vater) {
		if (sDav == null) {
			sDav = dav;
		}
		this.objekt = (ConfigurationObject) objekt;
		this.vater = vater;
	}

	/**
	 * Versucht eine Fehlerpublikation fuer diesen Geraet.
	 * 
	 * @param zeitStempel
	 *            der Zeitstempel des Fehlers
	 */
	protected final void versucheFehlerPublikation(final long zeitStempel) {
		if (kannFehlerHierPublizieren(zeitStempel)) {
			if (this.isTopElement()) {
				publiziereFehler(zeitStempel);
			} else {
				if (this.vater.kannFehlerHierPublizieren(zeitStempel)) {
					this.vater.versucheFehlerPublikation(zeitStempel);
				} else {
					publiziereFehler(zeitStempel);
				}
			}
		}
	}

	/**
	 * Erfragt die in der TLS-Hierarchie unter diesem Geraet liegenden Geraete.
	 * 
	 * @return die in der TLS-Hierarchie unter diesem Geraet liegenden Geraete
	 *         (ggf. leere Liste)
	 */
	public final Set<AbstraktGeraet> getKinder() {
		return this.kinder;
	}

	/**
	 * Efragt das in der TLS-Hierarchie ueber diesem Geraet liegende Geraet.
	 * 
	 * @return das in der TLS-Hierarchie ueber diesem Geraet liegende Geraet
	 *         bzw. <code>null</code>, wenn dieses Geraet die Spitze der
	 *         Hierarchie sein sollte
	 */
	public final AbstraktGeraet getVater() {
		return this.vater;
	}

	/**
	 * Erfragt ob dieses Geraet an der Spitze einer TLS-Hierarchie steht.
	 * 
	 * @return ob dieses Geraet an der Spitze einer TLS-Hierarchie steht
	 */
	public final boolean isTopElement() {
		return this.vater == null || this.vater.getObjekt() == null;
	}

	@Override
	public boolean equals(Object obj) {
		boolean ergebnis = false;

		if (obj != null && obj instanceof AbstraktGeraet) {
			AbstraktGeraet that = (AbstraktGeraet) obj;
			ergebnis = this.objekt.getId() == that.objekt.getId();
		}

		return ergebnis;
	}

	@Override
	public String toString() {
		if (TLS_BAUM) {
			String baum = "";

			AbstraktGeraet dummy = this;
			while (dummy.getVater() != null) {
				baum += "   "; //$NON-NLS-1$
				dummy = dummy.getVater();
			}
			baum += this.objekt == null ? "WURZEL" : this.objekt.getPid(); //$NON-NLS-1$
			for (AbstraktGeraet kind : this.kinder) {
				baum += "\n" + kind.toString(); //$NON-NLS-1$
			}

			return baum;
		} else {
			String v = "keiner"; //$NON-NLS-1$
			String k = "keine"; //$NON-NLS-1$

			if (this.vater != null) {
				v = this.vater.getObjekt() == null ? "WURZEL" : this.vater.getObjekt().getPid(); //$NON-NLS-1$
			}
			if (!this.kinder.isEmpty()) {
				AbstraktGeraet[] dummy = this.kinder
						.toArray(new AbstraktGeraet[0]);
				k = dummy[0].getObjekt().getPid();
				for (int i = 1; i < dummy.length; i++) {
					k += ", " + dummy[i].getObjekt().getPid(); //$NON-NLS-1$
				}
			}

			return this.objekt == null ? "WURZEL" : this.objekt.toString() + " (Vater: " + v + //$NON-NLS-1$ //$NON-NLS-2$
							", Kinder:[" + k + "])"; //$NON-NLS-1$ //$NON-NLS-2$			
		}
	}

	/**
	 * Erfragt die in der untersten TLS-Hierarchie unter diesem Geraet liegenden
	 * DEs, die von der DeFa im Moment erfasst sind.
	 * 
	 * @return die in der untersten TLS-Hierarchie unter diesem Geraet liegenden
	 *         DEs, die von der DeFa im Moment erfasst sind (ggf. leere Liste)
	 */
	public final Set<De> getErfassteDes() {
		Set<De> erfassteDes = new HashSet<De>();

		for (De de : getDes()) {
			if (de.getZustand() != null && de.getZustand().isErfasst()) {
				erfassteDes.add(de);
			}
		}

		return erfassteDes;
	}

	/**
	 * Erfragt die in der untersten TLS-Hierarchie unter diesem Geraet liegenden
	 * Geraete (DEs).
	 * 
	 * @return die in der untersten TLS-Hierarchie unter diesem Geraet liegenden
	 *         Geraete (ggf. leere Liste)
	 */
	public final Set<De> getDes() {
		if (this.des == null) {
			synchronized (this) {
				this.des = new HashSet<De>();
				sammleDes(this.des);
			}
		}

		return this.des;
	}

	/**
	 * Erfragt das mit diesem Objekt assoziierte Systemobjekt.
	 * 
	 * @return das mit diesem Objekt assoziierte Systemobjekt
	 */
	public final SystemObject getObjekt() {
		return this.objekt;
	}

	/**
	 * Sammelt rekursiv alle DE unterhalb dieses Objektes.
	 * 
	 * @param des1
	 *            eine Menge mit Des
	 */
	private void sammleDes(Set<De> des1) {
		if (this.getGeraeteArt() == Art.DE) {
			des1.add((De) this);
		} else {
			for (AbstraktGeraet kind : this.kinder) {
				kind.sammleDes(des1);
			}
		}
	}

	/**
	 * Veröffentlicht eine Betriebsmeldung (einmalig)
	 * @param message Betriebsmeldung
	 */
	public void publiziere(OperatingMessage message) {
		if(message.getMessage().equals(_lastMessage)) {
			return;
		}
		_lastMessage = message.getMessage();
		message.send();
	}
}
