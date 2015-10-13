package danaapp.danaapp.bolus;

import android.app.FragmentManager;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import danaapp.danaapp.MainActivity;
import danaapp.danaapp.R;

import java.text.DecimalFormat;

public class BolusUI {
    private final MainActivity mainActivity;
    public TextView bolusStatus;
    public ProgressBar bolusProgressBar;
    public Button bolusButton;
    public static final DecimalFormat bolusNumberFormat = new DecimalFormat("0.0");
    public double bolusAmount = 0;
    public boolean bolusInProgress = false;
    public double bolusDeliveredAmountSoFar = 0;

    public BolusUI(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    public void bolusInit() {
        bolusStatus = (TextView) mainActivity.findViewById(R.id.bolusStatus);
        bolusProgressBar = (ProgressBar) mainActivity.findViewById(R.id.bolusProgressBar);
        bolusButton = (Button) mainActivity.findViewById(R.id.bolusButton);

        bolusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!bolusInProgress) {
                    FragmentManager manager = mainActivity.getFragmentManager();
                    BolusDialogFragment bolusDialogFragment = new BolusDialogFragment();
                    bolusDialogFragment.show(manager, "BolusDialog");
                }
                if (bolusInProgress) {
                    bolusStop();
                }
            }
        });
    }

    public void bolusStop() {
//        bolusInProgress = false;
        mainActivity.bolusStop();
    }

    public void bolusStart(double amount) {
        this.bolusAmount = amount;

        bolusInProgress = true;
        bolusDeliveredAmountSoFar = 0;
        //mHandlerElapsedTime.sendMessageDelayed(Message.obtain(mHandlerElapsedTime, 0), 100);
    }

    public void bolusFinished() {
        mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                bolusStatus.setText("Delivered ");
                bolusButton.setText("bolus");
            }
        });
        bolusInProgress = false;
    }

    public void bolusDelivering() {

        mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                bolusButton.setText("STOP");
                bolusStatus.setText(
                        "Delivering "
                                + bolusNumberFormat.format(bolusAmount-bolusDeliveredAmountSoFar) + "U of "
                                + bolusNumberFormat.format(bolusAmount) + "U");
                bolusProgressBar.setProgress((int) (bolusDeliveredAmountSoFar / bolusAmount * 100d));
                Log.d("Progress", String.valueOf(bolusDeliveredAmountSoFar));
            }
        });

    }

//    public Handler mHandlerElapsedTime = new Handler() {
//        public void handleMessage(Message m) {
//            bolusDelivering();
//            bolusDeliveredAmountSoFar = bolusDeliveredAmountSoFar + 1 / 120d;
//            if(bolusAmount <= bolusDeliveredAmountSoFar || !bolusInProgress) {
//                bolusDeliveredAmountSoFar = 0;
//                bolusFinished();
//            } else {
//                mHandlerElapsedTime.sendMessageDelayed(Message.obtain(mHandlerElapsedTime, 0), 100);
//            }
//        }
//
//    };

}