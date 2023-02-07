package ir.Bprogrammer.ika;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONObject;
import android.app.Activity;

public class RegisterActivity extends Activity {
    RequestQueue mqueue;
    JSONObject result;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mqueue = Volley.newRequestQueue(this);
        setContentView(R.layout.activity_register);
        
        TextView title = findViewById(R.id.title);
        final EditText field = findViewById(R.id.field);
        final Button login = findViewById(R.id.login);
        Button signup = findViewById(R.id.signup);
        
        title.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/vazirblack.ttf"), 1);
        field.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/vazir.ttf"), 1);
        login.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/vazir.ttf"), 1);
        signup.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/vazir.ttf"), 1);
        
        login.setEnabled(false);
        
        field.addTextChangedListener(new TextWatcher(){

                @Override
                public void beforeTextChanged(CharSequence s, int start,int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    int length = String.valueOf(field.getText()).length();
                    //Toast.makeText(getApplication(), length+"", Toast.LENGTH_SHORT).show();
                    if(length == 32){
                        login.setEnabled(true);
                    }else {
                        login.setEnabled(false);
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                    
                }
            });
        
        login.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    try{
                    String value = field.getText()+"";
                    String url = getResources().getString(R.string.ServerURL)+"/api/getUser?UUID="+value;
                    Login(url);
                    //Toast.makeText(getApplication(), result+"", Toast.LENGTH_SHORT).show();
                    if(result.getString("status").equals("ok")){
                        SharedPreferences shared = getSharedPreferences("Prefs",MODE_PRIVATE);
                        SharedPreferences.Editor editor = shared.edit();
                        editor.putString("UUID", value);
                        editor.apply();
                        
                        Intent move = new Intent(RegisterActivity.this, MainActivity.class);
                        move.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        move.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(move);
                        overridePendingTransition(0,0);
                    }else {
                        Toast.makeText(getApplication(), "شناسه اشتباه است!", Toast.LENGTH_LONG).show();
                    }
                 } catch (Exception e){
                     Toast.makeText(getApplication(), e.getMessage(), Toast.LENGTH_SHORT).show();
                 }
               }
            });
        
        signup.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    try{
                        Intent move = new Intent(Intent.ACTION_VIEW, Uri.parse(getResources().getString(R.string.ServerURL)));
                        move.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        move.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(move);
                        overridePendingTransition(0,0);
                    } catch ( Exception e ){
                        Toast.makeText(getApplication(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
               }
            });
    }
    
    private void Login(String URL){
        JsonObjectRequest request = new JsonObjectRequest(
            Request.Method.GET,
            URL,
            null,
            new Response.Listener<JSONObject>(){
                @Override
                public void onResponse(JSONObject response){
                    try {
                        result = response;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError e){
                    e.printStackTrace();
                }
            });

        mqueue.add(request);
    }
}
