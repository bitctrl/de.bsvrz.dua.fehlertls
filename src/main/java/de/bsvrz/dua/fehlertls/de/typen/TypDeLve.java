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

package de.bsvrz.dua.fehlertls.de.typen;

import de.bsvrz.dav.daf.main.Data;
import de.bsvrz.dua.fehlertls.de.AbstraktDeTyp;
import de.bsvrz.sys.funclib.bitctrl.dua.DUAKonstanten;

/**
 * DeFa-Beschreibung eines DE-Typs zur Langzeitdatenerfassung<br>
 * (PID: typ.deLve).
 *
 * @author BitCtrl Systems GmbH, Thierfelder
 *
 * @version $Id$
 */
public class TypDeLve extends AbstraktDeTyp {

	@Override
	public long getErfassungsIntervall(final Data parameter) {
		return parameter.getUnscaledValue("IntervallDauerKurzZeitDaten")
				.longValue() * 15L * 1000L;
	}

	@Override
	protected String getBetriebsParameterAtgPid() {
		return "atg.tlsLveBetriebsParameter";
	}

	@Override
	protected DataDescriptionPid[] getDataIdentifikations() {

		return new DataDescriptionPid[] { new DataDescriptionPid(
				"atg.tlsLveErgebnisMeldungVersion0Bis4",
				DUAKonstanten.ASP_TLS_ANTWORT, (short) 0) };

	}

}
