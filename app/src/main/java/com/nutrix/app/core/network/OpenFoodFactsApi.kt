package com.nutrix.app.core.network

import com.google.gson.annotations.SerializedName
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import java.util.concurrent.TimeUnit

data class OpenFoodFactsResponse(
    @SerializedName("status") val status: Int,
    @SerializedName("status_verbose") val statusVerbose: String?,
    @SerializedName("product") val product: OffProduct?
)

data class OffProduct(
    @SerializedName("product_name") val productName: String?,
    @SerializedName("brands") val brands: String?,
    @SerializedName("serving_size") val servingSize: String?,
    @SerializedName("image_front_url") val imageFrontUrl: String?,
    @SerializedName("nutriments") val nutriments: OffNutriments?,
    @SerializedName("ingredients_text") val ingredientsText: String?
)

data class OffNutriments(
    @SerializedName("energy-kcal_100g") val energyKcal100g: Float?,
    @SerializedName("energy-kcal_serving") val energyKcalServing: Float?,
    @SerializedName("proteins_100g") val proteins100g: Float?,
    @SerializedName("carbohydrates_100g") val carbohydrates100g: Float?,
    @SerializedName("fat_100g") val fat100g: Float?,
    @SerializedName("fiber_100g") val fiber100g: Float?,
    @SerializedName("sugars_100g") val sugars100g: Float?,
    @SerializedName("sodium_100g") val sodium100g: Float?
)

interface OpenFoodFactsApi {
    @GET("api/v2/product/{barcode}.json")
    suspend fun getProductByBarcode(@Path("barcode") barcode: String): OpenFoodFactsResponse

    companion object {
        private const val BASE_URL = "https://world.openfoodfacts.org/"

        fun create(): OpenFoodFactsApi {
            val client = OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .readTimeout(8, TimeUnit.SECONDS)
                .build()

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(OpenFoodFactsApi::class.java)
        }
    }
}