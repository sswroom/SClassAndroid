package org.sswr.android;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.DhcpInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;

import org.sswr.util.data.ByteTool;
import org.sswr.util.data.DateTimeUtil;
import org.sswr.util.data.StringUtil;
import org.sswr.util.net.MACEntry;
import org.sswr.util.net.MACInfo;
import org.sswr.util.net.SNMPAgentInfo;
import org.sswr.util.net.SNMPManager;
import org.sswr.util.net.SNMPOIDDB;
import org.sswr.util.net.SNMPReadingInfo;
import org.sswr.util.net.SNMPUtil;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.channels.AsynchronousByteChannel;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class SNMPManagerActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, View.OnClickListener {
	private EditText txtAgent;
	private CheckBox chkScan;
	private EditText txtCommunity;
	private Button btnAdd;
	private ListView lbServer;
	private EditText txtSvrAgent;
	private Button btnWalk;
	private EditText txtDesc;
	private EditText txtOID;
	private EditText txtOIDName;
	private EditText txtName;
	private EditText txtContact;
	private EditText txtLocation;
	private EditText txtPhyAddr;
	private EditText txtVendor;
	private EditText txtModel;
	private TableLayout itemsTable;
	private List<SNMPAgentInfo> agentList;
	private List<SNMPReadingInfo> readingTable;

	private SNMPManager mgr;
	private Timer timer;
	private long lastUpdateTime;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_snmpmanager);

		this.txtAgent = findViewById(R.id.snmpManagerAgent);
		this.chkScan = findViewById(R.id.snmpManagerScanIP);
		this.txtCommunity = findViewById(R.id.snmpManagerCommunity);
		this.btnAdd = findViewById(R.id.snmpManagerAdd);
		this.lbServer = findViewById(R.id.snmpManagerServer);
		this.txtSvrAgent = findViewById(R.id.snmpManagerServerAgent);
		this.btnWalk = findViewById(R.id.snmpManagerWalk);
		this.txtDesc = findViewById(R.id.snmpManagerDesc);
		this.txtOID = findViewById(R.id.snmpManagerOID);
		this.txtOIDName = findViewById(R.id.snmpManagerOIDName);
		this.txtName = findViewById(R.id.snmpManagerName);
		this.txtContact = findViewById(R.id.snmpManagerContact);
		this.txtLocation = findViewById(R.id.snmpManagerLocation);
		this.txtPhyAddr = findViewById(R.id.snmpManagerPhyAddr);
		this.txtVendor = findViewById(R.id.snmpManagerVendor);
		this.txtModel = findViewById(R.id.snmpManagerModel);
		this.itemsTable = findViewById(R.id.snmpManagerSvrItem);
		this.readingTable = new ArrayList<SNMPReadingInfo>();
		this.agentList = new ArrayList<SNMPAgentInfo>();

		this.mgr = new SNMPManager(AndroidUtil.getFirstWiFiAddress());
		if (this.mgr.isError())
		{
			Toast.makeText(this, "Error in starting SNMP Manager", Toast.LENGTH_SHORT).show();
		}
		DhcpInfo dhcp = AndroidUtil.getWifiDhcp(this);
		if (dhcp != null)
		{
			byte ip[] = new byte[4];
			ByteTool.writeInt32(ip, 0, dhcp.ipAddress | ~dhcp.netmask);
			try {
				this.txtAgent.setText(Inet4Address.getByAddress(ip).getHostAddress());
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
		}

		this.lbServer.setOnItemClickListener(this);

		this.timer = new Timer();
		this.timer.schedule(new TimerTask(){

			@Override
			public void run() {
				long dt = System.currentTimeMillis();
				if (dt - SNMPManagerActivity.this.lastUpdateTime >= 30000)
				{
					SNMPReadingInfo reading;
					SNMPManagerActivity.this.mgr.updateValues();
					int i = SNMPManagerActivity.this.readingTable.size();
					while (i-- > 0)
					{
						reading = SNMPManagerActivity.this.readingTable.get(i);
						if (reading.getValValid())
						{
							AndroidUtil.setSubItem(SNMPManagerActivity.this.itemsTable, i + 1, 3, StringUtil.fromDouble(reading.getCurrVal()));
						}
						else
						{
							AndroidUtil.setSubItem(SNMPManagerActivity.this.itemsTable, i + 1, 3, "-");
						}
					}
/*                    if (me.chkSendToSvr.IsChecked())
                    {
                        Data::ArrayList<Net::SNMPManager::AgentInfo*> agentList;
                        me.mgr.GetAgentList(&agentList);
                        if (agentList.GetCount() > 0)
                        {
                            me.SendAgentValues(&agentList);
                        }
                    }*/
					SNMPManagerActivity.this.lastUpdateTime = System.currentTimeMillis();
				}
			}
		}, 0, 1000);

		this.btnAdd.setOnClickListener(this);
		this.btnWalk.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				String s = SNMPManagerActivity.this.txtSvrAgent.getText().toString();
				if (s.length() > 0)
				{
					Intent intent = new Intent(SNMPManagerActivity.this, SNMPWalkActivity.class);
					intent.putExtra("agent", s);
					intent.putExtra("community", SNMPManagerActivity.this.txtCommunity.getText().toString());
					startActivity(intent);

				}
			}
		});
	}

	@Override
	public void onClick(View view) {
		SNMPManagerActivity me = this;
		InetAddress addr;
		try {
			addr = InetAddress.getByName(me.txtAgent.getText().toString());
		}
		catch (UnknownHostException ex)
		{
			Toast.makeText(me, "Error in parsing Agent Address", Toast.LENGTH_SHORT).show();
			return;
		}

		String community = me.txtCommunity.getText().toString();
		if (community.length() <= 0)
		{
			Toast.makeText(me, "Please enter community", Toast.LENGTH_SHORT).show();
			return;
		}
		new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... voids) {
				int j;
				List<SNMPAgentInfo> agentList = new ArrayList<SNMPAgentInfo>();
				j = me.mgr.addAgents(addr, community, agentList, me.chkScan.isChecked());
				if (j <= 0) {
					Toast.makeText(me, "Error in Adding Agent", Toast.LENGTH_SHORT).show();
				} else {
					SNMPManagerActivity.this.agentList.addAll(agentList);
				}
				return null;
			}

			protected void onPostExecute(Void tmp)
			{
				SNMPManagerActivity me = SNMPManagerActivity.this;
				int i = 0;
				int j = me.agentList.size();
				SNMPAgentInfo agent;
	/*		int k;
			if (me->chkSendToSvr->IsChecked())
			{
				Int64 cliId;
				UOSInt l;
				Net::SNMPManager::ReadingInfo *reading;
				Data::UInt32Map<UInt16> readingMap;
				UInt16 currId;
				me->SendAgentValues(&agentList);
				Sync::Thread::Sleep(100);
				i = 0;
				while (i < j)
				{
					agent = agentList.GetItem(i);
					cliId = me->mgr->Agent2CliId(agent);
					if (agent->name)
					{
						me->redir->SendDevName(cliId, agent->name);
					}
					if (agent->model)
					{
						if (agent->vendor)
						{
							Text::StringBuilderUTF8 sbPlatform;
							sbPlatform.Append(agent->vendor);
							sbPlatform.AppendChar(' ', 1);
							sbPlatform.Append(agent->model);
							me->redir->SendDevPlatform(cliId, sbPlatform.ToString());
						}
						else
						{
							me->redir->SendDevPlatform(cliId, agent->model);
						}
					}
					if (agent->cpuName)
					{
						me->redir->SendDevPlatform(cliId, agent->cpuName);
					}
					k = 0;
					l = agent->readingList->GetCount();
					while (k < l)
					{
						reading = agent->readingList->GetItem(k);
						currId = readingMap.Get((UInt32)reading->index);
						readingMap.Put((UInt32)reading->index, (UInt16)(currId + 1));
						if (reading->name)
						{
							me->redir->SendDevReadingName(cliId, k, (UInt16)reading->index, currId, reading->name);
						}
						k++;
					}
					i++;
				}
			}*/
				String[] items = new String[j];
				i = 0;
				while (i < j) {
					agent = me.agentList.get(i);
					items[i] = agent.getAddr().getHostAddress();
					i++;
				}
				ArrayAdapter<String> adapter = new ArrayAdapter<String>(me, R.layout.simple_list, R.id.listText, items);
				me.lbServer.setAdapter(adapter);
			}
		}.execute();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		SNMPManagerActivity me = this;
		SNMPAgentInfo agent = (SNMPAgentInfo)me.agentList.get(position);
		if (agent != null)
		{
			me.txtSvrAgent.setText(agent.getAddr().getHostAddress());
			if (agent.getDescr() != null)
			{
				me.txtDesc.setText(agent.getDescr());
			}
			else
			{
				me.txtDesc.setText("");
			}
			if (agent.getObjIdLen() > 0)
			{
				StringBuilder sb = new StringBuilder();
				SNMPUtil.oidToString(agent.getObjId(), 0, agent.getObjIdLen(), sb);
				me.txtOID.setText(sb.toString());
				sb.setLength(0);
				SNMPOIDDB.oidToNameString(agent.getObjId(), 0, agent.getObjIdLen(), sb);
				me.txtOIDName.setText(sb.toString());
			}
			else
			{
				me.txtOID.setText("");
				me.txtOIDName.setText("");
			}
			if (agent.getName() != null)
			{
				me.txtName.setText(agent.getName());
			}
			else
			{
				me.txtName.setText("");
			}
			if (agent.getContact() != null)
			{
				me.txtContact.setText(agent.getContact());
			}
			else
			{
				me.txtContact.setText("");
			}
			if (agent.getLocation() != null)
			{
				me.txtLocation.setText(agent.getLocation());
			}
			else
			{
				me.txtLocation.setText("");
			}
			me.txtPhyAddr.setText(StringUtil.toHex(agent.getMac(), 0, 6, ':'));
			MACEntry ent = MACInfo.getMACInfoBuff(agent.getMac(), 0);
			me.txtVendor.setText(ent.getName());
			if (agent.getModel() != null)
			{
				me.txtModel.setText(agent.getModel());
			}
			else
			{
				me.txtModel.setText("");
			}
			me.resetReading();
			int i = 0;
			int j = agent.getReadingList().size();
			SNMPReadingInfo reading;
			while (i < j)
			{
				reading = agent.getReadingList().get(i);
				me.itemsTable.addView(AndroidUtil.newTextRow(me, new String[]{reading.getName(), reading.getIndex()+"", reading.getReadingType().toString(), reading.getValValid()?(StringUtil.fromDouble(reading.getCurrVal())):"-"}));
				me.readingTable.add(reading);
				i++;
			}
		}
		else
		{
			me.txtSvrAgent.setText("");
			me.txtDesc.setText("");
			me.txtOID.setText("");
			me.txtOIDName.setText("");
			me.txtName.setText("");
			me.txtContact.setText("");
			me.txtLocation.setText("");
			me.txtPhyAddr.setText("");
			me.txtVendor.setText("");
			me.txtModel.setText("");
			me.resetReading();
		}
	}

	private void resetReading()
	{
		this.readingTable.clear();
		this.itemsTable.removeAllViews();
		TableRow row = AndroidUtil.newTextRow(this, new String[]{"Name", "Index", "Type", "Value"});
		row.setBackgroundColor(0xffcccccc);
		this.itemsTable.addView(row);
	}
}