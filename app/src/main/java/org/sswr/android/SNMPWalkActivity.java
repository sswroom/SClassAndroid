package org.sswr.android;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.Toast;

import org.sswr.util.net.SNMPAgentInfo;
import org.sswr.util.net.SNMPBindingItem;
import org.sswr.util.net.SNMPClient;
import org.sswr.util.net.SNMPErrorStatus;
import org.sswr.util.net.SNMPInfo;
import org.sswr.util.net.SNMPOIDDB;
import org.sswr.util.net.SNMPUtil;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.channels.AsynchronousByteChannel;
import java.util.ArrayList;
import java.util.List;

public class SNMPWalkActivity extends AppCompatActivity {
	private EditText txtAgent;
	private TableLayout itemTable;
	private InetAddress addr;
	private String community;
	private List<SNMPBindingItem> itemList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_snmpwalk);

		this.txtAgent = findViewById(R.id.snmpWalkAgent);
		this.itemTable = findViewById(R.id.snmpWalkItems);

		this.itemTable.addView(AndroidUtil.newTextRow(this, new String[]{"OID", "Name", "ValueType", "Value"}));
		String agent = this.getIntent().getStringExtra("agent");
		this.txtAgent.setText(agent);
		try {
			this.addr = InetAddress.getByName(agent);
		}
		catch (UnknownHostException ex)
		{
			return;
		}
		this.community = this.getIntent().getStringExtra("community");
		this.itemList = new ArrayList<SNMPBindingItem>();

		new AsyncTask<Void, Void, Void>(){
			@Override
			protected Void doInBackground(Void... voids) {
				SNMPClient cli = new SNMPClient(AndroidUtil.getFirstWiFiAddress());
				if (!cli.isError())
				{
					cli.v1Walk(addr, community, "1.3.6.1.2.1", itemList);
				}
				cli.close();
				
				return null;
			}

			protected void onPostExecute(Void tmp) {
				SNMPBindingItem item;
				int i;
				int j;
				StringBuilder sb = new StringBuilder();
				String cols[] = new String[4];
				i = 0;
				j = itemList.size();
				while (i < j)
				{
					item = itemList.get(i);
					sb.setLength(0);
					SNMPUtil.oidToString(item.getOid(), 0, item.getOidLen(), sb);
					cols[0] = sb.toString();
					sb.setLength(0);
					SNMPOIDDB.oidToNameString(item.getOid(), 0, item.getOidLen(), sb);
					cols[1] = sb.toString();
					cols[2] = SNMPUtil.typeGetName(item.getValType());
					if (item.getValBuff() != null)
					{
						sb.setLength(0);
						SNMPInfo.valueToString(item.getValType(), item.getValBuff(), 0, item.getValLen(), sb);
						cols[3] = sb.toString();
					}
					else
					{
						cols[3] = "";
					}
					itemTable.addView(AndroidUtil.newTextRow(SNMPWalkActivity.this, cols));
					i++;
				}
			}
		}.execute();
	}
}