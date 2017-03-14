package com.royalteck.progtobi.multilinguamailngapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import Adapter.MessageAdapter;
import Modal.ChatMessage;

public class ChatRoom extends AppCompatActivity {
    String Language = "", Roomname, sendperson = "";
    FirebaseAuth mAuth;
    private static final String API_KEY = "AIzaSyAaMmkiZw2dabEZnb4GykUJ5Yto12LjEXw";
    TextView user, msgtime, msg_english, msg_chinese;
    private String User, Msgtime, Msg_english, Msg_chinese;
    ArrayList<ChatMessage> arrayList = new ArrayList<>();
    EditText msg;
    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;
    RecyclerView.LayoutManager layoutManager;

    private DatabaseReference root;
    private String temp_key;
    String sender = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        user = (TextView) findViewById(R.id.message_user);
        msg = (EditText) findViewById(R.id.msgtxt);
        msgtime = (TextView) findViewById(R.id.message_time);
        msg_chinese = (TextView) findViewById(R.id.message_textch);
        msg_english = (TextView) findViewById(R.id.message_texteng);


        Roomname = getIntent().getExtras().getString("roomname");
        SharedPreferences mydata = ChatRoom.this.getSharedPreferences("DATA", 0);
        sender = mydata.getString("name", "");


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(Roomname);

        FloatingActionButton fab =
                (FloatingActionButton) findViewById(R.id.fab);


        root = FirebaseDatabase.getInstance().getReference().child(Roomname);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!validateForm()) {
                    return;
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(ChatRoom.this);

                builder.setTitle("Language Selection");

                LayoutInflater inflater = ChatRoom.this.getLayoutInflater();
                builder.setView(inflater.inflate(R.layout.dialog_language, null));

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        AlertDialog dlg = (AlertDialog) dialog;
                        if (Language.isEmpty()) {
                            Toast.makeText(ChatRoom.this, "Please Select Language", Toast.LENGTH_LONG).show();
                        } else {

                            if (Language.equals("chinese")) {
                                final String texttranslate = msg.getText().toString();
                                final Handler textViewHandler = new Handler();
                                new AsyncTask<Void, Void, Void>() {
                                    @Override
                                    protected Void doInBackground(Void... params) {
                                        TranslateOptions options = TranslateOptions.newBuilder()
                                                .setApiKey(API_KEY)
                                                .build();
                                        Translate translate = options.getService();
                                        final Translation translation =
                                                translate.translate(texttranslate,
                                                        Translate.TranslateOption.targetLanguage("zh-CN"));
                                        textViewHandler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                if (msg != null) {
                                                    msg.setText(translation.getTranslatedText());
                                                    Map<String, Object> map = new HashMap<String, Object>();
                                                    temp_key = root.push().getKey();
                                                    root.updateChildren(map);

                                                    DatabaseReference message_root = root.child(temp_key);
                                                    Map<String, Object> map2 = new HashMap<String, Object>();
                                                    map2.put("name", sender);
                                                    //map2.put("msgtime", new Date().getTime());
                                                    map2.put("msgenglish", texttranslate);
                                                    map2.put("msgchinese", msg.getText().toString());
                                                    message_root.updateChildren(map2);
                                                    msg.setText("");
                                                }
                                            }
                                        });
                                        return null;
                                    }
                                }.execute();
                            } else if (Language.equals("english")) {
                                final String texttranslate = msg.getText().toString();
                                final Handler textViewHandler = new Handler();
                                new AsyncTask<Void, Void, Void>() {
                                    @Override
                                    protected Void doInBackground(Void... params) {
                                        TranslateOptions options = TranslateOptions.newBuilder()
                                                .setApiKey(API_KEY)
                                                .build();
                                        Translate translate = options.getService();
                                        final Translation translation =
                                                translate.translate(texttranslate,
                                                        Translate.TranslateOption.targetLanguage("en"));
                                        textViewHandler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                if (msg != null) {
                                                    msg.setText(translation.getTranslatedText());
                                                    Map<String, Object> map = new HashMap<String, Object>();
                                                    temp_key = root.push().getKey();
                                                    root.updateChildren(map);

                                                    DatabaseReference message_root = root.child(temp_key);
                                                    Map<String, Object> map2 = new HashMap<String, Object>();
                                                    map2.put("name", sender);
                                                    map2.put("msgenglish", msg.getText().toString());
                                                    map2.put("msgchinese", texttranslate);
                                                    message_root.updateChildren(map2);
                                                    msg.setText("");
                                                }
                                            }
                                        });
                                        return null;
                                    }
                                }.execute();


                            }
                        }


                        // TODO: sign in to Firebase

                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Language = "";
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();

            }
        });

        root.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                append_chat_conversation(dataSnapshot);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                append_chat_conversation(dataSnapshot);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void append_chat_conversation(DataSnapshot dataSnapshot) {
        Iterator i = dataSnapshot.getChildren().iterator();
        while (i.hasNext()) {
            Msg_chinese = (String) ((DataSnapshot) i.next()).getValue();
            Msg_english = (String) ((DataSnapshot) i.next()).getValue();
            sendperson = (String) ((DataSnapshot) i.next()).getValue();
            if (sendperson.equals(Roomname) || sendperson.equals(sender)) {
                ChatMessage message = new ChatMessage();
                message.setMessagechi(Msg_chinese);
                message.setMessageeng(Msg_english);
                message.setMessageUser(sendperson);
                arrayList.add(message);

                recyclerView = (RecyclerView) findViewById(R.id.msgviewrecycler);
                layoutManager = new LinearLayoutManager(ChatRoom.this);
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setHasFixedSize(true);
                adapter = new MessageAdapter(ChatRoom.this, arrayList);
                recyclerView.setAdapter(adapter);
            }


        }
    }

    private boolean validateForm() {
        boolean valid = true;


        String message = msg.getText().toString();
        if (TextUtils.isEmpty(message)) {
            msg.setError("Empty Message.");
            valid = false;
        } else {
            msg.setError(null);
        }

        return valid;
    }

    public void selectSex(View view) {
        boolean checked = ((RadioButton) view).isChecked();
        switch (view.getId()) {
            case R.id.english:
                if (checked) {
                    Language = "english";
                } else
                    Language = "";
                break;

            case R.id.chinese:
                if (checked) {
                    Language = "chinese";
                } else
                    Language = "";
                break;

        }

    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_chatroom, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(ChatRoom.this, LoginActivity.class);
            ChatRoom.this.finish();
            startActivity(intent);

        }

        return super.onOptionsItemSelected(item);
    }*/

}
