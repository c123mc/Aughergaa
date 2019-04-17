package com.conor.aughergaa.Fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.braintreepayments.api.dropin.BraintreePaymentActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.conor.aughergaa.Classes.MySingleTon;
import com.conor.aughergaa.R;
import com.conor.aughergaa.SilentEmail.SendMail;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class Checkout extends Fragment {

    Button payment_start;
    ImageView header_image;
    Spinner title_text,sizes,sizes_1,sizes_2,sizes_3;
    LinearLayout extras;
    ArrayList<String> title_array = new ArrayList<>();
    ArrayList<String> sizes_array = new ArrayList<>();
    ArrayList<String> lotto_extras_array = new ArrayList<>();
    TextView price;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_checkout, container, false);

        header_image = (ImageView) view.findViewById(R.id.header_image);
        title_text = (Spinner) view.findViewById(R.id.title_text);
        sizes = (Spinner) view.findViewById(R.id.sizes);
        sizes_1 = (Spinner) view.findViewById(R.id.sizes_1);
        sizes_2 = (Spinner) view.findViewById(R.id.sizes_2);
        sizes_3 = (Spinner) view.findViewById(R.id.sizes_3);
        extras = (LinearLayout) view.findViewById(R.id.extras);
        price = (TextView) view.findViewById(R.id.price);

        init_array();
        init();

        title_text.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (position == 0)
                header_image.setImageResource(R.mipmap.option_3);
                if (position == 1)
                header_image.setImageResource(R.mipmap.option_2);
                if (position == 2)
                header_image.setImageResource(R.mipmap.option_4);
                if (position == 3)
                header_image.setImageResource(R.mipmap.option_5);
                change_sizes(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        payment_start = (Button) view.findViewById(R.id.payment_start);
        payment_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (title_text.getSelectedItem().toString().equals("Club Lotto")){
                    if (sizes.getSelectedItem().toString().equals(sizes_1.getSelectedItem().toString())||
                            sizes.getSelectedItem().toString().equals(sizes_2.getSelectedItem().toString())||
                            sizes.getSelectedItem().toString().equals(sizes_3.getSelectedItem().toString())){
                        Toast.makeText(getActivity(), "You cannot select same number twice", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (sizes_1.getSelectedItem().toString().equals(sizes.getSelectedItem().toString()) ||
                            sizes_1.getSelectedItem().toString().equals(sizes_2.getSelectedItem().toString())||
                            sizes_1.getSelectedItem().toString().equals(sizes_3.getSelectedItem().toString())){
                        Toast.makeText(getActivity(), "You cannot select same number twice", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (sizes_2.getSelectedItem().toString().equals(sizes.getSelectedItem().toString()) ||
                            sizes_2.getSelectedItem().toString().equals(sizes_1.getSelectedItem().toString())||
                            sizes_2.getSelectedItem().toString().equals(sizes_3.getSelectedItem().toString())){
                        Toast.makeText(getActivity(), "You cannot select same number twice", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (sizes_3.getSelectedItem().toString().equals(sizes.getSelectedItem().toString()) ||
                            sizes_3.getSelectedItem().toString().equals(sizes_1.getSelectedItem().toString())||
                            sizes_3.getSelectedItem().toString().equals(sizes_2.getSelectedItem().toString())){
                        Toast.makeText(getActivity(), "You cannot select same number twice", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                else if (sizes.getSelectedItem().toString().equals("Select Size")){
                    Toast.makeText(getActivity(), "Please Select Size", Toast.LENGTH_SHORT).show();
                    return;
                }
                setPayment_start();
            }
        });
        return view;
    }

    void setPayment_start(){
        getResult("https://grudging-steeples.000webhostapp.com/braintree/braintree-php-3.40.0/main.php");
    }

    public void getResult(String url)  {
        final ProgressDialog dialog = new ProgressDialog(getActivity());
        dialog.setMessage("Loading...");
        dialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String result) {
                if (!result.isEmpty()) {
                    dialog.dismiss();
                    Intent intent = new Intent(getActivity(), BraintreePaymentActivity.class);
                    intent.putExtra(BraintreePaymentActivity.EXTRA_CLIENT_TOKEN, result);

                    // REQUEST_CODE is arbitrary and is only used within this activity.
                    startActivityForResult(intent, 1001);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), ""+error.getMessage(), Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();

                String price = "";

                if (title_text.getSelectedItem().toString().equals("Club Lotto"))
                    price = "1";
                else if (title_text.getSelectedItem().toString().equals("1/4 Zip"))
                    price = "40";
                else if (title_text.getSelectedItem().toString().equals("Jersey"))
                    price = "40";
                else if (title_text.getSelectedItem().toString().equals("Shorts"))
                    price = "12";
                else if (title_text.getSelectedItem().toString().equals("Jumper"))
                    price = "40";

                params.put("amount",price);
                return params;
            }
        };

        stringRequest.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 30000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 30000;
            }

            @Override
            public void retry(VolleyError volleyError) throws VolleyError {

            }
        });
        MySingleTon.getInstance(getActivity()).addToRequestQueue(stringRequest);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1001) {
            switch (resultCode) {
                case BraintreePaymentActivity.RESULT_OK:
                    String paymentMethodNonce = data
                            .getStringExtra(BraintreePaymentActivity.EXTRA_PAYMENT_METHOD_NONCE);
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Orders").push();
                    if (title_text.getSelectedItem().toString().equals("Club Lotto")){
                        reference.child("Title").setValue(title_text.getSelectedItem().toString());
                        reference.child("Size 1").setValue(sizes.getSelectedItem().toString());
                        reference.child("Size 2").setValue(sizes_1.getSelectedItem().toString());
                        reference.child("Size 3").setValue(sizes_2.getSelectedItem().toString());
                        reference.child("Size 4").setValue(sizes_3.getSelectedItem().toString());
                    }else {
                        reference.child("Title").setValue(title_text.getSelectedItem().toString());
                        reference.child("Size 1").setValue(sizes.getSelectedItem().toString());
                    }

                    SharedPreferences preferences = getActivity().getSharedPreferences("user",MODE_PRIVATE);

                    String msg = "Item ordered: "+title_text.getSelectedItem().toString()+"\n" +
                            "Price: "+price.getText().toString()+"\n\n" +
                            "" +
                            "Name: "+preferences.getString("Name","")+"\n" +
                            "Address: "+preferences.getString("Address","")+"\n" +
                            "Phone Number: "+preferences.getString("Mobile","")+"\n" +
                            "Email Address: "+preferences.getString("Email","");

                    //Creating SendMail object
                    SendMail sm = new SendMail(getActivity(), FirebaseAuth.getInstance().getCurrentUser().getEmail(), "Order Placed", msg);

                    //Executing sendmail to send email
                    sm.execute();
                    SendMail sm_ = new SendMail(getActivity(), "aughergfc@gmail.com", "Order Placed", msg);

                    //Executing sendmail to send email
                    sm_.execute();
                    Toast.makeText(getActivity(), "Order Placed", Toast.LENGTH_SHORT).show();
                    break;
                case BraintreePaymentActivity.BRAINTREE_RESULT_DEVELOPER_ERROR:
                    Toast.makeText(getActivity(), "An error has occurred", Toast.LENGTH_SHORT).show();
                case BraintreePaymentActivity.BRAINTREE_RESULT_SERVER_ERROR:

                    Toast.makeText(getActivity(), "A Server Error has occurred", Toast.LENGTH_SHORT).show();
                case BraintreePaymentActivity.BRAINTREE_RESULT_SERVER_UNAVAILABLE:
                    Toast.makeText(getActivity(), "A Server Error has occurred", Toast.LENGTH_SHORT).show();
                    // handle errors here, a throwable may be available in
                    // data.getSerializableExtra(BraintreePaymentActivity.EXTRA_ERROR_MESSAGE)
                    break;
                default:
                    break;
            }
        }
    }

    void init(){
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>
                (getActivity(), android.R.layout.simple_spinner_item,
                        title_array);
        title_text.setAdapter(spinnerArrayAdapter);

        sizes.setAdapter(new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item,lotto_extras_array));
        sizes_1.setAdapter(new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item,lotto_extras_array));
        sizes_2.setAdapter(new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item,lotto_extras_array));
        sizes_3.setAdapter(new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item,lotto_extras_array));
    }

    void init_array(){

        //title_array.add("Club Lotto");
        title_array.add("1/4 Zip");
        title_array.add("Jersey");
        title_array.add("Shorts");
        title_array.add("Jumper");

        for (int i=1;i<=30;i++){
            lotto_extras_array.add(i+"");
        }
    }

    void change_sizes(int position){
        if (position == -1){
            extras.setVisibility(View.VISIBLE);
            sizes.setAdapter(new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item,lotto_extras_array));

        }else {
            if (title_text.getSelectedItem().toString().equals("Shorts"))
            price.setText("Price: £12");else price.setText("Price: £40");
            extras.setVisibility(View.GONE);
            sizes_array = new ArrayList<>();
            sizes_array.add("Select Size");
            sizes_array.add("9-11");
            sizes_array.add("11-13");
            sizes_array.add("Small");
            sizes_array.add("Medium");
            sizes_array.add("Large");
            sizes.setAdapter(new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item,sizes_array));
        }
    }
}
