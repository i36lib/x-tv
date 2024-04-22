package com.github.i36lib.xtv.data.repositories

import com.github.i36lib.xtv.data.entities.GithubRelease

interface GithubRepository {
    suspend fun latestRelease(): GithubRelease
}