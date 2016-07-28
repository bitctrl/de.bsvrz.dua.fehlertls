/*
 * Copyright 2016 by Kappich Systemberatung Aachen
 * 
 * This file is part of de.bsvrz.dua.fehlertls.tests.
 * 
 * de.bsvrz.dua.fehlertls.tests is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * de.bsvrz.dua.fehlertls.tests is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with de.bsvrz.dua.fehlertls.tests.  If not, see <http://www.gnu.org/licenses/>.

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
 * DeFa-Beschreibung eines Test DE-Typs (nur für Test-Zwecke innerhalb der
 * Test-Konfiguration).<br>
 * (PID: typ.deTest)
 * 
 * @author BitCtrl Systems GmbH, Thierfelder
 */
public class TypDeTest extends AbstraktDeTyp {

	public long getErfassungsIntervall(Data parameter) {
		long erfassungsIntervallDauer = -1;

		if (parameter.getUnscaledValue("Übertragungsverfahren").intValue() == 1) {
			erfassungsIntervallDauer = parameter.getTimeValue("Erfassungsperiodendauer").getMillis();
		}

		return erfassungsIntervallDauer;
	}

	@Override
	protected String getBetriebsParameterAtgPid() {
		return "atg.testBetriebsParameter";
	}

	public DataDescriptionPid[] getDataIdentifikations() {
		return new DataDescriptionPid[] { new DataDescriptionPid(
				"atg.test", DUAKonstanten.ASP_TLS_ANTWORT, (short) 0),
		};
	}

}
