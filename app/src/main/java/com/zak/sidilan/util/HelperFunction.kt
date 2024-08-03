package com.zak.sidilan.util

object HelperFunction {
    fun parseUserRole(role: String?): UserRole? {
        return when (role) {
            "Admin" -> UserRole.ADMIN
            "Direktur" -> UserRole.DIRECTOR
            "Manajer" -> UserRole.MANAGER
            "Staf" -> UserRole.STAFF
            else -> UserRole.STAFF
        }
    }
}