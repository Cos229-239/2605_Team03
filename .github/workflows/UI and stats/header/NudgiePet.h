#pragma once
#include <functional>

//  The data structure holding Nudgie's current status
struct NudgieState {
    int happiness = 85;
    int energy = 62;
    int level = 1;
    int ageDays = 1;
    int currentXP = 0;
    int xpToNextLevel = 100;
};

// The core logic engine
class NudgiePet {
private:
    NudgieState state;

    // Callback function to let Android's UI know stats have updated
    std::function<void(const NudgieState&)> onStateChanged;

    // Internal helper to trigger the UI update
    void notifyUI();

public:
    // UI Binding & State Retrieval
    void setOnStateChangedCallback(std::function<void(const NudgieState&)> callback);
    NudgieState getState() const;

    //  Energy Logic 
    void drainEnergy(int amount);
    void rechargeEnergy();
    void wakeUpPet(); // Used by the OS Alarm Clock

    //  Habit & Happiness Logic
    void missedHabit();
    void completeHabit();
    void updateHappiness(int amount);

    // Growth & Progression Logic 
    void addXP(int amount);
    void incrementAge();
};