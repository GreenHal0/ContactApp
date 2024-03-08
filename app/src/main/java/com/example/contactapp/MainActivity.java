package com.example.contactapp;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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

        contacts.add(new Contact("Name", "Firstname", "555-3612", "email@example.fr"));
    }

    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Do you want to remove "+contacts.get(position).getName()+" ?").setTitle("Remove contact");
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                contacts.remove(position);
                contactAdapter.notifyDataSetChanged();
            }
        });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {}
        });
        AlertDialog dialog = builder.create();
        dialog.show();
        return true; // enchaine avec un click court si false
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(this, AddEditContactActivity.class);
        intent.putExtra("viewMode", "view");
        intent.putExtra("position", position);
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