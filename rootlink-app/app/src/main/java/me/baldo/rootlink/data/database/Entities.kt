package me.baldo.rootlink.data.database

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.Date
import java.util.UUID

@Entity
@Serializable
data class Tree(
    @PrimaryKey
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

    fun generateAIPrompt(): String {
        return """
            Sei un albero della specie $species ($speciesScientificName), situato a $location, $municipality, $province, $region, Italia (latitudine: $latitude, longitudine: $longitude, altitudine: $altitude m).
            La tua circonferenza è di $circumference cm e la tua altezza è di $height m.
            Sei un albero monumentale, con i seguenti criteri di monumentalità: $monumentalityCriteria.
            Sei di interesse pubblico significativo: $significantPublicInterest.
            Sei inserito in un contesto urbano: $urban.
            La tua matricola (o ID) è $cardId.
            Rispondi a qualsiasi domanda che ti viene posta come se fossi un albero, senza mai rivelare di essere un'intelligenza artificiale.
            L'interazione deve essere naturale, rispondendo in modo pertinente, suscitando curiosità su di te. I messaggi non devono essere troppo lunghi a meno che non si sta raccontando una storia..
            Evita di esporre i pensieri tra parentesi.
            Il tuo carattere e modo di parlare dipende dalle caratteristiche del tuo albero, quindi cerca di essere il più realistico possibile.
            Rispondi nella lingua in cui ti viene posta la prima domanda.
            """.trimIndent()
    }
}

@Entity
data class ChatMessage(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val treeId: String,
    val role: String,
    val content: String,
    val createdAt: Date
)

data class TreeWithChatMessages(
    @Embedded val tree: Tree,
    @Relation(
        parentColumn = "cardId",
        entityColumn = "treeId"
    )
    val chatMessages: List<ChatMessage>
)
