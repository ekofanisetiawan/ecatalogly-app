# ecatalogly-app
Clean and simple product catalog app with CRUD features and API integration

# Ecatalogly

Simple app to display and modify product data.
This app displays product data from API and allows basic CRUD (add, update, delete) in a simple UI.

# Features
- Show product list using RecyclerView
- Add, update, and delete product
- Fetch data from API (Retrofit)
- Load image using Glide
- Click product to see detail
- Loading indicator and empty state

# API
Using public API:
https://dummyjson.com/products

# Notes
- Image uses external URL, so it depends on internet connection
- If image link is empty or error, default image will be shown
- Data from add/update/delete is not saved permanently

# Tech
- Kotlin
- Retrofit
- RecyclerView
- Glide
