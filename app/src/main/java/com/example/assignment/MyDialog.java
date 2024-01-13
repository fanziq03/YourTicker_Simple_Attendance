package com.example.assignment;

import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import java.util.ArrayList;

public class MyDialog extends DialogFragment {
    public static final String CLASS_ADD_DIALOG = "addClass";
    public static final String CLASS_UPDATE_DIALOG = "updateClass";
    public static final String STUDENT_ADD_DIALOG = "addStudent";
    public static final String STUDENT_UPDATE_DIALOG = "updateStudent";
    private String roll;
    private String name;

    private OnClickListener listener;

    private ArrayList<StudentItem> studentItems;

    public void setStudentItems(ArrayList<StudentItem> studentItems) {
        this.studentItems = studentItems;
    }

    public MyDialog(String roll, String name) {
        this.roll = roll;
        this.name = name;
    }

    public MyDialog() {

    }

    public interface OnClickListener{
        void onClick(String text1, String text2);
    }

    public void setListener(OnClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = null;
        if(getTag().equals(CLASS_ADD_DIALOG)) dialog = getAddClassDialog();
        if(getTag().equals(STUDENT_ADD_DIALOG)) dialog = getAddStudentDialog();
        if(getTag().equals(CLASS_UPDATE_DIALOG)) dialog = getUpdateClassDialog();
        if(getTag().equals(STUDENT_UPDATE_DIALOG)) dialog = getUpdateStudentDialog();

        //dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        return dialog;
    }

    private Dialog getUpdateClassDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog, null);
        builder.setView(view);

        AlertDialog dialog = builder.create();

        TextView title = view.findViewById(R.id.titleDialog);
        title.setText("Update Class");

        EditText class_edt = view.findViewById(R.id.edt01);
        EditText subject_edt = view.findViewById(R.id.edt02);

        class_edt.setHint("Class");
        subject_edt.setHint("Subject");

        // Set pre-filled data for updating
        class_edt.setText(roll);
        subject_edt.setText(name);

        Button cancel = view.findViewById(R.id.cancel_btn);
        Button add = view.findViewById(R.id.add_btn);
        add.setText("Update");

        cancel.setOnClickListener(v -> dismiss());
        add.setOnClickListener(v -> {
            String className = class_edt.getText().toString();
            String subName = subject_edt.getText().toString();
            listener.onClick(className, subName);
            dialog.dismiss();
        });

        return dialog;
    }

    public void setClassData(String className, String subjectName) {
        this.roll = className; // Reuse the 'roll' field for className
        this.name = subjectName; // Reuse the 'name' field for subjectName
    }

    private Dialog getUpdateStudentDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog, null);
        builder.setView(view);

        AlertDialog dialog = builder.create();
        //dialog.show(); Akan crash kalau guna

        TextView title = view.findViewById(R.id.titleDialog);
        title.setText("Update Student");

        EditText roll_edt = view.findViewById(R.id.edt01);
        EditText name_edt = view.findViewById(R.id.edt02);

        roll_edt.setHint("Roll");
        name_edt.setHint("Name");
        Button cancel = view.findViewById(R.id.cancel_btn);
        Button add = view.findViewById(R.id.add_btn);
        add.setText("Update");

        roll_edt.setText(roll);
        roll_edt.setEnabled(false);
        name_edt.setText(name);

        cancel.setOnClickListener(v -> dismiss());
        add.setOnClickListener(v -> {
            String roll = roll_edt.getText().toString();
            String name = name_edt.getText().toString();
            listener.onClick(roll, name);
            dismiss();
        });

        return builder.create();
    }
    private Dialog getAddStudentDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog, null);
        builder.setView(view);

        AlertDialog dialog = builder.create();

        TextView title = view.findViewById(R.id.titleDialog);
        title.setText("Add Student");

        EditText roll_edt = view.findViewById(R.id.edt01);
        EditText name_edt = view.findViewById(R.id.edt02);

        roll_edt.setHint("Roll");
        name_edt.setHint("Name");

        // Automatically set the roll and disable editing
        int nextRoll = getNextRoll();
        roll_edt.setText(String.valueOf(nextRoll));
        roll_edt.setEnabled(false);

        Button cancel = view.findViewById(R.id.cancel_btn);
        Button add = view.findViewById(R.id.add_btn);

        cancel.setOnClickListener(v -> dialog.dismiss());
        add.setOnClickListener(v -> {
            String roll = roll_edt.getText().toString();
            String name = name_edt.getText().toString();
            if (!roll.isEmpty() && !name.isEmpty()) {
                listener.onClick(roll, name);
                dialog.dismiss();
            }
        });

        return dialog;
    }

    private int getNextRoll() {
        int maxRoll = 0;
        for (StudentItem student : studentItems) {
            try {
                int roll = Integer.parseInt(student.getRoll());
                if (roll > maxRoll) {
                    maxRoll = roll;
                }
            } catch (NumberFormatException e) {
                Log.e("MyDialog", "Invalid roll format: " + student.getRoll());
            }
        }
        return maxRoll + 1;
    }

    public void showAddStudentDialog(FragmentManager manager, String tag, ArrayList<StudentItem> studentItems) {
        this.studentItems = studentItems;
        show(manager, tag);
    }


    private Dialog getAddClassDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog, null);
        builder.setView(view);

        AlertDialog dialog = builder.create();
        //dialog.show(); Akan crash kalau guna

        TextView title = view.findViewById(R.id.titleDialog);
        title.setText("Add Class");

        EditText class_edt = view.findViewById(R.id.edt01);
        EditText subject_edt = view.findViewById(R.id.edt02);

        class_edt.setHint("Class");
        subject_edt.setHint("Subject");
        Button cancel = view.findViewById(R.id.cancel_btn);
        Button add = view.findViewById(R.id.add_btn);

        cancel.setOnClickListener(v -> dismiss());
        add.setOnClickListener(v -> {
            String className = class_edt.getText().toString();
            String subName = subject_edt.getText().toString();
            listener.onClick(className, subName);
            dialog.dismiss();
        });

        return builder.create();
    }
}
