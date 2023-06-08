package com.example.mipmip.repositories

interface IRepositoryResult {
    fun <T> onSuccesses(data: T? = null)
    fun onFailed(message: String)
}