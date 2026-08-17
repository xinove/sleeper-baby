package com.sleeperbaby.app.library

enum class StoryId {
    Caperucita,
    TresCerditos,
    Ricitos,
    PatitoFeo,
    Cenicienta,
    BellaDurmiente,
    GatoConBotas,
    PrincesaGuisante,
    LiebreTortuga,
    Pulgarcito,
    Soldadito,
    Rapunzel,
}

data class Story(
    val id: StoryId,
    val title: String,
    val origin: String,
    val teaser: String,
    val coverColor: Long,
    val coverArt: Int? = null,
    val paragraphs: List<String>,
)

fun Story.spokenParts(): List<String> =
    listOf("$title.") + paragraphs + listOf("Buenas noches.")

fun Story.spokenNarration(): String = spokenParts().joinToString(" ")

fun Story.audioAssetPath(): String {
    val file = when (id) {
        StoryId.Caperucita -> "caperucita"
        StoryId.TresCerditos -> "tres_cerditos"
        StoryId.Ricitos -> "ricitos"
        StoryId.PatitoFeo -> "patito_feo"
        StoryId.Cenicienta -> "cenicienta"
        StoryId.BellaDurmiente -> "bella_durmiente"
        StoryId.GatoConBotas -> "gato_con_botas"
        StoryId.PrincesaGuisante -> "princesa_guisante"
        StoryId.LiebreTortuga -> "liebre_tortuga"
        StoryId.Pulgarcito -> "pulgarcito"
        StoryId.Soldadito -> "soldadito"
        StoryId.Rapunzel -> "rapunzel"
    }
    return "stories/$file.mp3"
}
