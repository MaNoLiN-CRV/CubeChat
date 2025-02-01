package crv.manolin.entities;

import java.io.Serializable;

public class User implements Serializable {
    private String id;
    private String username;
    private String passwordHash;

    public User(String username) {
        this.username = username;
    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }
}
