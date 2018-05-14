package mess.bangalore.com.mess.models;

public class User {

    String id, username, password;
    boolean isActive;

    public User() {
    }

    public User(String id, String username, String password, boolean isActive) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.isActive = isActive;
    }

    public boolean isActive() {
        return isActive;
    }

    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
