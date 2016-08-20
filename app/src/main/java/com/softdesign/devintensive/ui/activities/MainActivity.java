package com.softdesign.devintensive.ui.activities;

import android.Manifest;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.softdesign.devintensive.R;
import com.softdesign.devintensive.data.managers.DataManager;
import com.softdesign.devintensive.data.network.res.UploadPhotoRes;
import com.softdesign.devintensive.utils.AppConfig;
import com.softdesign.devintensive.utils.ConstantManager;
import com.softdesign.devintensive.utils.DevIntensiveApplication;
import com.softdesign.devintensive.utils.ImageUtils;
import com.softdesign.devintensive.utils.NetworkStatusChecker;
import com.softdesign.devintensive.utils.UIHelper;
import com.softdesign.devintensive.utils.Validator;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.softdesign.devintensive.utils.UIHelper.filePathFromUri;


public class MainActivity extends BaseActivity {
    private static final String TAG = ConstantManager.TAG_PREFIX + "MainActivity";
    private static int NOTIFY_ID = 0;

    private DataManager mDataManager;
    private int mCurrentEditMode = 0;

    private ImageView mAvatar;
    @BindView(R.id.call_img) ImageView mCalling;
    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.navigation_drawer) DrawerLayout mNavigationDrawer;
    @BindView(R.id.floating_ab) FloatingActionButton mFab;
    @BindView(R.id.profile_placeholder) RelativeLayout mProfilePlaceholder;
    @BindView(R.id.main_coordinator) CoordinatorLayout mCoordinatorLayout;
    @BindView(R.id.collapsing_toolbar) CollapsingToolbarLayout mCollapsingToolbar;
    @BindView(R.id.appbar_layout) AppBarLayout mAppBarLayout;
    @BindView(R.id.user_photo_img) ImageView mProfileImage;

    @BindViews({R.id.phone_et, R.id.email_et, R.id.vk_profile_et, R.id.repo_et, R.id.about_et})
    List<EditText> mUserInfoViews;

    @BindViews({R.id.numOfRate, R.id.numOfStrings, R.id.numOfProj})
    List<TextView> mUserValueViews;

    @BindView(R.id.send_iv) ImageView mEmailIv;
    @BindView(R.id.vk_show_iv) ImageView mVkIv;
    @BindView(R.id.git_show_iv) ImageView mGithubIv;

    private AppBarLayout.LayoutParams mAppBarParams = null;
    private File mPhotoFile = null;
    private Uri mSelectedImage = null;
    private Uri mOldImage = null;

    private int mGranted = PackageManager.PERMISSION_GRANTED;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDataManager = DataManager.getInstance();

        ButterKnife.bind(this);

        setupToolbar();
        setupDrawer();

        initUserFields();
        initUserInfoValue();
        downloadPhoto();

        Picasso.with(this)
                .load(mDataManager.getPreferencesManager().loadUserPhoto())
                .placeholder(R.drawable.userphoto_3)
                .into(mProfileImage);

        mCurrentEditMode = 0;
        if (savedInstanceState != null) {
            mCurrentEditMode = savedInstanceState.getInt(ConstantManager.CURRENT_EDIT_MODE);
        }
        changeEditMode(mCurrentEditMode, 1);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            mNavigationDrawer.openDrawer(GravityCompat.START);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initUserFields();
    }
    @Override
    protected void onPause() {
        super.onPause();
        saveUserFields();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @OnClick(R.id.floating_ab)
    public void onFabClick() {
        if (mCurrentEditMode == 0) {
            mCurrentEditMode = 1;
            changeEditMode(mCurrentEditMode, 0);
        } else {
            mCurrentEditMode = 0;
            changeEditMode(mCurrentEditMode, 0);
        }
    }

    @OnClick(R.id.profile_placeholder)
    public void openDialog() {
        showDialog(ConstantManager.LOAD_PROFILE_PHOTO);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.ECLAIR
                && keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
            onBackPressed();
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        if (mNavigationDrawer.isShown()) {
            mNavigationDrawer.closeDrawer(GravityCompat.START);
        } else if (mCurrentEditMode == 1) {
            mCurrentEditMode = 0;
            changeEditMode(mCurrentEditMode, 0);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle ourState) {
        super.onSaveInstanceState(ourState);
        ourState.putInt(ConstantManager.CURRENT_EDIT_MODE, mCurrentEditMode);
    }

    private void setupToolbar() {
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        mAppBarParams = (AppBarLayout.LayoutParams) mCollapsingToolbar.getLayoutParams();
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setupDrawer() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        circleImage(navigationView);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                showSnackbar(mCoordinatorLayout, item.getTitle().toString());
                item.setChecked(true);

                mNavigationDrawer.closeDrawer(GravityCompat.START);
                if (item.getItemId() == R.id.team_menu) {
                    Intent listIntent = new Intent(MainActivity.this, UserListActivity.class);
                    startActivity(listIntent);
                    finish();
                } else if (item.getItemId() == R.id.notify_menu) {
                    createNotification();
                } else if (item.getItemId() == R.id.settings_menu) {
                    Intent listIntent = new Intent(MainActivity.this, SettingsActivity.class);
                    startActivity(listIntent);
                } else if (item.getItemId() == R.id.logout_menu) {
                    deleteAuth();

                    Intent listIntent = new Intent(MainActivity.this, AuthActivity.class);
                    startActivity(listIntent);
                    finish();
                }
                return false;
            }
        });
    }

    private void createNotification() {
        Context ctx = DevIntensiveApplication.getContext();
        Resources res = ctx.getResources();
        PendingIntent contentIntent = PendingIntent.getActivity(ctx, 0, new Intent(
                Intent.ACTION_VIEW, Uri.parse(AppConfig.RESUME)
        ), PendingIntent.FLAG_CANCEL_CURRENT);
        Notification.Builder builder = new Notification.Builder(ctx);
        builder.setContentIntent(contentIntent)
                .setSmallIcon(R.drawable.ic_notifications_active_black_24dp)
                .setLargeIcon(BitmapFactory.decodeResource(res, R.drawable.java_icon))
                .setTicker(getString(R.string.notify_ticker))
                .setWhen(System.currentTimeMillis() + 60*5000)
                .setAutoCancel(true)
                .setPriority(Notification.PRIORITY_MAX)
                .setContentTitle(getString(R.string.notify_title))
                .setContentText(getString(R.string.notyfy_text))
                .setDefaults(Notification.DEFAULT_VIBRATE | Notification.DEFAULT_SOUND);//поменять 2е на lights?

        long[] vibrate = new long[] { 1000, 1000, 1000, 1000, 1000 };
        //Notification notification = builder.build();
        Notification notification = new Notification.BigPictureStyle(builder)
                .bigPicture(BitmapFactory.decodeResource(res, R.drawable.partnership)).build();
        notification.vibrate = vibrate;

        NotificationManager notificationManager = (NotificationManager) ctx.getSystemService(
                Context.NOTIFICATION_SERVICE);
        notificationManager.notify(++NOTIFY_ID, notification);
    }

    private void deleteAuth() {
        mDataManager.getPreferencesManager().deleteAuth();
    }

    private void uploadPhoto(Uri fileUri) {
        if (NetworkStatusChecker.isNetworkAvailable(this)) {
            Log.d(TAG, "Magic = " + filePathFromUri(fileUri));
            File file = new File(filePathFromUri(fileUri));
            RequestBody requestFile =
                    RequestBody.create(MediaType.parse("multipart/form-data"), file);
            MultipartBody.Part body =
                    MultipartBody.Part.createFormData("photo", file.getName(), requestFile);

            Call<UploadPhotoRes> call = mDataManager.uploadPhoto(
                    mDataManager.getPreferencesManager().getUserId(), body);
            call.enqueue(new Callback<UploadPhotoRes>() {
                @Override
                public void onResponse(Call<UploadPhotoRes> call,
                                       Response<UploadPhotoRes> response) {
                    if (!response.isSuccessful()) {
                        showError(TAG, "Error: " + String.valueOf(response.errorBody().source()));
                    } else {
                        Log.v(TAG, "success");
                        showSnackbar(mCoordinatorLayout, "Upload success!");
                    }
                }

                @Override
                public void onFailure(Call<UploadPhotoRes> call, Throwable t) {
                    Log.e(TAG, "Upload error:" + t.getMessage());
                    showSnackbar(mCoordinatorLayout, "Upload failed!");
                }
            });
        } else {
            showSnackbar(mCoordinatorLayout, "Сеть на данный момент не доступна, попробуйте позже");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case ConstantManager.REQUEST_GALLERY_PICTURE:
                if (resultCode == RESULT_OK && data != null) {
                    mOldImage = mSelectedImage;
                    mSelectedImage = data.getData();
                    if (mOldImage != mSelectedImage) {
                        boolean sendPhoto = mDataManager.getPreferencesManager().isSendPhotoEnabled();
                        Log.d(TAG, "sendPhoto = " + sendPhoto);
                        if (sendPhoto) {
                            uploadPhoto(mSelectedImage);
                        }
                        insertProfileImage(mSelectedImage, 0);
                    }
                }
                break;
            case ConstantManager.REQUEST_CAMERA_PICTURE:
                if (requestCode == RESULT_OK && mPhotoFile != null) {
                    mOldImage = mSelectedImage;
                    mSelectedImage = Uri.fromFile(mPhotoFile);
                    Log.d(TAG, "mOldImage = " + mOldImage);
                    if (mOldImage != mSelectedImage) {
                        boolean sendPhoto = mDataManager.getPreferencesManager().isSendPhotoEnabled();
                        Log.d(TAG, "sendPhoto = " + sendPhoto);
                        if (sendPhoto) {
                            uploadPhoto(mSelectedImage);
                        }
                        insertProfileImage(mSelectedImage, 0);
                    }
                }
                break;
        }
    }

    private void circleImage(NavigationView view) {
        if (mAvatar == null) {
            //mAvatar = (ImageView) view.getHeaderView(0).findViewById(R.id.user_avatar);
            mAvatar = ButterKnife.findById(view.getHeaderView(0), R.id.user_avatar);
        }
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.userphoto_3);
        mAvatar.setImageBitmap(ImageUtils.getRoundedBitmap(bitmap));
    }

    /**
     * @param mode - 0 - view mode, 1 - edit mode
     * @param first - 0 - on create, 1 - in runtime
     */
    private void changeEditMode(int mode, int first) {
        if (mode == 1) {
            mFab.setImageResource(R.drawable.ic_done_black_24dp);
            for (EditText userValue : mUserInfoViews) {
                userValue.setEnabled(true);
                userValue.setFocusable(true);
                userValue.setFocusableInTouchMode(true);

                showProfilePlaceholder();
                lockToolbar();
                mCollapsingToolbar.setExpandedTitleColor(Color.TRANSPARENT);
            }
            EditText userPhone = mUserInfoViews.get(0);
            if (userPhone != null) userPhone.requestFocus();
        } else {
            mFab.setImageResource(R.drawable.ic_create_black_24dp);
            for (EditText userValue : mUserInfoViews) {
                userValue.setEnabled(false);
                userValue.setFocusable(false);
                userValue.setFocusableInTouchMode(false);
            }
            hideProfilePlaceholder();
            unlockToolbar();
            mCollapsingToolbar.setExpandedTitleColor(getResources().getColor(R.color.white));
            // in first time nothing more to do
            if (first == 1) return;

            if (!mDataManager.getPreferencesManager().checkFields()) {
                saveUserFields();
            } else {
                Validator validator = new Validator(mUserInfoViews);
                if (validator.validate()) {
                    saveUserFields();
                } else {
                    List<String> errList = validator.getErrorList();
                    for (String err : errList) {
                        showError(TAG, err);
                    }
                }
            }
        }
    }

    private void saveUserFields() {
        List<String> userData = new ArrayList<>();
        for (EditText userField : mUserInfoViews) {
            userData.add(userField.getText().toString());
        }
        mDataManager.getPreferencesManager().saveUserProfileData(userData);
    }

    private void initUserFields() {
        List<String> userData = mDataManager.getPreferencesManager().loadUserProfileData();
        for (int i=0; i < userData.size(); i++) {
            String curParam = userData.get(i);
            if (!curParam.equals("null")) {
                mUserInfoViews.get(i).setText(curParam);
            }
        }
        String fullName = mDataManager.getPreferencesManager().getFullName();
        if (!fullName.equals("null")) {
            mCollapsingToolbar.setTitle(fullName);
        }
    }

    private void initUserInfoValue() {
        List<String> userData = mDataManager.getPreferencesManager().loadUserProfileValues();
        for (int i=0; i < userData.size(); i++) {
            mUserInfoViews.get(i).setText(userData.get(i));
        }
    }

    private void downloadPhoto() {
        if (NetworkStatusChecker.isNetworkAvailable(this)) {
            mSelectedImage = mDataManager.getPreferencesManager().loadUserPhoto();
            insertProfileImage(mSelectedImage, 1);
        } else {
            showSnackbar(mCoordinatorLayout, "Сеть на данный момент не доступна, попробуйте позже");
        }
    }

    private void loadPhotoFromGallery() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) == mGranted) {
            sendGalleryIntent();
        } else {
            ActivityCompat.requestPermissions(this, new String[] {
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
            }, ConstantManager.REQUEST_PERMISSIONS_READ_SDCARD);

            Snackbar.make(mCoordinatorLayout,
                    getString(R.string.need_a_permission_title), Snackbar.LENGTH_LONG)
                    .setAction(getString(R.string.give_a_permission), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            openApplicationSettings();
                        }
                    }).show();
        }
    }

    private void sendGalleryIntent() {
        Intent takeGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        takeGalleryIntent.setType("image/*");
        startActivityForResult(Intent.createChooser(takeGalleryIntent,
                getString(R.string.user_profile_chose_message)),
                ConstantManager.REQUEST_GALLERY_PICTURE);
    }

    private void loadPhotoFromCamera() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) == mGranted &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == mGranted) {
            sendCaptureIntent();
        } else {
            ActivityCompat.requestPermissions(this, new String[] {
                    android.Manifest.permission.CAMERA,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, ConstantManager.PERMISSION_REQUEST_CAMERA_CODE);

            Snackbar.make(mCoordinatorLayout,
                    getString(R.string.need_a_permission_title), Snackbar.LENGTH_LONG)
                    .setAction(getString(R.string.give_a_permission), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            openApplicationSettings();
                        }
                    }).show();
        }
    }

    private void sendCaptureIntent() {
        Intent takeCaptureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            mPhotoFile = createImageFile();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, e.getMessage());
        }

        if (mPhotoFile != null) {
            takeCaptureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mPhotoFile));
            startActivityForResult(takeCaptureIntent, ConstantManager.REQUEST_CAMERA_PICTURE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length == 0) return; //cancelled
        switch (requestCode) {
            case ConstantManager.PERMISSION_REQUEST_CAMERA_CODE:
                //Check CAMERA and WRITE_EXTERNAL_STORAGE
                if (grantResults.length == 2 && grantResults[0] == mGranted && grantResults[1] == mGranted) {
                    sendCaptureIntent();
                } else {
                    onPermissionsDenied(permissions, grantResults, ConstantManager.PERMISSION_REQUEST_CAMERA_CODE);
                }
                break;
            case ConstantManager.REQUEST_PERMISSIONS_READ_SDCARD:
                //Check READ_EXTERNAL_STORAGE
                if (grantResults[0] == mGranted) {
                    sendGalleryIntent();
                } else {
                    onPermissionsDenied(permissions, grantResults, ConstantManager.REQUEST_PERMISSIONS_READ_SDCARD);
                }
                break;
        }
    }

    private void onPermissionsDenied(@NonNull String[] permissions, @NonNull int[] grantResults, int requestCode) {
        for (int i = 0; i < permissions.length; i++) {
            if (grantResults[i] != PackageManager.PERMISSION_DENIED) continue;
            switch (permissions[i]) {
                case Manifest.permission.CAMERA:
                    showError(TAG, getString(R.string.error_denied_camera));
                    break;
                case Manifest.permission.WRITE_EXTERNAL_STORAGE:
                    showError(TAG, getString(R.string.error_denied_write_storage));
                    break;
                case Manifest.permission.READ_EXTERNAL_STORAGE:
                    showError(TAG, getString(R.string.error_denied_read_storage));
                    break;
            }
        }
    }

    private void hideProfilePlaceholder() {
        mProfilePlaceholder.setVisibility(View.GONE);
    }

    private void showProfilePlaceholder() {
        mProfilePlaceholder.setVisibility(View.VISIBLE);
    }

    private void lockToolbar() {
        mAppBarLayout.setExpanded(true, true);
        mAppBarParams.setScrollFlags(0);
        mCollapsingToolbar.setLayoutParams(mAppBarParams);
    }

    private void unlockToolbar() {
        mAppBarParams.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL |
                AppBarLayout.LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED);
        mCollapsingToolbar.setLayoutParams(mAppBarParams);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case ConstantManager.LOAD_PROFILE_PHOTO:
                String[] selectItems = {
                        getString(R.string.user_profile_dialog_gallery),
                        getString(R.string.user_profile_dialog_camera),
                        getString(R.string.user_profile_dialog_cancel)
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(getString(R.string.user_profile_dialog_title));
                builder.setItems(selectItems, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int choiceItem) {
                        switch (choiceItem) {
                            case 0:
                                loadPhotoFromGallery();
                                showSnackbar(mCoordinatorLayout, "загрузить из галереи");
                                break;
                            case 1:
                                loadPhotoFromCamera();
                                showSnackbar(mCoordinatorLayout, "загрузить с камеры");
                                break;
                            case 2:
                                dialog.cancel();
                                showSnackbar(mCoordinatorLayout, "Отмена");
                                break;
                        }
                    }
                });
                return builder.create();
            default:
                return null;
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

        File image = File.createTempFile(imageFileName, ".jpg", storageDir);

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        values.put(MediaStore.MediaColumns.DATA, image.getAbsolutePath());
        getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        return image;
    }

    private void insertProfileImage(Uri image, int flag) {
        Picasso.with(this)
                .load(image)
                .into(mProfileImage);

        if (flag == 0) {
            mDataManager.getPreferencesManager().saveUserPhoto(image);
        }
    }

    public void openApplicationSettings() {
        Intent appSettingsIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.parse("package:" + getPackageName()));

        startActivityForResult(appSettingsIntent, ConstantManager.PERMISSION_REQUEST_SETTINGS_CODE);
    }

    private String getPhoneNumber(String phone) {
        phone = phone.replace("(","");
        phone = phone.replace(")","");
        phone = phone.replace("-","");
        phone = phone.replace(" ","");

        return phone;
    }

    @OnClick(R.id.call_img)
    public void dialNumber() {
        EditText userPhone = mUserInfoViews.get(0);
        if (userPhone == null) return;

        String phone = userPhone.getText().toString().trim();
        if (phone.isEmpty()) return;

        Intent intent = new Intent(Intent.ACTION_CALL);
        phone = getPhoneNumber(phone);
        intent.setData(Uri.parse("tel:" + phone));

        if (intent.resolveActivity(getPackageManager()) != null) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) == mGranted) {
                startActivity(intent);
            }
        }
    }

    @OnClick(R.id.send_iv)
    public void sendEmailMessage() {
        EditText userMail = mUserInfoViews.get(1);
        if (userMail == null) return;

        String emailAddress = userMail.getText().toString().trim().toLowerCase();
        if (emailAddress.isEmpty()) return;

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, "Greetings");
        intent.putExtra(Intent.EXTRA_TEXT, "Hello, it's DevIntensive\n2016" +
                "\n\n" + getString(R.string.app_name));
        intent.putExtra(Intent.EXTRA_EMAIL, new String[] { emailAddress });
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(Intent.createChooser(intent, "SEND.MESSAGE"));
        }
    }

    @OnClick(R.id.vk_show_iv)
    public void openVK() {
        EditText userVK = mUserInfoViews.get(2);
        if (userVK == null) return;

        String vkAddress = userVK.getText().toString().trim().toLowerCase();
        if (vkAddress.isEmpty()) return;

        Uri address = Uri.parse("http://" + vkAddress);
        Intent intent = new Intent(Intent.ACTION_VIEW, address);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    @OnClick(R.id.git_show_iv)
    public void openGithub() {
        EditText userGit = mUserInfoViews.get(3);
        if (userGit == null) return;

        String gitAddress = userGit.getText().toString().trim().toLowerCase();
        if (gitAddress.isEmpty()) return;

        UIHelper.openRepo(this, gitAddress);
    }
}
