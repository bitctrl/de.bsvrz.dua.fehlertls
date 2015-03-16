/**
 * Segment 4 Daten�bernahme und Aufbereitung (DUA), SWE 4.DeFa DE Fehleranalyse fehlende Messdaten
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
 * Wei�enfelser Stra�e 67<br>
 * 04229 Leipzig<br>
 * Phone: +49 341-490670<br>
 * mailto: info@bitctrl.de
 */

package de.bsvrz.dua.fehlertls.tls;

import java.util.HashSet;
import java.util.Set;

import com.bitctrl.Constants;

import de.bsvrz.dav.daf.main.ClientDavInterface;
import de.bsvrz.dav.daf.main.config.ConfigurationObject;
import de.bsvrz.dav.daf.main.config.SystemObject;
import de.bsvrz.dua.fehlertls.fehlertls.SingleMessageSender;

/**
 * Abstrakte Repraesentation einer Objektes vom Typ <code>typ.ger�t</code>.
 *
 * @author BitCtrl Systems GmbH, Thierfelder
 *
 * @version $Id$
 */
public abstract class TlsHierarchieElement {

	/**
	 * ob die TLS-Hierarchie in einem Baum dargestellt werden soll.
	 */
	private static final boolean TLS_BAUM = false;

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
	private static ClientDavInterface dav;

	/**
	 * zur einmaligen Publikation von Fehlermeldungen.
	 */
	private final SingleMessageSender einzelPublikator;

	/**
	 * das Konfigurationsobjekt vom Typ <code>typ.ger�t</code>.
	 */
	private final ConfigurationObject objekt;

	/**
	 * die in der TLS-Hierarchie unter diesem Geraet liegenden Geraete.
	 */
	private final Set<TlsHierarchieElement> kinder = new HashSet<TlsHierarchieElement>();

	/**
	 * das in der TLS-Hierarchie ueber diesem Geraet liegende Geraet.
	 */
	private final TlsHierarchieElement vater;

	/**
	 * alle DEs, die sich unterhalb von diesem Element befinden.
	 */
	private Set<De> des;

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
	public boolean kannFehlerHierPublizieren(final long zeitStempel) {
		boolean kannHierPublizieren = false;

		if (this.getErfassteDes().size() > 0) {
			kannHierPublizieren = true;

			for (final De de : this.getErfassteDes()) {
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
	 *            ein Systemobjekt vom Typ <code>typ.ger�t</code>
	 * @param vater
	 *            das in der TLS-Hierarchie ueber diesem Geraet liegende Geraet
	 */
	protected TlsHierarchieElement(final ClientDavInterface dav,
			final SystemObject objekt, final TlsHierarchieElement vater) {
		if (TlsHierarchieElement.dav == null) {
			TlsHierarchieElement.dav = dav;
		}
		this.einzelPublikator = new SingleMessageSender(dav);
		this.objekt = (ConfigurationObject) objekt;
		this.vater = vater;

		if ((objekt != null) && objekt.isOfType("typ.ger�t")) {
			/** Initialisiere Anschlusspunkte. */
			for (final SystemObject ap : this.objekt.getNonMutableSet(
					"AnschlussPunkteGer�t").getElements()) {
				if (ap.isValid()) {
					addKind(new AnschlussPunkt(dav, ap, this));
				}
			}
		}
	}

	protected void addKind(final TlsHierarchieElement kind) {
		kinder.add(kind);
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
	public final Set<TlsHierarchieElement> getKinder() {
		return this.kinder;
	}

	/**
	 * Efragt das in der TLS-Hierarchie ueber diesem Geraet liegende Geraet.
	 *
	 * @return das in der TLS-Hierarchie ueber diesem Geraet liegende Geraet
	 *         bzw. <code>null</code>, wenn dieses Geraet die Spitze der
	 *         Hierarchie sein sollte
	 */
	public final TlsHierarchieElement getVater() {
		return this.vater;
	}

	/**
	 * Erfragt ob dieses Geraet an der Spitze einer TLS-Hierarchie steht.
	 *
	 * @return ob dieses Geraet an der Spitze einer TLS-Hierarchie steht
	 */
	public final boolean isTopElement() {
		return (this.vater == null) || (this.vater.getObjekt() == null);
	}

	@Override
	public boolean equals(final Object obj) {
		boolean ergebnis = false;

		if ((obj != null) && (obj instanceof TlsHierarchieElement)) {
			final TlsHierarchieElement that = (TlsHierarchieElement) obj;
			ergebnis = this.objekt.getId() == that.objekt.getId();
		}

		return ergebnis;
	}

	@Override
	public String toString() {
		if (TlsHierarchieElement.TLS_BAUM) {
			String baum = Constants.EMPTY_STRING;

			TlsHierarchieElement dummy = this;
			while (dummy.getVater() != null) {
				baum += "   ";
				dummy = dummy.getVater();
			}
			baum += this.objekt == null ? "WURZEL" : this.objekt.getPid();
			for (final TlsHierarchieElement kind : this.kinder) {
				baum += "\n" + kind.toString();
			}

			return baum;
		}
		String v = "keiner";
		String k = "keine";

		if (this.vater != null) {
			v = this.vater.getObjekt() == null ? "WURZEL" : this.vater
					.getObjekt().getPid();
		}
		if (!this.kinder.isEmpty()) {
			final TlsHierarchieElement[] dummy = this.kinder
					.toArray(new TlsHierarchieElement[0]);
			k = dummy[0].getObjekt().getPid();
			for (int i = 1; i < dummy.length; i++) {
				k += ", " + dummy[i].getObjekt().getPid();
			}
		}

		return this.objekt == null ? "WURZEL" : this.objekt.toString()
				+ " (Vater: " + v + ", Kinder:[" + k + "])";
	}

	/**
	 * Erfragt die in der untersten TLS-Hierarchie unter diesem Geraet liegenden
	 * DEs, die von der DeFa im Moment erfasst sind.
	 *
	 * @return die in der untersten TLS-Hierarchie unter diesem Geraet liegenden
	 *         DEs, die von der DeFa im Moment erfasst sind (ggf. leere Liste)
	 */
	public final Set<De> getErfassteDes() {
		final Set<De> erfassteDes = new HashSet<De>();

		for (final De de : getDes()) {
			if ((de.getZustand() != null) && de.getZustand().isErfasst()) {
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
	protected final ConfigurationObject getObjekt() {
		return this.objekt;
	}

	/**
	 * Sammelt rekursiv alle DE unterhalb dieses Objektes.
	 *
	 * @param des1
	 *            eine Menge mit Des
	 */
	private void sammleDes(final Set<De> des1) {
		if (this.getGeraeteArt() == Art.DE) {
			des1.add((De) this);
		} else {
			for (final TlsHierarchieElement kind : this.kinder) {
				kind.sammleDes(des1);
			}
		}
	}

	protected SingleMessageSender getEinzelPublikator() {
		return einzelPublikator;
	}

	protected static ClientDavInterface getDav() {
		return TlsHierarchieElement.dav;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (prime * result) + ((objekt == null) ? 0 : objekt.hashCode());
		return result;
	}

}
