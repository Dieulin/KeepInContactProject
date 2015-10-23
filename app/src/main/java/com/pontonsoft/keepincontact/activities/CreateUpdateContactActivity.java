package com.pontonsoft.keepincontact.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.pontonsoft.keepincontact.R;
import com.pontonsoft.keepincontact.data.ContactOpenDatabaseHelper;
import com.pontonsoft.keepincontact.model.Contact;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;

/**
 * Classe pour l'enregistrement d'un contact
 */
public class CreateUpdateContactActivity extends AppCompatActivity {

    private ImageButton enregister;
    private ContactOpenDatabaseHelper contactOpenDatabaseHelper;
    private Dao<Contact, Long> contactDao;
    private Contact contact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);

        // rempli les champs si contact pas null
        if (getIntent().getSerializableExtra("contact") != null) {
            this.contact = (Contact)getIntent().getSerializableExtra("contact");

            // recuperation des données du formulaire
            EditText nom = (EditText) findViewById(R.id.add_nom);
            EditText prenom = (EditText) findViewById(R.id.add_prenom);
            EditText telephone = (EditText) findViewById(R.id.add_telephone);
            EditText dateNaissance = (EditText) findViewById(R.id.add_date_de_naissance);
            EditText adresse = (EditText) findViewById(R.id.add_adresse);

            // Populate the data into the template view using the data object
            nom.setText(contact.getNom());
            prenom.setText(contact.getPrenom());
            telephone.setText(contact.getTelephone());
            adresse.setText(contact.getAdresse());

            //dateNaissance.setText(Date.(contact.getDateBirth()));

            //
            this.setTitle(contact.getPrenom() + " " + contact.getNom());
        }

        // sauvegarde des données du formulaire
        this.enregister = (ImageButton) findViewById(R.id.add_enregistrer);
        this.enregister.setOnClickListener(saveToDB);
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
            contactOpenDatabaseHelper = OpenHelperManager.getHelper(CreateUpdateContactActivity.this, ContactOpenDatabaseHelper.class);
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

            String msg = "";

            try {
                if (contact != null) {
                    msg = "Contact modifié";
                    contactDao.update(new Contact(contact.getId(), prenom.getText().toString(), nom.getText().toString(), dateBirth, currDateTime, telephone.getText().toString(), adresse.getText().toString()));
                } else {
                    msg = "Contact Créé";
                    contactDao.create(new Contact(prenom.getText().toString(), nom.getText().toString(), dateBirth, currDateTime, telephone.getText().toString(), adresse.getText().toString()));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            // message à l'utilisateur
            Toast.makeText(CreateUpdateContactActivity.this, msg, Toast.LENGTH_LONG).show();

            // Finish activity
            CreateUpdateContactActivity.this.finish();
        }
    };
}
