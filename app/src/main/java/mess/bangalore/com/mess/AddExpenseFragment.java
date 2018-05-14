package mess.bangalore.com.mess;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import mess.bangalore.com.mess.Utilities.AppConstants;
import mess.bangalore.com.mess.Utilities.SessionHandler;
import mess.bangalore.com.mess.models.ExpenseItem;

public class AddExpenseFragment extends DialogFragment {


    private TextInputLayout til_description, til_amount;
    private EditText et_description, et_amount;
    private Spinner tagSpinner;
    String[] tags;
    String tag;
    private DatabaseReference databaseReferenceTransactions;

    public AddExpenseFragment() {
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root_view = inflater.inflate(R.layout.content_add_expense, container, false);
        til_description = root_view.findViewById(R.id.til_description);
        til_amount = root_view.findViewById(R.id.til_amount);
        et_description = root_view.findViewById(R.id.description);
        et_amount = root_view.findViewById(R.id.amount);
        tagSpinner = root_view.findViewById(R.id.tagspinner);
        ImageView close = root_view.findViewById(R.id.bt_close);
        Button save = root_view.findViewById(R.id.bt_save);
        tagSpinner = root_view.findViewById(R.id.tagspinner);
        tags = getResources().getStringArray(R.array.tags);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.simple_spinner_dropdown_item, tags);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tagSpinner.setAdapter(adapter);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateData();
            }
        });
        tagSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                tag = tags[i];
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        return root_view;
    }

    private void validateData() {
        til_amount.setErrorEnabled(false);
        til_amount.setErrorEnabled(false);
        if (et_description.getText().toString().trim().length() > 0) {
            if (et_amount.getText().toString().trim().length() > 0) {
                sendData();
            } else {
                til_amount.setErrorEnabled(true);
                til_amount.setError("Enter a valid amount");
            }
        } else {
            til_description.setErrorEnabled(true);
            til_description.setError("Enter a valid description");
        }
    }

    private void sendData() {
        try {
            Double amount = Double.valueOf(et_amount.getText().toString());
            String desc = et_description.getText().toString();

            databaseReferenceTransactions = FirebaseDatabase.getInstance().getReference(AppConstants.TRANSACTIONS_TABLE);
            String id = databaseReferenceTransactions.push().getKey();
            ExpenseItem item = new ExpenseItem(id, desc, String.valueOf(System.currentTimeMillis()), amount, tag, SessionHandler.getInstance(getActivity()).getUserId(), SessionHandler.getInstance(getActivity()).getUserName());
            databaseReferenceTransactions.child(id).setValue(item);
            databaseReferenceTransactions.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                        processData(dataSnapshot);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }catch (Exception ignored){

        }

    }

    private void processData(DataSnapshot dataSnapshot) {
        List<ExpenseItem> expenseItems = new ArrayList<>();
        if (dataSnapshot != null && dataSnapshot.getChildrenCount() > 0) {
            for (DataSnapshot item : dataSnapshot.getChildren()) {
                ExpenseItem expenseItem = item.getValue(ExpenseItem.class);
                expenseItems.add(expenseItem);
            }
        }
        dismiss();
    }

}
