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

package de.bsvrz.dua.fehlertls;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Utensilien fuer die SWE DE Fehleranalyse fehlende Messdaten
 *  
 * @author BitCtrl Systems GmbH, Thierfelder
 *
 */
public class Util {

	/**
	 * Erfragt den ersten Zeitstempel, der sich echt nach dem Zeitstempel <code>jetzt</code>
	 * (angenommenr Jetzt-Zeitpunkt) befindet und der zur uebergebenen Erfassungsintervalllange
	 * passt 
	 * 
	 * @param jetzt angenommenr Jetzt-Zeitpunkt (int ms)
	 * @param intervallLaenge eine Erfassungsintervalllaenge (int ms)
	 * @return der ersten Zeitstempel, der sich echt nach dem Zeitstempel <code>jetzt</code>
	 * befindet und der zur uebergebenen Erfassungsintervalllange passt (in ms)
	 */
	public static final long getNaechstenIntervallZeitstempel(final long jetzt,
															  final long intervallLaenge){
		GregorianCalendar stundenAnfang = new GregorianCalendar();
		stundenAnfang.setTimeInMillis(jetzt);
		stundenAnfang.set(Calendar.MINUTE, 0);
		stundenAnfang.set(Calendar.SECOND, 0);
		stundenAnfang.set(Calendar.MILLISECOND, 0);
		final long msNachStundenAnfang = jetzt - stundenAnfang.getTimeInMillis();
		final long intervalleSeitStundenAnfang = msNachStundenAnfang / intervallLaenge;
				
		return stundenAnfang.getTimeInMillis() + (intervalleSeitStundenAnfang + 1) * intervallLaenge;
	}
	
}
