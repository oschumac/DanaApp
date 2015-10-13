package danaapp.danaapp.tempBasal;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import danaapp.danaapp.R;
import danaapp.danaapp.Settings;
import danaapp.danaapp.calc.IobCalc;
import danaapp.danaapp.db.TempBasal;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class TempBasalArrayAdapter extends ArrayAdapter<TempBasal> {

    DecimalFormat formatNumber2place = new DecimalFormat("0.00");
    DateFormat formatDateToJustTime = new SimpleDateFormat("HH:mm");
    DateFormat formatDateToJustTimeDiif;

    {
        formatDateToJustTimeDiif = new SimpleDateFormat("HH:mm");
        formatDateToJustTimeDiif.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    private final Activity context;
    private List<TempBasal> list;

    static class ViewHolder {
        protected TextView tempBasal_timeStart;
        protected TextView tempBasal_timeEnd;
        protected TextView tempBasal_timeDuration;
        protected TextView tempBasal_percent;


        public TextView tempBasal_iob;
    }

    public TempBasalArrayAdapter(Activity context, List<TempBasal> list) {
        super(context, R.layout.tempbasal_row, list);
        this.context = context;
        this.list = list;
        if(this.list == null) this.list = new ArrayList<>();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = null;
        TempBasal tempBasal = list.get(position);
        if (convertView == null) {
            LayoutInflater inflator = context.getLayoutInflater();
            view = inflator.inflate(R.layout.tempbasal_row, null);
            final ViewHolder viewHolder = new ViewHolder();
            viewHolder.tempBasal_timeStart = (TextView) view.findViewById(R.id.tempBasal_timeStart);
            viewHolder.tempBasal_percent = (TextView) view.findViewById(R.id.tempBasal_percent);
            viewHolder.tempBasal_timeEnd = (TextView) view.findViewById(R.id.tempBasal_timeEnd);
            viewHolder.tempBasal_iob = (TextView) view.findViewById(R.id.tempBasal_iob);
            viewHolder.tempBasal_timeDuration = (TextView) view.findViewById(R.id.tempBasal_timeDuration);

            view.setTag(viewHolder);
            viewHolder.tempBasal_percent.setTag(tempBasal);
        } else {
            view = convertView;
            ((ViewHolder) view.getTag()).tempBasal_timeStart.setTag(tempBasal);
        }
        ViewHolder holder = (ViewHolder) view.getTag();
        holder.tempBasal_percent.setText(
                formatNumber2place.format((tempBasal.tempRatio-tempBasal.baseRatio)/100d) + " (" +
                Integer.toString(tempBasal.percent)+"%)"
        );
        holder.tempBasal_timeStart.setText(formatDateToJustTime.format(tempBasal.timeStart));
        Date tempBasalTimePlannedEnd = tempBasal.getPlannedTimeEnd();
        if(tempBasal.timeEnd!=null) {
            holder.tempBasal_timeEnd.setText(formatDateToJustTime.format(tempBasal.timeEnd));
        } else {
            holder.tempBasal_timeEnd.setText(formatDateToJustTime.format(tempBasalTimePlannedEnd));
        }

        holder.tempBasal_timeDuration.setText(formatDateToJustTimeDiif
            .format(
                    new Date(tempBasal.getCurrentTimeEnd().getTime() - tempBasal.timeStart.getTime())
            )
        );

        IobCalc.Iob iob = tempBasal.calcIob();
        holder.tempBasal_iob.setText(formatNumber2place.format(iob.iobContrib)
            + " " + formatNumber2place.format(iob.activityContrib*Settings.insSensitivity));
        return view;
    }
}
