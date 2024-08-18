package com.camscanner.paperscanner.pdfcreator.view.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.camscanner.paperscanner.pdfcreator.ads.InterstialListner;
import com.camscanner.paperscanner.pdfcreator.ads.InterstitialHelper;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import com.tapscanner.polygondetect.DetectionResult;
import java.util.ArrayList;
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
import com.camscanner.paperscanner.pdfcreator.common.utils.FileNameDialogUtils;
import com.camscanner.paperscanner.pdfcreator.common.utils.ImageStorageUtils;
import com.camscanner.paperscanner.pdfcreator.common.utils.PermissionsUtils;
import com.camscanner.paperscanner.pdfcreator.common.utils.PolygonUtil;
import com.camscanner.paperscanner.pdfcreator.common.utils.SharedPrefsUtils;
import com.camscanner.paperscanner.pdfcreator.common.utils.StringHelper;
import com.camscanner.paperscanner.pdfcreator.common.views.draglistview.DragItem;
import com.camscanner.paperscanner.pdfcreator.common.views.draglistview.DragListView;
import com.camscanner.paperscanner.pdfcreator.features.tutorial.TutorialManager;
import com.camscanner.paperscanner.pdfcreator.features.tutorial.model.TutorialInfo;
import com.camscanner.paperscanner.pdfcreator.features.tutorial.model.TutorialViewInfo;
import com.camscanner.paperscanner.pdfcreator.model.Document;
import com.camscanner.paperscanner.pdfcreator.view.activity.main.MainActivity;
import com.camscanner.paperscanner.pdfcreator.view.activity.setting.Setting_MyDocPropertyActivity;
import com.camscanner.paperscanner.pdfcreator.view.adapter.ItemAdapter;
import com.camscanner.paperscanner.pdfcreator.view.camera.CameraOpenActivity;
import com.camscanner.paperscanner.pdfcreator.view.crop.CropImageActivity;
import com.camscanner.paperscanner.pdfcreator.view.interfaces.DragItemCallback;

public class GridModeLayoutActivity extends BaseMainActivity implements DragItemCallback, TutorialManager.OnTutorialListener {
    public static final String SHOW_RATE_US_KEY = "SHOW_RATE_US_KEY";
    /* access modifiers changed from: private */
    public ItemAdapter adapter;
    AppBarLayout appbar;
    FloatingActionButton btnCamera;
    FloatingActionButton btnGallery;
    ImageView btnSave;
    FloatingActionButton btnShare;
    TextView docName;
    private int gridOpened = 0;
    View gridRoot;
    /* access modifiers changed from: private */
    public ArrayList<Document> m_arrData;
    private boolean m_bCanEdit;
    DragListView m_dragView;
    private String m_strName;
    /* access modifiers changed from: private */
    public String m_strParent;

    /* access modifiers changed from: protected */
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(268468224);
            startActivity(intent);
            finish();
            return;
        }
        setContentView((int) R.layout.activity_grid);
        appbar = (AppBarLayout)findViewById(R.id.appbar);
        btnCamera = (FloatingActionButton) findViewById(R.id.btn_camera);
        btnGallery = (FloatingActionButton) findViewById(R.id.btn_gallery);
        btnSave = (ImageView) findViewById(R.id.btn_save);
        btnShare = (FloatingActionButton) findViewById(R.id.btn_export);
     ImageView   btn_back = (ImageView) findViewById(R.id.btn_back);
     ImageView   btn_edit = (ImageView) findViewById(R.id.btn_edit);
     ImageView   btn_settings = (ImageView) findViewById(R.id.btn_settings);
        docName = (TextView) findViewById(R.id.title);
        gridRoot = (View) findViewById(R.id.grid_root);
        m_dragView = (DragListView) findViewById(R.id.drag_list_view);

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(-1, new Intent());
                finish();
            }
        });

        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PermissionsUtils.askPermissions(GridModeLayoutActivity.this, new PermissionsUtils.PermissionListener() {
                    public final void onGranted() {
                        GridModeLayoutActivity.this.startCamera();
                    }
                }, PermissionsUtils.TAKE_SAVE_PICTURE_PERMISSIONS);
            }
        });
        btn_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showRenameDlg();
            }
        });
        btnGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PermissionsUtils.askPermissions(GridModeLayoutActivity.this, new PermissionsUtils.PermissionListener() {
                    public final void onGranted() {
                        GridModeLayoutActivity.this.lambda$onClick$0$GridModeActivity();
                    }
                }, PermissionsUtils.STORAGE_PERMISSIONS);
            }
        });
        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayShareDialog();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displaySaveDialog();
            }
        });

        btn_settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToPDFSetting();
            }
        });

        initVariable();
        initUI();
        setupGridVerticalRecyclerView();
        showOverlay();
    }


    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_back:
                setResult(-1, new Intent());
                finish();
                return;
            case R.id.btn_camera:
                PermissionsUtils.askPermissions(this, new PermissionsUtils.PermissionListener() {
                    public final void onGranted() {
                        GridModeLayoutActivity.this.startCamera();
                    }
                }, PermissionsUtils.TAKE_SAVE_PICTURE_PERMISSIONS);
                return;
            case R.id.btn_edit:
                showRenameDlg();
                return;
            case R.id.btn_export:
                displayShareDialog();
                return;
            case R.id.btn_gallery:
                PermissionsUtils.askPermissions(this, new PermissionsUtils.PermissionListener() {
                    public final void onGranted() {
                        GridModeLayoutActivity.this.lambda$onClick$0$GridModeActivity();
                    }
                }, PermissionsUtils.STORAGE_PERMISSIONS);
                return;
            case R.id.btn_save:
                displaySaveDialog();
                return;
            case R.id.btn_settings:
                goToPDFSetting();
                return;
            default:
                return;
        }
    }


    private void showOverlay() {
        int i = this.gridOpened;
        if (i == 1) {
            ActivityUtils.waitVisibleFor(new ActivityUtils.OnVisibleChecker() {
                public final boolean isVisible() {
                    return GridModeLayoutActivity.this.isBtnSaveReady();
                }
            }, new ActivityUtils.OnVisibleListener() {
                public final void onVisible() {
                    GridModeLayoutActivity.this.showSaveTutorial();
                }
            });
        } else if (i == 2 && this.m_arrData.size() == 2) {
            ActivityUtils.waitVisibleFor(new ActivityUtils.OnVisibleChecker() {
                public final boolean isVisible() {
                    return GridModeLayoutActivity.this.isBtnAddReady();
                }
            }, new ActivityUtils.OnVisibleListener() {
                public final void onVisible() {
                    GridModeLayoutActivity.this.showAddTutorial();
                }
            });
        } else if (getIntent().getBooleanExtra(SHOW_RATE_US_KEY, false)) {
        }
    }

    /* access modifiers changed from: private */
    public boolean isBtnAddReady() {
        DragListView dragListView;
        if (isFinishing() || (dragListView = this.m_dragView) == null || dragListView.getRecyclerView() == null || this.m_dragView.getRecyclerView().getLayoutManager() == null || this.adapter.getItemCount() <= 0 || this.m_dragView.getRecyclerView().getLayoutManager().findViewByPosition(this.adapter.getItemCount() - 1) == null) {
            return false;
        }
        return true;
    }

    /* access modifiers changed from: private */
    public boolean isBtnSaveReady() {
        return this.btnSave.getWidth() != 0;
    }

    /* access modifiers changed from: package-private */
    public void initVariable() {
        this.m_strParent = getIntent().getStringExtra(Constant.EXTRA_PARENT);
        this.m_strName = getIntent().getStringExtra("name");
        this.m_arrData = new ArrayList<>();
        this.gridOpened = SharedPrefsUtils.getGridOpened(this);
        loadDB();
    }

    /* access modifiers changed from: private */
    public void showAddTutorial() {
        TutorialManager.showTutorial(getSupportFragmentManager(), getAddTutorialInfo());
    }

    /* access modifiers changed from: private */
    public void showSaveTutorial() {
        TutorialManager.showTutorial(getSupportFragmentManager(), getSaveTutorialInfo());
    }

    private TutorialViewInfo getAddTutorialInfo() {
        View view = this.m_dragView.getRecyclerView().getLayoutManager().findViewByPosition(this.adapter.getItemCount() - 1);
        float padding = getResources().getDimension(R.dimen.padding_grid) * 2.0f;
        float y = this.gridRoot.getY() + view.getY() + padding;
        return new TutorialViewInfo((int) R.layout.tutorial_grid_add, (int) R.id.btn_add, (int) R.id.btn_add_out, this.gridRoot.getX() + view.getX() + padding, y, (int) (((float) view.getWidth()) - (padding * 2.0f)), (int) (((float) view.getHeight()) - (2.0f * padding)));
    }

    private TutorialViewInfo getSaveTutorialInfo() {
        float x = this.appbar.getX() + this.btnSave.getX();
        return new TutorialViewInfo(R.layout.tutorial_grid_save, R.id.btn_save, x, this.appbar.getY() + this.btnSave.getY(), this.btnSave.getWidth(), this.btnSave.getHeight());
    }

    public void onTutorialViewClicked(View v) {
        switch (v.getId()) {
            case R.id.btn_add:
                onClick(this.btnCamera);
            case R.id.btn_add_out:
                onClick(this.btnCamera);
                return;
            case R.id.btn_save:
                onClick(this.btnSave);
                return;
            default:
                return;
        }
    }

    public void onTutorialClosed(TutorialInfo tutorialInfo, boolean targetHit) {
        if (tutorialInfo.layoutId == R.layout.tutorial_grid_save) {
            SharedPrefsUtils.setGridOpened(this, 1);
//            Analytics.get().logTutorShare(targetHit);
        } else if (tutorialInfo.layoutId == R.layout.tutorial_grid_add) {
            SharedPrefsUtils.setGridOpened(this, 2);
//            Analytics.get().logTutorAddMore(targetHit);
        }
    }

    private void setupGridVerticalRecyclerView() {
        this.m_dragView.setLayoutManager(new GridLayoutManager(this, 2));
        this.adapter = new ItemAdapter(GridModeLayoutActivity.this,this.m_arrData, R.layout.grid_view_item, R.id.grid_item_layout, true, this);
        this.m_dragView.setAdapter(this.adapter, true);
        this.m_dragView.setCanDragHorizontally(true);
        this.m_dragView.setCustomDragItem((DragItem) null);
    }

    /* access modifiers changed from: package-private */
    public void loadDB() {
        DBManager.getInstance().getDocumentsBySortID(this.m_arrData, this.m_strParent);
        this.m_arrData.add(new Document(this.m_strParent));
    }

    /* access modifiers changed from: package-private */
    public synchronized void goToEditActivity(final Document doc, final int position) {
        if (this.m_bCanEdit) {


            if(InterstitialHelper.isLoaded)
            {
                InterstitialHelper.showInterstitial(this, new InterstialListner() {
                    @Override
                    public void onAddClose() {
                        Intent intent = new Intent(GridModeLayoutActivity.this, EditImageActivity.class);
                        doc.parentName = m_strName;
                        intent.putExtra(Constant.EXTRA_DOCUMENT, doc);
                        intent.putExtra(Constant.EXTRA_EXIST_COUNT, m_arrData.size());
                        intent.putExtra("position", position);
                        startActivityForResult(intent, 1007);
                    }
                });
            }
            else {
                Intent intent = new Intent(GridModeLayoutActivity.this, EditImageActivity.class);
                doc.parentName = m_strName;
                intent.putExtra(Constant.EXTRA_DOCUMENT, doc);
                intent.putExtra(Constant.EXTRA_EXIST_COUNT, m_arrData.size());
                intent.putExtra("position", position);
                startActivityForResult(intent, 1007);
            }



            // TODO: 04-02-2022 comment by peenalkumar
            //Google_Intertitial.Show_Intertitial_Ad(GridModeLayoutActivity.this);

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


                            Intent intent = new Intent(GridModeLayoutActivity.this, EditImageActivity.class);

                            doc.parentName = m_strName;
                            intent.putExtra(Constant.EXTRA_DOCUMENT, doc);
                            intent.putExtra(Constant.EXTRA_EXIST_COUNT, m_arrData.size());
                            intent.putExtra("position", position);
                            startActivityForResult(intent, 1007);

                        } else {
                            Log.d("Ankita Savaliya", "" + Google_Intertitial.close);
                        }
                    } else {
                        Google_Intertitial.close = false;
                        Google_Intertitial.Load = false;
                        timer.cancel();

                        Intent intent = new Intent(GridModeLayoutActivity.this, EditImageActivity.class);

                        doc.parentName = m_strName;
                        intent.putExtra(Constant.EXTRA_DOCUMENT, doc);
                        intent.putExtra(Constant.EXTRA_EXIST_COUNT, m_arrData.size());
                        intent.putExtra("position", position);
                        startActivityForResult(intent, 1007);


                    }
                    timer.cancel();


                }
            }, 0, 3);*/


        }
        this.m_bCanEdit = false;
    }

    /* access modifiers changed from: package-private */
    public void initUI() {
        this.m_dragView.getRecyclerView().setVerticalScrollBarEnabled(true);
        this.m_dragView.setDragListListener(new DragListView.DragListListenerAdapter() {
            public void onItemDragStarted(int position) {
                GridModeLayoutActivity.this.showBottomBar(false);
            }

            public void onItemDragEnded(final int fromPosition, final int toPosition, boolean remove) {
                if (remove) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(GridModeLayoutActivity.this);
                    builder.setTitle((int) R.string.str_delete);
                    builder.setMessage((int) R.string.msg_delete);
                    builder.setPositiveButton((int) R.string.str_yes, (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {


                        public final void onClick(DialogInterface dialogInterface, int i) {
                           lambda$onItemDragEnded$0$GridModeActivity$1(toPosition, dialogInterface, i);
                        }
                    });
                    builder.setNegativeButton((int) R.string.str_cancel, (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {


                        public final void onClick(DialogInterface dialogInterface, int i) {
                            lambda$onItemDragEnded$1$GridModeActivity$1(fromPosition, toPosition, dialogInterface, i);
                        }
                    });
                    builder.setCancelable(false);
                    builder.show();
                    return;
                }
                GridModeLayoutActivity.this.updateAfterDrag(fromPosition, toPosition);
            }

            public /* synthetic */ void

            lambda$onItemDragEnded$0$GridModeActivity$1(int toPosition, DialogInterface dialog, int which) {
                GridModeLayoutActivity.this.showBottomBar(true);
                DocumentRepository.removeDocument(GridModeLayoutActivity.this, (Document) GridModeLayoutActivity.this.m_arrData.get(toPosition));
                if (GridModeLayoutActivity.this.m_arrData.size() == 1) {
                    GridModeLayoutActivity.this.removeParent();
                    GridModeLayoutActivity.this.finish();
                } else {
                    if (toPosition == 0) {
                        GridModeLayoutActivity.this.updateParentThumb();
                    }
                    GridModeLayoutActivity.this.adapter.notifyDataSetChanged();
                }
                GridModeLayoutActivity.this.m_arrData.remove(toPosition);

                GridModeLayoutActivity.this.showBottomBar(true);
                dialog.dismiss();
            }

            public /* synthetic */ void lambda$onItemDragEnded$1$GridModeActivity$1(int fromPosition, int toPosition, DialogInterface dialog, int which) {
                GridModeLayoutActivity.this.updateAfterDrag(fromPosition, toPosition);
                dialog.dismiss();
            }
        });
        this.m_dragView.setDragListCallback(new DragListView.DragListCallback() {
            public boolean canDragItemAtPosition(int dragPosition) {
                return dragPosition != GridModeLayoutActivity.this.m_arrData.size() - 1;
            }

            public boolean canDropItemAtPosition(int dropPosition) {
                return dropPosition != GridModeLayoutActivity.this.m_arrData.size() - 1;
            }
        });
    }

    /* access modifiers changed from: private */
    public void updateAfterDrag(int fromPosition, int toPosition) {
        showBottomBar(true);
        if (fromPosition != toPosition) {
            for (int i = 0; i < this.m_arrData.size() - 1; i++) {
                Document document = this.m_arrData.get(i);
                document.sortID = i + 1;
                DBManager.getInstance().updateDocument(document);
            }
            if (toPosition == 0 || fromPosition == 0) {
                updateParentThumb();
            }
        }
    }

    public void showBottomBar(boolean bShow) {
        if (bShow) {
            this.btnCamera.setVisibility(0);
            this.btnGallery.setVisibility(0);
            this.btnShare.setVisibility(0);
        } else {
            this.btnCamera.setVisibility(View.GONE);
            this.btnGallery.setVisibility(8);
            this.btnShare.setVisibility(8);
        }
        this.adapter.showLastItem(bShow);
    }


    public /* synthetic */ void lambda$onClick$0$GridModeActivity() {
        ImagesPickerManager.startPicker((Activity) this);
    }


    public void startCamera() {
        startActivityForResult(new Intent(this, CameraOpenActivity.class), 1003);
    }

    private void displayShareDialog() {
        ExportDialogUtils.showShareDialog((FragmentActivity) this, getData(), this.m_strName);
    }

    private void displaySaveDialog() {
        ExportDialogUtils.showSaveModeDialog(this, getData(), this.m_strName);
    }

    private List<Document> getData() {
        List<Document> arrData = new ArrayList<>();
        for (int i = 0; i < this.m_arrData.size() - 1; i++) {
            arrData.add(this.m_arrData.get(i));
        }
        return arrData;
    }

    private void goToPDFSetting() {
        startActivity(new Intent(this, Setting_MyDocPropertyActivity.class));
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        saveBatchModeWithGallery(ImagesPickerManager.handlePickerMultiResults(requestCode, resultCode, data));
        if (requestCode != 1002) {
            if (requestCode != 1003) {
                if (requestCode != 1007) {
                    if (requestCode == 1010) {
                        ImageStorageUtils.clearShareFolder();
                    }
                } else if (resultCode == -1) {
                    ActionBar actionBar = getSupportActionBar();
                    if (actionBar != null) {
                        actionBar.setDisplayHomeAsUpEnabled(true);
                    }
                    this.m_arrData.clear();
                    loadDB();
                    if (this.m_arrData.size() == 1) {
                        removeParent();
                        finish();
                        return;
                    }
                    if (data != null && data.hasExtra("position") && data.getIntExtra("position", -1) == 0) {
                        updateParentThumb();
                    }
                    this.adapter.notifyDataSetChanged();
                }
            } else if (resultCode == 5) {
                Toast.makeText(this, R.string.error_can_not_take_image, 1).show();
            } else if (resultCode != -1) {
            } else {
                if (data.getFlags() == 1) {
                    PolygonUtil.insertBatch2Grid(this, (List) new Gson().fromJson(data.getStringExtra(Constant.EXTRA_DOC_ARRAY), new TypeToken<List<Document>>() {
                    }.getType()), this.m_strParent, this.m_arrData.size());
                    this.m_arrData.clear();
                    loadDB();
                    this.adapter.notifyDataSetChanged();
                    return;
                }
                CropImageActivity.cropNewFile(this, DetectionResult.FIX_RECT_MODE.FIX_RECT_CAMERA, this.m_strParent, true);
            }
        } else if (resultCode == -1) {
            this.m_arrData.clear();
            loadDB();
            this.adapter.notifyDataSetChanged();
//            ScanApplication.adsManager.show(false);
        }
    }

    /* access modifiers changed from: package-private */
    public void saveBatchModeWithGallery(final ArrayList<GalleryImage> galleryImages) {
        int cnt;
        if (galleryImages != null && (cnt = galleryImages.size()) != 0) {
            if (cnt == 1) {
                PolygonUtil.importGalleryImage(this, galleryImages.get(0).getBitmapUri());
                CropImageActivity.cropNewFile(this, DetectionResult.FIX_RECT_MODE.FIX_RECT_GALLERY, this.m_strParent, true);
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
                            PolygonUtil.copyImageFromGalleryWithAutoCrop(GridModeLayoutActivity.this, ((GalleryImage) it.next()).getBitmapUri(), docBatchList);
                        }
                        GridModeLayoutActivity gridModeActivity = GridModeLayoutActivity.this;
                        PolygonUtil.insertBatch2Grid(gridModeActivity, docBatchList, gridModeActivity.m_strParent, GridModeLayoutActivity.this.m_arrData.size());
                        GridModeLayoutActivity.this.m_arrData.clear();
                        GridModeLayoutActivity.this.loadDB();
                        GridModeLayoutActivity.this.runOnUiThread(new Runnable() {
                            public void run() {
                                dlgProgress.dismiss();
                                GridModeLayoutActivity.this.adapter.notifyDataSetChanged();
                            }
                        });
                    }
                }).start();
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void updateParentName(String parentUid, String parentName) {
        List<Document> tempArr = new ArrayList<>();
        DBManager.getInstance().searchDocument(tempArr, parentUid);
        if (tempArr.size() >= 1) {
            tempArr.get(0).name = parentName;
            DBManager.getInstance().updateDocument(tempArr.get(0));
        }
    }

    /* access modifiers changed from: package-private */
    public void updateParentThumb() {
        List<Document> tempArr = new ArrayList<>();
        DBManager.getInstance().searchDocument(tempArr, this.m_strParent);
        if (tempArr.size() >= 1) {
            tempArr.get(0).thumb = this.m_arrData.get(0).thumb;
            DBManager.getInstance().updateDocument(tempArr.get(0));
        }
    }

    /* access modifiers changed from: package-private */
    public void removeParent() {
        List<Document> tempArr = new ArrayList<>();
        DBManager.getInstance().searchDocument(tempArr, this.m_strParent);
        if (tempArr.size() >= 1) {
            DocumentRepository.removeDocument(this, tempArr.get(0));
        }
    }

    /* access modifiers changed from: protected */
    public void onStart() {
        super.onStart();
//        Analytics.get().logGridScreen();
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        super.onResume();
        this.m_bCanEdit = true;
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        this.docName.setText(this.m_strName);

        this.m_arrData.clear();
        loadDB();
        this.adapter.notifyDataSetChanged();
        SharedPrefsUtils.isFirstTimeInGrid(this);
    }

    public void onItemClicked(int position) {
        if (position == this.adapter.getItemCount() - 1) {
            onClick(this.btnCamera);
            return;
        }
        Document doc = this.m_arrData.get(position);
        doc.m_bNew = false;
        goToEditActivity(doc, position);
    }

    /* access modifiers changed from: package-private */
    public void showRenameDlg() {
        FileNameDialogUtils.showFileNameDialog(this, this.m_strName, getString(R.string.str_rename), getString(R.string.change_group_name), new FileNameDialogUtils.OnRenameListener() {
            public final void nameChanged(String str) {
                GridModeLayoutActivity.this.lambda$showRenameDlg$1$GridModeActivity(str);
            }
        });
    }

    public /* synthetic */ void lambda$showRenameDlg$1$GridModeActivity(String newName) {
        if (StringHelper.isEmpty(newName)) {
            Toast.makeText(this, getApplicationContext().getString(R.string.alert_file_name_empty), 0).show();
            return;
        }
        updateParentName(this.m_strParent, newName);
        this.m_strName = newName;
        this.docName.setText(this.m_strName);
    }

    /* access modifiers changed from: package-private */

}
