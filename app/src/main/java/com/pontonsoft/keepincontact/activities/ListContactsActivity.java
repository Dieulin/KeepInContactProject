package com.pontonsoft.keepincontact.activities;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.pontonsoft.keepincontact.R;
import com.pontonsoft.keepincontact.adaptaters.ContactsAdapter;
import com.pontonsoft.keepincontact.model.Contact;
import com.pontonsoft.keepincontact.data.ContactOpenDatabaseHelper;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Classe pour le listing des contacts enregistrés
 */
public class ListContactsActivity extends AppCompatActivity {

    private ArrayList<Contact> contactsSaved;
    private ContactOpenDatabaseHelper contactOpenDatabaseHelper;
    private Dao<Contact, Long> contactDao;
    private ListView listView;
    private ContactsAdapter adapter;

    private Timer timer;
    private TimerTask timerTask;
    final Handler handler = new Handler();
    private LocationManager locationManager;

    /**
     * Initialisation l'arraylist des contacts
     */
    public ListContactsActivity() {
        this.contactsSaved = new ArrayList<Contact>();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts_list);

        // recuperation de tous les contacts dans la bdd
        try {
            this.getContacts("");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // enregistrer le menu contextuel dans la listview
        this.listView = (ListView) findViewById(R.id.list_contact);
        registerForContextMenu(this.listView);

        // Populer la WiewList
        this.populateViewList();

        // set contact click listview item listener
        this.listView.setOnItemClickListener(updateContact);

        // Find contacts distance
        ImageButton findBtn = (ImageButton) findViewById(R.id.find_contacts);
        findBtn.setOnClickListener(findContacts);

        // recherche
        final EditText recherche = (EditText)findViewById(R.id.search_contact);
        recherche.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                // clear viewlist
                adapter.clear();

                // get contacts by keyword
                try {
                    getContacts(recherche.getText().toString());
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                return false;
            }
        });

        /*ActionBar actionBar = getActionBar();
        actionBar.setSubtitle("Gestion des contacts");*/

    }

    /**
     * Listener permettant de basculer dans la vue d'ajout d'un contact
     */
    View.OnClickListener findContacts = new View.OnClickListener() {
        public void onClick(View v) {

            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

                // if there are contacts
                if (contactsSaved != null && contactsSaved.size() > 0) {
                    Geocoder coder = new Geocoder(ListContactsActivity.this, Locale.getDefault());
                    LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

                    List<Address> target_address_list = null;

                    // loop by contacts
                    Iterator<Contact> contact_iterator = contactsSaved.iterator();
                    ArrayList<String> contacts_founded = new ArrayList<String>();
                    Contact contact;

                    //get rayon
                    SeekBar seekBar = (SeekBar) findViewById(R.id.distance_in_meter);
                    int distance = seekBar.getProgress();

                    while (contact_iterator.hasNext()) {

                        // get current contact address
                        contact = contact_iterator.next();
                        try {
                            target_address_list = coder.getFromLocationName(contact.getAdresse(), 5);
                        } catch (IOException ioe) {
                            ioe.printStackTrace();
                        }

                        // check if the addresses is not null or empty
                        if (target_address_list != null && !target_address_list.isEmpty()) {

                            Address address;
                            Location source = null, target;

                            try {
                                source = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            // get target long & lat
                            address = target_address_list.get(0);

                            target = new Location(LocationManager.GPS_PROVIDER);
                            target.setLatitude(address.getLatitude());
                            target.setLongitude(address.getLongitude());

                            if (target.distanceTo(source) <= distance) {
                                contacts_founded.add(contact.getNom() + " " + contact.getPrenom());
                            }
                        }
                    }

                    if (contacts_founded.size() > 0) {
                        Iterator<String> string_iterator = contacts_founded.iterator();
                        String contacts = "";
                        while (string_iterator.hasNext()) {

                            contacts += string_iterator.next() + ", ";
                        }

                        Toast.makeText(ListContactsActivity.this, "Vous êtes à moins de " + String.valueOf(distance) + "m de " + contacts.substring(0, contacts.length() - 2), Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(ListContactsActivity.this, "Aucun contact dans le rayon de moins de " + String.valueOf(distance) + "m", Toast.LENGTH_LONG).show();
                    }
                }
            } else {
                Toast.makeText(ListContactsActivity.this, "Veuillez activer votre GPS d'abord", Toast.LENGTH_LONG).show();
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        adapter.clear();
        try {
            this.getContacts("");
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.adapter.notifyDataSetChanged();
    }

    /**
     * Lstener permettant de bsculer dans la vue de modification d'un contact
     */
    AdapterView.OnItemClickListener updateContact = new AdapterView.OnItemClickListener(){
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            Contact selected_contact = (Contact) adapter.getItem(position);
            // go to edit view passing the selected contact
            Intent intent = new Intent(ListContactsActivity.this, CreateUpdateContactActivity.class);
            intent.putExtra("contact", selected_contact);
            ListContactsActivity.this.startActivity(intent);
        }
    };

    /**
     * Listener permettant de basculer dans la vue d'ajout d'un contact
     */
    View.OnClickListener addContact = new View.OnClickListener() {
        public void onClick(View v) {
            Intent intent = new Intent(ListContactsActivity.this, CreateUpdateContactActivity.class);
            ListContactsActivity.this.startActivity(intent);
        }
    };

    /**
     * Méthode permettant de populer la ViewList des données de contact
     */
    private void populateViewList() {
        // Attache l'adaptateur à la Viewlist
        this.adapter = new ContactsAdapter(this, this.contactsSaved);
        this.listView.setAdapter(adapter);

        // save form data
        ImageButton addBtn = (ImageButton) findViewById(R.id.add_contact);
        addBtn.setOnClickListener(addContact);
    }

    /**
     * Méthode permettant d'obtenir la liste des objet Contact dans la liste
     * @throws SQLException
     */
    private void getContacts(String keyword) throws SQLException {
        List<Contact> contacts;

        this.contactOpenDatabaseHelper = OpenHelperManager.getHelper(this, ContactOpenDatabaseHelper.class);
        this.contactDao = this.contactOpenDatabaseHelper.getDao();

        // get contacts by keyword
        if (keyword != null && !keyword.isEmpty()) {
            contacts = this.contactDao.queryBuilder().where().like("nom", "%" + keyword + "%").or().like("prenom", "%" + keyword + "%").query();
        }
        else {
            // get all contacts
            contacts = this.contactDao.queryForAll();
        }

        for (Contact contact : contacts) {
            this.contactsSaved.add(contact);
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.contact_context_menu, menu);

        menu.setHeaderTitle("Options");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        int selected_menu_item_id = item.getItemId();
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int contact_position_on_the_list = info.position;
        Contact selected_contact = (Contact)this.listView.getItemAtPosition(contact_position_on_the_list);

        switch (selected_menu_item_id) {
            case R.id.delete_contact:

                if (this.deleteContact(selected_contact) > 0) {
                    // refresh the listview
                    this.adapter.remove(selected_contact);
                    this.adapter.notifyDataSetChanged();

                    // message à l'utilisateur
                    Toast.makeText(ListContactsActivity.this, "Contact supprimé", Toast.LENGTH_LONG).show();
                }

                return true;

            case R.id.map_contact:

                Intent intent = new Intent(ListContactsActivity.this, MapContactActivity.class);
                intent.putExtra("contact", selected_contact);
                ListContactsActivity.this.startActivity(intent);
                return true;

            default:
                return super.onContextItemSelected(item);
        }
    }

    private int deleteContact (Contact contact) {

        contactOpenDatabaseHelper = OpenHelperManager.getHelper(ListContactsActivity.this, ContactOpenDatabaseHelper.class);
        int nbLignesDeleted = 0;

        try {
            contactDao = contactOpenDatabaseHelper.getDao();
            nbLignesDeleted = contactDao.deleteById(Long.valueOf(contact.getId()));
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return nbLignesDeleted;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.application_menu, menu);
        return true;
    }

    /*@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        Intent intent;
        switch (item.getItemId()) {
            case R.id.delete_contact:

                return true;
            case R.id.edit_contact:

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }*/
}
