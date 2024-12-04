data class ShowResponse(
    val id: Int,
    val date: String,
    val cover_art_urls: String,
    val album_cover_url: String,
    val album_zip_url: String,
    val duration: Int,
    val incomplete: Boolean,
    val admin_notes: String,
    val tour_name: String,
    val venue_name: String,
    val venue: Venue, // Bu artıq bir obyekt olmalıdır
    val taper_notes: String,
    val likes_count: Int,
    val updated_at: String,
    val tags: List<Tag>,
    val tracks: List<String>,
    val liked_by_user: String,
    val previous_show_date: String,
    val next_show_date: String
)

data class Venue(
    val slug: String,
    val name: String,
    val other_names: List<String>,
    val latitude: Double,
    val longitude: Double,
    val city: String,
    val state: String,
    val country: String,
    val location: String,
    val shows_count: Int,
    val updated_at: String
)

data class Tag(
    val name: String,
    val description: String,
    val color: String,
    val priority: Int,
    val notes: String
)
