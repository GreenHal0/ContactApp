package com.example.contactapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
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
        name.setText(list.get(position).getFirstName() +" "+ list.get(position).getName());
        phone.setText(list.get(position).getPhone());
        avatar.setImageURI(list.get(position).getAvatar());

        /*ImageView myImgView = layoutItem.findViewById(R.id.imageView);
        int resID = context.getResources().getIdentifier("ic_contact_"+list.get(position).getId(), "mipmap", context.getPackageName());
        if (resID != 0) myImgView.setImageResource(resID);
        else myImgView.setImageResource(R.mipmap.ic_contact_icon);*/


        return layoutItem;
    }

    public static class AddEditContactActivity extends AppCompatActivity {

        EditText name, firstName, phone, email;
        Button avatarButton, leftButton, rightButton;
        ImageView avatarView;
        Uri avatarUri;
        String viewMode;

        private static final int CAMERA_PERMISSION_CODE = 100;
        private static final int STORAGE_PERMISSION_CODE = 101;


        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_add_edit_contact);
            Intent intent = getIntent();
            viewMode = intent.getStringExtra("viewMode");
            if (viewMode==null) viewMode = "create";

            name = findViewById(R.id.editText_FirstName);
            firstName = findViewById(R.id.editText_Name);
            phone = findViewById(R.id.editText_Phone);
            email = findViewById(R.id.editText_Email);
            avatarButton = findViewById(R.id.button_Avatar);
            avatarView = findViewById(R.id.imageView_avatar);
            leftButton = findViewById(R.id.button_left);
            leftButton.setText("Cancel");
            rightButton = findViewById(R.id.button_right);
            rightButton.setText("Submit");

            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            avatarView.getLayoutParams().width = (int) (displayMetrics.widthPixels*0.66);
            avatarView.getLayoutParams().height = displayMetrics.heightPixels/5;

            if (viewMode.equals("edit") || viewMode.equals("view")) {
                String[] contactInfos = intent.getStringArrayExtra("contactInfos");
                Uri contactAvatar = intent.getParcelableExtra("contactAvatar");
                name.setText(contactInfos[0]);
                firstName.setText(contactInfos[1]);
                phone.setText(contactInfos[2]);
                email.setText(contactInfos[3]);
                avatarView.setImageURI(contactAvatar);

                if (viewMode.equals("view")) {
                    name.setKeyListener(null);
                    firstName.setKeyListener(null);
                    phone.setKeyListener(null);
                    email.setKeyListener(null);
                    avatarButton.setVisibility(View.GONE);
                    leftButton.setText("Delete Contact");
                    rightButton.setText("Ok");
                }
            }

        }

        public void checkPermission(String permission, int requestCode)
        {
            // Checking if permission is not granted
            if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_DENIED) {
                ActivityCompat.requestPermissions(this, new String[] { permission }, requestCode);
            }
            else {
                Toast.makeText(this, "Permission already granted", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onRequestPermissionsResult(int requestCode,
                                               @NonNull String[] permissions,
                                               @NonNull int[] grantResults)
        {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);

            if (requestCode == CAMERA_PERMISSION_CODE) {

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Camera Permission Granted", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(this, "Camera Permission Denied", Toast.LENGTH_SHORT).show();
                }
            }
            else if (requestCode == STORAGE_PERMISSION_CODE) {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Storage Permission Granted", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(this, "Storage Permission Denied", Toast.LENGTH_SHORT).show();
                }
            }
        }

        public void onChooseAvatar (View v){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Do you want to take a picture or choose existing one ?").setTitle("Avatar selection");
            builder.setPositiveButton("Take picture", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    selectAnAvatar.launch(takePicture);
                }
            });
            builder.setNeutralButton("Pick from galery", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    selectAnAvatar.launch(pickPhoto);
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
        ActivityResultLauncher<Intent> selectAnAvatar =
                registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK){
                            Intent intent = result.getData();
                            if (intent != null) {
                                avatarUri = intent.getData();
                                avatarView.setImageURI(avatarUri);
                                avatarButton.setVisibility(View.GONE);
                            }
                        }
                    }
                });


        public void onLeftButtonClick(View v) {
            Intent intent = new Intent();
            intent.putExtra("viewMode", viewMode);
            intent.putExtra("button", "left");
            intent.putExtra("position", getIntent().getIntExtra("position", -1));
            setResult(RESULT_OK, intent);
            finish();
        }

        public void onRightButtonClick(View v) {
            Intent intent = new Intent();
            intent.putExtra("button", "right");
            intent.putExtra("viewMode", viewMode);
            if (viewMode.equals("create") || viewMode.equals("edit")){
                intent.putExtra("viewMode", viewMode);
                intent.putExtra("position", getIntent().getIntExtra("position", 0));

                String[] infosContact = {name.getText().toString(), firstName.getText().toString(), phone.getText().toString(), email.getText().toString()};
                // Ajout à l'intent et envoies
                intent.putExtra("contactInfos", infosContact);
                intent.putExtra("avatar", avatarUri);
            }
            setResult(RESULT_OK, intent);
            finish();
        }
    }
}
