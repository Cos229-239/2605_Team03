#include "../../../../.github/workflows/UI and stats/header/NudgiePet.h"
#include <algorithm>

void NudgiePet::notifyUI() {
    if (onStateChanged) {
        onStateChanged(state);
    }
}

void NudgiePet::setOnStateChangedCallback(std::function<void(const NudgieState&)> callback) {
    onStateChanged = callback;
}

NudgieState NudgiePet::getState() const {
    return state;
}

void NudgiePet::drainEnergy(int amount) {
    state.energy = std::max(0, state.energy - amount);
    notifyUI();
}

void NudgiePet::missedHabit() {
    int penalty = 15;
    int nonPunishmentFloor = 30;
    int newHappiness = state.happiness - penalty;
    state.happiness = (newHappiness < nonPunishmentFloor) ? nonPunishmentFloor : newHappiness;
    notifyUI();
}

void NudgiePet::completeHabit() {
    state.happiness = std::min(100, state.happiness + 20);
    notifyUI();
}