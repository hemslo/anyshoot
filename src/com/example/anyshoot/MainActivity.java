package com.example.anyshoot;

import android.support.v7.app.ActionBarActivity;
import android.annotation.SuppressLint;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.util.FloatMath;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        TextView textView = (TextView) findViewById(R.id.textView1);
        SensorManager mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if(mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE) != null)
        	textView.setText("GYROSCOPE Found");
    	else
        	textView.setText("GYROSCOPE Not Found");
        
        
    }
    
 // Create a constant to convert nanoseconds to seconds.
    private static final float NS2S = 1.0f / 1000000000.0f;
	private static final float EPSILON = 0.01f;
    private final float[] deltaRotationVector = new float[4];
    private float timestamp;

    @SuppressLint("NewApi") public void onSensorChanged(SensorEvent event) {
      // This timestep's delta rotation to be multiplied by the current rotation
      // after computing it from the gyro sample data.
      if (timestamp != 0) {
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
      }
      timestamp = event.timestamp;
      
      TextView textView1 = (TextView) findViewById(R.id.textView2);
      TextView textView2 = (TextView) findViewById(R.id.textView3);
      TextView textView3 = (TextView) findViewById(R.id.textView4);
      TextView textView4 = (TextView) findViewById(R.id.textView5);
      
      textView1.setText(Float.toString(deltaRotationVector[0]));
      textView2.setText(Float.toString(deltaRotationVector[1]));
      textView3.setText(Float.toString(deltaRotationVector[2]));
      textView4.setText(Float.toString(deltaRotationVector[3]));
      
      float[] deltaRotationMatrix = new float[9];
      SensorManager.getRotationMatrixFromVector(deltaRotationMatrix, deltaRotationVector);
        // User code should concatenate the delta rotation we computed with the current rotation
        // in order to get the updated rotation.
        // rotationCurrent = rotationCurrent * deltaRotationMatrix;
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
