package org.opencv.android;

import android.annotation.TargetApi;
import android.graphics.SurfaceTexture;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.Log;


import com.camscanner.paperscanner.pdfcreator.common.Constant;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

@TargetApi(15)
public abstract class CameraGLRendererBase implements GLSurfaceView.Renderer, SurfaceTexture.OnFrameAvailableListener {
    private int[] FBO = {0};
    protected final String LOGTAG = "CameraGLRendererBase";
    private final String fss2D = "precision mediump float;\nuniform sampler2D sTexture;\nvarying vec2 texCoord;\nvoid main() {\n  gl_FragColor = texture2D(sTexture,texCoord);\n}";
    private final String fssOES = "#extension GL_OES_EGL_image_external : require\nprecision mediump float;\nuniform samplerExternalOES sTexture;\nvarying vec2 texCoord;\nvoid main() {\n  gl_FragColor = texture2D(sTexture,texCoord);\n}";
    protected int mCameraHeight = -1;
    protected int mCameraIndex = -1;
    protected int mCameraWidth = -1;
    protected boolean mEnabled = true;
    protected int mFBOHeight = -1;
    protected int mFBOWidth = -1;
    protected boolean mHaveFBO = false;
    protected boolean mHaveSurface = false;
    protected boolean mIsStarted = false;
    protected int mMaxCameraHeight = -1;
    protected int mMaxCameraWidth = -1;
    protected SurfaceTexture mSTexture;
    protected boolean mUpdateST = false;
    protected CameraGLSurfaceView mView;
    private int prog2D = -1;
    private int progOES = -1;
    private FloatBuffer tex2D;
    private int[] texCamera = {0};
    private final float[] texCoord2D = {0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f};
    private final float[] texCoordOES = {0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f, 0.0f};
    private int[] texDraw = {0};
    private int[] texFBO = {0};
    private FloatBuffer texOES;
    private int vPos2D;
    private int vPosOES;
    private int vTC2D;
    private int vTCOES;
    private FloatBuffer vert;
    private final float[] vertices = {-1.0f, -1.0f, -1.0f, 1.0f, 1.0f, -1.0f, 1.0f, 1.0f};
    private final String vss = "attribute vec2 vPosition;\nattribute vec2 vTexCoord;\nvarying vec2 texCoord;\nvoid main() {\n  texCoord = vTexCoord;\n  gl_Position = vec4 ( vPosition.x, vPosition.y, 0.0, 1.0 );\n}";


    public abstract void closeCamera();


    public abstract void openCamera(int i);


    public abstract void setCameraPreviewSize(int i, int i2);

    public CameraGLRendererBase(CameraGLSurfaceView view) {
        this.mView = view;
        int bytes = (this.vertices.length * 32) / 8;
        this.vert = ByteBuffer.allocateDirect(bytes).order(ByteOrder.nativeOrder()).asFloatBuffer();
        this.texOES = ByteBuffer.allocateDirect(bytes).order(ByteOrder.nativeOrder()).asFloatBuffer();
        this.tex2D = ByteBuffer.allocateDirect(bytes).order(ByteOrder.nativeOrder()).asFloatBuffer();
        this.vert.put(this.vertices).position(0);
        this.texOES.put(this.texCoordOES).position(0);
        this.tex2D.put(this.texCoord2D).position(0);
    }

    public synchronized void onFrameAvailable(SurfaceTexture surfaceTexture) {
        this.mUpdateST = true;
        this.mView.requestRender();
    }

    public void onDrawFrame(GL10 gl) {
        if (this.mHaveFBO) {
            synchronized (this) {
                if (this.mUpdateST) {
                    this.mSTexture.updateTexImage();
                    this.mUpdateST = false;
                }
                GLES20.glClear(16384);
                CameraGLSurfaceView.CameraTextureListener texListener = this.mView.getCameraTextureListener();
                if (texListener != null) {
                    drawTex(this.texCamera[0], true, this.FBO[0]);
                    if (texListener.onCameraTexture(this.texFBO[0], this.texDraw[0], this.mCameraWidth, this.mCameraHeight)) {
                        drawTex(this.texDraw[0], false, 0);
                    } else {
                        drawTex(this.texFBO[0], false, 0);
                    }
                } else {
                    Log.d("CameraGLRendererBase", "texCamera(OES) -> screen");
                    drawTex(this.texCamera[0], true, 0);
                }
            }
        }
    }

    public void onSurfaceChanged(GL10 gl, int surfaceWidth, int surfaceHeight) {
        Log.i("CameraGLRendererBase", "onSurfaceChanged(" + surfaceWidth + "x" + surfaceHeight + Constant.STR_BRACKET_CLOSE);
        this.mHaveSurface = true;
        updateState();
        setPreviewSize(surfaceWidth, surfaceHeight);
    }

    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        Log.i("CameraGLRendererBase", "onSurfaceCreated");
        initShaders();
    }

    private void initShaders() {
        String strGLVersion = GLES20.glGetString(7938);
        if (strGLVersion != null) {
            Log.i("CameraGLRendererBase", "OpenGL ES version: " + strGLVersion);
        }
        GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
        this.progOES = loadShader("attribute vec2 vPosition;\nattribute vec2 vTexCoord;\nvarying vec2 texCoord;\nvoid main() {\n  texCoord = vTexCoord;\n  gl_Position = vec4 ( vPosition.x, vPosition.y, 0.0, 1.0 );\n}", "#extension GL_OES_EGL_image_external : require\nprecision mediump float;\nuniform samplerExternalOES sTexture;\nvarying vec2 texCoord;\nvoid main() {\n  gl_FragColor = texture2D(sTexture,texCoord);\n}");
        this.vPosOES = GLES20.glGetAttribLocation(this.progOES, "vPosition");
        this.vTCOES = GLES20.glGetAttribLocation(this.progOES, "vTexCoord");
        GLES20.glEnableVertexAttribArray(this.vPosOES);
        GLES20.glEnableVertexAttribArray(this.vTCOES);
        this.prog2D = loadShader("attribute vec2 vPosition;\nattribute vec2 vTexCoord;\nvarying vec2 texCoord;\nvoid main() {\n  texCoord = vTexCoord;\n  gl_Position = vec4 ( vPosition.x, vPosition.y, 0.0, 1.0 );\n}", "precision mediump float;\nuniform sampler2D sTexture;\nvarying vec2 texCoord;\nvoid main() {\n  gl_FragColor = texture2D(sTexture,texCoord);\n}");
        this.vPos2D = GLES20.glGetAttribLocation(this.prog2D, "vPosition");
        this.vTC2D = GLES20.glGetAttribLocation(this.prog2D, "vTexCoord");
        GLES20.glEnableVertexAttribArray(this.vPos2D);
        GLES20.glEnableVertexAttribArray(this.vTC2D);
    }

    private void initSurfaceTexture() {
        Log.d("CameraGLRendererBase", "initSurfaceTexture");
        deleteSurfaceTexture();
        initTexOES(this.texCamera);
        this.mSTexture = new SurfaceTexture(this.texCamera[0]);
        this.mSTexture.setOnFrameAvailableListener(this);
    }

    private void deleteSurfaceTexture() {
        Log.d("CameraGLRendererBase", "deleteSurfaceTexture");
        SurfaceTexture surfaceTexture = this.mSTexture;
        if (surfaceTexture != null) {
            surfaceTexture.release();
            this.mSTexture = null;
            deleteTex(this.texCamera);
        }
    }

    private void initTexOES(int[] tex) {
        if (tex.length == 1) {
            GLES20.glGenTextures(1, tex, 0);
            GLES20.glBindTexture(36197, tex[0]);
            GLES20.glTexParameteri(36197, 10242, 33071);
            GLES20.glTexParameteri(36197, 10243, 33071);
            GLES20.glTexParameteri(36197, 10241, 9728);
            GLES20.glTexParameteri(36197, 10240, 9728);
        }
    }

    private static void deleteTex(int[] tex) {
        if (tex.length == 1) {
            GLES20.glDeleteTextures(1, tex, 0);
        }
    }

    private static int loadShader(String vss2, String fss) {
        Log.d("CameraGLRendererBase", "loadShader");
        int vshader = GLES20.glCreateShader(35633);
        GLES20.glShaderSource(vshader, vss2);
        GLES20.glCompileShader(vshader);
        int[] status = new int[1];
        GLES20.glGetShaderiv(vshader, 35713, status, 0);
        if (status[0] == 0) {
            Log.e("CameraGLRendererBase", "Could not compile vertex shader: " + GLES20.glGetShaderInfoLog(vshader));
            GLES20.glDeleteShader(vshader);
            return 0;
        }
        int fshader = GLES20.glCreateShader(35632);
        GLES20.glShaderSource(fshader, fss);
        GLES20.glCompileShader(fshader);
        GLES20.glGetShaderiv(fshader, 35713, status, 0);
        if (status[0] == 0) {
            Log.e("CameraGLRendererBase", "Could not compile fragment shader:" + GLES20.glGetShaderInfoLog(fshader));
            GLES20.glDeleteShader(vshader);
            GLES20.glDeleteShader(fshader);
            return 0;
        }
        int program = GLES20.glCreateProgram();
        GLES20.glAttachShader(program, vshader);
        GLES20.glAttachShader(program, fshader);
        GLES20.glLinkProgram(program);
        GLES20.glDeleteShader(vshader);
        GLES20.glDeleteShader(fshader);
        GLES20.glGetProgramiv(program, 35714, status, 0);
        if (status[0] == 0) {
            Log.e("CameraGLRendererBase", "Could not link shader program: " + GLES20.glGetProgramInfoLog(program));
            return 0;
        }
        GLES20.glValidateProgram(program);
        GLES20.glGetProgramiv(program, 35715, status, 0);
        if (status[0] == 0) {
            Log.e("CameraGLRendererBase", "Shader program validation error: " + GLES20.glGetProgramInfoLog(program));
            GLES20.glDeleteProgram(program);
            return 0;
        }
        Log.d("CameraGLRendererBase", "Shader program is built OK");
        return program;
    }

    private void deleteFBO() {
        Log.d("CameraGLRendererBase", "deleteFBO(" + this.mFBOWidth + "x" + this.mFBOHeight + Constant.STR_BRACKET_CLOSE);
        GLES20.glBindFramebuffer(36160, 0);
        GLES20.glDeleteFramebuffers(1, this.FBO, 0);
        deleteTex(this.texFBO);
        deleteTex(this.texDraw);
        this.mFBOHeight = 0;
        this.mFBOWidth = 0;
    }

    private void initFBO(int width, int height) {
        int i = width;
        int i2 = height;
        Log.d("CameraGLRendererBase", "initFBO(" + i + "x" + i2 + Constant.STR_BRACKET_CLOSE);
        deleteFBO();
        GLES20.glGenTextures(1, this.texDraw, 0);
        GLES20.glBindTexture(3553, this.texDraw[0]);
        GLES20.glTexImage2D(3553, 0, 6408, width, height, 0, 6408, 5121, (Buffer) null);
        GLES20.glTexParameteri(3553, 10242, 33071);
        GLES20.glTexParameteri(3553, 10243, 33071);
        GLES20.glTexParameteri(3553, 10241, 9728);
        GLES20.glTexParameteri(3553, 10240, 9728);
        GLES20.glGenTextures(1, this.texFBO, 0);
        GLES20.glBindTexture(3553, this.texFBO[0]);
        GLES20.glTexImage2D(3553, 0, 6408, width, height, 0, 6408, 5121, (Buffer) null);
        GLES20.glTexParameteri(3553, 10242, 33071);
        GLES20.glTexParameteri(3553, 10243, 33071);
        GLES20.glTexParameteri(3553, 10241, 9728);
        GLES20.glTexParameteri(3553, 10240, 9728);
        GLES20.glGenFramebuffers(1, this.FBO, 0);
        GLES20.glBindFramebuffer(36160, this.FBO[0]);
        GLES20.glFramebufferTexture2D(36160, 36064, 3553, this.texFBO[0], 0);
        Log.d("CameraGLRendererBase", "initFBO error status: " + GLES20.glGetError());
        int FBOstatus = GLES20.glCheckFramebufferStatus(36160);
        if (FBOstatus != 36053) {
            Log.e("CameraGLRendererBase", "initFBO failed, status: " + FBOstatus);
        }
        this.mFBOWidth = i;
        this.mFBOHeight = i2;
    }

    private void drawTex(int tex, boolean isOES, int fbo) {
        int i = tex;
        int i2 = fbo;
        GLES20.glBindFramebuffer(36160, i2);
        if (i2 == 0) {
            GLES20.glViewport(0, 0, this.mView.getWidth(), this.mView.getHeight());
        } else {
            GLES20.glViewport(0, 0, this.mFBOWidth, this.mFBOHeight);
        }
        GLES20.glClear(16384);
        if (isOES) {
            GLES20.glUseProgram(this.progOES);
            GLES20.glVertexAttribPointer(this.vPosOES, 2, 5126, false, 8, this.vert);
            GLES20.glVertexAttribPointer(this.vTCOES, 2, 5126, false, 8, this.texOES);
        } else {
            GLES20.glUseProgram(this.prog2D);
            GLES20.glVertexAttribPointer(this.vPos2D, 2, 5126, false, 8, this.vert);
            GLES20.glVertexAttribPointer(this.vTC2D, 2, 5126, false, 8, this.tex2D);
        }
        GLES20.glActiveTexture(33984);
        if (isOES) {
            GLES20.glBindTexture(36197, i);
            GLES20.glUniform1i(GLES20.glGetUniformLocation(this.progOES, "sTexture"), 0);
        } else {
            GLES20.glBindTexture(3553, i);
            GLES20.glUniform1i(GLES20.glGetUniformLocation(this.prog2D, "sTexture"), 0);
        }
        GLES20.glDrawArrays(5, 0, 4);
        GLES20.glFlush();
    }

    public synchronized void enableView() {
        Log.d("CameraGLRendererBase", "enableView");
        this.mEnabled = true;
        updateState();
    }

    public synchronized void disableView() {
        Log.d("CameraGLRendererBase", "disableView");
        this.mEnabled = false;
        updateState();
    }


    public void updateState() {
        Log.d("CameraGLRendererBase", "updateState");
        Log.d("CameraGLRendererBase", "mEnabled=" + this.mEnabled + ", mHaveSurface=" + this.mHaveSurface);
        boolean willStart = this.mEnabled && this.mHaveSurface && this.mView.getVisibility() == 0;
        if (willStart == this.mIsStarted) {
            Log.d("CameraGLRendererBase", "keeping State unchanged");
        } else if (willStart) {
            doStart();
        } else {
            doStop();
        }
        Log.d("CameraGLRendererBase", "updateState end");
    }


    public synchronized void doStart() {
        Log.d("CameraGLRendererBase", "doStart");
        initSurfaceTexture();
        openCamera(this.mCameraIndex);
        this.mIsStarted = true;
        if (this.mCameraWidth > 0 && this.mCameraHeight > 0) {
            setPreviewSize(this.mCameraWidth, this.mCameraHeight);
        }
    }


    public void doStop() {
        Log.d("CameraGLRendererBase", "doStop");
        synchronized (this) {
            this.mUpdateST = false;
            this.mIsStarted = false;
            this.mHaveFBO = false;
            closeCamera();
            deleteSurfaceTexture();
        }
        CameraGLSurfaceView.CameraTextureListener listener = this.mView.getCameraTextureListener();
        if (listener != null) {
            listener.onCameraViewStopped();
        }
    }


    public void setPreviewSize(int width, int height) {
        synchronized (this) {
            this.mHaveFBO = false;
            this.mCameraWidth = width;
            this.mCameraHeight = height;
            setCameraPreviewSize(width, height);
            initFBO(this.mCameraWidth, this.mCameraHeight);
            this.mHaveFBO = true;
        }
        CameraGLSurfaceView.CameraTextureListener listener = this.mView.getCameraTextureListener();
        if (listener != null) {
            listener.onCameraViewStarted(this.mCameraWidth, this.mCameraHeight);
        }
    }

    public void setCameraIndex(int cameraIndex) {
        disableView();
        this.mCameraIndex = cameraIndex;
        enableView();
    }

    public void setMaxCameraPreviewSize(int maxWidth, int maxHeight) {
        disableView();
        this.mMaxCameraWidth = maxWidth;
        this.mMaxCameraHeight = maxHeight;
        enableView();
    }

    public void onResume() {
        Log.i("CameraGLRendererBase", "onResume");
    }

    public void onPause() {
        Log.i("CameraGLRendererBase", "onPause");
        this.mHaveSurface = false;
        updateState();
        this.mCameraHeight = -1;
        this.mCameraWidth = -1;
    }
}
