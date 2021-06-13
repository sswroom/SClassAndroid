package org.sswr.android;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.Toast;

import org.sswr.util.net.SNMPBindingItem;
import org.sswr.util.net.SNMPClient;
import org.sswr.util.net.SNMPErrorStatus;
import org.sswr.util.net.SNMPInfo;
import org.sswr.util.net.SNMPUtil;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class SNMPClientActivity extends AppCompatActivity {
    private EditText txtAgent;
    private EditText txtCommunity;
    private EditText txtOID;
    private Spinner cboCmdType;
    private Button btnRequest;
    private TableLayout resultTable;
    private SNMPClient cli;

    private InetAddress agentAddr;
    private String community;
    private String oid;
    private int cmdType;
    private boolean resultUpdated;
    private SNMPErrorStatus resultErr;
    private List<SNMPBindingItem> resultList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_snmpclient);
        this.setTitle("SNMP Client");
        this.txtAgent = findViewById(R.id.snmpClientAgent);
        this.txtCommunity = findViewById(R.id.snmpClientCommunity);
        this.txtOID = findViewById(R.id.snmpClientOID);
        this.cboCmdType = findViewById(R.id.snmpClientCommandType);
        this.btnRequest = findViewById(R.id.snmpClientRequest);
        this.resultTable = findViewById(R.id.snmpClientResult);
        this.cboCmdType.setAdapter(new ArrayAdapter<String>(SNMPClientActivity.this, R.layout.simple_list, R.id.listText, new String[]{"GetRequest", "GetNextRequest", "Walk"}));
        this.cboCmdType.setSelection(2);

        this.cli = new SNMPClient(AndroidUtil.getFirstWiFiAddress());
        if (this.cli.isError())
        {
            Toast.makeText(this, "Error in starting SNMP Client", Toast.LENGTH_SHORT).show();
        }

        this.btnRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    SNMPClientActivity.this.agentAddr = InetAddress.getByName(SNMPClientActivity.this.txtAgent.getText().toString());
                }
                catch (UnknownHostException ex)
                {
                    Toast.makeText(SNMPClientActivity.this, "Error in resolving Agent Address", Toast.LENGTH_SHORT).show();
                    return;
                }
                SNMPClientActivity.this.community = SNMPClientActivity.this.txtCommunity.getText().toString();
                SNMPClientActivity.this.oid = SNMPClientActivity.this.txtOID.getText().toString();
                if (SNMPClientActivity.this.community.length() <= 0)
                {
                    Toast.makeText(SNMPClientActivity.this, "Please enter community", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (SNMPClientActivity.this.oid.length() <= 0)
                {
                    Toast.makeText(SNMPClientActivity.this, "Please enter OID", Toast.LENGTH_SHORT).show();
                    return;
                }
                SNMPClientActivity.this.cmdType = SNMPClientActivity.this.cboCmdType.getSelectedItemPosition();
                new AsyncTask<Void, Void, Void>(){

                    @Override
                    protected Void doInBackground(Void... voids) {
                        try {
                            List<SNMPBindingItem> itemList = new ArrayList<SNMPBindingItem>();
                            SNMPErrorStatus err;
                            if (SNMPClientActivity.this.cmdType == 0)
                            {
                                err = SNMPClientActivity.this.cli.v1GetRequest(agentAddr, community, oid, itemList);
                            }
                            else if (SNMPClientActivity.this.cmdType == 1)
                            {
                                err = SNMPClientActivity.this.cli.v1GetNextRequest(agentAddr, community, oid, itemList);
                            }
                            else
                            {
                                err = SNMPClientActivity.this.cli.v1Walk(agentAddr, community, oid, itemList);
                            }
                            SNMPClientActivity.this.resultErr = err;
                            SNMPClientActivity.this.resultList = itemList;
                            SNMPClientActivity.this.resultUpdated = true;
                        }
                        catch (Exception ex)
                        {
                            ex.printStackTrace();
                        }
                        return null;
                    }

                    protected void onPostExecute(Void tmp)
                    {
                        if (!SNMPClientActivity.this.resultUpdated)
                        {
                            return;
                        }
                        SNMPClientActivity.this.resultUpdated = false;
                        int i;
                        int j;
                        StringBuilder sb = new StringBuilder();
                        SNMPBindingItem item;
                        SNMPClientActivity.this.resultTable.removeAllViews();
                        if (SNMPClientActivity.this.resultErr != SNMPErrorStatus.NOERROR)
                        {
                            Toast.makeText(SNMPClientActivity.this, "Error in requesting to the server, error code = "+SNMPClientActivity.this.resultErr.toString(), Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            String[] row = new String[3];
                            row[0] = "OID";
                            row[1] = "ValueType";
                            row[2] = "Value";
                            SNMPClientActivity.this.resultTable.addView(AndroidUtil.newTextRow(SNMPClientActivity.this, row));

                            i = 0;
                            j = SNMPClientActivity.this.resultList.size();
                            while (i < j)
                            {
                                item = SNMPClientActivity.this.resultList.get(i);
                                sb.setLength(0);
                                SNMPUtil.oidToString(item.getOid(), 0, item.getOidLen(), sb);
                                row[0] = sb.toString();
                                row[1] = SNMPUtil.typeGetName(item.getValType());
                                if (item.getValBuff() == null)
                                {
                                    row[2] = "";
                                }
                                else
                                {
                                    sb.setLength(0);
                                    SNMPInfo.valueToString(item.getValType(), item.getValBuff(), 0, item.getValLen(), sb);
                                    row[2] = sb.toString();
                                }

                                SNMPClientActivity.this.resultTable.addView(AndroidUtil.newTextRow(SNMPClientActivity.this, row));
                                i++;
                            }
                        }
                    }

                }.execute();
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        this.cli.close();
    }
}