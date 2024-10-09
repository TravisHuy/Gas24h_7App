package com.nhathuy.gas24h_7app.admin.product_management.all_product

import com.nhathuy.gas24h_7app.data.model.Product
import com.nhathuy.gas24h_7app.data.repository.CategoryRepository
import com.nhathuy.gas24h_7app.data.repository.ProductRepository
import com.nhathuy.gas24h_7app.util.normalizeVietnamese
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

class AllProductPresenter @Inject constructor(private val productRepository: ProductRepository,
                                              private val categoryRepository: CategoryRepository
):AllProductContract.Presenter{

    private var view:AllProductContract.View? =null
    private val job = SupervisorJob()
    private val coroutineScope= CoroutineScope(Dispatchers.Main+job)
    private var currentProducts: List<Product> = emptyList()
    override fun attachView(view: AllProductContract.View) {
        this.view = view
    }

    override fun detachView() {
        view = null
        job.cancel()
    }

    override fun loadProducts() {
        coroutineScope.launch {
            view?.showLoading()
            try {
                val result = productRepository.getAllProducts()
                result.fold(
                    onSuccess = { products ->
                        currentProducts = products
                        view?.showProducts(products)
                    },
                    onFailure = {
                        e ->
                        view?.showMessage("Failed load products: ${e.message}")
                    }
                )
            }
            catch (e:Exception){
                view?.showMessage("Failed load products: ${e.message}")
            }
            finally {
                view?.hideLoading()
            }
        }
    }

    override fun sortProductsByPrice(highToLow: Boolean) {
        val sortedProducts = if(highToLow){
            currentProducts.sortedByDescending { it.getDiscountedPrice() }
        }
        else{
            currentProducts.sortedBy { it.getDiscountedPrice() }
        }
        view?.showProducts(sortedProducts)
    }

    override fun sortProductsByStock(highToLow: Boolean) {
        val sortedProducts = if(highToLow){
            currentProducts.sortedByDescending { it.stockCount }
        }
        else{
            currentProducts.sortedBy { it.stockCount }
        }
        view?.showProducts(sortedProducts)
    }

    override fun sortProductsBySelling(highToLow: Boolean) {
        coroutineScope.launch {
            view?.showLoading()
            try {
                val productsWithSoldCount = currentProducts.map { product ->
                    val soldCount = productRepository.getProductSoldCount(product.id).getOrDefault(0)
                    Pair(product,soldCount)
                }
                val sortedProducts  = if(highToLow){
                    productsWithSoldCount.sortedByDescending { it.second }
                }
                else{
                    productsWithSoldCount.sortedBy { it.second}
                }.map { it.first }
                view?.showProducts(sortedProducts)
            }
            catch (e:Exception){
                view?.showMessage("Failed to sort products: ${e.message}")
            }
            finally {
                view?.hideLoading()
            }
        }
    }

    override fun searchProducts(query: String) {
        coroutineScope.launch {
            view?.showLoading()
            try {
                val normalizedQuery = query.normalizeVietnamese()
                val products = productRepository.getAllProducts().getOrThrow()
                val categories = categoryRepository.getCategories().getOrThrow()

                val filteredProducts = products.filter { product ->
                    val normalizedName = product.name.normalizeVietnamese()
                    val matchesName = normalizedName.contains(normalizedQuery, ignoreCase = true)
                    val category = categories.find { it.id == product.categoryId }
                    val normalizedCategoryName = category?.categoryName?.normalizeVietnamese() ?: ""
                    val matchesCategory = normalizedCategoryName.contains(normalizedQuery, ignoreCase = true)
                    matchesName || matchesCategory
                }
                view?.showProducts(filteredProducts)
            } catch (e: Exception) {
                view?.showMessage("Không thể tìm kiếm sản phẩm: ${e.message}")
            } finally {
                view?.hideLoading()
            }
        }
    }

    override fun onEditProductClicked(product: Product) {
        view?.showEditProduct(product)
    }

    override fun onRemoveProductClicked(product: Product) {
        view?.showRemoveProductDialog(product)
    }

    override fun editProduct(product: Product) {
        coroutineScope.launch {
            view?.showLoading()
            try {
                productRepository.updateProduct(product)
                loadProducts() // Reload the product list
                view?.showMessage("Product updated successfully")
            } catch (e: Exception) {
                view?.showMessage("Failed to update product: ${e.message}")
            } finally {
                view?.hideLoading()
            }
        }
    }

    override fun removeProduct(productId: String) {
        coroutineScope.launch {
            view?.showLoading()
            try {
                productRepository.removeProduct(productId)
                loadProducts() // Reload the product list
                view?.showMessage("Product updated successfully")
            } catch (e: Exception) {
                view?.showMessage("Failed to update product: ${e.message}")
            } finally {
                view?.hideLoading()
            }
        }
    }
}