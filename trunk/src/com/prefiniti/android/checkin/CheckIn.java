package com.prefiniti.android.checkin;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;
import java.util.ArrayList;


public class CheckIn extends Activity {
    
	private ArrayList<Location> collectedPositions;
	private Integer epochCount;
	private Integer minAccuracy;
	private String deviceCode;
	private String serverURI;
	private PrefinitiMobileDevice device;
    private Integer epochsCollected;
    private ProgressBar pbrCollect; //= (ProgressBar) findViewById(R.id.pbrCollect);
    private TextView txtAveraging; //= (TextView) findViewById(R.id.txtAveraging);
    private TextView txtGPSStatus;
    private EditText txtComment;
    private ArrayList<Double> lats;
    private ArrayList<Double> lons;
    private ArrayList<Double> accs;
    private ArrayList<Double> elevs;
    private Double latSum;
    private Double lonSum;
    private Double accSum;
    private Double elevSum;
    private Double latAvg;
    private Double lonAvg;
    private Double accAvg;
    private Double elevAvg;
    private Integer goodFixes;
    private Location finalLocation;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);    
        setContentView(R.layout.main);
        
        pbrCollect = (ProgressBar) findViewById(R.id.pbrCollect);
        txtAveraging = (TextView) findViewById(R.id.txtAveraging);
        txtGPSStatus = (TextView) findViewById(R.id.txtGPSStatus);
        txtComment = (EditText) findViewById(R.id.txtComment);
        
        txtGPSStatus.setText("");
        txtAveraging.setText("");
        pbrCollect.setVisibility(pbrCollect.INVISIBLE);
        
        final Button updateButton = (Button) findViewById(R.id.cmdUpdateLocation);
        
        
        final LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
    	final LocationListener locationListener = new LocationListener() {
    	    public void onLocationChanged(Location location) {    	    	
        		
    	    	Bundle fixData;
    	    	fixData = new Bundle();
    	    	fixData = location.getExtras();
    	    	
    	    	float bearing;
    	    	float speed;
    	    	
    	    	bearing = 0;
    	    	speed = 0;
    	    	
    	    	String prov;
    	    	
    	    	prov = location.getProvider();
    	    	
    	    	if(prov.equals(new String("gps"))) {
    	    		txtGPSStatus.setText("GPS (Accuracy: " + location.getAccuracy() + "m)");
    	    	}
    	    	else if (prov.equals(new String("network"))) {
    	    		txtGPSStatus.setText("Network (Accuracy: " + location.getAccuracy() + "m)");    	    		    	    	
    	    	}
    	    	else {
    	    		txtGPSStatus.setText("Unknown");
    	    	}
    	    	
    	    	txtAveraging.setText("Averaging position (" + epochsCollected + "/" + epochCount + " epochs)");
    	    	pbrCollect.setProgress(epochsCollected);
    	    	
    	    	if(location.getAccuracy() <= minAccuracy) {
    	    		collectedPositions.add(location);
    	    		goodFixes++;
    	    	}
    	    	
    	    	if (epochsCollected == epochCount) {
    	    		
    	    		for (Location l : collectedPositions) {
    	    			lats.add(l.getLatitude());
    	    			lons.add(l.getLongitude());
    	    			elevs.add(l.getAltitude());
    	    			bearing = l.getBearing();
    	    			speed = l.getSpeed();
    	    			accs.add(Double.valueOf(String.valueOf(l.getAccuracy())));
    	    		}
    	    		
    	    		for (Double lat : lats) {
    	    			latSum += lat;
    	    			txtGPSStatus.setText("Adding latitude: " + lat);
    	    		}
    	    		
    	    		for (Double lon : lons) {
    	    			lonSum += lon;
    	    			txtGPSStatus.setText("Adding longitude: " + lon);
    	    		}
    	    		
    	    		for (Double acc : accs) {   			    	    		
    	    			accSum += acc;
    	    			txtGPSStatus.setText("Adding accuracy: " + acc);
    	    		}
    	    		
    	    		for (Double elev : elevs) {
    	    			elevSum += elev;
    	    			txtGPSStatus.setText("Adding elevation: " + elev);
    	    		}
    	    		
    	    		latAvg = latSum / goodFixes;
    	    		lonAvg = lonSum / goodFixes;
    	    		accAvg = accSum / goodFixes;
    	    		elevAvg = elevSum / goodFixes;
    	    		
    	    		finalLocation = new Location("gps");
    	    		
    	    		finalLocation.setLatitude(latAvg);
    	    		finalLocation.setLongitude(lonAvg);
    	    		finalLocation.setAccuracy(accAvg.floatValue());
    	    		finalLocation.setAltitude(elevAvg);
    	    		finalLocation.setBearing(bearing);
    	    		finalLocation.setSpeed(speed);
    	    		
	        	    device = new PrefinitiMobileDevice(deviceCode, serverURI);
	    	    	try {	    	    		
						device.SetLocation(finalLocation, txtComment.getText().toString());
						Toast.makeText(getBaseContext(), "Prefiniti device location updated.", Toast.LENGTH_LONG).show();
					} catch (Exception e) {					
						txtGPSStatus.setText(e.toString());
					}
					
	    	    	locationManager.removeUpdates(this);
	    	    	pbrCollect.setVisibility(pbrCollect.INVISIBLE);
	    	    	txtAveraging.setText("");
	    	    	txtGPSStatus.setText("");
    	    	}
    	    	epochsCollected++;
    	    	
    	    	
    	    }

    	    public void onStatusChanged(String provider, int status, Bundle extras) {
    	    	//txtGPSStatus.setText("Provider: " + provider + " SV: " + extras.get("satellites"));
    	    	
    	    }

    	    public void onProviderEnabled(String provider) {}

    	    public void onProviderDisabled(String provider) {
    	    	Toast.makeText(getBaseContext(), provider.toUpperCase() + " is unavailable.", Toast.LENGTH_LONG).show();    	    	
    	    }

    		
    	  };              
                
        updateButton.setOnClickListener(new Button.OnClickListener() {
        	public void onClick(View view) {
        		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getBaseContext());        	        
        		epochsCollected = 0;        		        	
        		goodFixes = 0;                
        		
        		collectedPositions = new ArrayList<Location>();
        		lats = new ArrayList<Double>();
        		lons = new ArrayList<Double>();
        		accs = new ArrayList<Double>();
        		elevs = new ArrayList<Double>();
        		latSum = 0D;
        		lonSum = 0D;
        		accSum = 0D;
        		elevSum = 0D;
        		latAvg = 0D;
        		lonAvg = 0D;
        		accAvg = 0D;
        		elevAvg = 0D;
        		
        		
        		
        	    deviceCode = settings.getString("deviceCode", "");
        	    serverURI = settings.getString("serverURI", "");
        		epochCount = Integer.valueOf(settings.getString("epochCount", "1")); 
        		minAccuracy = Integer.valueOf(settings.getString("minAccuracy", "40"));
        		
        		txtAveraging.setText("Averaging position (0/" + epochCount + " epochs)");
        		pbrCollect.setMax(epochCount);
        		pbrCollect.setVisibility(pbrCollect.VISIBLE);
        		pbrCollect.setProgress(0);
                txtGPSStatus.setText("No Fix");
                
                
        	    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        	    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);        
        	}
        });
        
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	MenuInflater inflater = getMenuInflater();
    	inflater.inflate(R.menu.mainmenu, menu);
    	return true;
    }
    
    @Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.settings:
			Intent i = new Intent(CheckIn.this, AppSettings.class);
			startActivity(i);
			break;
		}
		return true;
	}
    
}

