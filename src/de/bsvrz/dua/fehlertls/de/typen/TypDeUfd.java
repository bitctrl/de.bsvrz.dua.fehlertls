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
import de.bsvrz.dua.fehlertls.de.AnalyseDatenBeschreibung;
import de.bsvrz.dua.fehlertls.de.IBetriebsParameterInfo;
import de.bsvrz.dua.fehlertls.de.IDeTyp;

/**
 * DeFa-Beschreibung eines DE-Typs zur Umfelddatenerfassung 
 * (PID: typ.deUfd)
 * 
 * @author BitCtrl Systems GmbH, Thierfelder
 *
 */
public class TypDeUfd
implements IDeTyp{

	/**
	 * Informationen zu den Betriebsparametern von UFD-DE 
	 */
	private static IBetriebsParameterInfo PARAMETER_INFO = new IBetriebsParameterInfo(){

		/**
		 * {@inheritDoc}
		 */
		public String getBetriebsParameterAtgPid() {
			return "atg.tlsUfdBetriebsParameter"; //$NON-NLS-1$
		}

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
		
	};
	
	
	/**
	 * {@inheritDoc}
	 */
	public AnalyseDatenBeschreibung[] getAnalyseDatenBeschreibung() {
		return new AnalyseDatenBeschreibung[]{
			new AnalyseDatenBeschreibung("atg.tlsUfdErgebnisMeldungLuftTemperaturLT", ASP_TLS_ANTWORT, (short)0, PARAMETER_INFO ), //$NON-NLS-1$
			new AnalyseDatenBeschreibung("atg.tlsUfdErgebnisMeldungFahrbahnOberFlächenTemperaturFBT", ASP_TLS_ANTWORT, (short)0, PARAMETER_INFO ), //$NON-NLS-1$
			new AnalyseDatenBeschreibung("atg.tlsUfdErgebnisMeldungFahrbahnFeuchteFBF", ASP_TLS_ANTWORT, (short)0, PARAMETER_INFO ), //$NON-NLS-1$
			new AnalyseDatenBeschreibung("atg.tlsUfdErgebnisMeldungFahrbahnOberFläche", ASP_TLS_ANTWORT, (short)0, PARAMETER_INFO ), //$NON-NLS-1$
			new AnalyseDatenBeschreibung("atg.tlsUfdErgebnisMeldungRestSalzRS", ASP_TLS_ANTWORT, (short)0, PARAMETER_INFO ), //$NON-NLS-1$
			
			new AnalyseDatenBeschreibung("atg.tlsUfdErgebnisMeldungNiederschlagsIntensitätNI", ASP_TLS_ANTWORT, (short)0, PARAMETER_INFO ), //$NON-NLS-1$
			new AnalyseDatenBeschreibung("atg.tlsUfdErgebnisMeldungLuftDruckLD", ASP_TLS_ANTWORT, (short)0, PARAMETER_INFO ), //$NON-NLS-1$
			new AnalyseDatenBeschreibung("atg.tlsUfdErgebnisMeldungRelativeLuftFeuchteRLF", ASP_TLS_ANTWORT, (short)0, PARAMETER_INFO ), //$NON-NLS-1$
			new AnalyseDatenBeschreibung("atg.tlsUfdErgebnisMeldungWindRichtungWR", ASP_TLS_ANTWORT, (short)0, PARAMETER_INFO ), //$NON-NLS-1$
			new AnalyseDatenBeschreibung("atg.tlsUfdErgebnisMeldungWindGeschwindigkeitMittelWertWGM", ASP_TLS_ANTWORT, (short)0, PARAMETER_INFO ), //$NON-NLS-1$
			
			new AnalyseDatenBeschreibung("atg.tlsUfdErgebnisMeldungSchneeHöheSH", ASP_TLS_ANTWORT, (short)0, PARAMETER_INFO ), //$NON-NLS-1$
			new AnalyseDatenBeschreibung("atg.tlsUfdErgebnisMeldungFahrbahnGlätteFBG", ASP_TLS_ANTWORT, (short)0, PARAMETER_INFO ), //$NON-NLS-1$
			new AnalyseDatenBeschreibung("atg.tlsUfdErgebnisMeldungSichtWeiteSW", ASP_TLS_ANTWORT, (short)0, PARAMETER_INFO ), //$NON-NLS-1$
			new AnalyseDatenBeschreibung("atg.tlsUfdErgebnisMeldungHelligkeitHK", ASP_TLS_ANTWORT, (short)0, PARAMETER_INFO ), //$NON-NLS-1$
			new AnalyseDatenBeschreibung("atg.tlsUfdErgebnisMeldungNiederschlagsMengeNM", ASP_TLS_ANTWORT, (short)0, PARAMETER_INFO ), //$NON-NLS-1$
			
			new AnalyseDatenBeschreibung("atg.tlsUfdErgebnisMeldungNiederschlag", ASP_TLS_ANTWORT, (short)0, PARAMETER_INFO ), //$NON-NLS-1$
			new AnalyseDatenBeschreibung("atg.tlsUfdErgebnisMeldungWindGeschwindigkeitSpitzenWertWGS", ASP_TLS_ANTWORT, (short)0, PARAMETER_INFO ), //$NON-NLS-1$
			new AnalyseDatenBeschreibung("atg.tlsUfdErgebnisMeldungGefrierTemperaturGT", ASP_TLS_ANTWORT, (short)0, PARAMETER_INFO ), //$NON-NLS-1$
			new AnalyseDatenBeschreibung("atg.tlsUfdErgebnisMeldungTaupunktTemperaturTPT", ASP_TLS_ANTWORT, (short)0, PARAMETER_INFO ), //$NON-NLS-1$
			new AnalyseDatenBeschreibung("atg.tlsUfdErgebnisMeldungTemperaturInTiefe1TT1", ASP_TLS_ANTWORT, (short)0, PARAMETER_INFO ), //$NON-NLS-1$
			
			new AnalyseDatenBeschreibung("atg.tlsUfdErgebnisMeldungTemperaturInTiefe2TT2", ASP_TLS_ANTWORT, (short)0, PARAMETER_INFO ), //$NON-NLS-1$
			new AnalyseDatenBeschreibung("atg.tlsUfdErgebnisMeldungTemperaturInTiefe3TT3", ASP_TLS_ANTWORT, (short)0, PARAMETER_INFO ), //$NON-NLS-1$
			new AnalyseDatenBeschreibung("atg.tlsUfdErgebnisMeldungZustandDerFahrbahnOberFlächeFBZ", ASP_TLS_ANTWORT, (short)0, PARAMETER_INFO ), //$NON-NLS-1$
			new AnalyseDatenBeschreibung("atg.tlsUfdErgebnisMeldungNiederschlagsArtNS", ASP_TLS_ANTWORT, (short)0, PARAMETER_INFO ), //$NON-NLS-1$
			new AnalyseDatenBeschreibung("atg.tlsUfdErgebnisMeldungWasserFilmDickeWFD", ASP_TLS_ANTWORT, (short)0, PARAMETER_INFO ), //$NON-NLS-1$
			new AnalyseDatenBeschreibung("atg.tlsUfdErgebnisMeldungZeitreserveGlätteZG", ASP_TLS_ANTWORT, (short)0, PARAMETER_INFO ) //$NON-NLS-1$
		};
	}
	
}
