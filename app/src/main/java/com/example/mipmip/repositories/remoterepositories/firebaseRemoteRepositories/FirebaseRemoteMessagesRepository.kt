package com.example.mipmip.repositories.remoterepositories.firebaseRemoteRepositories


import com.example.mipmip.models.Message
import com.example.mipmip.repositories.IListenerAction
import com.example.mipmip.repositories.IRepositoryResult
import com.example.mipmip.repositories.remoterepositories.remoteInterfaces.IRemoteMessagesRepository
import com.example.mipmip.utils.Constants.CONVERSATIONS
import com.example.mipmip.utils.Constants.LAST_MESSAGES
import com.example.mipmip.utils.Constants.MESSAGES_TABLE_NAME
import com.example.mipmip.utils.Constants.MESSAGE_CONTACT_PHONE_NUM
import com.example.mipmip.utils.Constants.MESSAGE_ID
import com.example.mipmip.utils.Constants.MESSAGE_READ
import com.example.mipmip.utils.Constants.MESSAGE_RECIPIENT
import com.example.mipmip.utils.Constants.MESSAGE_SENDER
import com.example.mipmip.utils.Constants.MESSAGE_TEXT
import com.example.mipmip.utils.Constants.MESSAGE_TIME
import com.google.firebase.database.*
import kotlinx.coroutines.*
import java.util.*
import javax.inject.Inject

class FirebaseRemoteMessagesRepository @Inject constructor() : IRemoteMessagesRepository {

    private var mDatabaseRef: FirebaseDatabase = FirebaseDatabase.getInstance()
    private var mMessagesDatabaseRef: DatabaseReference =
        mDatabaseRef.getReference(MESSAGES_TABLE_NAME)
    private var updateMessagesJob: Job? = null
    private val listenersHashMap = HashMap<String, ChildEventListener>()

    companion object {
        const val TAG = "FirebaseRemoteMessagesRepository"
    }

    override suspend fun fetchMessagesRemote(
        myPhoneNum: String,
        contactPhoneNum: String,
        lastLocalMessage: Message?,
        fetchMessagesIRepositoryResult: IRepositoryResult
    ) {
        val uniqueHash = generateUniqueHash(myPhoneNum, contactPhoneNum)
        mMessagesDatabaseRef.child(CONVERSATIONS).child(uniqueHash)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {

                    val messagesList: MutableList<Message> = mutableListOf()
                    val lastLocalMessageId = lastLocalMessage?.id

                    for (ds in dataSnapshot.children.reversed()) {
                        val messageHS = ds.value as HashMap<*, *>
                        val messageId = messageHS[MESSAGE_ID] as String

                        if (lastLocalMessageId == null || messageId != lastLocalMessageId) {
                            val messageDate = messageHS[MESSAGE_TIME] as Long
                            val message = Message(
                                messageId,
                                messageHS[MESSAGE_TEXT] as String,
                                messageDate,
                                messageHS[MESSAGE_SENDER] as String,
                                messageHS[MESSAGE_RECIPIENT] as String,
                                messageHS[MESSAGE_CONTACT_PHONE_NUM] as String,
                                messageHS[MESSAGE_READ] as Boolean
                            )
                            messagesList.add(message)
                        } else {
                            break
                        }
                    }
                    fetchMessagesIRepositoryResult.onSuccesses(messagesList.reversed())
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    fetchMessagesIRepositoryResult.onFailed(databaseError.message)
                }
            })
    }

    override suspend fun sendMessage(
        newMessage: Message,
        myPhoneNum: String,
        contactPhoneNum: String,
        sendMessageIRepositoryResult: IRepositoryResult
    ) {
        generateUniqueHash(myPhoneNum, contactPhoneNum).let { hash ->
            val newMessageRef = mMessagesDatabaseRef.child(
                CONVERSATIONS
            ).child(hash).push()
            if (newMessageRef.key != null) {
                newMessage.id = newMessageRef.key!!
                newMessage.isRead = true
                mMessagesDatabaseRef.child(LAST_MESSAGES).child(myPhoneNum)
                    .child(contactPhoneNum).setValue(newMessage).addOnSuccessListener {
                        newMessage.isRead = false
                        newMessage.messageContactPhoneNum = myPhoneNum
                        mMessagesDatabaseRef.child(LAST_MESSAGES).child(contactPhoneNum)
                            .child(myPhoneNum).setValue(newMessage).addOnSuccessListener {
                                newMessageRef.setValue(newMessage).addOnSuccessListener {
                                }.addOnFailureListener { error ->
                                    error.message?.let { sendMessageIRepositoryResult.onFailed(it) }
                                }
                            }.addOnFailureListener { error ->
                                error.message?.let { sendMessageIRepositoryResult.onFailed(it) }
                            }
                    }.addOnFailureListener { error ->
                        error.message?.let { sendMessageIRepositoryResult.onFailed(it) }
                    }
            }
        }
    }

    override suspend fun fetchLastMessagesRemote(
        myPhoneNum: String,
        fetchLastMessagesIRepositoryResult: IRepositoryResult
    ) {
        mMessagesDatabaseRef.child(LAST_MESSAGES).child(myPhoneNum).orderByChild(MESSAGE_TIME)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val messagesList: MutableList<Message> = mutableListOf()
                    for (ds in dataSnapshot.children) {
                        messagesList.add(dataSnapshotToMessage(ds))
                    }
                    fetchLastMessagesIRepositoryResult.onSuccesses(messagesList)
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    fetchLastMessagesIRepositoryResult.onFailed(databaseError.message)
                }
            })
    }

    override suspend fun setListenerToNewMessage(
        myPhoneNum: String,
        onNewMessageAction: IListenerAction<Message>
    ) {
        mMessagesDatabaseRef.child(LAST_MESSAGES).child(myPhoneNum).orderByChild(MESSAGE_TIME)
            .startAt(System.currentTimeMillis().toDouble())
            .addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    onNewMessageAction.onDataChange(dataSnapshotToMessage(snapshot))
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                    onNewMessageAction.onDataChange(dataSnapshotToMessage(snapshot))
                }

                override fun onChildRemoved(snapshot: DataSnapshot) {
                }

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                }

                override fun onCancelled(error: DatabaseError) {
                }

            })
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun updateMessageToRead(
        myPhoneNum: String,
        contactPhoneNum: String,
        unReadMessagesList: MutableList<Message>
    ) {
        updateMessagesJob?.cancel()
        updateMessagesJob = GlobalScope.launch(Dispatchers.IO) {
            val conversationsHash = generateUniqueHash(myPhoneNum, contactPhoneNum)
            for (message in unReadMessagesList) {
                mMessagesDatabaseRef.child(CONVERSATIONS).child(conversationsHash).child(message.id)
                    .child(
                        MESSAGE_READ
                    ).setValue(true)
            }
        }
    }

    override suspend fun setNewMessageAddedToConversationsListener(
        myPhoneNum: String,
        contactPhoneNum: String,
        onNewMessageAction: IListenerAction<Message>
    ) {
        val conversationsHash = generateUniqueHash(myPhoneNum, contactPhoneNum)
        val newMessageAddedToConversationsListener =
            setNewMessageAddedListenerGetter(myPhoneNum,contactPhoneNum,onNewMessageAction)

        listenersHashMap[contactPhoneNum]?.let {
            removeNewMessageAddedToConversationsListener(myPhoneNum, contactPhoneNum)
        }

        listenersHashMap[contactPhoneNum] = newMessageAddedToConversationsListener
        mMessagesDatabaseRef.child(CONVERSATIONS).child(conversationsHash)
            .orderByChild(MESSAGE_TIME).startAt(System.currentTimeMillis().toDouble())
            .addChildEventListener(newMessageAddedToConversationsListener)
    }

    @OptIn(DelicateCoroutinesApi::class)
    override suspend fun updateLastMessageToRead(myPhoneNum: String, contactPhoneNum: String) {
        GlobalScope.launch(Dispatchers.IO) {
            mMessagesDatabaseRef.child(LAST_MESSAGES).child(myPhoneNum).child(contactPhoneNum)
                .child(MESSAGE_READ).setValue(true)
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun removeNewMessageAddedToConversationsListener(
        myPhoneNum: String,
        contactPhoneNum: String
    ) {
        val conversationsHash = generateUniqueHash(myPhoneNum, contactPhoneNum)
        val newMessageAddedToConversationsListener = listenersHashMap[contactPhoneNum]!!

        GlobalScope.launch(Dispatchers.IO) {
            mMessagesDatabaseRef.child(CONVERSATIONS).child(conversationsHash)
                .removeEventListener(newMessageAddedToConversationsListener)
        }
    }

    private fun setNewMessageAddedListenerGetter(myPhoneNum:String,contactPhoneNum:String,onNewMessageAction: IListenerAction<Message>) =
        object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val newMessage = dataSnapshotToMessage(snapshot)
                mMessagesDatabaseRef.child(LAST_MESSAGES).child(myPhoneNum)
                    .child(contactPhoneNum).child(
                    MESSAGE_READ
                ).setValue(true).addOnSuccessListener {
                    newMessage.isRead = true
                    onNewMessageAction.onDataChange(newMessage)
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onCancelled(error: DatabaseError) {
            }

        }

    private fun dataSnapshotToMessage(snapShot: DataSnapshot): Message {
        val messageHS = snapShot.value as HashMap<*, *>
        val messageId = messageHS[MESSAGE_ID] as String
        val messageDate = messageHS[MESSAGE_TIME] as Long
        return Message(
            messageId,
            messageHS[MESSAGE_TEXT] as String,
            messageDate,
            messageHS[MESSAGE_SENDER] as String,
            messageHS[MESSAGE_RECIPIENT] as String,
            messageHS[MESSAGE_CONTACT_PHONE_NUM] as String,
            messageHS[MESSAGE_READ] as Boolean
        )
    }

    private fun generateUniqueHash(phone1: String, phone2: String): String {
        val phones = listOf(phone1, phone2).sorted()
        return phones[0].hashCode().toString() + phones[1].hashCode().toString()
    }
}