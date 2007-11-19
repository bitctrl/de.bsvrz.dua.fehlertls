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
import java.util.Date;
import java.util.GregorianCalendar;

import org.junit.Test;

import junit.framework.Assert;
import de.bsvrz.dav.daf.main.ClientDavInterface;
import de.bsvrz.dav.daf.main.config.SystemObject;
import de.bsvrz.dua.fehlertls.AtgTlsFehlerAnalyse;
import de.bsvrz.dua.fehlertls.DAVTest;
import de.bsvrz.dua.fehlertls.DeStatus;
import de.bsvrz.dua.fehlertls.IAtgTlsFehlerAnalyseListener;
import de.bsvrz.dua.fehlertls.TestKEx;
import de.bsvrz.dua.fehlertls.enums.TlsFehlerAnalyse;
import de.bsvrz.sys.funclib.bitctrl.app.Pause;
import de.bsvrz.sys.funclib.bitctrl.dua.DUAKonstanten;
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
	 * Zeigt an, ob der Test an ist, oder ob nur die Ausgaben mitgeloggt werden sollen
	 */
	public static final boolean ASSERTION_AN = false;
	

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

		AtgTlsFehlerAnalyse.getInstanz(TestKEx.IB2_SM1_LVE1_DE1).addListener(new IAtgTlsFehlerAnalyseListener(){
			private TlsFehlerAnalyse[] fehler = new TlsFehlerAnalyse[]{
					TlsFehlerAnalyse.SM_MODEM_ODER_SM_DEFEKT,
					TlsFehlerAnalyse.SM_MODEM_ODER_SM_DEFEKT,
					TlsFehlerAnalyse.SM_MODEM_ODER_SM_DEFEKT
			};			
			private int i = 0;
			
			public void aktualisiereTlsFehlerAnalyse(
					TlsFehlerAnalyse fehlerAnalyse) {
				if(ASSERTION_AN)Assert.assertEquals(fehler[i++], fehlerAnalyse);
			}
		});

		AtgTlsFehlerAnalyse.getInstanz(TestKEx.IB2_SM2_LVE1_DE1).addListener(new IAtgTlsFehlerAnalyseListener(){
			private TlsFehlerAnalyse[] fehler = new TlsFehlerAnalyse[]{
					TlsFehlerAnalyse.EAK_AN_SM_DEFEKT,
					TlsFehlerAnalyse.KRI_DEFEKT
			};			
			private int i = 0;
			
			public void aktualisiereTlsFehlerAnalyse(
					TlsFehlerAnalyse fehlerAnalyse) {
				if(ASSERTION_AN)Assert.assertEquals(fehler[i++], fehlerAnalyse);
			}
		});
		
		AtgTlsFehlerAnalyse.getInstanz(TestKEx.IB2_SM2_LVE1_DE2).addListener(new IAtgTlsFehlerAnalyseListener(){
			private TlsFehlerAnalyse[] fehler = new TlsFehlerAnalyse[]{
					TlsFehlerAnalyse.KRI_DEFEKT,
			};			
			private int i = 0;
			
			public void aktualisiereTlsFehlerAnalyse(
					TlsFehlerAnalyse fehlerAnalyse) {
				if(ASSERTION_AN)Assert.assertEquals(fehler[i++], fehlerAnalyse);
			}
		});


		for(SystemObject de:kex.getAlleLveDes()){
			if(de.equals(TestKEx.IB2_SM2_LVE1_DE1) || de.equals(TestKEx.IB2_SM2_LVE1_DE2) ||
					de.equals(TestKEx.IB2_SM1_LVE1_DE1)){
				kex.setDeFehlerStatus(de, 0, false);
			}else{
				kex.setDeFehlerStatus(de, 0, true);
			}
			kex.setBetriebsParameter(de, 15L * 1000L);			
			
			final SystemObject obj = de;
			AtgTlsFehlerAnalyse.getInstanz(de).addListener(new IAtgTlsFehlerAnalyseListener(){

				public void aktualisiereTlsFehlerAnalyse(
						TlsFehlerAnalyse fehlerAnalyse) {
					System.out.println("+++ " + DUAKonstanten.ZEIT_FORMAT_GENAU.format(new Date()) + ":\n" + //$NON-NLS-1$ //$NON-NLS-2$
							obj + ", " + fehlerAnalyse + " +++"); //$NON-NLS-1$ //$NON-NLS-2$
				}
				
			});
		}
		for(SystemObject de:kex.getAlleUFDDes()){
			kex.setBetriebsParameter(de, -1);
			kex.setDeFehlerStatus(de, 0, false);
		}
		Pause.warte(1000L);
		
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTimeInMillis(System.currentTimeMillis());
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		long datenZeitStempel = cal.getTimeInMillis(); 
		long theoretischerEmpfangsZeitStempel = cal.getTimeInMillis() + 15L * 1000L;
		while(theoretischerEmpfangsZeitStempel <= System.currentTimeMillis()){
			datenZeitStempel += 15L * 1000L; 
			theoretischerEmpfangsZeitStempel += 15L * 1000L;			
		}
		
		warteBis(theoretischerEmpfangsZeitStempel);
		kex.setDe(TestKEx.IB2_SM2_LVE1_DE1, theoretischerEmpfangsZeitStempel, DeStatus.NUTZ_DATEN);
		kex.setDe(TestKEx.IB2_SM2_LVE1_DE2, theoretischerEmpfangsZeitStempel, DeStatus.NUTZ_DATEN);
		
		datenZeitStempel += 15L * 1000L; 
		theoretischerEmpfangsZeitStempel += 15L * 1000L;
		warteBis(theoretischerEmpfangsZeitStempel);
		kex.setDe(TestKEx.IB2_SM2_LVE1_DE2, theoretischerEmpfangsZeitStempel, DeStatus.NUTZ_DATEN);
//
//		datenZeitStempel += 15L * 1000L; 
//		theoretischerEmpfangsZeitStempel += 15L * 1000L;
//		warteBis(theoretischerEmpfangsZeitStempel);
//		for(SystemObject de:dav.getDataModel().getType("typ.deLve").getElements()){ //$NON-NLS-1$
//			kex.sendeDatum(de, datenZeitStempel);
//		}
//
//		datenZeitStempel += 15L * 1000L; 
//		theoretischerEmpfangsZeitStempel += 15L * 1000L;
//		warteBis(theoretischerEmpfangsZeitStempel);
//		for(SystemObject de:dav.getDataModel().getType("typ.deLve").getElements()){ //$NON-NLS-1$
//			kex.sendeDatum(de, datenZeitStempel);
//		}
		
		warteBis(System.currentTimeMillis() + 1000L * 1000L);

	}

	
	/**
	 * globaler Meldungszaehler
	 */
	private static int MELDUNGS_NR = 0;
	
	/**
	 * Die Meldungen die der Reihe nach erwartet werden
	 */
	private static final String[] MELDUNGEN = new String[]{
		"kri1.ib2.sm1.eaklve1.de1 (kri1.ib2.sm1.eaklve1.de1), Modem-Steuermodul oder Steuermodul defekt" //$NON-NLS-1$
	};
	
	
	/**
	 * {@inheritDoc}
	 */
	public void aktualisiereBetriebsMeldungen(SystemObject obj, long zeit, String text){
		if(ASSERTION_AN){
			Assert.assertEquals(MELDUNGEN[MELDUNGS_NR], text);
			MELDUNGS_NR++;
		}
		
		if(text.length() > 70){
			System.out.println("*** " + DUAKonstanten.ZEIT_FORMAT_GENAU.format(new Date()) + ":\n" + obj + "\n" + //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					text.substring(0, 60) + "\n      " + text.substring(60, text.length()) + " ***"); //$NON-NLS-1$ //$NON-NLS-2$
		}else{
			System.out.println("*** " + DUAKonstanten.ZEIT_FORMAT_GENAU.format(new Date()) + ":\n" + obj + "\n" + //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					 text + " ***"); //$NON-NLS-1$
		}
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
