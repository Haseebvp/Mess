package mess.bangalore.com.mess;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.internal.NavigationMenuView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import mess.bangalore.com.mess.Utilities.AppConstants;
import mess.bangalore.com.mess.Utilities.SessionHandler;
import mess.bangalore.com.mess.adapters.ExpenseAdapter;
import mess.bangalore.com.mess.models.ExpenseItem;
import mess.bangalore.com.mess.models.User;

public class MainScreen extends AppCompatActivity {

    RecyclerView recyclerView;
    LinearLayout lv_progress, lv_error;
    TextView tv_amount, tv_username;
    Toolbar toolbar;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    SessionHandler sessionHandler;
    private DatabaseReference databaseReferenceTransactions;
    List<ExpenseItem> expenseItems = new ArrayList<>();
    ExpenseAdapter adapter;
    ActionBarDrawerToggle toggle;
    FloatingActionButton addExpense;


    LinearLayout membersLayout;
    RelativeLayout lv_generatebill;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try {
            toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle("");
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        initialise();
    }

    private void initialise() {
        drawerLayout = findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        sessionHandler = SessionHandler.getInstance(this);

        navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);

        tv_username = headerView.findViewById(R.id.username);
        tv_username.setText(SessionHandler.getInstance(this).getUserName());
        membersLayout = headerView.findViewById(R.id.membersLayout);
        lv_generatebill = headerView.findViewById(R.id.generateBill);
        addExpense = findViewById(R.id.addExpense);
        recyclerView = findViewById(R.id.recyclerview);
        lv_error = findViewById(R.id.lv_error);
        lv_progress = findViewById(R.id.lv_progress);
        tv_amount = findViewById(R.id.tv_amount);
        setAmount("0");
        setRecyclerView();
        setProgress(true);

        showMembers();
        lv_generatebill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                generateBill();
            }
        });

        fetchData();
        addExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddExpenseFragment();
            }
        });
    }

    private void generateBill() {
        Intent bill = new Intent(this, BillActivity.class);
        startActivity(bill);
    }

    private void showMembers() {
        LayoutInflater vi;
        final List<User> members = SessionHandler.getInstance(this).getUserList();
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, 10, 0, 10);
        membersLayout.removeAllViews();
        members.add(0, new User("", "All", "", true));
        for (int i = 0; i < members.size(); i++) {
            if (members.get(i) != null) {
                vi = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                assert vi != null;
                View v = vi.inflate(R.layout.member_item, null);
                v.setLayoutParams(layoutParams);
                TextView name = v.findViewById(R.id.membername);
                if (members.get(i).getUsername().equalsIgnoreCase(SessionHandler.getInstance(this).getUserName()) &&
                        members.get(i).getId().equalsIgnoreCase(SessionHandler.getInstance(this).getUserId())) {
                    name.setText("You (" + members.get(i).getUsername() + ")");
                } else {
                    name.setText(members.get(i).getUsername());
                }

                final int finalI = i;
                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showIndividualData(members.get(finalI).getId());
                    }
                });
                membersLayout.addView(v);
            }
        }
    }

    private void showIndividualData(String id) {
        try {
            if (drawerLayout.isDrawerOpen(Gravity.LEFT)) {
                drawerLayout.closeDrawer(Gravity.LEFT);
            }
        } catch (Exception ignored) {

        }
        setProgress(true);
        Double val = 0.0;
        List<ExpenseItem> individualData = new ArrayList<>();
        if (SessionHandler.getInstance(this).getExpenseList() != null && SessionHandler.getInstance(this).getExpenseList().size() > 0) {
            for (ExpenseItem item : SessionHandler.getInstance(this).getExpenseList()) {
                if (item != null) {
                    if (!id.isEmpty()) {
                        if (item.getUserId().equalsIgnoreCase(id)) {
                            individualData.add(item);
                            val = val + item.getAmount();
                        }
                    } else {
                        individualData.add(item);
                        val = val + item.getAmount();
                    }
                }
            }
        }
        expenseItems.clear();
        expenseItems.addAll(individualData);
        adapter.notifyDataSetChanged();
        tv_amount.setText(getString(R.string.amount, String.valueOf(val)));
        if (individualData.size() == 0) {
            setError(true);
        } else {
            setProgress(false);
        }

    }

    private void showAddExpenseFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        AddExpenseFragment newFragment = new AddExpenseFragment();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.add(newFragment, "Add");
        transaction.addToBackStack(null).commit();
    }

    private void setRecyclerView() {
        adapter = new ExpenseAdapter(this, expenseItems);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
    }

    private void setProgress(boolean val) {
        if (val) {
            recyclerView.setVisibility(View.GONE);
            lv_progress.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            lv_progress.setVisibility(View.GONE);
        }
    }

    private void setError(boolean val) {
        if (val) {
            recyclerView.setVisibility(View.GONE);
            lv_progress.setVisibility(View.GONE);
            lv_error.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            lv_progress.setVisibility(View.GONE);
            lv_error.setVisibility(View.GONE);
        }
    }


    private void setAmount(String amount) {
        tv_amount.setText(getString(R.string.amount, amount));
    }

    private void fetchData() {
        databaseReferenceTransactions = FirebaseDatabase.getInstance().getReference(AppConstants.TRANSACTIONS_TABLE);
        databaseReferenceTransactions.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                    setError(false);
                    processData(dataSnapshot);
                } else {
                    calculationProcess();
                    setError(true);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                setError(true);
            }
        });
    }

    private void processData(DataSnapshot dataSnapshot) {
        List<ExpenseItem> itemList = new ArrayList<>();
        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
            ExpenseItem item = postSnapshot.getValue(ExpenseItem.class);
            itemList.add(item);
        }
        SessionHandler.getInstance(this).setExpenseList(itemList);
        Collections.reverse(itemList);
        expenseItems.clear();
        expenseItems.addAll(itemList);
        adapter.notifyDataSetChanged();

        calculationProcess();
    }

    private void calculationProcess() {
        Double val = 0.0;
        if (expenseItems.size() > 0) {
            for (ExpenseItem item : expenseItems) {
                if (item != null) {
                    val = val + item.getAmount();
                }
            }
        }
        tv_amount.setText(getString(R.string.amount, String.valueOf(val)));
    }

}
