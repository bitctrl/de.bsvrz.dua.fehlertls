/**
 * Segment 4 Datenübernahme und Aufbereitung (DUA), SWE 4.DeFa DE Fehleranalyse fehlende Messdaten
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
 * Weißenfelser Straße 67<br>
 * 04229 Leipzig<br>
 * Phone: +49 341-490670<br>
 * mailto: info@bitctrl.de
 */

package de.bsvrz.dua.fehlertls.de;

import java.util.ArrayList;
import java.util.List;

import de.bsvrz.dav.daf.main.ClientDavInterface;
import de.bsvrz.dav.daf.main.DataDescription;
import de.bsvrz.dav.daf.main.config.Aspect;
import de.bsvrz.dav.daf.main.config.AttributeGroup;
import de.bsvrz.sys.funclib.bitctrl.daf.DaVKonstanten;

/**
 * Von diesem Typ sollten alle finalen DE-Typ-Beschreibungen abgeleitet sein.
 * 
 * @author BitCtrl Systems GmbH, Thierfelder
 * 
 * @version $Id$
 */
public abstract class AbstraktDeTyp implements IDeTyp {

	/**
	 * Erfragt einen Array mit (ATG-Pid, ASP-Pid, SIMVAR)-Tripeln, die eine
	 * Datenidentifikation von Messwerten beschreiben, welche bzgl. dieses DE
	 * ueberprueft werden sollen
	 * 
	 * @return ggf. leerer Array mit (ATG, ASP, SIMVAR)-Tripeln
	 */
	protected abstract DataDescriptionPid[] getDataIdentifikations();

	/**
	 * Erfragt die Pid der Attributgruppe, in der die Betriebsparameter des DE
	 * parametriert sind.
	 * 
	 * @return die Pid der Attributgruppe, in der die Betriebsparameter des DE
	 *         parametriert sind
	 */
	protected abstract String getBetriebsParameterAtgPid();

	/**
	 * {@inheritDoc}
	 */
	public DataDescription[] getDeFaMesswertDataDescriptions(
			final ClientDavInterface dav) throws DeFaException {
		List<DataDescription> dataDescriptions = new ArrayList<DataDescription>();

		for (DataDescriptionPid dataIdentifikation : this
				.getDataIdentifikations()) {
			AttributeGroup atg = dav.getDataModel().getAttributeGroup(
					dataIdentifikation.getAtgPid());
			if (atg == null) {
				throw new DeFaException(
						"Attributgruppe " + dataIdentifikation.getAtgPid() + //$NON-NLS-1$
								" existiert nicht im Datenkatalog"); //$NON-NLS-1$
			}

			Aspect asp = dav.getDataModel().getAspect(
					dataIdentifikation.getAspPid());
			if (asp == null) {
				throw new DeFaException(
						"Aspekt " + dataIdentifikation.getAspPid() + //$NON-NLS-1$
								" existiert nicht im Datenkatalog"); //$NON-NLS-1$
			}

			dataDescriptions.add(new DataDescription(atg, asp,
					dataIdentifikation.getSimVar()));
		}

		return dataDescriptions.toArray(new DataDescription[0]);
	}

	/**
	 * {@inheritDoc}
	 */
	public DataDescription getDeFaIntervallParameterDataDescription(
			ClientDavInterface dav) throws DeFaException {
		AttributeGroup atg = dav.getDataModel().getAttributeGroup(
				this.getBetriebsParameterAtgPid());

		if (atg == null) {
			throw new DeFaException(
					"Die Parameter-Attributgruppe " + this.getBetriebsParameterAtgPid() + //$NON-NLS-1$ 
							" konnte nicht identifiziert werden"); //$NON-NLS-1$
		}

		return new DataDescription(atg, dav.getDataModel().getAspect(
				DaVKonstanten.ASP_PARAMETER_SOLL), (short) 0);
	}

	/**
	 * Haelt pro Instanz das Tripel (ATG-Pid, ASP-Pid, SIMVAR) vor, das eine
	 * Datenidentifikation beschreibt.
	 * 
	 * @author BitCtrl Systems GmbH, Thierfelder
	 * 
	 */
	protected class DataDescriptionPid {

		/**
		 * Pid einer ATG.
		 */
		private String atgPid = null;

		/**
		 * Pid eines ASP.
		 */
		private String aspPid = null;

		/**
		 * Simulationsvariante.
		 */
		private short simVar = 0;

		/**
		 * Standardkonstruktor (Simulationsvariante ist 0).
		 * 
		 * @param atgPid
		 *            Pid einer ATG
		 * @param aspPid
		 *            Pid eines ASP
		 */
		public DataDescriptionPid(final String atgPid, final String aspPid) {
			this.atgPid = atgPid;
			this.aspPid = aspPid;
		}

		/**
		 * Konstruktor.
		 * 
		 * @param atgPid
		 *            Pid einer ATG
		 * @param aspPid
		 *            Pid eines ASP
		 * @param simVar
		 *            Simulationsvariante
		 */
		public DataDescriptionPid(final String atgPid, final String aspPid,
				final short simVar) {
			this(atgPid, aspPid);
			this.simVar = simVar;
		}

		/**
		 * Erfragt die Pid des ASP.
		 * 
		 * @return die Pid des ASP
		 */
		protected String getAspPid() {
			return aspPid;
		}

		/**
		 * Erfragt die Pid der ATG.
		 * 
		 * @return die Pid der ATG
		 */
		protected String getAtgPid() {
			return atgPid;
		}

		/**
		 * Erfragt die Simulationsvariante.
		 * 
		 * @return die Simulationsvariante
		 */
		protected short getSimVar() {
			return simVar;
		}

	}

}
