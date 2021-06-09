package org.sswr.android;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.sswr.util.crypto.JasyptEncryptor;

import java.nio.charset.StandardCharsets;

public class JasyptActivity extends AppCompatActivity {
    private Button encryptButton;
    private Button decryptButton;
    private EditText passwordText;
    private EditText messageText;
    private EditText resultText;
    private Spinner keyAlgSpinner;
    private Spinner cipherAlgSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jasypt);
        this.setTitle("Jasypt");
        this.encryptButton = findViewById(R.id.jasyptBtnEncrypt);
        this.decryptButton = findViewById(R.id.jasyptBtnDecrypt);
        this.passwordText = findViewById(R.id.jasyptPassword);
        this.messageText = findViewById(R.id.jasyptMessage);
        this.resultText = findViewById(R.id.jasyptResult);
        this.keyAlgSpinner = findViewById(R.id.jasyptKeyAlg);
        this.cipherAlgSpinner = findViewById(R.id.jasyptCipherAlg);
        ArrayAdapter<JasyptEncryptor.KeyAlgorithm> keyAlgs = new ArrayAdapter<JasyptEncryptor.KeyAlgorithm>(this, R.layout.simple_list, R.id.listText, JasyptEncryptor.KeyAlgorithm.values());
        this.keyAlgSpinner.setAdapter(keyAlgs);
        ArrayAdapter<JasyptEncryptor.CipherAlgorithm> cipherAlgs = new ArrayAdapter<JasyptEncryptor.CipherAlgorithm>(this, R.layout.simple_list, R.id.listText, JasyptEncryptor.CipherAlgorithm.values());
        this.cipherAlgSpinner.setAdapter(cipherAlgs);
        this.decryptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pwd = JasyptActivity.this.passwordText.getText().toString();
                String message = JasyptActivity.this.messageText.getText().toString();
                if (pwd.length() == 0)
                {
                    Toast.makeText(JasyptActivity.this, "Password is empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (message.length() == 0)
                {
                    Toast.makeText(JasyptActivity.this, "Message is empty", Toast.LENGTH_SHORT).show();
                }
                JasyptEncryptor.KeyAlgorithm keyAlg = (JasyptEncryptor.KeyAlgorithm)JasyptActivity.this.keyAlgSpinner.getSelectedItem();
                JasyptEncryptor.CipherAlgorithm cipherAlg = (JasyptEncryptor.CipherAlgorithm)JasyptActivity.this.cipherAlgSpinner.getSelectedItem();
                JasyptEncryptor enc = new JasyptEncryptor(keyAlg, cipherAlg, pwd);
                JasyptActivity.this.resultText.setText(enc.decryptToString(message));
            }
        });

        this.encryptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pwd = JasyptActivity.this.passwordText.getText().toString();
                String message = JasyptActivity.this.messageText.getText().toString();
                if (pwd.length() == 0)
                {
                    Toast.makeText(JasyptActivity.this, "Password is empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (message.length() == 0)
                {
                    Toast.makeText(JasyptActivity.this, "Message is empty", Toast.LENGTH_SHORT).show();
                }
                JasyptEncryptor.KeyAlgorithm keyAlg = (JasyptEncryptor.KeyAlgorithm)JasyptActivity.this.keyAlgSpinner.getSelectedItem();
                JasyptEncryptor.CipherAlgorithm cipherAlg = (JasyptEncryptor.CipherAlgorithm)JasyptActivity.this.cipherAlgSpinner.getSelectedItem();
                JasyptEncryptor enc = new JasyptEncryptor(keyAlg, cipherAlg, pwd);
                byte buff[] = message.getBytes();
                JasyptActivity.this.resultText.setText(enc.encryptAsB64(buff, 0, buff.length));
            }
        });
    }
}