package com.example.mipmip.ui




sealed class Screen(val route:String){
    object MainScreen : Screen("main_screen")
    object LoginScreen : Screen("login_screen")
    object NewMessageScreen : Screen("new_message_screen")
    object ChatScreen : Screen("chat_screen")

    fun withArgs(vararg  args: String): String{
        return buildString {
            append(route)
            args.forEach {arg ->
                append("/$arg")
            }
        }
    }
}


