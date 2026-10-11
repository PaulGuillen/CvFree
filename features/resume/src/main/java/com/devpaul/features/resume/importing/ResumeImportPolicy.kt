package com.devpaul.features.resume.importing

data class ResumeImportPolicy(
    val aiEnhancementEnabled: Boolean = true,
    val aiModelName: String = DEFAULT_AI_MODEL,
)

private const val DEFAULT_AI_MODEL = "gemini-3.8-flash"
