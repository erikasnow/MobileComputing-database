package wpi.essnow.project3;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.IccOpenLogicalChannelResponse;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static java.security.AccessController.getContext;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    // Gets the data repository in write mode
    LogContract.LogEntryDbHelper mHelper;
    SQLiteDatabase db;

    int REQUEST_IMAGE_CAPTURE = 1;              //part 2
    static final int REQUEST_TAKE_PHOTO = 1;    //part 2
    String mCurrentPhotoPath;                   //part 2
    private SensorManager mSensorManager;       //part 2
    private Sensor mSensor;                     //part 2
    private float temp;                         //part 2
    private String timeStamp;                   //part 2

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mHelper = new LogContract.LogEntryDbHelper(getApplicationContext());
        db = mHelper.getWritableDatabase();

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);  //part 2
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE); //part 2
    }

    public void updateDatabase(){                                           //changed from onButtonClick for part 1
        // Grab data from UI
        //TextView mText = findViewById(R.id.textIn);
        //String title = mText.getEditableText().toString();

        //Long longTime = System.currentTimeMillis()/1000;
        //String time = longTime.toString();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        //values.put(LogContract.LogEntry._ID, time);                       //from part 1
        values.put(LogContract.LogEntry._ID, timeStamp);                    //part 2
        //values.put(LogContract.LogEntry.COLUMN_NAME_ENTRY, title);        //from part 1
        values.put(LogContract.LogEntry.IMAGE_ENTRY, mCurrentPhotoPath);    //part 2
        values.put(LogContract.LogEntry.TEMP_ENTRY, Float.toString(temp));  //part 2

        // Clear the text entry
        //mText.setText("");

        // Insert the new row, returning the primary key value of the new row
        long newRowId = db.insert(LogContract.LogEntry.TABLE_NAME, null, values);

        // Insert text into output
        updateLogCount();
    }

    public void updateLogCount() {
        SQLiteDatabase db = mHelper.getReadableDatabase();

        Long totalEntries = DatabaseUtils.queryNumEntries(db, LogContract.LogEntry.TABLE_NAME);
        System.out.print("Total entries" + Long.toString(totalEntries));

        TextView mOut = findViewById(R.id.textOut);
        mOut.setText(totalEntries.toString());
    }

    /******PART 2******/
    private File createImageFile() throws IOException {
        Long longTime = System.currentTimeMillis()/1000;                            //moved from part 1
        timeStamp = longTime.toString();                                            //moved from part 1
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );

        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "wpi.essnow.project3.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    public void onButtonClick(View v) {
        dispatchTakePictureIntent();
    }

    private void setPic() {
        ImageView mImageView = findViewById(R.id.pictureDisplay);
        // Get the dimensions of the View
        int targetW = mImageView.getWidth();
        int targetH = mImageView.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        mImageView.setImageBitmap(bitmap);
    }

    public void temperature() {
        TextView mTempText = findViewById(R.id.tempText);
        mTempText.setText(Float.toString(temp) + " Celsius");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            temperature();
            setPic();

            //add to the database
            updateDatabase();
        }
    }

    @Override
    public final void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Don't need to do anything
    }

    @Override
    public final void onSensorChanged(SensorEvent event) {
        // Temp sensor returns a single value
        temp = event.values[0];
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }
}
