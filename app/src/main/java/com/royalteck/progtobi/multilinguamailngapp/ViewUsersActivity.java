package com.royalteck.progtobi.multilinguamailngapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class ViewUsersActivity extends AppCompatActivity {
    ArrayAdapter arrayAdapter;
    ArrayList<String> list_of_rooms = new ArrayList<>();
    ListView listuser;
    ProgressBar fetuserpb;
    private DatabaseReference root = FirebaseDatabase.getInstance().getReference().getRoot();
    FirebaseAuth mAuth;
    final String[] mine = {""};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_users);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        SharedPreferences mydata = ViewUsersActivity.this.getSharedPreferences("DATA", 0);
        final String sender = mydata.getString("name", "");
        fetuserpb = (ProgressBar) findViewById(R.id.fetuserpb);
        listuser = (ListView) findViewById(R.id.listroom);
        arrayAdapter = new ArrayAdapter(ViewUsersActivity.this, android.R.layout.simple_list_item_1,
                list_of_rooms);
        listuser.setAdapter(arrayAdapter);

        fetuserpb.setVisibility(View.VISIBLE);
        root.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Set<String> set = new HashSet<String>();
                Iterator i = dataSnapshot.getChildren().iterator();
                while (i.hasNext()) {
                    String person = ((DataSnapshot) i.next()).getKey();
                    if ((sender.equals(person))) {
                        mine[0] = person;
                    } else {
                        set.add(person);
                    }

                }

                list_of_rooms.clear();
                list_of_rooms.addAll(set);
                arrayAdapter.notifyDataSetChanged();
                fetuserpb.setVisibility(View.GONE);
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        listuser.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String room_name;
                room_name = adapterView.getItemAtPosition(i).toString();
                Intent intent = new Intent(ViewUsersActivity.this, ChatRoom.class);
                intent.putExtra("roomname", room_name);
                startActivity(intent);

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
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
            Intent intent = new Intent(ViewUsersActivity.this, LoginActivity.class);
            ViewUsersActivity.this.finish();
            startActivity(intent);

        }

        if (id == R.id.inbox) {
            Intent intent = new Intent(ViewUsersActivity.this, ChatRoom.class);
            intent.putExtra("roomname", mine[0]);
            startActivity(intent);

        }

        return super.onOptionsItemSelected(item);
    }

}
