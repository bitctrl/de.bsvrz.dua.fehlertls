/*
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

package de.bsvrz.dua.fehlertls.de.typen;

import de.bsvrz.dav.daf.main.Data;
import de.bsvrz.dua.fehlertls.de.AbstraktDeTyp;
import de.bsvrz.sys.funclib.bitctrl.dua.DUAKonstanten;

/**
 * DeFa-Beschreibung eines Test DE-Typs (nur f�r Test-Zwecke innerhalb der
 * Test-Konfiguration).<br>
 * (PID: typ.deTest)
 *
 * @author BitCtrl Systems GmbH, Thierfelder
 */
public class TypDeTest extends AbstraktDeTyp {

	@Override
	public long getErfassungsIntervall(final Data parameter) {
		long erfassungsIntervallDauer = -1;

		if (parameter.getUnscaledValue("�bertragungsverfahren").intValue() == 1) {
			erfassungsIntervallDauer = parameter.getTimeValue("Erfassungsperiodendauer").getMillis();
		}

		return erfassungsIntervallDauer;
	}

	@Override
	protected String getBetriebsParameterAtgPid() {
		return "atg.testBetriebsParameter";
	}

	@Override
	public DataDescriptionPid[] getDataIdentifikations() {
		return new DataDescriptionPid[] {
				new DataDescriptionPid("atg.test", DUAKonstanten.ASP_TLS_ANTWORT, (short) 0), };
	}

}
