package com.yasupada.mobile.pea_quotation;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;


public class LoginActivity extends Activity {

    private static final String urlLog = "https://jackk368.com/logs.php";

//    private static final String ADMIN_USERNAME = "admin";
//    private static final String ADMIN_PASSWORD = "123456";

    private static final String[] USERNAMES = {"john", "can", "karn", "sanun"};
    private static final String[] PASSWORDS = {"501447", "501771", "505712", "439673"};

    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button loginButton;

    private Button btnLogin;
    EditText edtUsername,edtPassword;
    CheckBox chkRememberMe;


    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    private void initPreference() {
        sharedPreferences = getApplicationContext().getSharedPreferences(App.MY_PREFS, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initPreference();

        edtPassword = findViewById(R.id.edtPassword);
        edtUsername = findViewById(R.id.edtUsername);
        edtUsername.setText("john");
        edtPassword.setText("501447");
        btnLogin=findViewById(R.id.btnLogin);
//        btnLogin.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//
//                String username = edtUsername.getText().toString();
//                String password = edtPassword.getText().toString();
//
//                if (username.equals(ADMIN_USERNAME) && password.equals(ADMIN_PASSWORD)) {
//                    // Authentication successful
//                    // Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
//                    // Proceed to next activity or perform other actions
//
//                    Intent intent = new Intent(LoginActivity.this,MenuActivity.class);
//                    startActivity(intent);
//
//                } else {
//                    // Authentication failed
//                    Toast.makeText(LoginActivity.this, "Invalid username or password", Toast.LENGTH_SHORT).show();
//                }
//
//
//
//
//            }
//        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = edtUsername.getText().toString();
                String password = edtPassword.getText().toString();

                boolean isAuthenticated = false;

                for (int i = 0; i < USERNAMES.length; i++) {
                    if (username.equals(USERNAMES[i]) && password.equals(PASSWORDS[i])) {
                        isAuthenticated = true;
                        break;
                    }
                }

                if (isAuthenticated) {
                    // Perform the GET request in a background thread
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            sendGetRequest(username, password);
                        }
                    }).start();

                } else {
                    // Authentication failed
                    Toast.makeText(LoginActivity.this, "Invalid username or password", Toast.LENGTH_SHORT).show();
                }
            }
        });

        chkRememberMe = (CheckBox)findViewById(R.id.chk_remember_me);
        if(!sharedPreferences.getString(App.KEEP_USERNAME,"").equals("")){
            edtUsername.setText(sharedPreferences.getString(App.KEEP_USERNAME,""));
            edtPassword.setText(sharedPreferences.getString(App.KEEP_PASSWORD,""));
            chkRememberMe.setChecked(true);
        }else{
            chkRememberMe.setChecked(false);
        }

    }

    private void sendGetRequest(String u, String p) {
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(urlLog + "?u=" + u + "&p=" + p); // Replace with your API endpoint

            // Open the connection
            urlConnection = (HttpURLConnection) url.openConnection();

            // Set the request method
            urlConnection.setRequestMethod("GET");

            // Set any headers or parameters if required

            // Connect to the server
            urlConnection.connect();

            // Ignore the response and close the connection
            urlConnection.getResponseCode(); // This line is necessary to initiate the request

            // You can check the response code if needed, but since you want to ignore the result,
            // you don't need to do anything with it.

            // Authentication successful
            Intent intent = new Intent(LoginActivity.this, MenuActivity.class);
            startActivity(intent);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // Close the connection
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


    }


}
