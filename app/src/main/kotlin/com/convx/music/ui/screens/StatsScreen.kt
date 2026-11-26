/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.ui.screens

import com.convx.music.ui.component.sf
import dev.chrisbanes.haze.hazeSource
import com.convx.music.ui.component.SymbolIcon
import com.convx.music.ui.utils.appTopBarWindowInsets
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.material3.ExperimentalMaterial3Api
import com.convx.music.ui.component.GlassCircleButton
import com.convx.music.ui.component.HeroBackground
import com.convx.music.ui.utils.rememberHeroZoom
import com.convx.music.ui.utils.heroPullZoom
import com.convx.music.ui.utils.listOverscroll
import com.convx.music.ui.component.rememberHeroSource
import com.convx.music.ui.component.rememberHeroTint
import com.convx.music.ui.theme.AppleTokens
import com.convx.music.ui.theme.HeroTintedContent
import com.convx.music.ui.component.LocalGlassEffectConfig
import com.convx.music.ui.component.isGlassAllowed
import com.convx.music.ui.component.liquidGlass
import com.convx.music.ui.component.shapes.ContinuousRoundedRectangle
import com.convx.music.ui.component.backdrop.backdrops.rememberLayerBackdrop
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.width
import androidx.compose.ui.Alignment
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import coil3.compose.AsyncImage
import com.convx.music.ui.ios.foundation.IosTheme
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import com.convx.music.ui.ios.compat.Icon
import com.convx.music.ui.ios.compat.IconButton
import com.convx.music.ui.ios.compat.AppTheme
import com.convx.music.ui.ios.compat.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.koin.androidx.compose.koinViewModel
import androidx.navigation.NavController
import com.music.innertube.models.WatchEndpoint
import com.convx.music.ui.utils.albumRoute
import com.convx.music.ui.utils.artistRoute
import com.convx.music.ui.utils.bounceClick
import com.convx.music.ui.utils.combinedBounceClick
import com.convx.music.LocalPlayerAwareWindowInsets
import com.convx.music.LocalPlayerConnection
import com.convx.music.R
import com.convx.music.constants.StatPeriod
import com.convx.music.extensions.toMediaItem
import com.convx.music.models.toMediaMetadata
import com.convx.music.playback.queues.ListQueue
import com.convx.music.playback.queues.YouTubeQueue
import com.convx.music.ui.component.ChoiceChipsRow
import com.convx.music.ui.component.FrostedTopBar
import com.convx.music.ui.component.HideOnScrollFAB
import com.convx.music.ui.component.LargeScreenTitle
import com.convx.music.ui.component.rememberTitleCollapseProgressState
import com.convx.music.ui.component.LocalAlbumsGrid
import com.convx.music.ui.component.LocalArtistsGrid
import com.convx.music.ui.component.LocalMenuState
import com.convx.music.ui.component.LocalSongsGrid
import com.convx.music.ui.component.NavigationTitle
import com.convx.music.ui.menu.AlbumMenu
import com.convx.music.ui.menu.ArtistMenu
import com.convx.music.ui.menu.SongMenu
import com.convx.music.ui.utils.backToMain
import com.convx.music.ui.utils.bleedStart
import com.convx.music.ui.utils.plusStart
import com.convx.music.utils.joinByBullet
import com.convx.music.utils.makeTimeString
import com.convx.music.viewmodels.StatsViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun StatsScreen(
    navController: NavController,
    viewModel: StatsViewModel = koinViewModel(),
) {
    val menuState = LocalMenuState.current
    val haptic = LocalHapticFeedback.current
    val playerConnection = LocalPlayerConnection.current ?: return
    val isPlaying by playerConnection.isEffectivelyPlaying.collectAsStateWithLifecycle()
    val mediaMetadata by playerConnection.mediaMetadata.collectAsStateWithLifecycle()
    val context = LocalContext.current

    val indexChips by viewModel.indexChips.collectAsStateWithLifecycle()
    val mostPlayedSongs by viewModel.mostPlayedSongs.collectAsStateWithLifecycle()
    val mostPlayedSongsStats by viewModel.mostPlayedSongsStats.collectAsStateWithLifecycle()
    val mostPlayedArtists by viewModel.mostPlayedArtists.collectAsStateWithLifecycle()
    val mostPlayedAlbums by viewModel.mostPlayedAlbums.collectAsStateWithLifecycle()
    val firstEvent by viewModel.firstEvent.collectAsStateWithLifecycle()
    val currentDate = LocalDateTime.now()

    val coroutineScope = rememberCoroutineScope()
    val lazyListState = rememberLazyListState()
    val statsTitleProgress = rememberTitleCollapseProgressState(lazyListState)
    val selectedOption by viewModel.selectedOption.collectAsStateWithLifecycle()

    val weeklyDates =
        if (currentDate != null && firstEvent != null) {
            generateSequence(currentDate) { it.minusWeeks(1) }
                .takeWhile { it.isAfter(firstEvent?.event?.timestamp?.minusWeeks(1)) }
                .mapIndexed { index, date ->
                    val endDate = date.plusWeeks(1).minusDays(1).coerceAtMost(currentDate)
                    val formatter = DateTimeFormatter.ofPattern("dd MMM")

                    val startDateFormatted = formatter.format(date)
                    val endDateFormatted = formatter.format(endDate)

                    val startMonth = date.month
                    val endMonth = endDate.month
                    val startYear = date.year
                    val endYear = endDate.year

                    val text =
                        when {
                            startYear != currentDate.year -> "$startDateFormatted, $startYear - $endDateFormatted, $endYear"
                            startMonth != endMonth -> "$startDateFormatted - $endDateFormatted"
                            else -> "${date.dayOfMonth} - $endDateFormatted"
                        }
                    Pair(index, text)
                }.toList()
        } else {
            emptyList()
        }

    val monthlyDates =
        if (currentDate != null && firstEvent != null) {
            generateSequence(
                currentDate.plusMonths(1).withDayOfMonth(1).minusDays(1)
            ) { it.minusMonths(1) }
                .takeWhile {
                    it.isAfter(
                        firstEvent
                            ?.event
                            ?.timestamp
                            ?.withDayOfMonth(1),
                    )
                }.mapIndexed { index, date ->
                    val formatter = DateTimeFormatter.ofPattern("MMM")
                    val formattedDate = formatter.format(date)
                    val text =
                        if (date.year != currentDate.year) {
                            "$formattedDate ${date.year}"
                        } else {
                            formattedDate
                        }
                    Pair(index, text)
                }.toList()
        } else {
            emptyList()
        }

    val yearlyDates =
        if (currentDate != null && firstEvent != null) {
            generateSequence(
                currentDate
                    .plusYears(1)
                    .withDayOfYear(1)
                    .minusDays(1),
            ) { it.minusYears(1) }
                .takeWhile {
                    it.isAfter(
                        firstEvent
                            ?.event
                            ?.timestamp,
                    )
                }.mapIndexed { index, date ->
                    Pair(index, "${date.year}")
                }.toList()
        } else {
            emptyList()
        }

    val tint = AppleTokens.BgElevated
    val onTint = AppleTokens.onColor(tint)
    val heroSource = rememberHeroSource(staticArt = null)
    val heroBackdrop = rememberLayerBackdrop()

    val heroZoom = rememberHeroZoom()

    val sideInset = LocalPlayerAwareWindowInsets.current
        .asPaddingValues()
        .calculateStartPadding(LocalLayoutDirection.current)

    HeroBackground(
        tint = tint,
        heroSource = heroSource,
        heroScale = heroZoom.scale,
        modifier = Modifier.fillMaxSize(),
    ) {
      HeroTintedContent(tint = tint, backdrop = heroBackdrop) {
        val chromeShape = ContinuousRoundedRectangle(percent = 50)
        val hazeState = androidx.compose.runtime.remember { dev.chrisbanes.haze.HazeState() }
        val totalTimeListenedMs = mostPlayedSongsStats.fold(0L) { acc, song -> acc + (song.timeListened ?: 0L) }
        val totalPlayCount = mostPlayedSongsStats.fold(0) { acc, song -> acc + song.songCountListened }
        val accentColor = AppTheme.colorScheme.primary
        val cardShape = ContinuousRoundedRectangle(22.dp)

        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                overscrollEffect = heroZoom.listOverscroll(),
                modifier = Modifier.heroPullZoom(heroZoom).hazeSource(hazeState),
                state = lazyListState,
                contentPadding = LocalPlayerAwareWindowInsets.current.asPaddingValues(),
            ) {
                item(key = "header") {
                    LargeScreenTitle(
                        title = stringResource(R.string.stats),
                        color = onTint,
                    )
                }

                item(key = "choice_chips") {
                    ChoiceChipsRow(
                        chips =
                        when (selectedOption) {
                            OptionStats.WEEKS -> weeklyDates
                            OptionStats.MONTHS -> monthlyDates
                            OptionStats.YEARS -> yearlyDates
                            OptionStats.CONTINUOUS -> {
                                listOf(
                                    StatPeriod.WEEK_1.ordinal to pluralStringResource(
                                        R.plurals.n_week,
                                        1,
                                        1
                                    ),
                                    StatPeriod.MONTH_1.ordinal to pluralStringResource(
                                        R.plurals.n_month,
                                        1,
                                        1
                                    ),
                                    StatPeriod.MONTH_3.ordinal to pluralStringResource(
                                        R.plurals.n_month,
                                        3,
                                        3
                                    ),
                                    StatPeriod.MONTH_6.ordinal to pluralStringResource(
                                        R.plurals.n_month,
                                        6,
                                        6
                                    ),
                                    StatPeriod.YEAR_1.ordinal to pluralStringResource(
                                        R.plurals.n_year,
                                        1,
                                        1
                                    ),
                                    StatPeriod.ALL.ordinal to stringResource(R.string.filter_all),
                                )
                            }
                        },
                        options =
                        listOf(
                            OptionStats.CONTINUOUS to stringResource(id = R.string.continuous),
                            OptionStats.WEEKS to stringResource(R.string.weeks),
                            OptionStats.MONTHS to stringResource(R.string.months),
                            OptionStats.YEARS to stringResource(R.string.years),
                        ),
                        selectedOption = selectedOption,
                        onSelectionChange = {
                            viewModel.selectedOption.value = it
                            viewModel.indexChips.value = 0
                        },
                        currentValue = indexChips,
                        onValueUpdate = { viewModel.indexChips.value = it },
                    )
                }

                if (mostPlayedSongsStats.isEmpty() && mostPlayedArtists.isEmpty() && mostPlayedAlbums.isEmpty()) {
                    item(key = "stats_empty") {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 32.dp)
                                .clip(cardShape)
                                .background(onTint.copy(alpha = 0.06f))
                                .padding(horizontal = 24.dp, vertical = 32.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                SymbolIcon(
                                    symbol = sf("chart.bar.fill"),
                                    contentDescription = null,
                                    size = 34.dp,
                                    tint = accentColor,
                                )
                                Text(
                                    text = "No listening stats yet",
                                    style = AppTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = onTint,
                                )
                                Text(
                                    text = "Listen to songs in MELORA to unlock play breakdowns, top tracks, artists, and listening time.",
                                    style = AppTheme.typography.bodyMedium,
                                    color = onTint.copy(alpha = 0.68f),
                                    textAlign = TextAlign.Center,
                                )
                            }
                        }
                    }
                } else {
                    item(key = "stats_overview") {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 10.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            // Hero summary card
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(cardShape)
                                    .background(
                                        Brush.linearGradient(
                                            colors = listOf(
                                                accentColor.copy(alpha = 0.24f),
                                                onTint.copy(alpha = 0.07f),
                                            )
                                        )
                                    )
                                    .padding(18.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp),
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Text(
                                        text = "LISTENING OVERVIEW",
                                        style = AppTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.sp,
                                        color = accentColor,
                                    )
                                    firstEvent?.event?.timestamp?.let { since ->
                                        val sinceLabel = since.format(DateTimeFormatter.ofPattern("MMM d, yyyy"))
                                        Text(
                                            text = "Since $sinceLabel",
                                            style = AppTheme.typography.labelSmall,
                                            color = onTint.copy(alpha = 0.65f),
                                        )
                                    }
                                }
                                Text(
                                    text = makeTimeString(totalTimeListenedMs),
                                    fontSize = 30.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = onTint,
                                )
                                Text(
                                    text = "$totalPlayCount total plays across ${mostPlayedSongsStats.size} tracks",
                                    style = AppTheme.typography.bodyMedium,
                                    color = onTint.copy(alpha = 0.72f),
                                )
                            }

                            // 3-metric quick breakdown row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                            ) {
                                StatMetricTile(
                                    label = "Plays",
                                    value = totalPlayCount.toString(),
                                    symbolName = "play.fill",
                                    accent = accentColor,
                                    textColor = onTint,
                                    modifier = Modifier.weight(1f),
                                )
                                StatMetricTile(
                                    label = stringResource(id = R.string.songs),
                                    value = mostPlayedSongsStats.size.toString(),
                                    symbolName = "music.note",
                                    accent = IosTheme.colors.systemPink,
                                    textColor = onTint,
                                    modifier = Modifier.weight(1f),
                                )
                                StatMetricTile(
                                    label = stringResource(id = R.string.artists),
                                    value = mostPlayedArtists.size.toString(),
                                    symbolName = "person.2.fill",
                                    accent = IosTheme.colors.systemTeal,
                                    textColor = onTint,
                                    modifier = Modifier.weight(1f),
                                )
                            }
                        }
                    }

                    // #1 Spotlight track
                    mostPlayedSongsStats.firstOrNull()?.let { topSong ->
                        item(key = "stats_spotlight") {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 4.dp)
                                    .clip(cardShape)
                                    .background(onTint.copy(alpha = 0.06f))
                                    .combinedBounceClick(
                                        onClick = {
                                            if (topSong.id == mediaMetadata?.id) {
                                                playerConnection.togglePlayPause()
                                            } else if (mostPlayedSongs.isNotEmpty()) {
                                                playerConnection.playQueue(
                                                    YouTubeQueue(
                                                        endpoint = WatchEndpoint(topSong.id),
                                                        preloadItem = mostPlayedSongs[0].toMediaMetadata(),
                                                    ),
                                                )
                                            }
                                        },
                                        onLongClick = {
                                            if (mostPlayedSongs.isNotEmpty()) {
                                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                                menuState.show {
                                                    SongMenu(
                                                        originalSong = mostPlayedSongs[0],
                                                        navController = navController,
                                                        onDismiss = menuState::dismiss,
                                                    )
                                                }
                                            }
                                        },
                                    )
                                    .padding(14.dp),
                            ) {
                                Text(
                                    text = "#1 ON REPEAT",
                                    style = AppTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.8.sp,
                                    color = accentColor,
                                )
                                Spacer(Modifier.height(10.dp))
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                                ) {
                                    AsyncImage(
                                        model = topSong.thumbnailUrl,
                                        contentDescription = topSong.title,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .size(64.dp)
                                            .clip(ContinuousRoundedRectangle(14.dp))
                                            .background(onTint.copy(alpha = 0.08f)),
                                    )
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = topSong.title,
                                            style = AppTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = onTint,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                        )
                                        Spacer(Modifier.height(2.dp))
                                        Text(
                                            text = joinByBullet(
                                                pluralStringResource(
                                                    R.plurals.n_time,
                                                    topSong.songCountListened,
                                                    topSong.songCountListened,
                                                ),
                                                makeTimeString(topSong.timeListened),
                                            ),
                                            style = AppTheme.typography.bodyMedium,
                                            color = onTint.copy(alpha = 0.7f),
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                        )
                                    }
                                    SymbolIcon(
                                        symbol = if (topSong.id == mediaMetadata?.id && isPlaying) sf("pause.circle.fill") else sf("play.circle.fill"),
                                        contentDescription = null,
                                        size = 32.dp,
                                        tint = accentColor,
                                    )
                                }
                            }
                        }
                    }

                    // Top Songs proportional bar breakdown
                    if (mostPlayedSongsStats.isNotEmpty()) {
                        val topRanked = mostPlayedSongsStats.take(8)
                        val maxListenMs = (topRanked.maxOfOrNull { it.timeListened ?: 0L } ?: 1L).coerceAtLeast(1L)
                        item(key = "top_breakdown_title") {
                            NavigationTitle(
                                title = "Top Tracks Breakdown",
                                modifier = Modifier,
                            )
                        }
                        item(key = "top_breakdown_card") {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp)
                                    .clip(cardShape)
                                    .background(onTint.copy(alpha = 0.055f))
                                    .padding(vertical = 8.dp),
                            ) {
                                topRanked.forEachIndexed { index, song ->
                                    val songListenMs = song.timeListened ?: 0L
                                    val fraction = (songListenMs.toFloat() / maxListenMs.toFloat()).coerceIn(0.06f, 1f)
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .combinedBounceClick(
                                                onClick = {
                                                    if (song.id == mediaMetadata?.id) {
                                                        playerConnection.togglePlayPause()
                                                    } else if (index < mostPlayedSongs.size) {
                                                        playerConnection.playQueue(
                                                            YouTubeQueue(
                                                                endpoint = WatchEndpoint(song.id),
                                                                preloadItem = mostPlayedSongs[index].toMediaMetadata(),
                                                            ),
                                                        )
                                                    }
                                                },
                                                onLongClick = {
                                                    if (index < mostPlayedSongs.size) {
                                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                                        menuState.show {
                                                            SongMenu(
                                                                originalSong = mostPlayedSongs[index],
                                                                navController = navController,
                                                                onDismiss = menuState::dismiss,
                                                            )
                                                        }
                                                    }
                                                },
                                            )
                                            .padding(horizontal = 14.dp, vertical = 9.dp),
                                        verticalArrangement = Arrangement.spacedBy(6.dp),
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                                        ) {
                                            Text(
                                                text = "${index + 1}",
                                                style = AppTheme.typography.labelLarge,
                                                fontWeight = FontWeight.Bold,
                                                color = if (index < 3) accentColor else onTint.copy(alpha = 0.55f),
                                                modifier = Modifier.width(22.dp),
                                                textAlign = TextAlign.Center,
                                            )
                                            AsyncImage(
                                                model = song.thumbnailUrl,
                                                contentDescription = song.title,
                                                contentScale = ContentScale.Crop,
                                                modifier = Modifier
                                                    .size(40.dp)
                                                    .clip(ContinuousRoundedRectangle(9.dp))
                                                    .background(onTint.copy(alpha = 0.08f)),
                                            )
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                    text = song.title,
                                                    style = AppTheme.typography.bodyLarge,
                                                    fontWeight = FontWeight.SemiBold,
                                                    color = onTint,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis,
                                                )
                                                Text(
                                                    text = joinByBullet(
                                                        pluralStringResource(
                                                            R.plurals.n_time,
                                                            song.songCountListened,
                                                            song.songCountListened,
                                                        ),
                                                        makeTimeString(song.timeListened),
                                                    ),
                                                    style = AppTheme.typography.bodySmall,
                                                    color = onTint.copy(alpha = 0.65f),
                                                    maxLines = 1,
                                                )
                                            }
                                        }
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(start = 32.dp)
                                                .height(5.dp)
                                                .clip(CircleShape)
                                                .background(onTint.copy(alpha = 0.08f)),
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth(fraction)
                                                    .height(5.dp)
                                                    .clip(CircleShape)
                                                    .background(
                                                        if (index == 0) accentColor
                                                        else accentColor.copy(alpha = 0.65f)
                                                    ),
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                if (mostPlayedSongs.isNotEmpty()) {
                    item(key = "mostPlayedSongs") {
                        NavigationTitle(
                            title = "${mostPlayedSongsStats.size} ${stringResource(id = R.string.songs)}",
                            modifier = Modifier,
                        )
                    }

                    item(key = "mostPlayedSongsList") {
                        LazyRow(
                            contentPadding = WindowInsets.systemBars
                                .only(WindowInsetsSides.Horizontal)
                                .asPaddingValues().plusStart(sideInset),
                            modifier = Modifier.bleedStart(sideInset),
                        ) {
                            itemsIndexed(
                                items = mostPlayedSongsStats,
                                key = { _, song -> song.id },
                            ) { index, song ->
                                LocalSongsGrid(
                                    title = "${index + 1}. ${song.title}",
                                    subtitle =
                                    joinByBullet(
                                        pluralStringResource(
                                            R.plurals.n_time,
                                            song.songCountListened,
                                            song.songCountListened,
                                        ),
                                        makeTimeString(song.timeListened),
                                    ),
                                    thumbnailUrl = song.thumbnailUrl,
                                    isActive = song.id == mediaMetadata?.id,
                                    isPlaying = isPlaying,
                                    modifier =
                                    Modifier
                                        .fillMaxWidth()
                                        .combinedBounceClick(
                                            onClick = {
                                                if (song.id == mediaMetadata?.id) {
                                                    playerConnection.togglePlayPause()
                                                } else {
                                                    playerConnection.playQueue(
                                                        YouTubeQueue(
                                                            endpoint = WatchEndpoint(song.id),
                                                            preloadItem = mostPlayedSongs[index].toMediaMetadata(),
                                                        ),
                                                    )
                                                }
                                            },
                                            onLongClick = {
                                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                                menuState.show {
                                                    SongMenu(
                                                        originalSong = mostPlayedSongs[index],
                                                        navController = navController,
                                                        onDismiss = menuState::dismiss,
                                                    )
                                                }
                                            },
)
                                )
                            }
                        }
                    }
                }

                if (mostPlayedArtists.isNotEmpty()) {
                    item(key = "mostPlayedArtistsTitle") {
                        NavigationTitle(
                            title = "${mostPlayedArtists.size} ${stringResource(id = R.string.artists)}",
                            modifier = Modifier,
                        )
                    }

                    item(key = "mostPlayedArtistsList") {
                        LazyRow(
                            contentPadding = WindowInsets.systemBars
                                .only(WindowInsetsSides.Horizontal)
                                .asPaddingValues().plusStart(sideInset),
                            modifier = Modifier.bleedStart(sideInset),
                        ) {
                            itemsIndexed(
                                items = mostPlayedArtists,
                                key = { _, artist -> artist.id },
                            ) { index, artist ->
                                LocalArtistsGrid(
                                    title = "${index + 1}. ${artist.artist.name}",
                                    subtitle =
                                    joinByBullet(
                                        pluralStringResource(
                                            R.plurals.n_time,
                                            artist.songCount,
                                            artist.songCount
                                        ),
                                        makeTimeString(artist.timeListened?.toLong()),
                                    ),
                                    thumbnailUrl = artist.artist.thumbnailUrl,
                                    modifier =
                                    Modifier
                                        .combinedBounceClick(
                                            onClick = {
                                                navController.navigate(artistRoute(artist.id, artist.artist.thumbnailUrl))
                                            },
                                            onLongClick = {
                                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                                menuState.show {
                                                    ArtistMenu(
                                                        originalArtist = artist,
                                                        coroutineScope = coroutineScope,
                                                        onDismiss = menuState::dismiss,
                                                    )
                                                }
                                            },
)
                                )
                            }
                        }
                    }
                }

                if (mostPlayedAlbums.isNotEmpty()) {
                    item(key = "mostPlayedAlbumsTitle") {
                        NavigationTitle(
                            title = "${mostPlayedAlbums.size} ${stringResource(id = R.string.albums)}",
                            modifier = Modifier,
                        )
                    }

                    item(key = "mostPlayedAlbumsList") {
                        LazyRow(
                            contentPadding = WindowInsets.systemBars
                                .only(WindowInsetsSides.Horizontal)
                                .asPaddingValues().plusStart(sideInset),
                            modifier = Modifier.bleedStart(sideInset),
                        ) {
                            itemsIndexed(
                                items = mostPlayedAlbums,
                                key = { _, album -> album.id },
                            ) { index, album ->
                                val playCount = album.songCountListened ?: 0
                                LocalAlbumsGrid(
                                    title = "${index + 1}. ${album.album.title}",
                                    subtitle =
                                    joinByBullet(
                                        pluralStringResource(
                                            R.plurals.n_time,
                                            playCount,
                                            playCount,
                                        ),
                                        makeTimeString(album.timeListened),
                                    ),
                                    thumbnailUrl = album.album.thumbnailUrl,
                                    sharedArtworkId = album.id,
                                    isActive = album.id == mediaMetadata?.album?.id,
                                    isPlaying = isPlaying,
                                    modifier =
                                    Modifier
                                        .fillMaxWidth()
                                        .combinedBounceClick(
                                            onClick = {
                                                navController.navigate(albumRoute(album.id, album.album.thumbnailUrl))
                                            },
                                            onLongClick = {
                                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                                menuState.show {
                                                    AlbumMenu(
                                                        originalAlbum = album,
                                                        navController = navController,
                                                        onDismiss = menuState::dismiss,
                                                    )
                                                }
                                            },
)
                                )
                            }
                        }
                    }
                }
            }

            if (mostPlayedSongs.isNotEmpty()) {
                HideOnScrollFAB(
                    visible = true,
                    lazyListState = lazyListState,
                    icon = R.drawable.shuffle,
                    onClick = {
                        playerConnection.playQueue(
                            ListQueue(
                                title = context.getString(R.string.most_played_songs),
                                items = mostPlayedSongs.map { it.toMediaMetadata().toMediaItem() }.shuffled()
                            )
                        )
                    }
                )
            }

            com.convx.music.ui.component.TopFadeBlur(
                hazeState = hazeState,
                pageColor = tint,
                modifier = Modifier.align(Alignment.TopCenter),
            )

            FrostedTopBar(
                title = stringResource(R.string.stats),
                titleAlpha = 0f,
                titleAlphaProvider = { statsTitleProgress.value },
                titleColor = onTint,
                modifier = Modifier.align(Alignment.TopCenter),
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .windowInsetsPadding(appTopBarWindowInsets())
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                GlassCircleButton(
                    onClick = navController::navigateUp,
                    onLongClick = navController::backToMain,
                ) {
                    SymbolIcon(symbol = sf("arrow.left"), contentDescription = null)
                }

                Spacer(Modifier.weight(1f))

                GlassCircleButton(
                    onClick = { navController.navigate("wrapped") }
                ) {
                    Icon(
                        painter = painterResource(R.drawable.sparks),
                        contentDescription = null,
                    )
                }
            }
        }
      }
    }
}

@Composable
private fun StatMetricTile(
    label: String,
    value: String,
    symbolName: String,
    accent: Color,
    textColor: Color,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .clip(ContinuousRoundedRectangle(18.dp))
            .background(textColor.copy(alpha = 0.06f))
            .padding(horizontal = 12.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        SymbolIcon(symbol = sf(symbolName), contentDescription = null, size = 16.dp, tint = accent)
        Text(
            text = value,
            style = AppTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = textColor,
            maxLines = 1,
        )
        Text(
            text = label,
            style = AppTheme.typography.labelSmall,
            color = textColor.copy(alpha = 0.65f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

enum class OptionStats { WEEKS, MONTHS, YEARS, CONTINUOUS }
