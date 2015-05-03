package com.qtrandev.arex;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
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
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareButton;
import com.facebook.share.widget.ShareDialog;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.Random;


public class ArexMainMenuActivity extends ActionBarActivity {
    private ShareButton shareButton;
    private Button mRegularButton;
    private CallbackManager callbackManager;
    private AccessTokenTracker accessTokenTracker;
    private ShareDialog shareDialog;
    private String mCurrentPhotoPath;
    private boolean takingPhoto = false;
    private boolean takingPhotoForWall = false;
    private File photoFile=null;
    static final int CAMERA_PIC_REQUEST = 1;
    private static final int REQUEST_CODE_SHARE_TO_MESSENGER = 1;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_TAKE_PHOTO = 1;


    //Info for photo intent

    private static final String EXTRA_PROTOCOL_VERSION = "com.facebook.orca.extra.PROTOCOL_VERSION";
    private static final String EXTRA_APP_ID = "com.facebook.orca.extra.APPLICATION_ID";
    private static final int PROTOCOL_VERSION = 20150314;
    private static final String YOUR_APP_ID = "870248279704331";
    private static final int SHARE_TO_MESSENGER_REQUEST_CODE = 1;
/*
    @Override

    public void onResume() {
        super.onResume();


        // Call the 'activateApp' method to log an app event for use in analytics and advertising
        // reporting.  Do so in the onResume methods of the primary Activities that an app may be
        // launched into.
        AppEventsLogger.activateApp(this);
    }
    */

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
        if((takingPhoto)&&(resultCode>0) ){
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            this.takingPhoto=false;
            processPhotoDataForMessenger(photo);
        }
        else if((takingPhoto)&&(resultCode>0)){
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            this.takingPhotoForWall=false;
            processPhotoDataForWallPost(photo);
        }
        else
        {
            takingPhoto=false;
            takingPhotoForWall=false;
            super.onActivityResult(requestCode, resultCode, data);
            callbackManager.onActivityResult(requestCode, resultCode, data);

        }

    }


    public void dispatchTakePictureIntent(View view) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            this.takingPhoto=true;
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }


    public void dispatchTakePictureIntentWallPost(View view) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            this.takingPhotoForWall=true;
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    private void processPhotoDataForWallPost(Bitmap photo) {


        if (ShareDialog.canShow(ShareLinkContent.class)) {

            SharePhoto sharePhoto = new SharePhoto.Builder()
                    .setBitmap(photo)
                    .build();

            SharePhotoContent photoContent = new SharePhotoContent.Builder()
                    .addPhoto(sharePhoto)
                    .build();

            shareDialog.show(photoContent);
        }
    }


    public Uri bitmapToUriConverter(Bitmap mBitmap) {
        Uri uri = null;
        try {
            final BitmapFactory.Options options = new BitmapFactory.Options();


            File file = new File(this.getFilesDir(), "Image"
                    + new Random().nextInt() + ".jpeg");
            FileOutputStream out = this.openFileOutput(file.getName(),
                    Context.MODE_WORLD_READABLE);
            mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
            String realPath = file.getAbsolutePath();
            File f = new File(realPath);
            uri = Uri.fromFile(f);

        } catch (Exception e) {
            Log.e("Cannot convert to uri", e.getMessage());
        }
        return uri;
    }

    public void processPhotoDataForMessenger(Bitmap photo) {



        ShareToMessengerParams shareToMessengerParams =
                ShareToMessengerParams.newBuilder(this.bitmapToUriConverter(photo), "image/jpeg").build();


        MessengerUtils.shareToMessenger(
                this,
                REQUEST_CODE_SHARE_TO_MESSENGER,
                shareToMessengerParams);
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
