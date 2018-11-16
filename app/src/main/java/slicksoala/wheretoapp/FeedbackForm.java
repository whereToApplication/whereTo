package slicksoala.wheretoapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.OutputStream;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class FeedbackForm extends AppCompatActivity {

    final static String url = "https://where2trip.herokuapp.com/feedback";
    EditText feedbackTxt;
    Button submitBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback_form);
        feedbackTxt = findViewById(R.id.feedbackText);
        submitBtn = findViewById(R.id.submitBtn);

        submitBtn.setOnClickListener(v -> submitFeedback());
    }

    public void submitFeedback() {
        String feedback = feedbackTxt.getText().toString();
        Thread postThread = new Thread(() -> {
            try  {
                sendPost(feedback);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        postThread.start();
        Toast.makeText(FeedbackForm.this,
                "Feedback received! Thank you for your inputs!",
                Toast.LENGTH_LONG).show();
        Intent backIntent = new Intent(this, DetailsForm.class);
        startActivity(backIntent);
    }

    public void sendPost(String feedback) throws Exception {
        URL obj = new URL(url);
        HttpsURLConnection connection = (HttpsURLConnection) obj.openConnection();
        connection.setRequestMethod("POST");
        OutputStream outputStream =  connection.getOutputStream();
        outputStream.write(feedback.getBytes("UTF-8"));
        outputStream.flush();
    }
}
