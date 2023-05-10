package com.yasupada.mobile.pea_quotation;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class LoginActivity extends Activity {

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
        edtUsername.setText("");
        edtPassword.setText("");
        btnLogin=findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DoLogin doLogin = new DoLogin();
                doLogin.uname = edtUsername.getText().toString();
                doLogin.pword = edtPassword.getText().toString();
                doLogin.execute();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


    }

    ///////////////////
    private class DoLogin extends AsyncTask<Void, Void, String> {
        String postUrl;
        String uname="",pword="";

        @Override
        protected void onPreExecute() {
            // Create Show ProgressBar
            postUrl  = App.getInstance().SERVER_URI + "welcome/authen_business_site_json/?app_uname=" + uname + "&app_pword=" + pword;
        }

        protected String doInBackground(Void... urls)   {
            client.setConnectTimeout(30, TimeUnit.SECONDS);
            client.setReadTimeout(30,TimeUnit.SECONDS);
            String result = null;
            try {
                RequestBody formBody = new FormEncodingBuilder()
                        .add("app_uname",uname)
                        .add("app_pword",pword)
                        .build();
                Request request = new Request.Builder()
                        .url(postUrl)
                        .post(formBody)
                        .build();

                Response response = client.newCall(request).execute();
                return response.body().string();

            } catch (IOException e) {
                e.printStackTrace();
            }

            return result;
        }

        protected void onPostExecute(String result)  {



            try {
                JSONObject jsonObj = new JSONObject(result);
                if(jsonObj.getString("status").equals("success")) {

                    JSONArray arr = jsonObj.getJSONArray("data");
                    JSONObject obj = arr.getJSONObject(0);

                    App.getInstance().Id = obj.getString("id");
                    App.getInstance().LicenseKey = obj.getString("license_key");
                    App.getInstance().BusinessId = obj.getString("business_id");
                    App.getInstance().HeaderPage = obj.getString("header_slip");
                    App.getInstance().FootPage = obj.getString("footer_slip");

                    App.getInstance().Shift = "";

                    // update check or not checked
                    if(chkRememberMe.isChecked()) {
                        editor.putString(App.KEEP_USERNAME, edtUsername.getText().toString());
                        editor.putString(App.KEEP_PASSWORD, edtPassword.getText().toString());
                    }else{
                        editor.putString(App.KEEP_USERNAME,"");
                        editor.putString(App.KEEP_PASSWORD,"");
                    }

                    editor.commit();

                    // .End update check or not checked

                    finish();

                    Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                    startActivity(intent);
                }else{

                    new AlertDialog.Builder(LoginActivity.this)
                            .setTitle(getResources().getString(R.string.warnning_info))
                            .setMessage(getResources().getString(R.string.username_password_err))
                            .setNeutralButton(getResources().getString(R.string.close),
                                    new DialogInterface.OnClickListener() {

                                        @Override
                                        public void onClick(DialogInterface dialog,
                                                            int which) {
                                            dialog.dismiss();
                                        }
                                    })
                            .show();
                }

            } catch (Exception e) {
                e.printStackTrace();

                new AlertDialog.Builder(LoginActivity.this)
                        .setTitle(getResources().getString(R.string.warnning_info))
                        .setMessage(getResources().getString(R.string.network_process_err))
                        .setNeutralButton(getResources().getString(R.string.close),
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        dialog.dismiss();


                                    }
                                }).show();

                        ;
            }

        }

        OkHttpClient client = new OkHttpClient();

    }
}
