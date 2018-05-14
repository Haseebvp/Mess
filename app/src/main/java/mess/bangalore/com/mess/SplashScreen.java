package mess.bangalore.com.mess;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import mess.bangalore.com.mess.Utilities.AppConstants;
import mess.bangalore.com.mess.Utilities.SessionHandler;
import mess.bangalore.com.mess.models.Active;
import mess.bangalore.com.mess.models.User;

public class SplashScreen extends AppCompatActivity {

    private LinearLayout lyt_progress;
    private Snackbar snackbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        initialise();
    }

    private void initialise() {
        lyt_progress = findViewById(R.id.lyt_progress);
        LinearLayout lv_main = findViewById(R.id.lv_main);
        snackbar = Snackbar
                .make(lv_main, "Check your internet connection", Snackbar.LENGTH_LONG)
                .setAction("TRY AGAIN", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        retry();
                    }
                });
        firebaseCallback();
    }

    private void firebaseCallback() {
        DatabaseReference databaseReferenceUser = FirebaseDatabase.getInstance().getReference(AppConstants.USER_TABLE);
        databaseReferenceUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                    processData(dataSnapshot);
                } else {
                    goToLogin(false);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                lyt_progress.setVisibility(View.GONE);
                snackbar.show();
            }
        });

    }

    private void retry() {
        lyt_progress.setVisibility(View.VISIBLE);
        firebaseCallback();
    }

    private void processData(DataSnapshot dataSnapshot) {
        List<User> userList = new ArrayList<>();
        boolean login = false;
        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
            User user = postSnapshot.getValue(User.class);
            userList.add(user);
            if (user != null && user.getUsername().equalsIgnoreCase(SessionHandler.getInstance(this).getUserName()) && user.getId().equalsIgnoreCase(SessionHandler.getInstance(this).getUserId())) {
                login = true;
            }
        }
        SessionHandler.getInstance(this).setUserList(userList);
        lyt_progress.setVisibility(View.GONE);
        goToLogin(login);
    }

    private void goToLogin(Boolean param) {
        if (!param) {
            Intent login = new Intent(this, LoginActivity.class);
            startActivity(login);
            finish();
        }else {
            Intent login = new Intent(this, MainScreen.class);
            startActivity(login);
            finish();
        }
    }
}
