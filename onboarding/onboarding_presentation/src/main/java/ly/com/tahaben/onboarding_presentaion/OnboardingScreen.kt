package ly.com.tahaben.onboarding_presentaion

//import ly.com.tahaben.core_ui.DarkYellow
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch
import ly.com.tahaben.core.R
import ly.com.tahaben.core.util.UiEvent
import ly.com.tahaben.core_ui.LocalSpacing
import ly.com.tahaben.core_ui.theme.BottomCardShape
import ly.com.tahaben.domain.model.OnBoardingData

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnBoardingScreen(
    onFinishOnBoarding: () -> Unit,
    viewModel: OnBoardingViewModel = hiltViewModel()
) {

    val items = ArrayList<OnBoardingData>()

    LaunchedEffect(key1 = true) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is UiEvent.Success -> onFinishOnBoarding()
                else -> Unit
            }
        }
    }

    items.add(
        OnBoardingData(
            R.drawable.ic_baseline_wifi_off_24,
            stringResource(R.string.no_internet_permission),
            stringResource(R.string.no_internet_permission_desc)
        )
    )

    items.add(
        OnBoardingData(
            R.drawable.ic_baseline_lock_open_24,
            stringResource(R.string.open_source),
            stringResource(R.string.open_source_desc)
        )
    )

    items.add(
        OnBoardingData(
            R.drawable.farhan_transparent_bg,
            stringResource(R.string.use_farhan_be_farhan),
            stringResource(R.string.use_farhan_be_farhan_desc)
        )
    )


    val pagerState = rememberPagerState(
        initialPage = 0,
        initialPageOffsetFraction = 0f,
        pageCount = {
            items.size
        }
    )


    OnBoardingPager(
        item = items, pagerState = pagerState, modifier = Modifier
            .fillMaxWidth(),
        onFinishClick = viewModel::setOnBoardingShown

    )

}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnBoardingPager(
    item: List<OnBoardingData>,
    pagerState: PagerState,
    modifier: Modifier = Modifier,
    onFinishClick: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val spacing = LocalSpacing.current
    Column(
        modifier = Modifier.fillMaxSize()

    ) {
        Box(
            modifier = modifier
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                HorizontalPager(
                    modifier = Modifier,
                    state = pagerState,
                    pageSpacing = 0.dp,
                    userScrollEnabled = true,
                    reverseLayout = false,
                    contentPadding = PaddingValues(0.dp),
                    beyondBoundsPageCount = 0,
                    pageSize = PageSize.Fill,
                    flingBehavior = PagerDefaults.flingBehavior(state = pagerState),
                    key = null,
                    pageNestedScrollConnection = PagerDefaults.pageNestedScrollConnection(
                        state = pagerState, orientation = Orientation.Horizontal
                    ),
                    pageContent = { page ->
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    if (page == 2) {
                                        MaterialTheme.colorScheme.inversePrimary
                                    } else {
                                        MaterialTheme.colorScheme.background
                                    }
                                ),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Top
                        ) {

                            Image(
                                painter = painterResource(id = item[page].image),
                                contentDescription = item[page].title,
                                modifier = Modifier
                                    .padding(spacing.spaceMedium)
                                    .fillMaxWidth()
                                    .aspectRatio(2f)
                            )
                        }
                    }
                )

            }

            Box(modifier = Modifier.align(Alignment.BottomCenter)) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(380.dp)
                        .padding(top = spacing.spaceExtraLarge),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                    shape = BottomCardShape.large
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            PagerIndicator(items = item, currentPage = pagerState.currentPage)
                            Text(
                                text = item[pagerState.currentPage].title,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = spacing.spaceMedium),
                                color = MaterialTheme.colorScheme.onSurface,
                                style = MaterialTheme.typography.displaySmall,
                                textAlign = TextAlign.Center,
                                fontWeight = FontWeight.ExtraBold
                            )

                            Text(
                                text = item[pagerState.currentPage].desc,
                                modifier = Modifier.padding(
                                    top = spacing.spaceMedium,
                                    start = spacing.spaceMedium,
                                    end = spacing.spaceMedium
                                ),
                                color = MaterialTheme.colorScheme.onSurface,
                                style = MaterialTheme.typography.headlineMedium,
                                textAlign = TextAlign.Center,
                                fontWeight = FontWeight.Normal
                            )

                        }
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(spacing.spaceExtraLarge)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                if (pagerState.currentPage != 2) {
                                    TextButton(
                                        onClick =
                                        onFinishClick
                                    ) {
                                        Text(
                                            text = stringResource(R.string.skip_now),
                                            color = MaterialTheme.colorScheme.onSurface,
                                            textAlign = TextAlign.Right,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }

                                    Button(
                                        onClick = {
                                            coroutineScope.launch {
                                                pagerState.scrollToPage(
                                                    pagerState.currentPage + 1,
                                                    pagerState.currentPageOffsetFraction
                                                )
                                            }
                                        },
                                    ) {
                                        Text(
                                            text = stringResource(id = R.string.next),
                                            color = Color.White,
                                            style = MaterialTheme.typography.labelLarge
                                        )
                                    }

                                } else {
                                    Button(
                                        onClick = onFinishClick,
                                        modifier = Modifier.fillMaxWidth(),
                                        contentPadding = PaddingValues(vertical = spacing.spaceSmall)
                                    ) {
                                        Text(
                                            text = stringResource(R.string.get_started),
                                            color = Color.White,
                                            fontSize = 16.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

            }
        }
    }

}

@Composable
fun PagerIndicator(currentPage: Int, items: List<OnBoardingData>) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.padding(top = 20.dp)
    ) {
        repeat(items.size) {
            Indicator(isSelected = it == currentPage)
        }
    }
}

@Composable
fun Indicator(isSelected: Boolean) {
    val width = animateDpAsState(
        targetValue = if (isSelected) 40.dp else 10.dp,
        label = "Onboarding dot anim"
    )

    Box(
        modifier = Modifier
            .padding(4.dp)
            .height(10.dp)
            .width(width.value)
            .clip(CircleShape)
            .background(
                if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray.copy(alpha = 0.5f)
            )
    )
}
