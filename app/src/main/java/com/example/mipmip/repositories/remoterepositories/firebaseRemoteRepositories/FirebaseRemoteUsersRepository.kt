package com.example.mipmip.repositories.remoterepositories.firebaseRemoteRepositories

import android.app.Activity
import android.content.Context
import android.util.Log
import com.example.mipmip.models.ContactDetails
import com.example.mipmip.models.User
import com.example.mipmip.repositories.IListenerAction
import com.example.mipmip.repositories.IRepositoryResult
import com.example.mipmip.repositories.remoterepositories.remoteInterfaces.IRemoteUsersRepository
import com.example.mipmip.utils.Constants
import com.example.mipmip.utils.Constants.DATE_FORMAT
import com.example.mipmip.utils.Constants.STATUS
import com.example.mipmip.utils.Constants.USER_ACTIVE_STATUS
import com.example.mipmip.utils.Constants.USER_NOT_ACTIVE_STATUS
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import java.lang.ref.WeakReference
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class FirebaseRemoteUsersRepository(context: Context) : IRemoteUsersRepository {

    private var mFirebaseAuth: FirebaseAuth
    private var mDatabaseRef: FirebaseDatabase
    private var mUsersDatabaseRef: DatabaseReference
    private val mAuth = FirebaseAuth.getInstance()
    private var auth: FirebaseAuth
    private lateinit var mStoredVerificationId: String
    private lateinit var mResendToken: PhoneAuthProvider.ForceResendingToken
    private lateinit var mVerifyPhoneNumResultListener: IRepositoryResult

    companion object {
        const val TAG = "FirebaseUsersRepository"
        const val LANGUAGE_CODE = "il"
        const val UN_KNOW_ERROR_MESSAGE = "Please try again later"
        const val AUTH_ERROR = "Unable to determine user login status"
        const val WRONG_OTP = "Wrong OTP, Please try again"
    }

    init {
        FirebaseApp.initializeApp(context)
        auth = Firebase.auth
        mFirebaseAuth = FirebaseAuth.getInstance()
        mDatabaseRef = FirebaseDatabase.getInstance()
        mUsersDatabaseRef = mDatabaseRef.getReference(Constants.USER_TABLE_NAME)
        mAuth.setLanguageCode(LANGUAGE_CODE)
    }

    override suspend fun createUser(
        phoneNum: String,
        loginActivityWR: WeakReference<Activity>,
        verifyPhoneNumResultListener: IRepositoryResult
    ) {
        val createdAt = SimpleDateFormat(DATE_FORMAT, Locale.getDefault()).format(Date())
        mVerifyPhoneNumResultListener = verifyPhoneNumResultListener
        FirebaseMessaging.getInstance().token.addOnSuccessListener {
            val user = User(
                phoneNum = phoneNum,
                status = USER_NOT_ACTIVE_STATUS,
                created_at = createdAt,
                fcm = it
            )
            mUsersDatabaseRef.child(phoneNum).setValue(user).addOnSuccessListener {
                sendOtp(phoneNum, loginActivityWR)
            }.addOnFailureListener { error ->
                Log.e(TAG, "Create user is failed: " + error.message)
                mVerifyPhoneNumResultListener.onFailed(UN_KNOW_ERROR_MESSAGE)
            }
        }.addOnFailureListener {
            Log.e(TAG, "Fetching FCM registration token failed" + it.message)
            mVerifyPhoneNumResultListener.onFailed(UN_KNOW_ERROR_MESSAGE)
        }
    }

    override suspend fun verifyOtp(otp: String, verifyOTPResultListener: IRepositoryResult) {
        val credential = PhoneAuthProvider.getCredential(mStoredVerificationId, otp)
        signInWithPhoneAuthCredential(credential, verifyOTPResultListener)
    }

    override fun sendOtp(phoneNum: String, loginActivityWR: WeakReference<Activity>) {
        val options = loginActivityWR.get()?.let {
            PhoneAuthOptions.newBuilder(mAuth)
                .setPhoneNumber(phoneNum)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setCallbacks(otpCallbacks)
                .setActivity(it)
                .build()
        }
        options?.let { PhoneAuthProvider.verifyPhoneNumber(it) }
    }

    private fun signInWithPhoneAuthCredential(
        credential: PhoneAuthCredential,
        verifyOTPResultListener: IRepositoryResult
    ) {
        mAuth.signInWithCredential(credential).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val user = task.result?.user
                mUsersDatabaseRef.child(user?.phoneNumber.toString()).child(STATUS)
                    .setValue(USER_ACTIVE_STATUS).addOnSuccessListener {
                        verifyOTPResultListener.onSuccesses(user?.phoneNumber)
                    }.addOnFailureListener { error ->
                        Log.w(TAG, "Failed update user status", error)
                        verifyOTPResultListener.onFailed(UN_KNOW_ERROR_MESSAGE)
                    }
            } else {
                Log.w(TAG, "signInWithCredential: ", task.exception)
                if (task.exception is FirebaseAuthInvalidCredentialsException) {
                    verifyOTPResultListener.onFailed(WRONG_OTP)
                } else {
                    verifyOTPResultListener.onFailed(UN_KNOW_ERROR_MESSAGE)
                }
            }
        }
    }


    override suspend fun setStatusChangedAction(
        phoneNum: String,
        isActive: IListenerAction<Boolean>
    ) {
        mUsersDatabaseRef.child(phoneNum).child(STATUS)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.value != null) {
                        isActive.onDataChange((snapshot.value as Long).toInt() == USER_ACTIVE_STATUS)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.w(TAG, "Failed to read value.", error.toException())
                }
            })
    }

    override suspend fun getActiveContactsList(verifyPhoneNumContactsList: List<ContactDetails>): List<ContactDetails> {
        TODO("Not yet implemented")
    }

    override suspend fun isUserLoggedIn(isUserLoggedInListener: IRepositoryResult) {
        try {
            val currentUser = auth.currentUser?.phoneNumber
            isUserLoggedInListener.onSuccesses(currentUser)
        } catch (e: Exception) {
//           isUserLoggedInListener.onFailed(AUTH_ERROR)
        }
    }

    override fun getMyPhoneNum(): String? {
        return auth.currentUser?.phoneNumber
    }

    private val otpCallbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
        }

        override fun onVerificationFailed(e: FirebaseException) {
            Log.w(TAG, "onVerificationFailed", e)
            mVerifyPhoneNumResultListener.onFailed(UN_KNOW_ERROR_MESSAGE)
        }

        override fun onCodeSent(
            verificationId: String,
            token: PhoneAuthProvider.ForceResendingToken
        ) {
            Log.d(TAG, "onCodeSent:$verificationId")
            mStoredVerificationId = verificationId
            mResendToken = token
            mVerifyPhoneNumResultListener.onSuccesses<Nothing>()
        }
    }

}

