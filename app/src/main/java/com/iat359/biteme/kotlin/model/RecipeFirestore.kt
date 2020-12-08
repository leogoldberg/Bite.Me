package com.iat359.biteme.kotlin.model

import com.google.firebase.firestore.IgnoreExtraProperties

/**
 * Recipe POJO.
 */
@IgnoreExtraProperties
data class RecipeFirestore(
        var numRatings: Int = 0,
        var avgRating: Double = 0.toDouble()
) {

}
