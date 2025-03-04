package com.netforge.midtermfinalproject;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Livestock_Center extends AppCompatActivity implements  AdapterView.OnItemClickListener {
    ArrayAdapter<String> adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_livestock_center);


        String[]data={"Buyer Requests","Transactions","Educating the Community on Farming"};
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
        String info=adapter.getItem(position);
        switch (position){
            case 0:
                Intent intent1=new Intent(getApplicationContext(), BuyerPurchaseApprovals.class);
                startActivity(intent1);
                break;
            case 1:
                Intent intent2=new Intent(getApplicationContext(),FarmerTransactionsActivity.class);
                startActivity(intent2);
                break;
            case 2:
                String videoId = "dQw4w9WgXcQ";
                String youtubeUrl = "https://www.youtube.com/watch?v=qXvOsfdI4Rg&list=PLdsm-UX-yWpzfxLzxzj4IeLNh6ZP71sE9" + videoId;

                try {
                    Intent intent3 = new Intent(Intent.ACTION_VIEW, Uri.parse(youtubeUrl));
                    startActivity(intent3);
                } catch (Exception e) {
                    Toast.makeText(Livestock_Center.this, "Unable to open YouTube video", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}