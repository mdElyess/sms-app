package com.example.helpmeapp;

import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private EditText etPhoneNumber, etCustomMessage;
    private Button btnAddContact, btnSendAlert;
    private RecyclerView rvContacts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Demander les permissions
        requestPermissions();

        // Liaison avec les éléments de l'interface
        etPhoneNumber = findViewById(R.id.et_phone_number);
        etCustomMessage = findViewById(R.id.et_custom_message);
        btnAddContact = findViewById(R.id.btn_add_contact);
        btnSendAlert = findViewById(R.id.btn_send_alert);
        rvContacts = findViewById(R.id.rv_contacts);

        List<String> contactList = new ArrayList<>();
        ContactAdapter adapter = new ContactAdapter(contactList);
        rvContacts.setLayoutManager(new LinearLayoutManager(this));
        rvContacts.setAdapter(adapter);

        btnAddContact.setOnClickListener(v -> {
            String phoneNumber = etPhoneNumber.getText().toString();
            if (!phoneNumber.isEmpty()) {
                contactList.add(phoneNumber);
                adapter.notifyDataSetChanged();
                etPhoneNumber.setText(""); // Réinitialiser le champ
            } else {
                Toast.makeText(this, "Veuillez entrer un numéro de téléphone", Toast.LENGTH_SHORT).show();
            }
        });

        Button panicButton = findViewById(R.id.btn_send_alert);
        panicButton.setOnClickListener(view -> {
            EditText phoneEditText = findViewById(R.id.et_phone_number);
            String phoneNumber = phoneEditText.getText().toString();

            if (!phoneNumber.isEmpty()) {
                String message = "Alerte ! Voici ma localisation :";
                getLocationAndSendSMS(phoneNumber, message);
            } else {
                Toast.makeText(this, "Veuillez entrer un numéro de téléphone", Toast.LENGTH_SHORT).show();
            }
        });

        EditText phoneEditText = findViewById(R.id.et_phone_number);

        panicButton.setOnClickListener(view -> {
            String phoneNumber = phoneEditText.getText().toString();

            if (!phoneNumber.isEmpty()) {
                // Message personnalisé ou fixe
                String message = "Alerte d'urgence! Voici ma localisation: https://maps.google.com/?q=latitude,longitude";

                // Envoie le SMS
                sendSMS(phoneNumber, message);
            } else {
                Toast.makeText(this, "Veuillez entrer un numéro de téléphone", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void requestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED || checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.SEND_SMS, android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }
    }

    private void getLocationAndSendSMS(String phoneNumber, String message) {
        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();

                String fullMessage = message + "\nLocalisation : https://maps.google.com/?q=" + latitude + "," + longitude;

                sendSMS(phoneNumber, fullMessage);
            } else {
                Toast.makeText(this, "Impossible d'obtenir la localisation", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Erreur lors de la récupération de la localisation", Toast.LENGTH_SHORT).show();
        });
    }

    private void sendSMS(String phoneNumber, String message) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNumber, null, message, null, null);
            Toast.makeText(this, "SMS envoyé avec succès", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Erreur lors de l'envoi du SMS", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }




}