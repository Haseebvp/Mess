package mess.bangalore.com.mess.models;

public class Active {

    String username, id;
    String isActive;

    public Active() {
    }


    public Active(String isActive, String username, String id) {
        this.isActive = isActive;
        this.username = username;
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public String getId() {
        return id;
    }

    public boolean isActive() {
        return isActive != null && isActive.equalsIgnoreCase("true");
    }

}
