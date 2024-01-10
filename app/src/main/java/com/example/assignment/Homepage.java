//package com.example.assignment;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.Button;
//import android.widget.ImageButton;
//import android.widget.TextView;
//import android.widget.Toolbar;
//
//public class Homepage extends AppCompatActivity {
//    Toolbar toolbar;
//    private Button button;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_homepage);
//
//        button = (Button) findViewById(R.id.Attendance);
//        button.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View v){
//                openAttendance();
//            }
//        });
//    }
//
//    public void openAttendance(){
//        Intent intent = new Intent(this, MainActivity.class);
//        startActivity(intent);
//    }
//
//    private void setToolbar() {
//        toolbar = findViewById(R.id.toolbar);
//        TextView title = toolbar.findViewById(R.id.title_toolbar);
//        TextView subtitle = toolbar.findViewById(R.id.subtitle_toolbar);
//        ImageButton back = toolbar.findViewById(R.id.back);
//
//        title.setText("Attendance App");
//        subtitle.setVisibility(View.GONE);
//        back.setVisibility(View.INVISIBLE);
//    }
//}