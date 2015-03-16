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
 *
 * @version $Id$
 */
public class TypDeTest extends AbstraktDeTyp {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public long getErfassungsIntervall(final Data parameter) {
		long erfassungsIntervallDauer = -1;

		if (parameter.getUnscaledValue("Übertragungsverfahren").intValue() == 1) {
			erfassungsIntervallDauer = parameter.getTimeValue(
					"Erfassungsperiodendauer").getMillis();
		}

		return erfassungsIntervallDauer;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getBetriebsParameterAtgPid() {
		return "atg.testBetriebsParameter";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DataDescriptionPid[] getDataIdentifikations() {
		return new DataDescriptionPid[] { new DataDescriptionPid("atg.test",
				DUAKonstanten.ASP_TLS_ANTWORT, (short) 0), };
	}

}
