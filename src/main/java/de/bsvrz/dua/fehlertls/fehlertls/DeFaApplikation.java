/*
 * Segment 4 Datenübernahme und Aufbereitung (DUA), SWE 4.DeFa DE Fehleranalyse fehlende Messdaten
 * Copyright (C) 2007-2015 BitCtrl Systems GmbH
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
import de.bsvrz.sys.funclib.operatingMessage.MessageSender;

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
 */
public class DeFaApplikation implements StandardApplication {

	private static final Debug LOGGER = Debug.getLogger();

	/** Verbindung zum Datenverteiler. */
	private ClientDavInterface dav;

	/**
	 * das Systemobjekt vom Typ <code>typ.tlsFehlerAnalyse</code>, mit dem diese
	 * Applikation assoziiert ist (aus der sie ihre Parameter bezieht).
	 */
	private static SystemObject tlsFehlerAnalyseObjekte;

	private static boolean ignoriereSammelkanaele;

	/**
	 * Geraete, die in der Kommandozeile uebergeben wurden.
	 */
	private final Set<SystemObject> geraete = new HashSet<>();

	/**
	 * die PIDs der Geraete, die in der Kommandozeile uebergeben wurden.
	 */
	private String[] geraetePids;

	/**
	 * die Pid des Objektes vom Typ <code>typ.tlsFehlerAnalyse</code> mit dem
	 * diese Applikation assoziiert ist (aus der sie ihre Parameter bezieht).
	 */
	private String parameterModulPid;

	/**
	 * Erfragt das Systemobjekt vom Typ <code>typ.tlsFehlerAnalyse</code>, mit
	 * dem diese Applikation assoziiert ist (aus der sie ihre Parameter
	 * bezieht).
	 *
	 * @return das Systemobjekt vom Typ <code>typ.tlsFehlerAnalyse</code>, mit
	 *         dem diese Applikation assoziiert ist (aus der sie ihre Parameter
	 *         bezieht)
	 */
	public static final SystemObject getTlsFehlerAnalyseObjekt() {
		return DeFaApplikation.tlsFehlerAnalyseObjekte;
	}

	/**
	 * Erfragt den Namen dieser Applikation.
	 *
	 * @return der Name dieser Applikation
	 */
	public static final String getAppName() {
		return "DeFa";
	}

	@Override
	public void initialize(final ClientDavInterface connection)
			throws Exception {
		this.dav = connection;

		MessageSender.getInstance().setApplicationLabel(
				"Ueberpruefung fehlende Messdaten TLS-LVE");

		for (final String pidVonGeraet : this.geraetePids) {
			final SystemObject geraeteObjekt = connection.getDataModel()
					.getObject(pidVonGeraet);
			if (geraeteObjekt != null) {
				if (geraeteObjekt.isOfType("typ.gerät")) {
					this.geraete.add(geraeteObjekt);
				} else {
					DeFaApplikation.LOGGER.warning("Das uebergebene Objekt "
							+ pidVonGeraet + " ist nicht vom Typ Geraet");
				}
			} else {
				DeFaApplikation.LOGGER.warning("Das uebergebene Geraet "
						+ pidVonGeraet + " existiert nicht");
			}
		}

		if (this.parameterModulPid == null) {
			for (final SystemObject obj : connection.getDataModel()
					.getType("typ.tlsFehlerAnalyse").getElements()) {
				if (obj.isValid()) {
					if (DeFaApplikation.tlsFehlerAnalyseObjekte != null) {
						DeFaApplikation.LOGGER
						.warning("Es existieren mehrere Objekte vom Typ \"typ.tlsFehlerAnalyse\"");
						break;
					}
					DeFaApplikation.tlsFehlerAnalyseObjekte = obj;
					if (obj.getConfigurationArea().equals(
							connection.getDataModel()
							.getConfigurationAuthority()
							.getConfigurationArea())) {
						break;
					}
				}
			}
		} else {
			final SystemObject dummy = connection.getDataModel().getObject(
					this.parameterModulPid);
			if ((dummy != null) && dummy.isValid()) {
				DeFaApplikation.tlsFehlerAnalyseObjekte = dummy;
			}
		}

		if (DeFaApplikation.tlsFehlerAnalyseObjekte == null) {
			throw new RuntimeException(
					"Es existiert kein Objekt vom Typ \"typ.tlsFehlerAnalyse\"");
		}
		ParameterTlsFehlerAnalyse.getInstanz(connection,
				DeFaApplikation.tlsFehlerAnalyseObjekte);
		DeFaApplikation.LOGGER.config("Es werden die Parameter von "
				+ DeFaApplikation.tlsFehlerAnalyseObjekte + " verwendet");

		if (this.geraete.isEmpty()) {
			DeFaApplikation.LOGGER
			.warning("Es wurden keine gueltigen Geraete uebergeben");
		} else {
			TlsHierarchie.initialisiere(connection, geraete);
		}
	}

	@Override
	public void parseArguments(final ArgumentList argumente) throws Exception {

		Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
			@Override
			public void uncaughtException(final Thread t, final Throwable e) {
				DeFaApplikation.LOGGER.error("Applikation wird wegen"
						+ " unerwartetem Fehler beendet", e);
				e.printStackTrace();
				Runtime.getRuntime().exit(-1);
			}
		});

		if (argumente.hasArgument("-param")) {
			this.parameterModulPid = argumente.fetchArgument("-param")
					.asNonEmptyString();
		} else {
			DeFaApplikation.LOGGER
			.warning("Kein Objekt vom Typ \"typ.tlsFehlerAnalyse\" zur Parametrierung dieser Instanz uebergeben (-param=...)");
		}

		this.geraetePids = argumente.fetchArgument("-geraet")
				.asNonEmptyString().split(",");

		DeFaApplikation.ignoriereSammelkanaele = argumente.fetchArgument(
				"-ignoriereSammelkanaele=nein").booleanValue();

		argumente.fetchUnusedArguments();
	}

	/**
	 * Erfragt die statische Verbindung zum Datenverteiler.
	 *
	 * @return die statische Verbindung zum Datenverteiler.
	 */
	public final ClientDavInterface getDav() {
		return dav;
	}

	/**
	 * Startet diese Applikation.
	 *
	 * @param argumente
	 *            Argumente der Kommandozeile
	 */
	public static void main(final String[] argumente) {
		StandardApplicationRunner.run(new DeFaApplikation(), argumente);
	}

	/**
	 * Gibt die Information zur&uuml;ck, ob Sammelkan&auml;le ignoriert werden
	 * sollen.
	 *
	 * @return <code>true</code> oder <code>false</code>
	 */
	public static boolean isIgnoriereSammelkanaele() {
		return DeFaApplikation.ignoriereSammelkanaele;
	}

}
