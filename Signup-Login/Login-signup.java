
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.google.example.games.basegameutils.BaseGameActivity;


public class HomeActivity extends BaseGameActivity implements GameHomeFragment.Listener, GameScoreFragment.Listener,
        GameModeChooserFragment.Listener, LeaderboardChooserFragment.Listener, BonusFragment.Listener, GameModeFragment.Listener {
    //Key
    public static final String KEY_HAS_TUTO_BEEN_SEEN = "HomeActivity.Key.HasTutoBeenSeen";
    //Request code
    private static final int REQUEST_ACHIEVEMENT = 0x00000000;
    private static final int REQUEST_LEADERBOARD = 0x00000001;
    private static final int REQUEST_GAME_ACTIVITY_FRESH_START = 0x00000002;
    private static final int REQUEST_GAME_ACTIVITY_REPLAY = 0x00000003;
    //Achievement
    private static final int ACHIEVEMENT_NOVICE_LOOTER_LIMIT = 20;
    private static final int ACHIEVEMENT_TRAINED_LOOTER_LIMIT = 65;
    private static final int ACHIEVEMENT_EXPERT_LOOTER_LIMIT = 90;

    private Toast mTextToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_game_home);


        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.game_home_fragment_container,
                    new GameHomeFragment(), GameHomeFragment.FRAGMENT_TAG).commit();
        }
    }

    @Override
    protected void onActivityResult(int request, int response, Intent data) {
        super.onActivityResult(request, response, data);

        if (REQUEST_GAME_ACTIVITY_REPLAY == request) {
            getSupportFragmentManager().popBackStackImmediate();
        }

        if (RESULT_OK == response) {
            if (REQUEST_GAME_ACTIVITY_REPLAY == request || REQUEST_GAME_ACTIVITY_FRESH_START == request) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.game_home_fragment_container,
                                GameScoreFragment.newInstance(
                                        (GameInformation) data.getParcelableExtra(GameScoreFragment.EXTRA_GAME_INFORMATION)),
                                GameScoreFragment.FRAGMENT_TAG
                        )
                        .addToBackStack(null).commitAllowingStateLoss();
            }
        }

        if (ARActivity.RESULT_SENSOR_NOT_SUPPORTED == response) {
            makeToast(getString(R.string.home_device_not_compatible) + " (rotation sensor)");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        hideToast();
    }

    @Override
    public void onSignInFailed() {
        final Fragment gameScoreFragment = getSupportFragmentManager()
                .findFragmentByTag(GameScoreFragment.FRAGMENT_TAG);
        if (gameScoreFragment != null) {
            ((GameScoreFragment) gameScoreFragment).notifySignedStateChanged(false);
        }
    }

    @Override
    public void onSignInSucceeded() {
        final Fragment gameHomeFragment = getSupportFragmentManager()
                .findFragmentByTag(GameHomeFragment.FRAGMENT_TAG);


        if (gameHomeFragment != null) {
            ((GameHomeFragment) gameHomeFragment).notifySignedStateChanged(true);
        }
        final Fragment gameScoreFragment = getSupportFragmentManager()
                .findFragmentByTag(GameScoreFragment.FRAGMENT_TAG);
        if (gameScoreFragment != null) {
            ((GameScoreFragment) gameScoreFragment).notifySignedStateChanged(true);
        }
    }

    @Override
    protected void signOut() {
        super.signOut();
        makeToast(getString(R.string.home_sign_out_success));
        final Fragment gameHomeFragment = getSupportFragmentManager()
                .findFragmentByTag(GameHomeFragment.FRAGMENT_TAG);
        if (gameHomeFragment != null) {
            ((GameHomeFragment) gameHomeFragment).notifySignedStateChanged(false);
        }
        final Fragment gameScoreFragment = getSupportFragmentManager()
                .findFragmentByTag(GameScoreFragment.FRAGMENT_TAG);
        if (gameScoreFragment != null) {
            ((GameScoreFragment) gameScoreFragment).notifySignedStateChanged(false);
        }
    }

    @Override
    public void onStartGameRequested() {
        getSupportFragmentManager().beginTransaction().replace(R.id.game_home_fragment_container,
                new GameModeChooserFragment()).addToBackStack(null).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit();
    }

    @Override
    public void onShowAchievementsRequested() {
        final GoogleApiClient gameClient = getApiClient();
        if (gameClient.isConnected()) {
            startActivityForResult(Games.Achievements.getAchievementsIntent(gameClient), REQUEST_ACHIEVEMENT);
        } else {
            makeToast(getResources().getString(R.string.home_not_sign_in_achievement));
        }
    }

    
    @Override
    public void onShowAboutRequested() {
        getSupportFragmentManager().beginTransaction().replace(R.id.game_home_fragment_container,
                new AboutFragment()).addToBackStack(null).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit();
    }

    @Override
    public void onSignInButtonClicked() {
        if (isNetworkAvailable()) {
            beginUserInitiatedSignIn();
        } else {
            makeToast(getResources().getString(R.string.home_internet_unavailable));
        }
    }

    @Override
    public void onSignOutButtonClicked() {
        SignOutConfirmDialogFragment.newInstance().show(getSupportFragmentManager(), "dialog");
    }


   

    @Override
    public void toast(String message) {
        makeToast(message);
    }

   
    private void makeToast(String message) {
        if (mTextToast != null) {
            mTextToast.cancel();
        }
        mTextToast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        mTextToast.show();
    }

    private void hideToast() {
        if (mTextToast != null) {
            mTextToast.cancel();
            mTextToast = null;
        }
    }

    

    

   

    

   

   
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public void onGameStartRequest(GameMode gameMode) {
        startNewGame(gameMode, REQUEST_GAME_ACTIVITY_FRESH_START);
    }

  


    public static class SignOutConfirmDialogFragment extends DialogFragment {
        public SignOutConfirmDialogFragment() {
        }

        public static SignOutConfirmDialogFragment newInstance() {
            return new SignOutConfirmDialogFragment();
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return new AlertDialog.Builder(getActivity())
                    .setTitle(R.string.app_name)
                    .setMessage(R.string.home_sign_out_confirm_dialog_message)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int which) {
                            ((HomeActivity) getActivity()).signOut();
                        }
                    })
                    .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int which) {
                            dialogInterface.dismiss();
                        }
                    })
                    .create();
        }
    }

}
