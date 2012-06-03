package org.rhq.pocket;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import org.rhq.pocket.helper.DisplayRange;

/**
 * Dialog to pick a metric display range.
 * Currently only the duration before now is supported
 * @author Heiko W. Rupp
 */
public class MetricDisplayRangeDialogFragment extends DialogFragment implements SeekBar.OnSeekBarChangeListener,
        View.OnClickListener, RadioGroup.OnCheckedChangeListener {

    private SeekBar seekBar;
    private TextView valueView;
    private Button pickButton;
    private Button cancelButton;
    private RadioGroup radioGroup;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.metric_display_range_dialog_fragment, container);

        seekBar = (SeekBar) v.findViewById(R.id.seekBar);
        valueView = (TextView) v.findViewById(R.id.valueView);
        seekBar.setOnSeekBarChangeListener(this);
        seekBar.setMax(60);

        pickButton = (Button) v.findViewById(R.id.pick_resource);
        cancelButton = (Button) v.findViewById(R.id.back);
        pickButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);

        radioGroup = (RadioGroup) v.findViewById(R.id.unit_selector);
        radioGroup.setOnCheckedChangeListener(this);

        return v;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setTitle(R.string.pick_display_range);

        return dialog;
    }

    @Override
    public void onResume() {
        super.onResume();

        int id;
        int maxVal ;
        switch (RHQPocket.getInstance().displayRangeUnits) {
        case MINUTE:
            id = R.id.minutes_button;
            maxVal = 120; // 2 h
            break;
        case HOUR:
            id = R.id.hours_button;
            maxVal = 48; // 4 days
            break;
        case DAY:
            id = R.id.days_button;
            maxVal = 21; // 3 weeks
            break;
        case WEEK:
            id = R.id.weeks_button;
            maxVal = 52;
            break;
        default:
            id = R.id.hours_button;
            maxVal = 48; // 4 days
        }

        radioGroup.check(id);
        seekBar.setProgress(RHQPocket.getInstance().displayRangeValue);
        seekBar.setMax(maxVal);
        valueView.setText(String.valueOf(RHQPocket.getInstance().displayRangeValue));

    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        valueView.setText(String.valueOf(i));
        // A duration of zero makes no sense
        if (pickButton!=null) {
            if (i==0)
                pickButton.setEnabled(false);
            else
                pickButton.setEnabled(true);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) { }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) { }

    @Override
    public void onClick(View view) {
        if (view.equals(pickButton)) {
            RHQPocket.getInstance().displayRangeValue = Integer.valueOf(valueView.getText().toString());
            int checkedRadioButtonId = radioGroup.getCheckedRadioButtonId();
            DisplayRange val ;
            switch (checkedRadioButtonId) {
            case R.id.minutes_button:
                val=DisplayRange.MINUTE; break;
            case R.id.hours_button:
                val=DisplayRange.HOUR; break;
            case R.id.days_button:
                val=DisplayRange.DAY; break;
            case R.id.weeks_button:
                val=DisplayRange.WEEK; break;

            default:
                val = DisplayRange.HOUR;
            }

            RHQPocket.getInstance().displayRangeUnits = val;
            Activity activity = getActivity();
            if (activity instanceof Refreshable) {
                ((Refreshable)activity).refresh(null);
            }
        }
        dismiss();
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {
        int maxVal;
        switch (i) {
        case R.id.minutes_button: maxVal = 120;
            break;
        case R.id.hours_button: maxVal = 48;
            break;
        case R.id.days_button: maxVal = 21;
            break;
        case R.id.weeks_button: maxVal = 52;
            break;
        default:
            maxVal = 48;
        }
        seekBar.setMax(maxVal);
    }
}
