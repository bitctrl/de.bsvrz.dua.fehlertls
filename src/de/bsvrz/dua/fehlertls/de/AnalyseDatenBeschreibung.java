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

package de.bsvrz.dua.fehlertls.de;

import de.bsvrz.dav.daf.main.ClientDavInterface;
import de.bsvrz.dav.daf.main.DataDescription;
import de.bsvrz.dav.daf.main.config.Aspect;
import de.bsvrz.dav.daf.main.config.AttributeGroup;

/**
 * Klasse zur Beschreibung einer Datenidentifikation deren zyklische Bereitstellung
 * ueber TLS ueberprueft werden soll
 * 
 * @author BitCtrl Systems GmbH, Thierfelder
 *
 */
public class AnalyseDatenBeschreibung {

	/**
	 * PID einer Attributgruppe, deren zyklische Bereitstellung
	 * ueberprueft werden soll
	 */
	private String atgPid = null;
	
	/**
	 * PID des Aspektes, unter dem die Daten fuer die obige Attributgruppe
	 * erwarttet werden
	 */
	private String aspPid = null;
	
	/**
	 * Simulationsvariante unter dem die Daten fuer die obige Attributgruppe
	 * erwarttet werden
	 */
	private short sim = 0;
	
	/**
	 * Schnittstelle zu den Informationen ueber die Betriebsparameter,
	 * die sich auf die hier beschriebenen Daten beziehen
	 */
	private IBetriebsParameterInfo parameterInfo = null;
	
	
	/**
	 * Standardkonstruktor
	 * 
	 * @param atgPid PID einer Attributgruppe, deren zyklische Bereitstellung
	 * ueberprueft werden soll
	 * @param aspPid PID des Aspektes, unter dem die Daten fuer die obige Attributgruppe
	 * erwarttet werden
	 * @param sim Simulationsvariante unter dem die Daten fuer die obige Attributgruppe
	 * erwarttet werden
	 * @param parameterInfo Schnittstelle zu den Informationen ueber die Betriebsparameter,
	 * die sich auf die hier beschriebenen Daten beziehen
	 */
	public AnalyseDatenBeschreibung(final String atgPid,
									final String aspPid,
									final short sim,
									final IBetriebsParameterInfo parameterInfo){
		if(atgPid == null){
			throw new NullPointerException("Attributgruppe ist <<null>>"); //$NON-NLS-1$
		}
		if(aspPid == null){
			throw new NullPointerException("Aspekt ist <<null>>"); //$NON-NLS-1$
		}		
		if(parameterInfo == null){
			throw new NullPointerException("Betriebsparameter-Information ist <<null>>"); //$NON-NLS-1$
		}
		this.atgPid = atgPid;
		this.aspPid = aspPid;
		this.sim = sim;
		this.parameterInfo = parameterInfo;
	}

	
	/**
	 * Erfragt eine Schnittstelle zu den Informationen ueber die Betriebsparameter,
	 * die sich auf die hier beschriebenen Daten beziehen
	 * 
	 * @return Schnittstelle zu den Informationen ueber die Betriebsparameter,
	 * die sich auf die hier beschriebenen Daten beziehen
	 */
	public final IBetriebsParameterInfo getParameterInfo(){
		return this.parameterInfo;
	}
	
	
	/**
	 * Erfragt die Datenbeschreibung der Daten deren zyklische Bereitstellung
	 * ueber TLS ueberprueft werden soll
	 * 
	 * @param dav Verbindung zum Datenverteiler
	 * @return die Datenbeschreibung der Daten deren zyklische Bereitstellung
	 * ueber TLS ueberprueft werden soll
	 * @throws DeFaException wenn die hier definierte Attributgruppe oder der 
	 * hier definierte Aspekt nicht im Datenkatalog identifiziert werden konnten
	 */
	public final DataDescription getDatenBeschreibung(final ClientDavInterface dav)
	throws DeFaException{
		AttributeGroup atg = dav.getDataModel().getAttributeGroup(this.atgPid);
		Aspect asp = dav.getDataModel().getAspect(this.aspPid);
		if(atg == null){
			throw new DeFaException("Attributgruppe " + this.atgPid +  //$NON-NLS-1$
					" konnte nicht im Datenkatalog identifiziert werden"); //$NON-NLS-1$
		}
		if(asp == null){
			throw new DeFaException("Aspekt " + this.aspPid + //$NON-NLS-1$
					" konnte nicht im Datenkatalog identifiziert werden"); //$NON-NLS-1$
		}
		
		return new DataDescription(atg, asp, this.sim);
	}
}
