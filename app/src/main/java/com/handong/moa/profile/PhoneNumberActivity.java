package com.handong.moa.profile;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.handong.moa.R;
import com.handong.moa.data.MyData;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PhoneNumberActivity extends AppCompatActivity {
    //* for server
    private static final String urls = "http://54.180.8.235:5000/user/phone";

    //* for getting phone number
    private EditText phoneNumber;
    private EditText checkingPhoneNumber;

    private ImageButton backButton;
    private ImageButton finishButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_phone_number);
        phoneNumber = findViewById(R.id.phonenumber_number_edittext);
        checkingPhoneNumber = findViewById(R.id.phonenumber_numberconfirm_edittext);

        backButton = findViewById(R.id.phonenumber_backbutton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        finishButton = findViewById(R.id.phonenumber_finish_button);
        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveData();
                loadPhoneData();
                sendServer();
                Intent intent = new Intent();
                intent.putExtra("result", "good");
                setResult(RESULT_OK, intent);
                finish();

            }
        });
    }

    // get the phone number info and save phone number info into users local
    void saveData() {
        String tempPhoneNumber = phoneNumber.getText().toString();
        String tempCheckPhoneNumber = checkingPhoneNumber.getText().toString();

        if(tempPhoneNumber.equals(tempCheckPhoneNumber)) {
            MyData.phoneNumber = phoneNumber.getText().toString();
            SharedPreferences preferences= getSharedPreferences("info",MODE_PRIVATE);
            SharedPreferences.Editor editor=preferences.edit();
            editor.putString("phoneNumber", MyData.phoneNumber);
            editor.commit();
            finish();
        }else{
            Toast.makeText(this, "The phone number is different", Toast.LENGTH_SHORT).show();
        }
    }

    // read phone number info from users local
    void loadPhoneData(){
        SharedPreferences preferences = getSharedPreferences("info",MODE_PRIVATE);
        MyData.phoneNumber = preferences.getString("phoneNumber", null);
    }

    public void sendServer(){
        class sendData extends AsyncTask<Void, Void, String> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }
            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
            }
            @Override
            protected void onProgressUpdate(Void... values) {
                super.onProgressUpdate(values);
            }
            @Override
            protected void onCancelled(String s) {
                super.onCancelled(s);
            }
            @Override
            protected void onCancelled() {
                super.onCancelled();
            }
            @Override
            protected String doInBackground(Void... voids) {
                try {

                    OkHttpClient client = new OkHttpClient();
                    JSONObject jsonInput = new JSONObject();

                    jsonInput.put("phone", MyData.phoneNumber);
                    jsonInput.put("email", MyData.mail);

                    RequestBody reqBody = RequestBody.create(
                            MediaType.parse("application/json; charset=utf-8"),
                            jsonInput.toString()
                    );

                    Request request = new Request.Builder()
                            .put(reqBody)
                            .url(urls)
                            .build();

                    Response responses = null;
                    responses = client.newCall(request).execute();

                    responses.close();
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return null;
            }
        }
        sendData sendData = new sendData();
        sendData.execute();
    }
}