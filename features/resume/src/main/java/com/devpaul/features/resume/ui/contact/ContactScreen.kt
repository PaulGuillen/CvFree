package com.devpaul.features.resume.ui.contact

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.devpaul.core.designsystem.theme.CvFreeTheme
import com.devpaul.core.domain.resume.ResumeContact
import com.devpaul.features.resume.ui.components.BuilderFormCard
import com.devpaul.features.resume.ui.components.BuilderPage
import com.devpaul.features.resume.ui.components.BuilderTextField
import com.devpaul.features.resume.ui.components.isValidEmailOrBlank

@Composable
fun ContactScreen(
    onBack: () -> Unit,
    onContinue: (ResumeContact) -> Unit,
    modifier: Modifier = Modifier,
    totalSteps: Int = 5,
    initialData: ResumeContact = ResumeContact(
        name = "Paul",
        lastName = "Guillen Acuña",
        professionalTitle = "Senior Mobile Developer",
        email = "paul@ejemplo.com",
        phone = "+51 987 654 321",
        city = "Lima",
        address = "Lima, Perú",
    ),
) {
    var name by rememberSaveable(initialData.name) { mutableStateOf(initialData.name) }
    var lastName by rememberSaveable(initialData.lastName) { mutableStateOf(initialData.lastName) }
    var professionalTitle by rememberSaveable(initialData.professionalTitle) { mutableStateOf(initialData.professionalTitle) }
    var email by rememberSaveable(initialData.email) { mutableStateOf(initialData.email) }
    var phone by rememberSaveable(initialData.phone) { mutableStateOf(initialData.phone) }
    var city by rememberSaveable(initialData.city) { mutableStateOf(initialData.city) }
    var address by rememberSaveable(initialData.address) { mutableStateOf(initialData.address) }
    var showErrors by rememberSaveable { mutableStateOf(false) }

    fun submit() {
        showErrors = true
        val valid = name.isNotBlank() && professionalTitle.isNotBlank() &&
            (email.isNotBlank() || phone.isNotBlank()) && email.isValidEmailOrBlank()
        if (valid) {
            onContinue(
                ResumeContact(
                    name = name.trim(),
                    lastName = lastName.trim(),
                    professionalTitle = professionalTitle.trim(),
                    email = email.trim(),
                    phone = phone.trim(),
                    city = city.trim(),
                    address = address.trim(),
                ),
            )
        }
    }

    BuilderPage(
        onBack = onBack,
        step = 1,
        totalSteps = totalSteps,
        stepName = "Contacto",
        title = "Información de contacto",
        description = "Introduce tus datos básicos para que los reclutadores puedan contactarte.",
        actionText = "Continuar a Experiencia laboral →",
        onContinue = ::submit,
        modifier = modifier,
    ) {
        BuilderFormCard {
            BuilderTextField(
                value = name,
                onValueChange = { name = it },
                label = "Nombre",
                placeholder = "Tu nombre",
                isError = showErrors && name.isBlank(),
                supportingText = if (showErrors && name.isBlank()) "El nombre es obligatorio" else null,
            )
            Spacer(modifier = Modifier.height(16.dp))
            BuilderTextField(lastName, { lastName = it }, "Apellido", "Tus apellidos")
            Spacer(modifier = Modifier.height(16.dp))
            BuilderTextField(
                value = professionalTitle,
                onValueChange = { professionalTitle = it },
                label = "Título profesional",
                placeholder = "Ej. Android Developer",
                isError = showErrors && professionalTitle.isBlank(),
                supportingText = if (showErrors && professionalTitle.isBlank()) "El título es obligatorio" else null,
            )
            Spacer(modifier = Modifier.height(16.dp))
            BuilderTextField(
                email,
                { email = it },
                "Email",
                "correo@ejemplo.com",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                isError = showErrors && (email.isBlank() && phone.isBlank() || !email.isValidEmailOrBlank()),
                supportingText = when {
                    showErrors && !email.isValidEmailOrBlank() -> "Ingresa un correo válido"
                    showErrors && email.isBlank() && phone.isBlank() -> "Agrega correo o teléfono"
                    else -> null
                },
            )
            Spacer(modifier = Modifier.height(16.dp))
            BuilderTextField(
                phone,
                { phone = it },
                "Teléfono",
                "+51 999 999 999",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                isError = showErrors && email.isBlank() && phone.isBlank(),
                supportingText = if (showErrors && email.isBlank() && phone.isBlank()) {
                    "Agrega correo o teléfono"
                } else null,
            )
            Spacer(modifier = Modifier.height(16.dp))
            BuilderTextField(city, { city = it }, "Ciudad", "Ciudad")
            Spacer(modifier = Modifier.height(16.dp))
            BuilderTextField(address, { address = it }, "Dirección", "Dirección")
        }
    }
}

@Preview(name = "Contact Light", showBackground = true, heightDp = 900)
@Composable
private fun ContactLightPreview() {
    CvFreeTheme(darkTheme = false) { ContactScreen({}, {}) }
}

@Preview(name = "Contact Dark", showBackground = true, heightDp = 900)
@Composable
private fun ContactDarkPreview() {
    CvFreeTheme(darkTheme = true) { ContactScreen({}, {}) }
}
