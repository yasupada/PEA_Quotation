package com.yasupada.mobile.pea_quotation;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.view.WindowCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.yasupada.mobile.pea_quotation.databinding.ActivityQuotationBinding;

import java.util.ArrayList;

public class QuotationActivity extends AppCompatActivity {
    private ArrayList<CheckBox> checkBoxList;
    private TextView totalTextView;
    private TextView netTotalTextView;
    private TextView operationTextView;
    private Button openPdfButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quotation);

        checkBoxList = new ArrayList<>();
        totalTextView = findViewById(R.id.totalTextView);
        operationTextView = findViewById(R.id.operationTotalTextView);
        netTotalTextView = findViewById(R.id.netTotalTextView);

        // Create checkboxes dynamically
        createCheckBoxes();

        // Set listener to calculate total when checkboxes are checked/unchecked
        setCheckBoxListener();


        openPdfButton = findViewById(R.id.openPdfButton);

        openPdfButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String pdfUrl = "https://example.com/path/to/pdf.pdf";
                openPdfInChrome(pdfUrl);
            }
        });
    }

    private void openPdfInChrome(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse(url), "application/pdf");
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

        // Check if there is an application to handle the intent
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            // No application available to handle PDF files
            // Handle this case as per your app's requirements
        }
    }

    private void createCheckBoxes() {
        LinearLayout checkBoxContainer = findViewById(R.id.checkBoxContainer);

        // Example data for checkbox items
        ArrayList<CartItem> cartItems = new ArrayList<>();
        cartItems.add(new CartItem("450 สายขนาด 50 AW ชนิด AAA", 58));
        cartItems.add(new CartItem("452 สายขนาด 95 AW ชนิด AAA", 108));
        cartItems.add(new CartItem("3 เสาขนาด 8 เมตร", 3464));
        cartItems.add(new CartItem("4 เสาขนาด 9 เมตร", 4239));
        cartItems.add(new CartItem("60 Rack 2 เสา 8 9 ประเภท SP", 558));
        cartItems.add(new CartItem("63 Rack 2 เสา 8 9 ประเภท DE", 1046));
        cartItems.add(new CartItem("66 Rack 2 เสา 8 9 ประเภท DDE", 2031));
        cartItems.add(new CartItem("68 Rack 2 เสา 12 ประเภท SP", 583));

        cartItems.add(new CartItem("71 Rack 2 เสา 12 ประเภท DE", 584));
        cartItems.add(new CartItem("74 Rack 2 เสา 12 ประเภท DDE", 1076));


        // Create checkboxes based on the cart items
        for (CartItem item : cartItems) {
            CheckBox checkBox = new CheckBox(this);
            checkBox.setText(item.getName());

            checkBoxContainer.addView(checkBox);
            checkBoxList.add(checkBox);
        }
    }

    private void setCheckBoxListener() {
        for (final CheckBox checkBox : checkBoxList) {
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                    updateTotal();
                }
            });
        }
    }

    private void updateTotal() {
        double total = 0;

        for (CheckBox checkBox : checkBoxList) {
            if (checkBox.isChecked()) {

                CartItem item = (CartItem) checkBox.getTag();
                // total += item.getPrice() * item.getQuantity();
                }


        }
        double operationAmount = total * 0.30;
        double netTotalPrice = total + operationAmount;

        totalTextView.setText("Total: $" + netTotalPrice);
        operationTextView.setText("Operation Total: $" + netTotalPrice);
        netTotalTextView.setText("Net Total: $" + netTotalPrice);
    }
}