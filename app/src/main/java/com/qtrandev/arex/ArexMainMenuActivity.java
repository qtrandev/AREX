package com.qtrandev.arex;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookGraphResponseException;
import com.facebook.login.DefaultAudience;
import com.facebook.login.LoginManager;
import com.facebook.messenger.MessengerUtils;
import com.facebook.messenger.ShareToMessengerParams;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareButton;
import com.facebook.share.widget.ShareDialog;

import java.util.Arrays;


public class ArexMainMenuActivity extends ActionBarActivity {
    private ShareButton shareButton;
    private Button mRegularButton;
    private CallbackManager callbackManager;
    private AccessTokenTracker accessTokenTracker;
    private ShareDialog shareDialog;

    private static final int REQUEST_CODE_SHARE_TO_MESSENGER = 1;

    private FacebookCallback<Sharer.Result> shareCallback =
            new FacebookCallback<Sharer.Result>() {
                @Override
                public void onCancel() {
                    Toast.makeText(ArexMainMenuActivity.this, "Cancelled", Toast.LENGTH_SHORT).show();

                }

                @Override
                public void onError(FacebookException error) {
                    if (error instanceof FacebookGraphResponseException) {
                        FacebookGraphResponseException graphError =
                                (FacebookGraphResponseException) error;
                        if (graphError.getGraphResponse() != null) {
                            Toast.makeText(ArexMainMenuActivity.this, "Graph Error", Toast.LENGTH_SHORT).show();

                            return;
                        }
                    }
                    Toast.makeText(ArexMainMenuActivity.this, "Unknown Error", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onSuccess(Sharer.Result result) {
                    Toast.makeText(ArexMainMenuActivity.this, "Worked", Toast.LENGTH_SHORT).show();

                }
            };

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arex_main_menu);

        callbackManager = CallbackManager.Factory.create();

        //mRegularButton = (Button) findViewById(R.id.regularButton);
        shareButton = (ShareButton)findViewById(R.id.share_button);
        shareDialog = new ShareDialog(this);
        // this part is optional
        shareDialog.registerCallback(callbackManager, shareCallback);

          accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken,
                                                       AccessToken currentAccessToken) {
                updateWithToken(currentAccessToken);
            }
        };
    }

    public void messengerClick(View view) {
        Uri uri =Uri.parse("android.resource://com.qtrandev.arex/" + R.drawable.tree);
        // contentUri points to the content being shared to Messenger
        ShareToMessengerParams shareToMessengerParams =
                ShareToMessengerParams.newBuilder(uri, "image/jpeg")
                        .setMetaData("{ \"image\" : \"tree\" }")
                        .build();

        // Sharing from an Activity
        MessengerUtils.shareToMessenger(
                this,
                REQUEST_CODE_SHARE_TO_MESSENGER,
                shareToMessengerParams);
    }


    private void updateWithToken(AccessToken currentAccessToken) {
        if (currentAccessToken != null) {
            Toast.makeText(ArexMainMenuActivity.this, "new token", Toast.LENGTH_SHORT).show();

        } else {
            Toast.makeText(ArexMainMenuActivity.this, "error", Toast.LENGTH_SHORT).show();

        }
    }

    public void shareClick(View view){

        if (ShareDialog.canShow(ShareLinkContent.class)) {
            ShareLinkContent linkContent = new ShareLinkContent.Builder()
                    .setContentTitle("Hello Facebook")
                    .setContentDescription(
                            "[Description]")
                    .setContentUrl(Uri.parse("http://developers.facebook.com/android"))
                    .build();

            shareDialog.show(linkContent);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_arex_main_menu, menu);
        return true;
    }

    private void requestPublishPermissions() {
        LoginManager.getInstance()
                .setDefaultAudience(DefaultAudience.FRIENDS)
                .logInWithPublishPermissions(this, Arrays.asList("publish_actions"));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
