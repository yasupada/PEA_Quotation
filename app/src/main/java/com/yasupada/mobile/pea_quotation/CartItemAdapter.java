package com.yasupada.mobile.pea_quotation;


import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import java.util.List;

public class CartItemAdapter extends ArrayAdapter<CartItem> {
    private List<CartItem> cartItems;

    public CartItemAdapter(Context context, List<CartItem> cartItems) {
        super(context, 0, cartItems);
        this.cartItems = cartItems;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.cart_item, parent, false);
        }

        CartItem cartItem = cartItems.get(position);

        TextView nameTextView = convertView.findViewById(R.id.txvName);
        TextView priceTextView = convertView.findViewById(R.id.txvPrice);
        EditText quantityEditText = convertView.findViewById(R.id.edtQty);
        CheckBox checkBox = convertView.findViewById(R.id.chkItem);

        nameTextView.setText(cartItem.getName());
        priceTextView.setText(String.valueOf(cartItem.getPrice()));
        quantityEditText.setText(String.valueOf(cartItem.getQuantity()));
        checkBox.setChecked(cartItem.isSelected());

        quantityEditText.addTextChangedListener(new QuantityTextWatcher(cartItem));

        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            cartItem.setSelected(isChecked);
        });

        return convertView;
    }

    private class QuantityTextWatcher implements TextWatcher {
        private CartItem cartItem;

        public QuantityTextWatcher(CartItem cartItem) {
            this.cartItem = cartItem;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable editable) {
            String quantityString = editable.toString();
            int quantity = quantityString.isEmpty() ? 0 : Integer.parseInt(quantityString);
            cartItem.setQuantity(quantity);
        }


    }
}