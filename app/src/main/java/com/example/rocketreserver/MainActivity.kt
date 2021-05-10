package com.example.rocketreserver

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Dimension
import com.apollographql.apollo.coroutines.await
import com.apollographql.apollo.coroutines.toFlow
import com.google.accompanist.coil.rememberCoilPainter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            RocketReserverApp()
        }
    }
}

@Composable
fun RocketReserverApp() {
    MaterialTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(text = "RocketReserver") },
                )
            }, content = {
                LaunchListContent()
            }
        )
    }
}

sealed class UiState {
    object Loading : UiState()
    object Error : UiState()
    class Success(val launchList: List<LaunchListQuery.Launch>) : UiState()
}

@Composable
fun LaunchListContent() {
    val context = LocalContext.current
    val state = remember {
        apolloClient(context).query(LaunchListQuery()).watcher().toFlow()
            .map {
                val launchList = it
                    .data
                    ?.launchConnection
                    ?.launches
                    ?.filterNotNull()
                if (launchList == null) {
                    // There were some error
                    // TODO: do something with response.errors
                    UiState.Error
                } else {
                    UiState.Success(launchList)
                }
            }
            .catch { e ->
                emit(UiState.Error)
            }
    }.collectAsState(initial = UiState.Loading)

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        when (val value = state.value) {
            is UiState.Loading -> Loading()
            is UiState.Error -> Error()
            is UiState.Success -> LaunchList(launchList = value.launchList)
        }
    }
}

@Composable
fun Loading() {
    CircularProgressIndicator()
}

@Composable
fun Error() {
    Text(
        text = "Oops something went wrong"
    )
}

@Preview
@Composable
fun ErrorPreview() {
    Error()
}


@Composable
fun LaunchList(launchList: List<LaunchListQuery.Launch>) {
    LazyColumn {
        items(launchList) { launch ->
            LaunchItem(
                modifier = Modifier.fillMaxWidth(),
                launch = launch
            )
        }
    }
}

@Composable
fun LaunchItem(launch: LaunchListQuery.Launch, modifier: Modifier = Modifier) {
    val context = LocalContext.current

    val bookTrip = {
        // TODO: use something better than GlobalScope
        GlobalScope.launch(Dispatchers.Main) {
            if (!launch.isBooked) {
                try {
                    apolloClient(context).mutate(
                        BookTripMutation(launch.id),
                        BookTripMutation.Data(
                            bookTrips = BookTripMutation.BookTrips(
                                launches = listOf(
                                    BookTripMutation.Launch(
                                        id = launch.id,
                                        isBooked = true
                                    )
                                )
                            )
                        )
                    ).await()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        Unit
    }

    ConstraintLayout(
        modifier = modifier,
        constraintSet = ConstraintSet {
            val image = createRefFor("image")
            val divider = createRefFor("divider")

            val missionName = createRefFor("missionName")
            val site = createRefFor("site")
            val button = createRefFor("button")

            constrain(image) {
                start.linkTo(parent.start, 16.dp)
                top.linkTo(parent.top, 16.dp)
                bottom.linkTo(parent.bottom, 16.dp)
                width = Dimension.value(80.dp)
                height = Dimension.value(80.dp)
            }

            constrain(divider) {
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                bottom.linkTo(parent.bottom)
                width = Dimension.fillToConstraints
                height = Dimension.value(1.dp)
            }

            constrain(missionName) {
                start.linkTo(image.end, 16.dp)
                end.linkTo(button.start, 8.dp)
                width = Dimension.fillToConstraints
                height = Dimension.wrapContent
                top.linkTo(image.top)
                bottom.linkTo(site.top)
            }

            constrain(site) {
                start.linkTo(image.end, 16.dp)
                end.linkTo(button.start, 8.dp)
                width = Dimension.fillToConstraints
                height = Dimension.wrapContent
                top.linkTo(missionName.bottom)
                bottom.linkTo(image.bottom)
            }

            constrain(button) {
                end.linkTo(parent.end, 8.dp)
                width = Dimension.wrapContent
                height = Dimension.wrapContent
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
            }
        }) {
        Image(
            modifier = Modifier.layoutId("image"),
            painter = rememberCoilPainter(
                request = launch.mission!!.missionPatch ?: R.drawable.ic_placeholder,
                previewPlaceholder =R.drawable.ic_placeholder,
            ),
            contentDescription = null
        )
        Divider(
            modifier = Modifier.layoutId("divider"),
            color = MaterialTheme.colors.onSurface.copy(alpha = 0.05f)
        )
        Text(
            modifier = Modifier.layoutId("missionName"),
            fontWeight = FontWeight.Bold,
            text = launch.mission.name!!,
        )
        Text(
            modifier = Modifier.layoutId("site"),
            text = launch.site!!,
        )
        Button(
            modifier = Modifier.layoutId("button"),
            enabled = launch.isBooked.not(),
            onClick = bookTrip
        ) {
            Text(if (launch.isBooked) "BOOKED" else "BOOK")
        }
    }
}


@Preview
@Composable
fun LaunchListPreview() {
    val list = 0.until(20).map {
        LaunchListQuery.Launch(
            id = it.toString(),
            site = "site $it",
            mission = LaunchListQuery.Mission(
                missionPatch = "https://raw.githubusercontent.com/apollographql/apollo-client/master/docs/source/logo/square.png",
                name = "mission $it"
            ),
            isBooked = false

        )
    }
    LaunchList(list)
}
