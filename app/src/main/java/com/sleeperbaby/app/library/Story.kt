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
    NubeCohete,
    RobotDormilon,
    EstrellaNavidad,
    RenoCalcetin,
    CapitanLuciernaga,
    Superabuela,
    BosqueSusurros,
    PrincesaNube,
    GuillePirata,
    TesoroGalleta,
    IslaSiesta,
    DueloCumplidos,
    GuilleElige,
    EstrellaElige,
    NinoElige,
}

enum class StoryShelf(val title: String) {
    All("Todos"),
    Classic("Clásicos"),
    Modern("Modernos"),
    Christmas("Navideños"),
    Heroes("Superhéroes"),
    Magic("Mágicos"),
    Princesses("Princesas"),
    Pirates("Piratas"),
}

data class StoryChoice(
    val label: String,
    val nextId: String,
)

data class StoryNode(
    val id: String,
    val paragraphs: List<String>,
    val question: String? = null,
    val choices: List<StoryChoice> = emptyList(),
)

data class StoryAdventure(
    val nodes: List<StoryNode>,
) {
    val startId: String get() = nodes.first().id

    fun node(id: String): StoryNode =
        nodes.firstOrNull { it.id == id } ?: nodes.first()
}

data class Story(
    val id: StoryId,
    val title: String,
    val origin: String,
    val teaser: String,
    val coverColor: Long,
    val coverArt: Int? = null,
    val shelves: Set<StoryShelf> = setOf(StoryShelf.Classic),
    val paragraphs: List<String>,
    val adventure: StoryAdventure? = null,
)

fun Story.isAdventure(): Boolean = adventure != null

fun Story.matchesShelf(shelf: StoryShelf): Boolean =
    shelf == StoryShelf.All || shelf in shelves

fun storyShelvesOf(stories: List<Story>): List<StoryShelf> {
    val present = StoryShelf.entries.filter { shelf ->
        shelf != StoryShelf.All && stories.any { shelf in it.shelves }
    }
    return listOf(StoryShelf.All) + present
}

const val STORY_CLOSING = "¡Buenas noches, chicos!"

fun Story.spokenParts(): List<String> =
    listOf("$title.") + paragraphs + listOf(STORY_CLOSING)

fun Story.spokenNarration(): String = spokenParts().joinToString(" ")

fun Story.fileStem(): String = when (id) {
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
    StoryId.NubeCohete -> "nube_cohete"
    StoryId.RobotDormilon -> "robot_dormilon"
    StoryId.EstrellaNavidad -> "estrella_navidad"
    StoryId.RenoCalcetin -> "reno_calcetin"
    StoryId.CapitanLuciernaga -> "capitan_luciernaga"
    StoryId.Superabuela -> "superabuela"
    StoryId.BosqueSusurros -> "bosque_susurros"
    StoryId.PrincesaNube -> "princesa_nube"
    StoryId.GuillePirata -> "guille_pirata"
    StoryId.TesoroGalleta -> "tesoro_galleta"
    StoryId.IslaSiesta -> "isla_siesta"
    StoryId.DueloCumplidos -> "duelo_cumplidos"
    StoryId.GuilleElige -> "guille_elige"
    StoryId.EstrellaElige -> "estrella_elige"
    StoryId.NinoElige -> "nino_elige"
}

fun Story.audioAssetPath(nodeId: String? = null): String {
    val stem = fileStem()
    val adventure = adventure
    if (adventure == null || nodeId == null || nodeId == adventure.startId) {
        return "stories/$stem.mp3"
    }
    return "stories/${stem}_$nodeId.mp3"
}
