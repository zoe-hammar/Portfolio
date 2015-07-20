package com.justinhammar.simplecal;

import android.content.Intent;
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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class LoginActivity extends ActionBarActivity {
    EditText loginNameEditText;
    EditText loginPassEditText;
    EditText newAcctNameEditText;
    EditText newAcctPassEditText;
    private static final String BASE_URL = "http://heroic-district-93919.appspot.com";
    RequestParams params;
    private static final String PREF = "prefs";
    private static final String PREF_ID = "id";
    private static final String PREF_NAME = "name";
    private static final String PREF_PASS = "password";
    SharedPreferences sharePrefs;
    SharedPreferences.Editor spEditor;
    String user_id;
    String username;
    String password;
    String toastText;
    AsyncHttpClient client = new AsyncHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginNameEditText = (EditText) findViewById(R.id.edit_loginName);
        loginPassEditText = (EditText) findViewById(R.id.edit_loginPass);
        newAcctNameEditText = (EditText) findViewById(R.id.edit_newAcctName);
        newAcctPassEditText = (EditText) findViewById(R.id.edit_newAcctPass);

        sharePrefs = getSharedPreferences(PREF, MODE_PRIVATE);
        spEditor = sharePrefs.edit();
        user_id = sharePrefs.getString(PREF_ID, "");
        username = sharePrefs.getString(PREF_NAME, "");
        password = sharePrefs.getString(PREF_PASS, "");
        Log.d("jh stored user_id", user_id);

        if (!user_id.equals("") && !password.equals("")) {
            authenticateUser();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
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

    public void login(View view) {
        if (loginNameEditText.getText().toString().length() != 0) {
            username = loginNameEditText.getText().toString();
        } else {
            Toast.makeText(this, "Username is a required field", Toast.LENGTH_LONG).show();
            return;
        }
        if (loginPassEditText.getText().toString().length() != 0) {
            password = loginPassEditText.getText().toString();
        } else {
            Toast.makeText(this, "Password is a required field", Toast.LENGTH_LONG).show();
            return;
        }

        // get the ID that goes with the username, if it exists
        client.get(BASE_URL + "/user", new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Log.d("jh GET success", response.toString());

                JSONObject user;
                int i;
                for (i = 0; i < response.length(); ++i) {
                    try {
                        user = response.getJSONObject(i);
                        if (user.getString("username").equals(username)) {
                            user_id = user.getString("key");
                            spEditor.putString(PREF_ID, user.getString("key"));
                            spEditor.putString(PREF_NAME, username);
                            spEditor.putString(PREF_PASS, password);
                            spEditor.commit();
                            authenticateUser();
                            break;
                        }
                    } catch (JSONException e) {
                        Log.d("jh json exception", e.getMessage());
                    }
                }
                if (i == response.length()) {
                    // user not found
                    Toast.makeText(LoginActivity.this, "user not found", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void createAccount(View view) {
        params = new RequestParams();
        // make sure they provided a username
        if (newAcctNameEditText.getText().toString().length() != 0) {
            username = newAcctNameEditText.getText().toString();
            params.put("username", username);
        } else {
            Toast.makeText(this, "Username is a required field", Toast.LENGTH_LONG).show();
            return;
        }
        // make sure they provided a password
        if (newAcctPassEditText.getText().toString().length() != 0) {
            password = newAcctPassEditText.getText().toString();
            params.put("password", password);
        } else {
            Toast.makeText(this, "Password is a required field", Toast.LENGTH_LONG).show();
            return;
        }

        // request that a new account be made
        client.post(BASE_URL + "/user", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("jh POST success", response.toString());

                try {
                    // login to this new account
                    spEditor.putString(PREF_ID, response.getString("key"));
                    spEditor.putString(PREF_NAME, response.getString("username"));
                    spEditor.putString(PREF_PASS, response.getString("password"));
                } catch (Exception ex) {
                    Log.d("jh json exception", ex.getMessage());
                }
                spEditor.commit();

                loginGreeting();

                // take them to the main screen
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable error) {
                Log.d("jh POST fail", responseString);

                // tell them what they did wrong
                if (statusCode == 400) {
                    toastText = responseString.substring("400 Bad Request, ".length());
                    Toast.makeText(LoginActivity.this, toastText, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    // greet the newly logged in user by name
    public void loginGreeting() {
        toastText = "Hi, " + username  + "!";
        Toast.makeText(this, toastText, Toast.LENGTH_LONG).show();
    }

    public void authenticateUser() {
        // make sure that the username and password are valid
        client.get(BASE_URL + "/user/" + user_id + "?password=" + password, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("jh auth GET success", response.toString());

                loginGreeting();

                // user is already logged in, go to main screen
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable error) {
                Log.d("jh auth GET fail", responseString);

                // clear the invalid username and password
                try {
                    // logout of the current account
                    spEditor.remove(PREF_ID);
                    spEditor.remove(PREF_NAME);
                    spEditor.remove(PREF_PASS);
                } catch (Exception ex) {
                    Log.d("jh json exception", ex.getMessage());
                }
                spEditor.commit();

                // tell them what they did wrong
                if (statusCode == 400) {
                    toastText = responseString.substring("400 Bad Request, ".length());
                    Toast.makeText(LoginActivity.this, toastText, Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
