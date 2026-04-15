package com.example.akashacrudapp

import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class ProductAdapter(
    private val productList: MutableList<Product>,
    private val onItemClick: (position: Int) -> Unit,
    private val onEditClick: (position: Int) -> Unit,
    private val onDeleteClick: (position: Int) -> Unit
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    init {
        setHasStableIds(true)
    }

    class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgProduct: ImageView = itemView.findViewById(R.id.imgProduct)
        val txtTitle: TextView = itemView.findViewById(R.id.txtTitle)
        val txtPrice: TextView = itemView.findViewById(R.id.txtPrice)
        val btnMenu: ImageButton = itemView.findViewById(R.id.btnMenu)
    }

    override fun getItemId(position: Int): Long {
        return productList[position].id.toLong()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = productList[position]

        holder.txtTitle.text = product.title
        holder.txtPrice.text = "$ ${product.price}"

        Glide.with(holder.itemView.context).clear(holder.imgProduct)
        holder.imgProduct.setImageResource(R.drawable.ic_product_placeholder)

        if (product.thumbnail.isNotEmpty()) {
            Glide.with(holder.itemView.context)
                .load(product.thumbnail)
                .placeholder(R.drawable.ic_product_placeholder)
                .error(R.drawable.ic_product_placeholder)
                .into(holder.imgProduct)
        } else {
            holder.imgProduct.setImageResource(R.drawable.ic_product_placeholder)
        }

        holder.itemView.setOnClickListener {
            val currentPosition = holder.bindingAdapterPosition
            if (currentPosition != RecyclerView.NO_POSITION) {
                onItemClick(currentPosition)
            }
        }

        holder.btnMenu.setOnClickListener { anchorView ->
            val currentPosition = holder.bindingAdapterPosition
            if (currentPosition != RecyclerView.NO_POSITION) {
                showCustomMenu(anchorView, currentPosition)
            }
        }
    }

    private fun showCustomMenu(anchorView: View, position: Int) {
        val context = anchorView.context
        val popupView = LayoutInflater.from(context).inflate(R.layout.menu_product, null)

        val popupWindow = PopupWindow(
            popupView,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        )

        popupWindow.isOutsideTouchable = true
        popupWindow.setBackgroundDrawable(ColorDrawable())

        val menuEdit = popupView.findViewById<LinearLayout>(R.id.menuEdit)
        val menuDelete = popupView.findViewById<LinearLayout>(R.id.menuDelete)

        menuEdit.setOnClickListener {
            popupWindow.dismiss()
            onEditClick(position)
        }

        menuDelete.setOnClickListener {
            popupWindow.dismiss()
            onDeleteClick(position)
        }

        popupView.measure(
            View.MeasureSpec.UNSPECIFIED,
            View.MeasureSpec.UNSPECIFIED
        )

        val xOffset = -popupView.measuredWidth + anchorView.width
        val yOffset = 8

        popupWindow.showAsDropDown(anchorView, xOffset, yOffset)
    }

    override fun getItemCount(): Int = productList.size
}