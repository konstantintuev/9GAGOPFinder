
#include <jni.h>
#include <string>
#include <stdlib.h>


class regex;

extern "C" JNIEXPORT void JNICALL Java_tuev_konstantin_a9gagopfinder_fuckyou_systemcall(JNIEnv *env, jobject jobj, jstring path) {

    const char *nat = (env)->GetStringUTFChars(path, false);


    std::string string = "";
    string+="am start -n com.ninegag.android.app/.ui.UserProfileListActivity -a android.intent.action.MAIN -e group_id 0 -e tag profile-main -e account_id ";
    string+=nat;
    string+=" --ei profile_type 1 --ez opFind true";
    jclass onR = (env)->FindClass("tuev/konstantin/a9gagopfinder/RootTools");
    jmethodID ready = env->GetStaticMethodID(onR, "getShell", "(Z)Ltuev/konstantin/a9gagopfinder/execution/Shell;");
    jobject shell= env->CallStaticObjectMethod(onR, ready, true);
    if(env->ExceptionOccurred())
    {
        env->ExceptionClear();
    }
    jclass sh = (env)->FindClass("tuev/konstantin/a9gagopfinder/execution/Shell");
    jmethodID Add = env->GetMethodID(sh, "add", "(Ltuev/konstantin/a9gagopfinder/execution/Command;)Ltuev/konstantin/a9gagopfinder/execution/Command;");
    jclass Command = env->FindClass("tuev/konstantin/a9gagopfinder/execution/Command");
    jmethodID methodIDComp = (env)->GetMethodID(Command, "<init>", "(ILjava/lang/String;)V");
    jobject cn=(env)->NewObject(Command, methodIDComp, 0, env->NewStringUTF(string.data()));
    env->CallObjectMethod(shell, Add, cn);
}

extern "C" JNIEXPORT void JNICALL Java_tuev_konstantin_a9gagopfinder_fuckyou_modcall(JNIEnv *env, jobject jobj, jstring path, jobject activity) {
    jclass intentClass = env->FindClass("android/content/Intent");
    jmethodID methodID = (env)->GetMethodID(intentClass, "<init>", "()V");
    jobject a=(env)->NewObject(intentClass, methodID);
    jclass comp = env->FindClass("android/content/ComponentName");
    jmethodID methodIDComp = (env)->GetMethodID(comp, "<init>", "(Ljava/lang/String;Ljava/lang/String;)V");
    jobject cn=(env)->NewObject(comp, methodIDComp, env->NewStringUTF("com.ninegag.modded.app"), env->NewStringUTF("com.ninegag.modded.app.ui.UserProfileListActivity"));
    jmethodID setComponent = env->GetMethodID(intentClass, "setComponent","(Landroid/content/ComponentName;)Landroid/content/Intent;");
    jmethodID putIntExtra = env->GetMethodID(intentClass, "putExtra","(Ljava/lang/String;I)Landroid/content/Intent;");
    jmethodID putStringExtra = env->GetMethodID(intentClass, "putExtra","(Ljava/lang/String;Ljava/lang/String;)Landroid/content/Intent;");
    jmethodID putBoolExtra = env->GetMethodID(intentClass, "putExtra","(Ljava/lang/String;Z)Landroid/content/Intent;");
    jclass activityClass = env->FindClass("android/app/Activity");
    jmethodID startActivity = env->GetMethodID(activityClass,"startActivity", "(Landroid/content/Intent;)V");
    env->CallObjectMethod(a, putStringExtra, env->NewStringUTF("group_id"), env->NewStringUTF("0"));
    env->CallObjectMethod(a, putStringExtra, env->NewStringUTF("tag"), env->NewStringUTF("profile-main"));
    env->CallObjectMethod(a, putStringExtra, env->NewStringUTF("account_id"), path);
    env->CallObjectMethod(a, setComponent, cn);
    env->CallObjectMethod(a, putIntExtra, env->NewStringUTF("profile_type"), 1);
    env->CallObjectMethod(a, putBoolExtra, env->NewStringUTF("opFind"), true);
    env->CallVoidMethod(activity, startActivity, a);
}

extern "C" JNIEXPORT bool JNICALL Java_tuev_konstantin_a9gagopfinder_fuckyou_cell(JNIEnv *env, jobject jobj, jstring path) {

    const char *nat = (env)->GetStringUTFChars(path, false);


    std::string string = nat;
    return string.find("opClientId") != std::string::npos;
}

void replaceAll(std::string& str, const std::string& from, const std::string& to) {
    if(from.empty())
        return;
    size_t start_pos = 0;
    while((start_pos = str.find(from, start_pos)) != std::string::npos) {
        str.replace(start_pos, from.length(), to);
        start_pos += to.length(); // In case 'to' contains 'from', like replacing 'x' with 'yx'
    }
}


extern "C" JNIEXPORT jstring JNICALL Java_tuev_konstantin_a9gagopfinder_fuckyou_cr(JNIEnv *env, jobject jobj, jstring path) {

    const char *nat = (env)->GetStringUTFChars(path, false);
    try {
        std::string str = nat;
        std::string b = "\'opClientId\': \'";
        unsigned long first = str.find(b);
        std::string e = "\',";
        unsigned long last = str.find_last_of(e);
        std::string strNew = str.substr(first+b.length(), last-(first+b.length()+1));
        return env->NewStringUTF(strNew.data());
    } catch (...) {
        return env->NewStringUTF("");
    }
}