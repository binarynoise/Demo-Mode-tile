# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /usr/local/opt/android-sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

-dontobfuscate
# -dontpreverify

-keepattributes SourceFile, LineNumberTable, Exception, *Annotation*, InnerClasses, EnclosingMethod, Signature

-keepclassmembers, allowoptimization enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keepclassmembers, allowoptimization class ** extends androidx.viewbinding.ViewBinding {
	public static ** inflate(android.view.LayoutInflater);
	public static ** bind(android.view.View);
}
