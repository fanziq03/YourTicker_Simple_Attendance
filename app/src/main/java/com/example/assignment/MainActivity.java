package com.example.assignment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.assignment.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    FloatingActionButton fab;
    RecyclerView recyclerView;
    ClassAdapter classAdapter;
    RecyclerView.LayoutManager layoutManager;
    ArrayList<ClassItem> classItems = new ArrayList<>();
    Toolbar toolbar;
    DatabaseReference classRef;
    private TextView welcomeText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.home);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.home) {
                return true;
            } else if (item.getItemId() == R.id.info) {
                startActivity(new Intent(getApplicationContext(), InfoActivity.class));
                finish();
                return true;
            }
            return false;
        });


        classItems = new ArrayList<>();
        FirebaseApp.initializeApp(this);
        classRef = FirebaseDatabase.getInstance().getReference().child("Classes");
        classRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                classItems.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    if (dataSnapshot != null) {
                        ClassItem retrievedClass = dataSnapshot.getValue(ClassItem.class);
                        if (retrievedClass != null) {
                            classItems.add(retrievedClass);
                        }
                    }
                }
                classAdapter.notifyDataSetChanged();

                // Update visibility class
                updateWelcomeTextVisibility();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle errors
            }
        });

        fab = findViewById(R.id.fab_main);
        fab.setOnClickListener(v -> showDialog());

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        classAdapter = new ClassAdapter(this, classItems);
        recyclerView.setAdapter(classAdapter);
        classAdapter.setOnItemClickListener(position -> gotoItemActivity(position));

        setToolbar();

        welcomeText = findViewById(R.id.welcomeText);
        classAdapter.notifyDataSetChanged();
        updateWelcomeTextVisibility();
    }

    private void updateWelcomeTextVisibility() {
        if (classItems.isEmpty()) {
            welcomeText.setVisibility(View.VISIBLE);
        } else {
            welcomeText.setVisibility(View.GONE);
        }
    }

    private void setToolbar() {
        toolbar = findViewById(R.id.toolbar);
        TextView title = toolbar.findViewById(R.id.title_toolbar);
        TextView subtitle = toolbar.findViewById(R.id.subtitle_toolbar);
        ImageButton back = toolbar.findViewById(R.id.back);

        title.setText("YourTicker");
        subtitle.setVisibility(View.GONE);
        back.setVisibility(View.INVISIBLE);
    }

    private void gotoItemActivity(int position) {
        Intent intent = new Intent(this, StudentActivity.class);
        intent.putExtra("classId", classItems.get(position).getClassId());
        intent.putExtra("className", classItems.get(position).getClassName());
        intent.putExtra("subjectName", classItems.get(position).getSubjectName());
        intent.putExtra("classDate", classItems.get(position).getClassDate());
        intent.putExtra("position", position);
        startActivity(intent);
    }

    private void showDialog(){
        MyDialog dialog = new MyDialog();
        dialog.show(getSupportFragmentManager(), MyDialog.CLASS_ADD_DIALOG);
        dialog.setListener((className, subjectName) -> addClass(className, subjectName));
    }

    private void showUpdateDialog(int position) {
        MyDialog dialog = new MyDialog(classItems.get(position).getClassName(), classItems.get(position).getSubjectName());
        dialog.show(getSupportFragmentManager(), MyDialog.CLASS_UPDATE_DIALOG);
        dialog.setListener((className, subjectName) -> updateClass(position, className, subjectName));
    }

    private void addClass(String className, String subjectName) {
        // Check if className or subjectName is empty or null
        if (TextUtils.isEmpty(className) || TextUtils.isEmpty(subjectName)) {
            showMessage("Class name and subject cannot be empty!");
            return;
        }

        // Generate a unique classId using Firebase push key
        String classId = classRef.push().getKey();

        // Create a new ClassItem with the generated classId and the current date
        String classDate = getCurrentDate(); // You need to implement this method
        ClassItem newClass = new ClassItem(classId, className, subjectName, classDate);

        // Add data to Firebase
        classRef.child(classId).setValue(newClass);

        classItems.add(newClass);
        classAdapter.notifyDataSetChanged();

        // Update visibility class
        updateWelcomeTextVisibility();
        showMessage("Class added!");

        // Dismiss the dialog
        FragmentManager fragmentManager = getSupportFragmentManager();
        MyDialog myDialog = (MyDialog) fragmentManager.findFragmentByTag(MyDialog.CLASS_ADD_DIALOG);
        if (myDialog != null) {
            myDialog.dismiss();
        }
    }


    private void showMessage(String message) {
        // Show a Toast message
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private String getCurrentDate() {
        // Implement this method to get the current date in the desired format
        // For example, you can use SimpleDateFormat
        // return formattedDate;
        String formattedDate = "YYYY-MM-DD"; // Placeholder, replace with the actual implementation
        return formattedDate;
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()) {
            case 0:
                showUpdateDialog(item.getGroupId());
                break;
            case 1:
                deleteClass(item.getGroupId());
                break;
        }
        return super.onContextItemSelected(item);
    }

    private void updateClass(int position, String className, String subjectName) {
        // Ensure classRef is not null
        if (classRef != null) {
            // Update data in the local list
            classItems.get(position).setClassName(className);
            classItems.get(position).setSubjectName(subjectName);
            classAdapter.notifyItemChanged(position);

            // Get the ClassItem at the specified position
            ClassItem updatedClass = classItems.get(position);

            // Update only className and subjectName in Firebase
            String classId = updatedClass.getClassId();
            if (classId != null) {
                DatabaseReference classToUpdateRef = classRef.child(classId);
                classToUpdateRef.child("className").setValue(className);
                classToUpdateRef.child("subjectName").setValue(subjectName)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Log.d("UpdateClass", "Data updated successfully in Firebase");
                            } else {
                                Log.e("UpdateClass", "Failed to update data in Firebase: " + task.getException());
                            }
                        });
            } else {
                // Handle the case where classId is null
                Log.e("UpdateClass", "classId is null");
            }
        } else {
            // Handle the case where classRef is null
            Log.e("UpdateClass", "classRef is null");
        }

        // Dismiss the dialog
        FragmentManager fragmentManager = getSupportFragmentManager();
        MyDialog myDialog = (MyDialog) fragmentManager.findFragmentByTag(MyDialog.CLASS_UPDATE_DIALOG);
        if (myDialog != null) {
            myDialog.dismiss();
        }

        showMessage("Class updated!");
    }
    private void deleteClass(int position) {
        // Check if the list is empty before attempting to remove an element
        if (!classItems.isEmpty() && position >= 0 && position < classItems.size()) {
            // Get the ClassItem at the specified position
            ClassItem deletedClass = classItems.get(position);

            // Remove data from Firebase
            String classId = deletedClass.getClassId();
            if (classId != null) {
                DatabaseReference classToDeleteRef = classRef.child(classId);
                classToDeleteRef.removeValue()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Log.d("DeleteClass", "Data deleted successfully from Firebase");

                                // Refresh the entire list from Firebase after deletion
                                classRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        classItems.clear();

                                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                            if (dataSnapshot != null) {
                                                ClassItem retrievedClass = dataSnapshot.getValue(ClassItem.class);
                                                if (retrievedClass != null) {
                                                    classItems.add(retrievedClass);
                                                }
                                            }
                                        }
                                        classAdapter.notifyDataSetChanged();

                                        //Update visibility class
                                        updateWelcomeTextVisibility();
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        // Handle errors
                                    }
                                });
                            } else {
                                Log.e("DeleteClass", "Failed to delete data from Firebase: " + task.getException());
                            }
                        });
            } else {
                // Handle the case where classId is null
                Log.e("DeleteClass", "classId is null");
            }
        } else {
            // Handle the case where the list is empty or the position is out of bounds
            Log.e("DeleteClass", "List is empty or position is out of bounds");
        }

        showMessage("Class deleted!");
    }
}