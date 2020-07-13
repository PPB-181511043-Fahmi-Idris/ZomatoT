package com.google.zomatot;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.zomatot.model.Restaurant;

import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.views.MapView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class DetailActivity extends AppCompatActivity {
    MapView mMapView;
    ImageView restaurantImageView;
    TextView restaurantNameTextView;
    TextView restaurantAddressTextView;
    TextView restaurantRatingTextView;
    TextView costTextView;
    Restaurant aRestaurant;
    private Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_detail);
        this.context = getApplicationContext();

        Intent iGet = getIntent();
        long restaurantId = iGet.getLongExtra("restaurantId", 0);

        restaurantImageView = (ImageView) findViewById(R.id.imageview_restaurant);
        restaurantNameTextView = (TextView) findViewById(R.id.textview_restaurant_name);
        restaurantAddressTextView = (TextView) findViewById(R.id.restaurant_address_textview);
        restaurantRatingTextView = (TextView) findViewById(R.id.rating);
        costTextView = (TextView) findViewById(R.id.cost_for_two_textview);

        Configuration.getInstance().setUserAgentValue("com-google-zomatot");
        mMapView = (MapView) findViewById(R.id.map);
        mMapView.setTileSource(TileSourceFactory.MAPNIK);
        mMapView.setTilesScaledToDpi(true);
        mMapView.setBuiltInZoomControls(true);
        mMapView.setMultiTouchControls(true);

        new FetchRestaurantDetail().execute(restaurantId);
    }
    public class FetchRestaurantDetail extends AsyncTask<Long, Void, Restaurant> {
        private String mZomatoString;

        @Override
        protected Restaurant doInBackground(Long... params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            Uri builtUri = Uri.parse(getString(R.string.zomato_api_detail) + "?res_id=" + params[0]);
            URL url;
            try {
                url = new URL(builtUri.toString());
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setRequestProperty("user-key", "acfd3e623c5f01289bd87aaaff1926c1");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {

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
                JSONObject jRestaurant = new JSONObject(mZomatoString);

                Log.v("Response", jRestaurant.toString());

               {

                    long id;
                    String name;
                    String address;
                    String currency;
                    String imageUrl;
                    double lon;
                    double lat;
                    long cost;
                    float rating;

                    JSONObject jLocattion = jRestaurant.getJSONObject("location");
                    JSONObject jRating = jRestaurant.getJSONObject("user_rating");

                    id = jRestaurant.getLong("id");
                    name = jRestaurant.getString("name");
                    address = jLocattion.getString("address");
                    lat = jLocattion.getDouble("latitude");
                    lon = jLocattion.getDouble("longitude");
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
                    return restaurant;

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
        protected void onPostExecute(Restaurant restaurant) {
            aRestaurant = restaurant;
            UpdateUI();
        }
    }
    public void UpdateUI (){

        IMapController mMapViewController = mMapView.getController();
        mMapViewController.setZoom(18);
        mMapViewController.setCenter(aRestaurant.getRestaurantGeoPoint());

//        restaurantImageView = (ImageView) findViewById(R.id.imageview_restaurant);
//        restaurantNameTextView = (TextView) findViewById(R.id.textview_restaurant_name);
//        restaurantAddressTextView = (TextView) findViewById(R.id.restaurant_address_textview);
//        restaurantRatingTextView = (TextView) findViewById(R.id.rating);
//        costTextView = (TextView) findViewById(R.id.cost_for_two_textview);

        Glide.with(this)
                .load(aRestaurant.getImageUrl())
                .into(restaurantImageView);
        restaurantNameTextView.setText(aRestaurant.getName());
        restaurantAddressTextView.setText(aRestaurant.getAddress());
        costTextView.setText("Kisaran Harga: " + aRestaurant.getCurrency() + aRestaurant.getCost());
        restaurantRatingTextView.setText(aRestaurant.getRating());
    }
}
