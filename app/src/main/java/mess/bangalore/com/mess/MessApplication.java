package mess.bangalore.com.mess;

import android.app.Application;

import com.amitshekhar.DebugDB;

public class MessApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        DebugDB.getAddressLog();
    }
}
