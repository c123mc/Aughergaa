package com.conor.aughergaa.Fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class Lotto extends Fragment {

    Button payment_start;
    Spinner sizes,sizes_1,sizes_2,sizes_3;

    ArrayList<String> title_array = new ArrayList<>();
    ArrayList<String> lotto_extras_array = new ArrayList<>();
    EditText entrant;
    TextView price;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_lotto, container, false);
        ImageView header_image = (ImageView) view.findViewById(R.id.header_image);
        header_image.setImageResource(R.mipmap.option_1);
        sizes = (Spinner) view.findViewById(R.id.sizes);
        sizes_1 = (Spinner) view.findViewById(R.id.sizes_1);
        sizes_2 = (Spinner) view.findViewById(R.id.sizes_2);
        sizes_3 = (Spinner) view.findViewById(R.id.sizes_3);
        price = (TextView) view.findViewById(R.id.price);
        entrant = (EditText) view.findViewById(R.id.entrant);

        init_array();
        init();

        payment_start = (Button) view.findViewById(R.id.payment_start);
        payment_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                    if (entrant.getText().toString().isEmpty()){
                        Toast.makeText(getActivity(), "Entrant is empty, please enter a name", Toast.LENGTH_SHORT).show();
                        return;
                    }
                setPayment_start();
            }
        });
        return view;
    }

    void setPayment_start(){
        getResult("https://iamadeeel.000webhostapp.com/BrainTree/braintree-php-3.40.0/main.php");
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
                Map<String, String> parms = new HashMap<String, String>();

                String price = "1";

                parms.put("amount",price);
                return parms;
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
                        reference.child("Title").setValue("Club Lotto");
                        reference.child("Entrant").setValue(entrant.getText().toString());
                        reference.child("Size 1").setValue(sizes.getSelectedItem().toString());
                        reference.child("Size 2").setValue(sizes_1.getSelectedItem().toString());
                        reference.child("Size 3").setValue(sizes_2.getSelectedItem().toString());
                        reference.child("Size 4").setValue(sizes_3.getSelectedItem().toString());


                    SharedPreferences preferences = getActivity().getSharedPreferences("user",MODE_PRIVATE);
                    String msg = "Name: "+preferences.getString("Name","")+"\n" +
                                    "Address: "+preferences.getString("Address","")+"\n" +
                                    "Phone Number: "+preferences.getString("Mobile","")+"\n" +
                                    "Email Address: "+preferences.getString("Email","")+"\n\nDate of Entry: " +new Date() +
                            "\nNumbers Picked: "+sizes.getSelectedItem().toString()+", "+sizes_1.getSelectedItem().toString()+", "+sizes_2.getSelectedItem().toString()+", "+sizes_3.getSelectedItem().toString()+"\n\n" +
                            "Name of Entrant: "+entrant.getText().toString()+"\n";
                    //Creating SendMail object
                    SendMail sm = new SendMail(getActivity(), FirebaseAuth.getInstance().getCurrentUser().getEmail(), "Lotto Entry", msg);

                    //Executing sendmail to send email
                    sm.execute();
                    SendMail sm_ = new SendMail(getActivity(), "aughergfc@gmail.com", "Lotto Entry", msg);

                    //Executing sendmail to send email
                    sm_.execute();

                    Toast.makeText(getActivity(), "Order Placed successfully", Toast.LENGTH_SHORT).show();
                    break;
                case BraintreePaymentActivity.BRAINTREE_RESULT_DEVELOPER_ERROR:
                    Toast.makeText(getActivity(), "Some Error occur", Toast.LENGTH_SHORT).show();
                case BraintreePaymentActivity.BRAINTREE_RESULT_SERVER_ERROR:

                    Toast.makeText(getActivity(), "Server Error occur", Toast.LENGTH_SHORT).show();
                case BraintreePaymentActivity.BRAINTREE_RESULT_SERVER_UNAVAILABLE:
                    Toast.makeText(getActivity(), "Server Error occur", Toast.LENGTH_SHORT).show();

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

}
