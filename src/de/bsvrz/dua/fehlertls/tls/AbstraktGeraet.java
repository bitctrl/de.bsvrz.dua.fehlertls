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

package de.bsvrz.dua.fehlertls.tls;

import java.util.HashSet;
import java.util.Set;

import de.bsvrz.dav.daf.main.ClientDavInterface;
import de.bsvrz.dav.daf.main.config.ConfigurationObject;
import de.bsvrz.dav.daf.main.config.SystemObject;

/**
 * Abstrakte Repraesentation einer Objektes vom Typ <code>typ.gerät</code>
 * 
 * @author BitCtrl Systems GmbH, Thierfelder
 *
 */
public abstract class AbstraktGeraet {
	
	/**
	 * moegliche Geraetearten
	 */
	public enum Art{
		KRI,
		INSELBUS,
		SM,
		EAK,
		DE
	}

	/**
	 * statische Datenverteiler-Verbindund
	 */
	protected static ClientDavInterface DAV = null;
	
	/**
	 * das Konfigurationsobjekt vom Typ <code>typ.gerät</code>
	 */
	protected ConfigurationObject objekt = null;
	
	/**
	 * die in der TLS-Hierarchie unter diesem Geraet liegenden
	 * Geraete
	 */
	protected Set<AbstraktGeraet> kinder = new HashSet<AbstraktGeraet>();
	
	/**
	 * das in der TLS-Hierarchie ueber diesem Geraet liegende
	 * Geraet
	 */
	protected AbstraktGeraet vater = null;
	
	/**
	 * alle DEs, die sich unterhalb von diesem Element befinden
	 */
	private Set<De> des = null;
	
	
	/**
	 * Erfragt die Geraeteart dieses Geraetes
	 * 
	 * @return die Geraeteart dieses Geraetes
	 */
	public abstract Art getGeraeteArt();
	
	
	/**
	 * Standardkonstruktor
	 * 
	 * @param dav Datenverteiler-Verbindund
	 * @param objekt ein Systemobjekt vom Typ <code>typ.gerät</code>
	 * @param vater das in der TLS-Hierarchie ueber diesem Geraet liegende
	 * Geraet 
	 */
	protected AbstraktGeraet(ClientDavInterface dav, SystemObject objekt, AbstraktGeraet vater){
		if(DAV == null){
			DAV = dav;
		}
		this.objekt = (ConfigurationObject)objekt;
		this.vater = vater;
	}
	
	
	/**
	 * Erfragt die in der TLS-Hierarchie unter diesem Geraet liegenden
	 * Geraete
	 * 
	 * @return die in der TLS-Hierarchie unter diesem Geraet liegenden
	 * Geraete (ggf. leere Liste)
	 */
	public final Set<AbstraktGeraet> getKinder(){
		return this.kinder;
	}
	
	
	/**
	 * Efragt das in der TLS-Hierarchie ueber diesem Geraet liegende
	 * Geraet
	 * 
	 * @return das in der TLS-Hierarchie ueber diesem Geraet liegende
	 * Geraet bzw. <code>null</code>, wenn dieses Geraet die Spitze
	 * der Hierarchie sein sollte
	 */
	public final AbstraktGeraet getVater(){
		return this.vater;
	}
	
	
	/**
	 * Erfragt (implizit) ob dieses Geraet einen Vater hat
	 *  
	 * @return ob dieses Geraet einen Vater hat
	 */
	public final boolean isTopElement(){
		return this.vater == null;
	}
	
	
	/**
	 * {@inheritDoc} 
	 */
	@Override
	public boolean equals(Object obj) {
		boolean ergebnis = false;
		
		if(obj != null && obj instanceof AbstraktGeraet){
			AbstraktGeraet that = (AbstraktGeraet)obj;
			ergebnis = this.objekt.getId() == that.objekt.getId();
		}
		
		return ergebnis;
	}

	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return this.objekt.toString();
	}
	
	
	/**
	 * Erfragt die in der untersten TLS-Hierarchie unter diesem Geraet
	 * liegenden Geraete (DEs)
	 * 
	 * @return die in der untersten TLS-Hierarchie unter diesem Geraet
	 * liegenden Geraete (ggf. leere Liste)
	 */
	public final Set<De> getDes(){
		if(this.des == null){
			synchronized (this) {
				this.des = new HashSet<De>();	
				sammleDes(this.des);		
			}			
		}		
		
		return this.des;
	}
	
	
	/**
	 * Sammelt rekursiv alle DE unterhalb dieses Objektes
	 * 
	 * @param des eine Menge mit Des
	 */
	private final void sammleDes(Set<De> des){
		if(this.getGeraeteArt() == Art.DE){
			des.add((De)this);
		}else{
			for(AbstraktGeraet kind:this.kinder){
				kind.sammleDes(des);
			}
		}
	}
}
