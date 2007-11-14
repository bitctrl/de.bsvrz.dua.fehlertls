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

package de.bsvrz.dua.fehlertls.fehlertls;

import java.util.Calendar;
import java.util.GregorianCalendar;

import org.junit.Test;

import de.bsvrz.dav.daf.main.ClientDavInterface;
import de.bsvrz.dav.daf.main.config.SystemObject;
import de.bsvrz.dua.fehlertls.DAVTest;
import de.bsvrz.dua.fehlertls.TestKEx;
import de.bsvrz.sys.funclib.bitctrl.app.Pause;
import de.bsvrz.sys.funclib.bitctrl.dua.bm.BmClient;
import de.bsvrz.sys.funclib.bitctrl.dua.bm.IBmListener;

/**
 * Stellt eine Datenverteiler-Verbindung
 * zur Verfügung.
 * 
 * @author BitCtrl Systems GmbH, Thierfelder
 * 
 */
public class DeFaApplikationTest
implements IBmListener{

	/**
	 * Testet alles
	 * 
	 * @throws Exception wird weitergereicht
	 */
	@Test
	public void test()
	throws Exception{
		ClientDavInterface dav = DAVTest.getDav();
		
		BmClient.getInstanz(dav).addListener(this);
		
		TestKEx kex = TestKEx.getInstanz(dav);
		
		kex.setAnalyseParameter(3000L, 3000L);
		
		for(SystemObject de:dav.getDataModel().getType("typ.deLve").getElements()){ //$NON-NLS-1$
			kex.setBetriebsParameter(de, 15L * 1000L);
		}
		for(SystemObject de:dav.getDataModel().getType("typ.deUfd").getElements()){ //$NON-NLS-1$
			kex.setBetriebsParameter(de, -1);
		}
		Pause.warte(1000L);
		
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTimeInMillis(System.currentTimeMillis());
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		long datenZeitStempel = cal.getTimeInMillis(); 
		long theoretischerEmpfangsZeitStempel = cal.getTimeInMillis() + 15L * 1000L;
		
		warteBis(theoretischerEmpfangsZeitStempel);
		for(SystemObject de:dav.getDataModel().getType("typ.deLve").getElements()){ //$NON-NLS-1$
			kex.sendeDatum(de, datenZeitStempel);
		}
		
		datenZeitStempel += 15L * 1000L; 
		theoretischerEmpfangsZeitStempel += 15L * 1000L;
		warteBis(theoretischerEmpfangsZeitStempel);
		for(SystemObject de:dav.getDataModel().getType("typ.deLve").getElements()){ //$NON-NLS-1$
			kex.sendeDatum(de, datenZeitStempel);
		}

		datenZeitStempel += 15L * 1000L; 
		theoretischerEmpfangsZeitStempel += 15L * 1000L;
		warteBis(theoretischerEmpfangsZeitStempel);
		for(SystemObject de:dav.getDataModel().getType("typ.deLve").getElements()){ //$NON-NLS-1$
			kex.sendeDatum(de, datenZeitStempel);
		}

		datenZeitStempel += 15L * 1000L; 
		theoretischerEmpfangsZeitStempel += 15L * 1000L;
		warteBis(theoretischerEmpfangsZeitStempel);
		for(SystemObject de:dav.getDataModel().getType("typ.deLve").getElements()){ //$NON-NLS-1$
			kex.sendeDatum(de, datenZeitStempel);
		}
		
		warteBis(System.currentTimeMillis() + 1000L * 1000L);

	}

	
	/**
	 * {@inheritDoc}
	 */
	public void aktualisiere(long zeit, String text) {
		System.out.println(text);
	}
	
	
	/**
	 * Wartet bis zu einem bestimmten Zeitpunkt
	 * 
	 * @param zeitStempel Zeitpunkt bis zu dem gewartet werden soll
	 */
	public final void warteBis(long zeitStempel){
		while(System.currentTimeMillis() < zeitStempel){
			Pause.warte(50L);
		}
	}
	
}
