package com.softdesign.devintensive.ui.activities;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
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
import android.text.Layout;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.softdesign.devintensive.R;
import com.softdesign.devintensive.data.managers.DataManager;
import com.softdesign.devintensive.utils.ConstantManager;
import com.softdesign.devintensive.utils.ImageUtils;
import com.softdesign.devintensive.utils.RoundedAvatarDrawable;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.jar.Manifest;


public class MainActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = ConstantManager.TAG_PREFIX + "MainActivity";

    private DataManager mDataManager;
    private int mCurrentEditMode = 0;

    private ImageView mCalling;
    private ImageView mAvatar;
    private Layout mLayout;
    private CoordinatorLayout mCoordinatorLayout;
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

    private AppBarLayout.LayoutParams mAppBarParams = null;
    private File mPhotoFile = null;
    private Uri mSelectedImage = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate");

        //mLayout = R.layout.drawer_header;
        mDataManager = DataManager.getInstance();
        mCalling = (ImageView) findViewById(R.id.call_img);
        mCalling.setOnClickListener(this);
        /*mAvatar = (ImageView) findViewById(R.id.user_avatar);
        RoundedAvatarDrawable rad = new RoundedAvatarDrawable(mAvatar.getDrawingCache());
        //rad.draw(mAvatar);
        if (android.os.Build.VERSION.SDK_INT > Build.VERSION_CODES.ECLAIR) {
            //mAvatar.setForeground(rad.getCurrent());
        }*/

        mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.main_coordinator);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mNavigationDrawer = (DrawerLayout) findViewById(R.id.navigation_drawer);
//        mNavigationView = (NavigationView) findViewById(R.id.navigation_view);
//        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
//            @Override
//            public boolean onNavigationItemSelected(MenuItem item) {
//                onNavItemSelected(item);
//            }
//        });
        mFab = (FloatingActionButton) findViewById(R.id.floating_ab);
        mFab.setOnClickListener(this);

        mProfilePlaceholder = (RelativeLayout) findViewById(R.id.profile_placeholder);
        mCollapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        mAppBarLayout = (AppBarLayout) findViewById(R.id.appbar_layout);
        mProfilePlaceholder.setOnClickListener(this);
        mProfileImage = (ImageView) findViewById(R.id.user_photo_img);

        //mAvatar = (ImageView) mNavigationView.getHeaderView(0).findViewById(R.id.user_avatar);
        RoundedAvatarDrawable rad = new RoundedAvatarDrawable(mAvatar.getDrawingCache());
        mAvatar.setBackground(new BitmapDrawable(getResources(), rad.getBitmap()));

        mUserPhone = (EditText) findViewById(R.id.phone_et);
        mUserMail = (EditText) findViewById(R.id.email_et);
        mUserVK = (EditText) findViewById(R.id.vk_profile_et);
        mUserGit = (EditText) findViewById(R.id.repo_et);
        mUserAbout = (EditText) findViewById(R.id.about_et);

        mUserInfoViews = new ArrayList<>();
        mUserInfoViews.add(mUserPhone);
        mUserInfoViews.add(mUserMail);
        mUserInfoViews.add(mUserVK);
        mUserInfoViews.add(mUserGit);
        mUserInfoViews.add(mUserAbout);

        setupToolbar();
        setupDrawer();
        loadUserInfo();
        Picasso.with(this)
                .load(mDataManager.getPreferencesManager().loadUserPhoto())
                .placeholder(R.drawable.userphoto_3)// TODO: 01.07.2016 поменять плейсхолдер
                .into(mProfileImage);

        List<String> test = mDataManager.getPreferencesManager().loadUserProfileData();

        mCurrentEditMode = 0;
        if (savedInstanceState == null) {
            showSnackbar("Активити запускается впервые");
        } else {
            showSnackbar("Активити уже запускалась ранее");
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
        Log.d(TAG, "onResume");
        loadUserInfo();
    }
    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
        saveUserInfo();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.call_img:
                showProgress();
                runWithDelay();
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
                // TODO: 01.07.2016 сделать выбор откуда загружать фото
                showDialog(ConstantManager.LOAD_PROFILE_PHOTO);
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.ECLAIR
                && keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
            // Take care of calling this method on earlier versions of
            // the platform where it doesn't exist.
            onBackPressed();
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onSaveInstanceState(Bundle ourState) {
        super.onSaveInstanceState(ourState);
        ourState.putInt(ConstantManager.CURRENT_EDIT_MODE, mCurrentEditMode);
    }

    private void showSnackbar(String message) {
        Snackbar.make(mCoordinatorLayout, message, Snackbar.LENGTH_LONG).show();
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
                return false;
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case ConstantManager.REQUEST_GALLERY_PICTURE:
                if (resultCode == RESULT_OK && data != null) {
                    mSelectedImage = data.getData();

                    insertProfileImage(mSelectedImage);
                }
                break;
            case ConstantManager.REQUEST_CAMERA_PICTURE:
                if (requestCode == RESULT_OK && mPhotoFile != null) {
                    mSelectedImage = Uri.fromFile(mPhotoFile);

                    insertProfileImage(mSelectedImage);
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
            mFab.setImageResource(R.drawable.ic_create_black_24dp);
            for (EditText userValue : mUserInfoViews) {
                userValue.setEnabled(true);
                userValue.setFocusable(true);
                userValue.setFocusableInTouchMode(true);

                showProfilePlaceholder();
                lockToolbar();
                mCollapsingToolbar.setExpandedTitleColor(Color.TRANSPARENT);
            }
        } else {
            mFab.setImageResource(R.drawable.ic_done_black_24dp);
            for (EditText userValue : mUserInfoViews) {
                userValue.setEnabled(false);
                userValue.setFocusable(false);
                userValue.setFocusableInTouchMode(false);

                hideProfilePlaceholder();
                unlockToolbar();
                mCollapsingToolbar.setExpandedTitleColor(getResources().getColor(R.color.white));

                saveUserInfo();
            }
        }
    }

    private void saveUserInfo() {
        List<String> userData = new ArrayList<>();
        for (EditText userField : mUserInfoViews) {
            userData.add(userField.getText().toString());
        }
        mDataManager.getPreferencesManager().saveUserProfileData(userData);
    }

    private void loadUserInfo() {
        List<String> userData = mDataManager.getPreferencesManager().loadUserProfileData();
        for (int i=0; i < userData.size(); i++) {
            mUserInfoViews.get(i).setText(userData.get(i));
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
        int granted = PackageManager.PERMISSION_GRANTED;
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) == granted &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == granted) {
            Intent takeCaptureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            try {
                mPhotoFile = createImageFile();
            } catch (IOException e) {
                e.printStackTrace();
                // TODO: 01.07.2016 обработать ошибку
            }

            if (mPhotoFile != null) {
                // TODO: 01.07.2016 передать фотофайл в интент
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
                                // TODO: 01.07.2016 загрузить из галереи
                                loadPhotoFromGallery();
                                showSnackbar("загрузить из галереи");
                                break;
                            case 1:
                                // TODO: 01.07.2016 загрузить с камеры
                                loadPhotoFromCamera();
                                showSnackbar("загрузить с камеры");
                                break;
                            case 2:
                                // TODO: 01.07.2016 отмена
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

    private void insertProfileImage(Uri image) {
        Picasso.with(this)
                .load(image)
                .into(mProfileImage);

        mDataManager.getPreferencesManager().saveUserPhoto(image);
    }

    public void openApplicationSettings() {
        Intent appSettingsIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.parse("packege:" + getPackageName()));

        startActivityForResult(appSettingsIntent, ConstantManager.PERMISSION_REQUEST_SETTINGS_CODE);
    }
}
