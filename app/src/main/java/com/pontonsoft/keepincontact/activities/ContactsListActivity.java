package com.pontonsoft.keepincontact.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.pontonsoft.keepincontact.R;
import com.pontonsoft.keepincontact.adaptaters.ContactsAdapter;
import com.pontonsoft.keepincontact.model.Contact;
import com.pontonsoft.keepincontact.data.ContactOpenDatabaseHelper;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Classe pour le listing des contacts enregistrés
 */
public class ContactsListActivity extends AppCompatActivity {

    private ArrayList<Contact> contactsSaved;
    private ContactOpenDatabaseHelper contactOpenDatabaseHelper;
    private Dao<Contact, Long> contactDao;

    /**
     * Initialisation l'arraylist des contacts
     */
    public ContactsListActivity() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts_list);

        // récupération des contacts dans la bdd
        try {
            this.getContacts();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Populer la WiewList
        this.populateViewList();

        EditText rechercher = (EditText) findViewById(R.id.search_contact);
        rechercher.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                try {
                    if (v.getText().toString().equals("")) {
                            getContacts();
                    } else {
                        getContacts(v.getText().toString());
                    }
                } catch (SQLException se) {
                    return false;
                }
                return true;
            }
        });
    }

    /**
     * Listener permettant de basculer dans la vue d'ajout d'un contact
     */
    View.OnClickListener addContact = new View.OnClickListener() {
        public void onClick(View v) {
            Intent intent = new Intent(ContactsListActivity.this, AddContactActivity.class);
            ContactsListActivity.this.startActivity(intent);
        }
    };

    /**
     * Méthode permettant de populer la ViewList des données de contact
     */
    private void populateViewList() {
        // Attache l'adaptateur à la Viewlist
        ContactsAdapter adapter = new ContactsAdapter(this, this.contactsSaved);


        ListView listView = (ListView) findViewById(R.id.list_contact);
        listView.setAdapter(adapter);

        // save form data
        Button addBtn = (Button) findViewById(R.id.add_contact);
        addBtn.setOnClickListener(addContact);
    }
    /*
    * Méthode permettant d'obtenir et d'afficher la liste de tous les objets Contact de la liste
     */
    private void getContacts() throws SQLException {
        getContacts(null);
    }
    /**
     * Méthode permettant d'obtenir et d'afficher la liste des objets Contact dans la liste dont
     * le prénom ou le nom commencent par start.
     * @throws SQLException
     */
    private void getContacts(String start) throws SQLException {
        contactsSaved = new ArrayList<Contact>();
        populateViewList();

        List<Contact> contacts;
        this.contactOpenDatabaseHelper = OpenHelperManager.getHelper(this, ContactOpenDatabaseHelper.class);
        this.contactDao = this.contactOpenDatabaseHelper.getDao();

        if (start != null) {
            start = "%" + start + "%";
            contacts = this.contactDao.queryBuilder().where().like("nom", start).or().like("prenom", start).query();
        }
        else {
            contacts = this.contactDao.queryForAll();
        }

        for (Contact contact : contacts) {
            this.contactsSaved.add(contact);
        }
        populateViewList();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_contacts_list, menu);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
