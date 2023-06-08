package com.example.mipmip.utils

object Constants {
        //Remote Users Database
        const val USER_TABLE_NAME = "Users"
        const val FCM = "fcm"
        const val PHONE_NUM = "phoneNum"
        const val STATUS = "status"
        const val USER_NOT_ACTIVE_STATUS = 0
        const val USER_ACTIVE_STATUS = 1


        //Phones Prefix
        const val ISRAEL_PREFIX = "+972"
        val ISRAEL_PHONE_NUM_PATTERN = """^\+9725\d{8}$""".toRegex()
        val REGULAR_PHONE_NUM_PATTERN = """^05\d{8}$""".toRegex()
        const val PATTERN_REMOVE_SPACES_AND_DASHES = "[\\s-]+"

        //Date
        const val DATE_FORMAT = "dd/MM/yyyy HH:mm:ss"

        const val MIP_MIP_DB_NAME = "MipMipDb"


        //Local Contacts Database
        const val CONTACT_TABLE_NAME = "Contacts"
        const val CONTACT_PHONE_NUM = "phoneNum"
        const val CONTACTS_DETAILS_TABLE_NAME = "contacts_details"
        const val CONTACT_NAME = "contact_name"
        const val CONTACT_IMAGE_URL = "contact_image_url"

        //Messages Database
        const val MESSAGES_TABLE_NAME = "Messages"
        const val CONVERSATIONS = "Conversations"
        const val LAST_MESSAGES = "Last_Messages"
        const val MESSAGE_ID = "messages_id"
        const val MESSAGE_TEXT = "messages_text"
        const val MESSAGE_SENDER ="message_sender"
        const val MESSAGE_TIME ="message_time"
        const val MESSAGE_READ = "message_read"
        const val MESSAGE_RECIPIENT = "message_recipient"
        const val MESSAGE_CONTACT_PHONE_NUM = "message_contact_phone_num"
        const val EMPTY_STRING = ""

}
