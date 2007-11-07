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

import java.util.HashSet;
import java.util.Set;

import de.bsvrz.dav.daf.main.ClientDavInterface;
import de.bsvrz.dav.daf.main.config.SystemObject;
import de.bsvrz.sys.funclib.application.StandardApplication;
import de.bsvrz.sys.funclib.application.StandardApplicationRunner;
import de.bsvrz.sys.funclib.commandLineArgs.ArgumentList;
import de.bsvrz.sys.funclib.debug.Debug;

/**
 * Diese SWE dient zur Ermittlung der Fehlerursache bei fehlenden Messwerten an DE
 * (Datenendger�t gem�� TLS). Im Rahmen der Erfassung von Daten �ber eine externe 
 * TLS-Schnittstelle kann aus einer Reihe von Gr�nden ein erwarteter Messwert eines 
 * DE z. T. nicht ermittelt werden. Der fehlende Messwert muss dabei nicht zwangsl�ufig 
 * durch den Detektor verursacht werden. Fehlende Messwerte sind h�ufig auch durch 
 * Kommunikationsst�rungen in der langen Kommunikationskette zwischen 
 * Detektor � EAK � SM � KRI � UZ und VRZ bedingt. Diese SWE versucht die St�rung 
 * innerhalb dieser Kommunikationskette zu lokalisieren und �ber Betriebsmeldungen 
 * bzw. Fehlerstatusausgaben pro DE verf�gbar zu machen
 *  
 * @author BitCtrl Systems GmbH, Thierfelder
 *
 */
public class DeFaApplikation 
implements StandardApplication{
	
	/**
	 * Debug-Logger
	 */
	private static final Debug LOGGER = Debug.getLogger();
	
	/**
	 * Geraete, die in der Kommandozeile uebergeben wurden
	 */
	private Set<SystemObject> geraete = new HashSet<SystemObject>();
	
	/**
	 * die PIDs der Geraete, die in der Kommandozeile uebergeben wurden
	 */
	private String[] geraetePids = null;
	

	/**
	 * {@inheritDoc}
	 */
	public void initialize(ClientDavInterface dav) throws Exception {
		for(String pidVonGeraet:this.geraetePids){
			SystemObject geraeteObjekt = dav.getDataModel().getObject(pidVonGeraet);
			if(geraeteObjekt != null){
				this.geraete.add(geraeteObjekt);
			}else{
				LOGGER.warning("Das uebergebene Geraet " + pidVonGeraet + " existiert nicht"); //$NON-NLS-1$ //$NON-NLS-2$
			}
		}
		
		if(this.geraete.isEmpty()){
			LOGGER.warning("Es wurden keine gueltigen Geraete uebergeben"); //$NON-NLS-1$
		}else{
			
		}
	}

	
	/**
	 * {@inheritDoc}
	 */
	public void parseArguments(ArgumentList argumente)
	throws Exception {
		Debug.init("DE Fehleranalyse fehlende Messdaten", argumente); //$NON-NLS-1$

		this.geraetePids = argumente.fetchArgument("-geraet").asString().split(","); //$NON-NLS-1$ //$NON-NLS-2$

		argumente.fetchUnusedArguments();
	}

	
	/**
	 * Startet diese Applikation
	 * 
	 * @param argumente Argumente der Kommandozeile
	 */
	public static void main(String argumente[]){
        Thread.setDefaultUncaughtExceptionHandler(new Thread.
        				UncaughtExceptionHandler(){
            public void uncaughtException(@SuppressWarnings("unused")
			Thread t, Throwable e) {
                LOGGER.error("Applikation wird wegen" +  //$NON-NLS-1$
                		" unerwartetem Fehler beendet", e);  //$NON-NLS-1$
            	e.printStackTrace();
                Runtime.getRuntime().exit(0);
            }
        });
		StandardApplicationRunner.run(new DeFaApplikation(), argumente);
	}

}
