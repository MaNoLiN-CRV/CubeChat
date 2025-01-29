package crv.manolin.debug;



public class DebugCenter {
    static boolean debug = true;
    public static void log(String message) {
        if (debug) System.out.println("[DEBUG] " + message);
    }
    public static void error(String message) {
        if (debug) System.err.println("[ERROR] " + message);
    }
}
