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
 * DeFa-Beschreibung eines DE-Typs zur Umfelddatenerfassung 
 * (PID: typ.deUfd)
 * 
 * @author BitCtrl Systems GmbH, Thierfelder
 *
 */
public class TypDeUfd
extends AbstraktDeTyp{

	/**
	 * {@inheritDoc}
	 */
	public long getErfassungsIntervall(Data parameter){
		long erfassungsIntervallDauer = -1;
		
		if(parameter.getUnscaledValue("Übertragungsverfahren").intValue() == 1){ //$NON-NLS-1$
			erfassungsIntervallDauer = parameter.getUnscaledValue("Erfassungsperiodendauer").longValue() * 1000L; //$NON-NLS-1$
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
	public DataIdentifikation[] getDataIdentifikations() {
		return new DataIdentifikation[]{
			new DataIdentifikation("atg.tlsUfdErgebnisMeldungLuftTemperaturLT", DUAKonstanten.ASP_TLS_ANTWORT, (short)0), //$NON-NLS-1$
			new DataIdentifikation("atg.tlsUfdErgebnisMeldungFahrbahnOberFlächenTemperaturFBT", DUAKonstanten.ASP_TLS_ANTWORT, (short)0), //$NON-NLS-1$
			new DataIdentifikation("atg.tlsUfdErgebnisMeldungFahrbahnFeuchteFBF", DUAKonstanten.ASP_TLS_ANTWORT, (short)0), //$NON-NLS-1$
			new DataIdentifikation("atg.tlsUfdErgebnisMeldungFahrbahnOberFläche", DUAKonstanten.ASP_TLS_ANTWORT, (short)0), //$NON-NLS-1$
			new DataIdentifikation("atg.tlsUfdErgebnisMeldungRestSalzRS", DUAKonstanten.ASP_TLS_ANTWORT, (short)0), //$NON-NLS-1$
			
			new DataIdentifikation("atg.tlsUfdErgebnisMeldungNiederschlagsIntensitätNI", DUAKonstanten.ASP_TLS_ANTWORT, (short)0), //$NON-NLS-1$
			new DataIdentifikation("atg.tlsUfdErgebnisMeldungLuftDruckLD", DUAKonstanten.ASP_TLS_ANTWORT, (short)0), //$NON-NLS-1$
			new DataIdentifikation("atg.tlsUfdErgebnisMeldungRelativeLuftFeuchteRLF", DUAKonstanten.ASP_TLS_ANTWORT, (short)0), //$NON-NLS-1$
			new DataIdentifikation("atg.tlsUfdErgebnisMeldungWindRichtungWR", DUAKonstanten.ASP_TLS_ANTWORT, (short)0), //$NON-NLS-1$
			new DataIdentifikation("atg.tlsUfdErgebnisMeldungWindGeschwindigkeitMittelWertWGM", DUAKonstanten.ASP_TLS_ANTWORT, (short)0), //$NON-NLS-1$
			
			new DataIdentifikation("atg.tlsUfdErgebnisMeldungSchneeHöheSH", DUAKonstanten.ASP_TLS_ANTWORT, (short)0), //$NON-NLS-1$
			new DataIdentifikation("atg.tlsUfdErgebnisMeldungFahrbahnGlätteFBG", DUAKonstanten.ASP_TLS_ANTWORT, (short)0), //$NON-NLS-1$
			new DataIdentifikation("atg.tlsUfdErgebnisMeldungSichtWeiteSW", DUAKonstanten.ASP_TLS_ANTWORT, (short)0), //$NON-NLS-1$
			new DataIdentifikation("atg.tlsUfdErgebnisMeldungHelligkeitHK", DUAKonstanten.ASP_TLS_ANTWORT, (short)0), //$NON-NLS-1$
			new DataIdentifikation("atg.tlsUfdErgebnisMeldungNiederschlagsMengeNM", DUAKonstanten.ASP_TLS_ANTWORT, (short)0), //$NON-NLS-1$
			
			new DataIdentifikation("atg.tlsUfdErgebnisMeldungNiederschlag", DUAKonstanten.ASP_TLS_ANTWORT, (short)0), //$NON-NLS-1$
			new DataIdentifikation("atg.tlsUfdErgebnisMeldungWindGeschwindigkeitSpitzenWertWGS", DUAKonstanten.ASP_TLS_ANTWORT, (short)0), //$NON-NLS-1$
			new DataIdentifikation("atg.tlsUfdErgebnisMeldungGefrierTemperaturGT", DUAKonstanten.ASP_TLS_ANTWORT, (short)0), //$NON-NLS-1$
			new DataIdentifikation("atg.tlsUfdErgebnisMeldungTaupunktTemperaturTPT", DUAKonstanten.ASP_TLS_ANTWORT, (short)0), //$NON-NLS-1$
			new DataIdentifikation("atg.tlsUfdErgebnisMeldungTemperaturInTiefe1TT1", DUAKonstanten.ASP_TLS_ANTWORT, (short)0), //$NON-NLS-1$
			
			new DataIdentifikation("atg.tlsUfdErgebnisMeldungTemperaturInTiefe2TT2", DUAKonstanten.ASP_TLS_ANTWORT, (short)0), //$NON-NLS-1$
			new DataIdentifikation("atg.tlsUfdErgebnisMeldungTemperaturInTiefe3TT3", DUAKonstanten.ASP_TLS_ANTWORT, (short)0), //$NON-NLS-1$
			new DataIdentifikation("atg.tlsUfdErgebnisMeldungZustandDerFahrbahnOberFlächeFBZ", DUAKonstanten.ASP_TLS_ANTWORT, (short)0), //$NON-NLS-1$
			new DataIdentifikation("atg.tlsUfdErgebnisMeldungNiederschlagsArtNS", DUAKonstanten.ASP_TLS_ANTWORT, (short)0), //$NON-NLS-1$
			new DataIdentifikation("atg.tlsUfdErgebnisMeldungWasserFilmDickeWFD", DUAKonstanten.ASP_TLS_ANTWORT, (short)0), //$NON-NLS-1$
			new DataIdentifikation("atg.tlsUfdErgebnisMeldungZeitreserveGlätteZG", DUAKonstanten.ASP_TLS_ANTWORT, (short)0) //$NON-NLS-1$
		};
	}
	
}
