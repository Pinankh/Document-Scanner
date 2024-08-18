package com.camscanner.paperscanner.pdfcreator.view.activity;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.camscanner.paperscanner.pdfcreator.BuildConfig;
import com.camscanner.paperscanner.pdfcreator.R;
import com.camscanner.paperscanner.pdfcreator.common.Constant;

import java.util.Calendar;
import ashiti.custome.aboutpage.AboutPage;
import ashiti.custome.aboutpage.Element;

public class AboutUsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Element adsElement = new Element();
        adsElement.setTitle("Support US");

        View aboutPage = new AboutPage(this)
                .isRTL(false)
                .setImage(R.drawable.ashiti_infotech_white_logo)
                .addItem(new Element().setTitle("Version "+BuildConfig.VERSION_NAME))
                .addItem(adsElement)
                .addGroup("Connect with us")
                .addEmail("ahitiinfotech2022@gmail.com")
                .addWebsite(Constant.website)
                .addFacebook(Constant.facebook_id)
                //.addTwitter("medyo80")
                //.addYoutube("UCdPQtdWIsg7_pi4mrRu46vA")
                .addPlayStore(BuildConfig.APPLICATION_ID)
                .addInstagram(Constant.instagram_id)
                .addGitHub(Constant.github_id)
                .addItem(getCopyRightsElement())
                .create();

        setContentView(aboutPage);
    }


    Element getCopyRightsElement() {
        Element copyRightsElement = new Element();
        final String copyrights = String.format(getString(R.string.copy_right), Calendar.getInstance().get(Calendar.YEAR));
        copyRightsElement.setTitle(copyrights);
        copyRightsElement.setIconDrawable(R.drawable.about_icon_copy_right);
        copyRightsElement.setAutoApplyIconTint(true);
        copyRightsElement.setIconTint(R.color.about_item_icon_color);
        copyRightsElement.setIconNightTint(android.R.color.white);
        copyRightsElement.setGravity(Gravity.CENTER);
        copyRightsElement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(AboutUsActivity.this, copyrights, Toast.LENGTH_SHORT).show();
            }
        });
        return copyRightsElement;
    }
}