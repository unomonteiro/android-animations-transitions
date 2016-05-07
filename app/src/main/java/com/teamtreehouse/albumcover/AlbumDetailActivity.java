package com.teamtreehouse.albumcover;

import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.graphics.Palette;
import android.transition.ChangeBounds;
import android.transition.Fade;
import android.transition.Scene;
import android.transition.Slide;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.transition.TransitionSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.teamtreehouse.albumcover.transitions.Fold;
import com.teamtreehouse.albumcover.transitions.Scale;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class AlbumDetailActivity extends Activity {

    public static final String EXTRA_ALBUM_ART_RESID = "EXTRA_ALBUM_ART_RESID";

    @Bind(R.id.album_art) ImageView albumArtView;
    @Bind(R.id.fab) ImageButton fab;
    @Bind(R.id.title_panel) ViewGroup titlePanel;
    @Bind(R.id.track_panel) ViewGroup trackPanel;
    @Bind(R.id.detail_container) ViewGroup detailContainer;

    private TransitionManager mTransitionManager;
    private Scene mExpandedScene;
    private Scene mCollapsedScene;
    private Scene mCurrentScene;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_detail);
        ButterKnife.bind(this);
        populate();
        setupTransitions();
    }

    //    private void animate() {
//        /*fab.setScaleX(0);
//        fab.setScaleY(0);
//        fab.animate().scaleX(1).scaleY(1).start();*/
//
////        ObjectAnimator scalex = ObjectAnimator.ofFloat(fab, "scaleX", 0, 1);
////        ObjectAnimator scaley = ObjectAnimator.ofFloat(fab, "scaleY", 0, 1);
////        AnimatorSet scaleFab = new AnimatorSet();
////        scaleFab.playTogether(scalex,scaley);
//
//        Animator scaleFab = AnimatorInflater.loadAnimator(this, R.animator.scale);
//        scaleFab.setTarget(fab);
//
//        int titleStartValue = titlePanel.getTop();
//        int titleEndValue = titlePanel.getBottom();
//        // ObjectAnimator.ofInt(titlePanel, "bottom", titleStartValue, titleEndValue).start();
//        ObjectAnimator animatorTitle = ObjectAnimator.ofInt(titlePanel, "bottom", titleStartValue, titleEndValue);
//        animatorTitle.setInterpolator(new AccelerateInterpolator());
//
//        int trackStartValue = trackPanel.getTop();
//        int trackEndValue = trackPanel.getBottom();
//        // ObjectAnimator.ofInt(trackPanel, "bottom", trackStartValue, trackEndValue).start();
//        ObjectAnimator animatorTrack = ObjectAnimator.ofInt(trackPanel, "bottom", trackStartValue, trackEndValue);
//        animatorTrack.setInterpolator(new DecelerateInterpolator());
//
//        // set initial position to avoid flickering
//        titlePanel.setBottom(titleStartValue);
//        trackPanel.setBottom(titleStartValue);
//        fab.setScaleX(0);
//        fab.setScaleY(0);
//
//        // setting duration of animations
////        animatorTitle.setDuration(1000);
////        animatorTrack.setDuration(1000);
////        animatorTitle.setStartDelay(1000);
//
//        AnimatorSet set = new AnimatorSet();
//        set.playSequentially(animatorTitle, animatorTrack, scaleFab);
//        set.start();
//
//    }


    private Transition createTransition() {
        TransitionSet set = new TransitionSet();
        set.setOrdering(TransitionSet.ORDERING_SEQUENTIAL);

        Transition tFab = new Scale();
        tFab.setDuration(150);
        tFab.addTarget(fab);

        Transition tTitle = new Fold();
        tTitle.setDuration(150);
        tTitle.addTarget(titlePanel);

        Transition tTrack = new Fold();
        tTrack.setDuration(150);
        tTrack.addTarget(trackPanel);

        set.addTransition(tTrack);
        set.addTransition(tTitle);
        set.addTransition(tFab);

        return set;
    }

    @SuppressWarnings("unused")
    @OnClick(R.id.album_art)
    public void onAlbumArtClick(View view) {
        // createTransition();
        Transition transition = createTransition();
        TransitionManager.beginDelayedTransition(detailContainer, transition);
        fab.setVisibility(View.INVISIBLE);
        titlePanel.setVisibility(View.INVISIBLE);
        trackPanel.setVisibility(View.INVISIBLE);
    }

    @SuppressWarnings("unused")
    @OnClick(R.id.track_panel)
    public void onTrackPanelClicked(View view) {
        //setupTransitions();

        if (mCurrentScene == mExpandedScene) {
            mCurrentScene = mCollapsedScene;
        } else {
            mCurrentScene = mExpandedScene;
        }
        mTransitionManager.transitionTo(mCurrentScene);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void setupTransitions() {
//        getWindow().setEnterTransition(new Slide(Gravity.RIGHT));
//        getWindow().setReturnTransition(new Fade());

        Slide slide = new Slide(Gravity.BOTTOM);
        slide.excludeTarget(android.R.id.statusBarBackground, true);
        getWindow().setEnterTransition(slide);
        // avoid overlay above fab button
        getWindow().setSharedElementsUseOverlay(false);

        mTransitionManager = new TransitionManager();
        ViewGroup transitionRoot = detailContainer;

        // Expanded scene
        mExpandedScene = Scene.getSceneForLayout(transitionRoot,
                R.layout.activity_album_detail_expanded, this);

        mExpandedScene.setEnterAction(new Runnable() {
            @Override
            public void run() {
                ButterKnife.bind(AlbumDetailActivity.this);
                populate();
                mCurrentScene = mExpandedScene;
            }
        });

        TransitionSet expandTransitionSet = new TransitionSet();
        expandTransitionSet.setOrdering(TransitionSet.ORDERING_SEQUENTIAL);
        ChangeBounds changeBounds = new ChangeBounds();
        changeBounds.setDuration(200);
        expandTransitionSet.addTransition(changeBounds);

        Fade fadeLyrics = new Fade();
        fadeLyrics.addTarget(R.id.lyrics);
        fadeLyrics.setDuration(150);
        expandTransitionSet.addTransition(fadeLyrics);

        // Collapsed scene
        mCollapsedScene = Scene.getSceneForLayout(transitionRoot,
                R.layout.activity_album_detail, this);

        mCollapsedScene.setEnterAction(new Runnable() {
            @Override
            public void run() {
                ButterKnife.bind(AlbumDetailActivity.this);
                populate();
                mCurrentScene = mCollapsedScene;
            }
        });

        TransitionSet collapseTransitionSet = new TransitionSet();
        collapseTransitionSet.setOrdering(TransitionSet.ORDERING_SEQUENTIAL);

        Fade fadeOutLyrics = new Fade();
        fadeOutLyrics.addTarget(R.id.lyrics);
        fadeOutLyrics.setDuration(150);
        collapseTransitionSet.addTransition(fadeOutLyrics);

        ChangeBounds resetBounds = new ChangeBounds();
        resetBounds.setDuration(200);
        collapseTransitionSet.addTransition(resetBounds);

        mTransitionManager.setTransition(mExpandedScene, mCollapsedScene, collapseTransitionSet);
        mTransitionManager.setTransition(mCollapsedScene, mExpandedScene, expandTransitionSet);
        mCollapsedScene.enter();

        // postponeEnterTransition();
    }

    private void populate() {
        /*new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                int albumArtResId = getIntent().getIntExtra(EXTRA_ALBUM_ART_RESID, R.drawable.mean_something_kinder_than_wolves);
                albumArtView.setImageResource(albumArtResId);

                Bitmap albumBitmap = getReducedBitmap(albumArtResId);
                colorizeFromImage(albumBitmap);

//                startPostponedEnterTransition();
            }
        }, 1000);*/

        int albumArtResId = getIntent().getIntExtra(EXTRA_ALBUM_ART_RESID, R.drawable.mean_something_kinder_than_wolves);
        albumArtView.setImageResource(albumArtResId);

        Bitmap albumBitmap = getReducedBitmap(albumArtResId);
        colorizeFromImage(albumBitmap);


    }

    private Bitmap getReducedBitmap(int albumArtResId) {
        // reduce image size in memory to avoid memory errors
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = false;
        options.inSampleSize = 8;
        return BitmapFactory.decodeResource(getResources(), albumArtResId, options);
    }

    private void colorizeFromImage(Bitmap image) {
        Palette palette = Palette.from(image).generate();

        // set panel colors
        int defaultPanelColor = 0xFF808080;
        int defaultFabColor = 0xFFEEEEEE;
        titlePanel.setBackgroundColor(palette.getDarkVibrantColor(defaultPanelColor));
        trackPanel.setBackgroundColor(palette.getLightMutedColor(defaultPanelColor));

        // set fab colors
        int[][] states = new int[][]{
                new int[]{android.R.attr.state_enabled},
                new int[]{android.R.attr.state_pressed}
        };

        int[] colors = new int[]{
                palette.getVibrantColor(defaultFabColor),
                palette.getLightVibrantColor(defaultFabColor)
        };
//        fab.setBackgroundTintList(new ColorStateList(states, colors));
    }
}
