package com.iat359.biteme.kotlin.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.toObject

import com.iat359.biteme.databinding.ItemReviewBinding
import com.iat359.biteme.kotlin.model.Review
import java.text.SimpleDateFormat
import java.util.*


/**
 * RecyclerView adapter for a list of [Review].
 *
 * Adapted from example code: https://github.com/firebase/quickstart-android/tree/master/firestore
 * Author: Leo Goldberg
 * */
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

            Glide.with(binding.ratingImageView)
                    .load(review.photoUrl)
                    .into(binding.ratingImageView)

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
