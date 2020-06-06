package ventenltd.com;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class filter_adapter extends RecyclerView.Adapter<filter_adapter.ViewHolder> {
    private ArrayList<String> dates, genders, countries, colors;
    private Context context;

    filter_adapter(ArrayList<String> dates, ArrayList<String> genders, ArrayList<String> countries, ArrayList<String> colors, Context context) {
        this.dates = dates;
        this.genders = genders;
        this.countries = countries;
        this.colors = colors;
        this.context = context;
    }

    @NonNull
    @Override
    public filter_adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View v = layoutInflater.inflate(R.layout.filter_adapter_layout, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull filter_adapter.ViewHolder holder, final int position) {
        int dp = (int) context.getResources().getDimension(R.dimen.radius);
        int dpp = (int) context.getResources().getDimension(R.dimen.padding);
        if (position == 0) {
            holder.parent.setPadding(dpp, dpp, dpp, dp);
        } else if (position == dates.size() - 1) {
            holder.parent.setPadding(dpp, dp, dpp, dpp);
        }

        String date_text = " " + dates.get(position);
        String gender_text = " " + genders.get(position);
        String country_text = " " + countries.get(position);
        String color_text = " " + colors.get(position);
        holder.date.setText(date_text);
        holder.gender.setText(gender_text);
        holder.country.setText(country_text);
        holder.color.setText(color_text);

        holder.parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, car_records.class);
                i.putExtra("date", dates.get(position));
                i.putExtra("gender", genders.get(position));
                i.putExtra("country", countries.get(position));
                i.putExtra("color", colors.get(position));
                context.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dates.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView date, gender, country, color;
        RelativeLayout parent;

        ViewHolder(@NonNull View v) {
            super(v);
            date = v.findViewById(R.id.date);
            gender = v.findViewById(R.id.gender);
            country = v.findViewById(R.id.country);
            color = v.findViewById(R.id.color);
            parent = v.findViewById(R.id.parent);
        }
    }
}
