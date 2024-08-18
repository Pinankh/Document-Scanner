package com.camscanner.paperscanner.pdfcreator.view.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import androidx.constraintlayout.widget.ConstraintLayout;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import com.tapscanner.polygondetect.DetectionResult;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import com.camscanner.paperscanner.pdfcreator.R;
import com.camscanner.paperscanner.pdfcreator.common.Constant;
import com.camscanner.paperscanner.pdfcreator.common.cameraGalleryPicker.GalleryImage;
import com.camscanner.paperscanner.pdfcreator.common.cameraGalleryPicker.ImagesPickerManager;
import com.camscanner.paperscanner.pdfcreator.common.p018db.DBManager;
import com.camscanner.paperscanner.pdfcreator.common.p018db.DocumentRepository;
import com.camscanner.paperscanner.pdfcreator.common.utils.ExportDialogUtils;
import com.camscanner.paperscanner.pdfcreator.common.utils.FabListViewListener;
import com.camscanner.paperscanner.pdfcreator.common.utils.FileNameDialogUtils;
import com.camscanner.paperscanner.pdfcreator.common.utils.ImageStorageUtils;
import com.camscanner.paperscanner.pdfcreator.common.utils.PermissionsUtils;
import com.camscanner.paperscanner.pdfcreator.common.utils.PolygonUtil;
import com.camscanner.paperscanner.pdfcreator.common.utils.StringHelper;
import com.camscanner.paperscanner.pdfcreator.features.premium.PremiumHelper;
import com.camscanner.paperscanner.pdfcreator.model.Document;
import com.camscanner.paperscanner.pdfcreator.view.adapter.DocumentAdapter;
import com.camscanner.paperscanner.pdfcreator.view.camera.CameraOpenActivity;
import com.camscanner.paperscanner.pdfcreator.view.crop.CropImageActivity;
import com.camscanner.paperscanner.pdfcreator.view.dialog.MoveToFragmentDialog;
import com.camscanner.paperscanner.pdfcreator.view.dialog.SortDialog;
import com.camscanner.paperscanner.pdfcreator.view.element.BubbleDrawable;

public class FolderMainActivity extends BaseMainActivity implements View.OnClickListener, DocumentAdapter.DocumentAdapterCallback, SortDialog.SortDialogCallback, MoveToFragmentDialog.MoveToListener {
    static AnimatorSet mAnimationSet;
    public static boolean m_bAnimate;
    private boolean afterNewDoc;
    View btnBack;
    private ImageButton btnCreateFolder;
    private ImageButton btnDelete;
    private ImageButton btnSearch;
    private ImageButton btnSort;
    private ImageButton btnTag;
    private ImageView ivEmptyBG;
    private String lastName;
    private String lastParent;
    private String mName;
    /* access modifiers changed from: private */
    public String mParent;
    /* access modifiers changed from: private */
    public DocumentAdapter m_adapter;
    /* access modifiers changed from: private */
    public List<Document> m_arrData;
    private boolean m_bActivityChanged;
    /* access modifiers changed from: private */
    public boolean m_bMultiMode;
    /* access modifiers changed from: private */
    public boolean m_bSelectedAll;
    private ImageView m_btnCamera;
    private ImageView m_btnGallery;
    /* access modifiers changed from: private */
    public ImageView m_ivSelectAll;
    private LinearLayout m_llSecondBar;
    /* access modifiers changed from: private */
    public ListView m_lvDocument;
    private ConstraintLayout m_rlBottomBar;
    private RelativeLayout m_rlMultiBar;
    private ViewGroup m_rlMultiBottomBar;
    Constant.SORT_TYPE m_sortType;
    /* access modifiers changed from: private */
    public TextView m_tvMultiSelect;
    /* access modifiers changed from: private */
    public MaterialSearchView searchView;
    TextView title;
    Toolbar toolbar;
    private TextView tvStartScan;

    public void onCreatedDateUp() {
        if (this.m_sortType != Constant.SORT_TYPE.CREATE_UP) {
            this.m_sortType = Constant.SORT_TYPE.CREATE_UP;
            loadDB();
            this.m_adapter.notifyDataSetChanged();
        }
    }

    public void onCreatedDateDown() {
        if (this.m_sortType != Constant.SORT_TYPE.CREATE_DOWN) {
            this.m_sortType = Constant.SORT_TYPE.CREATE_DOWN;
            loadDB();
            this.m_adapter.notifyDataSetChanged();
        }
    }

    public void onNameA2Z() {
        if (this.m_sortType != Constant.SORT_TYPE.NAMEA2Z) {
            this.m_sortType = Constant.SORT_TYPE.NAMEA2Z;
            loadDB();
            this.m_adapter.notifyDataSetChanged();
        }
    }

    public void onNameZ2A() {
        if (this.m_sortType != Constant.SORT_TYPE.NAMEZ2A) {
            this.m_sortType = Constant.SORT_TYPE.NAMEZ2A;
            loadDB();
            this.m_adapter.notifyDataSetChanged();
        }
    }

    public void onRenameClick(int pos) {
        showRenameDlg(this.m_arrData.get(pos));
    }

    public void onRemoveClick() {
        if (this.m_arrData.size() > 0) {
            this.ivEmptyBG.setVisibility(4);
            this.tvStartScan.setVisibility(4);
            m_bAnimate = false;
            removeAlphaAnimation(this.m_btnCamera);
            return;
        }
        this.ivEmptyBG.setVisibility(0);
        this.tvStartScan.setVisibility(0);
        m_bAnimate = true;
        setAlphaAnimation(this.m_btnCamera);
    }

    /* access modifiers changed from: package-private */
    public void showRenameDlg(final Document doc) {
        FileNameDialogUtils.showFileNameDialog(this, doc.name, getString(R.string.str_rename), getString(R.string.change_group_name), new FileNameDialogUtils.OnRenameListener() {


            public final void nameChanged(String str) {
                FolderMainActivity.this.lambda$showRenameDlg$0$FolderActivity(doc, str);
            }
        });
    }

    public /* synthetic */ void lambda$showRenameDlg$0$FolderActivity(Document doc, String newName) {
        if (StringHelper.isEmpty(newName)) {
            Toast.makeText(getApplication(), getString(R.string.alert_file_name_empty), 0).show();
            return;
        }
        doc.name = newName;
        DBManager.getInstance().updateDocument(doc);
        this.m_adapter.notifyDataSetChanged();
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_folder);

        // TODO: 04-02-2022 comment by peenalkumar

       // Get_All_ID.LoadData_Activity( FolderMainActivity.this, getPackageName() );

        initVariable();
        initUI();
    }

    /* access modifiers changed from: package-private */
    public void initVariable() {
        this.mParent = getIntent().getStringExtra(Constant.EXTRA_PARENT);
        this.mName = getIntent().getStringExtra("name");
        this.m_arrData = new ArrayList();
        this.m_sortType = Constant.SORT_TYPE.CREATE_DOWN;
        this.m_bMultiMode = false;
        this.m_bSelectedAll = false;
        m_bAnimate = false;
        this.m_bActivityChanged = false;
        loadDB();
        this.m_adapter = new DocumentAdapter(this, this.m_arrData);
        this.m_adapter.callback = this;
    }

    /* access modifiers changed from: package-private */
    public void loadDB() {
        this.m_arrData.clear();
        if (this.m_sortType == Constant.SORT_TYPE.CREATE_UP) {
            DBManager.getInstance().getDocumentsCreatedUp(this.m_arrData, this.mParent, -1, true);
            DBManager.getInstance().getDocumentsCreatedUp(this.m_arrData, this.mParent, -1, false);
        } else if (this.m_sortType == Constant.SORT_TYPE.CREATE_DOWN) {
            DBManager.getInstance().getDocumentsCreatedDown(this.m_arrData, this.mParent, -1, true);
            DBManager.getInstance().getDocumentsCreatedDown(this.m_arrData, this.mParent, -1, false);
        } else if (this.m_sortType == Constant.SORT_TYPE.NAMEA2Z) {
            DBManager.getInstance().getDocumentsNameA2Z(this.m_arrData, this.mParent, -1, true);
            DBManager.getInstance().getDocumentsNameA2Z(this.m_arrData, this.mParent, -1, false);
        } else if (this.m_sortType == Constant.SORT_TYPE.NAMEZ2A) {
            DBManager.getInstance().getDocumentsNameZ2A(this.m_arrData, this.mParent, -1, true);
            DBManager.getInstance().getDocumentsNameZ2A(this.m_arrData, this.mParent, -1, false);
        }
        for (int i = 0; i < this.m_arrData.size(); i++) {
            this.m_arrData.get(i).m_bShowButtons = !this.m_bMultiMode;
        }
    }

    public void onBackPressed() {
        if (this.m_bMultiMode) {
            setMultiMode(false);
        } else {
            super.onBackPressed();
        }
    }

    /* access modifiers changed from: package-private */
    public void initUI() {
        this.toolbar = (Toolbar) findViewById(R.id.toolbar);
        this.title = (TextView) findViewById(R.id.title);
        this.btnBack = findViewById(R.id.btn_menu);
        setSupportActionBar(this.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        this.title.setText(this.mName);
        this.btnBack.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                FolderMainActivity.this.lambda$initUI$1$FolderActivity(view);
            }
        });
        this.m_lvDocument = (ListView) findViewById(R.id.document_list);
        this.m_lvDocument.setAdapter(this.m_adapter);
        this.searchView = (MaterialSearchView) findViewById(R.id.search_view);
        this.searchView.setVoiceSearch(false);
        this.searchView.setCursorDrawable(R.drawable.custom_cursor);
        this.searchView.setEllipsize(true);
        this.searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            public boolean onQueryTextSubmit(String query) {
                return true;
            }

            public boolean onQueryTextChange(String newText) {
                if (!newText.isEmpty()) {
                    FolderMainActivity.this.searchDoc(newText);
                    return false;
                }
                FolderMainActivity.this.loadDB();
                FolderMainActivity.this.m_adapter.notifyDataSetChanged();
                return false;
            }
        });
        this.searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            public void onSearchViewShown() {
            }

            public void onSearchViewClosed() {
                FolderMainActivity.this.loadDB();
                FolderMainActivity.this.m_adapter.notifyDataSetChanged();
            }
        });
        this.m_lvDocument.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Document doc = (Document) FolderMainActivity.this.m_arrData.get(i);
                if (FolderMainActivity.this.m_bMultiMode) {
                    doc.m_bSelected = !doc.m_bSelected;
                    FolderMainActivity.this.m_adapter.notifyDataSetChanged();
                    int cnt = FolderMainActivity.this.getSelectedCount();
                    FolderMainActivity.this.m_tvMultiSelect.setText(String.format("%d %s", new Object[]{Integer.valueOf(cnt), FolderMainActivity.this.getString(R.string.str_selected)}));
                    if (cnt == FolderMainActivity.this.m_arrData.size()) {
                        FolderMainActivity.this.m_ivSelectAll.setImageResource(R.drawable.icon_toolbar_check_on);
                        boolean unused = FolderMainActivity.this.m_bSelectedAll = true;
                        return;
                    }
                    FolderMainActivity.this.m_ivSelectAll.setImageResource(R.drawable.icon_toolbar_check_off);
                    boolean unused2 = FolderMainActivity.this.m_bSelectedAll = false;
                    return;
                }
                FolderMainActivity.this.m_lvDocument.setChoiceMode(1);
                if (!doc.isLocked) {
                    FolderMainActivity.this.searchView.hideKeyboard(view);
                    FolderMainActivity.this.openFile(doc);
                    return;
                }
                Toast.makeText(FolderMainActivity.this, R.string.alert_locked_file, 0).show();
            }
        });
        this.m_lvDocument.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                Document doc = (Document) FolderMainActivity.this.m_arrData.get(i);
                if (!FolderMainActivity.this.m_bMultiMode) {
                    FolderMainActivity.this.setMultiMode(true);
                    doc.m_bSelected = !doc.m_bSelected;
                    FolderMainActivity.this.m_adapter.notifyDataSetChanged();
                    int cnt = FolderMainActivity.this.getSelectedCount();
                    FolderMainActivity.this.m_tvMultiSelect.setText(String.format("%d %s", new Object[]{Integer.valueOf(cnt), FolderMainActivity.this.getString(R.string.str_selected)}));
                    if (cnt == FolderMainActivity.this.m_arrData.size()) {
                        FolderMainActivity.this.m_ivSelectAll.setImageResource(R.drawable.icon_toolbar_check_on);
                        boolean unused = FolderMainActivity.this.m_bSelectedAll = true;
                    } else {
                        FolderMainActivity.this.m_ivSelectAll.setImageResource(R.drawable.icon_toolbar_check_off);
                        boolean unused2 = FolderMainActivity.this.m_bSelectedAll = false;
                    }
                }
                return true;
            }
        });
        this.m_rlBottomBar = (ConstraintLayout) findViewById(R.id.rlBottom);
        this.m_btnCamera = (ImageView) findViewById(R.id.btn_camera);
        this.m_btnGallery = (ImageView) findViewById(R.id.btn_gallery);
        this.m_btnCamera.setOnClickListener(this);
        this.m_btnGallery.setOnClickListener(this);
        this.m_llSecondBar = (LinearLayout) findViewById(R.id.secondbar);
        this.m_rlMultiBar = (RelativeLayout) findViewById(R.id.multi_select_bar);
        this.m_lvDocument.setOnScrollListener(FabListViewListener.create(this.m_rlBottomBar, R.dimen.fab_margin));
        this.m_rlMultiBar.setVisibility(4);
        this.m_rlMultiBottomBar = (ViewGroup) findViewById(R.id.multi_select_bottom_bar);
        this.m_rlMultiBottomBar.setVisibility(8);
        this.m_rlMultiBar.findViewById(R.id.btn_bar_back).setOnClickListener(this);
        this.m_rlMultiBar.findViewById(R.id.btn_bar_select_all).setOnClickListener(this);
        this.m_rlMultiBar.findViewById(R.id.rl_tv_bar_select_all).setOnClickListener(this);
        this.m_rlMultiBottomBar.findViewById(R.id.btn_bar_delete).setOnClickListener(this);
        this.m_rlMultiBottomBar.findViewById(R.id.btn_bar_move).setOnClickListener(this);
        this.m_rlMultiBottomBar.findViewById(R.id.btn_bar_share).setOnClickListener(this);
        this.m_rlMultiBottomBar.findViewById(R.id.btn_bar_tag).setOnClickListener(this);
        this.m_ivSelectAll = (ImageView) findViewById(R.id.iv_bar_select_all);
        this.m_tvMultiSelect = new TextView(this);
        TextView textView = this.m_tvMultiSelect;
        textView.setText("0 " + getString(R.string.str_selected));
        this.m_tvMultiSelect.setTextColor(getResources().getColor(R.color.colorWhite));
        this.m_tvMultiSelect.setTextSize(2, 16.0f);
        this.m_tvMultiSelect.setGravity(17);
        RelativeLayout.LayoutParams layoutParams1 = new RelativeLayout.LayoutParams(-2, -2);
        Resources resources = getResources();
        layoutParams1.leftMargin = 0;
        layoutParams1.addRule(1, R.id.btn_bar_back);
        layoutParams1.addRule(15);
        this.m_tvMultiSelect.setLayoutParams(layoutParams1);
        this.m_rlMultiBar.addView(this.m_tvMultiSelect);
        TextView tvSelectAll = new TextView(this);
        tvSelectAll.setText(getString(R.string.str_select_all));
        tvSelectAll.setTextColor(getResources().getColor(R.color.colorWhite));
        tvSelectAll.setTextSize(2, 16.0f);
        RelativeLayout.LayoutParams layoutParams2 = new RelativeLayout.LayoutParams(-2, -2);
        layoutParams2.addRule(13);
        tvSelectAll.setLayoutParams(layoutParams2);
        ((RelativeLayout) this.m_rlMultiBar.findViewById(R.id.rl_tv_bar_select_all)).addView(tvSelectAll);
        this.ivEmptyBG = (ImageView) findViewById(R.id.ivEmptyBG);
        this.tvStartScan = (TextView) findViewById(R.id.tvStartScan);
        if (this.m_arrData.size() > 0) {
            this.ivEmptyBG.setVisibility(4);
            this.tvStartScan.setVisibility(4);
            m_bAnimate = false;
            removeAlphaAnimation(this.m_btnCamera);
        } else {
            this.ivEmptyBG.setVisibility(0);
            this.tvStartScan.setVisibility(0);
            m_bAnimate = true;
            setAlphaAnimation(this.m_btnCamera);
        }
        this.btnSort = (ImageButton) findViewById(R.id.btn_sort);
        this.btnTag = (ImageButton) findViewById(R.id.btn_tag);
        this.btnCreateFolder = (ImageButton) findViewById(R.id.btn_create_folder);
        this.btnSearch = (ImageButton) findViewById(R.id.btn_search);
        this.btnDelete = (ImageButton) findViewById(R.id.btn_multi);
        this.btnSort.setOnClickListener(this);
        this.btnTag.setOnClickListener(this);
        this.btnCreateFolder.setVisibility(8);
        this.btnSearch.setOnClickListener(this);
        this.btnDelete.setOnClickListener(this);
        BubbleDrawable myBubble = new BubbleDrawable(this);
        myBubble.setCornerRadius(4.0f);
        myBubble.setPointerAlignment(2);
        myBubble.setPadding(25, 25, 25, 30);
        this.tvStartScan.setBackgroundDrawable(myBubble);
    }

    public /* synthetic */ void lambda$initUI$1$FolderActivity(View v) {
        onBackPressed();
    }

    /* access modifiers changed from: package-private */
    public void searchDoc(String keyWord) {
        this.m_arrData.clear();
        DBManager.getInstance().searchFolderDocuments(this.m_arrData, this.mParent, keyWord);
        this.m_adapter.notifyDataSetChanged();
    }

    /* access modifiers changed from: package-private */
    public void openFile(final Document doc) {
        if (doc.isDir) {
            this.mParent = doc.uid;
            loadDB();
            this.m_adapter.notifyDataSetChanged();
            return;
        }
        PermissionsUtils.askPermissions(this, new PermissionsUtils.PermissionListener() {


            public final void onGranted() {
                FolderMainActivity.this.lambda$openFile$2$FolderActivity(doc);
            }
        }, PermissionsUtils.STORAGE_PERMISSIONS);
    }

    /* access modifiers changed from: private */
    /* renamed from: openGridScreen */
    public void lambda$openFile$2$FolderActivity(Document doc) {
        Intent intent = new Intent(this, GridModeLayoutActivity.class);
        Constant.m_strParent = doc.uid;
        intent.putExtra(Constant.EXTRA_PARENT, doc.uid);
        intent.putExtra("name", doc.name);
        intent.putExtra(GridModeLayoutActivity.SHOW_RATE_US_KEY, true);
        startActivityForResult(intent, 1006);
    }

    /* access modifiers changed from: package-private */
    public void autoOpenFile(String Parent1, String Name1) {
        Intent intent = new Intent(this, GridModeLayoutActivity.class);
        intent.putExtra(Constant.EXTRA_PARENT, Parent1);
        intent.putExtra("name", Name1);
        startActivityForResult(intent, 1006);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == 16908332) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void startCamera() {
        Intent intent = new Intent(this, CameraOpenActivity.class);
        intent.putExtra(Constant.EXTRA_PARENT, this.mParent);
        startActivityForResult(intent, 1003);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_bar_back:
                setMultiMode(false);
                return;
            case R.id.btn_bar_delete:
                multiDelete();
                return;
            case R.id.btn_bar_move:
                if (getDocsSelectedCount() > 0) {
                    showMoveToFolderDlg();
                    return;
                }
                return;
            case R.id.btn_bar_select_all:
            case R.id.rl_tv_bar_select_all:
                selectAll();
                return;
            case R.id.btn_bar_share:
                showShareDialog();
                return;
            case R.id.btn_bar_tag:
            case R.id.btn_tag:
            default:
                return;
            case R.id.btn_camera:
                PermissionsUtils.askPermissions(this, new PermissionsUtils.PermissionListener() {
                    public final void onGranted() {
                        FolderMainActivity.this.startCamera();
                    }
                }, PermissionsUtils.TAKE_SAVE_PICTURE_PERMISSIONS);
                return;
            case R.id.btn_gallery:
                PermissionsUtils.askPermissions(this, new PermissionsUtils.PermissionListener() {
                    public final void onGranted() {
                        FolderMainActivity.this.lambda$onClick$3$FolderActivity();
                    }
                }, PermissionsUtils.STORAGE_PERMISSIONS);
                return;
            case R.id.btn_multi:
                setMultiMode(true);
                return;
            case R.id.btn_search:
                this.searchView.showSearch();
                return;
            case R.id.btn_sort:
                showSortDialog();
                return;
        }
    }

    public /* synthetic */ void lambda$onClick$3$FolderActivity() {
        ImagesPickerManager.startPicker((Activity) this);
    }

    /* access modifiers changed from: package-private */
    public void showMoveToFolderDlg() {
        MoveToFragmentDialog.newInstance().setListener(this).setExclude(this.mParent).showDialog(this);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        this.m_bActivityChanged = true;
        saveBatchModeWithGallery(ImagesPickerManager.handlePickerMultiResults(requestCode, resultCode, data));
        if (requestCode != 1002) {
            if (requestCode != 1003) {
                if (requestCode != 1006) {
                    if (requestCode == 1010) {
                        ImageStorageUtils.clearShareFolder();
                    }
                } else if (resultCode == -1) {
                    loadDB();
                    this.m_adapter.notifyDataSetChanged();
                }
            } else if (resultCode == 5) {
                Toast.makeText(this, R.string.error_can_not_take_image, 1).show();
            } else if (resultCode != -1) {
            } else {
                if (data.getFlags() == 1) {
                    Document docContainer = PolygonUtil.InsertBatchData(this, (List) new Gson().fromJson(data.getStringExtra(Constant.EXTRA_DOC_ARRAY), new TypeToken<List<Document>>() {
                    }.getType()), this.mParent);
                    if (docContainer != null) {
                        autoOpenFile(docContainer.uid, docContainer.name);
                        return;
                    }
                    return;
                }
                CropImageActivity.cropNewFile(this, DetectionResult.FIX_RECT_MODE.FIX_RECT_CAMERA, this.mParent, false);
            }
        } else if (resultCode == -1) {
            String mParent2 = data.getExtras().getString(Constant.EXTRA_MPARENT);
            String mName2 = data.getExtras().getString(Constant.EXTRA_MNAME);
            autoOpenFile(mParent2, mName2);

//            if (!ScanApplication.adsManager.show(false)) {
//                autoOpenFile(mParent2, mName2);
//                return;
//            }
            this.afterNewDoc = true;
            this.lastParent = mParent2;
            this.lastName = mName2;
        }
    }

    /* access modifiers changed from: package-private */
    public void saveBatchModeWithGallery(final ArrayList<GalleryImage> galleryImages) {
        int cnt;
        if (galleryImages != null && (cnt = galleryImages.size()) != 0) {
            if (cnt == 1) {
                PolygonUtil.importGalleryImage(this, galleryImages.get(0).getBitmapUri());
                CropImageActivity.cropNewFile(this, DetectionResult.FIX_RECT_MODE.FIX_RECT_GALLERY, this.mParent, false);
            } else if (cnt > 1) {
                final ProgressDialog dlgProgress = new ProgressDialog(this);
                dlgProgress.setMessage(getString(R.string.str_saving_processing));
                dlgProgress.setCancelable(false);
                dlgProgress.show();
                new Thread(new Runnable() {
                    public void run() {
                        ArrayList<Document> docBatchList = new ArrayList<>();
                        Iterator it = galleryImages.iterator();
                        while (it.hasNext()) {
                            PolygonUtil.copyImageFromGalleryWithAutoCrop(FolderMainActivity.this, ((GalleryImage) it.next()).getBitmapUri(), docBatchList);
                        }
                        FolderMainActivity folderActivity = FolderMainActivity.this;
                        final Document docContainer = PolygonUtil.InsertBatchData(folderActivity, docBatchList, folderActivity.mParent);
                        FolderMainActivity.this.runOnUiThread(new Runnable() {
                            public void run() {
                                dlgProgress.dismiss();
                                Document document = docContainer;
                                if (document != null) {
                                    FolderMainActivity.this.autoOpenFile(document.uid, docContainer.name);
                                }
                            }
                        });
                    }
                }).start();
            }
        }
    }

    public void onStart() {
        super.onStart();
        checkPermissionForDocs();
    }

    private void checkPermissionForDocs() {
        if (this.m_arrData.size() > 0 && ContextCompat.checkSelfPermission(this, PermissionsUtils.STORAGE_PERMISSIONS) != 0) {
            PermissionsUtils.askPermissions(this, new PermissionsUtils.PermissionListener() {
                public final void onGranted() {
                    FolderMainActivity.this.lambda$checkPermissionForDocs$4$FolderActivity();
                }
            }, PermissionsUtils.STORAGE_PERMISSIONS);
        }
    }

    public /* synthetic */ void lambda$checkPermissionForDocs$4$FolderActivity() {
        this.m_adapter.notifyDataSetChanged();
    }

    public void onResume() {
        super.onResume();
        if (this.m_bActivityChanged) {
            loadDB();
            this.m_adapter.notifyDataSetChanged();
            this.m_bActivityChanged = false;
        }
        if (this.m_arrData.size() > 0) {
            this.ivEmptyBG.setVisibility(4);
            this.tvStartScan.setVisibility(View.INVISIBLE);
            m_bAnimate = false;
            removeAlphaAnimation(this.m_btnCamera);
            return;
        }
        this.ivEmptyBG.setVisibility(0);
        this.tvStartScan.setVisibility(0);
        if (!m_bAnimate) {
            m_bAnimate = true;
            setAlphaAnimation(this.m_btnCamera);
        }
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        super.onDestroy();
//        ScanApplication.adsManager.removeListener(this);
        m_bAnimate = false;
        this.m_btnCamera.clearAnimation();
        this.m_btnCamera.animate().cancel();
        AnimatorSet animatorSet = mAnimationSet;
        if (animatorSet != null) {
            animatorSet.cancel();
            mAnimationSet.removeAllListeners();
            mAnimationSet = null;
        }
        mAnimationSet = null;
    }

    /* access modifiers changed from: package-private */
    public void multiDelete() {
        if (getSelectedCount() != 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle((CharSequence) getString(R.string.str_delete));
            builder.setMessage((int) R.string.msg_delete);
            builder.setPositiveButton((int) R.string.str_yes, (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    for (int i = 0; i < FolderMainActivity.this.m_arrData.size(); i++) {
                        Document doc = (Document) FolderMainActivity.this.m_arrData.get(i);
                        if (doc.m_bSelected) {
                            DocumentRepository.removeDocumentWithChilds(FolderMainActivity.this, doc);
                        }
                    }
                    FolderMainActivity.this.loadDB();
                    FolderMainActivity.this.m_adapter.notifyDataSetChanged();
                    FolderMainActivity.this.onRemoveClick();
                    FolderMainActivity.this.m_tvMultiSelect.setText(String.format("%d %s", new Object[]{Integer.valueOf(FolderMainActivity.this.getSelectedCount()), FolderMainActivity.this.getString(R.string.str_selected)}));
                    if (FolderMainActivity.this.m_arrData.size() == 0) {
                        FolderMainActivity.this.setMultiMode(false);
                    }
                    dialog.dismiss();
                }
            });
            builder.setNegativeButton((int) R.string.str_cancel, (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.setCancelable(false);
            builder.show();
        }
    }

    /* access modifiers changed from: package-private */
    public void selectAll() {
        this.m_bSelectedAll = !this.m_bSelectedAll;
        if (this.m_bSelectedAll) {
            for (int i = 0; i < this.m_arrData.size(); i++) {
                this.m_lvDocument.setItemChecked(i, true);
                this.m_arrData.get(i).m_bSelected = true;
            }
            this.m_ivSelectAll.setImageResource(R.drawable.icon_toolbar_check_on);
        } else {
            for (int i2 = 0; i2 < this.m_arrData.size(); i2++) {
                this.m_lvDocument.setItemChecked(i2, false);
                this.m_arrData.get(i2).m_bSelected = false;
            }
            this.m_ivSelectAll.setImageResource(R.drawable.icon_toolbar_check_off);
        }
        this.m_tvMultiSelect.setText(String.format("%d %s", new Object[]{Integer.valueOf(getSelectedCount()), getString(R.string.str_selected)}));
        this.m_adapter.notifyDataSetChanged();
    }

    public void setMultiMode(boolean multiMode) {
        if (!multiMode) {
            this.m_rlMultiBar.setVisibility(4);
            this.m_rlMultiBottomBar.setVisibility(8);
            this.m_llSecondBar.setVisibility(0);
            this.m_rlBottomBar.setVisibility(0);
            this.toolbar.setVisibility(View.VISIBLE);
            this.m_bSelectedAll = false;
            this.m_ivSelectAll.setImageResource(R.drawable.icon_toolbar_check_off);
            this.m_lvDocument.setChoiceMode(1);
            this.m_bMultiMode = false;
            for (int i = 0; i < this.m_arrData.size(); i++) {
                this.m_arrData.get(i).m_bSelected = false;
            }
            this.m_tvMultiSelect.setText(String.format("%d %s", new Object[]{Integer.valueOf(getSelectedCount()), getString(R.string.str_selected)}));
        } else if (this.m_arrData.size() > 0) {
            this.m_rlMultiBar.setVisibility(0);
            this.m_rlMultiBottomBar.setVisibility(0);
            this.m_llSecondBar.setVisibility(8);
            this.m_rlBottomBar.setVisibility(8);
            this.toolbar.setVisibility(4);
            this.m_lvDocument.setChoiceMode(2);
            this.m_bMultiMode = true;
        }
        for (int i2 = 0; i2 < this.m_arrData.size(); i2++) {
            this.m_arrData.get(i2).m_bShowButtons = !this.m_bMultiMode;
        }
        this.m_adapter.notifyDataSetChanged();
    }

    public static void setAlphaAnimation(final View v) {
        ObjectAnimator fadeOut = ObjectAnimator.ofFloat(v, "alpha", new float[]{1.0f, 0.5f});
        fadeOut.setDuration(1000);
        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(v, "alpha", new float[]{0.5f, 1.0f});
        fadeIn.setDuration(1000);
        AnimatorSet animatorSet = mAnimationSet;
        if (animatorSet != null) {
            animatorSet.cancel();
            mAnimationSet.removeAllListeners();
            mAnimationSet = null;
        }
        mAnimationSet = null;
        mAnimationSet = new AnimatorSet();
        mAnimationSet.play(fadeOut).after(fadeIn);
        mAnimationSet.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                try {
                    if (FolderMainActivity.m_bAnimate) {
                        FolderMainActivity.mAnimationSet.start();
                    } else {
                        v.setAlpha(1.0f);
                    }
                } catch (StackOverflowError e) {
                }
            }
        });
        mAnimationSet.start();
    }

    public static void removeAlphaAnimation(View v) {
        v.clearAnimation();
        v.animate().cancel();
    }

    public void showSortDialog() {
        new SortDialog(this, getLayoutInflater().inflate(R.layout.sort_listview, (ViewGroup) null), this, this.m_sortType).show();
    }

    public int getSelectedCount() {
        int cnt = 0;
        for (int i = 0; i < this.m_arrData.size(); i++) {
            if (this.m_arrData.get(i).m_bSelected) {
                cnt++;
            }
        }
        return cnt;
    }

    public int getDocsSelectedCount() {
        int cnt = 0;
        for (int i = 0; i < this.m_arrData.size(); i++) {
            if (this.m_arrData.get(i).m_bSelected && !this.m_arrData.get(i).isDir) {
                cnt++;
            }
        }
        return cnt;
    }

    public void onFolderSelected(Document folder) {
        if (folder.uid.equals(Document.CREATE_FOLDER_UID)) {
            showOnCreateFolderDlg();
        } else {
            moveSelectedDocsToTarget(folder);
        }
    }

    public void moveSelectedDocsToTarget(Document docTarget) {
        for (int i = 0; i < this.m_arrData.size(); i++) {
            Document document = this.m_arrData.get(i);
            if (!document.isDir && document.m_bSelected) {
                document.parent = docTarget.uid;
                DBManager.getInstance().updateDocument(document);
            }
        }
        this.m_arrData.clear();
        loadDB();
        this.m_adapter.notifyDataSetChanged();
        setMultiMode(false);
    }

    /* access modifiers changed from: package-private */
    public void showOnCreateFolderDlg() {
        if ( DBManager.getInstance().getFolderCount() < 5) {
            FileNameDialogUtils.showFileNameDialog(this, "", "", getString(R.string.create_new_folder), new FileNameDialogUtils.OnRenameListener() {
                public final void nameChanged(String str) {
                    FolderMainActivity.this.lambda$showOnCreateFolderDlg$5$FolderActivity(str);
                }
            });
        } else {
            PremiumHelper.showPremiumAfterAlertDialog(this, R.string.alert_premium_folders_title, R.string.alert_premium_folders_message, new PremiumHelper.StartActivityController() {
                public final void startActivity(Intent intent, int i) {
                    FolderMainActivity.this.startActivityForResult(intent, i);
                }
            });
        }
    }

    public /* synthetic */ void lambda$showOnCreateFolderDlg$5$FolderActivity(String newName) {
        if (StringHelper.isEmpty(newName)) {
            Toast.makeText(this, getString(R.string.alert_folder_name_emptry), 0).show();
            return;
        }
        Document doc = new Document("");
        doc.isDir = true;
        doc.name = newName;
        doc.path = "";
        doc.date = new Date().getTime();
        doc.thumb = "";
        DBManager.getInstance().addDocument(doc);
        moveSelectedDocsToTarget(doc);
    }

    /* access modifiers changed from: package-private */
    public void showShareDialog() {
        if (getSelectedCount() > 0) {
            List<Document> toShare = new ArrayList<>();
            for (Document document : this.m_arrData) {
                if (document.m_bSelected) {
                    toShare.add(document);
                }
            }
            ExportDialogUtils.showShareDialogWithDirs(this, toShare);
        }
    }

    public void onAdLoaded() {
    }

    public void onAdFailed() {
    }

    public void onAdOpened(String s) {
    }

    public void onAdClicked(String s) {
    }

    public void onAdClosed() {
        if (this.afterNewDoc && !TextUtils.isEmpty(this.lastParent) && !TextUtils.isEmpty(this.lastName)) {
            autoOpenFile(this.lastParent, this.lastName);
        }
        this.afterNewDoc = false;
        this.lastParent = null;
        this.lastName = null;
    }
}
