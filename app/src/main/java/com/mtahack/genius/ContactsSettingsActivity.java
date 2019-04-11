package com.mtahack.genius;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.wafflecopter.multicontactpicker.ContactResult;
import com.wafflecopter.multicontactpicker.LimitColumn;
import com.wafflecopter.multicontactpicker.MultiContactPicker;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ContactsSettingsActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    public Map<String, ?> m_ContactsMap;
    public final int REQUEST_CODE = 1;
    public static final String CONTACTS_PREFS = "emergencyContacts";
    ListView contactsListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts_settings);
        contactsListView = (ListView) findViewById(R.id.listview_contacts);
        addContactsButtonPressed();
    }

    public void getContacts() {
        SharedPreferences contactsSharedPref = this.getSharedPreferences(CONTACTS_PREFS, MODE_PRIVATE);
        Map<String, ?> allContacts = contactsSharedPref.getAll();
        m_ContactsMap = allContacts;
    }


    public void fillListViewWithContacts() {
        getContacts();
        List<String> contactNumbersList = new ArrayList<>();
        List<String> contactNameList = new ArrayList<>();
        for (Map.Entry<String, ?> entry : m_ContactsMap.entrySet()) {
            System.out.println(entry.getKey() + "/" + entry.getValue());
            contactNumbersList.add(entry.getKey() + "\n" + entry.getValue()) ;
            contactNameList.add(entry.getKey());
        }
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_list_item_1, contactNumbersList);
        contactsListView.setAdapter(arrayAdapter);
        contactsListView.setOnItemClickListener(this);
    }

    public void addContactsButtonPressed() {
        new MultiContactPicker.Builder(this) //Activity/fragment context
                .hideScrollbar(false) //Optional - default: false
                .showTrack(true) //Optional - default: true
                .searchIconColor(Color.WHITE) //Option - default: White
                .setChoiceMode(MultiContactPicker.CHOICE_MODE_MULTIPLE) //Optional - default: CHOICE_MODE_MULTIPLE
                .handleColor(ContextCompat.getColor(this, R.color.colorPrimary)) //Optional - default: Azure Blue
                .bubbleColor(ContextCompat.getColor(this, R.color.colorPrimary)) //Optional - default: Azure Blue
                .bubbleTextColor(Color.WHITE) //Optional - default: White
                .setTitleText("Select Contacts") //Optional - default: Select Contacts
                .setSelectedContacts("10") //Optional - will pre-select contacts of your choice. String... or List<ContactResult>
                .setLoadingType(MultiContactPicker.LOAD_ASYNC) //Optional - default LOAD_ASYNC (wait till all loaded vs stream results)
                .limitToColumn(LimitColumn.NONE) //Optional - default NONE (Include phone + email, limiting to one can improve loading time)
                .setActivityAnimations(android.R.anim.fade_in, android.R.anim.fade_out,
                        android.R.anim.fade_in,
                        android.R.anim.fade_out) //Optional - default: No animation overrides
                .showPickerForResult(REQUEST_CODE);
    }


    public void addContact(String i_ContactName, String i_ContactNumber) {
        SharedPreferences.Editor editor = this.getSharedPreferences(CONTACTS_PREFS, MODE_PRIVATE).edit();
        String phoneNumber = i_ContactNumber.replaceAll("[\\(\\)\\-]", "");
        editor.putString(i_ContactName, phoneNumber);
        editor.apply();
    }

public void removeContact(String i_ContactName){
    m_ContactsMap.remove(i_ContactName);
    SharedPreferences.Editor editor = this.getSharedPreferences(CONTACTS_PREFS, MODE_PRIVATE).edit();
    editor.remove(i_ContactName);
    editor.apply();


}
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                List<ContactResult> results = MultiContactPicker.obtainResult(data);
                for (ContactResult contact : results) {
                    addContact(contact.getDisplayName(), contact.getPhoneNumbers().get(0).getNumber());
                }
                fillListViewWithContacts();
                Log.d("MyTag", results.get(0).getDisplayName());
            } else if (resultCode == RESULT_CANCELED) {
                System.out.println("User closed the picker without selecting items.");
            }
        }
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final String contactName = getNameFromMap((int) id);
        new AlertDialog.Builder(this).setTitle("Confirm Delete")
                .setMessage("Do you want to delete " + contactName)
                .setPositiveButton("YES",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                removeContact(contactName);
                                fillListViewWithContacts();
                                // Perform Action & Dismiss dialog
                                dialog.dismiss();
                            }
                        })
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing
                        dialog.dismiss();
                    }
                })
                .create()
                .show();
    }


    public String getNameFromMap(int i_Index) {
        int index = 0;
        String res = null;
        for (String key : m_ContactsMap.keySet()) {
            if (index == i_Index) {
                res = key;
            }
            ++index;
        }
        return res;
    }
}
