package com.example.assignment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.widget.TextView;

public class InfoActivity extends AppCompatActivity {
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.info);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.home) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
                return true;
            } else if (item.getItemId() == R.id.info) {
                return true;
            }
            return false;
        });

        ImageSlider imageSlider = findViewById(R.id.imagesSlider);
        ArrayList<SlideModel> slideModels = new ArrayList<>();

        slideModels.add(new SlideModel(R.drawable.logo_uitm, ScaleTypes.CENTER_INSIDE));
        slideModels.add(new SlideModel(R.drawable.image1, ScaleTypes.FIT));
        slideModels.add(new SlideModel(R.drawable.image2, ScaleTypes.FIT));
        slideModels.add(new SlideModel(R.drawable.image3, ScaleTypes.FIT));
        slideModels.add(new SlideModel(R.drawable.image4, ScaleTypes.FIT));

        imageSlider.setImageList(slideModels, ScaleTypes.FIT);

        TextView articleTextView = findViewById(R.id.article);
        // Get the string resource and convert HTML-like tags
        String htmlText = getString(R.string.paragraph);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            articleTextView.setText(Html.fromHtml(htmlText, Html.FROM_HTML_MODE_COMPACT));
        } else {
            articleTextView.setText(Html.fromHtml(htmlText));
        }

        setToolbar();
    }

    private void setToolbar() {
        toolbar = findViewById(R.id.toolbar);
        TextView title = toolbar.findViewById(R.id.title_toolbar);
        TextView subtitle = toolbar.findViewById(R.id.subtitle_toolbar);
        ImageButton back = toolbar.findViewById(R.id.back);

        title.setText("App Information");
        subtitle.setVisibility(View.GONE);
        back.setVisibility(View.INVISIBLE);
    }
}