package me.mattrichard.driversed;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;


public class EditLessonFragment extends Fragment {

    private OnLessonSaveListener mListener;

    private Button startButton;
    private Button stopButton;
    private Button clearButton;
    private MenuItem saveAction;
    private Spinner lessonTypeSpinner;
    private Spinner conditionsSpinner;
    private TextView dateText;
    private TextView hoursText;
    private Long startTime = null;

    public Lesson lesson;

    public EditLessonFragment() {
        // Required empty public constructor
        this.lesson = new Lesson();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.fragment_edit_lesson, container, false);

        // Find views
        startButton = (Button) layout.findViewById(R.id.buttonStart);
        stopButton = (Button) layout.findViewById(R.id.buttonStop);
        clearButton = (Button) layout.findViewById(R.id.buttonClear);
        lessonTypeSpinner = (Spinner) layout.findViewById(R.id.lessonTypeSpinner);
        conditionsSpinner = (Spinner) layout.findViewById(R.id.conditionSpinner);
        dateText = (TextView) layout.findViewById(R.id.dateText);
        hoursText = (TextView) layout.findViewById(R.id.hoursText);

        // Initialize text
        dateText.setText(new SimpleDateFormat("MM/dd/yyyy").format(Calendar.getInstance().getTime()));
        hoursText.setText("-");

        // Hook up buttons to onClick callbacks
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onPressStart(view);
            }
        });
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onPressStop(view);
            }
        });
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onPressClear(view);
            }
        });

        // Tell activity that this fragment has action bar items to add
        setHasOptionsMenu(true);

        return layout;
    }

    public void onPressStart(View v) {
        startTime = System.currentTimeMillis();
        hoursText.setText("In progress");

        stopButton.setEnabled(true);
        startButton.setEnabled(false);
        clearButton.setEnabled(false);
        saveAction.setEnabled(false);
    }

    public void onPressStop(View v) {
        lesson.numHours += (System.currentTimeMillis() - startTime) / (double) (1000 * 60 * 60);
        hoursText.setText(String.format("%.2f", lesson.numHours));
        startTime = null;

        stopButton.setEnabled(false);
        startButton.setEnabled(true);
        clearButton.setEnabled(true);
        saveAction.setEnabled(true);
    }

    public void onPressClear(View v) {
        lesson.numHours = 0;
        hoursText.setText("-");
        dateText.setText(new SimpleDateFormat("MM/dd/yyyy").format(Calendar.getInstance().getTime()));
    }

    public void onPressSave() {
        String toast;
        if (lesson.numHours > 0) {
            toast = "Drive saved";
            lesson.save();
        } else {
            toast = "Won't save 0-hour drive";
        }
        Toast.makeText(getActivity(), toast, Toast.LENGTH_SHORT).show();

        // pass control to activity
        mListener.onLessonSave();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.action_bar_edit_lesson, menu);
        saveAction = menu.findItem(R.id.action_save);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_save:
                onPressSave(); break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnLessonSaveListener) {
            mListener = (OnLessonSaveListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnLessonSaveListener {
        void onLessonSave();
    }
}
