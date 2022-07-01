package it.uniroma2.dicii.amod.onerjscheduling.utils;

import org.ini4j.Ini;
import org.ini4j.IniPreferences;

import java.io.File;
import java.io.IOException;
import java.util.prefs.Preferences;

public class ExternalConfig {
    private String amplPath;
    private Integer computationTimeout;
    private static ExternalConfig inst = null;

    public static ExternalConfig getSingletonInstance() {
        if (inst == null) {
            inst = new ExternalConfig();
        }
        return inst;
    }
    private ExternalConfig() {
        Ini ini = null;
        try {
            ini = new Ini(new File("config.ini"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Preferences prefs = new IniPreferences(ini);
        amplPath =prefs.node("AMPL-specific").get("AMPL_PATH", "");
        computationTimeout =prefs.node("miscellaneous").getInt("COMPUTATION_TIMEOUT", 60000);
    }

    public String getAmplPath() {
        return amplPath;
    }

    public Integer getComputationTimeout() {
        return computationTimeout;
    }
}
