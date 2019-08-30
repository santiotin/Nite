package com.santiotin.nite;

import android.Manifest;
import android.database.Cursor;
import android.location.Location;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class ActivityPruebasAlex extends AppCompatActivity {

    TextView listaContactos;
    ArrayList<String> contactos;

    static public final int CONTACTS_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pruebas_alex);

        listaContactos = findViewById(R.id.textViewConactosPrueba);
        ArrayList<String> contactos;


    }

    public void llenarLista(){
        Toast.makeText(ActivityPruebasAlex.this, "Entra en llenarLista", Toast.LENGTH_LONG);
        Cursor cursor = getContentResolver().query( ContactsContract.Data.CONTENT_URI, new String[] {ContactsContract.Data.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER}, ContactsContract.CommonDataKinds.Phone.NUMBER + " IS NOT NULL", null, null);
        while (cursor.moveToNext()){
            String c = cursor.getString(1);
            contactos.add(c);
            listaContactos.append(c);
            listaContactos.append("\n");
        }

        mostrarContactos();

    }

    private void mostrarContactos() {


    }

    @Override
    public void onStart(){
        super.onStart();

        permisosContactos();

    }

    private void permisosContactos() {

        //Checking if the user has granted contacts permission for this app
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_CONTACTS) != PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS
            }, CONTACTS_REQUEST_CODE);

        }

        else{
            llenarLista();
        }
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case CONTACTS_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PERMISSION_GRANTED) {
                    //Permission Granted
                    llenarLista();
                } else
                    Toast.makeText(this, "Contacts Permission Denied", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}

