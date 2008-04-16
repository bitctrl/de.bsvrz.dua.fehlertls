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

import java.util.HashSet;
import java.util.Set;

import de.bsvrz.dav.daf.main.ClientDavInterface;
import de.bsvrz.dav.daf.main.config.SystemObject;
import de.bsvrz.dua.fehlertls.parameter.ParameterTlsFehlerAnalyse;
import de.bsvrz.dua.fehlertls.tls.TlsHierarchie;
import de.bsvrz.sys.funclib.application.StandardApplication;
import de.bsvrz.sys.funclib.application.StandardApplicationRunner;
import de.bsvrz.sys.funclib.commandLineArgs.ArgumentList;
import de.bsvrz.sys.funclib.debug.Debug;

/**
 * Diese SWE dient zur Ermittlung der Fehlerursache bei fehlenden Messwerten an
 * DE (Datenendgerät gemäß TLS). Im Rahmen der Erfassung von Daten über eine
 * externe TLS-Schnittstelle kann aus einer Reihe von Gründen ein erwarteter
 * Messwert eines DE z. T. nicht ermittelt werden. Der fehlende Messwert muss
 * dabei nicht zwangsläufig durch den Detektor verursacht werden. Fehlende
 * Messwerte sind häufig auch durch Kommunikationsstörungen in der langen
 * Kommunikationskette zwischen Detektor – EAK – SM – KRI – UZ und VRZ bedingt.
 * Diese SWE versucht die Störung innerhalb dieser Kommunikationskette zu
 * lokalisieren und über Betriebsmeldungen bzw. Fehlerstatusausgaben pro DE
 * verfügbar zu machen
 * 
 * @author BitCtrl Systems GmbH, Thierfelder
 * 
 * @version $Id$
 */
public class DeFaApplikation implements StandardApplication {
	
	/**
	 * das Systemobjekt vom Typ <code>typ.tlsFehlerAnalyse</code>, mit dem
	 * diese Applikation assoziiert ist (aus der sie ihre Parameter bezieht).
	 */
	private static SystemObject tlsFehlerAnalyseObjekte = null;

	/**
	 * Geraete, die in der Kommandozeile uebergeben wurden.
	 */
	private Set<SystemObject> geraete = new HashSet<SystemObject>();

	/**
	 * die PIDs der Geraete, die in der Kommandozeile uebergeben wurden.
	 */
	private String[] geraetePids = null;
	
	/**
	 * die Pid des Objektes vom Typ <code>typ.tlsFehlerAnalyse</code>
	 * mit dem diese Applikation assoziiert ist (aus der sie ihre Parameter
	 * bezieht).
	 */
	private String parameterModulPid = null; 


	/**
	 * Erfragt das Systemobjekt vom Typ <code>typ.tlsFehlerAnalyse</code>,
	 * mit dem diese Applikation assoziiert ist (aus der sie ihre Parameter
	 * bezieht).
	 * 
	 * @return das Systemobjekt vom Typ <code>typ.tlsFehlerAnalyse</code>,
	 *         mit dem diese Applikation assoziiert ist (aus der sie ihre
	 *         Parameter bezieht)
	 */
	public static final SystemObject getTlsFehlerAnalyseObjekt() {
		return tlsFehlerAnalyseObjekte;
	}

	/**
	 * Erfragt den Namen dieser Applikation.
	 * 
	 * @return der Name dieser Applikation
	 */
	public static final String getAppName() {
		return "DeFa"; //$NON-NLS-1$
	}

	/**
	 * {@inheritDoc}
	 */
	public void initialize(ClientDavInterface dav) throws Exception {
		for (String pidVonGeraet : this.geraetePids) {
			SystemObject geraeteObjekt = dav.getDataModel().getObject(
					pidVonGeraet);
			if (geraeteObjekt != null) {
				if (geraeteObjekt.isOfType("typ.gerät")) { //$NON-NLS-1$
					this.geraete.add(geraeteObjekt);
				} else {
					Debug
							.getLogger()
							.warning(
									"Das uebergebene Objekt " + pidVonGeraet + " ist nicht vom Typ Geraet"); //$NON-NLS-1$ //$NON-NLS-2$
				}
			} else {
				Debug
						.getLogger()
						.warning(
								"Das uebergebene Geraet " + pidVonGeraet + " existiert nicht"); //$NON-NLS-1$ //$NON-NLS-2$
			}
		}

		if (this.parameterModulPid == null) {
			for (SystemObject obj : dav.getDataModel().getType(
					"typ.tlsFehlerAnalyse").getElements()) { //$NON-NLS-1$
				if (obj.isValid()) {
					if (tlsFehlerAnalyseObjekte != null) {
						Debug
								.getLogger()
								.warning(
										"Es existieren mehrere Objekte vom Typ \"typ.tlsFehlerAnalyse\""); //$NON-NLS-1$
						break;
					}
					tlsFehlerAnalyseObjekte = obj;
					if (obj.getConfigurationArea().equals(
							dav.getDataModel().getConfigurationAuthority()
									.getConfigurationArea())) {
						break;
					}
				}
			}
		} else {
			SystemObject dummy = dav.getDataModel().getObject(
					this.parameterModulPid);
			if (dummy != null && dummy.isValid()) {
				tlsFehlerAnalyseObjekte = dummy;
			}
		}

		if (tlsFehlerAnalyseObjekte == null) {
			throw new RuntimeException(
					"Es existiert kein Objekt vom Typ \"typ.tlsFehlerAnalyse\""); //$NON-NLS-1$
		} else {
			ParameterTlsFehlerAnalyse.getInstanz(dav, tlsFehlerAnalyseObjekte);
			Debug.getLogger().config(
					"Es werden die Parameter von " + tlsFehlerAnalyseObjekte //$NON-NLS-1$
							+ " verwendet"); //$NON-NLS-1$
		}

		if (this.geraete.isEmpty()) {
			Debug.getLogger().warning(
					"Es wurden keine gueltigen Geraete uebergeben"); //$NON-NLS-1$
		} else {
			TlsHierarchie.initialisiere(dav, geraete);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void parseArguments(ArgumentList argumente) throws Exception {

		Thread
				.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
					public void uncaughtException(@SuppressWarnings("unused")
					Thread t, Throwable e) {
						Debug.getLogger().error("Applikation wird wegen" + //$NON-NLS-1$
								" unerwartetem Fehler beendet", e); //$NON-NLS-1$
						e.printStackTrace();
						Runtime.getRuntime().exit(-1);
					}
				});

		Debug.init("DE Fehleranalyse fehlende Messdaten", argumente); //$NON-NLS-1$

		
		if (argumente.hasArgument("-param")) {
			this.parameterModulPid = argumente.fetchArgument("-param")
					.asNonEmptyString();
		} else {
			Debug
					.getLogger()
					.warning(
							"Kein Objekt vom Typ \"typ.tlsFehlerAnalyse\" zur Parametrierung dieser Instanz uebergeben (-param=...)");
		}

		this.geraetePids = argumente
				.fetchArgument("-geraet").asNonEmptyString().split(","); //$NON-NLS-1$ //$NON-NLS-2$

		argumente.fetchUnusedArguments();
	}

	/**
	 * Startet diese Applikation.
	 * 
	 * @param argumente
	 *            Argumente der Kommandozeile
	 */
	public static void main(String[] argumente) {
		StandardApplicationRunner.run(new DeFaApplikation(), argumente);
	}

}
