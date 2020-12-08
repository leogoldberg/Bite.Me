package com.iat359.biteme.kotlin.model

import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp
import java.util.*

@IgnoreExtraProperties
data class Review (
        var reviewer_name: String = "",
        var recipe_name: String = "",
        var review_body: String = "",
        var rating: Double = 0.0,
        var photoUrl: String? = null,
        @ServerTimestamp var timestamp: Date? = null
) {
}