package com.jose.pantrypal.navigation

object Routes {
    const val LOGIN = "login"
    const val SIGN_UP = "sign_up"
    const val DASHBOARD = "dashboard"
    const val INVENTORY = "inventory"
    const val STORAGE = "storage"
    const val PROFILE = "profile"

    const val ITEM_DETAIL = "item_detail/{itemId}"
    fun itemDetail(itemId: String) = "item_detail/$itemId"
}