#pragma once
#include <functional>

struct NudgieState {
    int happiness = 85;
    int energy = 62;
    int level = 6;
    int ageDays = 5;
};

class NudgiePet {
private:
    NudgieState state;
    std::function<void(const NudgieState&)> onStateChanged;
    void notifyUI();

public:
    void setOnStateChangedCallback(std::function<void(const NudgieState&)> callback);
    NudgieState getState() const;

    void drainEnergy(int amount);
    void rechargeEnergy();
    void missedHabit();
    void completeHabit();
};