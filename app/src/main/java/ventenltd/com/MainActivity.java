package ventenltd.com;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    RelativeLayout error, load;
    TextView error_text;
    RecyclerView list;
    ArrayList<String> dates, genders, countries, colors;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        list = findViewById(R.id.list);
        list.setNestedScrollingEnabled(false);
        list.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        error = findViewById(R.id.error);
        error_text = findViewById(R.id.error_text);
        load = findViewById(R.id.load);

        if (isNetworkAvailable(this)) {
            load_data();
        } else {
            list.setVisibility(View.INVISIBLE);
            load.setVisibility(View.INVISIBLE);
            error.setVisibility(View.VISIBLE);
            String text = "Oops something went wrong. Please check your Network connection and tap this text to refresh.";
            error_text.setText(text);
        }
    }

    public void load_data() {
        list.setVisibility(View.INVISIBLE);
        load.setVisibility(View.VISIBLE);
        error.setVisibility(View.INVISIBLE);
        String text = "";
        error_text.setText(text);

        dates = new ArrayList<>();
        genders = new ArrayList<>();
        countries = new ArrayList<>();
        colors = new ArrayList<>();
        list.setAdapter(new filter_adapter(dates, genders, countries, colors, MainActivity.this));

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url = "https://ven10.co/assessment/filter.json";
        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        int start_year = jsonArray.getJSONObject(i).getInt("start_year");
                        int end_year = jsonArray.getJSONObject(i).getInt("end_year");
                        String gender = jsonArray.getJSONObject(i).getString("gender");
                        StringBuilder country = new StringBuilder();
                        StringBuilder color = new StringBuilder();

                        dates.add(start_year + " - " + end_year);
                        if (!gender.equals("")) {
                            gender = gender.substring(0, 1).toUpperCase() + gender.substring(1);
                            genders.add(gender);
                        } else {
                            genders.add("All");
                        }

                        JSONArray jsonArray2 = jsonArray.getJSONObject(i).getJSONArray("countries");
                        for (int j = 0; j < jsonArray2.length(); j++) {
                            country.append(jsonArray2.getString(j)).append(", ");
                        }
                        if (!country.toString().equals("")) {
                            countries.add(country.toString().substring(0, country.toString().lastIndexOf(",")));
                        } else {
                            countries.add("All");
                        }

                        JSONArray jsonArray3 = jsonArray.getJSONObject(i).getJSONArray("colors");
                        for (int j = 0; j < jsonArray3.length(); j++) {
                            color.append(jsonArray3.getString(j)).append(", ");
                        }
                        if (!color.toString().equals("")) {
                            colors.add(color.toString().substring(0, color.toString().lastIndexOf(",")));
                        } else {
                            colors.add("All");
                        }
                    }
                    list.setAdapter(new filter_adapter(dates, genders, countries, colors, MainActivity.this));
                    list.setVisibility(View.VISIBLE);
                    load.setVisibility(View.INVISIBLE);
                    error.setVisibility(View.INVISIBLE);
                    String textt = "";
                    error_text.setText(textt);
                } catch (JSONException e) {
                    e.printStackTrace();
                    list.setVisibility(View.INVISIBLE);
                    load.setVisibility(View.INVISIBLE);
                    error.setVisibility(View.VISIBLE);
                    String text = "An error occurred somewhere, data could not be loaded. Please tap this text to refresh.";
                    error_text.setText(text);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError e) {
                e.printStackTrace();
                list.setVisibility(View.INVISIBLE);
                load.setVisibility(View.INVISIBLE);
                error.setVisibility(View.VISIBLE);
                String text = "An error occurred somewhere, data could not be loaded. Please tap this text to refresh.";
                error_text.setText(text);
            }
        }) {
        };
        requestQueue.add(request);

    }

    public void refresh(View v) {
        if (isNetworkAvailable(this)) {
            load_data();
        } else {
            list.setVisibility(View.INVISIBLE);
            load.setVisibility(View.INVISIBLE);
            error.setVisibility(View.VISIBLE);
            String text = "Oops something went wrong. Please check your Network connection and tap this text to refresh.";
            error_text.setText(text);
        }
    }

    public boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE) != null) && connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED || (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI) != null) && connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED;
    }
}
