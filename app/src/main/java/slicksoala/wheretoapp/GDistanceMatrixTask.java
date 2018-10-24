package slicksoala.wheretoapp;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;


public class GDistanceMatrixTask extends AsyncTask<ArrayList<Place>, Void, double[][]> {
    final String DISTMATRIX_BASE = "https://maps.googleapis.com/maps/api/distancematrix/json?units=metric";
    final String ORIG_PAR = "&origins=";
    final String DEST_PAR = "&destinations=";
    final String KEY_PAR = "&key=";
    final String API_KEY = "AIzaSyAr-MIu6A-LmtXGsm94fDfIjICLguluajQ";
    @Override
    protected double[][] doInBackground(ArrayList<Place>... al) {
        ArrayList<Place> kList = al[0];
        double[][] distMatrix = new double[kList.size()][kList.size()];

        StringBuilder sb = new StringBuilder();
        if (kList != null && kList.size() != 0) {
            sb.append(Double.toString(kList.get(0).getLatitude()));
            sb.append(",");
            sb.append(Double.toString(kList.get(0).getLongitude()));
            for (int i = 1; i < kList.size(); i++) {
                sb.append("|");
                sb.append(Double.toString(kList.get(i).getLatitude()));
                sb.append(",");
                sb.append(Double.toString(kList.get(i).getLongitude()));
            }
        }
        String origdest = sb.toString();
        String urlString = DISTMATRIX_BASE + ORIG_PAR + origdest + DEST_PAR + origdest + KEY_PAR + API_KEY;

        URL url;
        HttpURLConnection urlConnection = null;
        String resultString = "";

        try {
            url = new URL(urlString);
            urlConnection = (HttpURLConnection) url.openConnection();

            InputStream in = urlConnection.getInputStream();
            InputStreamReader reader = new InputStreamReader(in);

            int data = reader.read();
            while (data != -1) {
                char current = (char) data;
                resultString += current;
                data = reader.read();
            }
            JSONObject jsonObject = new JSONObject(resultString);
            if (jsonObject.has("rows")) {
                JSONArray rowsArray = jsonObject.getJSONArray("rows");
                for (int i = 0; i < rowsArray.length(); i++) {
                    if (rowsArray.getJSONObject(i).has("elements")) {
                        JSONArray columnsArray = rowsArray.getJSONObject(i).
                                getJSONArray("elements");
                        for (int j = 0; j < columnsArray.length(); j++) {
                            if (columnsArray.getJSONObject(j).has("duration")) {
                                if (columnsArray.getJSONObject(j).getJSONObject("duration").
                                        has("value")) {
                                    int durSeconds = columnsArray.getJSONObject(j).getJSONObject("duration").
                                            getInt("value");
                                    double durMinutes = ((double) durSeconds) / 60;
                                    distMatrix[i][j] = durMinutes;
                                }
                            }
                        }
                    }
                }
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return distMatrix;
    }
}
