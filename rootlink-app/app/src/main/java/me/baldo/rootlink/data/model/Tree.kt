package me.baldo.rootlink.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Tree(
    @SerialName("cardId")
    val cardId: String,
    @SerialName("region")
    val region: String,
    @SerialName("province")
    val province: String,
    @SerialName("municipality")
    val municipality: String,
    @SerialName("location")
    val location: String,
    @SerialName("latitude")
    val latitude: String,
    @SerialName("longitude")
    val longitude: String,
    @SerialName("altitude")
    val altitude: Double,
    @SerialName("urban")
    val urban: Boolean,
    @SerialName("speciesScientificName")
    val speciesScientificName: String,
    @SerialName("species")
    val species: String,
    @SerialName("circumference")
    val circumference: Double,
    @SerialName("height")
    val height: Double,
    @SerialName("monumentalityCriteria")
    val monumentalityCriteria: String,
    @SerialName("significantPublicInterest")
    val significantPublicInterest: Boolean,
) {
    override fun equals(other: Any?): Boolean {
        return if (other is Tree) {
            this.cardId == other.cardId
        } else {
            false
        }
    }

    override fun hashCode(): Int {
        var result = cardId.hashCode()
        return result
    }
}
