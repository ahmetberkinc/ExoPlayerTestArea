package com.example.exoplayertestarea;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.dash.DashChunkSource;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.dash.DefaultDashChunkSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

public class MainActivity extends AppCompatActivity {

    PlayerView playerView;

    SimpleExoPlayer player;

    public static DefaultBandwidthMeter bandwidthMeter;

    DefaultTrackSelector trackSelector;

    Uri uri;

    String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        playerView = findViewById(R.id.player_view);

        url = "https://bitdash-a.akamaihd.net/content/MI201109210084_1/m3u8s/f08e80da-bf1d-4e3d-8899-f0f6155f6efa.m3u8";

        uri = Uri.parse(url);

        initializePlayer();


    }

    private void initializePlayer() {

        if (player == null) {

            @C.ContentType int type = Util.inferContentType(uri);

            switch (type) {

                case C.TYPE_HLS:


                    bandwidthMeter = new DefaultBandwidthMeter();
                    trackSelector = new DefaultTrackSelector(new AdaptiveTrackSelection.Factory(bandwidthMeter));
                    ((DefaultTrackSelector) trackSelector).setParameters(((DefaultTrackSelector) trackSelector).buildUponParameters());
                    player = ExoPlayerFactory.newSimpleInstance(this, trackSelector);


                case C.TYPE_OTHER:

                    break;
                case C.TYPE_SS:

                    break;
            }

            playerView.setPlayer(player);
            player.prepare(buildMediaSource(uri));
            player.setPlayWhenReady(true);
        }
    }

    public static MediaSource buildMediaSource(Uri uri) {
        @C.ContentType int type = Util.inferContentType(uri);
        DefaultHttpDataSourceFactory dataSourceFactory = new DefaultHttpDataSourceFactory("exoplayer_video");
        DashChunkSource.Factory dashChunkSourceFactory = new DefaultDashChunkSource.Factory(
                new DefaultHttpDataSourceFactory("exoplayer_video", new DefaultBandwidthMeter()));
        switch (type) {
            case C.TYPE_DASH:
                return new DashMediaSource.Factory(dashChunkSourceFactory, dataSourceFactory)
                        .createMediaSource(uri);
            case C.TYPE_HLS:
                return new HlsMediaSource.Factory(dataSourceFactory)
                        .createMediaSource(uri);

            default: {
                throw new IllegalStateException("Unsupported type: " + type);
            }
        }
    }

}