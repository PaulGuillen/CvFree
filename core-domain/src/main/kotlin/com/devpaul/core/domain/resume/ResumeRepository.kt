package com.devpaul.core.domain.resume

import kotlinx.coroutines.flow.Flow

interface ResumeRepository {
    val resumes: Flow<List<ResumeDocument>>

    suspend fun save(resume: ResumeDocument)
}
