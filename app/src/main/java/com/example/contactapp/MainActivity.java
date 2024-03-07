package com.example.contactapp;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

    protected static ArrayList<Contact> contacts;
    ContactAdapter contactAdapter;
    ListView listView;
    FloatingActionButton addContact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        contacts = new ArrayList<Contact>();
        contactAdapter = new ContactAdapter(this, contacts);

        listView = findViewById(R.id.listView_contact);
        addContact = findViewById(R.id.button_addContact);

        listView.setOnItemClickListener(this);
        listView.setOnItemLongClickListener(this);
        listView.setAdapter(contactAdapter);

        contacts.add(new Contact("Name", "First name", "dezfdsfs", ""));
    }

    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        contacts.remove(position);
        contactAdapter.notifyDataSetChanged();
        return true; // enchaine avec un click court si false
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(this, AddEditContactActivity.class);
        intent.putExtra("viewMode", "view");
        intent.putExtra("position", position);
        intent.putExtra("contactInfos", contacts.get(position).getAll());
        intent.putExtra("contactAvatar", (Parcelable) contacts.get(position).getAvatar());
        contactView.launch(intent);
    }

    public void addNewContact(View view) {
        Intent intent = new Intent(this, AddEditContactActivity.class);
        intent.putExtra("viewMode", "create");
        contactView.launch(intent);
    }

    ActivityResultLauncher<Intent> contactView = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getData() != null){
                Intent intent = result.getData();
                String viewMode = intent.getStringExtra("viewMode");
                if (viewMode==null) viewMode = "create";

                // Refresh data if needed
                if (result.getResultCode() == RESULT_OK && intent.getBooleanExtra("needRefresh", true)) {
                    Collections.sort(contacts);
                    contactAdapter.notifyDataSetChanged();
                }
            }
        }
    });


}