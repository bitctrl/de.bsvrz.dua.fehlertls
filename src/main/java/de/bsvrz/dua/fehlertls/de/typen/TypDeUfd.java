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
 * DeFa-Beschreibung eines DE-Typs zur Umfelddatenerfassung.<br>
 * (PID: typ.deUfd)
 *
 * @author BitCtrl Systems GmbH, Thierfelder
 */
public class TypDeUfd extends AbstraktDeTyp {

	@Override
	public long getErfassungsIntervall(final Data parameter) {
		long erfassungsIntervallDauer = -1;

		if (parameter.getUnscaledValue("Übertragungsverfahren").intValue() == 1) {
			erfassungsIntervallDauer = parameter.getUnscaledValue("Erfassungsperiodendauer").longValue() * 1000L;
		}

		return erfassungsIntervallDauer;
	}

	@Override
	protected String getBetriebsParameterAtgPid() {
		return "atg.tlsUfdBetriebsParameter";
	}

	@Override
	public DataDescriptionPid[] getDataIdentifikations() {
		return new DataDescriptionPid[] {
				new DataDescriptionPid("atg.tlsUfdErgebnisMeldungLuftTemperaturLT", DUAKonstanten.ASP_TLS_ANTWORT,
						(short) 0),
				new DataDescriptionPid("atg.tlsUfdErgebnisMeldungFahrbahnOberFlächenTemperaturFBT",
						DUAKonstanten.ASP_TLS_ANTWORT, (short) 0),
				new DataDescriptionPid("atg.tlsUfdErgebnisMeldungFahrbahnFeuchteFBF", DUAKonstanten.ASP_TLS_ANTWORT,
						(short) 0),
				new DataDescriptionPid("atg.tlsUfdErgebnisMeldungFahrbahnOberFläche", DUAKonstanten.ASP_TLS_ANTWORT,
						(short) 0),
				new DataDescriptionPid("atg.tlsUfdErgebnisMeldungRestSalzRS", DUAKonstanten.ASP_TLS_ANTWORT, (short) 0),

				new DataDescriptionPid("atg.tlsUfdErgebnisMeldungNiederschlagsIntensitätNI",
						DUAKonstanten.ASP_TLS_ANTWORT, (short) 0),
				new DataDescriptionPid("atg.tlsUfdErgebnisMeldungLuftDruckLD", DUAKonstanten.ASP_TLS_ANTWORT,
						(short) 0),
				new DataDescriptionPid("atg.tlsUfdErgebnisMeldungRelativeLuftFeuchteRLF", DUAKonstanten.ASP_TLS_ANTWORT,
						(short) 0),
				new DataDescriptionPid("atg.tlsUfdErgebnisMeldungWindRichtungWR", DUAKonstanten.ASP_TLS_ANTWORT,
						(short) 0),
				new DataDescriptionPid("atg.tlsUfdErgebnisMeldungWindGeschwindigkeitMittelWertWGM",
						DUAKonstanten.ASP_TLS_ANTWORT, (short) 0),

				new DataDescriptionPid("atg.tlsUfdErgebnisMeldungSchneeHöheSH", DUAKonstanten.ASP_TLS_ANTWORT,
						(short) 0),
				new DataDescriptionPid("atg.tlsUfdErgebnisMeldungFahrbahnGlätteFBG", DUAKonstanten.ASP_TLS_ANTWORT,
						(short) 0),
				new DataDescriptionPid("atg.tlsUfdErgebnisMeldungSichtWeiteSW", DUAKonstanten.ASP_TLS_ANTWORT,
						(short) 0),
				new DataDescriptionPid("atg.tlsUfdErgebnisMeldungHelligkeitHK", DUAKonstanten.ASP_TLS_ANTWORT,
						(short) 0),
				new DataDescriptionPid("atg.tlsUfdErgebnisMeldungNiederschlagsMengeNM", DUAKonstanten.ASP_TLS_ANTWORT,
						(short) 0),

				new DataDescriptionPid("atg.tlsUfdErgebnisMeldungNiederschlag", DUAKonstanten.ASP_TLS_ANTWORT,
						(short) 0),
				new DataDescriptionPid("atg.tlsUfdErgebnisMeldungWindGeschwindigkeitSpitzenWertWGS",
						DUAKonstanten.ASP_TLS_ANTWORT, (short) 0),
				new DataDescriptionPid("atg.tlsUfdErgebnisMeldungGefrierTemperaturGT", DUAKonstanten.ASP_TLS_ANTWORT,
						(short) 0),
				new DataDescriptionPid("atg.tlsUfdErgebnisMeldungTaupunktTemperaturTPT", DUAKonstanten.ASP_TLS_ANTWORT,
						(short) 0),
				new DataDescriptionPid("atg.tlsUfdErgebnisMeldungTemperaturInTiefe1TT1", DUAKonstanten.ASP_TLS_ANTWORT,
						(short) 0),

				new DataDescriptionPid("atg.tlsUfdErgebnisMeldungTemperaturInTiefe2TT2", DUAKonstanten.ASP_TLS_ANTWORT,
						(short) 0),
				new DataDescriptionPid("atg.tlsUfdErgebnisMeldungTemperaturInTiefe3TT3", DUAKonstanten.ASP_TLS_ANTWORT,
						(short) 0),
				new DataDescriptionPid("atg.tlsUfdErgebnisMeldungZustandDerFahrbahnOberFlächeFBZ",
						DUAKonstanten.ASP_TLS_ANTWORT, (short) 0),
				new DataDescriptionPid("atg.tlsUfdErgebnisMeldungNiederschlagsArtNS", DUAKonstanten.ASP_TLS_ANTWORT,
						(short) 0),
				new DataDescriptionPid("atg.tlsUfdErgebnisMeldungWasserFilmDickeWFD", DUAKonstanten.ASP_TLS_ANTWORT,
						(short) 0),
				new DataDescriptionPid("atg.tlsUfdErgebnisMeldungZeitreserveGlätteZG", DUAKonstanten.ASP_TLS_ANTWORT,
						(short) 0) };
	}

}
