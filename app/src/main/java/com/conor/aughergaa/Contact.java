package com.conor.aughergaa;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Contact extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;

    @BindView(R.id.input_email)
    EditText _emailText;
    @BindView(R.id.message)
    EditText message;
    @BindView(R.id.input_name)
    EditText _nameText;
    @BindView(R.id.input_Subject)
    EditText _subject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        ButterKnife.bind(this);

        Button _loginButton = (Button) findViewById(R.id.btn_sendQuery);

        _loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (_emailText.getText().toString().isEmpty()){
                    Toast.makeText(Contact.this, "Please enter Email", Toast.LENGTH_SHORT).show();
                }else if (_nameText.getText().toString().isEmpty()){
                    Toast.makeText(Contact.this, "Please enter Name", Toast.LENGTH_SHORT).show();
                }else if (_subject.getText().toString().isEmpty()){
                    Toast.makeText(Contact.this, "Please enter Subject", Toast.LENGTH_SHORT).show();
                }else if (message.getText().toString().isEmpty()){
                    Toast.makeText(Contact.this, "Please enter Message", Toast.LENGTH_SHORT).show();
                }else{
                    Intent i = new Intent(Intent.ACTION_SEND);
                    i.setType("message/rfc822");
                    i.putExtra(Intent.EXTRA_EMAIL, new String[]{"aughergfc@gmail.com"});
                    i.putExtra(Intent.EXTRA_SUBJECT, _subject.getText().toString());
                    i.putExtra(Intent.EXTRA_TEXT, message.getText().toString());
                    try {
                        startActivity(Intent.createChooser(i, "Send mail..."));
                    } catch (android.content.ActivityNotFoundException ex) {
                        Toast.makeText(Contact.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });
    }
}
