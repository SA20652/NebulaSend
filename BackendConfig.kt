package com.nebula.messenger.data

/**
 * Backend setup placeholder.
 * Create a Supabase project and add its public Project URL + anon key here
 * only after configuring Row Level Security (RLS).
 * NEVER put a service_role key in the Android app.
 */
object BackendConfig {
    const val SUPABASE_URL = ""
    const val SUPABASE_ANON_KEY = ""
    val isConfigured: Boolean
        get() = SUPABASE_URL.startsWith("https://") && SUPABASE_ANON_KEY.isNotBlank()
}
