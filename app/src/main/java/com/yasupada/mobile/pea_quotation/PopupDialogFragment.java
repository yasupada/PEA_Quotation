package com.yasupada.mobile.pea_quotation;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class PopupDialogFragment extends DialogFragment {

    private static final String[] ITEMS = {"หม้อแปลงเช่า 30",
            "หม้อแปลงเช่า 100",
            "หม้อแปลงเช่า 160",
            "หม้อแปลงเช่า 250",
            "หม้อแปลงเช่า 315",
            "หม้อแปลงเช่า 400",
            "หม้อแปลงเช่า 500",
            "หม้อแปลงเช่า 630",
            "หม้อแปลงเช่า 800",
            "หม้อแปลงเช่า 1000",
            "หม้อแปลงเช่า 1250",
            "หม้อแปลงเช่า 1500"};
    private static final String[] URLS = {"https://jackk368.com/adapter_30.pdf",
            "https://jackk368.com/adapter_100.pdf",
            "https://jackk368.com/adapter_160.pdf",
            "https://jackk368.com/adapter_250.pdf",
            "https://jackk368.com/adapter_315.pdf",
            "https://jackk368.com/adapter_400.pdf",
            "https://jackk368.com/adapter_500.pdf",
            "https://jackk368.com/adapter_630.pdf",
            "https://jackk368.com/adapter_800.pdf",
            "https://jackk368.com/adapter_1000.pdf",
            "https://jackk368.com/adapter_1250.pdf",
            "https://jackk368.com/adapter_1500.pdf"

            };

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Select an item")
                .setAdapter(new ArrayAdapter<>(requireActivity(), android.R.layout.simple_list_item_1, ITEMS), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String url = URLS[which];
                        // Open the URL here using an appropriate method
                        // For example, you can open it in a WebView, Chrome Custom Tabs, or the device's default browser
                        // Replace `OpenUrlActivity` with your desired activity or method
                        openPdfInChrome(url);
                    }
                });

        return builder.create();
    }

    private void openPdfInChrome(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse(url), "application/pdf");
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

        // Check if there is an application to handle the intent
        startActivity(intent);

    }
}
