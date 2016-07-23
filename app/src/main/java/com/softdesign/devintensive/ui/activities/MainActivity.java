package com.softdesign.devintensive.ui.activities;

import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.softdesign.devintensive.R;
import com.softdesign.devintensive.data.managers.DataManager;
import com.softdesign.devintensive.data.network.RestService;
import com.softdesign.devintensive.data.network.ServiceGenerator;
import com.softdesign.devintensive.data.network.res.UploadPhotoRes;
import com.softdesign.devintensive.ui.fragments.SettingsFragment;
import com.softdesign.devintensive.utils.AppConfig;
import com.softdesign.devintensive.utils.ConstantManager;
import com.softdesign.devintensive.utils.DevIntensiveApplication;
import com.softdesign.devintensive.utils.FileUtils;
import com.softdesign.devintensive.utils.ImageUtils;
import com.softdesign.devintensive.utils.NetworkStatusChecker;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = ConstantManager.TAG_PREFIX + "MainActivity";
    private static int NOTIFY_ID = 0;

    private DataManager mDataManager;
    private int mCurrentEditMode = 0;

    private ImageView mCalling;
    private ImageView mAvatar;
    private Toolbar mToolbar;
    private DrawerLayout mNavigationDrawer;
    //private NavigationView mNavigationView;
    private FloatingActionButton mFab;
    private RelativeLayout mProfilePlaceholder;
    private CollapsingToolbarLayout mCollapsingToolbar;
    private AppBarLayout mAppBarLayout;
    private ImageView mProfileImage;

    private EditText mUserPhone, mUserMail, mUserVK, mUserGit, mUserAbout;
    private List<EditText> mUserInfoViews;

    private TextView mUserRating, mLinesCode, mProjects;
    private List<TextView> mUserValueViews;

    private AppBarLayout.LayoutParams mAppBarParams = null;
    private File mPhotoFile = null;
    private Uri mSelectedImage = null;
    private Uri mOldImage = null;

    private int mGranted = PackageManager.PERMISSION_GRANTED;

    private ImageView mCallIv, mEmailIv, mSendIv, mVkIv, mVkVisibleIv, mGithubIv, mGithubVisibleIv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDataManager = DataManager.getInstance();
        mCalling = (ImageView) findViewById(R.id.call_img);
        mCalling.setOnClickListener(this);

        mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.main_coordinator);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mNavigationDrawer = (DrawerLayout) findViewById(R.id.navigation_drawer);
//        mNavigationView = (NavigationView) findViewById(R.id.navigation_view);

        mFab = (FloatingActionButton) findViewById(R.id.floating_ab);
        mFab.setOnClickListener(this);

        mProfilePlaceholder = (RelativeLayout) findViewById(R.id.profile_placeholder);
        mCollapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        mAppBarLayout = (AppBarLayout) findViewById(R.id.appbar_layout);
        mProfilePlaceholder.setOnClickListener(this);
        mProfileImage = (ImageView) findViewById(R.id.user_photo_img);

        //mAvatar = (ImageView) mNavigationView.getHeaderView(0).findViewById(R.id.user_avatar);

        mCallIv = (ImageView) findViewById(R.id.call_iv);
        mEmailIv = (ImageView) findViewById(R.id.email_iv);
        mSendIv = (ImageView) findViewById(R.id.send_iv);
        mVkIv = (ImageView) findViewById(R.id.vk_iv);
        mVkVisibleIv = (ImageView) findViewById(R.id.vk_visible_iv);
        mGithubIv = (ImageView) findViewById(R.id.github_iv);
        mGithubVisibleIv = (ImageView) findViewById(R.id.github_visible_iv);

        mCallIv.setOnClickListener(this);
        mEmailIv.setOnClickListener(this);
        mVkIv.setOnClickListener(this);
        mGithubIv.setOnClickListener(this);

        mUserPhone = (EditText) findViewById(R.id.phone_et);
        mUserMail = (EditText) findViewById(R.id.email_et);
        mUserVK = (EditText) findViewById(R.id.vk_profile_et);
        mUserGit = (EditText) findViewById(R.id.repo_et);
        mUserAbout = (EditText) findViewById(R.id.about_et);

        mUserRating = (TextView) findViewById(R.id.numOfRate);
        mLinesCode = (TextView) findViewById(R.id.numOfStrings);
        mProjects = (TextView) findViewById(R.id.numOfProj);

        mUserInfoViews = new ArrayList<>();
        mUserInfoViews.add(mUserPhone);
        mUserInfoViews.add(mUserMail);
        mUserInfoViews.add(mUserVK);
        mUserInfoViews.add(mUserGit);
        mUserInfoViews.add(mUserAbout);

        mUserValueViews = new ArrayList<>();
        mUserValueViews.add(mUserRating);
        mUserValueViews.add(mLinesCode);
        mUserValueViews.add(mProjects);

        setupToolbar();
        setupDrawer();

        initUserFields();
        initUserInfoValue();
        downloadPhoto();

        Picasso.with(this)
                .load(mDataManager.getPreferencesManager().loadUserPhoto())
                .placeholder(R.drawable.userphoto_3)// TODO: 01.07.2016 поменять плейсхолдер
                .into(mProfileImage);

        mCurrentEditMode = 0;
        if (savedInstanceState != null) {
            mCurrentEditMode = savedInstanceState.getInt(ConstantManager.CURRENT_EDIT_MODE);
        }
        changeEditMode(mCurrentEditMode);
    }

//    private boolean onNavItemSelected(MenuItem item) {
//        return false;
//    }

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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.call_img:
                showProgress();
                runWithDelay();
                dialNumber();
                break;
            case R.id.floating_ab:
                showSnackbar("Click");
                if (mCurrentEditMode == 0) {
                    mCurrentEditMode = 1;
                    changeEditMode(1);
                } else {
                    mCurrentEditMode = 0;
                    changeEditMode(0);
                }
                break;
            case R.id.appbar_layout:
                showDialog(ConstantManager.LOAD_PROFILE_PHOTO);
                break;
            case R.id.profile_placeholder:
                showDialog(ConstantManager.LOAD_PROFILE_PHOTO);
                break;
            case R.id.call_iv:
                dialNumber();
                break;
            case R.id.email_iv:
                sendEmailMessage();
                break;
            case R.id.vk_iv:
                openVK();
                break;
            case R.id.github_iv:
                openGithub();
                break;
        }
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
            changeEditMode(0);
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
                showSnackbar(item.getTitle().toString());
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
            RestService restService = ServiceGenerator.createService(RestService.class);
            File file = FileUtils.getFile(fileUri);
            RequestBody requestFile =
                    RequestBody.create(MediaType.parse("multipart/form-data"), file);
//            MultipartBody.Part body =
//                    MultipartBody.Part.createFormData("photo", file.getName(), requestFile);
            //mDataManager.uploadPhoto(file);
            Call<UploadPhotoRes> call = restService.uploadPhoto(
                    mDataManager.getPreferencesManager().getUserId(), requestFile);
            call.enqueue(new Callback<UploadPhotoRes>() {
                @Override
                public void onResponse(Call<UploadPhotoRes> call,
                                       Response<UploadPhotoRes> response) {
                    Log.v("Upload", "success");
                }

                @Override
                public void onFailure(Call<UploadPhotoRes> call, Throwable t) {
                    Log.e("Upload error:", t.getMessage());
                }
            });
        } else {
            showSnackbar("Сеть на данный момент не доступна, попробуйте позже");
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
                    if (mOldImage != mSelectedImage) {
                        boolean sendPhoto = mDataManager.getPreferencesManager().isSendPhotoEnabled();
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
            mAvatar = (ImageView) view.getHeaderView(0).findViewById(R.id.user_avatar);
        }
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.userphoto_3);
        mAvatar.setImageBitmap(ImageUtils.getRoundedBitmap(bitmap));
    }

    private void changeEditMode(int mode) {
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
            mUserPhone.requestFocus();
        } else {
            mFab.setImageResource(R.drawable.ic_create_black_24dp);
            for (EditText userValue : mUserInfoViews) {
                userValue.setEnabled(false);
                userValue.setFocusable(false);
                userValue.setFocusableInTouchMode(false);

                hideProfilePlaceholder();
                unlockToolbar();
                mCollapsingToolbar.setExpandedTitleColor(getResources().getColor(R.color.white));

                if (validatePhone() && validateMail() && validateVk() && validateGithub()) {
                    saveUserFields();
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
            showSnackbar("Сеть на данный момент не доступна, попробуйте позже");
        }
    }

    private void loadPhotoFromGallery() {
        Intent takeGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        takeGalleryIntent.setType("image/*");
        startActivityForResult(Intent.createChooser(takeGalleryIntent,
                getString(R.string.user_profile_chose_message)),
                ConstantManager.REQUEST_GALLERY_PICTURE);
    }

    private void loadPhotoFromCamera() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) == mGranted &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == mGranted) {
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == ConstantManager.PERMISSION_REQUEST_CAMERA_CODE && grantResults.length == 2) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // TODO: 01.07.2016 обработать получение разрешения
            }
            if (grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                // TODO: 01.07.2016 обработать получение разрешения
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
                                showSnackbar("загрузить из галереи");
                                break;
                            case 1:
                                loadPhotoFromCamera();
                                showSnackbar("загрузить с камеры");
                                break;
                            case 2:
                                dialog.cancel();
                                showSnackbar("Отмена");
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

    public void dialNumber() {
        String phone = mUserPhone.getText().toString().trim();
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

    public void sendEmailMessage() {
        String emailAddress = mUserMail.getText().toString().trim().toLowerCase();
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

    public void openVK() {
        String vkAddress = mUserVK.getText().toString().trim().toLowerCase();
        if (vkAddress.isEmpty()) return;

        Uri address = Uri.parse("http://" + vkAddress);
        Intent intent = new Intent(Intent.ACTION_VIEW, address);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    public void openGithub() {
        String gitAddress = mUserGit.getText().toString().trim().toLowerCase();
        if (gitAddress.isEmpty()) return;

        Uri address = Uri.parse("http://" + gitAddress);
        Intent intent = new Intent(Intent.ACTION_VIEW, address);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    private boolean validatePhone() {
        String phone = mUserPhone.getText().toString().trim();
        if (phone.isEmpty()) return false;

        phone = getPhoneNumber(phone);
        if (phone.length() < 11 || phone.length() > 20) return false;
        return true;
    }

    private boolean validateMail() {
        String email = mUserMail.getText().toString().trim().toLowerCase();
        if (email.isEmpty()) return false;

        int pos = email.indexOf("@");
        if (pos < 2) return false;
        email = email.substring(pos);

        pos = email.indexOf(".");
        if (pos < 1) return false;
        email = email.substring(pos);

        if (email.length() < 2) return false;
        return true;
    }

    private boolean validateVk() {
        String vkAddr = mUserVK.getText().toString().trim().toLowerCase();
        if (vkAddr.isEmpty()) return false;

        int pos = vkAddr.indexOf("https://");
        if (pos > 0) return false;
        else if (pos == 0) {
            vkAddr = vkAddr.substring(8);
            mUserVK.setText(vkAddr);
        }
        pos = vkAddr.indexOf("vk.com");
        if (pos != -1) return false;
        return true;
    }

    private boolean validateGithub() {
        String gitAddr = mUserGit.getText().toString().trim().toLowerCase();
        if (gitAddr.isEmpty()) return false;

        int pos = gitAddr.indexOf("https://");
        if (pos > 0) return false;
        else if (pos == 0) {
            gitAddr = gitAddr.substring(8);
            mUserVK.setText(gitAddr);
        }
        pos = gitAddr.indexOf("github.com");
        if (pos != -1) return false;
        return true;
    }
}
