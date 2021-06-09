package org.sswr.android;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.sswr.util.crypto.JasyptEncryptor;

import java.nio.charset.StandardCharsets;

public class JasyptActivity extends AppCompatActivity {
    private Button encryptButton;
    private Button decryptButton;
    private TextView passwordText;
    private TextView messageText;
    private TextView resultText;

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
                JasyptEncryptor enc = new JasyptEncryptor(JasyptEncryptor.KeyAlgorithm.PBEWITHHMACSHA512, JasyptEncryptor.CipherAlgorithm.AES256, pwd);
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
                JasyptEncryptor enc = new JasyptEncryptor(JasyptEncryptor.KeyAlgorithm.PBEWITHHMACSHA512, JasyptEncryptor.CipherAlgorithm.AES256, pwd);
                byte buff[] = message.getBytes();
                JasyptActivity.this.resultText.setText(enc.encryptAsB64(buff, 0, buff.length));
            }
        });
    }
}