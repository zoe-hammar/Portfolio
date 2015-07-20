package com.justinhammar.simplecal;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;


public class MainActivity extends ActionBarActivity implements AdapterView.OnItemClickListener {
    private static final String BASE_URL = "http://heroic-district-93919.appspot.com";
    RequestParams params = new RequestParams("username", "mobile");
    TextView mainTextView;
    private static final String PREF = "prefs";
    private static final String PREF_ID = "id";
    private static final String PREF_PASS = "password";
    SharedPreferences sharePrefs;
    String user_id;
    String password;
    String toastText;
    AsyncHttpClient client = new AsyncHttpClient();
    ListView eventListView;
    static ArrayList eventList;
    static ArrayList<String> eventIDs;
    static ArrayAdapter eventAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharePrefs = getSharedPreferences(PREF, MODE_PRIVATE);
        user_id = sharePrefs.getString(PREF_ID, "0");
        password = sharePrefs.getString(PREF_PASS, "");

        // set up the arrayList and adapter that will populate the event ListView
        eventListView = (ListView) findViewById(R.id.event_listview);
        eventList = new ArrayList();
        eventIDs = new ArrayList();
        eventAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, eventList);
        eventListView.setAdapter(eventAdapter);
        eventListView.setOnItemClickListener(this);

        // perform GET to do initial fill of the event listview
        client.get(BASE_URL + "/event?user_id=" + user_id + "&password=" + password, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                JSONObject event;
                for (int i = 0; i < response.length(); ++i) {
                    try {
                        event = response.getJSONObject(i);
                        eventList.add(event.getString("name"));
                        eventIDs.add(event.getString("key"));
                    } catch (Exception ex) {
                        Log.d("jh json exception", ex.getMessage());
                    }
                }
                eventAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable error) {
                Log.d("jh event GET fail", responseString);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        // noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void goToCreateEvent(View view) {
        Intent intent = new Intent(this, CreateEventActivity.class);
        startActivity(intent);
    }

    public void logout(View view) {
        SharedPreferences.Editor e = sharePrefs.edit();
        try {
            // logout of the current account
            e.putString(PREF_ID, "");
            e.putString(PREF_PASS, "");
        } catch (Exception ex) {
            Log.d("jh json exception", ex.getMessage());
        }
        e.commit();

        toastText = "You are logged out";
        Toast.makeText(this, toastText, Toast.LENGTH_LONG).show();

        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    public static void addToListView(String name, String id) {
        eventList.add(name);
        eventIDs.add(id);
        eventAdapter.notifyDataSetChanged();
    }

    public static void updateListView(String name, String id) {
        int index = eventIDs.indexOf(id);
        eventList.set(index, name);
        eventAdapter.notifyDataSetChanged();
    }

    public static void deleteFromListView(String id) {
        int index = eventIDs.indexOf(id);
        eventList.remove(index);
        eventIDs.remove(index);
        eventAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(this, EventDetailActivity.class);
        intent.putExtra("id", eventIDs.get(position));
        startActivity(intent);
    }
}
