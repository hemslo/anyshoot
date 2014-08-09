package com.example.anyshoot;

import android.support.v7.app.ActionBarActivity;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.util.FloatMath;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphViewDataInterface;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphViewSeries.GraphViewSeriesStyle;
import com.jjoe64.graphview.LineGraphView;

class DummyPassValue implements GraphViewDataInterface {

	private double X, Y;
	public DummyPassValue(double x, double y) {
		X = x;
		Y = y;
	}
	
	@Override
	public double getX() {
		return X;
	}

	@Override
	public double getY() {
		return Y;
	}
	
}

public class MainActivity extends ActionBarActivity implements SensorEventListener{

	private Socket socket;

	private int SERVERPORT = 5000;
	private String SERVER_IP = "0.0.0.0";
	
	private SensorManager sensorManager;
	private GraphViewSeries GraphSeriesX;
	private GraphViewSeries GraphSeriesY;
	private GraphViewSeries GraphSeriesZ;
	
	private double accumX = 0;
	private double accumY = 0;
	private double accumZ = 0;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        TextView textView = (TextView) findViewById(R.id.textView1);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if(sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE) != null) {
        	textView.setText("GYROSCOPE Ready");
            sensorManager.registerListener(this,sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE),SensorManager.SENSOR_DELAY_GAME);
        } else {
        	textView.setText("GYROSCOPE Not Found");
        }
        
        // first init data
    	// sin curve
    	int num = 150;
    	GraphViewData[] data = new GraphViewData[num];
    	double v=0;
    	for (int i=0; i<num; i++) {
    	  v += 0.2;
    	  data[i] = new GraphViewData(i, Math.sin(v));
    	}
    	GraphSeriesX = new GraphViewSeries("X axis", null, data);
    	GraphSeriesY = new GraphViewSeries("Y axis", null, data);
    	GraphSeriesZ = new GraphViewSeries("Z axis", null, data);
    }
    
    public void onClickBtnConnect(View v) {
    	TextView textView = (TextView) findViewById(R.id.textView7);
    	try {
			InetAddress serverAddr = InetAddress.getByName(SERVER_IP);
			socket = new Socket(serverAddr, SERVERPORT);
			textView.setText("Connected");
		} catch (Exception e) {
			textView.setText("onClickBtnConnect Exception");
		}
    }
    
    public void onClickBtnPlot(View vi) {
    	GraphView graphView = new LineGraphView(
		    this // context
		    , "GraphViewDemo" // heading
		);
    	graphView = new LineGraphView(
    	    this
    	    , "Gyroscope"
    	);
    	// add data
    	graphView.addSeries(GraphSeriesX);
    	graphView.addSeries(GraphSeriesY);
    	graphView.addSeries(GraphSeriesZ);
    	// optional - set view port, start=2, size=10
    	graphView.setViewPort(2, 300);
    	graphView.setManualYAxisBounds(-1.0f, 1.0f);
    	graphView.setScalable(true);
    	// optional - legend
    	graphView.setShowLegend(true);
    	 
    	LinearLayout layout = (LinearLayout) findViewById(R.id.plotlayout);
    	layout.addView(graphView);
    }
    
 // Create a constant to convert nanoseconds to seconds.
    private static final float NS2S = 1.0f / 1000000000.0f;
	private static final float EPSILON = 0.1f;
    private final float[] deltaRotationVector = new float[4];
    private float timestamp;

    @Override
    public void onSensorChanged(SensorEvent event) {
      // This timestep's delta rotation to be multiplied by the current rotation
      // after computing it from the gyro sample data.
      final float dT = (event.timestamp - timestamp) * NS2S;
      
        // Axis of the rotation sample, not normalized yet.
        float axisX = event.values[0];
        float axisY = event.values[1];
        float axisZ = event.values[2];

        // Calculate the angular speed of the sample
        float omegaMagnitude = FloatMath.sqrt(axisX*axisX + axisY*axisY + axisZ*axisZ);

        // Normalize the rotation vector if it's big enough to get the axis
        // (that is, EPSILON should represent your maximum allowable margin of error)
        if (omegaMagnitude > EPSILON) {
          axisX /= omegaMagnitude;
          axisY /= omegaMagnitude;
          axisZ /= omegaMagnitude;
        }

        // Integrate around this axis with the angular speed by the timestep
        // in order to get a delta rotation from this sample over the timestep
        // We will convert this axis-angle representation of the delta rotation
        // into a quaternion before turning it into the rotation matrix.
        float thetaOverTwo = omegaMagnitude * dT / 2.0f;
        float sinThetaOverTwo = FloatMath.sin(thetaOverTwo);
        float cosThetaOverTwo = FloatMath.cos(thetaOverTwo);
        deltaRotationVector[0] = sinThetaOverTwo * axisX;
        deltaRotationVector[1] = sinThetaOverTwo * axisY;
        deltaRotationVector[2] = sinThetaOverTwo * axisZ;
        deltaRotationVector[3] = cosThetaOverTwo;
//        deltaRotationVector[0] = dT * axisX;
//        deltaRotationVector[1] = dT * axisY;
//      	deltaRotationVector[2] = dT * axisZ;
      
      TextView textView1 = (TextView) findViewById(R.id.textView2);
      TextView textView2 = (TextView) findViewById(R.id.textView3);
      TextView textView3 = (TextView) findViewById(R.id.textView4);
      TextView textView4 = (TextView) findViewById(R.id.textView5);
      TextView textView6 = (TextView) findViewById(R.id.textView6);
      
      float omegaRotation = FloatMath.sqrt(deltaRotationVector[0]*deltaRotationVector[0]
    		  + deltaRotationVector[1]*deltaRotationVector[1] 
    		  + deltaRotationVector[2]*deltaRotationVector[2]);

      textView1.setText(Float.toString(deltaRotationVector[0]));
      textView2.setText(Float.toString(deltaRotationVector[1]));
      textView3.setText(Float.toString(deltaRotationVector[2]));
      textView6.setText(Float.toString(dT));
      textView4.setText(Float.toString(timestamp));
      
      accumX += deltaRotationVector[0];
      accumY += deltaRotationVector[1];
      accumZ += deltaRotationVector[2];
      
      if (dT > 0) {
//	      DummyPassValue dummy1 = new DummyPassValue(event.timestamp/100000000, deltaRotationVector[0]);
//	      DummyPassValue dummy2 = new DummyPassValue(event.timestamp/100000000, deltaRotationVector[1]);
//	      DummyPassValue dummy3 = new DummyPassValue(event.timestamp/100000000, deltaRotationVector[2]);
//    	  DummyPassValue dummy1 = new DummyPassValue(event.timestamp/100000000, accumX/Math.PI*180);
//	      DummyPassValue dummy2 = new DummyPassValue(event.timestamp/100000000, accumY/Math.PI*180);
//	      DummyPassValue dummy3 = new DummyPassValue(event.timestamp/100000000, accumZ/Math.PI*180);
    	  DummyPassValue dummy1 = new DummyPassValue(event.timestamp/100000000, accumX);
	      DummyPassValue dummy2 = new DummyPassValue(event.timestamp/100000000, accumY);
	      DummyPassValue dummy3 = new DummyPassValue(event.timestamp/100000000, accumZ);
	      GraphSeriesX.appendData(dummy1, true, 15000);
	      GraphSeriesY.appendData(dummy2, true, 15000);
	      GraphSeriesZ.appendData(dummy3, true, 15000);
      }
      
//      textView1.setText(Float.toString(axisX));
//      textView2.setText(Float.toString(axisY));
//      textView3.setText(Float.toString(axisZ));
//      float[] deltaRotationMatrix = new float[9];
      
      // SensorManager.getRotationMatrixFromVector(deltaRotationMatrix, deltaRotationVector);
        // User code should concatenate the delta rotation we computed with the current rotation
        // in order to get the updated rotation.
        // rotationCurrent = rotationCurrent * deltaRotationMatrix;
      timestamp = event.timestamp;
   }
    
    @Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) { 
	}


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
