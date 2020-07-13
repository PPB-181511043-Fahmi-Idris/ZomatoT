package com.google.zomatot;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.google.zomatot.adapters.RestaurantAdapter;
import com.google.zomatot.model.Restaurant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mRestaurantRecyclerView;
    private RestaurantAdapter mAdapter;
    private ArrayList<Restaurant> mRestaurantCollection;

    public static interface ClickListener{
        public void onClick(View view, int position);
        public void onLongClick(View view,int position);
    }

    class RecyclerTouchListener implements RecyclerView.OnItemTouchListener{

        private ClickListener clicklistener;
        private GestureDetector gestureDetector;

        public RecyclerTouchListener(Context context, final RecyclerView recycleView, final ClickListener clicklistener){

            this.clicklistener=clicklistener;
            gestureDetector=new GestureDetector(context,new GestureDetector.SimpleOnGestureListener(){
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child=recycleView.findChildViewUnder(e.getX(),e.getY());
                    if(child!=null && clicklistener!=null){
                        clicklistener.onLongClick(child,recycleView.getChildAdapterPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            View child=rv.findChildViewUnder(e.getX(),e.getY());
            if(child!=null && clicklistener!=null && gestureDetector.onTouchEvent(e)){
                clicklistener.onClick(child,rv.getChildAdapterPosition(child));
            }

            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {

        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestPermissions(new String[]{
                Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_FINE_LOCATION
        }, 100);

        init();
        new FetchDataTask().execute();
    }

    private void init() {
        mRestaurantRecyclerView = (RecyclerView) findViewById(R.id.restaurant_recycler);
        mRestaurantRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRestaurantRecyclerView.setHasFixedSize(true);
        mRestaurantRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), mRestaurantRecyclerView,
                new ClickListener() {
                    @Override
                    public void onClick(View view, int position) {
                        Intent intent = new Intent(getApplicationContext(), DetailActivity.class);
                        intent.putExtra("restaurantId", mRestaurantCollection.get(position).getId());
                        startActivity(intent);
                    }

                    @Override
                    public void onLongClick(View view, int position) {

                    }
                }));

        mRestaurantCollection = new ArrayList<>();
        mAdapter = new RestaurantAdapter(mRestaurantCollection, this);
        mRestaurantRecyclerView.setAdapter(mAdapter);
    }

    public class FetchDataTask extends AsyncTask<Void, Void, Void> {
        private String mZomatoString;

        @Override
        protected Void doInBackground(Void... params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            Uri builtUri = Uri.parse(getString(R.string.zomato_api));
            URL url;
            try {
                url = new URL(builtUri.toString());
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setRequestProperty("user-key", "0829293c0cb0c3fe86a16c204d75e80a");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    //Nothing to do
                    return null;
                }

                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    return null;
                }

                mZomatoString = buffer.toString();
                JSONObject jsonObject = new JSONObject(mZomatoString);

                Log.v("Response", jsonObject.toString());

                JSONArray restaurantsArray = jsonObject.getJSONArray("restaurants");

                for (int i = 0; i < restaurantsArray.length(); i++) {

                    Log.v("BRAD_", i + "");
                    long id;
                    String name;
                    String address;
                    String currency;
                    String imageUrl;
                    long lon;
                    long lat;
                    long cost;
                    float rating;


                    JSONObject jRestaurant = (JSONObject) restaurantsArray.get(i);
                    jRestaurant = jRestaurant.getJSONObject("restaurant");
                    JSONObject jLocattion = jRestaurant.getJSONObject("location");
                    JSONObject jRating = jRestaurant.getJSONObject("user_rating");

                    id = jRestaurant.getLong("id");
                    name = jRestaurant.getString("name");
                    address = jLocattion.getString("address");
                    lat = jLocattion.getLong("latitude");
                    lon = jLocattion.getLong("longitude");
                    currency = jRestaurant.getString("currency");
                    cost = jRestaurant.getInt("average_cost_for_two");
                    imageUrl = jRestaurant.getString("thumb");
                    rating = (float) jRating.getDouble("aggregate_rating");


                    Restaurant restaurant = new Restaurant();
                    restaurant.setId(id);
                    restaurant.setName(name);
                    restaurant.setAddress(address);
                    restaurant.setLatitiude(lat);
                    restaurant.setLongitude(lon);
                    restaurant.setCurrency(currency);
                    restaurant.setCost(String.valueOf(cost));
                    restaurant.setImageUrl(imageUrl);
                    restaurant.setRating(String.valueOf(rating));

                    mRestaurantCollection.add(restaurant);
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("MainActivity", "Error closing stream", e);
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            mAdapter.notifyDataSetChanged();
        }
    }
}
