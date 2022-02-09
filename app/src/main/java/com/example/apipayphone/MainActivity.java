package com.example.apipayphone;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    RequestQueue requestQueue;
    EditText txtCodPais, txtCelular, txtIdentificacion, txtMonto, txtTarifa, txtReferencia, txtJson;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txtCodPais = findViewById(R.id.txtpais);
        txtCelular = findViewById(R.id.txtcelular);
        txtIdentificacion = findViewById(R.id.txtcedula);
        txtMonto = findViewById(R.id.txtmonto);
        txtTarifa = findViewById(R.id.txttarifa);
        txtReferencia = findViewById(R.id.txtreferencia);
        txtJson = findViewById(R.id.txtJson);
        requestQueue = Volley.newRequestQueue(this);
    }
    public void Pagar(View view) {
        requestPago();

    }
    private void requestPago() {
        String url = "https://pay.payphonetodoesposible.com/api/Sale";
        HashMap<String, String> hash = new HashMap<>();
        hash.put("phoneNumber", txtCelular.getText().toString());
        hash.put("countryCode", txtCodPais.getText().toString());
        hash.put("clientUserId", txtIdentificacion.getText().toString());
        hash.put("reference", txtReferencia.getText().toString());
        double amount, tax, amountWithTax;
        amount = Double.parseDouble(txtMonto.getText().toString());
        amount = amount * 100;
        int intAmount, intTax, intAamountWithTax;
        intAmount = (int) Math.round(amount);
        hash.put("amount", String.valueOf(intAmount));
        tax = Double.parseDouble(txtTarifa.getText().toString());
        tax = (intAmount * tax / 100);

        if (tax > 0) {
            intTax = (int) Math.round(tax);
            hash.put("tax", String.valueOf(intTax));
            amountWithTax = amount - tax;
            txtTarifa.setText((amountWithTax / 100) + "");
            intAamountWithTax = (int) Math.round(amountWithTax);
            hash.put("amountWithTax", String.valueOf(intAamountWithTax));

            hash.put("amountWithoutTax", "0");
        } else {
            hash.put("amountWithTax", "0");
            hash.put("amountWithoutTax", String.valueOf(intAmount));
        }

        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        String date = format.format(new Date());
        hash.put("clientTransactionId", date);
        Toast.makeText(MainActivity.this, "Se generó la petición: " + date, Toast.LENGTH_LONG).show();

        JSONObject js = new JSONObject(hash);
        txtJson.setText(js.toString());
        System.out.println(js.toString());
        //JsonArrayRequest jsonRequest = new JsonArrayRequest(Request.Method.POST, url, createMyReqSuccessListener(), createMyReqErrorListener()) { protected Map<String, String> getParams() throws com.android.volley.AuthFailureError { Map<String, String> params = new HashMap<String, String>(); params.put("param1", num1); params.put("param2", num2); return params; }; };
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url, js, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String transaction = response.getString("transactionId");
                    Toast.makeText(MainActivity.this, "Se completó la transacción: " + transaction, Toast.LENGTH_LONG).show();
                    txtJson.append(",\n{\"transactionId\":\"" + transaction + "\"}");

                } catch (JSONException e) {
                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError ex) {
                Toast.makeText(MainActivity.this, "Incorrecto\n" + ex.getMessage(), Toast.LENGTH_LONG).show();

                System.out.println(ex.toString());
            }
        }) {
            @Override
            public HashMap<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headerMap = new HashMap<>();
                headerMap.put("Content-Type", "application/json");
                headerMap.put("Accept", "application/json");
                headerMap.put("Authorization", "Bearer pzI1aHEXvSjitbetdPSRN2PTeJCQPDPM6Mq5WZ5PfNqq_OktTuSJ6_ksrXPcVji0MG9OUlO9QfYE-EID4Ir6MYMkOomb-zQSEsn88m6_-yulLOP1-rqDCeZFMLo8WyM9rdmWR_t7Jt1fr43mfVVrabkB16vHBzWtkCrB5Yy9gfD7hXwjoTVnPXdGJw7DVCLzdw49Xpa8P9_I7bW3dU-EtPfwmzj3Xk7KIJ7nbyG32oR9QsJdD8Pp2xORZm6qGubQzn1YUp7saw7dhmEC2Lp7JEHipAU_NB15w-SUPQUzJ1ym-ZvDryS16Gtzc74kwP0mvF77J7C8qXFoV8nLNUZO1hxX8pA");
                return headerMap;
            }
        };

        requestQueue.add(jsonRequest);

    }
}