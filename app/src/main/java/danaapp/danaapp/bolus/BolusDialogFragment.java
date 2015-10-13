package danaapp.danaapp.bolus;

import android.app.Activity;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.*;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import danaapp.danaapp.R;

public class BolusDialogFragment extends DialogFragment implements OnClickListener {

    Button bolusDialogDeliverButton;
    Communicator communicator;
    TextView amount;
    @Override
    public void onAttach(Activity activity) {

        super.onAttach(activity);

        if (activity instanceof Communicator) {
            communicator = (Communicator) getActivity();

        } else {
            throw new ClassCastException(activity.toString()
                    + " must implemenet BolusDialogFragment.Communicator");
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bolus_fragment, null, false);

        bolusDialogDeliverButton = (Button) view.findViewById(R.id.bolusDialogDeliverButton);

        bolusDialogDeliverButton.setOnClickListener(this);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        amount = (TextView)view.findViewById(R.id.bolus_amount);

        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bolusDialogDeliverButton:

                double amount = Double.parseDouble(this.amount.getText().toString());
                if(amount >8) {
                    this.amount.setText("");
                } else {
                    dismiss();
                    communicator.bolusDialogDeliver(amount);
                }
                break;
        }

    }

    public interface Communicator {
        void bolusDialogDeliver(double amount);
    }

}