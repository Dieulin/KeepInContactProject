package com.pontonsoft.keepincontact.adaptaters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.pontonsoft.keepincontact.R;
import com.pontonsoft.keepincontact.model.Contact;

import java.util.ArrayList;
import java.util.logging.Filter;

public class ContactsAdapter extends ArrayAdapter<Contact> {

    public ContactsAdapter(Context context, ArrayList<Contact> users) {
        super(context, 0, users);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Contact contact = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_contact, parent, false);
        }
        // Lookup view for data population
        TextView nom = (TextView) convertView.findViewById(R.id.nom);
        TextView telephone = (TextView) convertView.findViewById(R.id.telephone);

        // Populate the data into the template view using the data object
        nom.setText(contact.getNom() + " " + contact.getPrenom());
        telephone.setText(contact.getTelephone());

        // Return the completed view to render on screen
        return convertView;
    }
}