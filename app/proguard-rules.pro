# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile


#
#-dontwarn
#-ignorewarnings
#
## RetroFit
#-dontwarn retrofit.**
#-keep class retrofit.** { *; }
#-keepattributes Signature
#-keepattributes Exceptions
#
## RenderScript
#-keepclasseswithmembernames class * {
#native <methods>;
#}
#-keep class androidx.renderscript.** { *; }
#
## Glide
#-keep public class * implements com.bumptech.glide.module.GlideModule
#-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
#    **[] $VALUES;
#    public *;
#}
#
## ButterKnife
#-keep class butterknife.** { *; }
#-dontwarn butterknife.internal.**
#-keep class **$$ViewBinder { *; }
#-keepclasseswithmembernames class * {
#    @butterknife.* <fields>;
#}
#-keepclasseswithmembernames class * {
#    @butterknife.* <methods>;
#}
#
#-keep public class com.facebook.ads.** {
#   public *;
#}
#
#-keep class com.google.ads.mediation.facebook.FacebookAdapter {
#    *;
#}
#-dontwarn com.facebook.ads.internal.**
#
#-dontwarn android.support.v4.**
#-keep class android.support.v4.** { *; }
#-keep class org.jaudiotagger.** { *; }
#
#-keep class * extends java.util.ListResourceBundle {
#    protected Object[][] getContents();
#}
#
#-keep public class com.google.android.gms.common.internal.safeparcel.SafeParcelable {
#    public static final *** NULL;
#}
#
#-keepnames @com.google.android.gms.common.annotation.KeepName class *
#-keepclassmembernames class * {
#    @com.google.android.gms.common.annotation.KeepName *;
#}
#
#-keepnames class * implements android.os.Parcelable {
#    public static final ** CREATOR;
#}
#
#-keep public class * extends androidx.versionedparcelable.VersionedParcelable {
#  <init>();
#}
#
#-dontwarn com.squareup.okhttp.**
#-dontwarn com.google.android.flexbox.**
#-dontwarn com.sun.mail.imap.**
#
#-dontwarn java.awt.**
#-dontwarn java.beans.Beans
#-dontwarn javax.security.**
#-keep class javamail.** {*;}
#-keep class javax.mail.** {*;}
#-keep class javax.activation.** {*;}
#-keep class com.sun.mail.dsn.** {*;}
#-keep class com.sun.mail.handlers.** {*;}
#-keep class com.sun.mail.smtp.** {*;}
#-keep class com.sun.mail.util.** {*;}
#-keep class mailcap.** {*;}
#-keep class mimetypes.** {*;}
#-keep class myjava.awt.datatransfer.** {*;}
#-keep class org.apache.harmony.awt.** {*;}
#-keep class org.apache.harmony.misc.** {*;}
#-keep class com.alibaba.** {*;}
#-keep class pic.video.status.videomaker.vidify.video.MyPagerFragment.** {*;}
#
#-dontwarn com.google.ads.mediation.**
#-dontwarn okio.**
#-dontwarn com.alibaba.**
#-keep class com.alibaba.sdk.android.feedback.** {*;}
#-keep public class com.google.ads.** {
#   public *;
#}
#
#-keep class org.apache.** { *; }
#-dontwarn org.apache.**
#
#-keep public class android.support.v7.widget.** { *; }
#-keep public class android.support.v7.internal.widget.** { *; }
#-keep public class android.support.v7.internal.view.menu.** { *; }
#
#-keep public class * extends android.support.v4.view.ActionProvider {
#    public <init>(android.content.Context);
#}
#-keep class android.support.v7.widget.RoundRectDrawable { *; }
#
#-keep public class * implements com.bumptech.glide.module.GlideModule
#-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
#    **[] $VALUES;
#    public *;
#}
#
#-keep public class org.jsoup.** {
#public *;
#}
#
#-keepattributes SourceFile,LineNumberTable
#-keepattributes JavascriptInterface
#-keep class android.webkit.JavascriptInterface {
#   *;
#}
#
##-keep class media.musicplayer.mp3.music.sound.mp3player.** { *; }
#-keep class com.google.android.gms.** { *; }
#-keep class android.graphics.** { *; }
#-keep public class com.google.ads.mediation. { public *; }
#-keep class hb.xvideoplayer.* { *; }
#-keep class mobi.charmer.ffplayerlib.touchsticker.* { *; }
#-keep class android.support.rastermill.* { *; }
#-dontwarn tv.danmaku.ijk.media.player.**
#-keep class tv.danmaku.ijk.media.player.** { *; }
#-keep interface tv.danmaku.ijk.media.player.* { *; }
#
#-keepattributes Signature
#-keepattributes *Annotation*
#-keep class okhttp3.** { *; }
#-keep interface okhttp3.** { *; }
#-dontwarn okhttp3.**
#
#-dontwarn vn.tungdx.mediapicker.imageloader.**
#-dontwarn com.googlecode.mp4parser.**
#
#-keep class com.facebook.ads.* { *; }
#-keep class com.facebook.* { *; }
#-keepattributes Signature
#-keep class com.google.android.exoplayer2.* { *; }
#-dontwarn com.google.android.exoplayer2.**
#-keep class net.idik.lib.cipher.** { *; }
#-keep class com.dotcom.photoframes.ClassThatUsesObjectAnimator { *; }
#
##ironsource
#-keepclassmembers class com.ironsource.sdk.controller.IronSourceWebView$JSInterface {
#    public *;
#}
#-keepclassmembers class * implements android.os.Parcelable {
#    public static final android.os.Parcelable$Creator *;
#}
#-dontwarn com.ironsource.adapters.**
#-keep class com.ironsource.mediationsdk.* { *; }
#-dontwarn com.ironsource.mediationsdk.**
#
#-keepclassmembers class com.ironsource.sdk.controller.IronSourceWebView$JSInterface {
#    public *;
#}
#-keepclassmembers class * implements android.os.Parcelable {
#    public static final android.os.Parcelable$Creator *;
#}
#-keep public class com.google.android.gms.ads.** {
#   public *;
#}
#-keep class com.ironsource.adapters.** { *;
#}
#-dontwarn com.moat.**
#-keep class com.moat.** { public protected private *; }
#
#-repackageclasses ''
##-allowaccessmodification
#-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*,!code/allocation/variable
#-keepattributes *Annotation*
##-dontskipnonpubliclibraryclasses
#-optimizationpasses 5
#-printmapping map.txt
#-flattenpackagehierarchy
##-overloadaggressively
#-mergeinterfacesaggressively
#
#
#-optimizationpasses 5
#-keepattributes SourceFile,LineNumberTable,Exceptions, Signature, InnerClasses,*Annotation*
#-keep class com.google.android.gms.** { *; }
#-dontwarn com.google.android.gms.**
#
#
#
#
#
#-dontobfuscate
#
#-keep,allowobfuscation @interface com.facebook.proguard.annotations.DoNotStrip
#-keep,allowobfuscation @interface com.facebook.proguard.annotations.KeepGettersAndSetters
#-keep,allowobfuscation @interface com.facebook.common.internal.DoNotStrip
#
## Do not strip any method/class that is annotated with @DoNotStrip
#-keep @com.facebook.proguard.annotations.DoNotStrip class *
#-keep @com.facebook.common.internal.DoNotStrip class *
#-keepclassmembers class * {
#    @com.facebook.proguard.annotations.DoNotStrip *;
#    @com.facebook.common.internal.DoNotStrip *;
#}
#
#-keepclassmembers @com.facebook.proguard.annotations.KeepGettersAndSetters class * {
#  void set*(***);
#  *** get*();
#}
#
#-keep class * extends com.facebook.react.bridge.JavaScriptModule { *; }
#-keep class * extends com.facebook.react.bridge.NativeModule { *; }
#-keepclassmembers,includedescriptorclasses class * { native <methods>; }
#-keepclassmembers class *  { @com.facebook.react.uimanager.UIProp <fields>; }
#-keepclassmembers class *  { @com.facebook.react.uimanager.annotations.ReactProp <methods>; }
#-keepclassmembers class *  { @com.facebook.react.uimanager.annotations.ReactPropGroup <methods>; }
#
#-dontwarn com.facebook.react.**
#
## TextLayoutBuilder uses a non-public Android constructor within StaticLayout.
## See libs/proxy/src/main/java/com/facebook/fbui/textlayoutbuilder/proxy for details.
#-dontwarn android.text.StaticLayout
#
## okhttp
#
#-keepattributes Signature
#-keepattributes *Annotation*
#-keep class okhttp3.** { *; }
#-keep interface okhttp3.** { *; }
#-dontwarn okhttp3.**
#
## okio
#
#-keep class sun.misc.Unsafe { *; }
#-dontwarn java.nio.file.*
#-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement
#-dontwarn okio.**



-dontwarn
-ignorewarnings

# RetroFit
-dontwarn retrofit.**
-keep class retrofit.** { *; }
-keepattributes Signature
-keepattributes Exceptions

# RenderScript
-keepclasseswithmembernames class * {
native <methods>;
}
-keep class androidx.renderscript.** { *; }

# Glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
    **[] $VALUES;
    public *;
}

# ButterKnife
-keep class butterknife.** { *; }
-dontwarn butterknife.internal.**
-keep class **$$ViewBinder { *; }
-keepclasseswithmembernames class * {
    @butterknife.* <fields>;
}
-keepclasseswithmembernames class * {
    @butterknife.* <methods>;
}

-keep public class com.facebook.ads.** {
   public *;
}

-keep class com.google.ads.mediation.facebook.FacebookAdapter {
    *;
}
-dontwarn com.facebook.ads.internal.**

-dontwarn android.support.v4.**
-keep class android.support.v4.** { *; }
-keep class org.jaudiotagger.** { *; }

-keep class * extends java.util.ListResourceBundle {
    protected Object[][] getContents();
}

-keep public class com.google.android.gms.common.internal.safeparcel.SafeParcelable {
    public static final *** NULL;
}

-keepnames @com.google.android.gms.common.annotation.KeepName class *
-keepclassmembernames class * {
    @com.google.android.gms.common.annotation.KeepName *;
}

-keepnames class * implements android.os.Parcelable {
    public static final ** CREATOR;
}

-keep public class * extends androidx.versionedparcelable.VersionedParcelable {
  <init>();
}

-dontwarn com.squareup.okhttp.**
-dontwarn com.google.android.flexbox.**
-dontwarn com.sun.mail.imap.**

-dontwarn java.awt.**
-dontwarn java.beans.Beans
-dontwarn javax.security.**
-keep class javamail.** {*;}
-keep class javax.mail.** {*;}
-keep class javax.activation.** {*;}
-keep class com.sun.mail.dsn.** {*;}
-keep class com.sun.mail.handlers.** {*;}
-keep class com.sun.mail.smtp.** {*;}
-keep class com.sun.mail.util.** {*;}
-keep class mailcap.** {*;}
-keep class mimetypes.** {*;}
-keep class myjava.awt.datatransfer.** {*;}
-keep class org.apache.harmony.awt.** {*;}
-keep class org.apache.harmony.misc.** {*;}
-keep class com.alibaba.** {*;}
-keep class pic.video.status.videomaker.vidify.video.MyPagerFragment.** {*;}

-dontwarn com.google.ads.mediation.**
-dontwarn okio.**
-dontwarn com.alibaba.**
-keep class com.alibaba.sdk.android.feedback.** {*;}
-keep public class com.google.ads.** {
   public *;
}

-keep class org.apache.** { *; }
-dontwarn org.apache.**

-keep public class android.support.v7.widget.** { *; }
-keep public class android.support.v7.internal.widget.** { *; }
-keep public class android.support.v7.internal.view.menu.** { *; }

-keep public class * extends android.support.v4.view.ActionProvider {
    public <init>(android.content.Context);
}
-keep class android.support.v7.widget.RoundRectDrawable { *; }

-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
    **[] $VALUES;
    public *;
}

-keep public class org.jsoup.** {
public *;
}

-keepattributes SourceFile,LineNumberTable
-keepattributes JavascriptInterface
-keep class android.webkit.JavascriptInterface {
   *;
}

#-keep class media.musicplayer.mp3.music.sound.mp3player.** { *; }
-keep class com.google.android.gms.** { *; }
-keep class android.graphics.** { *; }
#-keep public class com.google.ads.mediation. { public *; }
-keep class hb.xvideoplayer.* { *; }
-keep class mobi.charmer.ffplayerlib.touchsticker.* { *; }
-keep class android.support.rastermill.* { *; }
-dontwarn tv.danmaku.ijk.media.player.**
-keep class tv.danmaku.ijk.media.player.** { *; }
-keep interface tv.danmaku.ijk.media.player.* { *; }

-keepattributes Signature
-keepattributes *Annotation*
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }
-dontwarn okhttp3.**

-dontwarn vn.tungdx.mediapicker.imageloader.**
-dontwarn com.googlecode.mp4parser.**

-keep class com.facebook.ads.* { *; }
-keep class com.facebook.* { *; }
-keepattributes Signature
-keep class com.google.android.exoplayer2.* { *; }
-dontwarn com.google.android.exoplayer2.**
-keep class net.idik.lib.cipher.** { *; }
-keep class com.dotcom.photoframes.ClassThatUsesObjectAnimator { *; }

#ironsource
-keepclassmembers class com.ironsource.sdk.controller.IronSourceWebView$JSInterface {
    public *;
}
-keepclassmembers class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}
-dontwarn com.ironsource.adapters.**
-keep class com.ironsource.mediationsdk.* { *; }
-dontwarn com.ironsource.mediationsdk.**

-keepclassmembers class com.ironsource.sdk.controller.IronSourceWebView$JSInterface {
    public *;
}
-keepclassmembers class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}
-keep public class com.google.android.gms.ads.** {
   public *;
}
-keep class com.ironsource.adapters.** { *;
}
-dontwarn com.moat.**
-keep class com.moat.** { public protected private *; }

-repackageclasses ''
#-allowaccessmodification
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*,!code/allocation/variable
-keepattributes *Annotation*
#-dontskipnonpubliclibraryclasses
-optimizationpasses 5
-printmapping map.txt
-flattenpackagehierarchy
#-overloadaggressively
-mergeinterfacesaggressively


-keep class com.google.gson.excom.camscanner.docscanner.pdfcreator.features.ocr.model** { *; }
-keep class com.camscanner.paperscanner.pdfcreator.features.ocr** { *; }


#-keep class maxy.whatsweb.scan.Barcode.model.**
#-keep class maxy.whatsweb.scan.Barcode.model.Product.**
#-keep class maxy.whatsweb.scan.Barcode.activity.**
#-keep class maxy.whatsweb.scan.Barcode.fragment.**
#-keep class com.edwardvanraak.materialbarcodescanner.MaterialBarcodeScanner.**

-keepattributes *Annotation*
-keepclassmembers class ** {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }


#-keep class com.edwardvanraak.materialbarcodescanner.MaterialBarcodeScanner.* { ; }
#-dontwarn maxy.whatsweb.scan.Barcode.model.**

#-keep class videoeditor.mp3videoconverter.videotomp3converter.videotomp3.model.* { ; }
#-dontwarn videoeditor.mp3videoconverter.videotomp3converter.videotomp3.model.**