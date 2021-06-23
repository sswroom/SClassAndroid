package org.sswr.android;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.TableLayout;

public class SNMPWalkActivity extends AppCompatActivity {
	private EditText txtAgent;
	private TableLayout itemTable;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_snmpwalk);

		this.txtAgent = findViewById(R.id.snmpWalkAgent);
		this.itemTable = findViewById(R.id.snmpWalkItems);
	}
}