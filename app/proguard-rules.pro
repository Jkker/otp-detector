# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /home/jkker/android-sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.kts.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# Keep the Xposed Module entry point class
-keep class com.example.otpdetector.OtpModule { *; }

# Keep other classes if needed (e.g. if accessed via reflection)
# -keep class com.example.otpdetector.MyClass { *; }
