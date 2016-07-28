/*
 * Copyright 2016 by Kappich Systemberatung Aachen
 * 
 * This file is part of de.bsvrz.dua.fehlertls.tests.
 * 
 * de.bsvrz.dua.fehlertls.tests is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * de.bsvrz.dua.fehlertls.tests is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with de.bsvrz.dua.fehlertls.tests.  If not, see <http://www.gnu.org/licenses/>.

 * Contact Information:
 * Kappich Systemberatung
 * Martin-Luther-Straße 14
 * 52062 Aachen, Germany
 * phone: +49 241 4090 436 
 * mail: <info@kappich.de>
 */

package de.bsvrz.dua.fehlertls.tests.fehlertls;

import com.bitctrl.Constants;
import de.bsvrz.dav.daf.main.ClientDavInterface;
import de.bsvrz.dua.fehlertls.tests.DeFaTestBase;
import de.bsvrz.dua.fehlertls.tests.DeStatus;
import de.bsvrz.dua.fehlertls.tests.TypDeTestWrapper;
import de.bsvrz.dua.fehlertls.tests.TypTlsFehlerAnalyse;
import de.bsvrz.sys.funclib.bitctrl.dua.DUAKonstanten;
import de.bsvrz.sys.funclib.kappich.annotations.NotNull;
import org.junit.Test;

import java.util.Date;

/**
 * Testet die Applikation nach PruefSpez (zweiter Teil - SM2).
 * 
 * @author BitCtrl Systems GmbH, Thierfelder
 */
public class TestDeFaApplikation2 extends DeFaTestBase {

	/**
	 * Debug?
	 */
	public static final boolean DEBUG = false;

	/**
	 * Debug2?
	 */
	public static final boolean DEBUG2 = true;

	/**
	 * Zeigt an, ob der Test an ist, oder ob nur die Ausgaben mitgeloggt werden
	 * sollen.
	 */
	public static final boolean ASSERTION_AN = true;

	/**
	 * Zeigt an, ob der der Text der DE-Fehleranalyse an allen DE ausgegeben
	 * werden soll.
	 */
	public static final boolean SHOW_DE = true;

	/**
	 * Zeigt an, ob die Betriebsmeldungen an allen DE ausgegeben werden soll.
	 */
	public static final boolean SHOW_BM = true;

	/**
	 * Zeigt an, ob die Nummer des Zeitpunktes mitgeloggt werden soll.
	 */
	public static final boolean SHOW_ZEITPUNKT = true;

	@Override
	protected String getDevice() {
		return "sm.kri.test.sm.2";
	}

	/**
	 * Testet analog PrüfSpez. (2.Teil).
	 * 
	 * @throws Exception
	 *             wird weitergereicht
	 */
	@Test
	public void test() throws Exception {
		ClientDavInterface dav = _connection;

		TypDeTestWrapper.init(dav);
		TypTlsFehlerAnalyse parameter = TypTlsFehlerAnalyse.getInstanz(dav);

		parameter.setParameter(15L * Constants.MILLIS_PER_SECOND,
				60L * Constants.MILLIS_PER_SECOND);
		for (int i = 1; i < 17; i++) {
			TypDeTestWrapper de = TypDeTestWrapper.getInstanz("DE" + i);

			if (i == 6) {
				de.setBetriebsParameter(-1);
			} else {
				de.setBetriebsParameter(15L * 1000L);
			}
			de.setDe(DeStatus.KANAL_AKTIVIERT_DE_FEHLER_AUS);
		}
		TypDeTestWrapper.getInstanz("DE2").setDe(
				DeStatus.KANAL_AKTIVIERT_DE_FEHLER_AN);
		TypDeTestWrapper.getInstanz("DE4").setDe(
				DeStatus.KANAL_PASSIVIERT_DE_FEHLER_AUS);

		if (DEBUG2) {
			System.out
					.println("\n"
							+ date()
							+ "\nDatengenerator fuer alle ausser DE2, DE4 und DE6 starten und auf Ergebnisse warten\n");
		}
		for (int r = 0; r < 3; r++) {
			long jetzt = System.currentTimeMillis();
			for (int i = 1; i < 17; i++) {
				TypDeTestWrapper de = TypDeTestWrapper.getInstanz("DE" + i);
				if (i != 6 && i != 4 && i != 2) {
					de.sendDeData(true, jetzt);
				}
			}

			try {
				Thread.sleep(15L * Constants.MILLIS_PER_SECOND);
			} catch (InterruptedException ex) {
				//
			}
		}

		/**
		 * Datengenerator für DE1 stoppen und auf Ergebnisse warten
		 */
		if (DEBUG2) {
			System.out
					.println("\n"
							+ date()
							+ "\nDatengenerator fuer DE1 stoppen und auf Ergebnisse warten\n");
		}
		for (int r = 0; r < 6; r++) {
			long jetzt = System.currentTimeMillis();
			for (int i = 1; i < 17; i++) {
				TypDeTestWrapper de = TypDeTestWrapper.getInstanz("DE" + i);
				if (i != 6 && i != 2 && i != 4 && i != 1) {
					de.sendDeData(true, jetzt);
				}
			}

			try {
				Thread.sleep(15L * Constants.MILLIS_PER_SECOND);
			} catch (InterruptedException ex) {
				//
			}
		}

		/**
		 * Datengenerator für DE3 stoppen und 2 Minuten auf Ergebnisse warten
		 */
		if (DEBUG2) {
			System.out
					.println("\n"
							+ date()
							+ "\nDatengenerator fuer DE3 stoppen und auf Ergebnisse warten\n");
		}
		for (int r = 0; r < 6; r++) {
			long jetzt = System.currentTimeMillis();
			for (int i = 1; i < 17; i++) {
				TypDeTestWrapper de = TypDeTestWrapper.getInstanz("DE" + i);
				if (i != 6 && i != 1 && i != 3 && i != 4 && i != 2) {
					de.sendDeData(true, jetzt);
				}
			}

			try {
				Thread.sleep(15L * Constants.MILLIS_PER_SECOND);
			} catch (InterruptedException ex) {
				//
			}
		}

		/**
		 * Datengenerator für DE5, DE7 und DE8 stoppen und 2 Minuten auf
		 * Ergebnisse warten
		 */
		if (DEBUG2) {
			System.out
					.println("\n"
							+ date()
							+ "\nDatengenerator fuer DE5, DE7 und DE8 stoppen und auf Ergebnisse warten\n");
		}
		for (int r = 0; r < 6; r++) {
			long jetzt = System.currentTimeMillis();
			for (int i = 1; i < 17; i++) {
				TypDeTestWrapper de = TypDeTestWrapper.getInstanz("DE" + i);
				if (i != 1 && i != 2 && i != 3 && i != 4 && i != 5 && i != 6
						&& i != 7 && i != 8) {
					de.sendDeData(true, jetzt);
				}
			}

			try {
				Thread.sleep(15L * Constants.MILLIS_PER_SECOND);
			} catch (InterruptedException ex) {
				//
			}
		}

		/**
		 * Datengenerator für DE9,..., DE16 stoppen und 2 Minuten auf Ergebnisse
		 * warten
		 */
		if (DEBUG2) {
			System.out
					.println("\n"
							+ date()
							+ "\nDatengenerator fuer DE9,..., DE16 stoppen und auf Ergebnisse warten\n");
		}
		for (int r = 0; r < 6; r++) {
			try {
				Thread.sleep(15L * Constants.MILLIS_PER_SECOND);
			} catch (InterruptedException ex) {
				//
			}
		}

		/**
		 * Datengenerator alle außer DE2, DE4, DE6, DE13,..., DE16 wieder
		 * anschalten und 2 Minuten auf Ergebnisse warten
		 */
		if (DEBUG2) {
			System.out
					.println("\n"
							+ date()
							+ "\nDatengenerator fuer alle ausser DE2, DE4, DE6, DE13,..., DE16 wieder anschalten und auf Ergebnisse werden\n");
		}
		for (int r = 0; r < 20; r++) {
			long jetzt = System.currentTimeMillis();
			for (int i = 1; i < 17; i++) {
				TypDeTestWrapper de = TypDeTestWrapper.getInstanz("DE" + i);
				if (i != 2 && i != 4 && i != 6 && i != 13 && i != 14 && i != 15
						&& i != 16) {
					de.sendDeData(true, jetzt);
				}
			}

			try {
				Thread.sleep(15L * Constants.MILLIS_PER_SECOND);
			} catch (InterruptedException ex) {
				//
			}
		}

	}
}
