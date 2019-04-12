package com.mtahack.genius;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.wafflecopter.multicontactpicker.ContactResult;
import com.wafflecopter.multicontactpicker.LimitColumn;
import com.wafflecopter.multicontactpicker.MultiContactPicker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ContactsSettingsActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private List<String> m_ContactsPhoneNumbers;
    public Map<String, ?> m_ContactsMap;
    public final int REQUEST_CODE = 1;
    public static final String CONTACTS_PREFS = "emergencyContacts";
    ListView contactsListView;
    ImageButton buttonAddContact;
    TextView textViewContactsDescreption;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Settings.init(ContactsSettingsActivity.this);
        setContentView(R.layout.activity_contacts_settings);
        contactsListView = (ListView) findViewById(R.id.listview_Contacts);
        buttonAddContact = (ImageButton) findViewById(R.id.button_addContact);
        buttonAddContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addContactsButtonPressed();
            }
        });
        textViewContactsDescreption = (TextView)findViewById(R.id.textView_ToDisplay);
        fillListViewWithContacts();
    }

    public List<String> getM_ContactsPhoneNumbers() {
        return m_ContactsPhoneNumbers;
    }

    public void setPageDescreption(){
        String text;
        if(m_ContactsMap.size() > 0) {
            text = "Contacts to notify in emergency situation: \n";
        }else{
            text = "No contacts were found.\nClick the button in the corner to add.";
        }
        this.textViewContactsDescreption.setText(text);
    }

    public Map<String,?> getContacts() {
        Pair<String,String>[] contacts = null;
        try{
            contacts = Settings.getInstance().getContactsToText();
        } catch (Exception e) {e.printStackTrace();}
        Map<String, String> data = new HashMap<String,String>();
        for (Pair<String,String> contact : contacts){
            data.put(contact.first, contact.second);
        }
        m_ContactsMap = data;
        return data;
    }


    public static void isNeedPermission(Activity i_Activity) {
        if (ContextCompat.checkSelfPermission(i_Activity.getApplicationContext(), Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(i_Activity,
                    new String[]{Manifest.permission.READ_CONTACTS},
                    2);
        }
    }

    public void fillListViewWithContacts() {
        getContacts();
        List<String> contactNumbersList = new ArrayList<>();
        List<String> contactNameList = new ArrayList<>();
        for (Map.Entry<String, ?> entry : m_ContactsMap.entrySet()) {
            System.out.println(entry.getKey() + "/" + entry.getValue());
            contactNumbersList.add(entry.getKey() + "\n" + entry.getValue());
            contactNameList.add(entry.getKey());
        }
        m_ContactsPhoneNumbers = contactNumbersList;
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_list_item_1, contactNumbersList);
        contactsListView.setAdapter(arrayAdapter);
        contactsListView.setOnItemClickListener(this);
        setPageDescreption();
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
                .setSelectedContacts("10") //Optional - will pre-select activity_contacts_settings of your choice. String... or List<ContactResult>
                .setLoadingType(MultiContactPicker.LOAD_ASYNC) //Optional - default LOAD_ASYNC (wait till all loaded vs stream results)
                .limitToColumn(LimitColumn.NONE) //Optional - default NONE (Include phone + email, limiting to one can improve loading time)
                .setActivityAnimations(android.R.anim.fade_in, android.R.anim.fade_out,
                        android.R.anim.fade_in,
                        android.R.anim.fade_out) //Optional - default: No animation overrides
                .showPickerForResult(REQUEST_CODE);
    }

    public void removeContact(String i_ContactName) {
        m_ContactsMap.remove(i_ContactName);
        ArrayList<Pair<String,String>> str = new ArrayList<>();
        for (Map.Entry<String, ?> entry : m_ContactsMap.entrySet()) {
            str.add(new Pair<String, String>(entry.getKey(), (String)entry.getValue()));
        }
        try{
            Settings.getInstance().setContanctsToText(str.toArray(new Pair[str.size()]));
        } catch (Exception e) {}
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                List<ContactResult> results = MultiContactPicker.obtainResult(data);
                ArrayList<Pair<String,String>> lst = new ArrayList<Pair<String, String>>();
                for (ContactResult contact : results) {
                    lst.add(new Pair<String, String>(contact.getDisplayName(), contact.getPhoneNumbers().get(0).getNumber()));
                }
                try{
                    Settings.getInstance().setContanctsToText(lst.toArray(new Pair[lst.size()]));
                } catch (Exception e) {e.printStackTrace();}
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
