package com.pontonsoft.keepincontact.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.pontonsoft.keepincontact.R;
import com.pontonsoft.keepincontact.model.Contact;

import java.sql.SQLException;

public class ContactOpenDatabaseHelper extends OrmLiteSqliteOpenHelper{

    /**
     * The data access object used to interact with the Sqlite database to do C.R.U.D operations.
     */
    private Dao<Contact, Long> contactDao;
    private static final String DATABASE_NAME = "contacts";
    private static final int DATABASE_VERSION = 1;

    public ContactOpenDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION,
                /**
                 * R.raw.ormlite_config is a reference to the ormlite_config.txt file in the
                 * /res/raw/ directory of this project
                 * */
                R.raw.ormlite_config);
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {
            //TableUtils.dropTable(connectionSource, Contact.class, false);
            /**
             * creates the Contact database table
             */
            TableUtils.createTable(connectionSource, Contact.class);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource,int oldVersion, int newVersion) {
        try {
            /**
             * Recreates the database when onUpgrade is called by the framework
             */
            TableUtils.dropTable(connectionSource, Contact.class, false);
            onCreate(database, connectionSource);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns an instance of the data access object
     * @return
     * @throws SQLException
     */
    public Dao<Contact, Long> getDao() throws SQLException {
        if(this.contactDao == null) {
            this.contactDao = getDao(Contact.class);
        }
        return this.contactDao;
    }
}