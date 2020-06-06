package ventenltd.com;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class car_records_adapter extends RecyclerView.Adapter<car_records_adapter.ViewHolder> {
    private ArrayList<CarData> data;
    private Context context;

    car_records_adapter(ArrayList<CarData> data, Context context) {
        this.data = data;
        this.context = context;
    }

    @NonNull
    @Override
    public car_records_adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View v = layoutInflater.inflate(R.layout.car_records_adapter_layout, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull car_records_adapter.ViewHolder holder, int position) {
        int dp = (int) context.getResources().getDimension(R.dimen.radius);
        int dpp = (int) context.getResources().getDimension(R.dimen.padding);
        if (position == 0) {
            holder.parent.setPadding(dpp, dpp, dpp, dp);
        } else if (position == data.size() - 1) {
            holder.parent.setPadding(dpp, dp, dpp, dpp);
        }

        CarData carData = data.get(position);

        String fullname_text = " " + carData.getLast_name() + " " + carData.getFirst_name();
        String email_text = " " + carData.getEmail();
        String country_text = " " + carData.getCountry();
        String model_text = " " + carData.getModel();
        String year_text = " " + carData.getYear();
        String color_text = " " + carData.getColor();
        String gender_text = " " + carData.getGender();
        String title_text = " " + carData.getTitle();
        String bio_text = " " + carData.getBio();
        holder.fullname.setText(fullname_text);
        holder.email.setText(email_text);
        holder.country.setText(country_text);
        holder.model.setText(model_text);
        holder.year.setText(year_text);
        holder.color.setText(color_text);
        holder.gender.setText(gender_text);
        holder.title.setText(title_text);
        holder.bio.setText(bio_text);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    void filter(ArrayList<CarData> filteredlist) {
        data = filteredlist;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView fullname, email, country, model, year, color, gender, title, bio;
        RelativeLayout parent;

        ViewHolder(@NonNull View v) {
            super(v);
            fullname = v.findViewById(R.id.fullname);
            email = v.findViewById(R.id.email);
            country = v.findViewById(R.id.country);
            model = v.findViewById(R.id.model);
            year = v.findViewById(R.id.year);
            color = v.findViewById(R.id.color);
            gender = v.findViewById(R.id.gender);
            title = v.findViewById(R.id.title);
            bio = v.findViewById(R.id.bio);
            parent = v.findViewById(R.id.parent);
        }
    }
}
