package crv.manolin.debug;


import crv.manolin.config.Configuration;

public class DebugCenter {
    static boolean debug = Configuration.getInstance().isDebug();
    public static void log(String message) {
        if (debug) System.out.println("[DEBUG] " + message);
    }
    public static void error(String message) {
        if (debug) System.err.println("[ERROR] " + message);
    }
}
