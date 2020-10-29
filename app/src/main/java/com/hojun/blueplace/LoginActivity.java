package com.hojun.blueplace;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.hojun.blueplace.database.LocalDatabase;
import com.hojun.blueplace.http.HttpProgressInterface;
import com.hojun.blueplace.http.user.LoginAsyncTask;

public class LoginActivity extends AppCompatActivity {
    private ProgressBar progressBar;
    private LinearLayout loginFormContainer;

    private EditText emailInput;
    private EditText passwordInput;

    private Button signUpButton;
    private Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setResult(RESULT_CANCELED, null);

        progressBar = (ProgressBar)findViewById(R.id.loginProgress);
        loginFormContainer = (LinearLayout)findViewById(R.id.loginFormContainer);

        emailInput = (EditText)findViewById(R.id.emailInput);
        passwordInput = (EditText)findViewById(R.id.passwordInput);

        signUpButton = (Button)findViewById(R.id.signUpButton);
        loginButton = (Button)findViewById(R.id.loginButton);

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
                startActivity(intent);
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login(emailInput.getText().toString(), passwordInput.getText().toString());
            }
        });
    }

    public void login(String email, final String password){
        LoginAsyncTask loginAsyncTask = new LoginAsyncTask(email, password, LocalDatabase.getInstance(this),
                new HttpProgressInterface() {
                    @Override
                    public void onPreExecute() {
                        loginFormContainer.setVisibility(View.GONE);
                        progressBar.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onPostExecute(Integer httpResult, String Message) {
                        loginFormContainer.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                        passwordInput.setText("");
                        if (httpResult >= 200 && httpResult < 300){
                            Toast.makeText(getApplicationContext(),"로그인에 성공하였습니다.",Toast.LENGTH_LONG).show();
                            setResult(RESULT_OK, null);
                            finish();
                        } else {
                            Toast.makeText(getApplicationContext(),"로그인에 실패했습니다.",Toast.LENGTH_LONG).show();
                            Toast.makeText(getApplicationContext(),"http result: " + httpResult,Toast.LENGTH_LONG).show();
                            Toast.makeText(getApplicationContext(),"Message: " + Message,Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onProgressUpdate(Integer progress) {
                    }
                }
        );
        loginAsyncTask.execute();
        return;
    }
}