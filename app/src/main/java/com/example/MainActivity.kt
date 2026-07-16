package com.example

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.Announcement
import com.example.data.model.GalleryPhoto
import com.example.data.model.Match
import com.example.data.model.Player
import com.example.ui.theme.*
import com.example.ui.viewmodel.ClubTab
import com.example.ui.viewmodel.ClubViewModel
import com.example.ui.viewmodel.ClubViewModelFactory
import androidx.compose.foundation.border
import androidx.compose.ui.text.TextStyle
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : ComponentActivity() {

    private val viewModel: ClubViewModel by viewModels {
        ClubViewModelFactory(application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                ClubApp(viewModel = viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClubApp(viewModel: ClubViewModel) {
    val context = LocalContext.current
    val activeTab by viewModel.activeTab.collectAsStateWithLifecycle()
    val players by viewModel.players.collectAsStateWithLifecycle()
    val matches by viewModel.matches.collectAsStateWithLifecycle()
    val announcements by viewModel.announcements.collectAsStateWithLifecycle()
    val photos by viewModel.photos.collectAsStateWithLifecycle()

    // Dialog trigger states
    var showAddPlayerDialog by remember { mutableStateOf(false) }
    var showAddMatchDialog by remember { mutableStateOf(false) }
    var showAddAnnouncementDialog by remember { mutableStateOf(false) }
    var showAddPhotoDialog by remember { mutableStateOf(false) }
    var selectedPhotoForLightbox by remember { mutableStateOf<GalleryPhoto?>(null) }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val isWideScreen = maxWidth >= 600.dp

        Row(modifier = Modifier.fillMaxSize()) {
            // Left Navigation Rail for wide screens
            if (isWideScreen) {
                NavigationRail(
                    containerColor = MaterialTheme.colorScheme.background,
                    contentColor = MaterialTheme.colorScheme.primary,
                    header = {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(vertical = 16.dp)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.img_club_logo),
                                contentDescription = "Logo Club 24",
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(CircleShape)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "CLUB 24",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    },
                    modifier = Modifier.testTag("wide_nav_rail")
                ) {
                    Spacer(modifier = Modifier.weight(1f))
                    ClubTab.values().forEach { tab ->
                        NavigationRailItem(
                            selected = activeTab == tab,
                            onClick = { viewModel.selectTab(tab) },
                            icon = { Icon(getTabIcon(tab), contentDescription = tab.name) },
                            label = { Text(tab.name.lowercase().capitalize(Locale.ROOT)) },
                            colors = NavigationRailItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                indicatorColor = MaterialTheme.colorScheme.primary,
                                unselectedIconColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                                unselectedTextColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                            ),
                            modifier = Modifier.testTag("nav_rail_${tab.name.lowercase()}")
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))
                }
            }

            // Main Content Area
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Image(
                                    painter = painterResource(id = R.drawable.img_club_logo),
                                    contentDescription = "Mini Logo",
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(Color.White)
                                        .padding(2.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "CLUB 24 FUTSAL",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Black,
                                        color = MaterialTheme.colorScheme.onPrimary
                                    )
                                    Text(
                                        text = "EBOLOWA • CAMEROUN",
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Medium,
                                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
                                    )
                                }
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            titleContentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        modifier = Modifier.testTag("club_top_bar")
                    )
                },
                bottomBar = {
                    // Bottom Navigation for mobile screens
                    if (!isWideScreen) {
                        NavigationBar(
                            containerColor = MaterialTheme.colorScheme.background,
                            tonalElevation = 8.dp,
                            modifier = Modifier.testTag("mobile_bottom_nav")
                        ) {
                            ClubTab.values().forEach { tab ->
                                NavigationBarItem(
                                    selected = activeTab == tab,
                                    onClick = { viewModel.selectTab(tab) },
                                    icon = { Icon(getTabIcon(tab), contentDescription = tab.name) },
                                    label = {
                                        Text(
                                            text = getTabShortLabel(tab),
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                            fontSize = 10.sp
                                        )
                                    },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                                        selectedTextColor = MaterialTheme.colorScheme.primary,
                                        indicatorColor = MaterialTheme.colorScheme.primary,
                                        unselectedIconColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                                        unselectedTextColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                                    ),
                                    modifier = Modifier.testTag("bottom_tab_${tab.name.lowercase()}")
                                )
                            }
                        }
                    }
                },
                floatingActionButton = {
                    // Floating button to quickly add context-based details
                    when (activeTab) {
                        ClubTab.JOUEURS -> {
                            ExtendedFloatingActionButton(
                                onClick = { showAddPlayerDialog = true },
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary,
                                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                                text = { Text("Nouveau Joueur") },
                                modifier = Modifier.testTag("add_player_fab")
                            )
                        }
                        ClubTab.MATCHS -> {
                            ExtendedFloatingActionButton(
                                onClick = { showAddMatchDialog = true },
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary,
                                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                                text = { Text("Nouveau Match") },
                                modifier = Modifier.testTag("add_match_fab")
                            )
                        }
                        ClubTab.ANNONCES -> {
                            ExtendedFloatingActionButton(
                                onClick = { showAddAnnouncementDialog = true },
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary,
                                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                                text = { Text("Nouvelle Annonce") },
                                modifier = Modifier.testTag("add_announcement_fab")
                            )
                        }
                        ClubTab.GALERIE -> {
                            ExtendedFloatingActionButton(
                                onClick = { showAddPhotoDialog = true },
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary,
                                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                                text = { Text("Ajouter Photo") },
                                modifier = Modifier.testTag("add_photo_fab")
                            )
                        }
                        else -> { /* No FAB */ }
                    }
                },
                modifier = Modifier.weight(1f)
            ) { paddingValues ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    when (activeTab) {
                        ClubTab.ACCUEIL -> HomeScreen(
                            matches = matches,
                            announcements = announcements,
                            onNavigateToMatches = { viewModel.selectTab(ClubTab.MATCHS) }
                        )
                        ClubTab.JOUEURS -> PlayersScreen(
                            players = players,
                            onDeletePlayer = { viewModel.deletePlayer(it) }
                        )
                        ClubTab.MATCHS -> MatchesScreen(
                            matches = matches,
                            onAddMatchResult = { match, our, opp ->
                                viewModel.addMatch(
                                    date = match.date,
                                    time = match.time,
                                    opponent = match.opponent,
                                    location = match.location,
                                    ourScore = our,
                                    opponentScore = opp,
                                    isFinished = true
                                )
                                viewModel.deleteMatch(match.id) // replace
                            },
                            onDeleteMatch = { viewModel.deleteMatch(it) }
                        )
                        ClubTab.ANNONCES -> AnnouncementsScreen(
                            announcements = announcements,
                            onDeleteAnnouncement = { viewModel.deleteAnnouncement(it) }
                        )
                        ClubTab.GALERIE -> GalleryScreen(
                            photos = photos,
                            onPhotoClick = { selectedPhotoForLightbox = it },
                            onDeletePhoto = { viewModel.deletePhoto(it) }
                        )
                        ClubTab.CONTACT -> ContactScreen()
                    }
                }
            }
        }
    }

    // --- Dialogs ---

    if (showAddPlayerDialog) {
        AddPlayerDialog(
            onDismiss = { showAddPlayerDialog = false },
            onConfirm = { name, number, position, desc ->
                viewModel.addPlayer(name = name, number = number, position = position, description = desc)
                showAddPlayerDialog = false
                Toast.makeText(context, "Joueur ajouté avec succès !", Toast.LENGTH_SHORT).show()
            }
        )
    }

    if (showAddMatchDialog) {
        AddMatchDialog(
            onDismiss = { showAddMatchDialog = false },
            onConfirm = { date, time, opponent, location ->
                viewModel.addMatch(date = date, time = time, opponent = opponent, location = location)
                showAddMatchDialog = false
                Toast.makeText(context, "Match programmé !", Toast.LENGTH_SHORT).show()
            }
        )
    }

    if (showAddAnnouncementDialog) {
        AddAnnouncementDialog(
            onDismiss = { showAddAnnouncementDialog = false },
            onConfirm = { title, content, type ->
                viewModel.addAnnouncement(title = title, content = content, type = type)
                showAddAnnouncementDialog = false
                Toast.makeText(context, "Annonce publiée !", Toast.LENGTH_SHORT).show()
            }
        )
    }

    if (showAddPhotoDialog) {
        AddPhotoDialog(
            onDismiss = { showAddPhotoDialog = false },
            onConfirm = { title, description, isLocalAsset ->
                val drawableName = if (isLocalAsset) "img_hero_banner" else "img_club_logo"
                viewModel.addPhoto(title = title, description = description, drawableName = drawableName)
                showAddPhotoDialog = false
                Toast.makeText(context, "Photo ajoutée à la galerie !", Toast.LENGTH_SHORT).show()
            }
        )
    }

    selectedPhotoForLightbox?.let { photo ->
        LightboxDialog(
            photo = photo,
            onDismiss = { selectedPhotoForLightbox = null }
        )
    }
}

// --- Home Screen (Accueil) ---
@Composable
fun HomeScreen(
    matches: List<Match>,
    announcements: List<Announcement>,
    onNavigateToMatches: () -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .testTag("home_screen")
    ) {
        // Hero Image Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.img_hero_banner),
                contentDescription = "Club 24 Match de Futsal",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            // Black gradient shadow
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.85f)),
                            startY = 100f
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(id = R.drawable.img_club_logo),
                        contentDescription = "Logo",
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .border(2.dp, Color.White, CircleShape)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "CLUB 24 EBOLOWA",
                            color = Color.White,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            text = "Une équipe, Une famille, Un seul cœur",
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 13.sp,
                            fontStyle = FontStyle.Italic,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        Column(modifier = Modifier.padding(16.dp)) {
            // Motto banner card
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Default.Favorite,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "« Une équipe, Une famille, Un seul cœur »",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Devise officielle du Club 24 Futsal Ebolowa, unissant nos talents pour la victoire.",
                        color = Color.White.copy(alpha = 0.85f),
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Prochain Match Highlight
            val upcomingMatch = matches.firstOrNull { !it.isFinished }
            Text(
                text = "🎯 PROCHAIN MATCH",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            if (upcomingMatch != null) {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Badge(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = Color.White
                            ) {
                                Text("A VENIR", modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp))
                            }
                            Text(
                                text = "${upcomingMatch.date} à ${upcomingMatch.time}",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Image(
                                    painter = painterResource(id = R.drawable.img_club_logo),
                                    contentDescription = null,
                                    modifier = Modifier.size(50.dp)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("CLUB 24", fontWeight = FontWeight.Bold)
                            }

                            Text("VS", fontSize = 20.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary)

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Box(
                                    modifier = Modifier
                                        .size(50.dp)
                                        .background(Color.Gray.copy(alpha = 0.2f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.SportsSoccer, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(30.dp))
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(upcomingMatch.opponent, fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Divider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Place, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Lieu: ${upcomingMatch.location}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = onNavigateToMatches,
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            modifier = Modifier.fillMaxWidth().testTag("home_view_matches_btn")
                        ) {
                            Text("Voir tous les matchs")
                        }
                    }
                }
            } else {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.EventBusy, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(48.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Aucun match programmé pour le moment.",
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center,
                            color = Color.Gray
                        )
                    }
                }
            }

            // Latest Announcement Highlight
            val latestAnnouncement = announcements.firstOrNull()
            Text(
                text = "📢 DERNIÈRE ANNONCE",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            if (latestAnnouncement != null) {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Badge(
                                containerColor = when (latestAnnouncement.type) {
                                    "Réunion" -> Color(0xFF1976D2)
                                    "Convocation" -> MaterialTheme.colorScheme.primary
                                    else -> Color(0xFF388E3C)
                                },
                                contentColor = Color.White
                            ) {
                                Text(latestAnnouncement.type.uppercase(), modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), fontSize = 10.sp)
                            }

                            val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                            Text(
                                text = sdf.format(Date(latestAnnouncement.date)),
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = latestAnnouncement.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = latestAnnouncement.content,
                            style = MaterialTheme.typography.bodyMedium,
                            maxLines = 3,
                            overflow = TextOverflow.Ellipsis,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                        )
                    }
                }
            } else {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier.padding(24.dp).fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Aucune annonce récente.", color = Color.Gray)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Word from the President
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.FormatQuote,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(36.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "« Prêts pour la victoire. Ensemble, nous sommes invincibles ! »",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            fontStyle = FontStyle.Italic
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "— Henri Pio, Président Fondateur",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}

// --- Players Screen (Joueurs) ---
@Composable
fun PlayersScreen(
    players: List<Player>,
    onDeletePlayer: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("players_screen")
    ) {
        // Quick Stats Summary
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            val totalPlayers = players.size
            val totalGoals = players.sumOf { it.goals }
            val topScorer = players.maxByOrNull { it.goals }

            Card(
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("EFFECTIF", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                    Text("$totalPlayers", fontSize = 22.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary)
                    Text("Joueurs inscrits", fontSize = 9.sp, color = Color.Gray)
                }
            }

            Card(
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("BUTS MARQUÉS", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                    Text("$totalGoals", fontSize = 22.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary)
                    Text("Cette saison", fontSize = 9.sp, color = Color.Gray)
                }
            }

            Card(
                modifier = Modifier.weight(1.2f),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("MEILLEUR BUTEUR", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                    Text(
                        text = topScorer?.name ?: "N/A",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = if (topScorer != null) "${topScorer.goals} buts" else "0 but",
                        fontSize = 10.sp,
                        color = Color.Gray,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Text(
            text = "📋 EFFECTIF OFFICIEL (${players.size})",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        if (players.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.PeopleOutline, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(64.dp))
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Aucun joueur enregistré.", color = Color.Gray)
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(players, key = { it.id }) { player ->
                    PlayerCard(player = player, onDelete = { onDeletePlayer(player.id) })
                }
            }
        }
    }
}

@Composable
fun PlayerCard(player: Player, onDelete: () -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded }
            .testTag("player_card_${player.name.replace(" ", "_").lowercase()}")
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Jersey number circle
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(MaterialTheme.colorScheme.primary, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = player.number.toString(),
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = player.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Badge(
                            containerColor = when (player.position) {
                                "Gardien" -> Color(0xFFFBC02D)
                                "Défenseur" -> Color(0xFF1976D2)
                                "Ailier" -> Color(0xFF388E3C)
                                else -> MaterialTheme.colorScheme.primary
                            },
                            contentColor = Color.White
                        ) {
                            Text(player.position, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), fontSize = 10.sp)
                        }
                    }
                }

                // Stats preview
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.EmojiEvents,
                        contentDescription = "Buts",
                        tint = GoldAccent,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${player.goals}",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    IconButton(onClick = onDelete) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Supprimer joueur",
                            tint = MaterialTheme.colorScheme.error.copy(alpha = 0.8f)
                        )
                    }
                }
            }

            AnimatedVisibility(
                visible = expanded,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Column(
                    modifier = Modifier
                        .padding(top = 12.dp)
                        .fillMaxWidth()
                ) {
                    Divider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Statistiques avancées :",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Matchs joués :", style = MaterialTheme.typography.bodyMedium)
                        Text("${player.matchesPlayed}", fontWeight = FontWeight.Bold)
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Buts marqués :", style = MaterialTheme.typography.bodyMedium)
                        Text("${player.goals}", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }
                    if (!player.description.isNullOrEmpty()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Profil : ${player.description}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontStyle = FontStyle.Italic,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                }
            }
        }
    }
}

// --- Matches Screen (Matchs) ---
@Composable
fun MatchesScreen(
    matches: List<Match>,
    onAddMatchResult: (Match, Int, Int) -> Unit,
    onDeleteMatch: (Int) -> Unit
) {
    var selectedSegment by remember { mutableStateOf(0) } // 0 = A venir, 1 = Résultats

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("matches_screen")
    ) {
        // Tab segment selection
        TabRow(
            selectedTabIndex = selectedSegment,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .padding(bottom = 16.dp)
        ) {
            Tab(
                selected = selectedSegment == 0,
                onClick = { selectedSegment = 0 },
                text = { Text("Matchs à venir", fontWeight = FontWeight.Bold) },
                modifier = Modifier.testTag("upcoming_matches_tab")
            )
            Tab(
                selected = selectedSegment == 1,
                onClick = { selectedSegment = 1 },
                text = { Text("Résultats", fontWeight = FontWeight.Bold) },
                modifier = Modifier.testTag("results_matches_tab")
            )
        }

        val filteredMatches = if (selectedSegment == 0) {
            matches.filter { !it.isFinished }
        } else {
            matches.filter { it.isFinished }
        }

        if (filteredMatches.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        if (selectedSegment == 0) Icons.Default.EventBusy else Icons.Default.SportsSoccer,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        if (selectedSegment == 0) "Aucun match prévu pour l'instant." else "Aucun résultat de match disponible.",
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(filteredMatches, key = { it.id }) { match ->
                    MatchCard(
                        match = match,
                        onAddResult = { our, opp -> onAddMatchResult(match, our, opp) },
                        onDelete = { onDeleteMatch(match.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun MatchCard(
    match: Match,
    onAddResult: (Int, Int) -> Unit,
    onDelete: () -> Unit
) {
    var showResultInputDialog by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("match_card_${match.opponent.lowercase().replace(" ", "_")}")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header: Date & Time
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.CalendarToday,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = match.date,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Icon(
                        Icons.Default.AccessTime,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = match.time,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Supprimer match",
                        tint = Color.Gray,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Scoreboard style row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Team 1: Club 24
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.img_club_logo),
                        contentDescription = "Club 24 logo",
                        modifier = Modifier.size(44.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("CLUB 24", fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }

                // Scores or VS Block
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.width(100.dp)
                ) {
                    if (match.isFinished && match.ourScore != null && match.opponentScore != null) {
                        Text(
                            text = match.ourScore.toString(),
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Black,
                            color = if (match.ourScore > match.opponentScore) GreenAccent else if (match.ourScore < match.opponentScore) MaterialTheme.colorScheme.primary else Color.Gray
                        )
                        Text(
                            text = " : ",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Gray
                        )
                        Text(
                            text = match.opponentScore.toString(),
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Black,
                            color = if (match.opponentScore > match.ourScore) GreenAccent else if (match.opponentScore < match.ourScore) MaterialTheme.colorScheme.primary else Color.Gray
                        )
                    } else {
                        Badge(
                            containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                            contentColor = MaterialTheme.colorScheme.primary
                        ) {
                            Text(
                                "VS",
                                fontWeight = FontWeight.Black,
                                fontSize = 14.sp,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                // Team 2: Opponent
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .background(Color.Gray.copy(alpha = 0.15f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.SportsSoccer, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(24.dp))
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(match.opponent, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Divider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))

            Spacer(modifier = Modifier.height(8.dp))

            // Footer: Match venue & status button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Place,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = match.location,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                if (!match.isFinished) {
                    Button(
                        onClick = { showResultInputDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                        modifier = Modifier.height(32.dp).testTag("add_result_btn_${match.opponent.lowercase()}")
                    ) {
                        Text("Enregistrer score", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        val outcomeText = when {
                            match.ourScore!! > match.opponentScore!! -> "Victoire 👑"
                            match.ourScore < match.opponentScore -> "Défaite"
                            else -> "Nul"
                        }
                        val outcomeColor = when {
                            match.ourScore > match.opponentScore -> GreenAccent
                            match.ourScore < match.opponentScore -> MaterialTheme.colorScheme.primary
                            else -> Color.Gray
                        }

                        Box(
                            modifier = Modifier
                                .background(outcomeColor.copy(alpha = 0.12f), RoundedCornerShape(4.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = outcomeText,
                                color = outcomeColor,
                                fontWeight = FontWeight.Black,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
        }
    }

    if (showResultInputDialog) {
        AddResultDialog(
            opponent = match.opponent,
            onDismiss = { showResultInputDialog = false },
            onConfirm = { our, opp ->
                onAddResult(our, opp)
                showResultInputDialog = false
            }
        )
    }
}

// --- Announcements Screen (Annonces) ---
@Composable
fun AnnouncementsScreen(
    announcements: List<Announcement>,
    onDeleteAnnouncement: (Int) -> Unit
) {
    var selectedTypeFilter by remember { mutableStateOf("Tous") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("announcements_screen")
    ) {
        // Horizontal filters
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("Tous", "Réunion", "Convocation", "Information").forEach { type ->
                FilterChip(
                    selected = selectedTypeFilter == type,
                    onClick = { selectedTypeFilter = type },
                    label = { Text(type) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = Color.White
                    ),
                    modifier = Modifier.testTag("announcement_filter_${type.lowercase()}")
                )
            }
        }

        val filteredAnnouncements = if (selectedTypeFilter == "Tous") {
            announcements
        } else {
            announcements.filter { it.type == selectedTypeFilter }
        }

        if (filteredAnnouncements.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Campaign, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(64.dp))
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Aucune annonce pour cette catégorie.", color = Color.Gray)
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(filteredAnnouncements, key = { it.id }) { announcement ->
                    AnnouncementCard(
                        announcement = announcement,
                        onDelete = { onDeleteAnnouncement(announcement.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun AnnouncementCard(announcement: Announcement, onDelete: () -> Unit) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("announcement_card_${announcement.id}")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Badge(
                    containerColor = when (announcement.type) {
                        "Réunion" -> Color(0xFF1976D2)
                        "Convocation" -> MaterialTheme.colorScheme.primary
                        else -> Color(0xFF388E3C)
                    },
                    contentColor = Color.White
                ) {
                    Text(
                        text = announcement.type.uppercase(),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                    Text(
                        text = sdf.format(Date(announcement.date)),
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Supprimer l'annonce",
                            tint = Color.Gray,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = announcement.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = announcement.content,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f),
                lineHeight = 20.sp
            )
        }
    }
}

// --- Gallery Screen (Galerie) ---
@Composable
fun GalleryScreen(
    photos: List<GalleryPhoto>,
    onPhotoClick: (GalleryPhoto) -> Unit,
    onDeletePhoto: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("gallery_screen")
    ) {
        Text(
            text = "📷 GALERIE PHOTOS & AFFICHES",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        if (photos.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text("Aucune photo dans la galerie.", color = Color.Gray)
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(150.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(photos, key = { it.id }) { photo ->
                    GalleryPhotoItem(
                        photo = photo,
                        onClick = { onPhotoClick(photo) },
                        onDelete = { onDeletePhoto(photo.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun GalleryPhotoItem(
    photo: GalleryPhoto,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clickable { onClick() }
            .testTag("gallery_item_${photo.id}")
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            val resourceId = if (photo.drawableName == "img_hero_banner") {
                R.drawable.img_hero_banner
            } else {
                R.drawable.img_club_logo
            }

            Image(
                painter = painterResource(id = resourceId),
                contentDescription = photo.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Dark gradient overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f)),
                            startY = 80f
                        )
                    )
            )

            // Info text overlay
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(8.dp)
            ) {
                Text(
                    text = photo.title,
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = photo.description,
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 10.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            IconButton(
                onClick = onDelete,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(4.dp)
                    .size(24.dp)
                    .background(Color.Black.copy(alpha = 0.5f), CircleShape)
            ) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = "Supprimer",
                    tint = Color.White,
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}

// --- Contact Screen (Contact) ---
@Composable
fun ContactScreen() {
    val context = LocalContext.current
    var userName by remember { mutableStateOf("") }
    var userMessage by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .testTag("contact_screen")
    ) {
        Text(
            text = "📞 NOUS CONTACTER",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // Info Cards
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "ADMINISTRATION DU CLUB",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(12.dp))

                // President details
                ContactPersonRow(
                    name = "Henri Pio",
                    role = "Président Fondateur",
                    phone = "+237695002400",
                    email = "henripio95@gmail.com",
                    onCall = { triggerCall(context, "+237695002400") },
                    onEmail = { triggerEmail(context, "henripio95@gmail.com", "Contact Club 24 Futsal") }
                )

                Spacer(modifier = Modifier.height(12.dp))
                Divider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
                Spacer(modifier = Modifier.height(12.dp))

                // Vice President details
                ContactPersonRow(
                    name = "Jean Marc Noah",
                    role = "Vice-Président",
                    phone = "+237677002400",
                    email = "jeannoah@example.com",
                    onCall = { triggerCall(context, "+237677002400") },
                    onEmail = { triggerEmail(context, "jeannoah@example.com", "Contact Club 24 Futsal") }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Message input box
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "ENVOYER UN MESSAGE RAPIDE",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = userName,
                    onValueChange = { userName = it },
                    label = { Text("Votre Nom") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        focusedLabelColor = MaterialTheme.colorScheme.primary
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = userMessage,
                    onValueChange = { userMessage = it },
                    label = { Text("Message") },
                    minLines = 3,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        focusedLabelColor = MaterialTheme.colorScheme.primary
                    )
                )

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = {
                        if (userName.isNotEmpty() && userMessage.isNotEmpty()) {
                            triggerEmail(
                                context = context,
                                email = "henripio95@gmail.com",
                                subject = "Message de $userName (Club 24 App)",
                                body = userMessage
                            )
                            userName = ""
                            userMessage = ""
                        } else {
                            Toast.makeText(context, "Veuillez remplir tous les champs !", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    modifier = Modifier.fillMaxWidth().testTag("send_contact_msg_btn")
                ) {
                    Text("Envoyer par Email")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Location Info
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Place, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("SIÈGE SOCIAL DU CLUB", fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Quartier Angale, Ebolowa, Cameroun\nSéance d'entraînement au Gymnase d'Ebolowa.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                )
            }
        }
    }
}

@Composable
fun ContactPersonRow(
    name: String,
    role: String,
    phone: String,
    email: String,
    onCall: () -> Unit,
    onEmail: () -> Unit
) {
    Column {
        Text(text = name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        Text(text = role, color = MaterialTheme.colorScheme.primary, fontSize = 12.sp, fontWeight = FontWeight.Medium)

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = "Tél: $phone", fontSize = 13.sp)
                Text(text = "Email: $email", fontSize = 13.sp, color = Color.Gray)
            }

            Row {
                IconButton(onClick = onCall) {
                    Icon(Icons.Default.Call, contentDescription = "Appeler", tint = GreenAccent)
                }
                IconButton(onClick = onEmail) {
                    Icon(Icons.Default.Email, contentDescription = "Envoyer un email", tint = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}

// --- Dialog Components ---

@Composable
fun AddPlayerDialog(onDismiss: () -> Unit, onConfirm: (String, Int, String, String) -> Unit) {
    var name by remember { mutableStateOf("") }
    var number by remember { mutableStateOf("") }
    var position by remember { mutableStateOf("Pivot") }
    var description by remember { mutableStateOf("") }
    val positions = listOf("Gardien", "Défenseur", "Ailier", "Pivot")
    var menuExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Ajouter un Joueur") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nom complet") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = number,
                    onValueChange = { number = it },
                    label = { Text("Numéro de maillot") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                // Position drop down list
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = position,
                        onValueChange = {},
                        label = { Text("Poste") },
                        readOnly = true,
                        modifier = Modifier.fillMaxWidth().clickable { menuExpanded = true },
                        trailingIcon = {
                            IconButton(onClick = { menuExpanded = true }) {
                                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                            }
                        }
                    )
                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false }
                    ) {
                        positions.forEach { pos ->
                            DropdownMenuItem(
                                text = { Text(pos) },
                                onClick = {
                                    position = pos
                                    menuExpanded = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description / Profil (Optionnel)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotEmpty() && number.isNotEmpty()) {
                        onConfirm(name, number.toIntOrNull() ?: 1, position, description)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Ajouter")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annuler")
            }
        }
    )
}

@Composable
fun AddMatchDialog(onDismiss: () -> Unit, onConfirm: (String, String, String, String) -> Unit) {
    var opponent by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Planifier un Match") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = opponent,
                    onValueChange = { opponent = it },
                    label = { Text("Adversaire") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = date,
                    onValueChange = { date = it },
                    label = { Text("Date (e.g. 20 Juillet 2026)") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = time,
                    onValueChange = { time = it },
                    label = { Text("Heure (e.g. 18:00)") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("Lieu (e.g. Gymnase d'Ebolowa)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (opponent.isNotEmpty() && date.isNotEmpty() && time.isNotEmpty() && location.isNotEmpty()) {
                        onConfirm(date, time, opponent, location)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Planifier")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annuler")
            }
        }
    )
}

@Composable
fun AddResultDialog(
    opponent: String,
    onDismiss: () -> Unit,
    onConfirm: (Int, Int) -> Unit
) {
    var ourScore by remember { mutableStateOf("") }
    var opponentScore by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Enregistrer le Résultat") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Match contre $opponent", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = ourScore,
                        onValueChange = { ourScore = it },
                        label = { Text("Club 24") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.width(80.dp),
                        textStyle = TextStyle(textAlign = TextAlign.Center)
                    )
                    Text(":", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    OutlinedTextField(
                        value = opponentScore,
                        onValueChange = { opponentScore = it },
                        label = { Text(opponent) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.width(80.dp),
                        textStyle = TextStyle(textAlign = TextAlign.Center)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val our = ourScore.toIntOrNull()
                    val opp = opponentScore.toIntOrNull()
                    if (our != null && opp != null) {
                        onConfirm(our, opp)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Sauvegarder")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annuler")
            }
        }
    )
}

@Composable
fun AddAnnouncementDialog(onDismiss: () -> Unit, onConfirm: (String, String, String) -> Unit) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("Information") }
    var menuExpanded by remember { mutableStateOf(false) }
    val types = listOf("Information", "Réunion", "Convocation")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nouvelle Annonce") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Titre de l'annonce") },
                    modifier = Modifier.fillMaxWidth()
                )

                // Type Dropdown selection
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = type,
                        onValueChange = {},
                        label = { Text("Catégorie") },
                        readOnly = true,
                        modifier = Modifier.fillMaxWidth().clickable { menuExpanded = true },
                        trailingIcon = {
                            IconButton(onClick = { menuExpanded = true }) {
                                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                            }
                        }
                    )
                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false }
                    ) {
                        types.forEach { t ->
                            DropdownMenuItem(
                                text = { Text(t) },
                                onClick = {
                                    type = t
                                    menuExpanded = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("Contenu") },
                    minLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotEmpty() && content.isNotEmpty()) {
                        onConfirm(title, content, type)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Publier")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annuler")
            }
        }
    )
}

@Composable
fun AddPhotoDialog(onDismiss: () -> Unit, onConfirm: (String, String, Boolean) -> Unit) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var isHeroBannerSample by remember { mutableStateOf(true) } // true for hero sample, false for club logo sample

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Ajouter une Photo") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Titre") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text("Sélectionnez l'image d'illustration :", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Card(
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isHeroBannerSample) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f) else Color.Transparent
                        ),
                        border = BorderStroke(
                            width = 2.dp,
                            color = if (isHeroBannerSample) MaterialTheme.colorScheme.primary else Color.Gray.copy(alpha = 0.3f)
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .clickable { isHeroBannerSample = true }
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(8.dp)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.img_hero_banner),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(60.dp)
                                    .clip(RoundedCornerShape(4.dp))
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Affiche Match", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Card(
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (!isHeroBannerSample) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f) else Color.Transparent
                        ),
                        border = BorderStroke(
                            width = 2.dp,
                            color = if (!isHeroBannerSample) MaterialTheme.colorScheme.primary else Color.Gray.copy(alpha = 0.3f)
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .clickable { isHeroBannerSample = false }
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(8.dp)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.img_club_logo),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(60.dp)
                                    .clip(RoundedCornerShape(4.dp))
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Logo Club", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotEmpty() && description.isNotEmpty()) {
                        onConfirm(title, description, isHeroBannerSample)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Ajouter")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annuler")
            }
        }
    )
}

@Composable
fun LightboxDialog(photo: GalleryPhoto, onDismiss: () -> Unit) {
    val resourceId = if (photo.drawableName == "img_hero_banner") {
        R.drawable.img_hero_banner
    } else {
        R.drawable.img_club_logo
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(photo.title, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Fermer")
                }
            }
        },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Card(
                    shape = RoundedCornerShape(8.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp)
                ) {
                    Image(
                        painter = painterResource(id = resourceId),
                        contentDescription = photo.title,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black)
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = photo.description,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Fermer")
            }
        }
    )
}

// --- Helper Functions ---

fun getTabIcon(tab: ClubTab): androidx.compose.ui.graphics.vector.ImageVector {
    return when (tab) {
        ClubTab.ACCUEIL -> Icons.Default.Home
        ClubTab.JOUEURS -> Icons.Default.People
        ClubTab.MATCHS -> Icons.Default.SportsSoccer
        ClubTab.ANNONCES -> Icons.Default.Campaign
        ClubTab.GALERIE -> Icons.Default.PhotoLibrary
        ClubTab.CONTACT -> Icons.Default.ContactMail
    }
}

fun getTabShortLabel(tab: ClubTab): String {
    return when (tab) {
        ClubTab.ACCUEIL -> "Accueil"
        ClubTab.JOUEURS -> "Joueurs"
        ClubTab.MATCHS -> "Matchs"
        ClubTab.ANNONCES -> "Annonces"
        ClubTab.GALERIE -> "Galerie"
        ClubTab.CONTACT -> "Contact"
    }
}

fun triggerCall(context: android.content.Context, phoneNumber: String) {
    try {
        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phoneNumber"))
        context.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(context, "Impossible de passer l'appel !", Toast.LENGTH_SHORT).show()
    }
}

fun triggerEmail(context: android.content.Context, email: String, subject: String, body: String = "") {
    try {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, body)
        }
        context.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(context, "Aucune application de messagerie trouvée !", Toast.LENGTH_SHORT).show()
    }
}
