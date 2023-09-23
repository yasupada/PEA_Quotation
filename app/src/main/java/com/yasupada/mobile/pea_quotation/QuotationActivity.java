package com.yasupada.mobile.pea_quotation;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.view.WindowCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.yasupada.mobile.pea_quotation.databinding.ActivityQuotationBinding;

import java.util.ArrayList;
import java.util.List;

public class QuotationActivity extends AppCompatActivity {
    private ArrayList<CheckBox> checkBoxList;
    private ArrayList<CartItem> cartItems;
    private CartItemAdapter cartItemAdapter;
    private TextView totalTextView;
    private TextView netTotalTextView;
    private TextView operationTextView;
    private TextView vatTextView;
    private Button openPdfButton;

    private EditText edtNo,edtName;

    int price_set = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quotation);

        price_set = getIntent().getExtras().getInt("item_set",1);

        checkBoxList = new ArrayList<>();
        edtNo = findViewById(R.id.edtNo);
        edtName = findViewById(R.id.edtName);
        totalTextView = findViewById(R.id.totalTextView);
        operationTextView = findViewById(R.id.operationTotalTextView);
        vatTextView = findViewById(R.id.vatTextView);
        netTotalTextView = findViewById(R.id.netTotalTextView);

        // Create checkboxes dynamically
        //createCheckBoxes();

        // Set listener to calculate total when checkboxes are checked/unchecked
        //setCheckBoxListener();

        ListView listView = findViewById(R.id.listView_cart);
        cartItems = new ArrayList<CartItem>();
        priceSet(price_set);

        cartItemAdapter = new CartItemAdapter(this, cartItems);
        listView.setAdapter(cartItemAdapter);

//                Button summaryButton = findViewById(R.id.button_summary);
//                summaryButton.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        showSummary();
//                    }
//                });

//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                showSummary();
//            }
//        });

        listView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                showSummary();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        openPdfButton = findViewById(R.id.openPdfButton);

        openPdfButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String no = edtNo.getText().toString();
                String name = edtName.getText().toString();
                String total =  totalTextView.getText().toString();
                String oper_total =  operationTextView.getText().toString();
                String vat = vatTextView.getText().toString();
                String net_price = "13,100"; //totalTextView.getText();
                String waranty = "0";

                String pdfUrl = "https://jackk368.com/genpdf.php?docno="+no+"&docfor="+name+"&total="+total+"&oper_total="+oper_total+"&vat="+vat+"&net_price="+net_price+"&waranty=" +waranty;

                openPdfInChrome(pdfUrl);
            }
        });
    }

    private void showSummary() {
        // Iterate over the cartItems list and calculate the summary price based on selected items
        double summaryPrice = 0.0;
        for (CartItem cartItem : cartItems) {
            if (cartItem.isSelected()) {
                summaryPrice += cartItem.getPrice() * cartItem.getQuantity();
            }
        }

        // Show the summary price (e.g., in a toast or a dialog)
        // Replace the following line with your own implementation
        Toast.makeText(this, "Summary Price: " + summaryPrice, Toast.LENGTH_SHORT).show();
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

    private ArrayList<CartItem> priceSet(int i) {

        cartItems = new ArrayList<CartItem>();
        if(i==1) {
            cartItems.add(new CartItem(false,"10 POLE,CONCRETE, 12 M.LONG 5,560.26", 5560.26));
            cartItems.add(new CartItem(false,"11 POLE,CONCRETE, 12.20 M. LONG 7,849.52", 7849.52));
            cartItems.add(new CartItem(false,"12 POLE,CONCRETE, 14 M. LONG 7,462.18", 7462.18));
            cartItems.add(new CartItem(false,"13 POLE,CONCRETE, 14.30 M. LONG 12,826.63", 12826.63));
            cartItems.add(new CartItem(false,"75 เทโคน POLE FOUNDATION, 12 M. - 14.30 M. 3,759.45", 3759.45));
            cartItems.add(new CartItem(false,"102 X-ARM-C, FOR CUT OUT ON BREAK 1,026.67", 1026.67));
            cartItems.add(new CartItem(false,"110 X-ARM-C, 1-P, BA 5,818.13", 5818.13));
            cartItems.add(new CartItem(false,"114 X-ARM-C, 1-P, DDE 5,370.50", 5370.50));
            cartItems.add(new CartItem(false,"115 X-ARM-C, 1-P, DE 3,030.00", 3030.00));
            cartItems.add(new CartItem(false,"116 X-ARM-C, 1-P, DEAD END CONNECTION	2,531.00", 2531.00));
            cartItems.add(new CartItem(false,"117 X-ARM-C, 1-P, DP 5-30 * C 3,653.00", 3653.00));
            cartItems.add(new CartItem(false,"119 X-ARM-C, 1-P, SP 0-5 * C 1,904.00", 1904.00));
            cartItems.add(new CartItem(false,"125 X-ARM-C, 3-P, BA FOR AERIAL CABLE 22 kV	  5,309.00", 5309.00));
            cartItems.add(new CartItem(false,"129 หัวเสา DDE  X-ARM-C, 3-P, DDE 7,322.50", 7322.50));
            cartItems.add(new CartItem(false,"133 X-ARM-C, 3-P, DE FOR AERIAL CABLE 22 kV	  4,150.50", 4150.50));
            cartItems.add(new CartItem(false,"136 X-ARM-C, 3-P, DEAD END CONNECTION	  3,796.50", 3796.50));
            cartItems.add(new CartItem(false,"140 X-ARM-C, 3-P, SP 0-5 2,477.00", 2477.00));
            cartItems.add(new CartItem(false,"217 A-ARM-C, 1-P, DDE	5,498.00", 5498.00));
            cartItems.add(new CartItem(false,"218 A-ARM-C, 1-P, SP 0-5 * C 2,019.50", 2019.50));
            cartItems.add(new CartItem(false,"221 A-ARM-C, 1-P, BA 3,974.50", 3974.50));
            cartItems.add(new CartItem(false,"223 A-ARM-C, 1-P, DE 2,904.50", 2904.50));
            cartItems.add(new CartItem(false,"225 A-ARM-C, 3-P, BA 4,643.50", 4643.50));
            cartItems.add(new CartItem(false,"227 A-ARM-C, 3-P, DE 3,640.50", 3640.50));
            cartItems.add(new CartItem(false,"230 A-ARM-C, 3-P, SP 0-5 2,592.50", 2592.50));
            cartItems.add(new CartItem(false,"233 A-ARM-C, 3-P, DDE 7,432.50", 7432.50));

            cartItems.add(new CartItem(false,"274 OVERHEAD GROUND WIRE SP. 0-5 810.50", 810.50));
            cartItems.add(new CartItem(false,"275 OVERHEAD GROUND WIRE DP. 5-30 923.50", 923.50));
            cartItems.add(new CartItem(false,"282 OVERHEAD GROUND WIRE DDE. II (12,14,16 M.) 1,437.50", 1437.50));
            cartItems.add(new CartItem(false,"284 OVERHEAD GROUND WIRE DE. II (12,14,16 M.) 1,237.00", 1237.00));
            cartItems.add(new CartItem(false,"294 LIGHTNING ARRESTER 20-21 kV. 5 kA. 741.00", 741.00));
            cartItems.add(new CartItem(false,"313 CONDUCTOR,ACSR 50|8 sq.mm.TIS.86 25.00", 25.00));
            cartItems.add(new CartItem(false,"332 สาย 50 SAC  CABLE,AERIAL,AL 22 kV. 1x50 sq.mm. 84.00", 84.00));
            cartItems.add(new CartItem(false,"360 AERIAL CABLE ON STEEL BRACKET,CORNER 22 KV. 5-60 ccb เหล็กปอปลา 3,082.50", 3082.50));
            cartItems.add(new CartItem(false,"496 INSULATOR,LINE-POST TYPE, 22 kV.CLASS 57-2L POWER ARC TEST 567.50", 567.50));
            cartItems.add(new CartItem(false,"499 INSULATOR,SUSPENSION TYPE A (CLASS 52-1)TIS.354 ลูกยืดชุดพวง3ลูก 288.00", 288.00));
            cartItems.add(new CartItem(false,"519 FUSE LINK 22 kV. 10 A.EEI-NEMA TYPE K	27.00", 27.00));
            cartItems.add(new CartItem(false,"520 FUSE LINK 22 kV. 15 A.EEI-NEMA TYPE K	27.50", 27.50));
            cartItems.add(new CartItem(false,"521 FUSE LINK 22 kV. 20 A.EEI-NEMA TYPE K	30.50", 30.50));
            cartItems.add(new CartItem(false,"522 FUSE LINK 22 kV. 25 A.EEI-NEMA TYPE K	31.50", 31.50));
            cartItems.add(new CartItem(false,"565 HT. GROUNDING CONNECTION 1,571.00", 1571.00));
            cartItems.add(new CartItem(false,"582 GY-22,DE SIZE 95 SQ.MM. 2,588.50", 2588.50));
            cartItems.add(new CartItem(false,"583 GY-23 SIZE 50 SQ.MM. 3,951.50", 3951.50));

            cartItems.add(new CartItem(false,"757 WIRE,STEEL STRANDED 25 sq.mm.TIS.404 86.50", 86.50));
            cartItems.add(new CartItem(false,"843 OHGW DEADEND GUY  WITH DEADEND POLE, GY-28A WITH LV SYSTEM 3,458.50", 3458.50));
            cartItems.add(new CartItem(false,"1020330104 HOTLINE CLAMP,MAIN35-185,TAP50-185SQ.MM. 297.00", 297.00));
            cartItems.add(new CartItem(false,"1020330005 HOTLINE BAIL-CLAMP,MAIN 35-70 SQ.MM. 308.50", 308.50));
            cartItems.add(new CartItem(false,"1020330006 HOTLINE BAIL-CLAMP,MAIN 70-185 SQ.MM. 358.00", 358.00));
            cartItems.add(new CartItem(false,"1040010002 CUT-OUT,FUSE,OPEN TYPE,SINGLE INSULATOR DROPUOT 22 KV. 100 A. 12 KA. ASYM 1,458.50", 1458.50));
            cartItems.add(new CartItem(false,"10 POLE,CONCRETE, 12 M.LONG 11,120.51", 11120.51));
            cartItems.add(new CartItem(false,"11 POLE,CONCRETE, 12.20 M. LONG 15,699.04", 15699.04));
            cartItems.add(new CartItem(false,"12 POLE,CONCRETE, 14 M. LONG 14,924.36", 14924.36));
            cartItems.add(new CartItem(false,"13 POLE,CONCRETE, 14.30 M. LONG 25,653.25", 25653.25));
            cartItems.add(new CartItem(false,"75 เทโคน POLE FOUNDATION, 12 M. - 14.30 M. 7,518.89", 7518.89));


            cartItems.add(new CartItem(false,"102 X-ARM-C, FOR CUT OUT ON BREAK 2,053.33", 58));
            cartItems.add(new CartItem(false,"110 X-ARM-C, 1-P, BA 11,636.25", 58));
            cartItems.add(new CartItem(false,"114 X-ARM-C, 1-P, DDE 10,741.00", 58));
            cartItems.add(new CartItem(false,"115 X-ARM-C, 1-P, DE 6,060.00", 58));
            cartItems.add(new CartItem(false,"116 X-ARM-C, 1-P, DEAD END CONNECTION 5,062.00", 58));
            cartItems.add(new CartItem(false,"117 X-ARM-C, 1-P, DP 5-30 * C 7,306.00", 58));
            cartItems.add(new CartItem(false,"119 X-ARM-C, 1-P, SP 0-5 * C 3,808.00", 58));
            cartItems.add(new CartItem(false,"125 X-ARM-C, 3-P, BA FOR AERIAL CABLE 22 kV 10,618.00", 58));
            cartItems.add(new CartItem(false,"129 หัวเสา DDE  X-ARM-C, 3-P, DDE 14,645.00", 58));
            cartItems.add(new CartItem(false,"133 X-ARM-C, 3-P, DE FOR AERIAL CABLE 22 kV 8,301.00", 58));
            cartItems.add(new CartItem(false,"136 X-ARM-C, 3-P, DEAD END CONNECTION 7,593.00", 58));
            cartItems.add(new CartItem(false,"140 X-ARM-C, 3-P, SP 0-5 4,954.00", 58));
            cartItems.add(new CartItem(false,"217 A-ARM-C, 1-P, DDE 10,996.00", 58));
            cartItems.add(new CartItem(false,"218 A-ARM-C, 1-P, SP 0-5 * C 4,039.00", 58));
            cartItems.add(new CartItem(false,"221 A-ARM-C, 1-P, BA 7,949.00", 58));
            cartItems.add(new CartItem(false,"223 A-ARM-C, 1-P, DE 5,809.00", 58));
            cartItems.add(new CartItem(false,"225 A-ARM-C, 3-P, BA 9,287.00", 58));
            cartItems.add(new CartItem(false,"227 A-ARM-C, 3-P, DE 7,281.00", 58));
            cartItems.add(new CartItem(false,"230 A-ARM-C, 3-P, SP 0-5 5,185.00", 58));
            cartItems.add(new CartItem(false,"233 A-ARM-C, 3-P, DDE 14,865.00", 58));
            cartItems.add(new CartItem(false,"274 OVERHEAD GROUND WIRE SP. 0-5 1,621.00", 58));
            cartItems.add(new CartItem(false,"275 OVERHEAD GROUND WIRE DP. 5-30 1,847.00", 58));
            cartItems.add(new CartItem(false,"282 OVERHEAD GROUND WIRE DDE. II (12,14,16 M.) 2,875.00", 58));
            cartItems.add(new CartItem(false,"284 OVERHEAD GROUND WIRE DE. II (12,14,16 M.) 2,474.00", 58));
            cartItems.add(new CartItem(false,"294 LIGHTNING ARRESTER 20-21 kV. 5 kA. 1,482.00", 58));
            cartItems.add(new CartItem(false,"313 CONDUCTOR,ACSR 50|8 sq.mm.TIS.86 50.00", 58));
            cartItems.add(new CartItem(false,"332 สาย 50 SAC  CABLE,AERIAL,AL 22 kV. 1x50 sq.mm. 168.00", 58));
            cartItems.add(new CartItem(false,"360 AERIAL CABLE ON STEEL BRACKET,CORNER 22 KV. 5-60 ccb เหล็กปอปลา 6,165.00", 58));
            cartItems.add(new CartItem(false,"496 INSULATOR,LINE-POST TYPE, 22 kV.CLASS 57-2L POWER ARC TEST 1,135.00", 58));
            cartItems.add(new CartItem(false,"499 INSULATOR,SUSPENSION TYPE A (CLASS 52-1)TIS.354 ลูกยืดชุดพวง3ลูก 576.00", 58));
            cartItems.add(new CartItem(false,"519 FUSE LINK 22 kV. 10 A.EEI-NEMA TYPE K 54.00", 58));
            cartItems.add(new CartItem(false,"520 FUSE LINK 22 kV. 15 A.EEI-NEMA TYPE K 55.00", 58));
            cartItems.add(new CartItem(false,"521 FUSE LINK 22 kV. 20 A.EEI-NEMA TYPE K 61.00", 58));
            cartItems.add(new CartItem(false,"522 FUSE LINK 22 kV. 25 A.EEI-NEMA TYPE K 63.00", 58));
            cartItems.add(new CartItem(false,"565 HT. GROUNDING CONNECTION 3,142.00", 58));
            cartItems.add(new CartItem(false,"582 GY-22,DE SIZE 95 SQ.MM. 5,177.00", 58));
            cartItems.add(new CartItem(false,"583 GY-23 SIZE 50 SQ.MM. 7,903.00", 58));
            cartItems.add(new CartItem(false,"757 WIRE,STEEL STRANDED 25 sq.mm.TIS.404 173.00", 58));
            cartItems.add(new CartItem(false,"843 OHGW DEADEND GUY  WITH DEADEND POLE, GY-28A WITH LV SYSTEM 6,917.00", 58));
            cartItems.add(new CartItem(false,"1020330104 HOTLINE CLAMP,MAIN35-185,TAP50-185SQ.MM. 594.00", 58));
            cartItems.add(new CartItem(false,"1020330005 HOTLINE BAIL-CLAMP,MAIN 35-70 SQ.MM. 617.00", 58));
            cartItems.add(new CartItem(false,"1020330006 HOTLINE BAIL-CLAMP,MAIN 70-185 SQ.MM. 716.00", 58));
            cartItems.add(new CartItem(false,"1040010002 CUT-OUT,FUSE,OPEN TYPE,SINGLE INSULATOR DROPUOT 22 KV. 100 A. 12 KA. ASYM 2,917.00", 58));


            cartItems.add(new CartItem(false,"1050000011 หม้อแปลงขนาด 30 เควีเอ 119,531.00", 119531.00));
            cartItems.add(new CartItem(false,"1050010066 หม้อแปลงขนาด 50 เควีเอ 154,476.00", 154476.00));
            cartItems.add(new CartItem(false,"1050010067 หม้อแปลงขนาด 100 เควีเอ 264,314.00", 264314.00));
            cartItems.add(new CartItem(false,"1050010068 หม้อแปลงขนาด 160 เควีเอ 258,052.00", 258052.00));
            cartItems.add(new CartItem(false,"1050010069 หม้อแปลงขนาด 250 เควีเอ 399,495.00", 399495.00));
            cartItems.add(new CartItem(false,"1050010070 หม้อแปลงขนาด 315 เควีเอ 708,356.00", 708356.00));
            cartItems.add(new CartItem(false,"1050010071 หม้อแปลงขนาด 400 เควีเอ 954,763.00", 954763.00));
            cartItems.add(new CartItem(false,"1050010072 หม้อแปลงขนาด 500 เควีเอ 1,011,854.00", 1011854.00));
            cartItems.add(new CartItem(false,"1050010073 หม้อแปลงขนาด 630 เควีเอ 1,186,957.00", 1186957.00));
            cartItems.add(new CartItem(false,"1050010074 หม้อแปลงขนาด 800 เควีเอ 1,321,534.00", 1321534.00));
            cartItems.add(new CartItem(false,"1050010075 หม้อแปลงขนาด 1000 เควีเอ 1,605,622.00", 1605622.00));
            cartItems.add(new CartItem(false,"1050010076 หม้อแปลงขนาด 1250 เควีเอ	1,884,059.00", 1884059.00));
            cartItems.add(new CartItem(false,"1050010077 หม้อแปลงขนาด 1500 เควีเอ 2,286,214.00", 2286214.00));
            cartItems.add(new CartItem(false,"62 Single Pole X-Arm 1 เฟส 20,371.00", 20371.00));
            cartItems.add(new CartItem(false,"76 Single Pole X-Arm 3 เฟส 27,331.00", 27331.00));
            cartItems.add(new CartItem(false,"82 Platform 54,522.00", 54522.00));
            cartItems.add(new CartItem(false,"84 Platform ใหญ่ 68,154.00", 68154.00));
            cartItems.add(new CartItem(false,"85 Platform ขวางไลน์ 75,220.00", 75220.00));
            cartItems.add(new CartItem(false,"130 HT Ground 3,830.00", 3830.00));
            cartItems.add(new CartItem(false,"134 LT Switch 5,378.00", 5378.00));
            cartItems.add(new CartItem(false,"135 คอน LT 2 ฝั่ง 6 ตัว 8,216.44", 8216.44));
            cartItems.add(new CartItem(false,"207 LT FUSE 740.00", 740.00));
            cartItems.add(new CartItem(false,"211 LT LIGHTINING 5,003.32", 5003.32));
            cartItems.add(new CartItem(false,"236 LT Wiring 50 1 เฟส 2 สาย 743.65", 743.65));
            cartItems.add(new CartItem(false,"251 สายต่อบุชชิ่ง LT ขนาด 50 8,791.12", 8791.12));
            cartItems.add(new CartItem(false,"253 สายต่อบุชชิ่ง LT ขนาด 95 8,791.12", 8791.12));
            cartItems.add(new CartItem(false,"271 LT Wiring 50 3 เฟส 4 สาย 1,988.00", 1988.00));
            cartItems.add(new CartItem(false,"273 LT Wiring 95 3 เฟส 4 สาย 3,814.00", 3814.00));
            cartItems.add(new CartItem(false,"360 LT Ground connection 3,584.00", 3584.00));
            cartItems.add(new CartItem(false,"361 ROD Ground 1,588.00", 1588.00));
            cartItems.add(new CartItem(false,"1010100002 Wire steel 25.68", 25.68));
            cartItems.add(new CartItem(false,"1010230000 Clamp U Bolt M8 12.84", 12.84));
            cartItems.add(new CartItem(false,"1030010200 Insulator 4,031.57", 4031.57));
            cartItems.add(new CartItem(false,"1040010015 Animal Barrier 793.94", 793.94));
            cartItems.add(new CartItem(false,"1040010016 Cutout Bracket 1,046.46", 1046.46));
            cartItems.add(new CartItem(false,"1090250038 หม้อแลงโคลปเวอร์	598.13", 598.13));
            cartItems.add(new CartItem(false,"1090250039 ซีทีโคลปเวอร์ 598.13", 598.13));
            cartItems.add(new CartItem(false,"1090250040 ดรอปโคลปเวอร์ 1,373.88", 1373.88));
            cartItems.add(new CartItem(false,"1090250041 ล่อฟ้อโคลปเวอร์ 395.90", 395.90));
            cartItems.add(new CartItem(false,"ค่าสมทบหม้อแปลงขนาด 30 เควีเอ 3,000.00", 3000.00));
            cartItems.add(new CartItem(false,"ค่าสมทบหม้อแปลงขนาด 50 เควีเอ 5,000.00", 5000.00));

            cartItems.add(new CartItem(false,"ค่าสมทบหม้อแปลงขนาด 100 เควีเอ 10,000.00", 10000.00));
            cartItems.add(new CartItem(false,"ค่าสมทบหม้อแปลงขนาด 160 เควีเอ 16,000.00", 16000.00));
            cartItems.add(new CartItem(false,"ค่าสมทบหม้อแปลงขนาด 250 เควีเอ 25,000.00", 25000.00));
            cartItems.add(new CartItem(false,"ค่าสมทบหม้อแปลงขนาด 315 เควีเอ 31,500.00", 31500.00));
            cartItems.add(new CartItem(false,"ค่าสมทบหม้อแปลงขนาด 400 เควีเอ 40,000.00", 40000.00));
            cartItems.add(new CartItem(false,"ค่าสมทบหม้อแปลงขนาด 500 เควีเอ 50,000.00", 50000.00));
            cartItems.add(new CartItem(false,"ค่าสมทบหม้อแปลงขนาด 630 เควีเอ 63,000.00", 63000.00));
            cartItems.add(new CartItem(false,"ค่าสมทบหม้อแปลงขนาด 800 เควีเอ 80,000.00", 80000.00));
            cartItems.add(new CartItem(false,"ค่าสมทบหม้อแปลงขนาด 1000 เควีเอ 100,000.00", 100000.00));
            cartItems.add(new CartItem(false,"ค่าสมทบหม้อแปลงขนาด 1250 เควีเอ 125,000.00", 125000.00));
            cartItems.add(new CartItem(false,"ค่าสมทบหม้อแปลงขนาด 1500 เควีเอ 150,000.00", 150000.00));
            cartItems.add(new CartItem(false,"ค่าปลดสับอุปกรณ์แรงสูง 1,500.00", 1500.00));
            cartItems.add(new CartItem(false,"ค่าปลดเชื่อมอุปกรณ์แรงสูง 10,000.00", 10000.00));
            cartItems.add(new CartItem(false,"ค่าปฏิบัติการฮอตไลน์ 25,014.00", 25014.00));
        }else if(i==2) {
//            cartItems.add(new CartItem(false,"450 สายขนาด 50 AW ชนิด AAA", 58));
//            cartItems.add(new CartItem(false,"452 สายขนาด 95 AW ชนิด AAA", 108));
//            cartItems.add(new CartItem(false,"3 เสาขนาด 8 เมตร", 3464));
        }else if(i==3) {
            cartItems.add(new CartItem(false,"450 สายขนาด 50 AW ชนิด AAA 58.00", 58));
            cartItems.add(new CartItem(false,"452 สายขนาด 95 AW ชนิด AAA 108.00", 108));
            cartItems.add(new CartItem(false,"3 เสาขนาด 8 เมตร 3,464.00", 3464));
            cartItems.add(new CartItem(false,"4 เสาขนาด 9 เมตร 4,239.00", 4239));
            cartItems.add(new CartItem(false,"60 Rack 2 เสา 8 9 ประเภท SP 558.00", 558));
            cartItems.add(new CartItem(false,"63 Rack 2 เสา 8 9 ประเภท DE 1,046.00", 1046));
            cartItems.add(new CartItem(false,"66 Rack 2 เสา 8 9 ประเภท DDE 2,031.00", 2031));
            cartItems.add(new CartItem(false,"68 Rack 2 เสา 12 ประเภท SP 583.00", 583));
            cartItems.add(new CartItem(false,"71 Rack 2 เสา 12 ประเภท DE 584.00", 584));
            cartItems.add(new CartItem(false,"74 Rack 2 เสา 12 ประเภท DDE 1,076.00", 1076));
            cartItems.add(new CartItem(false,"175 Rack 4 เสา 8 9 ประเภท SP 888.00",888));
            cartItems.add(new CartItem(false,"178 Rack 4 เสา 8 9 ประเภท DE 1,950",1950));
            cartItems.add(new CartItem(false,"181 Rack 4 เสา 8 9 ประเภท DDE 3,784",3784));
            cartItems.add(new CartItem(false,"183 Rack 4 เสา 12 ประเภท SP 913.00",913));
            cartItems.add(new CartItem(false,"186 Rack 4 เสา 12 ประเภท DE 1,998.00",1998));
            cartItems.add(new CartItem(false,"189 Rack 4 เสา 12 ประเภท DDE 3,820.00",3820));
            cartItems.add(new CartItem(false,"369 GUY DE 3,645",3645));
            cartItems.add(new CartItem(false,"370 GUY DDE 3,690",3690));
            cartItems.add(new CartItem(false,"450 สายขนาด 50 AW ชนิด/m 58",58));
            cartItems.add(new CartItem(false,"452 สายขนาด 95 AW ชนิด/m 95",95));
            cartItems.add(new CartItem(false,"555 LT Lightning Arrester For 2W 3,362",3362));
            cartItems.add(new CartItem(false,"557 LT Lightning Arrester For 4W 4,096",4096));
            cartItems.add(new CartItem(false,"1020260301 PREFORMED D/E AW50 260",260));
            cartItems.add(new CartItem(false,"1020260302 PREFORMED D/E AW95 314",314));
        }

        return cartItems;
    }

//    private void createCheckBoxes() {
//        LinearLayout checkBoxContainer = findViewById(R.id.checkBoxContainer);
//
//        // Example data for checkbox items
//        List<CartItem> cartItems = priceSet(price_set);
//        // Create checkboxes based on the cart items
//        for (CartItem item : cartItems) {
//            CheckBox checkBox = new CheckBox(this);
//            checkBox.setText(item.getName());
//
//            checkBoxContainer.addView(checkBox);
//            checkBoxList.add(checkBox);
//        }
//    }

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
                 total += item.getPrice() * item.getQuantity();
                }
        }
        double operationAmount = total * 0.30;
        double netTotalPrice = total + operationAmount;

        totalTextView.setText("Total: $" + netTotalPrice);
        vatTextView.setText("Vat: $" + 0);
        operationTextView.setText("Operation Total: $" + netTotalPrice);
        netTotalTextView.setText("Net Total: $" + netTotalPrice);
    }
}