package danaapp.danaapp.bolus;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import danaapp.danaapp.MainApp;
import danaapp.danaapp.R;
import danaapp.danaapp.db.Bolus;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class BolusArrayAdapter extends ArrayAdapter<Bolus> {

    DecimalFormat formatNumber2place = new DecimalFormat("0.00");
    DateFormat formatDateToJustTime = new SimpleDateFormat("HH:mm");

    private final Activity context;
    private final List<Bolus> list;

    static class ViewHolder {
        protected TextView bolus_amount;
        protected TextView bolus_time;
        protected TextView bolus_iob;

        public Bolus bolus;
    }

    public BolusArrayAdapter(Activity context, List<Bolus> list) {
        super(context, R.layout.bolus_row, list);
        this.context = context;
        this.list = list;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = null;
        Bolus bolusAtPosition = null;
        if(list==null || list.size()==0) {
            bolusAtPosition = new Bolus();
            bolusAtPosition.timeStart = new Date();
        }
        else bolusAtPosition= list.get(position);
        if (convertView == null) {
            LayoutInflater inflator = context.getLayoutInflater();
            view = inflator.inflate(R.layout.bolus_row, null);
            final ViewHolder viewHolder = new ViewHolder();
            viewHolder.bolus_amount = (TextView) view.findViewById(R.id.bolus_amount);
            viewHolder.bolus_time = (TextView) view.findViewById(R.id.bolus_time);
            viewHolder.bolus_iob = (TextView) view.findViewById(R.id.bolus_iob);

            view.setTag(viewHolder);
            viewHolder.bolus_time.setTag(bolusAtPosition);
//            view.setOnLongClickListener(new View.OnLongClickListener() {
//                @Override
//                public boolean onLongClick(View v) {
//
//                    try {
//                        Bolus bol = (Bolus) ((ViewHolder)v.getTag()).bolus_time.getTag();
//
//                        //Dao<Bolus, Long> dao = MainApp.getDbHelper().getDaoBolus();
//                        //dao.delete(bol);
//                        Toast.makeText(getContext(),"Deleted",Toast.LENGTH_LONG).show();
//
//                    } catch (SQLException e) {
//
//                    }
//
//                    return true;
//                }
//            });
        } else {
            view = convertView;
            ((ViewHolder) view.getTag()).bolus_time.setTag(bolusAtPosition);
        }
        ViewHolder holder = (ViewHolder) view.getTag();
        holder.bolus_amount.setText(formatNumber2place.format( bolusAtPosition.amount));
        holder.bolus_time.setText(formatDateToJustTime.format(bolusAtPosition.timeStart));
        holder.bolus_iob.setText(
                formatNumber2place.format(bolusAtPosition.calcIob().iobContrib)
                +" "
                + formatNumber2place.format(bolusAtPosition.calcIobOpenAPS().iobContrib)
        );
        holder.bolus = bolusAtPosition;
        return view;
    }
}
