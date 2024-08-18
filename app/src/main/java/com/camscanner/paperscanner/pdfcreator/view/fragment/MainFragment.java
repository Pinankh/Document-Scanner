package com.camscanner.paperscanner.pdfcreator.view.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.camscanner.paperscanner.pdfcreator.ads.InterstialListner;
import com.camscanner.paperscanner.pdfcreator.ads.InterstitialHelper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import com.tapscanner.polygondetect.DetectionResult;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.camscanner.paperscanner.pdfcreator.R;
import com.camscanner.paperscanner.pdfcreator.common.Constant;
import com.camscanner.paperscanner.pdfcreator.common.cameraGalleryPicker.GalleryImage;
import com.camscanner.paperscanner.pdfcreator.common.cameraGalleryPicker.ImagesPickerManager;
import com.camscanner.paperscanner.pdfcreator.common.p018db.DBManager;
import com.camscanner.paperscanner.pdfcreator.common.p018db.DocumentRepository;
import com.camscanner.paperscanner.pdfcreator.common.utils.ActivityUtils;
import com.camscanner.paperscanner.pdfcreator.common.utils.ExportDialogUtils;
import com.camscanner.paperscanner.pdfcreator.common.utils.FabListViewListener;
import com.camscanner.paperscanner.pdfcreator.common.utils.FileNameDialogUtils;
import com.camscanner.paperscanner.pdfcreator.common.utils.ImageStorageUtils;
import com.camscanner.paperscanner.pdfcreator.common.utils.PermissionsUtils;
import com.camscanner.paperscanner.pdfcreator.common.utils.PolygonUtil;
import com.camscanner.paperscanner.pdfcreator.common.utils.SharedPrefsUtils;
import com.camscanner.paperscanner.pdfcreator.common.utils.StringHelper;
import com.camscanner.paperscanner.pdfcreator.features.barcode.presentation.BarcodeScannerActivity;
import com.camscanner.paperscanner.pdfcreator.features.premium.PremiumHelper;
import com.camscanner.paperscanner.pdfcreator.features.tutorial.TutorialManager;
import com.camscanner.paperscanner.pdfcreator.features.tutorial.model.TutorialInfo;
import com.camscanner.paperscanner.pdfcreator.model.Document;
import com.camscanner.paperscanner.pdfcreator.view.activity.FolderMainActivity;
import com.camscanner.paperscanner.pdfcreator.view.activity.GridModeLayoutActivity;
import com.camscanner.paperscanner.pdfcreator.view.adapter.DocumentAdapter;
import com.camscanner.paperscanner.pdfcreator.view.camera.CameraOpenActivity;
import com.camscanner.paperscanner.pdfcreator.view.crop.CropImageActivity;
import com.camscanner.paperscanner.pdfcreator.view.dialog.MoveToFragmentDialog;
import com.camscanner.paperscanner.pdfcreator.view.dialog.SortDialog;
import com.camscanner.paperscanner.pdfcreator.view.element.BubbleDrawable;

public class MainFragment extends Fragment implements View.OnClickListener, DocumentAdapter.DocumentAdapterCallback, SortDialog.SortDialogCallback, MoveToFragmentDialog.MoveToListener, TutorialManager.OnTutorialListener {
    /* access modifiers changed from: private */
    public static AnimatorSet mAnimationSet;
    public static boolean m_bAnimate;

    View btnBottomDelete;

    View btnBottomMove;

    View btnBottomShare;

    View btnCamera;

    ConstraintLayout fabGroup;
    private Context mContext;
    private DocumentAdapter mDocAdapter;
    private List<Document> m_arrData;
    public boolean m_bActivityChanged;
    private boolean m_bMultiMode;
    private boolean m_bSelectedAll;
    private Filter m_filter;

    ImageView m_ivEmptyBG;
    private ImageView m_ivSelectAll;

    ListView m_lvDocument;
    Constant.SORT_TYPE m_sortType;
    private TextView m_tvMultiSelect;

    TextView m_tvStartScan;
    private int mainOpened = 0;

    ViewGroup multiBottomBar;
    private RelativeLayout multiTopBar;
    private MaterialSearchView searchView;
    private Toolbar toolbar;

    LinearLayout toolbarSecond;


    private enum Filter {
        ALL,
        MARKED_DOC
    }

    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        initVariable();

        btnBottomDelete = (View)view.findViewById(R.id.btn_bar_delete);
        btnBottomMove = (View)view.findViewById(R.id.btn_bar_move);
        btnBottomShare = (View)view.findViewById(R.id.btn_bar_share);
        btnCamera = (View)view.findViewById(R.id.btn_camera);
        fabGroup = (ConstraintLayout)view.findViewById(R.id.rlBottom);
        m_ivEmptyBG = (ImageView) view.findViewById(R.id.ivEmptyBG);
        m_lvDocument = (ListView) view.findViewById(R.id.document_list);
        m_tvStartScan = (TextView) view.findViewById(R.id.tvStartScan);
        multiBottomBar = (ViewGroup) view.findViewById(R.id.multi_select_bottom_bar);
        toolbarSecond = (LinearLayout) view.findViewById(R.id.secondbar);
        ImageButton btn_create_folder = (ImageButton) view.findViewById(R.id.btn_create_folder);
        ImageButton    btn_multi = (ImageButton) view.findViewById(R.id.btn_multi);
        ImageButton    btn_sort = (ImageButton) view.findViewById(R.id.btn_sort);
        ImageView btn_gallery = (ImageView) view.findViewById(R.id.btn_gallery);
        ImageView btn_qr = (ImageView) view.findViewById(R.id.btn_qr);
        ImageView btn_search = (ImageView) view.findViewById(R.id.btn_search);

        btnBottomDelete.setOnClickListener(this);
        btnBottomMove.setOnClickListener(this);
        btnBottomShare.setOnClickListener(this);
        btnCamera.setOnClickListener(this);
        btn_create_folder.setOnClickListener(this);
        btn_gallery.setOnClickListener(this);
        btn_qr.setOnClickListener(this);
        btn_multi.setOnClickListener(this);
        btn_search.setOnClickListener(this);
        btn_sort.setOnClickListener(this);

        initUI(view);
        showOverlay();
        return view;
    }

    private void showOverlay() {
        if (this.mainOpened == 1) {
            ActivityUtils.waitVisibleFor(new ActivityUtils.OnVisibleChecker() {
                public final boolean isVisible() {
                    return MainFragment.this.isCameraReady();
                }
            }, new ActivityUtils.OnVisibleListener() {
                public final void onVisible() {
                    MainFragment.this.showCameraTutorial();
                }
            });
        }
    }

    /* access modifiers changed from: private */
    public boolean isCameraReady() {
        View view = this.btnCamera;
        return (view == null || view.getWidth() == 0 || getFragmentManager() == null) ? false : true;
    }

    /* access modifiers changed from: private */
    public void showCameraTutorial() {
        TutorialManager.showTutorial(getFragmentManager(), new TutorialInfo(R.layout.tutorial_main_camera, R.id.btn_camera));
    }

    public void onTutorialViewClicked(View v) {
        if (v.getId() == R.id.btn_camera) {
            onClick(this.btnCamera);
        }
    }

    public void onTutorialClosed(TutorialInfo tutorialInfo, boolean targetHit) {
        SharedPrefsUtils.setMainOpened(this.mContext, this.mainOpened);
    }

    public void onRenameClick(int pos) {
        showRenameDlg(this.m_arrData.get(pos));
    }

    public void onRemoveClick() {
        if (this.m_arrData.size() > 0) {
            this.m_ivEmptyBG.setVisibility(4);
            this.m_tvStartScan.setVisibility(4);
            m_bAnimate = false;
            removeAlphaAnimation(this.btnCamera);
            return;
        }
        this.m_ivEmptyBG.setVisibility(0);
        this.m_tvStartScan.setVisibility(0);
        m_bAnimate = true;
        setAlphaAnimation(this.btnCamera);
    }

    /* access modifiers changed from: package-private */
    public void showRenameDlg(final Document doc) {
        FileNameDialogUtils.showFileNameDialog(getActivity(), doc.name, getString(R.string.str_rename), getString(R.string.change_group_name), new FileNameDialogUtils.OnRenameListener() {
          

            public final void nameChanged(String str) {
                MainFragment.this.lambda$showRenameDlg$0$MainFragment(doc, str);
            }
        });
    }

    public /* synthetic */ void lambda$showRenameDlg$0$MainFragment(Document doc, String newName) {
        if (StringHelper.isEmpty(newName)) {
            Toast.makeText(getContext(), getString(R.string.alert_file_name_empty), 0).show();
            return;
        }
        doc.name = newName;
        DBManager.getInstance().updateDocument(doc);
        this.mDocAdapter.notifyDataSetChanged();
        updateList();
    }

    public void onCreatedDateUp() {
        if (this.m_sortType != Constant.SORT_TYPE.CREATE_UP) {
            this.m_sortType = Constant.SORT_TYPE.CREATE_UP;
            updateList();
        }
    }

    public void onCreatedDateDown() {
        if (this.m_sortType != Constant.SORT_TYPE.CREATE_DOWN) {
            this.m_sortType = Constant.SORT_TYPE.CREATE_DOWN;
            updateList();
        }
    }

    public void onNameA2Z() {
        if (this.m_sortType != Constant.SORT_TYPE.NAMEA2Z) {
            this.m_sortType = Constant.SORT_TYPE.NAMEA2Z;
            updateList();
        }
    }

    public void onNameZ2A() {
        if (this.m_sortType != Constant.SORT_TYPE.NAMEZ2A) {
            this.m_sortType = Constant.SORT_TYPE.NAMEZ2A;
            updateList();
        }
    }

    public void onFolderSelected(Document folder) {
        if (folder.uid.equals(Document.CREATE_FOLDER_UID)) {
            showOnCreateFolderDlg(false);
        } else {
            moveSelectedDocsToTarget(folder);
        }
    }

    public void moveSelectedDocsToTarget(Document docTarget) {
        for (int i = 0; i < this.m_arrData.size(); i++) {
            Document document = this.m_arrData.get(i);
            if (document.m_bSelected) {
                if (document.isDir) {
                    ArrayList<Document> subDocList = new ArrayList<>();
                    DBManager.getInstance().getDocuments(subDocList, document.uid);
                    int subCnt = subDocList.size();
                    for (int k = 0; k < subCnt; k++) {
                        subDocList.get(k).parent = docTarget.uid;
                        DBManager.getInstance().updateDocument(document);
                    }
                } else {
                    document.parent = docTarget.uid;
                    DBManager.getInstance().updateDocument(document);
                }
            }
        }
        this.m_arrData.clear();
        updateList();
        setMultiMode(false);
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    public void onStart() {
        super.onStart();
        checkPermissionForDocs();
    }

    public void onResume() {
        super.onResume();
        if (this.m_bActivityChanged) {
            updateList();
            this.m_bActivityChanged = false;
        }
        if (this.m_arrData.size() > 0) {
            this.m_ivEmptyBG.setVisibility(4);
            this.m_tvStartScan.setVisibility(4);
            m_bAnimate = false;
            removeAlphaAnimation(this.btnCamera);
            return;
        }
        this.m_ivEmptyBG.setVisibility(0);
        this.m_tvStartScan.setVisibility(0);
        if (!m_bAnimate) {
            m_bAnimate = true;
            setAlphaAnimation(this.btnCamera);
        }
    }

    private void checkPermissionForDocs() {
        if (this.m_arrData.size() > 0 && getActivity() != null && ContextCompat.checkSelfPermission(getActivity(), PermissionsUtils.STORAGE_PERMISSIONS) != 0) {
            PermissionsUtils.askPermissions(getActivity(), new PermissionsUtils.PermissionListener() {
                public final void onGranted() {
                    MainFragment.this.lambda$checkPermissionForDocs$1$MainFragment();
                }
            }, PermissionsUtils.STORAGE_PERMISSIONS);
        }
    }

    public /* synthetic */ void lambda$checkPermissionForDocs$1$MainFragment() {
        this.mDocAdapter.notifyDataSetChanged();
    }

    /* access modifiers changed from: package-private */
    public void initVariable() {
        this.m_filter = Filter.ALL;
        this.m_arrData = new ArrayList();
        this.m_sortType = Constant.SORT_TYPE.CREATE_DOWN;
        this.m_bMultiMode = false;
        this.m_bSelectedAll = false;
        loadDB();
        this.mDocAdapter = new DocumentAdapter(getActivity(), this.m_arrData);
        this.mDocAdapter.callback = this;
        m_bAnimate = false;
        this.m_bActivityChanged = false;
        this.mainOpened = SharedPrefsUtils.getMainOpened(this.mContext);
    }

    /* access modifiers changed from: package-private */
    public void loadDB() {
        this.m_arrData.clear();
        if (this.m_filter == Filter.ALL) {
            if (this.m_sortType == Constant.SORT_TYPE.CREATE_UP) {
                DBManager.getInstance().getDocumentsCreatedUp(this.m_arrData, "", -1, true);
                DBManager.getInstance().getDocumentsCreatedUp(this.m_arrData, "", -1, false);
            } else if (this.m_sortType == Constant.SORT_TYPE.CREATE_DOWN) {
                DBManager.getInstance().getDocumentsCreatedDown(this.m_arrData, "", -1, true);
                DBManager.getInstance().getDocumentsCreatedDown(this.m_arrData, "", -1, false);
            } else if (this.m_sortType == Constant.SORT_TYPE.NAMEA2Z) {
                DBManager.getInstance().getDocumentsNameA2Z(this.m_arrData, "", -1, true);
                DBManager.getInstance().getDocumentsNameA2Z(this.m_arrData, "", -1, false);
            } else if (this.m_sortType == Constant.SORT_TYPE.NAMEZ2A) {
                DBManager.getInstance().getDocumentsNameZ2A(this.m_arrData, "", -1, true);
                DBManager.getInstance().getDocumentsNameZ2A(this.m_arrData, "", -1, false);
            }
        }
        if (this.m_filter == Filter.MARKED_DOC) {
            DBManager.getInstance().getMarkedDocuments(this.m_arrData);
        }
        for (int i = 0; i < this.m_arrData.size(); i++) {
            this.m_arrData.get(i).m_bShowButtons = !this.m_bMultiMode;
        }
        checkEmpty();
    }

    /* access modifiers changed from: package-private */
    public void initUI(View view) {
        this.toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        if (this.mContext == null) {
            this.mContext = getContext();
        }
        this.multiTopBar = (RelativeLayout) getActivity().findViewById(R.id.multi_select_bar);
        this.multiTopBar.setVisibility(4);
        this.multiBottomBar.setVisibility(8);
        this.multiTopBar.findViewById(R.id.btn_bar_back).setOnClickListener(this);
        this.multiTopBar.findViewById(R.id.btn_bar_select_all).setOnClickListener(this);
        this.multiTopBar.findViewById(R.id.rl_tv_bar_select_all).setOnClickListener(this);
        this.m_ivSelectAll = (ImageView) this.multiTopBar.findViewById(R.id.iv_bar_select_all);
        this.m_tvMultiSelect = new TextView(this.mContext);
        TextView textView = this.m_tvMultiSelect;
        textView.setText("0 " + getString(R.string.str_selected));
        this.m_tvMultiSelect.setTextColor(getResources().getColor(R.color.colorWhite));
        this.m_tvMultiSelect.setTextSize(2, 16.0f);
        this.m_tvMultiSelect.setGravity(3);
        RelativeLayout.LayoutParams layoutParams1 = new RelativeLayout.LayoutParams(-2, -2);
        Resources resources = this.mContext.getResources();
        layoutParams1.leftMargin = 0;
        layoutParams1.addRule(1, R.id.btn_bar_back);
        layoutParams1.addRule(15);
        this.m_tvMultiSelect.setLayoutParams(layoutParams1);
        this.multiTopBar.addView(this.m_tvMultiSelect);
        TextView tvSelectAll = new TextView(this.mContext);
        tvSelectAll.setText(getString(R.string.str_select_all));
        tvSelectAll.setTextColor(getResources().getColor(R.color.colorWhite));
        tvSelectAll.setTextSize(2, 16.0f);
        RelativeLayout.LayoutParams layoutParams2 = new RelativeLayout.LayoutParams(-2, -2);
        layoutParams2.addRule(13);
        tvSelectAll.setLayoutParams(layoutParams2);
        ((RelativeLayout) this.multiTopBar.findViewById(R.id.rl_tv_bar_select_all)).addView(tvSelectAll);
        this.searchView = (MaterialSearchView) getActivity().findViewById(R.id.search_view);
        this.searchView.setVoiceSearch(false);
        this.searchView.setCursorDrawable(R.drawable.custom_cursor);
        this.searchView.setEllipsize(true);
        this.searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            public boolean onQueryTextSubmit(String query) {
                return true;
            }

            public boolean onQueryTextChange(String newText) {
                if (!newText.isEmpty()) {
                    MainFragment.this.searchDoc(newText);
                    return false;
                }
                MainFragment.this.displayFilterDoc();
                return false;
            }
        });
        this.searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            public void onSearchViewShown() {
            }

            public void onSearchViewClosed() {
                MainFragment.this.displayFilterDoc();
            }
        });
        this.m_lvDocument = (ListView) view.findViewById(R.id.document_list);
        this.m_lvDocument.setAdapter(this.mDocAdapter);
        this.m_lvDocument.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public final void onItemClick(AdapterView adapterView, View view, int i, long j) {
                MainFragment.this.lambda$initUI$2$MainFragment(adapterView, view, i, j);
            }
        });
        this.m_lvDocument.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            public final boolean onItemLongClick(AdapterView adapterView, View view, int i, long j) {
                return MainFragment.this.lambda$initUI$3$MainFragment(adapterView, view, i, j);
            }
        });
        this.m_lvDocument.setOnScrollListener(FabListViewListener.create(this.fabGroup, R.dimen.fab_margin));
        checkEmpty();
        BubbleDrawable myBubble = new BubbleDrawable(this.mContext);
        myBubble.setCornerRadius(4.0f);
        myBubble.setPointerAlignment(2);
        myBubble.setPadding(25, 25, 25, 30);
        this.m_tvStartScan.setBackgroundDrawable(myBubble);
    }

    public /* synthetic */ void lambda$initUI$2$MainFragment(AdapterView adapterView, View view12, int i, long l) {
        Document doc = this.m_arrData.get(i);
        if (this.m_bMultiMode) {
            doc.m_bSelected = !doc.m_bSelected;
            this.mDocAdapter.notifyDataSetChanged();
            updateSelection();
            return;
        }
        this.m_lvDocument.setChoiceMode(1);
        if (!doc.isLocked) {
            this.searchView.hideKeyboard(view12);
            openFile(doc);
            return;
        }
        Toast.makeText(getActivity(), getString(R.string.alert_lock), 0).show();
    }

    public /* synthetic */ boolean lambda$initUI$3$MainFragment(AdapterView adapterView, View view1, int i, long l) {
        Document doc = this.m_arrData.get(i);
        if (!this.m_bMultiMode) {
            setMultiMode(true);
            doc.m_bSelected = !doc.m_bSelected;
            this.mDocAdapter.notifyDataSetChanged();
            updateSelection();
        }
        return true;
    }

    private void updateSelection() {
        int cnt = getSelectedCount();
        this.m_tvMultiSelect.setText(String.format("%d %s", new Object[]{Integer.valueOf(cnt), getString(R.string.str_selected)}));
        if (cnt == getDocsAndFolderCount()) {
            this.m_ivSelectAll.setImageResource(R.drawable.icon_toolbar_check_on);
            this.m_bSelectedAll = true;
        } else {
            this.m_ivSelectAll.setImageResource(R.drawable.icon_toolbar_check_off);
            this.m_bSelectedAll = false;
        }
        if (isFolderSelected()) {
            this.btnBottomMove.setVisibility(8);
        } else {
            this.btnBottomMove.setVisibility(0);
        }
    }

    /* access modifiers changed from: package-private */
    public void checkEmpty() {
        if (this.m_arrData.size() > 0) {
            ImageView imageView = this.m_ivEmptyBG;
            if (imageView != null) {
                imageView.setVisibility(4);
            }
            TextView textView = this.m_tvStartScan;
            if (textView != null) {
                textView.setVisibility(4);
            }
            View view = this.btnCamera;
            if (view != null) {
                m_bAnimate = false;
                removeAlphaAnimation(view);
                return;
            }
            return;
        }
        ImageView imageView2 = this.m_ivEmptyBG;
        if (imageView2 != null) {
            imageView2.setVisibility(0);
        }
        TextView textView2 = this.m_tvStartScan;
        if (textView2 != null) {
            textView2.setVisibility(0);
        }
        View view2 = this.btnCamera;
        if (view2 != null) {
            m_bAnimate = true;
            setAlphaAnimation(view2);
        }
    }

    /* access modifiers changed from: package-private */
    public void searchDoc(String keyWord) {
        this.m_arrData.clear();
        if (this.m_filter == Filter.ALL) {
            DBManager.getInstance().searchDocuments(this.m_arrData, keyWord, false);
        } else if (this.m_filter == Filter.MARKED_DOC) {
            DBManager.getInstance().searchDocuments(this.m_arrData, keyWord, true);
        }
        this.mDocAdapter.notifyDataSetChanged();
    }

    /* access modifiers changed from: package-private */
    public void displayFilterDoc() {
        updateList();
    }

    public void startCamera() {
        if (isAdded()) {

            if(InterstitialHelper.isLoaded)
            {
                InterstitialHelper.showInterstitial(getActivity(), new InterstialListner() {
                    @Override
                    public void onAddClose() {
                        Intent intent = new Intent(mContext, CameraOpenActivity.class);
                        intent.putExtra("Parent", "");
                        startActivityForResult(intent, 1003);
                    }
                });
            }
            else {
                Intent intent = new Intent(mContext, CameraOpenActivity.class);
                intent.putExtra("Parent", "");
                startActivityForResult(intent, 1003);
            }
            // TODO: 04-02-2022 comment by peenalkumar
            //Google_Intertitial.Show_Intertitial_Ad((Activity) mContext);

            /*final Timer timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    if (Google_Intertitial.Load == true) {
                        if (Google_Intertitial.close == true) {
                            Log.d("Ankita Savaliya", "" + Google_Intertitial.close);
                            Google_Intertitial.close = false;
                            Google_Intertitial.Load = false;
                            timer.cancel();


                            Intent intent = new Intent(mContext, CameraOpenActivity.class);
                            intent.putExtra("Parent", "");
                            startActivityForResult(intent, 1003);

                        } else {
                            Log.d("Ankita Savaliya", "" + Google_Intertitial.close);
                        }
                    } else {
                        Google_Intertitial.close = false;
                        Google_Intertitial.Load = false;
                        timer.cancel();

                        Intent intent = new Intent(mContext, CameraOpenActivity.class);
                        intent.putExtra("Parent", "");
                        startActivityForResult(intent, 1003);


                    }



                }
            }, 0, 3);*/

        }
    }

    public void startQR() {
        startActivityForResult(new Intent(this.mContext, BarcodeScannerActivity.class), Constant.REQUEST_CAMERA_QR);
    }

    /* access modifiers changed from: package-private */
    public void openFile(final Document doc) {
        if (doc.isDir) {
//            Analytics.get().logOpenFolder();
            Intent intent = new Intent(getActivity(), FolderMainActivity.class);
            intent.putExtra(Constant.EXTRA_PARENT, doc.uid);
            intent.putExtra("name", doc.name);
            startActivityForResult(intent, Constant.REQUEST_FOLDER);
            return;
        }
        PermissionsUtils.askPermissions(getActivity(), new PermissionsUtils.PermissionListener() {


            public final void onGranted() {
                MainFragment.this.lambda$openFile$4$MainFragment(doc);
            }
        }, PermissionsUtils.STORAGE_PERMISSIONS);
    }

    /* access modifiers changed from: private */
    /* renamed from: openGridScreen */
    public void lambda$openFile$4$MainFragment(Document doc) {
        Intent intent = new Intent(getActivity(), GridModeLayoutActivity.class);
        intent.putExtra(Constant.EXTRA_PARENT, doc.uid);
        intent.putExtra("name", doc.name);
        intent.putExtra(GridModeLayoutActivity.SHOW_RATE_US_KEY, true);
        startActivityForResult(intent, 1006);
    }

    public void autoOpenFile(String mParent, String mName) {
        Intent intent = new Intent(getActivity(), GridModeLayoutActivity.class);
        intent.putExtra(Constant.EXTRA_PARENT, mParent);
        intent.putExtra("name", mName);
        startActivityForResult(intent, 1006);
    }


    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.btn_bar_back:
                setMultiMode(false);
                return;
            case R.id.btn_bar_delete:
                multiDelete();
                return;
            case R.id.btn_bar_merge:
            case R.id.btn_bar_tag:
                return;
            case R.id.btn_bar_move:
                if (getSelectedCount() > 0) {
                    showMoveToFolderDlg();
                    return;
                }
                return;
            case R.id.btn_bar_select_all:
                break;
            case R.id.btn_bar_share:
                showShareDialog();
                return;
            default:
                switch (id) {
                    case R.id.btn_camera:
                        PermissionsUtils.askPermissions(getActivity(), new PermissionsUtils.PermissionListener() {
                            public final void onGranted() {
                                MainFragment.this.startCamera();
                            }
                        }, PermissionsUtils.TAKE_SAVE_PICTURE_PERMISSIONS);
                        return;
                    case R.id.btn_create_folder:
                        showOnCreateFolderDlg(true);
                        return;
                    case R.id.btn_gallery:
//                        Analytics.get().logPreScreen(AnalyticsConstants.PARAM_VALUE_PRE_SCAN_MODE_GALLERY);
                        PermissionsUtils.askPermissions(getActivity(), new PermissionsUtils.PermissionListener() {
                            public final void onGranted() {
                                MainFragment.this.lambda$onClick$5$MainFragment();
                            }
                        }, PermissionsUtils.STORAGE_PERMISSIONS);
                        return;
                    case R.id.btn_multi:
                        setMultiMode(true);
                        return;
                    case R.id.btn_qr:
                        PermissionsUtils.askPermissions(getActivity(), new PermissionsUtils.PermissionListener() {
                            public final void onGranted() {
                                MainFragment.this.startQR();
                            }
                        }, PermissionsUtils.TAKE_PICTURE_PERMISSIONS);
                        return;
                    case R.id.btn_search:
                        this.searchView.showSearch();
                        return;
                    case R.id.btn_sort:
                        showSortDialog();
                        return;
                    case R.id.btn_tag:
                    default:
                        return;
                    case R.id.rl_tv_bar_select_all:
                        break;
                }
        }
        selectAll();
    }

    public /* synthetic */ void lambda$onClick$5$MainFragment() {
        ImagesPickerManager.startPicker((Fragment) this);
    }

    /* access modifiers changed from: package-private */
    public void showMoveToFolderDlg() {
        MoveToFragmentDialog.newInstance().setListener(this).showDialog(getActivity());
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
        this.mDocAdapter.notifyDataSetChanged();
    }

    public void setMultiMode(boolean multiMode) {
        if (!multiMode) {
            this.multiTopBar.setVisibility(4);
            this.multiBottomBar.setVisibility(8);
            this.toolbarSecond.setVisibility(0);
            this.fabGroup.setVisibility(0);
            this.toolbar.setVisibility(0);
            this.m_bSelectedAll = false;
            this.m_ivSelectAll.setImageResource(R.drawable.icon_toolbar_check_off);
            this.m_lvDocument.setChoiceMode(1);
            this.m_bMultiMode = false;
            for (int i = 0; i < this.m_arrData.size(); i++) {
                this.m_arrData.get(i).m_bSelected = false;
            }
            this.m_tvMultiSelect.setText(String.format("%s %s", new Object[]{Integer.valueOf(getDocsSelectedCount()), getString(R.string.str_selected)}));
        } else if (getDocsAndFolderCount() > 0) {
            this.multiTopBar.setVisibility(0);
            this.multiBottomBar.setVisibility(0);
            this.toolbarSecond.setVisibility(8);
            this.fabGroup.setVisibility(4);
            this.toolbar.setVisibility(4);
            this.m_lvDocument.setChoiceMode(2);
            this.m_bMultiMode = true;
        }
        for (int i2 = 0; i2 < this.m_arrData.size(); i2++) {
            this.m_arrData.get(i2).m_bShowButtons = !this.m_bMultiMode;
        }
        this.mDocAdapter.notifyDataSetChanged();
    }

    /* access modifiers changed from: package-private */
    public void multiDelete() {
        if (getSelectedCount() != 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this.mContext);
            builder.setTitle((int) R.string.str_delete);
            builder.setMessage((int) R.string.msg_delete);
            builder.setPositiveButton((int) R.string.str_yes, (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
                public final void onClick(DialogInterface dialogInterface, int i) {
                    MainFragment.this.lambda$multiDelete$6$MainFragment(dialogInterface, i);
                }
            });
            builder.setNegativeButton((int) R.string.str_cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            builder.setCancelable(false);
            builder.show();
        }
    }

    public /* synthetic */ void lambda$multiDelete$6$MainFragment(DialogInterface dialog, int which) {
        for (int i = 0; i < this.m_arrData.size(); i++) {
            Document doc = this.m_arrData.get(i);
            if (doc.m_bSelected) {
                DocumentRepository.removeDocumentWithChilds(this.mContext, doc);
            }
        }
        updateList();
        onRemoveClick();
        this.m_tvMultiSelect.setText(String.format("%s %s", new Object[]{Integer.valueOf(getSelectedCount()), getString(R.string.str_selected)}));
        setMultiMode(false);
        dialog.dismiss();
    }

    /* access modifiers changed from: package-private */
    public void showOnCreateFolderDlg(final boolean bOpenFile) {
        if ( DBManager.getInstance().getFolderCount() < 5) {
            FileNameDialogUtils.showFileNameDialog(getActivity(), "", "", getString(R.string.create_new_folder), new FileNameDialogUtils.OnRenameListener() {


                public final void nameChanged(String str) {
                    MainFragment.this.lambda$showOnCreateFolderDlg$8$MainFragment(bOpenFile, str);
                }
            });
            return;
        }
//        Analytics.get().logPremiumFeature(AnalyticsConstants.PARAM_VALUE_PREMIUM_FEATURE_5_FOLDER);
        PremiumHelper.showPremiumAfterAlertDialog(this.mContext, R.string.alert_premium_folders_title, R.string.alert_premium_folders_message, new PremiumHelper.StartActivityController() {
            public final void startActivity(Intent intent, int i) {
                MainFragment.this.startActivityForResult(intent, i);
            }
        });
    }

    public /* synthetic */ void lambda$showOnCreateFolderDlg$8$MainFragment(boolean bOpenFile, String newName) {
        if (StringHelper.isEmpty(newName)) {
            Toast.makeText(getActivity(), getString(R.string.alert_folder_name_emptry), 0).show();
            return;
        }
        Document doc = new Document("");
        doc.isDir = true;
        doc.name = newName;
        doc.path = "";
        doc.date = new Date().getTime();
        doc.thumb = "";
        DBManager.getInstance().addDocument(doc);
//        Analytics.get().logCreateFolder();
        if (bOpenFile) {
            this.m_arrData.add(0, doc);
            this.mDocAdapter.notifyDataSetChanged();
            openFile(doc);
            return;
        }
        moveSelectedDocsToTarget(doc);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        this.m_bActivityChanged = true;
        saveBatchModeWithGallery(ImagesPickerManager.handlePickerMultiResults(requestCode, resultCode, data));
        if (requestCode != 1003) {
            if (requestCode != 1006) {
                if (requestCode == 1010) {
                    ImageStorageUtils.clearShareFolder();
                }
            } else if (resultCode == -1) {
                updateList();
            }
        } else if (resultCode == 5) {
            Toast.makeText(getActivity(), R.string.error_can_not_take_image, 1).show();
        } else if (resultCode != -1) {
        } else {
            if (data.getFlags() == 1) {
                String temp = data.getStringExtra(Constant.EXTRA_DOC_ARRAY);
                Document docContainer = PolygonUtil.InsertBatchData(this.mContext, (List) new Gson().fromJson(temp, new TypeToken<List<Document>>() {
                }.getType()), (String) null);
                if (docContainer != null) {
                    autoOpenFile(docContainer.uid, docContainer.name);
                    return;
                }
                return;
            }
            CropImageActivity.cropNewFile(getActivity(), DetectionResult.FIX_RECT_MODE.FIX_RECT_CAMERA, (String) null, false);
        }
    }

    public void updateList() {
        loadDB();
        this.mDocAdapter.notifyDataSetChanged();
    }

    /* access modifiers changed from: package-private */
    public void saveBatchModeWithGallery(final ArrayList<GalleryImage> galleryImages) {
        int cnt;
        if (galleryImages != null && (cnt = galleryImages.size()) != 0) {
            if (cnt == 1) {
                PolygonUtil.importGalleryImage(this.mContext, galleryImages.get(0).getBitmapUri());
                CropImageActivity.cropNewFile(getActivity(), DetectionResult.FIX_RECT_MODE.FIX_RECT_GALLERY, (String) null, false);
            } else if (cnt > 1) {
                final ProgressDialog dlgProgress = new ProgressDialog(this.mContext);
                dlgProgress.setMessage(this.mContext.getString(R.string.str_saving_processing));
                dlgProgress.setCancelable(false);
                dlgProgress.show();
                new Thread(new Runnable() {

                    public final void run() {
                        MainFragment.this.lambda$saveBatchModeWithGallery$10$MainFragment(galleryImages, dlgProgress);
                    }
                }).start();
            }
        }
    }

    public /* synthetic */ void lambda$saveBatchModeWithGallery$10$MainFragment(ArrayList galleryImages, final ProgressDialog dlgProgress) {
        final List<Document> docBatchList = new ArrayList<>();
        Iterator it = galleryImages.iterator();
        while (it.hasNext()) {
            PolygonUtil.copyImageFromGalleryWithAutoCrop(this.mContext, ((GalleryImage) it.next()).getBitmapUri(), docBatchList);
        }
        ((Activity) this.mContext).runOnUiThread(new Runnable() {


            public final void run() {
                MainFragment.this.lambda$null$9$MainFragment(dlgProgress, PolygonUtil.InsertBatchData(mContext, docBatchList, (String) null));
            }
        });
    }

    public /* synthetic */ void lambda$null$9$MainFragment(ProgressDialog dlgProgress, Document docContainer) {
        try {
            dlgProgress.dismiss();
        } catch (IllegalArgumentException e) {
        }
        if (docContainer != null && isAdded()) {
            autoOpenFile(docContainer.uid, docContainer.name);
        }
    }

    public void onDestroyView() {
        super.onDestroyView();
        this.btnCamera.clearAnimation();
        this.btnCamera.animate().cancel();
    }

    public void onDestroy() {
        super.onDestroy();
        m_bAnimate = false;
        AnimatorSet animatorSet = mAnimationSet;
        if (animatorSet != null) {
            animatorSet.cancel();
            mAnimationSet.removeAllListeners();
            mAnimationSet = null;
        }
    }

    public static void setAlphaAnimation(final View v) {
        ObjectAnimator fadeOut = ObjectAnimator.ofFloat(v, "alpha", new float[]{1.0f, 0.5f});
        fadeOut.setDuration(1000);
        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(v, "alpha", new float[]{0.5f, 1.0f});
        fadeIn.setDuration(1000);
        mAnimationSet = new AnimatorSet();
        mAnimationSet.play(fadeOut).after(fadeIn);
        mAnimationSet.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                try {
                    if (MainFragment.m_bAnimate) {
                        MainFragment.mAnimationSet.start();
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
        new SortDialog(this.mContext, getLayoutInflater().inflate(R.layout.sort_listview, (ViewGroup) null), this, this.m_sortType).show();
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

    public boolean isFolderSelected() {
        for (Document doc : this.m_arrData) {
            if (doc.m_bSelected && doc.isDir) {
                return true;
            }
        }
        return false;
    }

    public int getDocsAndFolderCount() {
        return this.m_arrData.size();
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

    public boolean isMultiMode() {
        return this.m_bMultiMode;
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
            ExportDialogUtils.showShareDialogWithDirs((FragmentActivity) this.mContext, toShare);
        }
    }
}
