// DONO...
package com.uni.antonkluev.myapplication;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

// UI
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

// LOGGING
import android.util.Log;
import android.widget.Toast;

// HTTP
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

// ASYNC
import android.os.AsyncTask;
import android.graphics.Bitmap;

// ACTIVITY
public class MainActivity extends AppCompatActivity {

    TextView  textView;
    EditText  editText;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // textfields
        editText  = (EditText)  findViewById(R.id.editText);
        textView  = (TextView)  findViewById(R.id.textView);
        imageView = (ImageView) findViewById(R.id.imageView);
        // button
        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener () {
           public void onClick(View v) {
               // https://developer.android.com/reference/android/util/Log.html
               Log.v("onClick", "send");
               // http://stackoverflow.com/questions/4531396/get-value-of-a-edit-text-field
               String address = editText.getText().toString();
               // https://www.tutorialspoint.com/java/java_string_matches.htm
               if (address.matches("(.*)http://(.*)")) {
                   new doHTTPRequest().execute(address);
//                   webview.loadUrl(address);
               } else
                   // https://www.mkyong.com/android/android-toast-example/
                   Toast.makeText(
                       getApplicationContext(),
                       "Please enter valid URL...",
                       Toast.LENGTH_SHORT).show();
           }
        });
    }

    // http://stackoverflow.com/questions/8376072/whats-the-readstream-method-i-just-can-not-find-it-anywhere
    private ByteArrayOutputStream readStream(InputStream is) {
        try {
            ByteArrayOutputStream bo = new ByteArrayOutputStream();
            int i = is.read();
            while (i != -1) {
                bo.write(i);
                i = is.read();
            }
            return bo;
        } catch (IOException e) {
            return null;
        }
    }

    public class Tulpet {
        public String type = "error";
        public ByteArrayOutputStream content;
    }

    // https://developer.android.com/reference/android/os/AsyncTask.html
    private class doHTTPRequest extends AsyncTask <String, String, Tulpet> {
        @Override
        protected Tulpet doInBackground(String... addresses) {
            // https://developer.android.com/reference/java/net/HttpURLConnection.html
            try {
                URL url = new URL(addresses[0]);
                HttpURLConnection call = (HttpURLConnection) url.openConnection();
                try {
                    Tulpet tulpet  = new Tulpet();
                    tulpet.type    = call.getContentType();
                    tulpet.content = readStream(new BufferedInputStream(call.getInputStream()));
                    return tulpet;
                } finally { call.disconnect(); }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return new Tulpet();
        }
        @Override
        protected void onPostExecute(Tulpet response) {
            if (response.type.equals("error")) {
                Toast.makeText(
                    getApplicationContext(),
                    "Please enter valid URL...",
                    Toast.LENGTH_SHORT).show();
            } else if (response.type.contains("text/html")) {
                Toast.makeText(
                        getApplicationContext(),
                        "text/html",
                        Toast.LENGTH_SHORT).show();
                textView.setText(response.content.toString());
            } else if (response.type.contains("image/png")) {
                Toast.makeText(
                    getApplicationContext(),
                    "image/png",
                    Toast.LENGTH_SHORT).show();
                textView.setText(response.content.toString());
                // https://developer.android.com/reference/java/io/ByteArrayOutputStream.html
                byte[] bytearray = response.content.toByteArray();
                Bitmap bmp = BitmapFactory.decodeByteArray(bytearray, 0, bytearray.length);
                // https://developer.android.com/reference/android/widget/ImageView.html
                imageView.setImageBitmap(Bitmap.createScaledBitmap(bmp, imageView.getWidth(), imageView.getHeight(), false));
            } else {
                Toast.makeText(
                    getApplicationContext(),
                    "other type: " + response.type,
                    Toast.LENGTH_SHORT).show();
            }
        }
    }
}
