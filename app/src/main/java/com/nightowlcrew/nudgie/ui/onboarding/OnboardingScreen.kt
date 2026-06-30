package com.nightowlcrew.nudgie.ui.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.nightowlcrew.nudgie.R
import com.nightowlcrew.nudgie.ui.dashboard.NudgieViewModel
import com.nightowlcrew.nudgie.utils.PetType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingScreen(
    viewModel: NudgieViewModel,
    onComplete: () -> Unit
) {
    var currentStep by remember { mutableIntStateOf(1) }
    val totalSteps = 7

    // State for the questions
    var userName by remember { mutableStateOf("") }
    var selectedPetType by remember { mutableStateOf<PetType?>(null) }
    var mainReason by remember { mutableStateOf("") }
    var improvementArea by remember { mutableStateOf("") }
    var habitCount by remember { mutableStateOf("") }
    var firstHabitGoal by remember { mutableStateOf("") }
    var petName by remember { mutableStateOf("") }

    val progress = currentStep.toFloat() / totalSteps.toFloat()

    Scaffold(
        topBar = {
            Column {
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                )
                if (currentStep > 1) {
                    IconButton(onClick = { currentStep-- }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Nudgie Character
            Image(
                painter = painterResource(id = R.drawable.nudgie),
                contentDescription = "Nudgie",
                modifier = Modifier.size(150.dp),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Conversational UI based on step
            Box(modifier = Modifier.weight(1f)) {
                when (currentStep) {
                    1 -> QuestionName(userName) { userName = it }
                    2 -> QuestionPet(userName, selectedPetType) { selectedPetType = it }
                    3 -> QuestionReason(mainReason) { mainReason = it }
                    4 -> QuestionArea(improvementArea) { improvementArea = it }
                    5 -> QuestionHabitCount(habitCount) { habitCount = it }
                    6 -> QuestionFirstHabit(firstHabitGoal) { firstHabitGoal = it }
                    7 -> QuestionPetName(petName) { petName = it }
                }
            }

            Button(
                onClick = {
                    if (currentStep < totalSteps) {
                        currentStep++
                    } else {
                        // Onboarding Finalization
                        val finalPetType = selectedPetType ?: PetType.BLUE
                        viewModel.completeOnboarding(
                            userName = userName,
                            petType = finalPetType,
                            petName = petName,
                            firstHabit = firstHabitGoal
                        )
                        onComplete()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = isNextEnabled(currentStep, userName, selectedPetType, mainReason, improvementArea, habitCount, firstHabitGoal, petName)
            ) {
                Text(if (currentStep == totalSteps) "Let's Go!" else "Next")
            }
        }
    }
}

private fun isNextEnabled(
    step: Int,
    userName: String,
    selectedPet: PetType?,
    reason: String,
    area: String,
    count: String,
    goal: String,
    petName: String
): Boolean {
    return when (step) {
        1 -> userName.isNotBlank()
        2 -> selectedPet != null
        3 -> reason.isNotBlank()
        4 -> area.isNotBlank()
        5 -> count.isNotBlank()
        6 -> goal.isNotBlank()
        7 -> petName.isNotBlank()
        else -> false
    }
}

@Composable
fun QuestionName(name: String, onNameChange: (String) -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "Hi there! What is your name?",
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = name,
            onValueChange = onNameChange,
            label = { Text("Your Name") },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun QuestionPet(userName: String, selectedPet: PetType?, onPetSelect: (PetType) -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "Oh, Hi $userName, I remember. You are here to pick up your nudgie. What type would you like?",
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        
        val standardPets = listOf(PetType.BLUE, PetType.FOX, PetType.AXOLOTL, PetType.DRAGON)
        
        Column(Modifier.selectableGroup()) {
            standardPets.forEach { pet ->
                Row(
                    Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .selectable(
                            selected = (selectedPet == pet),
                            onClick = { onPetSelect(pet) },
                            role = Role.RadioButton
                        )
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(selected = (selectedPet == pet), onClick = null)
                    Text(text = pet.displayName, modifier = Modifier.padding(start = 16.dp))
                }
            }
            // Surprise Me option
            Row(
                Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .selectable(
                        selected = (selectedPet != null && selectedPet !in standardPets),
                        onClick = { 
                            val allPets = PetType.entries
                            onPetSelect(allPets.random())
                        },
                        role = Role.RadioButton
                    )
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(selected = (selectedPet != null && selectedPet !in standardPets), onClick = null)
                Text(text = "🎁 Surprise Me", modifier = Modifier.padding(start = 16.dp))
            }
        }
    }
}

@Composable
fun QuestionReason(selectedReason: String, onSelect: (String) -> Unit) {
    val options = listOf("Build better routines", "Stay focused", "Improve school/work productivity", "Improve health", "Reduce bad habits", "Stay motivated")
    SelectableList(
        question = "What is your main reason for using this habit tracker?",
        options = options,
        selectedOption = selectedReason,
        onOptionSelected = onSelect
    )
}

@Composable
fun QuestionArea(selectedArea: String, onSelect: (String) -> Unit) {
    val options = listOf("School", "Work", "Fitness", "Mental health", "Sleep", "Screen time", "Personal goals")
    SelectableList(
        question = "What area of your life do you want to improve the most?",
        options = options,
        selectedOption = selectedArea,
        onOptionSelected = onSelect
    )
}

@Composable
fun QuestionHabitCount(selectedCount: String, onSelect: (String) -> Unit) {
    val options = listOf("1 habit", "2–3 habits", "4–5 habits", "More than 5 habits")
    SelectableList(
        question = "How many habits do you want to start with?",
        options = options,
        selectedOption = selectedCount,
        onOptionSelected = onSelect
    )
}

@Composable
fun QuestionFirstHabit(goal: String, onGoalChange: (String) -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "What is your first habit goal? (e.g., study for 30 minutes, drink water, wake up earlier, go to the gym)",
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = goal,
            onValueChange = onGoalChange,
            label = { Text("First Habit") },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun QuestionPetName(name: String, onNameChange: (String) -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "Awesome! Lastly, what would you like to name your nudgie?",
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = name,
            onValueChange = onNameChange,
            label = { Text("Nudgie Name") },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun SelectableList(
    question: String,
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = question,
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        Column(Modifier.selectableGroup()) {
            options.forEach { option ->
                Row(
                    Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .selectable(
                            selected = (selectedOption == option),
                            onClick = { onOptionSelected(option) },
                            role = Role.RadioButton
                        )
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(selected = (selectedOption == option), onClick = null)
                    Text(text = option, modifier = Modifier.padding(start = 16.dp))
                }
            }
        }
    }
}
