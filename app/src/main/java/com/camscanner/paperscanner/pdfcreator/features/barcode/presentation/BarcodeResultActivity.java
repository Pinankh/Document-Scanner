package com.camscanner.paperscanner.pdfcreator.features.barcode.presentation;

import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.client.result.EmailAddressParsedResult;
import com.google.zxing.client.result.GeoParsedResult;
import com.google.zxing.client.result.ParsedResult;
import com.google.zxing.client.result.ParsedResultType;
import com.google.zxing.client.result.SMSParsedResult;
import com.google.zxing.client.result.TelParsedResult;
import com.google.zxing.client.result.URIParsedResult;
import com.camscanner.paperscanner.pdfcreator.R;
import com.camscanner.paperscanner.pdfcreator.features.barcode.model.ParsedResultAdapter;
import com.camscanner.paperscanner.pdfcreator.features.barcode.model.QrResult;
import com.camscanner.paperscanner.pdfcreator.view.activity.BaseMainActivity;

public class BarcodeResultActivity extends BaseMainActivity {
    private static final String LOG_TAG = BarcodeResultActivity.class.getSimpleName();
    public static final String QR_RESULT = "qr_result";
    View btnOpen;
    View btnSend;
    private ParsedResult result;
    TextView textOpen;
    TextView textSend;
    TextView textView;

    public static void startResultActivity(FragmentActivity activity, QrResult result2) {
        Intent intent = new Intent(activity, BarcodeResultActivity.class);
        intent.addFlags(131072);
        intent.putExtra(QR_RESULT, result2);
        activity.startActivityForResult(intent, 1024);
    }

    public void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView((int) R.layout.activity_qr_result);

        btnOpen = (View)findViewById(R.id.btn_open);
        btnSend = (View)findViewById(R.id.btn_send);
        textOpen = (TextView)findViewById(R.id.open_text);
        ConstraintLayout btn_copy = (ConstraintLayout)findViewById(R.id.btn_copy);
        ConstraintLayout btn_share = (ConstraintLayout)findViewById(R.id.btn_share);
        ImageView btn_list = (ImageView)findViewById(R.id.btn_list);
        ImageView btn_back = (ImageView)findViewById(R.id.btn_back);
        textSend = (TextView)findViewById(R.id.send_text);
        textView = (TextView)findViewById(R.id.text);

        btnOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onUriClicked();
            }
        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSendClicked();
            }
        });

        btn_copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onCopyClicked();
            }
        });
        btn_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onShareClicked();
            }
        });

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        btn_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onListClicked();
            }
        });

        initResult();
    }

    /* access modifiers changed from: protected */
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        initResult();
    }

    private void initResult() {
        this.result = ParsedResultAdapter.qrToParsedResult((QrResult) getIntent().getParcelableExtra(QR_RESULT));
        int i = C67521.$SwitchMap$com$google$zxing$client$result$ParsedResultType[this.result.getType().ordinal()];
        if (i == 1 || i == 2 || i == 3) {
            this.btnOpen.setVisibility(8);
            this.btnSend.setVisibility(0);
            this.textSend.setText(getSendTextFor(this.result.getType()));
        } else if (i == 4 || i == 5) {
            this.btnOpen.setVisibility(0);
            this.btnSend.setVisibility(8);
            this.textOpen.setText(getOpenTextFor(this.result.getType()));
        } else {
            this.btnOpen.setVisibility(8);
            this.btnSend.setVisibility(8);
        }
        this.textView.setText(this.result.toString());
    }

    static /* synthetic */ class C67521 {
        static final /* synthetic */ int[] $SwitchMap$com$google$zxing$client$result$ParsedResultType = new int[ParsedResultType.values().length];

        static {
            try {
                $SwitchMap$com$google$zxing$client$result$ParsedResultType[ParsedResultType.SMS.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$com$google$zxing$client$result$ParsedResultType[ParsedResultType.TEL.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$com$google$zxing$client$result$ParsedResultType[ParsedResultType.EMAIL_ADDRESS.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$com$google$zxing$client$result$ParsedResultType[ParsedResultType.GEO.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$com$google$zxing$client$result$ParsedResultType[ParsedResultType.URI.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
        }
    }

    private int getOpenTextFor(ParsedResultType type) {
        if (C67521.$SwitchMap$com$google$zxing$client$result$ParsedResultType[type.ordinal()] != 4) {
            return R.string.open_url;
        }
        return R.string.open_geo;
    }

    private int getSendTextFor(ParsedResultType type) {
        if (C67521.$SwitchMap$com$google$zxing$client$result$ParsedResultType[type.ordinal()] != 3) {
            return R.string.message;
        }
        return R.string.email;
    }

    private Uri getUriForOpen(ParsedResultType type) {
        if (C67521.$SwitchMap$com$google$zxing$client$result$ParsedResultType[type.ordinal()] != 4) {
//            Analytics.get().logQrAction(AnalyticsConstants.PARAM_VALUE_OPEN_URL);
            return Uri.parse(((URIParsedResult) this.result).getURI());
        }
//        Analytics.get().logQrAction(AnalyticsConstants.PARAM_VALUE_OPEN_MAP);
        return Uri.parse(((GeoParsedResult) this.result).getGeoURI());
    }

    public void onUriClicked() {
        launchIntent(new Intent("android.intent.action.VIEW", getUriForOpen(this.result.getType())));
    }


    public void onSendClicked() {
        int i = C67521.$SwitchMap$com$google$zxing$client$result$ParsedResultType[this.result.getType().ordinal()];
        if (i == 1) {
            SMSParsedResult sms = (SMSParsedResult) this.result;
            sendSms(sms.getNumbers()[0], sms.getBody());
        } else if (i == 2) {
            sendSms(((TelParsedResult) this.result).getNumber(), "");
        } else if (i == 3) {
            sendEmail((EmailAddressParsedResult) this.result);
        }
    }

    private void sendSms(String number, String body) {
        Intent intent = new Intent("android.intent.action.SENDTO", Uri.parse("smsto:" + number));
        putExtra(intent, "sms_body", body);
        intent.putExtra("compose_mode", true);
        launchIntent(intent);
//        Analytics.get().logQrAction(AnalyticsConstants.PARAM_VALUE_SEND_SMS);
    }

    private void sendEmail(EmailAddressParsedResult result2) {
        String[] to = result2.getTos();
        String[] cc = result2.getCCs();
        String[] bcc = result2.getBCCs();
        String subject = result2.getSubject();
        String body = result2.getBody();
        Intent intent = new Intent("android.intent.action.SEND", Uri.parse("mailto:"));
        intent.setType("text/plain");
        if (!(to == null || to.length == 0)) {
            intent.putExtra("android.intent.extra.EMAIL", to);
        }
        if (!(cc == null || cc.length == 0)) {
            intent.putExtra("android.intent.extra.CC", cc);
        }
        if (!(bcc == null || bcc.length == 0)) {
            intent.putExtra("android.intent.extra.BCC", bcc);
        }
        putExtra(intent, "android.intent.extra.SUBJECT", subject);
        putExtra(intent, "android.intent.extra.TEXT", body);
        launchIntent(intent);
//        Analytics.get().logQrAction(AnalyticsConstants.PARAM_VALUE_SEND_EMAIL);
    }

    /* access modifiers changed from: package-private */
    public final void launchIntent(Intent intent) {
        if (intent != null) {
            try {
                startActivity(intent);
            } catch (ActivityNotFoundException exception) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle((int) R.string.app_name);
                builder.setMessage((int) R.string.msg_intent_failed);
                builder.setPositiveButton((int) R.string.button_ok, (DialogInterface.OnClickListener) null);
                builder.show();
            }
        }
    }

    private static void putExtra(Intent intent, String key, String value) {
        if (value != null && !value.isEmpty()) {
            intent.putExtra(key, value);
        }
    }

    public void onCopyClicked() {
        ClipboardManager clipboard = (ClipboardManager) getSystemService("clipboard");
        if (clipboard != null) {
            clipboard.setPrimaryClip(ClipData.newPlainText("text", this.result.toString()));
            Toast.makeText(getApplicationContext(), getString(R.string.copied), 0).show();
//            Analytics.get().logQrAction(AnalyticsConstants.PARAM_VALUE_COPY);
        }
    }

    public void onShareClicked() {
        Intent sharingIntent = new Intent("android.intent.action.SEND");
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra("android.intent.extra.SUBJECT", getString(R.string.recognized_qr));
        sharingIntent.putExtra("android.intent.extra.TEXT", this.result.toString());
        startActivity(Intent.createChooser(sharingIntent, getResources().getString(R.string.ocr_share)));
//        Analytics.get().logQrAction("share");
    }

    public void onBackPressed() {
        setResult(0);
        finish();
    }

    public void onListClicked() {
        BarcodeHistoryActivity.startHistoryActivity(this);
    }
}
