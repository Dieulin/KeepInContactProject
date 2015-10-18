package com.pontonsoft.keepincontact.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.pontonsoft.keepincontact.R;
import com.pontonsoft.keepincontact.data.ContactOpenDatabaseHelper;
import com.pontonsoft.keepincontact.model.Contact;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Classe pour l'enregistrement d'un contact
 */
public class AddContactActivity extends AppCompatActivity {

    private Button enregistrer;
    private ContactOpenDatabaseHelper contactOpenDatabaseHelper;
    private Dao<Contact, Long> contactDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);

        // sauvegarde des données du formulaire
        this.enregistrer = (Button) findViewById(R.id.add_enregistrer);
        this.enregistrer.setOnClickListener(saveToDB);
    }

    /**
     * Listener permettant de recuperer les données du formulaire et de les enregistrer dans la BDD
     */
    // TODO : Gérer la date de naissance
    View.OnClickListener saveToDB = new View.OnClickListener() {
        public void onClick(View v) {
            // recuperation des données du formulaire
            EditText nom = (EditText) findViewById(R.id.add_nom);
            EditText prenom = (EditText) findViewById(R.id.add_prenom);
            EditText telephone = (EditText) findViewById(R.id.add_telephone);
            EditText dateNaissance = (EditText) findViewById(R.id.add_date_de_naissance);
            EditText adresse = (EditText) findViewById(R.id.add_adresse);

            // sauvegarde en bdd
            contactOpenDatabaseHelper = OpenHelperManager.getHelper(AddContactActivity.this, ContactOpenDatabaseHelper.class);
            contactDao = null;

            try {
                contactDao = contactOpenDatabaseHelper.getDao();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            Date currDateTime = new Date(System.currentTimeMillis());

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(currDateTime);
            calendar.add(Calendar.DATE, 12);
            calendar.add(Calendar.MONTH, 10);
            calendar.add(Calendar.YEAR, 2015);
            Date dateBirth = calendar.getTime();

            try {
                contactDao.create(new Contact(prenom.getText().toString(), nom.getText().toString(), dateBirth, currDateTime, telephone.getText().toString(), adresse.getText().toString()));
            } catch (SQLException e) {
                e.printStackTrace();
            }

            // message à l'utilisateur
            Toast.makeText(AddContactActivity.this, "Contact ajouté avec succès", Toast.LENGTH_LONG).show();

            // retour à la liste
            returnToList();
        }
    };

    private void returnToList() {
        Intent intent = new Intent(AddContactActivity.this, ContactsListActivity.class);
        AddContactActivity.this.startActivity(intent);
    }
}
