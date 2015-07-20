package com.justinhammar.simplecal;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONObject;


public class CreateEventActivity extends ActionBarActivity {
    EditText nameEditText;
    EditText datetimeEditText;
    EditText colorEditText;
    private static final String PREF = "prefs";
    private static final String PREF_ID = "id";
    private static final String PREF_PASS = "password";
    SharedPreferences sharePrefs;
    String user_id;
    String password;
    private static final String BASE_URL = "http://heroic-district-93919.appspot.com";
    RequestParams params;
    AsyncHttpClient client = new AsyncHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        nameEditText = (EditText) findViewById(R.id.edit_name);
        datetimeEditText = (EditText) findViewById(R.id.edit_datetime);
        colorEditText = (EditText) findViewById(R.id.edit_color);

        sharePrefs = getSharedPreferences(PREF, MODE_PRIVATE);
        user_id = sharePrefs.getString(PREF_ID, "0");
        password = sharePrefs.getString(PREF_PASS, "");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_create_event, menu);
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

    public void createEvent(View view) {
        String name;
        String datetime;
        String color;

        params = new RequestParams();
        params.put("user_id", user_id);
        params.put("password", password);

        if (nameEditText.getText().toString().length() != 0) {
            name = nameEditText.getText().toString();
            params.put("name", name);
        } else {
            Toast.makeText(this, "Name is a required field", Toast.LENGTH_LONG).show();
            return;
        }

        if (datetimeEditText.getText().toString().length() != 0) {
            datetime = datetimeEditText.getText().toString();
            params.put("date_time", datetime);
        }

        if (colorEditText.getText().toString().length() != 0) {
            color = colorEditText.getText().toString();
            params.put("color", color);
        }

        client.post(BASE_URL + "/event", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                String toastText;
                Log.d("jh event POST success", response.toString());

                try {
                    toastText = "Event created";
                    Toast.makeText(CreateEventActivity.this, toastText, Toast.LENGTH_LONG).show();
                    MainActivity.addToListView(response.getString("name"), response.getString("key"));
                } catch (Exception ex) {
                    Log.d("jh json exception", ex.getMessage());
                }

                finish();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable error) {
                String toastText;
                Log.d("jh event POST fail", responseString);

                // inform user of error in toast
                if (statusCode == 400) {
                    toastText = responseString.substring("400 Bad Request, ".length());
                    Toast.makeText(CreateEventActivity.this, toastText, Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
