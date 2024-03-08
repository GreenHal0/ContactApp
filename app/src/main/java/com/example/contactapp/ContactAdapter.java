package com.example.contactapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;

public class ContactAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Contact> list;

    public ContactAdapter(Context context, ArrayList<Contact> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ConstraintLayout layoutItem;
        LayoutInflater mInflater = LayoutInflater.from(context);
        //(1) Réutilisation du layout
        if (convertView == null)
            layoutItem = (ConstraintLayout) mInflater.inflate(R.layout.contact_layout, parent, false);
        else
            layoutItem = (ConstraintLayout) convertView;

        //(2) Récupération des TextView de notre layout
        TextView name = layoutItem.findViewById(R.id.textView_ContactFullName);
        TextView phone = layoutItem.findViewById(R.id.textView_ContactPhone);
        ImageView avatar = layoutItem.findViewById(R.id.imageView_ContactAvatar);

        //(3) Mise à jour des valeurs;
        name.setText(list.get(position).getFirstName() + " " + list.get(position).getName());
        phone.setText(list.get(position).getPhone());
        if (list.get(position).getAvatar() != null)
            avatar.setImageBitmap(list.get(position).getAvatar());
        else
            avatar.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_contact_icon));

        return layoutItem;
    }
}