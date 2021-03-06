package com.pressy4pie.oudhs.manager;

/**
 * Created by pressy4pie on 8/9/2014.
 */

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import com.pressy4pie.oudhs.manager.root_tools;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class SplashScreen extends Activity {
    String device = root_tools.readProp("ro.product.board");
    String md5File = Environment.getExternalStorageDirectory() + "/md5.txt";
    String deviceFile = Environment.getExternalStorageDirectory() + "/devices.txt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Log.d("Device Name", "Device is: " + device);

        //start the fun stuff here.
        new PrefetchData().execute();
    }

    //check to see if the devices ro.product.board is in devices
    public boolean is_in_devices(){
        boolean is = false;
        Scanner input = new Scanner (deviceFile);
        while(input.hasNextLine()){
            String lineFromFile = input.nextLine();
            if(lineFromFile.contains(device)) {
                is = true;
            }
        }
        if (is = true){
            return true;
        }
        else return false;
    }

        private class PrefetchData extends AsyncTask<Void, Void, Void> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                //check to see if the devices and md5 text file exist
                //if they do delete them
            }

            @Override
            protected Void doInBackground(Void... arg0) {
                //download a list of devices

                downloadFiles("http://pressy4pie.com.com/devices/devices.txt", "devices.txt");
                //download the md5 files for that device
                Log.d("Device Check", "Checking Device: " + device);
                if(is_in_devices()) {
                    Log.d("Device Check", device + " found in devices.txt, getting files...");
                    downloadFiles("http://pressy4pie.com/devices/" + device + "/md5.txt", "md5.txt");
                }
                else {
                    Log.d("Device Check", device + " not found in devices.txt");

                }

                String recoveryUpdate_md5 = root_tools.readLine(md5File, 1);
//                Log.d("recoveryUpdate_md5: ", recoveryUpdate_md5);

                //String recoveryUpdate_md5 = "bb25826589694a45e13b75561b2d5647";
                //String saferootUpdate_md5 = "6a6b725846577e9955db8608c957216b";

                /*
                if(root_tools.fileExists(recovery)) {
                    Log.i(tagname, "recovery.zip was found.");
                    String checkrecovery = root_tools.checkSum(recovery);
                    Log.d("checksum", "recovery checksum: " + "'" + checkrecovery + "'");
                    Log.d("checksum", "checking against: " + "'" + recoveryUpdate_md5 + "'");

                    //check for updates
                    if (!checkrecovery.equals(recoveryUpdate_md5)) {
                        Log.i(tagname, "an update was found. Downloading new recovery.zip");
                        root_tools.execute("busybox rm " + recovery);
                        download("recovery");
                    }
                }

                //download recovery.zip if it doesnt exist
                else {
                    download("recovery");
                }

                if(root_tools.fileExists(saferoot)) {
                    Log.i(tagname, "saferoot.zip was found.");
                    String checksaferoot = root_tools.checkSum(saferoot);
                    Log.d("checksum", "saferoot checksum: " + checksaferoot);
                    Log.d("checksum", "checking against: " + saferootUpdate_md5 );

                    //check for updates
                    if (!checksaferoot.equals(saferootUpdate_md5)) {
                        Log.i(tagname, "An update was found. Downloading new saferoot.zip");
                        root_tools.execute("busybox rm " + saferoot);
                        download("saferoot");
                    }
                }
                //downlaod saferoot.zip if it doesnt exist
                else {
                    download("saferoot");
                }


                if(root_tools.fileExists(SDhacks)) {
                    Log.i(tagname, "SDhacks.zip was found.");
                    //no updates for this. idgaf about it anymore
                }

                //downlaod SDhacks.zip if it doesnt exist
                else {
                    download("SDhacks");
                }
*/


                return null;
            }

            public void downloadFiles(String file2get, String filename){
                try {
                    //set the download URL, a url that points to a file on the internet
                    //this is the file to be downloaded
                    URL url = new URL(file2get);

                    //create the new connection
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                    //set up some things on the connection
                    urlConnection.setRequestMethod("GET");
                    urlConnection.setDoOutput(true);

                    //and connect!
                    urlConnection.connect();

                    //set the path where we want to save the file
                    //in this case, going to save it on the root directory of the
                    //sd card.
                    File SDCardDir = Environment.getExternalStorageDirectory();
                    //create a new file, specifying the path, and the filename
                    //which we want to save the file as.
                    File file = new File(SDCardDir + "/" + filename);

                    //this will be used to write the downloaded data into the file we created
                    FileOutputStream fileOutput = new FileOutputStream(file);

                    //this will be used in reading the data from the internet
                    InputStream inputStream = urlConnection.getInputStream();

                    //this is the total size of the file
                    int totalSize = urlConnection.getContentLength();
                    //variable to store total downloaded bytes
                    int downloadedSize = 0;

                    //create a buffer...
                    byte[] buffer = new byte[1024];
                    int bufferLength = 0; //used to store a temporary size of the buffer

                    //now, read through the input buffer and write the contents to the file
                    Log.i("File Download", "Downloading " + filename);
                    while ( (bufferLength = inputStream.read(buffer)) > 0 ) {
                        //add the data in the buffer to the file in the file output stream (the file on the sd card
                        fileOutput.write(buffer, 0, bufferLength);
                        //add up the size so we know how much is downloaded
                        downloadedSize += bufferLength;
                        //updateProgress(downloadedSize, totalSize, filename);
                    }
                    Log.i("File Download", "Download of " + filename + " has completed");
                    //close the output stream when done
                    fileOutput.close();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected void onPostExecute(Void result) {
                super.onPostExecute(result);
                if(is_in_devices()) {
                    Intent i = new Intent(SplashScreen.this, Main.class);
                    startActivity(i);
                }

                else {
                    Intent i = new Intent(SplashScreen.this, NoDevice.class);
                    startActivity(i);
                }
                // close this activity
                finish();
            }

        }
    }



