package org.sswr.android;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.sswr.util.crypto.JasyptEncryptor;
import org.sswr.util.data.textbinenc.TextBinEnc;
import org.sswr.util.data.textbinenc.TextBinEncList;

import java.util.List;

public class TextEncrypt extends AppCompatActivity {
    private Spinner srcEncSpinner;
    private EditText srcText;
    private Spinner destEncSpinner;
    private EditText destText;
    private Button convertButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_encrypt);
        this.setTitle("Text Encrypt");
        this.srcEncSpinner = findViewById(R.id.textEncSrcEnc);
        this.srcText = findViewById(R.id.textEncSrcText);
        this.destEncSpinner = findViewById(R.id.textEncDestEnc);
        this.destText = findViewById(R.id.textEncDestText);
        this.convertButton = findViewById(R.id.textEncConvert);
        List<TextBinEnc> encList = TextBinEncList.getEncList();
        ArrayAdapter<TextBinEnc> srcEncList = new ArrayAdapter<TextBinEnc>(this, R.layout.simple_list, R.id.listText, encList);
        this.srcEncSpinner.setAdapter(srcEncList);
        ArrayAdapter<TextBinEnc> destEncList = new ArrayAdapter<TextBinEnc>(this, R.layout.simple_list, R.id.listText, encList);
        this.destEncSpinner.setAdapter(destEncList);

        this.convertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextBinEnc srcEnc = (TextBinEnc)TextEncrypt.this.srcEncSpinner.getSelectedItem();
                TextBinEnc destEnc = (TextBinEnc)TextEncrypt.this.destEncSpinner.getSelectedItem();
                String s = TextEncrypt.this.srcText.getText().toString();
                if (srcEnc == null)
                {
                    Toast.makeText(TextEncrypt.this, "Please select source encryption", Toast.LENGTH_SHORT).show();
                }
                else if (destEnc == null)
                {
                    Toast.makeText(TextEncrypt.this, "Please select dest encryption", Toast.LENGTH_SHORT).show();
                }
                else if (s.length() == 0)
                {
                    Toast.makeText(TextEncrypt.this, "Please enter source text", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    byte buff[] = srcEnc.decodeBin(s);
                    if (buff == null)
                    {
                        Toast.makeText(TextEncrypt.this, "Unsupported decryption", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        TextEncrypt.this.destText.setText(destEnc.encodeBin(buff, 0, buff.length));
                    }
                }
            }
        });
    }
}