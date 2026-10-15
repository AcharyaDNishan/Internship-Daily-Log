package com.example.payment

import io.github.jan_tennert.supabase.createSupabaseClient
import io.github.jan_tennert.supabase.postgrest.Postgrest

object SupabaseClient {
    val client = createSupabaseClient(
        supabaseUrl = "https://lotfmshdfisqmrkjzilq.supabase.co",
        supabaseKey = "sb_publishable_9Ysjf2MrPQWORHKrcCalww_yVDY4_mZ"
    ) {
        install(Postgrest)
    }
}
