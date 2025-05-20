package me.baldo.rootlink.data.remote

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import me.baldo.rootlink.BuildConfig

// Request object schema
// {
//     "location": {
//     object (LatLng)
// },
//     "extraComputations": [
//     enum (ExtraComputation)
//     ],
//     "uaqiColorPalette": enum (ColorPalette),
//     "customLocalAqis": [
//     {
//         object (CustomLocalAqi)
//     }
//     ],
//     "universalAqi": boolean,
//     "languageCode": string
// }

@Serializable
private data class LatLng(
    val latitude: Double,
    val longitude: Double,
)

@Serializable
private enum class ExtraComputation {
    EXTRA_COMPUTATION_UNSPECIFIED,
    HEALTH_RECOMMENDATIONS,
    DOMINANT_POLLUTANT_CONCENTRATION,
    POLLUTANT_CONCENTRATION,
    LOCAL_AQI,
    POLLUTANT_ADDITIONAL_INFO
}

@Serializable
private data class AirQualityRequestMessage(
    @SerialName("location")
    val location: LatLng,
    @SerialName("extraComputations")
    val extraComputations: List<ExtraComputation>,
    @SerialName("languageCode")
    val languageCode: String // = Locale.current.region
)

// Air quality index schema
// {
//   "code": string,
//   "displayName": string,
//   "aqiDisplay": string,
//   "color": {
//     object (Color)
//   },
//   "category": string,
//   "dominantPollutant": string,
//   "aqi": integer
// }
//

@Serializable
data class AirQualityIndex(
    @SerialName("code")
    val code: String,
    @SerialName("displayName")
    val displayName: String,
    @SerialName("aqiDisplay")
    val aqiDisplay: String,
    @SerialName("category")
    val category: String,
    @SerialName("dominantPollutant")
    val dominantPollutant: String,
    @SerialName("aqi")
    val aqi: Int,
)

// Response object schema
// {
//     "dateTime": string,
//     "regionCode": string,
//     "indexes": [
//     {
//         object (AirQualityIndex)
//     }
//     ],
//     "pollutants": [
//     {
//         object (Pollutant)
//     }
//     ],
//     "healthRecommendations": {
//     object (HealthRecommendations)
//     }
// }

@Serializable
private data class AirQualityResponseMessage(
    @SerialName("dateTime")
    val dateTime: String,
    @SerialName("regionCode")
    val regionCode: String,
    @SerialName("indexes")
    val indexes: List<AirQualityIndex>
)

class AirQualityDataSource(
    private val httpClient: HttpClient
) {
    companion object {
        private const val TAG = "AirQualityDataSource"
        private const val BASE_URL =
            "https://airquality.googleapis.com/v1/currentConditions:lookup"
    }

    suspend fun getAirQuality(latitude: Double, longitude: Double): AirQualityIndex? {
        Log.i(TAG, "Getting air quality for $latitude, $longitude")
        val request = AirQualityRequestMessage(
            location = LatLng(latitude, longitude),
            extraComputations = listOf(
                ExtraComputation.DOMINANT_POLLUTANT_CONCENTRATION,
            ),
            languageCode = "en"
        )
        val answer = try {
            httpClient.post("$BASE_URL?key=${BuildConfig.MAPS_KEY}") {
                contentType(ContentType.Application.Json)
                setBody(request)
            }.body<AirQualityResponseMessage>().indexes.first()
        } catch (e: Exception) {
            Log.e(TAG, "Error while sending message: ${e.message}")
            null
        }
        Log.d(TAG, "Air quality response: $answer")
        return answer
    }
}