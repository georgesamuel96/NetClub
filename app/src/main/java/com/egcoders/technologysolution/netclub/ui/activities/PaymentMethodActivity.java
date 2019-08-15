package com.egcoders.technologysolution.netclub.ui.activities;

import android.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Pair;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.egcoders.technologysolution.netclub.ui.adapter.PaymentMethodAdapter;
import com.egcoders.technologysolution.netclub.R;
import com.egcoders.technologysolution.netclub.data.instance.SaveUserInstance;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class PaymentMethodActivity extends AppCompatActivity {

    private android.support.v7.widget.Toolbar toolbar;
    private String registrationId;
    private FirebaseFirestore firestore;
    private ListView listView;
    private ArrayList<Pair<Integer, String>> list = new ArrayList<>();
    private PaymentMethodAdapter adapter;
    private AlertDialog.Builder alertBuilder;
    private ProgressBar progressBar;
    private SaveUserInstance userInstance;
    private ArrayList<String> datesByUser = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_method);


       /* toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_action_menu);

        registrationId = getIntent().getStringExtra("registrationId");
        datesByUser = (ArrayList<String>) getIntent().getSerializableExtra("dates");
        firestore = FirebaseFirestore.getInstance();

        listView = (ListView) findViewById(R.id.list_view);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        firestore = FirebaseFirestore.getInstance();
        alertBuilder = new AlertDialog.Builder(this);
        userInstance = new SaveUserInstance();

        list.add(Pair.create(R.drawable.fawry, "Fawry"));
        list.add(Pair.create(R.drawable.aman, "Amaan"));

        adapter = new PaymentMethodAdapter(getApplicationContext(), list);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                alertBuilder.setTitle("Successful Registration");
                alertBuilder.setMessage("You will receive message with details");
                alertBuilder.setCancelable(false);
                alertBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        progressBar.setVisibility(View.VISIBLE);
                        for(String date : datesByUser) {

                            Map<String, Object> dateMap = new HashMap<>();
                            dateMap.put("date", date);
                            firestore.collection("Registration").document(registrationId)
                                    .collection("selectedDates").add(dateMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentReference> task) {
                                    if(task.isSuccessful()){

                                    }
                                    else{

                                    }
                                }
                            });
                        }

                        firestore.collection("Registration").document(registrationId).update("paymentMethod",
                                list.get(position).second).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){

                                }
                                else{

                                }
                            }
                        });

                        userInstance.setIsActivityFirstLoad(true);
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("TOP", true);
                        startActivity(intent);

                        dialog.cancel();
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                AlertDialog alertDialog = alertBuilder.create();
                alertDialog.show();
            }
        });*/
    }

    /*@Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent i = new Intent(PaymentMethodActivity.this, DetailsMentorActivity.class);
        startActivity(i);
        finish();
    }*/
}
