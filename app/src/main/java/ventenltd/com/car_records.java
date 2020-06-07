package ventenltd.com;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Objects;

public class car_records extends AppCompatActivity {
    RelativeLayout error, load, con, empty;
    TextView error_text, sub;
    RecyclerView list;
    ArrayList<CarData> data;
    String filter_start_year = "", filter_end_year = "", filter_gender = "", filter_colors = "", filter_countries = "";
    AutoCompleteTextView search;
    ImageView clear;
    car_records_adapter cra;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_records);

        Bundle extra = getIntent().getExtras();
        if (extra != null) {
            if (extra.getString("date") != null) {
                String[] filter_year = Objects.requireNonNull(extra.getString("date")).split(" - ");
                if (filter_year.length == 2) {
                    filter_start_year = filter_year[0];
                    filter_end_year = filter_year[1];
                }
            }

            if (extra.getString("gender") != null) {
                filter_gender = extra.getString("gender");
            }

            if (extra.getString("country") != null) {
                filter_countries = extra.getString("country");
            }

            if (extra.getString("color") != null) {
                filter_colors = extra.getString("color");
            }
        }

        list = findViewById(R.id.list);
        list.setNestedScrollingEnabled(false);
        list.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        error = findViewById(R.id.error);
        error_text = findViewById(R.id.error_text);
        load = findViewById(R.id.load);
        con = findViewById(R.id.con);
        sub = findViewById(R.id.sub);
        empty = findViewById(R.id.empty);
        search = findViewById(R.id.search);
        clear = findViewById(R.id.clear);

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (search.getText().length() > 0) {
                    clear.setVisibility(View.VISIBLE);
                } else if (search.getText().length() == 0) {
                    clear.setVisibility(View.INVISIBLE);
                }
                if (data.size() > 0) {
                    filter(search.getText().toString());
                    cra.notifyDataSetChanged();
                    list.setAdapter(cra);
                    list.scrollToPosition(0);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        check_permission();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void check_permission() {
        if (ActivityCompat.checkSelfPermission(car_records.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(car_records.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions(car_records.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);

            } else {
                ActivityCompat.requestPermissions(car_records.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }

        } else {
            download_file();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1) {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                download_file();
            } else {
                list.setVisibility(View.INVISIBLE);
                load.setVisibility(View.INVISIBLE);
                error.setVisibility(View.VISIBLE);
                String text_ = "Please enable storage permission for this device first, then tap this text to refresh.";
                error_text.setText(text_);
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @SuppressLint("StaticFieldLeak")
    public void download_file() {
        /*
        Please note that over here i initially tried to download the car_ownsers_data.csv
        file directly from the web to the mobile device but it appears that the link provided for the download
        is not a direct download link. So instead i kept the car_ownsers_data.csv file in a raw folder. So the app
        downloads the file from there to the mobile device instead.
        Below you will see my attempt to download the car_ownsers_data.csv file directly from the web
        with the link/url provided which failed.
        */
        if (isNetworkAvailable(this)) {
            File dir = new File(Environment.getExternalStorageDirectory() + "/Venten");
            boolean success = true;
            if (!dir.exists()) {
                success = dir.mkdir();
            }
            if (success) {
                File file = new File(Environment.getExternalStorageDirectory() + "/Venten/car_ownsers_data.csv");
                if (!file.exists()) {

                    //Downloading car_ownsers_data.csv file from raw folder to Venten folder on mobile device.
                    try {
                        FileOutputStream data = new FileOutputStream(
                                Environment.getExternalStorageDirectory() + "/Venten/car_ownsers_data.csv");
                        InputStream is = getResources().openRawResource(R.raw.car_ownsers_data);
                        int a = is.available();
                        byte[] buf = new byte[a];
                        int check = is.read(buf, 0, a);
                        if (check > 0) {
                            data.write(buf);
                            data.flush();
                            data.close();
                            load_data();
                        }
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                        list.setVisibility(View.INVISIBLE);
                        load.setVisibility(View.INVISIBLE);
                        error.setVisibility(View.VISIBLE);
                        String text_ = "An error occurred with the file.";
                        error_text.setText(text_);
                    } catch (IOException e) {
                        e.printStackTrace();
                        list.setVisibility(View.INVISIBLE);
                        load.setVisibility(View.INVISIBLE);
                        error.setVisibility(View.VISIBLE);
                        String text_ = "An error occurred with the file.";
                        error_text.setText(text_);
                    }

                    /*The code below was my attempt to download the car_ownsers_data.csv file
                    directly from the web with the link provided which failed.
                    */

                    /*pd = new ProgressDialog(this);
                    pd.setTitle("Please wait");
                    pd.setCancelable(false);
                    pd.setMessage("Downloading necessary file");
                    pd.show();

                    new AsyncTask<Void, Void, Void>() {

                        @Override
                        protected Void doInBackground(Void... voids) {
                        String DownloadUrl = "https://drive.google.com/file/d/1giBv3pK6qbOPo0Y02H-wjT9ULPksfBCm/view";
                        DownloadManager.Request rq = new DownloadManager.Request(Uri.parse(DownloadUrl));
                        rq.setDescription("car_ownsers_data.csv File");
                        rq.setTitle("car_ownsers_data.csv");
                        rq.setVisibleInDownloadsUi(false);
                        rq.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);
                        rq.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
                        rq.setMimeType("application/octet-stream");
                        rq.allowScanningByMediaScanner();
                        rq.setDestinationInExternalPublicDir("/Venten", "car_ownsers_data.csv");

                        DownloadManager mg = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                        Objects.requireNonNull(mg).enqueue(rq);
                        return null;
                        }

                        @Override
                        protected void onPostExecute(Void aVoid) {
                        runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                        if (DownloadManager.STATUS_SUCCESSFUL == 8) {
                        pd.dismiss();
                        load_data();
                        } else {
                        pd.dismiss();
                        list.setVisibility(View.INVISIBLE);
                        load.setVisibility(View.INVISIBLE);
                        error.setVisibility(View.VISIBLE);
                        String text_ = "File download failed. Please tap this text to refresh";
                        error_text.setText(text_);
                        }
                        }
                        });
                        cancel(true);
                        }
                        }.execute();*/
                } else {
                    load_data();
                }
            } else {
                list.setVisibility(View.INVISIBLE);
                load.setVisibility(View.INVISIBLE);
                error.setVisibility(View.VISIBLE);
                String text_ = "Venten Directory could not be created.";
                error_text.setText(text_);
            }
        } else {
            list.setVisibility(View.INVISIBLE);
            load.setVisibility(View.INVISIBLE);
            error.setVisibility(View.VISIBLE);
            String text_ = "Please check your network connection, then tap this text to refresh.";
            error_text.setText(text_);
        }
    }

    @SuppressLint("StaticFieldLeak")
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void load_data() {
        list.setVisibility(View.INVISIBLE);
        load.setVisibility(View.VISIBLE);
        error.setVisibility(View.INVISIBLE);
        String text = "";
        error_text.setText(text);

        data = new ArrayList<>();
        cra = new car_records_adapter(data, car_records.this);
        list.setAdapter(cra);

        String file_path = Environment.getExternalStorageDirectory().toString() + "/" + "Venten" + "/" + "car_ownsers_data.csv";
        File file = new File(file_path);

        try {
            final BufferedReader reader = new BufferedReader(
                    new FileReader(file)
            );
            new AsyncTask<Void, Void, Void>() {

                @Override
                protected Void doInBackground(Void... voids) {
                    try {
                        String line;
                        int i = 0;
                        while ((line = reader.readLine()) != null) {
                            if (i > 0) {
                                String[] tokens = line.split(",");
                                String add = filteration_function(tokens, filter_gender, filter_start_year, filter_end_year, filter_countries, filter_colors);
                                if (add.equals("YES")) {
                                    data.add(new CarData(tokens[1], tokens[2], tokens[3], tokens[4], tokens[5], tokens[7], tokens[8], tokens[9], tokens[10], Integer.parseInt(tokens[6])));
                                }
                            }
                            i++;
                        }
                        reader.close();

                    } catch (IOException e) {
                        e.printStackTrace();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                list.setVisibility(View.INVISIBLE);
                                load.setVisibility(View.INVISIBLE);
                                error.setVisibility(View.VISIBLE);
                                String text_ = "An error occurred somewhere, data could not be loaded. Please tap this text to refresh.";
                                error_text.setText(text_);
                            }
                        });
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    if (data.size() > 0) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                cra = new car_records_adapter(data, car_records.this);
                                list.setAdapter(cra);
                                list.setVisibility(View.VISIBLE);
                                load.setVisibility(View.INVISIBLE);
                                error.setVisibility(View.INVISIBLE);
                                String textt = "";
                                error_text.setText(textt);

                                RelativeLayout.LayoutParams par = new RelativeLayout.LayoutParams(
                                        RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT
                                );
                                par.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                                con.setLayoutParams(par);
                                String text;
                                if (data.size() == 1) {
                                    text = "1 record found.";
                                } else {
                                    text = data.size() + " records found.";
                                }
                                sub.setText(text);
                            }
                        });
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                list.setVisibility(View.INVISIBLE);
                                load.setVisibility(View.INVISIBLE);
                                error.setVisibility(View.VISIBLE);
                                String text_ = "Oops, sorry but nothing matches your filter.";
                                error_text.setText(text_);

                                RelativeLayout.LayoutParams par = new RelativeLayout.LayoutParams(
                                        0, 0
                                );
                                par.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                                con.setLayoutParams(par);
                                String text = "";
                                sub.setText(text);
                            }
                        });
                    }
                    cancel(true);
                }
            }.execute();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            list.setVisibility(View.INVISIBLE);
            load.setVisibility(View.INVISIBLE);
            error.setVisibility(View.VISIBLE);
            String text_ = "An error occurred, file not found.";
            error_text.setText(text_);
        }
    }

    //This is the function that performs the filtering. The unit test was also done on this function/method.
    public String filteration_function(String[] tokens, String filter_gender, String filter_start_year, String filter_end_year, String filter_countries, String filter_colors) {
        String add = "NO";
        if (tokens.length == 11) {
            add = "YES";
            if (!filter_gender.equals("All")) {
                if (!tokens[8].equals(filter_gender)) {
                    add = "NO";
                }
            }

            if (Integer.parseInt(tokens[6]) < Integer.parseInt(filter_start_year) || Integer.parseInt(tokens[6]) > Integer.parseInt(filter_end_year)) {
                add = "NO";
            }

            if (!filter_countries.contains(tokens[4])) {
                add = "NO";
            }

            if (!filter_colors.equals("All")) {
                if (!filter_colors.contains(tokens[7])) {
                    add = "NO";
                }
            }
        }
        return add;
    }

    public void back(View v) {
        finish();
    }

    public void clear(View v) {
        search.setText("");
    }

    public void filter(String text) {
        ArrayList<CarData> suggestions = new ArrayList<>();
        for (CarData item : data) {
            String fullname = item.getLast_name() + " " + item.getFirst_name();
            if (fullname.toLowerCase().contains(text.toLowerCase())) {
                suggestions.add(item);
            } else if (item.getEmail().toLowerCase().contains(text.toLowerCase())) {
                suggestions.add(item);
            } else if (item.getCountry().toLowerCase().contains(text.toLowerCase())) {
                suggestions.add(item);
            } else if (item.getModel().toLowerCase().contains(text.toLowerCase())) {
                suggestions.add(item);
            } else if (Integer.toString(item.getYear()).toLowerCase().contains(text.toLowerCase())) {
                suggestions.add(item);
            } else if (item.getColor().toLowerCase().contains(text.toLowerCase())) {
                suggestions.add(item);
            } else if (item.getGender().toLowerCase().contains(text.toLowerCase())) {
                suggestions.add(item);
            } else if (item.getTitle().toLowerCase().contains(text.toLowerCase())) {
                suggestions.add(item);
            } else if (item.getBio().toLowerCase().contains(text.toLowerCase())) {
                suggestions.add(item);
            }
        }
        if (suggestions.size() == 0) {
            empty.setVisibility(View.VISIBLE);
        } else {
            empty.setVisibility(View.INVISIBLE);
        }
        cra.filter(suggestions);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void refresh(View v) {
        check_permission();
    }

    public boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE) != null) && connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED || (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI) != null) && connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED;
    }
}

