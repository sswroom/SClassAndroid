package org.sswr.android;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ListView menuList = findViewById(R.id.menuList);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.simple_list, R.id.listText, new String[]{"Jasypt"} );
        menuList.setAdapter(adapter);
        menuList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String s = parent.getItemAtPosition(position).toString();
                switch (s)
                {
                    case "Jasypt":
                        startActivity(new Intent(MainActivity.this, JasyptActivity.class));
                        break;
                    default:
                        Toast.makeText(getApplicationContext(), "Unknwon item: "+s, Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });
    }
}