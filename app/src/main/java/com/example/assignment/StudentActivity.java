package com.example.assignment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class StudentActivity extends AppCompatActivity {

    Toolbar toolbar;
    private String classId;
    private String className;
    private String subjectName;
    private int position;
    private RecyclerView recyclerView;
    private StudentAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<StudentItem> studentItems = new ArrayList<>();

    private DatabaseReference classRef;
    private DatabaseReference studentRef;
    private MyCalendar calendar;
    private TextView subtitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student);

        calendar = new MyCalendar();

        studentItems = new ArrayList<>();

        Intent intent = getIntent();
        classId = intent.getStringExtra("classId");
        className = intent.getStringExtra("className");
        subjectName = intent.getStringExtra("subjectName");
        position = intent.getIntExtra("position", -1);

        setToolbar();
        recyclerView = findViewById(R.id.student_recycler);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new StudentAdapter(this, studentItems);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(position -> changeStatus(position));

        // Update the references to use the class-specific path
        classRef = FirebaseDatabase.getInstance().getReference().child("Classes").child(classId);
        studentRef = classRef.child("Students");

        // Retrieve classDate from Firebase and set it in the subtitle
        classRef.child("classDate").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String classDate = snapshot.getValue(String.class);
                subtitle.setText(subjectName + " | " + classDate);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle errors
            }
        });

        studentRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                studentItems.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    if (dataSnapshot != null) {
                        // Retrieve the studentId from the Firebase snapshot key
                        String studentId = dataSnapshot.getKey();

                        // Retrieve the StudentItem data
                        StudentItem retrievedStudent = dataSnapshot.getValue(StudentItem.class);

                        // Set the retrieved studentId in the StudentItem
                        if (retrievedStudent != null) {
                            retrievedStudent.setStudentId(studentId);
                            studentItems.add(retrievedStudent);
                        }
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle errors
            }
        });
    }

    private void changeStatus(int position) {
        String status = studentItems.get(position).getStatus();

        if (status.equals("P")) status = "A";
        else status = "P";

        studentItems.get(position).setStatus(status);
        adapter.notifyItemChanged(position);
        updateStudent(position, studentItems.get(position).getRoll(), studentItems.get(position).getName());
    }

    private void setToolbar() {
        toolbar = findViewById(R.id.toolbar);
        TextView title = toolbar.findViewById(R.id.title_toolbar);
        subtitle = toolbar.findViewById(R.id.subtitle_toolbar);
        ImageButton back = toolbar.findViewById(R.id.back);

        title.setText(className);
        back.setOnClickListener(v -> onBackPressed());
        toolbar.inflateMenu(R.menu.student_menu);
        toolbar.setOnMenuItemClickListener(menuItem -> onMenuItemClick(menuItem));
    }

    private boolean onMenuItemClick(MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.add_student) {
            showAddStudentDialog();
        } else if (menuItem.getItemId() == R.id.show_calendar) {
            showCalendar();
        }
        return true;
    }

    private void showCalendar() {
        calendar.show(getSupportFragmentManager(), "");
        calendar.setOnCalendarOnClickListener(this::onCalendarOkClicked);
    }

    private void onCalendarOkClicked(int year, int month, int day) {
        String formattedDate = String.format("%04d-%02d-%02d", year, month + 1, day);
        subtitle.setText(subjectName + " | " + formattedDate);

        // Update classDate in Firebase
        classRef.child("classDate").setValue(formattedDate);
    }

    private void showAddStudentDialog() {
        MyDialog dialog = new MyDialog();
        dialog.show(getSupportFragmentManager(), MyDialog.STUDENT_ADD_DIALOG);
        dialog.setListener((roll, name) -> addStudent(roll, name));
    }

    private void addStudent(String roll, String name) {
        // Generate a unique student ID using Firebase push key
        String studentId = studentRef.push().getKey();

        // Create a new StudentItem with the generated studentId
        StudentItem newStudent = new StudentItem(roll, name);

        // Add data to Firebase
        studentRef.child(studentId).setValue(newStudent);

        studentItems.add(newStudent);
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case 0:
                // Edit option is selected
                showEditStudentDialog(item.getGroupId());
                break;
            case 1:
                // Delete option is selected
                deleteStudent(item.getGroupId());
                break;
        }
        return super.onContextItemSelected(item);
    }

    private void deleteStudent(int position) {
        // Ensure studentRef is not null
        if (studentRef != null) {
            // Get the StudentItem at the specified position
            StudentItem studentToDelete = studentItems.get(position);

            // Get the key (studentId) from the studentToDelete
            String studentId = studentToDelete.getStudentId();

            if (studentId != null) {
                DatabaseReference studentToDeleteRef = studentRef.child(studentId);
                studentToDeleteRef.removeValue()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Log.d("DeleteStudent", "Data deleted successfully from Firebase");
                            } else {
                                Log.e("DeleteStudent", "Failed to delete data from Firebase: " + task.getException());
                            }
                        });
            } else {
                // Handle the case where studentId is null
                Log.e("DeleteStudent", "studentId is null");
            }

            // Remove the item from the local list
            studentItems.remove(position);
            adapter.notifyItemRemoved(position);
        } else {
            // Handle the case where studentRef is null
            Log.e("DeleteStudent", "studentRef is null");
        }
    }

    private void showEditStudentDialog(int position) {
        MyDialog dialog = new MyDialog(studentItems.get(position).getRoll(), studentItems.get(position).getName());
        dialog.show(getSupportFragmentManager(), MyDialog.STUDENT_UPDATE_DIALOG);
        dialog.setListener((roll, name) -> updateStudent(position, roll, name));
    }

    private void updateStudent(int position, String roll, String name) {
        // Ensure studentRef is not null
        if (studentRef != null) {
            // Update data in the local list
            studentItems.get(position).setRoll(roll);
            studentItems.get(position).setName(name);
            adapter.notifyItemChanged(position);

            // Get the StudentItem at the specified position
            StudentItem updatedStudent = studentItems.get(position);

            // Get the key (studentId) from the updatedStudent
            String studentId = updatedStudent.getStudentId();

            if (studentId != null) {
                DatabaseReference studentToUpdateRef = studentRef.child(studentId);
                studentToUpdateRef.setValue(updatedStudent)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Log.d("UpdateStudent", "Data updated successfully in Firebase");
                            } else {
                                Log.e("UpdateStudent", "Failed to update data in Firebase: " + task.getException());
                            }
                        });
            } else {
                // Handle the case where studentId is null
                Log.e("UpdateStudent", "studentId is null");
            }
        } else {
            // Handle the case where studentRef is null
            Log.e("UpdateStudent", "studentRef is null");
        }
    }
}