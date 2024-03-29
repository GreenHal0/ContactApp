package com.example.contactapp;

import static android.app.PendingIntent.getActivity;

import static com.example.contactapp.MainActivity.contacts;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;

public class AddEditContactActivity extends AppCompatActivity {

    EditText name, firstName, phone, email;
    Button avatarButton, leftButton, rightButton;
    ImageView avatarView;
    Bitmap avatarBitmap;
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

        name = findViewById(R.id.editText_Name);
        firstName = findViewById(R.id.editText_FirstName);
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
            String[] contactInfos = contacts.get(intent.getIntExtra("position", 0)).getAll();
            Bitmap contactAvatar = contacts.get(intent.getIntExtra("position", 0)).getAvatar();
            name.setText(contactInfos[0]);
            firstName.setText(contactInfos[1]);
            phone.setText(contactInfos[2]);
            email.setText(contactInfos[3]);
            if (contactAvatar!=null)
                avatarView.setImageBitmap(contactAvatar);
            else
                avatarView.setImageBitmap(BitmapFactory.decodeResource(this.getResources(), R.mipmap.ic_contact_icon));

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
    public void onChooseAvatar (View v){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Do you want to take a picture or choose existing one ?").setTitle("Avatar selection");
        builder.setPositiveButton("Pick from galery", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Intent pickPhoto = new Intent(Intent.ACTION_OPEN_DOCUMENT, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                selectAnAvatar.launch(pickPhoto);
            }
        });
        builder.setNeutralButton("Take picture", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                selectAnAvatar.launch(takePicture);
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
    ActivityResultLauncher<Intent> selectAnAvatar =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @SuppressLint("WrongConstant")
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK){
                        Intent intent = result.getData();
                        if (intent != null) {
                            try {
                                avatarBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), intent.getData());
                                avatarView.setImageBitmap(avatarBitmap);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                            avatarButton.setVisibility(View.GONE);
                        }
                    }
                }
            });

    /*
    return true if the necessary fields has been filled
     */
    public boolean requiredInformationContact() {
        return !name.getText().toString().isEmpty() && !phone.getText().toString().isEmpty();
    }


    public void onLeftButtonClick(View v) {
        Intent intent = new Intent();
        if (viewMode.equals("edit") || viewMode.equals("view"))
            contacts.remove(getIntent().getIntExtra("position", 0));
        setResult(RESULT_OK, intent);
        finish();
    }

    public void onRightButtonClick(View v) {
        Intent intent = new Intent();
        if (viewMode.equals("create") || viewMode.equals("edit")){
            if (requiredInformationContact()) {
                Contact newContact = new Contact(name.getText().toString(), firstName.getText().toString(), phone.getText().toString(), email.getText().toString(), avatarBitmap);
                if (avatarBitmap != null)
                    newContact.setAvatar(avatarBitmap);
                if (viewMode.equals("create"))
                    contacts.add(newContact);
                else if (viewMode.equals("edit"))
                    contacts.set(getIntent().getIntExtra("position", 0), newContact);
                intent.putExtra("needRefresh", true);
                setResult(RESULT_OK, intent);
                finish();
            }
            else
                Toast.makeText(this, "Missing informations to create a contact", Toast.LENGTH_SHORT).show();
        }
        else if (viewMode.equals("view")) {
            intent.putExtra("needRefresh", false);
            setResult(RESULT_OK, intent);
            finish();
        }
    }
}