package com.royalteck.progtobi.multilinguamailngapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import Tasks.Mysingleton;

public class LoginActivity extends AppCompatActivity {

    // private static final String API_KEY = "AIzaSyAaMmkiZw2dabEZnb4GykUJ5Yto12LjEXw";
    EditText Email, Password;
    TextView signup;
    Button Login;
    private FirebaseAuth mAuth;
    ProgressBar loginpb;
    String login_url = "http://elearningapp.eu5.org/multilingua_login.php";
    //AlertDialog.Builder builder;
// ...


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Email = (EditText) findViewById(R.id.emailedittxt);
        Password = (EditText) findViewById(R.id.passwordedittxt);
        Login = (Button) findViewById(R.id.loginbtn);
        signup = (TextView) findViewById(R.id.signup);
        //builder = new AlertDialog.Builder(LoginActivity.this);
        loginpb = (ProgressBar) findViewById(R.id.loginpb);
        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!validateForm()) {
                    return;
                }
                /*loginpb.setVisibility(View.VISIBLE);
                mAuth = FirebaseAuth.getInstance();
                String email = Email.getText().toString();
                String password = Password.getText().toString();
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (!task.isSuccessful()) {
                                    loginpb.setVisibility(View.GONE);
                                    Toast.makeText(LoginActivity.this, task.getException().getMessage(),
                                            Toast.LENGTH_LONG).show();
                                }else{
                                    loginpb.setVisibility(View.GONE);
                                    Intent intent = new Intent(LoginActivity.this, ViewUsersActivity.class);
                                    startActivity(intent);
                                }
                            }
                        });*/

                final String username = Email.getText().toString();
                final String password = Password.getText().toString();
                if (!isOnline(LoginActivity.this)) {
                    Toast.makeText(LoginActivity.this, "No network connection", Toast.LENGTH_LONG).show();
                    return;
                }
                if (!validateForm()) {
                    return;
                }
                loginpb.setVisibility(View.VISIBLE);
                StringRequest stringRequest = new StringRequest(Request.Method.POST, login_url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            JSONObject jsonObject = jsonArray.getJSONObject(0);
                            String code = jsonObject.getString("code");
                            if (code.equals("login_failed")) {
                                //builder.setTitle("Login Error");
                                //displayAlert(jsonObject.getString("message"));
                                Toast.makeText(LoginActivity.this, jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                                loginpb.setVisibility(View.GONE);
                            } else if (code.equals("login_success")) {
                                String displayname = jsonObject.getString("displayname");
                                Intent intent = new Intent(LoginActivity.this, ViewUsersActivity.class);
                                SharedPreferences mydata = LoginActivity.this.getSharedPreferences("DATA", 0);
                                SharedPreferences.Editor editor = mydata.edit();
                                editor.putString("name", displayname);
                                //intent.putExtra("displayname", displayname);
                                editor.commit();
                                LoginActivity.this.finish();
                                startActivity(intent);


                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(LoginActivity.this, "Error", Toast.LENGTH_LONG).show();
                        error.printStackTrace();
                        loginpb.setVisibility(View.GONE);

                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("username", username);
                        params.put("password", password);
                        return params;
                    }
                };
                Mysingleton.getInstance(LoginActivity.this).addtorequestque(stringRequest);
            }

            private boolean isOnline(Context mContext) {
                ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo netInfo = cm.getActiveNetworkInfo();
                if (netInfo != null && netInfo.isConnectedOrConnecting()) {
                    return true;
                }
                return false;
            }

        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent in = new Intent(LoginActivity.this, RegistrationActivity.class);
                startActivity(in);
            }
        });
       /* final TextView textView = (TextView) findViewById(R.id.text_view);
        final Handler textViewHandler = new Handler();*/
    }

    private boolean validateForm() {
        boolean valid = true;

        String email = Email.getText().toString();
        if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Email.setError("Required.");
            valid = false;
        } else {
            Email.setError(null);
        }

        String password = Password.getText().toString();
        if (TextUtils.isEmpty(password)) {
            Password.setError("Required.");
            valid = false;
        } else {
            Password.setError(null);
        }

        return valid;
    }

    /*public void displayAlert(String message) {
        builder.setMessage(message);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Email.setText("");
                Password.setText("");
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.setCancelable(false);
        alertDialog.show();
    }*/
}
