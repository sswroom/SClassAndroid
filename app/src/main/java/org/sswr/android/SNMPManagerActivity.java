package org.sswr.android;

import androidx.appcompat.app.AppCompatActivity;

import android.net.DhcpInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.Toast;

import org.sswr.util.data.ByteTool;
import org.sswr.util.data.DateTimeUtil;
import org.sswr.util.net.SNMPAgentInfo;
import org.sswr.util.net.SNMPManager;
import org.sswr.util.net.SNMPReadingInfo;
import org.sswr.util.net.SNMPUtil;

import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class SNMPManagerActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private EditText txtAgent;
    private EditText txtCommunity;
    private Button btnAdd;
    private ListView lbServer;
    private EditText txtSvrAgent;
    private Button btnWalk;
    private EditText txtDesc;
    private EditText txtOID;
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
        this.txtCommunity = findViewById(R.id.snmpManagerCommunity);
        this.btnAdd = findViewById(R.id.snmpManagerAdd);
        this.lbServer = findViewById(R.id.snmpManagerServer);
        this.txtSvrAgent = findViewById(R.id.snmpManagerServerAgent);
        this.btnWalk = findViewById(R.id.snmpManagerWalk);
        this.txtDesc = findViewById(R.id.snmpManagerDesc);
        this.txtOID = findViewById(R.id.snmpManagerOID);
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
            ByteTool.writeMInt32(ip, 0, dhcp.ipAddress | ~dhcp.netmask);
            try {
                this.txtAgent.setText(Inet4Address.getByAddress(ip).toString());
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
                    int i = SNMPManagerActivity.this.itemsTable.getChildCount();
                    while (i-- > 0)
                    {
                        reading = SNMPManagerActivity.this.readingTable.get(i);
                        if (reading.getValValid())
                        {
                            AndroidUtil.setSubItem(SNMPManagerActivity.this.itemsTable, i, 3, ""+reading.getCurrVal());
                        }
                        else
                        {
                            AndroidUtil.setSubItem(SNMPManagerActivity.this.itemsTable, i, 3, "-");
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
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        SNMPManagerActivity me = this;
        SNMPAgentInfo agent = (SNMPAgentInfo)me.agentList.get(position);
        if (agent != null)
        {
            me.txtSvrAgent.setText(agent.getAddr().toString());
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
            }
            else
            {
                me.txtOID.setText("");
            }
            if (agent.getName() != null)
            {
                me.txtName.setText(agent.getName());
            }
            else
            {
                me.txtName.setText("");
            }
/*            if (agent.contact)
            {
                me.txtAgentContact.SetText(agent.contact);
            }
            else
            {
                me.txtAgentContact.SetText((const UTF8Char*)"");
            }
            if (agent.location)
            {
                me.txtAgentLocation.SetText(agent.location);
            }
            else
            {
                me.txtAgentLocation.SetText((const UTF8Char*)"");
            }
            Text::StrHexBytes(sbuff, agent.mac, 6, ':');
            me.txtAgentPhyAddr.SetText(sbuff);
		const Net::MACInfo::MACEntry *ent = Net::MACInfo::GetMACInfoBuff(agent.mac);
            me.txtAgentVendor.SetText((const UTF8Char*)ent.name);
            if (agent.model)
            {
                me.txtAgentModel.SetText(agent.model);
            }
            else
            {
                me.txtAgentModel.SetText((const UTF8Char*)"");
            }
            me.lvAgentReading.ClearItems();
            UOSInt i = 0;
            UOSInt j = agent.readingList.GetCount();
            Net::SNMPManager::ReadingInfo *reading;
            while (i < j)
            {
                reading = agent.readingList.GetItem(i);
                me.lvAgentReading.AddItem(reading.name, reading);
                Text::StrUOSInt(sbuff, reading.index);
                me.lvAgentReading.SetSubItem(i, 1, sbuff);
                me.lvAgentReading.SetSubItem(i, 2, SSWR::SMonitor::SAnalogSensor::GetReadingTypeName(reading.readingType));
                if (reading.valValid)
                {
                    Text::StrDouble(sbuff, reading.currVal);
                    me.lvAgentReading.SetSubItem(i, 3, sbuff);
                }
                else
                {
                    me.lvAgentReading.SetSubItem(i, 3, (const UTF8Char*)"-");
                }

                i++;
            }*/
        }
        else
        {
            me.txtSvrAgent.setText("");
            me.txtDesc.setText("");
            me.txtOID.setText("");
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
        AndroidUtil.newTextRow(this, new String[]{"Name", "Index", "Type", "Value"});
    }
}