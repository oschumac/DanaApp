package danaapp.danaapp.carbs;

import android.app.Activity;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.*;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import danaapp.danaapp.R;

public class CarbsDialogFragment extends DialogFragment implements OnClickListener {

    Button carbsDialogDeliverButton;
    Communicator communicator;
    TextView amount;
    @Override
    public void onAttach(Activity activity) {

        super.onAttach(activity);

        if (activity instanceof Communicator) {
            communicator = (Communicator) getActivity();

        } else {
            throw new ClassCastException(activity.toString()
                    + " must implemenet CarbsDialogFragment.Communicator");
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.carbs_fragment, null, false);

        carbsDialogDeliverButton = (Button) view.findViewById(R.id.carbsDialogDeliverButton);

        carbsDialogDeliverButton.setOnClickListener(this);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        amount = (TextView)view.findViewById(R.id.carbsAmount);

        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.carbsDialogDeliverButton:

                int amount = Integer.parseInt(this.amount.getText().toString());
                if(amount >80) {
                    this.amount.setText("");
                } else {
                    dismiss();
                    communicator.carbsDialogDeliver(amount);
                }
                break;
        }

    }

    public interface Communicator {
        void carbsDialogDeliver(int amount);
    }

}