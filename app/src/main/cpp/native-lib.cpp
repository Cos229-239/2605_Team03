#include <jni.h>
#include "../../../../.github/workflows/UI and stats/header/NudgiePet.h"

// Initialize your pet globally (or attach it to a specific memory pointer)
NudgiePet myBuddy;

extern "C" JNIEXPORT void JNICALL
Java_com_nightowlcrew_nudgie_NudgieViewModel_completeHabitNative(JNIEnv* env, jobject) {
    myBuddy.completeHabit();
}

extern "C" JNIEXPORT jint JNICALL
Java_com_nightowlcrew_nudgie_NudgieViewModel_getHappinessNative(JNIEnv* env, jobject) {
    return myBuddy.getState().happiness;
}

extern "C" JNIEXPORT jint JNICALL
Java_com_nightowlcrew_nudgie_NudgieViewModel_getEnergyNative(JNIEnv* env, jobject) {
    return myBuddy.getState().energy;
}

extern "C" JNIEXPORT void JNICALL
Java_com_nightowlcrew_nudgie_NudgieViewModel_missedHabitNative(JNIEnv* env, jobject) {
    myBuddy.missedHabit();
}