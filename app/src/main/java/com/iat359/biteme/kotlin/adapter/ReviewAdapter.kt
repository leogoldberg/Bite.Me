package com.iat359.biteme.kotlin.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.toObject

import com.iat359.biteme.databinding.ItemReviewBinding
import com.iat359.biteme.kotlin.model.Review
import java.text.SimpleDateFormat
import java.util.*


/**
 * RecyclerView adapter for a list of [Review].
 */
open class ReviewAdapter(query: Query) : FirestoreAdapter<ReviewAdapter.ViewHolder>(query) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemReviewBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getSnapshot(position).toObject<Review>())
    }

    class ViewHolder(val binding: ItemReviewBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(review: Review?) {
            if (review == null) {
                return
            }

            binding.ratingItemName.text = review.reviewer_name
            binding.ratingItemRating.rating = review.rating.toFloat()
            binding.ratingItemText.text = review.review_body

            if (review.timestamp != null) {
                binding.ratingItemDate.text = FORMAT.format(review.timestamp)
            }
        }

        companion object {

            private val FORMAT = SimpleDateFormat(
                    "MM/dd/yyyy", Locale.US)
        }
    }
}
