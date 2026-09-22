package com.example.data

import kotlinx.coroutines.flow.Flow

class CreationRepository(private val creationDao: CreationDao) {

    val allCreations: Flow<List<CreationItem>> = creationDao.getAllCreations()
    val favoriteCreations: Flow<List<CreationItem>> = creationDao.getFavoriteCreations()

    fun getByCategory(category: String): Flow<List<CreationItem>> {
        return if (category == "ALL") {
            creationDao.getAllCreations()
        } else {
            creationDao.getCreationsByCategory(category)
        }
    }

    fun search(query: String): Flow<List<CreationItem>> = creationDao.searchCreations(query)

    suspend fun insert(item: CreationItem): Long = creationDao.insertCreation(item)

    suspend fun update(item: CreationItem) = creationDao.updateCreation(item)

    suspend fun delete(item: CreationItem) = creationDao.deleteCreation(item)

    suspend fun deleteById(id: Long) = creationDao.deleteCreationById(id)

    suspend fun toggleFavorite(id: Long, currentFavorite: Boolean) {
        creationDao.setFavorite(id, !currentFavorite)
    }

    suspend fun preloadSamplesIfNeeded() {
        if (creationDao.getCount() == 0) {
            val samples = listOf(
                CreationItem(
                    title = "Cyberpunk Neo-Tokyo Concept Art",
                    category = "VISUAL",
                    prompt = "Futuristic neon cyberpunk city street at rainy dusk with glowing holographic billboards, reflections, vibrant magenta and teal",
                    content = "High-definition visual concept rendered with local Copilot NPU acceleration. Volumetric atmospheric haze, neon reflections on wet asphalt, cinematic 8K lighting with deep teal and magenta accents.",
                    style = "Cyberpunk",
                    aspectRatio = "1:1",
                    drawableName = "sample_cyberpunk",
                    isFavorite = true,
                    tags = "Cyberpunk,ConceptArt,8K,TealMagenta"
                ),
                CreationItem(
                    title = "YouTube Video: 5 AI Workflow Secrets for Creators",
                    category = "SCRIPT",
                    prompt = "Write a high-retention 3-minute video script explaining how creators can use on-device AI to speed up editing and concepting.",
                    content = """[HOOK - 0:00-0:15]
"What if you could turn a raw idea into a full storyboard, voiceover, and video draft in under 60 seconds without leaving your laptop?"

[INTRO - 0:15-0:35]
Welcome back! Today we are breaking down 5 game-changing local AI workflow secrets that top digital creators are using in 2026. No cloud lag, complete privacy, and zero subscription fatigue.

[POINT 1: LOCAL PROMPT CHORDS - 0:35-1:15]
Secret number one: Multi-modal local prompting. Instead of typing separate requests, feed your thumbnail concept and script synopsis simultaneously into your NPU studio.

[POINT 2: INSTANT TELEPROMPTER PACING - 1:15-1:55]
Secret two: Script-to-speech cadence matching. Time your speaking rate against automatic visual cut points at exactly 145 words per minute.

[CALL TO ACTION - 1:55-2:20]
Try these workflows in your studio today! Drop your favorite tip in the comments below, hit subscribe, and let's create the future.""",
                    style = "YouTube Longform",
                    aspectRatio = "16:9",
                    drawableName = "banner_creator_studio",
                    isFavorite = true,
                    tags = "YouTube,Workflow,Retention,Tutorial"
                ),
                CreationItem(
                    title = "Ethereal Floating Islands Horizon",
                    category = "VISUAL",
                    prompt = "Breathtaking fantasy floating island with glowing crystalline waterfalls and warm sunset clouds, lush greenery, cinematic concept art",
                    content = "Fantasy concept art generated with Studio Ghibli warm sunlight palette. Features cascading crystalline waterfalls, lush biomes, and towering cloud towers illuminated by golden hour lighting.",
                    style = "Fantasy / Anime",
                    aspectRatio = "1:1",
                    drawableName = "sample_landscape",
                    isFavorite = false,
                    tags = "Fantasy,Landscape,GoldenHour,Atmospheric"
                ),
                CreationItem(
                    title = "Energetic Tech Podcast Trailer Intro",
                    category = "AUDIO",
                    prompt = "Generate a punchy, high-energy 30-second voiceover script for the launch of 'The Creator Edge' podcast episode on local neural engines.",
                    content = """(Upbeat electronic music fades in)
"Welcome to The Creator Edge! The podcast where artificial intelligence meets human ingenuity. In today's episode: how dedicated NPU silicon is redefining music production, 3D art, and video creation right on your machine. Plug in your headphones, grab your coffee, and let's dive in!"
(Bass drop and transition sting)""",
                    style = "Podcast Voiceover",
                    aspectRatio = "16:9",
                    drawableName = "",
                    isFavorite = true,
                    tags = "Voiceover,Podcast,BGM,Pacing"
                ),
                CreationItem(
                    title = "Viral Reel Script: Stop Doing This While Filming!",
                    category = "SOCIAL",
                    prompt = "Create a 30-second viral reel script highlighting the #1 mistake beginner video creators make with lighting.",
                    content = """[TEXT ON SCREEN: Stop making this #1 lighting mistake! 🚫]
[VISUAL: Creator snapping fingers, transitioning from dull flat lighting to cinematic three-point rim light]

"If you're still relying on just one ring light right in front of your face, stop! You're flattening your facial depth.

Instead, move that key light 45 degrees to your side, turn down your room lights, and add a subtle rim light behind your shoulder. Boom. You went from webcam to Netflix documentary in 10 seconds.

Save this for your next shoot!"
#CreatorTips #FilmmakingHacks #VideoProduction #ContentCreator""",
                    style = "Viral Short",
                    aspectRatio = "9:16",
                    drawableName = "",
                    isFavorite = false,
                    tags = "Reels,TikTok,ShortForm,LightingTips"
                )
            )
            for (sample in samples) {
                creationDao.insertCreation(sample)
            }
        }
    }
}
