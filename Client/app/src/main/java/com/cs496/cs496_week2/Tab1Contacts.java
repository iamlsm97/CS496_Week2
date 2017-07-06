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

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Locale;

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

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
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
        int permissionCheck = ContextCompat.checkSelfPermission(this.getActivity(), android.Manifest.permission.READ_CONTACTS);

        if (permissionCheck == PackageManager.PERMISSION_DENIED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), android.Manifest.permission.READ_CONTACTS)) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                dialog.setTitle("Permission Check")
                        .setMessage("[설정] > [개인정보 보호 및 안전] > [앱 권한] 에서 권한을 요청하여야 합니다")
                        .setPositiveButton("수락", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                requestPermissions(
                                        new String[]{android.Manifest.permission.READ_CONTACTS},
                                        MY_PERMISSIONS_REQUEST_READ_CONTACTS
                                );
                            }
                        })
                        .setNegativeButton("거절", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(getActivity(), "요청이 거부되었습니다", Toast.LENGTH_SHORT).show();
                            }
                        }).create().show();
            } else {
                requestPermissions(
                        new String[]{android.Manifest.permission.READ_CONTACTS},
                        MY_PERMISSIONS_REQUEST_READ_CONTACTS
                );
            }
        } else {
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
        }
        return view;
    }

    private class CustomAdapter extends ArrayAdapter<Contact> {
        public void filter(String searchText) {
            searchText = searchText.toLowerCase(Locale.getDefault());
            displayitems.clear();
            if (searchText.length() == 0) {
                displayitems.addAll(items);
            }
            else {
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

            ImageView imageView = (ImageView)v.findViewById(R.id.olaf);
            imageView.setImageResource(R.drawable.olaf);
            imageView.setBackground(new ShapeDrawable(new OvalShape()));
            imageView.setClipToOutline(true);

            TextView textView1 = (TextView)v.findViewById(R.id.textView1);
            textView1.setText(displayitems.get(position).name);
            TextView textView2 = (TextView)v.findViewById(R.id.textView2);
            textView2.setText(displayitems.get(position).phone_num);
            ImageButton button1 = (ImageButton)v.findViewById(R.id.button1);
            phone_num = displayitems.get(position).phone_num;

            button1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int permissionCheck = ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.CALL_PHONE);
                    if (permissionCheck == PackageManager.PERMISSION_DENIED) {
                        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), android.Manifest.permission.CALL_PHONE)) {
                            AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                            dialog.setTitle("Permission Check")
                                    .setMessage("[설정] > [개인정보 보호 및 안전] > [앱 권한] 에서 권한을 요청하여야 합니다")
                                    .setPositiveButton("수락", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            requestPermissions(
                                                    new String[]{android.Manifest.permission.CALL_PHONE},
                                                    MY_PERMISSIONS_REQUEST_CALL_PHONE
                                            );
                                        }
                                    })
                                    .setNegativeButton("거절", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Toast.makeText(getActivity(), "요청이 거부되었습니다", Toast.LENGTH_SHORT).show();
                                        }
                                    }).create().show();
                        } else {
                            requestPermissions(
                                    new String[]{android.Manifest.permission.CALL_PHONE},
                                    MY_PERMISSIONS_REQUEST_CALL_PHONE
                            );
                        }
                    } else {
                        String tel = "tel:" + displayitems.get(position).phone_num;
                        startActivity(new Intent("android.intent.action.CALL", Uri.parse(tel)));
                    }
                }
            });
            ImageButton button2 = (ImageButton) v.findViewById(R.id.button2);
            button2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int permissionCheck = ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.CALL_PHONE);
                    if (permissionCheck == PackageManager.PERMISSION_DENIED) {
                        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), android.Manifest.permission.CALL_PHONE)) {
                            AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                            dialog.setTitle("Permission Check")
                                    .setMessage("[설정] > [개인정보 보호 및 안전] > [앱 권한] 에서 권한을 요청하여야 합니다")
                                    .setPositiveButton("수락", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            requestPermissions(
                                                    new String[]{android.Manifest.permission.CALL_PHONE},
                                                    MY_PERMISSIONS_REQUEST_DIAL_PHONE
                                            );
                                        }
                                    })
                                    .setNegativeButton("거절", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Toast.makeText(getActivity(), "요청이 거부되었습니다", Toast.LENGTH_SHORT).show();
                                        }
                                    }).create().show();
                        } else {
                            requestPermissions(
                                    new String[]{android.Manifest.permission.CALL_PHONE},
                                    MY_PERMISSIONS_REQUEST_DIAL_PHONE
                            );
                        }
                    } else {
                        String tel = "tel:" + displayitems.get(position).phone_num;
                        startActivity(new Intent("android.intent.action.DIAL", Uri.parse(tel)));
                    }
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

        return contact_list;
    }

    private class Contact {
        long id = 0;
        String phone_num = "Default";
        String name = "Default";
    }

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
                    Toast.makeText(getActivity(),"요청이 거부되었습니다",Toast.LENGTH_SHORT).show();
                }
                return;

            case MY_PERMISSIONS_REQUEST_CALL_PHONE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    String tel = "tel:" + phone_num;
                    startActivity(new Intent("android.intent.action.CALL", Uri.parse(tel)));
                } else {
                    Toast.makeText(getActivity(),"요청이 거부되었습니다",Toast.LENGTH_SHORT).show();
                }
                return;

            case MY_PERMISSIONS_REQUEST_DIAL_PHONE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    String tel = "tel:" + phone_num;
                    startActivity(new Intent("android.intent.action.DIAL", Uri.parse(tel)));
                } else {
                    Toast.makeText(getActivity(),"요청이 거부되었습니다",Toast.LENGTH_SHORT).show();
                }
                return;
        }
    }
}
