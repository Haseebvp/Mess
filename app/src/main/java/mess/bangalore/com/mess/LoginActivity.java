package mess.bangalore.com.mess;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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

public class LoginActivity extends AppCompatActivity {

    TextInputEditText username, password;
    TextView error;
    Button signin;
    LinearLayout lyt_progress, lv_main;
    private DatabaseReference databaseReferenceUser;
    private Boolean isTrying = false;

    private String id, name, pwd;
    private Snackbar snackbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initialise();
    }

    private void initialise() {
        lyt_progress = findViewById(R.id.lyt_progress);
        lv_main = findViewById(R.id.lv_main);
        username = findViewById(R.id.tv_username);
        password = findViewById(R.id.tv_password);
        signin = findViewById(R.id.signin);
        error = findViewById(R.id.tv_error);
        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateLogin();
            }
        });
        databaseReferenceUser = FirebaseDatabase.getInstance().getReference(AppConstants.USER_TABLE);
        databaseReferenceUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (isTrying) {
                    if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                        processData(dataSnapshot);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                showSnackBar("Please check your internet connection");
            }
        });

    }

    private void showSnackBar(String message) {
        snackbar = Snackbar
                .make(lv_main, message, Snackbar.LENGTH_LONG);
        snackbar.show();

    }

    private void validateLogin() {
        resetView();
        name = username.getText().toString().trim();
        pwd = password.getText().toString().trim();

        if (name.length() == 0 && password.getText().toString().trim().length() == 0) {
            error.setVisibility(View.VISIBLE);
        } else if (name.length() < 4) {
            username.setError("Username should be more than 4 letters");
        } else if (password.getText().toString().trim().length() < 4) {
            password.setError("Password should be more than 4 letters");
        } else {
            resetView();
            addUser();
        }

    }

    private void addUser() {
        signin.setVisibility(View.GONE);
        lyt_progress.setVisibility(View.VISIBLE);

        if (!loginProcess(username.getText().toString())) {
            id = databaseReferenceUser.push().getKey();
            User user = new User(id, username.getText().toString(), password.getText().toString(), false);
            databaseReferenceUser.child(id).setValue(user);
            isTrying = true;
        } else {
            loginUser();
        }
    }

    private void loginUser() {
        boolean out = false;
        List<User> data = SessionHandler.getInstance(this).getUserList();
        if (data.size() > 0) {
            for (User user : data) {
                if (user != null && user.getUsername().equalsIgnoreCase(name) && user.getPassword().equalsIgnoreCase(pwd)) {
//                    if (user.isActive()) {
                    out = true;
                    SessionHandler.getInstance(this).setUserId(user.getId());
                    SessionHandler.getInstance(this).setUserName(user.getUsername());
                    gotoMainScreen();
                    break;
//                    } else {
//                        showSnackBar("Please contact admin to activate your account");
//                        signin.setVisibility(View.VISIBLE);
//                        lyt_progress.setVisibility(View.GONE);
//                        break;
//                }
                }
            }
        }
        if (!out) {
            showSnackBar("Your password or username is incorrect");
        }

    }

    private void gotoMainScreen() {
        Intent mainscreen = new Intent(this, MainScreen.class);
        startActivity(mainscreen);
        finish();
    }

    private boolean loginProcess(String s) {
        boolean out = false;
        List<User> data = SessionHandler.getInstance(this).getUserList();
        if (data != null && data.size() > 0) {
            for (User user : data) {
                if (user.getUsername() != null && user.getUsername().equalsIgnoreCase(s)) {
                    out = true;
                    break;
                }
            }
        } else {
            out = false;
        }
        return out;
    }

    private void resetView() {
        error.setVisibility(View.GONE);
        username.setError(null);
        password.setError(null);
    }

    private void processData(DataSnapshot dataSnapshot) {
        List<User> userList = new ArrayList<>();
        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
            User user = postSnapshot.getValue(User.class);
            userList.add(user);
        }
        SessionHandler.getInstance(this).setUserList(userList);
        if (isTrying) {
            SessionHandler.getInstance(this).setUserId(id);
            SessionHandler.getInstance(this).setUserName(name);
            gotoMainScreen();
        }
        isTrying = false;
    }
}
