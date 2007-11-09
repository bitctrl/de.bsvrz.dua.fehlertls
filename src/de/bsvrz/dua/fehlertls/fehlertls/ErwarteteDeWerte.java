/**
 * Segment 4 Daten�bernahme und Aufbereitung (DUA), SWE 4.DeFa DE Fehleranalyse fehlende Messdaten
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
 * Wei�enfelser Stra�e 67<br>
 * 04229 Leipzig<br>
 * Phone: +49 341-490670<br>
 * mailto: info@bitctrl.de
 */

package de.bsvrz.dua.fehlertls.fehlertls;

import de.bsvrz.dav.daf.main.ClientDavInterface;
import de.bsvrz.dav.daf.main.ResultData;
import de.bsvrz.dav.daf.main.config.SystemObject;
import de.bsvrz.dua.fehlertls.parameter.IParameterTlsFehlerAnalyseListener;
import de.bsvrz.dua.fehlertls.parameter.ParameterTlsFehlerAnalyse;
import de.bsvrz.sys.funclib.bitctrl.dua.ObjektWecker;
import de.bsvrz.sys.funclib.debug.Debug;

/**
 * TODO
 *  
 * @author BitCtrl Systems GmbH, Thierfelder
 *
 */
public class ErwarteteDeWerte
implements IParameterTlsFehlerAnalyseListener{
	
	/**
	 * Debug-Logger
	 */
	private static final Debug LOGGER = Debug.getLogger();
	
	/**
	 * statische Instanz dieser Klasse
	 */
	private static ErwarteteDeWerte INSTANZ = null;

	/**
	 * Alarmiert die DE, wenn der erwartete Datenzeitpunkt ueberschritten wurde
	 */
	private ObjektWecker wecker = new ObjektWecker();
	
	/**
	 * Der zus�tzliche Zeitverzug, der nach dem erwarteten Empfangszeitpunkt noch bis zur
	 * Erkennung eines nicht gelieferten Messwertes abgewartet werden muss
	 */
	private long zeitVerzugFehlerErkennung = -1;


	/**
	 * Erfragt die statische Instanz dieser Klasse 
	 * 
	 * @return statische Instanz dieser Klasse
	 */
	public static final ErwarteteDeWerte getInstanz(){
		if(INSTANZ == null){
			throw new RuntimeException("ErwarteteDeWerte wurde noch nicht instanziiert"); //$NON-NLS-1$
		}
		return INSTANZ;
	}
	
	
	/**
	 * Initialisiert die statische Instanz dieser Klasse
	 * 
	 * @param dav Datenverteiler-Verbindung
	 * @param tlsFehlerAnalyseObjekt das Systemobjekt vom Typ <code>typ.tlsFehlerAnalyse</code>,
	 * mit dem diese Applikation assoziiert ist (aus der sie ihre Parameter bezieht)
	 */
	public static final void initialisiere(ClientDavInterface dav,
										   SystemObject tlsFehlerAnalyseObjekt){
		if(INSTANZ == null){
			INSTANZ = new ErwarteteDeWerte(dav, tlsFehlerAnalyseObjekt);
		}else{
			LOGGER.warning("ErwarteteDeWerte wurde bereits instanziiert"); //$NON-NLS-1$
		}		 
	}
	
	
	/**
	 * Standardkonstruktor
	 * 
	 * @param dav Datenverteiler-Verbindung
	 * @param tlsFehlerAnalyseObjekt das Systemobjekt vom Typ <code>typ.tlsFehlerAnalyse</code>,
	 * mit dem diese Applikation assoziiert ist (aus der sie ihre Parameter bezieht)
	 */
	private ErwarteteDeWerte(ClientDavInterface dav,
							SystemObject tlsFehlerAnalyseObjekt){
		ParameterTlsFehlerAnalyse.getInstanz(dav, tlsFehlerAnalyseObjekt).addListener(this);	
	}

	
	/**
	 * 
	 * @param resultat ein Resultat-Datensatz eines ueberwachten DE
	 * @param intervallDauer
	 */
	public final void aktualisiere(ResultData resultat){
		if(this.zeitVerzugFehlerErkennung >= 0){
			
			
			
		}else{
			LOGGER.fine("Es wurden noch keine DeFa-Parameter empfangen"); //$NON-NLS-1$
		}
	}
	

	/**
	 * {@inheritDoc}
	 */
	public void aktualisiereParameterTlsFehlerAnalyse(
			long zeitverzugFehlerErkennung, long zeitverzugFehlerErmittlung) {
		this.zeitVerzugFehlerErkennung = zeitverzugFehlerErkennung;
	}	
	
}