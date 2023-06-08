package com.example.mipmip.repositories

interface IListenerAction<T> {
    fun onDataChange(data: T)
}