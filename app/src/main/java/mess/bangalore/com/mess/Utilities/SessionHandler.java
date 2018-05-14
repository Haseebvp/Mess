package mess.bangalore.com.mess.Utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.icu.lang.UScript;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import mess.bangalore.com.mess.models.Active;
import mess.bangalore.com.mess.models.ExpenseItem;
import mess.bangalore.com.mess.models.User;

public class SessionHandler {
    private static SessionHandler instance;

    private SharedPreferences pref;
    private Gson gson;
    private SharedPreferences.Editor editor;

    public SessionHandler(Context context) {
        pref = context.getSharedPreferences(AppConstants.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
        editor = pref.edit();
        editor.apply();
    }


    public static SessionHandler getInstance(Context context) {
        if (instance == null) {
            instance = new SessionHandler(context);
        }
        return instance;
    }

    public void setUserName(String name) {
        editor.putString(AppConstants.USER_NAME, name);
        editor.commit();
    }

    public String getUserName() {
        return pref.getString(AppConstants.USER_NAME, "");
    }

    public void setUserId(String id) {
        editor.putString(AppConstants.USER_ID, id);
        editor.commit();
    }

    public String getUserId() {
        return pref.getString(AppConstants.USER_ID, "");
    }


    public void setUserList(List<User> names) {
        String data = gson.toJson(names);
        editor.putString(AppConstants.USER_LIST, data);
        editor.commit();
    }

    public List<User> getUserList() {
        String json = pref.getString(AppConstants.USER_LIST, "");
        Type type = new TypeToken<List<User>>() {
        }.getType();

        return gson.fromJson(json, type);
    }


    public void setExpenseList(List<ExpenseItem> names) {
        String data = gson.toJson(names);
        editor.putString(AppConstants.Expense_LIST, data);
        editor.commit();
    }

    public List<ExpenseItem> getExpenseList() {
        String json = pref.getString(AppConstants.Expense_LIST, "");
        Type type = new TypeToken<List<ExpenseItem>>() {
        }.getType();

        return gson.fromJson(json, type);
    }

    public void setActiveList(List<Active> data) {
        String out = gson.toJson(data);
        editor.putString(AppConstants.ACTIVE_LIST, out);
        editor.commit();
    }

    public List<Active> getActiveList() {
        String json = pref.getString(AppConstants.ACTIVE_LIST, "");
        Type type = new TypeToken<List<Active>>() {
        }.getType();

        return gson.fromJson(json, type);
    }

}
