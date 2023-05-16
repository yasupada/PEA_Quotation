package com.yasupada.mobile.pea_quotation;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.core.view.WindowCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.yasupada.mobile.pea_quotation.databinding.ActivityMenuBinding;

public class MenuActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMenuBinding binding;
    private ImageView menu_1,menu_2,menu_3,menu_4,menu_5;


    private static final String IMAGE_URL = "https://jackk368.com/solar_info.jpg";
    private static final int REQUEST_PERMISSION = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMenuBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        menu_1 = findViewById(R.id.menu_1);
        menu_2 = findViewById(R.id.menu_2);
        menu_3 = findViewById(R.id.menu_3);
        menu_4 = findViewById(R.id.menu_4);
        menu_5 = findViewById(R.id.menu_5);

        menu_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(MenuActivity.this, "Item 3 clicked", Toast.LENGTH_SHORT).show();
                // Perform desired action for item 3

            }
        });

        menu_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        menu_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent intent = new Intent(MenuActivity.this,QuotationActivity.class);
                startActivity(intent);
            }
        });

        menu_4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MenuActivity.this, "Item 4 clicked", Toast.LENGTH_SHORT).show();
                // Perform desired action for item 4
            }
        });

        menu_5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImageInChrome(IMAGE_URL);
            }
        });

    }

    private void openImageInChrome(String imageUrl) {
        Uri imageUri = Uri.parse(imageUrl);
        Intent intent = new Intent(Intent.ACTION_VIEW, imageUri);
        intent.setPackage("com.android.chrome"); // Specify the package name for Chrome

        // Verify if Chrome is installed on the device
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            // Chrome is not installed, handle it accordingly
            // For example, show an error message or open in a different browser
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
      return true;
    }
}