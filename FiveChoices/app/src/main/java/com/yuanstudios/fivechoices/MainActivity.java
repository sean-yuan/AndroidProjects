package com.yuanstudios.fivechoices;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {
    private Random randomGenerator;
    private Button generatebutton;
    private ListView resultlistview;
    ArrayList<MyGooglePlaces> results;
    ArrayList<String> narrowed;
    ArrayAdapter<String> arrayAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        generatebutton = (Button) findViewById(R.id.generatebutton);
        resultlistview = (ListView) findViewById(R.id.list2);
        randomGenerator = new Random();
        results = new ArrayList<MyGooglePlaces>();
        narrowed = new ArrayList<String>();
        arrayAdapter = new ArrayAdapter<String>(
                MainActivity.this,
                android.R.layout.simple_list_item_1,
                narrowed );
        resultlistview.setAdapter(arrayAdapter);
        generatebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*try {
                    results = new RetrieveFeedTask().execute(" ").get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
                narrowed = getFive(results);
                arrayAdapter = new ArrayAdapter<String>(
                        MainActivity.this,
                        android.R.layout.simple_list_item_1,
                        narrowed );
                resultlistview.setAdapter(arrayAdapter);*/
            }
        });

    }
    private ArrayList<String> getFive(ArrayList<MyGooglePlaces> resultlist){
        ArrayList<String> finalFive = new ArrayList<String>();
        for( int i = 0; i< 5; i++){
            int index = randomGenerator.nextInt(resultlist.size());
            String placename = resultlist.get(index).getName();
            finalFive.add(placename);
        }
        return finalFive;
    }

    class RetrieveFeedTask extends AsyncTask<String, Void, ArrayList<MyGooglePlaces>> {
        @Override
        protected ArrayList<MyGooglePlaces> doInBackground(String... params) {
            double latitude=30.7333;
            double longitude=76.7794;
            String API_KEY="AIzaSyCg8ee432_yAVQZHNbG-oblWK_oxR7obaU";
            String url= "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" + latitude + "," + longitude + "&radius=10&type=restaurant&key=" + API_KEY;
            return getPlaces(url);
        }
        private ArrayList<MyGooglePlaces> getPlaces(String urlQuery) {
            //code for API level 23 as httpclient is depricated in API 23
            StringBuffer sb=null;
            URL url;
            HttpURLConnection urlConnection = null;
            try {
                url = new URL(urlQuery);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader isw = new InputStreamReader(in);
                int data = isw.read();
                sb=new StringBuffer("");
                while (data != -1) {
                    sb.append((char)data);
                    //char current = (char) data;
                    data = isw.read();
                    // System.out.print(current);
                }
            } catch (Exception e) {
                System.out.print("issue1");
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    System.out.print("issue2");
                    urlConnection.disconnect();
                }
            }
            return   parseGoogleParse(sb.toString());
        }

        private ArrayList parseGoogleParse(final String response) {
            ArrayList<MyGooglePlaces> temp = new ArrayList();
            try {
                // make an jsonObject in order to parse the response
                JSONObject jsonObject = new JSONObject(response);
                // make an jsonObject in order to parse the response
                if (jsonObject.has("results")) {
                    JSONArray jsonArray = jsonObject.getJSONArray("results");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        MyGooglePlaces poi = new MyGooglePlaces();
                        if (jsonArray.getJSONObject(i).has("name")) {
                            poi.setName(jsonArray.getJSONObject(i).optString("name"));
                            poi.setRating(jsonArray.getJSONObject(i).optString("rating", " "));
                            if (jsonArray.getJSONObject(i).has("opening_hours")) {
                                if (jsonArray.getJSONObject(i).getJSONObject("opening_hours").has("open_now")) {
                                    if (jsonArray.getJSONObject(i).getJSONObject("opening_hours").getString("open_now").equals("true")) {
                                        poi.setOpenNow("YES");
                                    } else {
                                        poi.setOpenNow("NO");
                                    }
                                }
                            } else {
                                poi.setOpenNow("Not Known");
                            }
                            if (jsonArray.getJSONObject(i).has("geometry"))
                            {
                                if (jsonArray.getJSONObject(i).getJSONObject("geometry").has("location"))
                                {
                                    if (jsonArray.getJSONObject(i).getJSONObject("geometry").getJSONObject("location").has("lat"))
                                    {
                                        poi.setLatLng(Double.parseDouble(jsonArray.getJSONObject(i).getJSONObject("geometry").getJSONObject("location").getString("lat")), Double.parseDouble(jsonArray.getJSONObject(i).getJSONObject("geometry").getJSONObject("location").getString("lng")));
                                    }
                                }
                            }
                            if (jsonArray.getJSONObject(i).has("vicinity")) {
                                poi.setVicinity(jsonArray.getJSONObject(i).optString("vicinity"));
                            }
                            if (jsonArray.getJSONObject(i).has("types")) {
                                JSONArray typesArray = jsonArray.getJSONObject(i).getJSONArray("types");
                                for (int j = 0; j < typesArray.length(); j++) {
                                    poi.setCategory(typesArray.getString(j) + ", " + poi.getCategory());
                                }
                            }
                        }
                        //if(temp.size()<5)
                        temp.add(poi);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                return new ArrayList();
            }
            return temp;
        }
    }
}
