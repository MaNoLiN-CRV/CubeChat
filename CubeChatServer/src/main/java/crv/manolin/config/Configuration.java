package crv.manolin.config;

public class Configuration {
    private String databaseUrl = "";
    private String databaseUsername = "";
    private String databasePassword = "";
    private boolean debug = true;
    private int jwtExpiration = calculateJwtExpiration(60);
    private static Configuration configuration;


    public static Configuration getInstance() {
        if (configuration == null) {
            synchronized (Configuration.class) {
                if (configuration == null) {
                    configuration = new Configuration();
                }
            }
        }
        return configuration;
    }

    private int calculateJwtExpiration(int minutes ) {
        return minutes * 60 * 1000;
    }

    public int getJwtExpiration() {
        return jwtExpiration;
    }

    public String getDatabaseUrl() {
        return databaseUrl;
    }

    public void setDatabaseUrl(String databaseUrl) {
        this.databaseUrl = databaseUrl;
    }

    public String getDatabaseUsername() {
        return databaseUsername;
    }

    public void setDatabaseUsername(String databaseUsername) {
        this.databaseUsername = databaseUsername;
    }

    public void setDatabasePassword(String databasePassword) {
        this.databasePassword = databasePassword;
    }

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }
}
