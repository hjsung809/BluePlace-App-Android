package com.hojun.blueplace;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.hojun.blueplace.database.LocalDatabase;
import com.hojun.blueplace.http.HttpProgressInterface;
import com.hojun.blueplace.http.user.CreateUserAsyncTask;

public class SignUpActivity extends AppCompatActivity {
    LinearLayout signInContainer;
    ProgressBar progressBar;

    EditText emailInput;
    EditText passwordInput;
    EditText phoneNumberInput;
    Button cancelButton;
    Button signUpButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        signInContainer = findViewById(R.id.signInFormContainer);
        progressBar = findViewById(R.id.signInProgress);

        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        phoneNumberInput = findViewById(R.id.phoneNumberInput);

        cancelButton = findViewById(R.id.cancelButton);
        signUpButton = findViewById(R.id.signUpButton);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUp(emailInput.getText().toString(), passwordInput.getText().toString(), phoneNumberInput.getText().toString());
            }
        });
    }

    private void signUp(String email, String password, final String phoneNumber) {
        CreateUserAsyncTask createUserAsyncTask = new CreateUserAsyncTask(email, password, phoneNumber, LocalDatabase.getInstance(this),
                new HttpProgressInterface() {
                    @Override
                    public void onPreExecute() {
                        progressBar.setVisibility(View.VISIBLE);
                        signInContainer.setVisibility(View.GONE);
                    }

                    @Override
                    public void onPostExecute(Integer httpResult, String Message) {
                        progressBar.setVisibility(View.GONE);
                        signInContainer.setVisibility(View.VISIBLE);
                        emailInput.setText("");
                        passwordInput.setText("");
                        phoneNumberInput.setText("");
                        if (httpResult >= 200 && httpResult < 300){
                            Toast.makeText(getApplicationContext(),"회원가입 성공하였습니다.",Toast.LENGTH_LONG).show();
                            finish();
                        } else {
                            Toast.makeText(getApplicationContext(),"회원가입에 실패했습니다.",Toast.LENGTH_LONG).show();
                            Toast.makeText(getApplicationContext(),"http result: " + httpResult,Toast.LENGTH_LONG).show();
                            Toast.makeText(getApplicationContext(),"Message: " + Message,Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onProgressUpdate(Integer progress) {

                    }
                });

        createUserAsyncTask.execute();
    }
}