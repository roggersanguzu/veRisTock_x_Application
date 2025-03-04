package com.netforge.midtermfinalproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class BuyerLanding extends AppCompatActivity implements AdapterView.OnItemClickListener {
    ArrayAdapter<String> adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_buyer_landing);

        String[]data={"Send Purchase Requests","Buyer Requests Storage","Buyer Transactions","System Settings"};
        ListView listView=findViewById(R.id.main);
        adapter=new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,data);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (position){
            case 0:
                Intent intent1=new Intent(getApplicationContext(), BuyerMakeOrder.class);
                startActivity(intent1);
                break;
            case 1:
                Intent intent2=new Intent(getApplicationContext(),BuyerPurchaseApprovals.class);
                startActivity(intent2);
                break;
            case 2:
                Intent intent3=new Intent(getApplicationContext(),BuyerTransactions.class);
                startActivity(intent3);
                break;
            case 3:
                Intent intent4=new Intent(getApplicationContext(),SettingsActivity.class);
                startActivity(intent4);
                break;

        }
    }
}