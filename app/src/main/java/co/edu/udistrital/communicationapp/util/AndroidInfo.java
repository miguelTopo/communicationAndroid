package co.edu.udistrital.communicationapp.util;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;

import java.util.ArrayList;
import java.util.List;

import co.edu.udistrital.communicationapp.application.CommunicationApplication;
import co.edu.udistrital.communicationapp.model.User;
import co.edu.udistrital.communicationapp.model.UserContact;

public class AndroidInfo {

    private Context context;

    public AndroidInfo() {
        this.context = CommunicationApplication.getAppContext();
    }

    /**
     * Permite obtener la cadena de texto para el numero de celular  del dispositivo actual
     */
    public String getCurrentUserMobilePhone() {
        try {
            TelephonyManager manager = (TelephonyManager) this.context.getSystemService(Context.TELEPHONY_SERVICE);
            return manager.getLine1Number();
        } catch (SecurityException se) {
            return "";
        }
    }

    public List<UserContact> getContactList() {
        ContentResolver cr = this.context.getContentResolver();
        Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        Cursor phoneCursor;
        List<UserContact> contactList = null;
        if ((cursor != null ? cursor.getCount() : 0) > 0) {
            String id = "";
            String name = "";
            String mobilePhone = "";
            while (cursor != null && cursor.moveToNext()) {
                id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                if (cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    phoneCursor = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=?", new String[]{id}, null);
                    while (phoneCursor.moveToNext()) {
                        mobilePhone = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        if (contactList == null)
                            contactList = new ArrayList<>(1);
                        User user = new User();
                        user.mobilePhone = mobilePhone;

                        UserContact contact = new UserContact();
                        contact.user = user;
                        contact.customName = name;
                        contactList.add(contact);
                    }
                    phoneCursor.close();
                }
            }
        }
        if (cursor != null)
            cursor.close();
        return contactList;
    }

}
