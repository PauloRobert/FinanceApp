package com.example.financeapp.data.remote.dto

import com.google.gson.annotations.SerializedName

// ── Profile ─────────────────────────────────────────────

data class ProfileResponse(
    val username: String = "",
    @SerializedName("display_name")
    val displayName: String? = null,
    val email: String? = null,
    val phone: String? = null
)

data class ProfileUpdateRequest(
    @SerializedName("display_name")
    val displayName: String? = null,
    val email: String? = null,
    val phone: String? = null
)

// ── Security ────────────────────────────────────────────

data class SecurityResponse(
    @SerializedName("two_factor_enabled")
    val twoFactorEnabled: Boolean = false,
    @SerializedName("biometric_enabled")
    val biometricEnabled: Boolean = false,
    @SerializedName("login_notification_enabled")
    val loginNotificationEnabled: Boolean = true,
    @SerializedName("last_password_change")
    val lastPasswordChange: String? = null
)

data class SecurityUpdateRequest(
    @SerializedName("two_factor_enabled")
    val twoFactorEnabled: Boolean? = null,
    @SerializedName("biometric_enabled")
    val biometricEnabled: Boolean? = null,
    @SerializedName("login_notification_enabled")
    val loginNotificationEnabled: Boolean? = null
)

data class PasswordChangeRequest(
    @SerializedName("current_password")
    val currentPassword: String,
    @SerializedName("new_password")
    val newPassword: String
)

data class MessageResponse(
    val message: String = ""
)

// ── Privacy ─────────────────────────────────────────────

data class PrivacyResponse(
    @SerializedName("share_usage_data")
    val shareUsageData: Boolean = false,
    @SerializedName("show_balance_on_home")
    val showBalanceOnHome: Boolean = true,
    @SerializedName("transaction_notifications")
    val transactionNotifications: Boolean = true,
    @SerializedName("marketing_emails")
    val marketingEmails: Boolean = false
)

data class PrivacyUpdateRequest(
    @SerializedName("share_usage_data")
    val shareUsageData: Boolean? = null,
    @SerializedName("show_balance_on_home")
    val showBalanceOnHome: Boolean? = null,
    @SerializedName("transaction_notifications")
    val transactionNotifications: Boolean? = null,
    @SerializedName("marketing_emails")
    val marketingEmails: Boolean? = null
)
