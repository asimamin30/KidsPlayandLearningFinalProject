package com.fyp.autisticchildlearner;

        import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
        import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

        import androidx.annotation.NonNull;
        import androidx.appcompat.app.AlertDialog;
        import androidx.appcompat.app.AppCompatActivity;
        import androidx.core.app.ActivityCompat;
        import androidx.core.content.ContextCompat;
        import androidx.core.graphics.drawable.DrawableCompat;

        import android.annotation.TargetApi;
        import android.content.DialogInterface;
        import android.content.Intent;
        import android.content.SharedPreferences;
        import android.content.pm.PackageManager;
        import android.graphics.Bitmap;
        import android.graphics.Color;
        import android.graphics.Point;
        import android.graphics.drawable.Drawable;
        import android.net.Uri;
        import android.os.Build;
        import android.os.Bundle;
        import android.os.Environment;
        import android.util.Log;
        import android.view.Display;
        import android.view.Menu;
        import android.view.MenuItem;
        import android.view.View;
        import android.widget.Button;
        import android.widget.Toast;

        import com.google.android.gms.tasks.OnFailureListener;
        import com.google.android.gms.tasks.OnSuccessListener;
        import com.google.android.youtube.player.YouTubeInitializationResult;
        import com.google.android.youtube.player.YouTubePlayer;
        import com.google.android.youtube.player.YouTubePlayerView;
        import com.google.firebase.storage.FirebaseStorage;
        import com.google.firebase.storage.StorageReference;
        import com.google.firebase.storage.UploadTask;
        import com.pes.androidmaterialcolorpickerdialog.ColorPicker;

        import java.io.File;
        import java.io.FileOutputStream;
        import java.util.Objects;
        import java.util.Random;

        import me.panavtec.drawableview.DrawableView;
        import me.panavtec.drawableview.DrawableViewConfig;

public class DrawingActivity extends AppCompatActivity {
    String api_key = "AIzaSyBA75zUlRg3Z8MTBIUpt3QcYivtau_bsEQ";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawing);

        // Get reference to the view of Video player
        YouTubePlayerView ytPlayer = (YouTubePlayerView)findViewById(R.id.ytPlayer);

        ytPlayer.initialize(
                api_key,
                new YouTubePlayer.OnInitializedListener() {
                    // Implement two methods by clicking on red
                    // error bulb inside onInitializationSuccess
                    // method add the video link or the playlist
                    // link that you want to play In here we
                    // also handle the play and pause
                    // functionality
                    @Override
                    public void onInitializationSuccess(
                            YouTubePlayer.Provider provider,
                            YouTubePlayer youTubePlayer, boolean b)
                    {
//                        https://www.youtube.com/watch?v=OWnAaARrRyc
                        youTubePlayer.loadVideo("OWnAaARrRyc");
                        youTubePlayer.play();
                    }

                    @Override
                    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
                        Toast.makeText(getApplicationContext(), "Video player Failed", Toast.LENGTH_SHORT).show();

                    }

                    // Inside onInitializationFailure
                    // implement the failure functionality
                    // Here we will show toast

                });
    }
}

