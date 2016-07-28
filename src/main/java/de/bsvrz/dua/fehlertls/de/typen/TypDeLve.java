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

package de.bsvrz.dua.fehlertls.de.typen;

import de.bsvrz.dav.daf.main.Data;
import de.bsvrz.dua.fehlertls.de.AbstraktDeTyp;
import de.bsvrz.sys.funclib.bitctrl.dua.DUAKonstanten;

/**
 * DeFa-Beschreibung eines DE-Typs zur Langzeitdatenerfassung<br>
 * (PID: typ.deLve).
 * 
 * @author BitCtrl Systems GmbH, Thierfelder
 */
public class TypDeLve extends AbstraktDeTyp {

	public long getErfassungsIntervall(Data parameter) {
		return parameter
				.getUnscaledValue("IntervallDauerKurzZeitDaten").longValue() * 15L * 1000L; //$NON-NLS-1$
	}

	@Override
	protected String getBetriebsParameterAtgPid() {
		return "atg.tlsLveBetriebsParameter"; //$NON-NLS-1$
	}

	@Override
	protected DataDescriptionPid[] getDataIdentifikations() {

		return new DataDescriptionPid[] { new DataDescriptionPid(
				"atg.tlsLveErgebnisMeldungVersion0Bis4", DUAKonstanten.ASP_TLS_ANTWORT, (short) 0) //$NON-NLS-1$
		};

	}

}
