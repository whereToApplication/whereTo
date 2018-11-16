package slicksoala.wheretoapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import java.io.OutputStream;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class FeedbackForm extends AppCompatActivity {

    final static String url = "http://where2trip.herokuapp.com/feedback";
    EditText feedbackTxt;
    Button submitBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback_form);
        feedbackTxt = findViewById(R.id.feedbackText);
        submitBtn = findViewById(R.id.submitBtn);
    }

    public void submitFeedback() {
        String feedback = feedbackTxt.getText().toString();
        try {
            sendPost(feedback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void sendPost(String feedback) throws Exception {
        URL obj = new URL(url);
        HttpsURLConnection connection = (HttpsURLConnection) obj.openConnection();
        connection.setRequestMethod("POST");
        OutputStream outputStream =  connection.getOutputStream();
        outputStream.write(feedback.getBytes("UTF-8"));
        outputStream.flush();
    }
}
