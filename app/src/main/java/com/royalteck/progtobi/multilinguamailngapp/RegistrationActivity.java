package com.royalteck.progtobi.multilinguamailngapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import Tasks.Mysingleton;

public class RegistrationActivity extends AppCompatActivity {

    // private FirebaseAuth mAuth;
    EditText Email, Password, displayname;
    Button signup;
    ProgressBar registerpb;
    String serverUrl = "http://elearningapp.eu5.org/multilingua_register.php";
    private DatabaseReference root = FirebaseDatabase.getInstance().getReference().getRoot();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        //mAuth = FirebaseAuth.getInstance();
        Email = (EditText) findViewById(R.id.regemailedittxt);
        Password = (EditText) findViewById(R.id.regpasswordedittxt);
        displayname = (EditText) findViewById(R.id.displayname);
        signup = (Button) findViewById(R.id.registerbtn);
        registerpb = (ProgressBar) findViewById(R.id.regpb);
        signup.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (!validateForm()) {
                    return;
                }
                registerpb.setVisibility(View.VISIBLE);

                final String email = Email.getText().toString();
                final String password = Password.getText().toString();
               /* mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(RegistrationActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                // If sign in fails, display a message to the user. If sign in succeeds
                                // the auth state listener will be notified and logic to handle the
                                // signed in user can be handled in the listener.
                                if (!task.isSuccessful()) {
                                    registerpb.setVisibility(View.GONE);
                                    Toast.makeText(RegistrationActivity.this, task.getException().getMessage(),
                                            Toast.LENGTH_LONG).show();
                                    registerpb.setVisibility(View.GONE);
                                } else {
                                    Map<String, Object> map = new HashMap<String, Object>();
                                    map.put(displayname.getText().toString(), "");
                                    root.updateChildren(map);
                                    registerpb.setVisibility(View.GONE);
                                }

                            }
                        });
*/


                StringRequest stringRequest = new StringRequest(Request.Method.POST, serverUrl, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            JSONObject jsonObject = jsonArray.getJSONObject(0);
                            String code = jsonObject.getString("code");
                            if (code.equals("reg_failed")) {
                                String message = jsonObject.getString("message");
                                registerpb.setVisibility(View.GONE);
                                Toast.makeText(RegistrationActivity.this, message, Toast.LENGTH_LONG).show();
                            } else if (code.equals("reg_success")) {
                                String message = jsonObject.getString("message");
                                Map<String, Object> map = new HashMap<String, Object>();
                                map.put(displayname.getText().toString(), "");
                                root.updateChildren(map);
                                Email.setText("");
                                Password.setText("");
                                displayname.setText("");
                                registerpb.setVisibility(View.GONE);


                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(RegistrationActivity.this, "Error Registering... Try Again", Toast.LENGTH_SHORT).show();
                        registerpb.setVisibility(View.GONE);
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("email", email);
                        params.put("password", password);
                        params.put("displayname", displayname.getText().toString());


                        return params;
                    }


                };

                Mysingleton.getInstance(RegistrationActivity.this).addtorequestque(stringRequest);

            }
        });
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

        String dpname = displayname.getText().toString();
        if (TextUtils.isEmpty(dpname)) {
            displayname.setError("Required.");
            valid = false;
        } else {
            displayname.setError(null);
        }

        return valid;
    }

}
