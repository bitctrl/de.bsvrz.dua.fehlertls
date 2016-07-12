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
 * DeFa-Beschreibung eines DE-Typs zur Umfelddatenerfassung.<br>
 * (PID: typ.deUfd)
 * 
 * @author BitCtrl Systems GmbH, Thierfelder
 * 
 * @version $Id$
 */
public class TypDeUfd extends AbstraktDeTyp {

	/**
	 * {@inheritDoc}
	 */
	public long getErfassungsIntervall(Data parameter) {
		long erfassungsIntervallDauer = -1;

		if (parameter.getUnscaledValue("Übertragungsverfahren").intValue() == 1) { //$NON-NLS-1$
			erfassungsIntervallDauer = parameter.getUnscaledValue(
					"Erfassungsperiodendauer").longValue() * 1000L; //$NON-NLS-1$
		}

		return erfassungsIntervallDauer;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getBetriebsParameterAtgPid() {
		return "atg.tlsUfdBetriebsParameter"; //$NON-NLS-1$
	}

	/**
	 * {@inheritDoc}
	 */
	public DataDescriptionPid[] getDataIdentifikations() {
		return new DataDescriptionPid[] {
				new DataDescriptionPid(
						"atg.tlsUfdErgebnisMeldungLuftTemperaturLT", DUAKonstanten.ASP_TLS_ANTWORT, (short) 0), //$NON-NLS-1$
				new DataDescriptionPid(
						"atg.tlsUfdErgebnisMeldungFahrbahnOberFlächenTemperaturFBT", DUAKonstanten.ASP_TLS_ANTWORT, (short) 0), //$NON-NLS-1$
				new DataDescriptionPid(
						"atg.tlsUfdErgebnisMeldungFahrbahnFeuchteFBF", DUAKonstanten.ASP_TLS_ANTWORT, (short) 0), //$NON-NLS-1$
				new DataDescriptionPid(
						"atg.tlsUfdErgebnisMeldungFahrbahnOberFläche", DUAKonstanten.ASP_TLS_ANTWORT, (short) 0), //$NON-NLS-1$
				new DataDescriptionPid(
						"atg.tlsUfdErgebnisMeldungRestSalzRS", DUAKonstanten.ASP_TLS_ANTWORT, (short) 0), //$NON-NLS-1$

				new DataDescriptionPid(
						"atg.tlsUfdErgebnisMeldungNiederschlagsIntensitätNI", DUAKonstanten.ASP_TLS_ANTWORT, (short) 0), //$NON-NLS-1$
				new DataDescriptionPid(
						"atg.tlsUfdErgebnisMeldungLuftDruckLD", DUAKonstanten.ASP_TLS_ANTWORT, (short) 0), //$NON-NLS-1$
				new DataDescriptionPid(
						"atg.tlsUfdErgebnisMeldungRelativeLuftFeuchteRLF", DUAKonstanten.ASP_TLS_ANTWORT, (short) 0), //$NON-NLS-1$
				new DataDescriptionPid(
						"atg.tlsUfdErgebnisMeldungWindRichtungWR", DUAKonstanten.ASP_TLS_ANTWORT, (short) 0), //$NON-NLS-1$
				new DataDescriptionPid(
						"atg.tlsUfdErgebnisMeldungWindGeschwindigkeitMittelWertWGM", DUAKonstanten.ASP_TLS_ANTWORT, (short) 0), //$NON-NLS-1$

				new DataDescriptionPid(
						"atg.tlsUfdErgebnisMeldungSchneeHöheSH", DUAKonstanten.ASP_TLS_ANTWORT, (short) 0), //$NON-NLS-1$
				new DataDescriptionPid(
						"atg.tlsUfdErgebnisMeldungFahrbahnGlätteFBG", DUAKonstanten.ASP_TLS_ANTWORT, (short) 0), //$NON-NLS-1$
				new DataDescriptionPid(
						"atg.tlsUfdErgebnisMeldungSichtWeiteSW", DUAKonstanten.ASP_TLS_ANTWORT, (short) 0), //$NON-NLS-1$
				new DataDescriptionPid(
						"atg.tlsUfdErgebnisMeldungHelligkeitHK", DUAKonstanten.ASP_TLS_ANTWORT, (short) 0), //$NON-NLS-1$
				new DataDescriptionPid(
						"atg.tlsUfdErgebnisMeldungNiederschlagsMengeNM", DUAKonstanten.ASP_TLS_ANTWORT, (short) 0), //$NON-NLS-1$

				new DataDescriptionPid(
						"atg.tlsUfdErgebnisMeldungNiederschlag", DUAKonstanten.ASP_TLS_ANTWORT, (short) 0), //$NON-NLS-1$
				new DataDescriptionPid(
						"atg.tlsUfdErgebnisMeldungWindGeschwindigkeitSpitzenWertWGS", DUAKonstanten.ASP_TLS_ANTWORT, (short) 0), //$NON-NLS-1$
				new DataDescriptionPid(
						"atg.tlsUfdErgebnisMeldungGefrierTemperaturGT", DUAKonstanten.ASP_TLS_ANTWORT, (short) 0), //$NON-NLS-1$
				new DataDescriptionPid(
						"atg.tlsUfdErgebnisMeldungTaupunktTemperaturTPT", DUAKonstanten.ASP_TLS_ANTWORT, (short) 0), //$NON-NLS-1$
				new DataDescriptionPid(
						"atg.tlsUfdErgebnisMeldungTemperaturInTiefe1TT1", DUAKonstanten.ASP_TLS_ANTWORT, (short) 0), //$NON-NLS-1$

				new DataDescriptionPid(
						"atg.tlsUfdErgebnisMeldungTemperaturInTiefe2TT2", DUAKonstanten.ASP_TLS_ANTWORT, (short) 0), //$NON-NLS-1$
				new DataDescriptionPid(
						"atg.tlsUfdErgebnisMeldungTemperaturInTiefe3TT3", DUAKonstanten.ASP_TLS_ANTWORT, (short) 0), //$NON-NLS-1$
				new DataDescriptionPid(
						"atg.tlsUfdErgebnisMeldungZustandDerFahrbahnOberFlächeFBZ", DUAKonstanten.ASP_TLS_ANTWORT, (short) 0), //$NON-NLS-1$
				new DataDescriptionPid(
						"atg.tlsUfdErgebnisMeldungNiederschlagsArtNS", DUAKonstanten.ASP_TLS_ANTWORT, (short) 0), //$NON-NLS-1$
				new DataDescriptionPid(
						"atg.tlsUfdErgebnisMeldungWasserFilmDickeWFD", DUAKonstanten.ASP_TLS_ANTWORT, (short) 0), //$NON-NLS-1$
				new DataDescriptionPid(
						"atg.tlsUfdErgebnisMeldungZeitreserveGlätteZG", DUAKonstanten.ASP_TLS_ANTWORT, (short) 0) //$NON-NLS-1$
		};
	}

}
