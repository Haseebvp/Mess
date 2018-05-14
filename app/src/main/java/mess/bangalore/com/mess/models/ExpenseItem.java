package mess.bangalore.com.mess.models;

public class ExpenseItem {
    String id, name, time, tag;

    Double amount;
    String userId;
    String username;

    public ExpenseItem() {
    }

    public ExpenseItem(String id, String name, String time, Double amount, String tag, String userId, String username) {
        this.id = id;
        this.name = name;
        this.time = time;
        this.amount = amount;
        this.tag = tag;
        this.userId = userId;
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public String getUserId() {
        return userId;
    }

    public String getTag() {
        return tag;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getTime() {
        return time;
    }

    public Double getAmount() {
        return amount;
    }
}
