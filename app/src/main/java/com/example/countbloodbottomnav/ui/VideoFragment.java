package com.example.countbloodbottomnav.ui;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.MediaController;
import android.widget.VideoView;
import androidx.fragment.app.Fragment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;

import com.example.countbloodbottomnav.R;

public class VideoFragment extends Fragment {

    private int totalSize, downloadedSize;

    @Override
    public View onCreateView(LayoutInflater li, ViewGroup vg, Bundle savedInstanceState) {
        View view =  li.inflate(R.layout.fragment_video, vg, false);

        //File video = downloadFile();

        VideoView vv = view.findViewById(R.id.view_video);
        MediaController mc = new MediaController(getContext());
        mc.setAnchorView(vv);
        vv.setMediaController(mc);
        //vv.setVideoPath(video.getPath());
        vv.setVideoURI(Uri.parse("https://cdn.filesend.jp/private/-NoAcE3lUrhhctJ1L1JaBOlQdOmrU6_EhzO097RIIEqHSbl_-P88at5y88d12c6L/Billie%20Eilish%20-%20bad%20guy.mp4"));
        vv.start();
        return view;
    }

    private File downloadFile(){

        try {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            URL url = new URL( "https://cdn.filesend.jp/private/-NoAcE3lUrhhctJ1L1JaBOlQdOmrU6_EhzO097RIIEqHSbl_-P88at5y88d12c6L/Billie%20Eilish%20-%20bad%20guy.mp4");
            HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setDoOutput(true);
            con.connect();

            //set the path where we want to save the file
            String SDCardRoot= Environment.getExternalStorageDirectory().getAbsolutePath() ;
            File file = new File(SDCardRoot,"CountBlood2.mp4");
            FileOutputStream fileOutput = new FileOutputStream(file);
            InputStream inputStream = con.getInputStream();

            totalSize = con.getContentLength();

            Log.d("TAG", "downloadFile: " + totalSize);
            byte[] buffer = new byte[1024];
            int bufferLength;
            while ( (bufferLength = inputStream.read(buffer)) > 0 ) {
                fileOutput.write(buffer, 0, bufferLength);

                downloadedSize += bufferLength;
                Log.d("", "downloadFile: " + downloadedSize);
            }
            fileOutput.close();
            return file;
        } catch (IOException e) { e.printStackTrace(); }
        return null;
    }
}

