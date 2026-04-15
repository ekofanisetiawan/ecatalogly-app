package com.example.akashacrudapp

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var btnAdd: ImageButton
    private lateinit var txtProductCount: TextView
    private lateinit var txtEmpty: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var productAdapter: ProductAdapter

    private val productList = mutableListOf<Product>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()
        setupRecyclerView()
        setupActions()
        loadProducts()
        handleBackPress()
    }

    private fun initViews() {
        recyclerView = findViewById(R.id.recyclerView)
        btnAdd = findViewById(R.id.btnAdd)
        txtProductCount = findViewById(R.id.txtProductCount)
        txtEmpty = findViewById(R.id.txtEmpty)
        progressBar = findViewById(R.id.progressBar)
    }

    private fun setupRecyclerView() {
        recyclerView.layoutManager = GridLayoutManager(this, 2)

        productAdapter = ProductAdapter(
            productList,
            onItemClick = { position ->
                showProductDetailDialog(position)
            },
            onEditClick = { position ->
                showEditDialog(position)
            },
            onDeleteClick = { position ->
                showDeleteDialog(position)
            }
        )

        recyclerView.adapter = productAdapter
    }

    private fun setupActions() {
        btnAdd.setOnClickListener {
            showAddDialog()
        }
    }

    private fun loadProducts() {
        showLoading(true)

        RetrofitClient.instance.getProducts().enqueue(object : Callback<ProductResponse> {
            override fun onResponse(
                call: Call<ProductResponse>,
                response: Response<ProductResponse>
            ) {
                showLoading(false)

                if (response.isSuccessful) {
                    val products = response.body()?.products.orEmpty()
                    productList.clear()
                    productList.addAll(products)
                    productAdapter.notifyDataSetChanged()
                }

                updateProductCount()
                updateEmptyState()
            }

            override fun onFailure(call: Call<ProductResponse>, t: Throwable) {
                showLoading(false)
                updateProductCount()
                updateEmptyState()
            }
        })
    }

    private fun showLoading(isLoading: Boolean) {
        progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        recyclerView.visibility = if (isLoading) View.GONE else View.VISIBLE
        txtEmpty.visibility = View.GONE
    }

    private fun updateProductCount() {
        val total = productList.size
        txtProductCount.text = "$total Product Found"
    }

    private fun updateEmptyState() {
        if (productList.isEmpty()) {
            recyclerView.visibility = View.GONE
            txtEmpty.visibility = View.VISIBLE
        } else {
            recyclerView.visibility = View.VISIBLE
            txtEmpty.visibility = View.GONE
        }
    }

    private fun getImageInput(etImage: EditText): String {
        return etImage.text.toString()
            .trim()
            .replace("\n", "")
    }

    private fun validateInput(etTitle: EditText, etPrice: EditText): Boolean {
        val title = etTitle.text.toString().trim()
        val priceText = etPrice.text.toString().trim()

        return when {
            title.isEmpty() -> {
                etTitle.error = "Product name is required"
                false
            }
            priceText.isEmpty() -> {
                etPrice.error = "Price is required"
                false
            }
            priceText.toDoubleOrNull() == null -> {
                etPrice.error = "Price must be a number"
                false
            }
            else -> true
        }
    }

    private fun refreshList() {
        productAdapter.notifyDataSetChanged()
        updateProductCount()
        updateEmptyState()
    }

    private fun showAddDialog() {
        val view = LayoutInflater.from(this).inflate(R.layout.dialog_add_product, null)

        val txtDialogTitle = view.findViewById<TextView>(R.id.txtDialogTitle)
        val etTitle = view.findViewById<EditText>(R.id.etTitle)
        val etPrice = view.findViewById<EditText>(R.id.etPrice)
        val etImage = view.findViewById<EditText>(R.id.etImage)
        val btnCancelForm = view.findViewById<Button>(R.id.btnCancelForm)
        val btnSaveForm = view.findViewById<Button>(R.id.btnSaveForm)

        txtDialogTitle.text = "Add Product"
        btnSaveForm.text = "Save"

        val dialog = AlertDialog.Builder(this)
            .setView(view)
            .create()

        dialog.show()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        btnCancelForm.setOnClickListener {
            dialog.dismiss()
        }

        btnSaveForm.setOnClickListener {
            if (!validateInput(etTitle, etPrice)) {
                return@setOnClickListener
            }

            val newProduct = Product(
                id = productList.size + 1,
                title = etTitle.text.toString().trim(),
                price = etPrice.text.toString().trim().toDouble(),
                thumbnail = getImageInput(etImage)
            )

            productList.add(0, newProduct)
            productAdapter.notifyItemInserted(0)
            recyclerView.scrollToPosition(0)
            updateProductCount()
            updateEmptyState()
            dialog.dismiss()
        }
    }

    private fun showEditDialog(position: Int) {
        val product = productList[position]
        val view = LayoutInflater.from(this).inflate(R.layout.dialog_add_product, null)

        val txtDialogTitle = view.findViewById<TextView>(R.id.txtDialogTitle)
        val etTitle = view.findViewById<EditText>(R.id.etTitle)
        val etPrice = view.findViewById<EditText>(R.id.etPrice)
        val etImage = view.findViewById<EditText>(R.id.etImage)
        val btnCancelForm = view.findViewById<Button>(R.id.btnCancelForm)
        val btnSaveForm = view.findViewById<Button>(R.id.btnSaveForm)

        txtDialogTitle.text = "Update Product"
        btnSaveForm.text = "Update"

        etTitle.setText(product.title)
        etPrice.setText(product.price.toString())
        etImage.setText(product.thumbnail)

        val dialog = AlertDialog.Builder(this)
            .setView(view)
            .create()

        dialog.show()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        btnCancelForm.setOnClickListener {
            dialog.dismiss()
        }

        btnSaveForm.setOnClickListener {
            if (!validateInput(etTitle, etPrice)) {
                return@setOnClickListener
            }

            val updatedProduct = product.copy(
                title = etTitle.text.toString().trim(),
                price = etPrice.text.toString().trim().toDouble(),
                thumbnail = getImageInput(etImage)
            )

            productList[position] = updatedProduct
            refreshList()
            dialog.dismiss()
        }
    }

    private fun showDeleteDialog(position: Int) {
        val view = LayoutInflater.from(this).inflate(R.layout.dialog_delete_product, null)

        val btnCancel = view.findViewById<Button>(R.id.btnCancel)
        val btnDelete = view.findViewById<Button>(R.id.btnDelete)

        val dialog = AlertDialog.Builder(this)
            .setView(view)
            .create()

        dialog.show()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        btnDelete.setOnClickListener {
            productList.removeAt(position)
            refreshList()
            dialog.dismiss()
        }
    }

    private fun showProductDetailDialog(position: Int) {
        val product = productList[position]
        val view = LayoutInflater.from(this).inflate(R.layout.dialog_product_detail, null)

        val imgDetailProduct = view.findViewById<ImageView>(R.id.imgDetailProduct)
        val txtDetailTitle = view.findViewById<TextView>(R.id.txtDetailTitle)
        val txtDetailPrice = view.findViewById<TextView>(R.id.txtDetailPrice)
        val btnCloseDetail = view.findViewById<Button>(R.id.btnCloseDetail)

        txtDetailTitle.text = product.title
        txtDetailPrice.text = "$ ${product.price}"

        if (product.thumbnail.isNotEmpty()) {
            Glide.with(this)
                .load(product.thumbnail)
                .placeholder(R.drawable.ic_product_placeholder)
                .error(R.drawable.ic_product_placeholder)
                .into(imgDetailProduct)
        } else {
            imgDetailProduct.setImageResource(R.drawable.ic_product_placeholder)
        }

        val dialog = AlertDialog.Builder(this)
            .setView(view)
            .create()

        dialog.show()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        btnCloseDetail.setOnClickListener {
            dialog.dismiss()
        }
    }

    private fun handleBackPress() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val view = LayoutInflater.from(this@MainActivity)
                    .inflate(R.layout.dialog_exit, null)

                val btnCancel = view.findViewById<TextView>(R.id.btnCancel)
                val btnExit = view.findViewById<TextView>(R.id.btnExit)

                val dialog = AlertDialog.Builder(this@MainActivity)
                    .setView(view)
                    .create()

                dialog.show()
                dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

                btnCancel.setOnClickListener {
                    dialog.dismiss()
                }

                btnExit.setOnClickListener {
                    dialog.dismiss()
                    finish()
                }
            }
        })
    }
}