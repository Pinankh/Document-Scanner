package com.camscanner.paperscanner.pdfcreator.view.activity.main;

import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import android.os.Environment;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;

import com.camscanner.paperscanner.pdfcreator.BuildConfig;
import com.camscanner.paperscanner.pdfcreator.common.utils.PrivacyUtil;
import com.camscanner.paperscanner.pdfcreator.view.activity.AboutUsActivity;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;


import com.tapscanner.polygondetect.PDFBoxResourceLoader;

import com.camscanner.paperscanner.pdfcreator.Glob;
import com.camscanner.paperscanner.pdfcreator.R;
import com.camscanner.paperscanner.pdfcreator.common.Constant;
import com.camscanner.paperscanner.pdfcreator.common.utils.ImageStorageUtils;
import com.camscanner.paperscanner.pdfcreator.features.premium.PremiumHelper;
import com.camscanner.paperscanner.pdfcreator.features.tutorial.TutorialManager;
import com.camscanner.paperscanner.pdfcreator.features.tutorial.model.TutorialInfo;
import com.camscanner.paperscanner.pdfcreator.view.activity.BaseMainActivity;
import com.camscanner.paperscanner.pdfcreator.view.activity.GridModeLayoutActivity;
import com.camscanner.paperscanner.pdfcreator.view.activity.setting.Setting_MyActivity;
import com.camscanner.paperscanner.pdfcreator.view.activity.signature.SignPadActivity;
import com.camscanner.paperscanner.pdfcreator.view.fragment.MainFragment;

import java.util.Objects;

public class MainActivity extends BaseMainActivity implements NavigationView.OnNavigationItemSelectedListener,  TutorialManager.OnTutorialListener {
    private static final String strDocFragTitle = "frag_doc";
    private boolean afterNewDoc;
    private boolean afterReceived;
    private ImageView btnMenu;
    private DrawerLayout drawer;
    private String lastName;
    private String lastParent;

//    private MenuItem m_navAdsRemove;
//    private MenuItem m_navUpgradePremium;


    private MainFragment mainFragment = null;
    private NavigationView navigationView;
    private ActionBarDrawerToggle mDrawerToggle;
    private Toolbar toolbar;

    public enum ACTIVE_VIEW {
        DOCS,
        SHARED,
        TAGS
    }


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= 30) {
            /*if (!Environment.isExternalStorageManager()) {
                Intent permissionIntent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                startActivity(permissionIntent);
            }*/
        }

        Intent intent = getIntent();

        initUI();
        if (savedInstanceState == null) {
            replaceFragment(ACTIVE_VIEW.DOCS);
        }

            this.afterReceived = true;

        PDFBoxResourceLoader.init(getApplicationContext());
    }

    /* access modifiers changed from: protected */
    public void onStart() {
        super.onStart();
//        Analytics.get().logDocsScreen();
    }



    /* access modifiers changed from: protected */
    public void onResume() {
        super.onResume();


    }

    /* access modifiers changed from: package-private */
    public void initUI() {
        toolbar  = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false);
        }
        this.drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        this.btnMenu = (ImageView) findViewById(R.id.btn_menu);
        this.btnMenu.setImageResource(R.mipmap.ic_launcher_round);
        this.btnMenu.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
//                MainActivity.this.lambda$initUI$0$MainActivity(view);
                showBottomSheetDialog();
            }
        });


        /*this.navigationView = (NavigationView) findViewById(R.id.nav_view);
        this.navigationView.setNavigationItemSelectedListener(this);
        this.navigationView.setItemIconTintList((ColorStateList) null);*/

//        this.m_swtHDQuality = (SwitchCompat) this.navigationView.getMenu().findItem(R.id.nav_hd_quality).getActionView().findViewById(R.id.drawer_switch);
//        this.m_swtHDQuality.setOnClickListener(new View.OnClickListener() {
//            public final void onClick(View view) {
//                MainActivity.this.lambda$initUI$1$MainActivity(view);
//            }
//        });
//        if (ScanApplication.userRepo().isUserPremium(this)) {
//            this.m_swtHDQuality.setChecked(true);
//        } else {
//            this.m_swtHDQuality.setChecked(false);
//        }
    }

    public /* synthetic */ void lambda$initUI$0$MainActivity(View v) {
        if (this.drawer.isDrawerOpen((int) GravityCompat.START)) {
            this.drawer.closeDrawer((int) GravityCompat.START);
        } else {
            this.drawer.openDrawer((int) GravityCompat.START);
        }
    }

    public /* synthetic */ void lambda$initUI$1$MainActivity(View v) {

//            Analytics.get().logPremiumFeature(AnalyticsConstants.PARAM_VALUE_PREMIUM_FEATURE_HD_QUALITY);
            PremiumHelper.showPremiumAfterAlertDialog(this, R.string.premium_feature_hd_quality, R.string.alert_premium_hd_message, new PremiumHelper.StartActivityController() {
                public final void startActivity(Intent intent, int i) {
                    MainActivity.this.startActivityForResult(intent, i);
                }
            });
//            this.m_swtHDQuality.setChecked(false);

    }

    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean("created", true);
        super.onSaveInstanceState(savedInstanceState);
    }

    public boolean onCreateOptionsMenu(Menu menu) {

        return true;
    }


    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    public void onBackPressed() {
       /* DrawerLayout drawer2 = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer2.isDrawerOpen((int) GravityCompat.START)) {
            drawer2.closeDrawer((int) GravityCompat.START);
            return;
        }*/
//        MainFragment mainFragment2 = this.mainFragment;
//        if (mainFragment2 == null || !mainFragment2.isVisible() || !this.mainFragment.isMultiMode()) {
//            finish();
//        } else {
//            this.mainFragment.setMultiMode(false);
//        }


        AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
        dialog.setMessage("Do you want to exit?")
//     .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//      public void onClick(DialogInterface dialoginterface, int i) {
//          dialoginterface.cancel();
//          }})
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialoginterface, int i) {
                        finishAffinity();
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();

            }
        }).show();

    }


    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {

            case R.id.nav_rate_us:
                rate();
                break;

            case R.id.nav_setting:
                goToSetting();
                break;
            case R.id.nav_share:
                try {
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_SUBJECT, "My application name");
                    String shareMessage= "\nLet me recommend you this application\n\n";
                    shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID +"\n\n";
                    shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                    startActivity(Intent.createChooser(shareIntent, "choose one"));
                } catch(Exception e) {
                    //e.toString();
                }
                break;
            case R.id.nav_privacy:
                PrivacyUtil.goToPrivacyPolicy(MainActivity.this);
                break;
//            case R.id.nav_signature:
//                goToSignPad();
//                break;

        }
       // DrawerLayout drawer2 = (DrawerLayout) findViewById(R.id.drawer_layout);

        return true;
    }

    private void rate() {
        if (Glob.isOnline( MainActivity.this )) {
            try {
                Uri uri = Uri.parse( "market://details?id=" + getPackageName() );
                Intent myAppLinkToMarket = new Intent( Intent.ACTION_VIEW, uri );
                try {
                    startActivity( myAppLinkToMarket );
                } catch (ActivityNotFoundException e) {
                    Toast.makeText( getApplicationContext(), "Unable to find market app", Toast.LENGTH_SHORT ).show();
                }
            } catch (Exception e) {
            }
        } else {
            Toast.makeText( getApplicationContext(), "No Internet Connection Available", Toast.LENGTH_SHORT ).show();
        }
    }



    public void goToSetting() {
        startActivity(new Intent(this, Setting_MyActivity.class));
    }

    

    private void goToSignPad() {
        startActivityForResult(new Intent(this, SignPadActivity.class), 1014);
    }



    /* access modifiers changed from: package-private */
    public void replaceFragment(ACTIVE_VIEW view) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (C68591.f14690xf0e6397e[view.ordinal()] == 1) {
            this.mainFragment = new MainFragment();
            fragmentManager.beginTransaction().replace(R.id.frameContainer, this.mainFragment, strDocFragTitle).addToBackStack("").commit();
        }
    }


    static /* synthetic */ class C68591 {

        static final /* synthetic */ int[] f14690xf0e6397e = new int[ACTIVE_VIEW.values().length];

        static {
            try {
                f14690xf0e6397e[ACTIVE_VIEW.DOCS.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        MainFragment mainFragment2 = this.mainFragment;
        if (mainFragment2 != null) {
            mainFragment2.m_bActivityChanged = true;
        }
        if (requestCode != 1002) {
            if (requestCode != 1010) {
                super.onActivityResult(requestCode, resultCode, data);
                return;
            }
            ImageStorageUtils.clearShareFolder();
        } else if (resultCode == -1) {
            String mParent = data.getExtras().getString(Constant.EXTRA_MPARENT);
            String mName = data.getExtras().getString(Constant.EXTRA_MNAME);

            autoOpenFile(mParent, mName);
//            if (!ScanApplication.adsManager.show(false)) {
//                autoOpenFile(mParent, mName);
//                return;
//            }
            this.afterNewDoc = true;
            this.lastParent = mParent;
            this.lastName = mName;
        }
    }

    public void autoOpenFile(String mParent, String mName) {
        MainFragment mainFragment2 = this.mainFragment;
        if (mainFragment2 != null) {
            mainFragment2.autoOpenFile(mParent, mName);
            return;
        }
        Intent intent = new Intent(this, GridModeLayoutActivity.class);
        Constant.m_strParent= mParent;
        intent.putExtra(Constant.EXTRA_PARENT, mParent);
        intent.putExtra("name", mName);
        startActivityForResult(intent, 1006);
    }


    public void openFile1(String mParent, String mName) {
        Intent intent = new Intent(this, GridModeLayoutActivity.class);
        Constant.m_strParent= mParent;
        intent.putExtra(Constant.EXTRA_PARENT, mParent);
        intent.putExtra("name", mName);
        startActivityForResult(intent, 1006);
    }


    public void onTutorialViewClicked(View v) {
        MainFragment mainFragment2 = this.mainFragment;
        if (mainFragment2 != null) {
            mainFragment2.onTutorialViewClicked(v);
        }
    }

    public void onTutorialClosed(TutorialInfo tutorialInfo, boolean targetHit) {
        MainFragment mainFragment2 = this.mainFragment;
        if (mainFragment2 != null) {
            mainFragment2.onTutorialClosed(tutorialInfo, targetHit);
        }
//        Analytics.get().logTutorCamera(targetHit);
    }


    private void showBottomSheetDialog() {

        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(R.layout.main_bottom_sheet);

        FloatingActionButton btn_share = (FloatingActionButton) bottomSheetDialog.findViewById(R.id.btn_share);
        FloatingActionButton btn_privacy_policy = (FloatingActionButton) bottomSheetDialog.findViewById(R.id.btn_privacy_policy);
        FloatingActionButton btn_RateUS = (FloatingActionButton) bottomSheetDialog.findViewById(R.id.btn_RateUS);
        FloatingActionButton btn_contactUS = (FloatingActionButton) bottomSheetDialog.findViewById(R.id.btn_contactUS);
        FloatingActionButton btn_aboutUs = (FloatingActionButton) bottomSheetDialog.findViewById(R.id.btn_aboutUs);
        FloatingActionButton btn_settings = (FloatingActionButton) bottomSheetDialog.findViewById(R.id.btn_settings);


        Objects.requireNonNull(btn_share).setOnClickListener(view -> {
            bottomSheetDialog.dismiss();

            try {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, R.string.app_name);
                String shareMessage= "\nLet me recommend you this application for easily create any paper, documents, Visiting cards, in image or pdf and share where you want.\n\n";
                shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID +"\n\n";
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                startActivity(Intent.createChooser(shareIntent, "Pick one"));
            } catch(Exception e) {
                //e.toString();
            }
        });

        Objects.requireNonNull(btn_privacy_policy).setOnClickListener(view -> {
            bottomSheetDialog.dismiss();
            PrivacyUtil.goToPrivacyPolicy(MainActivity.this);
        });

        Objects.requireNonNull(btn_RateUS).setOnClickListener(view -> {
            bottomSheetDialog.dismiss();
            rate();
        });

        Objects.requireNonNull(btn_contactUS).setOnClickListener(view -> {
            bottomSheetDialog.dismiss();

            /*Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:")); // only email apps should handle this
            intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"ahitiinfotech2022@gmail.com"});
            intent.putExtra(Intent.EXTRA_SUBJECT, "User Quarries & Feed Back");

            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            }*/

            Intent intent = new Intent(Intent.ACTION_SEND);
//            intent.setDataAndType(Uri.parse("mailto:"),"message/rfc822");
            intent.setType("message/rfc822");
            intent.putExtra(Intent.EXTRA_EMAIL  , new String[]{"ahitiinfotech2022@gmail.com"});
            intent.putExtra(Intent.EXTRA_SUBJECT, "User Quarries & Feed Back");
            intent.putExtra(Intent.EXTRA_TEXT   , "Contact US by email");
            try {
                startActivity(Intent.createChooser(intent, "Contact US by email"));
            } catch (android.content.ActivityNotFoundException e) {
                Toast.makeText(MainActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
            }
        });

        Objects.requireNonNull(btn_aboutUs).setOnClickListener(view -> {
            bottomSheetDialog.dismiss();
            startActivity(new Intent(MainActivity.this, AboutUsActivity.class));
        });

        Objects.requireNonNull(btn_settings).setOnClickListener(view -> {
            bottomSheetDialog.dismiss();
            goToSetting();
        });

        bottomSheetDialog.show();
    }
}
