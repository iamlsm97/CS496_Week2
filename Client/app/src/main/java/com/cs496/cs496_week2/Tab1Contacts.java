package com.cs496.cs496_week2;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.database.Cursor;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Locale;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by rongrong on 2017-07-06.
 */

public class Tab1Contacts extends Fragment {
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 527;
    private static final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 528;
    private static final int MY_PERMISSIONS_REQUEST_DIAL_PHONE = 529;
    ArrayList<Contact> ContactArrList;
    ArrayList<Contact> items;
    ArrayList<Contact> displayitems = new ArrayList<>();
    String phone_num;
    View view;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        try {
            PackageInfo info = getActivity().getPackageManager().getPackageInfo(
                    "com.cs496.cs496_week2", //앱의 패키지 명
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
        } catch (NoSuchAlgorithmException e) {
        }

        view = inflater.inflate(R.layout.tab1_contacts, null);

        ContactArrList = getContactList();
        final CustomAdapter adapter = new CustomAdapter(this.getActivity(), R.layout.tab1_contacts_layout, ContactArrList);

        final EditText searchText = (EditText) view.findViewById(R.id.text_search);
        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String search_text = s.toString();
                adapter.filter(search_text);
            }
        });

        Button direct = (Button) view.findViewById(R.id.direct);
        direct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tel = "tel:" + displayitems.get(0).phone_num;
                startActivity(new Intent("android.intent.action.CALL", Uri.parse(tel)));
            }
        });

        ListView listview = (ListView) view.findViewById(R.id.list_view);
        if (listview != null)
            listview.setAdapter(adapter);

        return view;
    }

    private class CustomAdapter extends ArrayAdapter<Contact> {
        public void filter(String searchText) {
            searchText = searchText.toLowerCase(Locale.getDefault());
            displayitems.clear();
            if (searchText.length() == 0) {
                displayitems.addAll(items);
            } else {
                for (Contact item : items) {
                    if (item.name.contains(searchText)) {
                        displayitems.add(item);
                    }
                }
            }
            notifyDataSetChanged();
        }

        public CustomAdapter(Context context, int textViewResourceId, ArrayList<Contact> objects) {
            super(context, textViewResourceId, objects);
            displayitems = objects;
            items = new ArrayList<>();
            items.addAll(displayitems);
        }

        @Override
        public int getCount() {
            return displayitems.size();
        }

        @Override
        public Contact getItem(int position) {
            return displayitems.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public View getView(final int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                LayoutInflater vi = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.tab1_contacts_layout, null);
            }

            ImageView imageView = (ImageView) v.findViewById(R.id.olaf);
            imageView.setImageResource(R.drawable.olaf);
            imageView.setBackground(new ShapeDrawable(new OvalShape()));
            imageView.setClipToOutline(true);

            TextView textView1 = (TextView) v.findViewById(R.id.textView1);
            textView1.setText(displayitems.get(position).name);
            TextView textView2 = (TextView) v.findViewById(R.id.textView2);
            textView2.setText(displayitems.get(position).phone_num);
            ImageButton button1 = (ImageButton) v.findViewById(R.id.button1);
            phone_num = displayitems.get(position).phone_num;

            button1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String tel = "tel:" + displayitems.get(position).phone_num;
                    startActivity(new Intent("android.intent.action.CALL", Uri.parse(tel)));

                }
            });
            ImageButton button2 = (ImageButton) v.findViewById(R.id.button2);
            button2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String tel = "tel:" + displayitems.get(position).phone_num;
                    startActivity(new Intent("android.intent.action.DIAL", Uri.parse(tel)));

                }
            });
            return v;
        }
    }

    private ArrayList<Contact> getContactList() {
        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String[] projection = {
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                ContactsContract.CommonDataKinds.Phone.NUMBER,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
        };
        String sortOrder = ContactsContract.Contacts.DISPLAY_NAME_PRIMARY + " asc";
        String condition = ContactsContract.Contacts.HAS_PHONE_NUMBER + "=1";
        String[] selectionArgs = null;

        Cursor contactCursor = getActivity().getContentResolver().query(
                uri,
                projection,
                condition,
                selectionArgs,
                sortOrder
        );

        ArrayList<Contact> contact_list = new ArrayList<>();

        while (contactCursor.moveToNext()) {
            Contact contact_ele = new Contact();
            contact_ele.id = contactCursor.getLong(0);
            contact_ele.phone_num = contactCursor.getString(1);
            contact_ele.name = contactCursor.getString(2);
            contact_list.add(contact_ele);
        }
        contactCursor.close();

//        String dbContact = null;

        JSONArray DBContact = null;
//        DBContact = getDBContact();
        CustomThread thread = new CustomThread();
        thread.start();

        try {
            thread.join();
        } catch (Exception e) {
            e.printStackTrace();
        }

        DBContact = thread.getResult();
        Log.e("dbdbdb", DBContact.toString());
        for (int i=0; i<DBContact.length(); i++){
            JSONObject single = null;
            try {
                single = DBContact.getJSONObject(i);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Contact contact_ele = new Contact();
            try {
                contact_ele.phone_num = single.getString("number");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                contact_ele.name = single.getString("name");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            contact_list.add(contact_ele);
        }


        return contact_list;
    }

    private class Contact {
        long id = 0;
        String phone_num = "Default";
        String name = "Default";
    }

    public class GetContact {
        OkHttpClient client = new OkHttpClient();

        String get(String url) throws IOException {
            Request request = new Request.Builder()
                    .url(url)
                    .build();

            Response response = client.newCall(request).execute();
            return response.body().string();

        }
    }

    public class CustomThread extends Thread {
        JSONArray dbContact = null;
        final GetContact example = new GetContact();

        public CustomThread() {
            dbContact = null;
        }

        @Override
        public void run() {
            String response = null;
            try {
                response = example.get("http://13.124.143.15:8080/api/rongrong@sparcs.org/contact");
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                dbContact = new JSONArray(response);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.e("response in Thread", dbContact.toString());
        }

        public JSONArray getResult() {
            Log.e("Return in Thread", dbContact.toString());
            return dbContact;
        }


    }

//    public JSONArray getDBContact(){
//        final GetContact example = new GetContact();
//
//
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                String response = null;
//                try {
//                    response = example.get("http://13.124.143.15:8080/api/rongrong@sparcs.org/contact");
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                JSONArray dbContact = null;
//                try {
//                    dbContact = new JSONArray(response);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//                Log.e("response", String.valueOf(dbContact.length()));
//
//            }
//        }).start();
//
//        return dbContact
//    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    ContactArrList = getContactList();
                    CustomAdapter adapter = new CustomAdapter(this.getActivity(), R.layout.tab1_contacts_layout, ContactArrList);

                    ListView listview = (ListView) view.findViewById(R.id.list_view);
                    if (listview != null)
                        listview.setAdapter(adapter);
                } else {
                    Toast.makeText(getActivity(), "요청이 거부되었습니다", Toast.LENGTH_SHORT).show();
                }
                return;

            case MY_PERMISSIONS_REQUEST_CALL_PHONE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    String tel = "tel:" + phone_num;
                    startActivity(new Intent("android.intent.action.CALL", Uri.parse(tel)));
                } else {
                    Toast.makeText(getActivity(), "요청이 거부되었습니다", Toast.LENGTH_SHORT).show();
                }
                return;

            case MY_PERMISSIONS_REQUEST_DIAL_PHONE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    String tel = "tel:" + phone_num;
                    startActivity(new Intent("android.intent.action.DIAL", Uri.parse(tel)));
                } else {
                    Toast.makeText(getActivity(), "요청이 거부되었습니다", Toast.LENGTH_SHORT).show();
                }
                return;
        }
    }
}
