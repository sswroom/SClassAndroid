package org.sswr.android;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ListView menuList = findViewById(R.id.menuList);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.simple_list, R.id.listText, new String[]{"Jasypt", "Text Encrypt", "SNMP Client", "SNMP Trap Monitor", "SNMP MIB", "SNMP Manager"} );
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
                    case "Text Encrypt":
                        startActivity(new Intent(MainActivity.this, TextEncrypt.class));
                        break;
                    case "SNMP Client":
                        startActivity(new Intent(MainActivity.this, SNMPClientActivity.class));
                        break;
                    default:
                        Toast.makeText(getApplicationContext(), "Unknwon item: "+s, Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });
    }
}