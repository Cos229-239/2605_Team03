#include <jni.h>
#include "NudgiePet.h"

// Initialize your pet globally (or attach it to a specific memory pointer)
NudgiePet myBuddy;

extern "C" JNIEXPORT void JNICALL
Java_com_yourcompany_nudgie_NudgieViewModel_completeHabitNative(JNIEnv* env, jobject /* this */) {
    myBuddy.completeHabit();
}

extern "C" JNIEXPORT jint JNICALL
Java_com_yourcompany_nudgie_NudgieViewModel_getHappinessNative(JNIEnv* env, jobject /* this */) {
    return myBuddy.getState().happiness;
}