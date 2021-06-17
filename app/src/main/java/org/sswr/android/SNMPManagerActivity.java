package org.sswr.android;

import androidx.appcompat.app.AppCompatActivity;

import android.net.DhcpInfo;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.Toast;

import org.sswr.util.data.ByteTool;
import org.sswr.util.net.SNMPManager;

import java.net.Inet4Address;
import java.net.UnknownHostException;

public class SNMPManagerActivity extends AppCompatActivity {
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

    private SNMPManager mgr;

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
    }
}