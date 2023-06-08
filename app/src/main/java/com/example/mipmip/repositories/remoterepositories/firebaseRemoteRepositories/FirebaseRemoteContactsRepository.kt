package com.example.mipmip.repositories.remoterepositories.firebaseRemoteRepositories

import android.content.Context
import com.example.mipmip.models.ContactDetails
import com.example.mipmip.repositories.remoterepositories.remoteInterfaces.IRemoteContactsRepository
import com.example.mipmip.utils.Constants
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase

class FirebaseRemoteContactsRepository(context: Context) : IRemoteContactsRepository {

    private var mFirebaseAuth: FirebaseAuth
    private var mDatabaseRef: FirebaseDatabase
    private var mContactsDatabaseRef: DatabaseReference
    private var auth: FirebaseAuth

    init {
        FirebaseApp.initializeApp(context)
        auth = Firebase.auth
        mFirebaseAuth = FirebaseAuth.getInstance()
        mDatabaseRef = FirebaseDatabase.getInstance()
        mContactsDatabaseRef = mDatabaseRef.getReference(Constants.CONTACT_TABLE_NAME)
    }
}