package it.uniroma2.dicii.amod.onerjscheduling.utils;

import org.ini4j.Ini;
import org.ini4j.IniPreferences;

import java.io.File;
import java.io.IOException;
import java.util.prefs.Preferences;

/**
 * A parser and container for the external configuration parameters obtainable from <code>config.ini</code> file.
 */
public class ExternalConfig {
    private static ExternalConfig inst = null;
    private final String amplPath;
    private final Integer computationTimeout;
    private final boolean showElapsedTimeOnTimeout;

    private ExternalConfig() {
        Ini ini = null;
        try {
            ini = new Ini(new File("config.ini"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Preferences prefs = new IniPreferences(ini);
        this.amplPath = prefs.node("AMPL-specific").get("AMPL_PATH", "");
        this.computationTimeout = prefs.node("miscellaneous").getInt("COMPUTATION_TIMEOUT", 60000);
        this.showElapsedTimeOnTimeout = prefs.node("miscellaneous").getBoolean("SHOW_ELAPSED_TIME_ON_TIMEOUT", false);
    }

    public static ExternalConfig getSingletonInstance() {
        if (inst == null) {
            inst = new ExternalConfig();
        }
        return inst;
    }

    public String getAmplPath() {
        return amplPath;
    }

    public Integer getComputationTimeout() {
        return computationTimeout;
    }
}
