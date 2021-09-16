package com.example.weather_finder;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity implements  View.OnFocusChangeListener, AdapterView.OnItemSelectedListener {

    EditText mob, fname, dob, pincode, adress1, adress2;
    Spinner gen;
    Button checkpin, Reg;
    ImageButton date;
    TextView dis, statev;
    private RequestQueue mRequestQueue;

    int y,d,m;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mob = (EditText) findViewById(R.id.mob);
        mob.setOnFocusChangeListener(this);
        fname = (EditText) findViewById(R.id.fullname);
        fname.setOnFocusChangeListener(this);
        dob = (EditText) findViewById(R.id.dob);
        dob.setOnFocusChangeListener(this);
        pincode = (EditText) findViewById(R.id.pin);
        pincode.setOnFocusChangeListener(this);
        adress1 = (EditText) findViewById(R.id.adressline1);
        adress1.setOnFocusChangeListener(this);
        adress2 = (EditText) findViewById(R.id.adressline2);
        adress2.setOnFocusChangeListener(this);
        gen = (Spinner) findViewById(R.id.gender);
        gen.setOnFocusChangeListener(this);
        checkpin = (Button) findViewById(R.id.checkpin);
        Reg = (Button) findViewById(R.id.reg);
        date = (ImageButton) findViewById(R.id.datepicker);
        dis = (TextView) findViewById(R.id.distview);
        statev = (TextView) findViewById(R.id.stateview);
        mRequestQueue = Volley.newRequestQueue(MainActivity.this);


        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.Gender,android.R.layout.simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        gen.setAdapter(adapter);
        gen.setOnItemSelectedListener((AdapterView.OnItemSelectedListener) this);

        Calendar calendar = Calendar.getInstance();


        Reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkforempty();

            }
        });

        pincode.addTextChangedListener(btnenable);

        checkpin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int pinlength=pincode.length();

                if (pinlength==6)
                {
                    String mypin=pincode.getText().toString();

                    mRequestQueue.getCache().clear();


                    int c =Integer.parseInt(mypin);

                    String url = "https://api.postalpincode.in/pincode/"+c;

                    RequestQueue queue = Volley.newRequestQueue(MainActivity.this);

                    JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET,url,null, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {


                            try {
                                JSONArray postofficearray = response.getJSONArray("PostOffice");

                                if (response.getString("Status").equals("Error"))
                                {
                                    AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
                                            .setMessage("Invalid Pincode")
                                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {

                                                }
                                            }).show();

                                }
                                else
                                {
                                    JSONObject obj = postofficearray.getJSONObject(0);
                                    String d = obj.getString("District");
                                    String s = obj.getString("State");

                                    dis.setText(d);
                                    statev.setText(s);


                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
                                        .setMessage("Invalid Pincode")
                                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {

                                            }
                                        }).show();
                            }

                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                            Toast.makeText(MainActivity.this, "Invalid Pincode", Toast.LENGTH_SHORT).show();

                        }

                    });
                    queue.add(objectRequest);


                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Please Enter 6 digit Pin code", Toast.LENGTH_SHORT).show();
                }

            }
        });

        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                y=calendar.get(Calendar.YEAR);
                d=calendar.get(Calendar.DATE);
                m=calendar.get(Calendar.MONTH);
                DatePickerDialog dailog = new DatePickerDialog(MainActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {


                        int day = datePicker.getDayOfMonth();
                        int month = datePicker.getMonth();
                        int Year = datePicker.getYear();


                        dob.setText(day+"/"+(month+1)+"/"+Year);

                    }
                },y,m,d);
                dailog.show();
            }
        });

    }



    private  TextWatcher btnenable = new TextWatcher() {
    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        String pincodeinput = pincode.getText().toString().trim();

        if (!pincodeinput.isEmpty())
        {
            checkpin.setEnabled(true);

        }
        else
        {
            checkpin.setEnabled(false);
        }

    }

    @Override
    public void afterTextChanged(Editable editable) {

    }
};


    private void checkforempty() {

        String mobile = mob.getText().toString();
        String fullname = fname.getText().toString();
        String db = dob.getText().toString();
        String findpincode = pincode.getText().toString();
        String line1 = adress1.getText().toString();
        String line2 = adress2.getText().toString();

        if (!mobile.isEmpty()) {
            mob.setError(null);
            mob.setEnabled(true);
            if (!fullname.isEmpty()) {
                fname.setError(null);
                fname.setEnabled(true);
                if (!db.isEmpty()) {
                    dob.setError(null);
                    dob.setEnabled(true);
                    if (!line1.isEmpty()) {
                        adress1.setError(null);
                        adress1.setEnabled(true);
                        if (!line2.isEmpty()) {
                            adress2.setError(null);
                            adress2.setEnabled(true);
                            if (!findpincode.isEmpty()) {
                                pincode.setError(null);
                                pincode.setEnabled(true);

                                Toast.makeText(getApplicationContext(), "Registration sucessful", Toast.LENGTH_SHORT).show();

                                mob.setText("");
                                fname.setText("");
                                dob.setText("");
                                adress1.setText("");
                                adress2.setText("");
                                pincode.setText("");

                            } else {
                                pincode.setError("Enter your Pin code");

                            }

                        } else {
                            adress2.setError("Please Enter your Adress2");
                        }

                    } else {
                        adress1.setError("Please Enter your Adress1");
                    }

                } else {
                    dob.setError("Select Your DOB");
                }

            } else {
                fname.setError("Enter Full name");
            }

        } else {
            mob.setError("Enter Mobile Number");
        }


    }

    @Override
    public void onFocusChange(View view, boolean b) {


        String mymobile = mob.getText().toString();
        String myfullname = fname.getText().toString();
        String mydb = dob.getText().toString();
        String myfindpincode = pincode.getText().toString();
        String myline1 = adress1.getText().toString();
        String myline2 = adress2.getText().toString();

        switch (view.getId()) {

            case R.id.mob:
                if (!Pattern.matches("(0/91)?[7-9][0-9]{9}", mymobile)) {
                    mob.setError("Entert Valid Mobile Number");
                } else {
                    mob.setError(null);
                    mob.setEnabled(true);
                }
                break;
            case R.id.fullname:
                if (!Pattern.matches("^[a-zA-Z  ]{0,30}$", myfullname)) {
                    fname.setError("only Alphabet used");
                } else {
                    fname.setError(null);
                    fname.setEnabled(true);
                }
                break;
            case R.id.dob:
                break;
            case R.id.adressline1:
                if (!Pattern.matches("^[a-zA-Z  ]{3,50}$", myline1)) {
                    adress1.setError("only Alphabet Allow and Min charecter 3");
                } else {
                    adress1.setError(null);
                    adress1.setEnabled(true);
                }
                break;
            case R.id.adressline2:
                if (!Pattern.matches("^[a-zA-Z  ]{3,50}$", myline2)) {
                    adress2.setError("only Alphabet Allow and Min charecter 3");
                } else {
                    adress2.setError(null);
                    adress2.setEnabled(true);
                }
                break;
            case R.id.pin:
                if (!Pattern.matches("[0-9]{6}", myfindpincode)) {
                    pincode.setError("Enter Vailed Pin");
                } else {
                    pincode.setError(null);
                    pincode.setEnabled(true);
                }
                break;

        }

    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String mygen = adapterView.getItemAtPosition(i).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}