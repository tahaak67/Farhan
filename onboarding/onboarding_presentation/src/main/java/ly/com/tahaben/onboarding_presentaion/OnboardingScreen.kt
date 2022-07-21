package ly.com.tahaben.onboarding_presentaion

import androidx.annotation.FloatRange
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
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
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import kotlinx.coroutines.launch
import ly.com.tahaben.core.R
import ly.com.tahaben.core.util.UiEvent
import ly.com.tahaben.core_ui.Black
import ly.com.tahaben.core_ui.DarkYellow
import ly.com.tahaben.core_ui.LocalSpacing
import ly.com.tahaben.core_ui.White
import ly.com.tahaben.core_ui.theme.BottomCardShape
import ly.com.tahaben.domain.model.OnBoardingData

@OptIn(ExperimentalPagerApi::class)
@Composable
fun OnBoardingScreen(
    onNavigateToMain: () -> Unit,
    viewModel: OnBoardingViewModel = hiltViewModel()
) {

    val items = ArrayList<OnBoardingData>()

    LaunchedEffect(key1 = true) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is UiEvent.Success -> onNavigateToMain()
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
            R.drawable.farhan_icon,
            stringResource(R.string.use_farhan_be_farhan),
            stringResource(R.string.use_farhan_be_farhan_desc)
        )
    )


    val pagerState = rememberPagerState(
        pageCount = items.size,
        initialOffscreenLimit = 2,
        infiniteLoop = false,
        initialPage = 0,
    )


    OnBoardingPager(
        item = items, pagerState = pagerState, modifier = Modifier
            .fillMaxWidth(),
        onFinishClick = viewModel::setOnBoardingShown

    )

}

@OptIn(ExperimentalPagerApi::class)
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
                HorizontalPager(state = pagerState) { page ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                if (page == 2) {
                                    DarkYellow
                                } else {
                                    White
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

            }

            Box(modifier = Modifier.align(Alignment.BottomCenter)) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(380.dp)
                        .padding(top = spacing.spaceLarge),
                    backgroundColor = MaterialTheme.colors.secondary,
                    shape = BottomCardShape.large
                ) {
                    Box() {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            PagerIndicator(items = item, currentPage = pagerState.currentPage)
                            Text(
                                text = item[pagerState.currentPage].title,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = spacing.spaceMedium),
                                color = Black,
                                style = MaterialTheme.typography.h3,
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
                                color = Black,
                                style = MaterialTheme.typography.h4,
                                textAlign = TextAlign.Center,
                                fontWeight = FontWeight.Normal
                            )

                        }
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(spacing.spaceLarge)
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
                                            color = Black,
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
                                                    pageOffset = 0f
                                                )
                                            }
                                        },
                                    ) {
                                        Text(
                                            text = stringResource(id = R.string.next),
                                            color = Color.White,
                                            style = MaterialTheme.typography.button
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
    val width = animateDpAsState(targetValue = if (isSelected) 40.dp else 10.dp)

    Box(
        modifier = Modifier
            .padding(4.dp)
            .height(10.dp)
            .width(width.value)
            .clip(CircleShape)
            .background(
                if (isSelected) DarkYellow else Color.Gray.copy(alpha = 0.5f)
            )
    )
}

@ExperimentalPagerApi
@Composable
fun rememberPagerState(
    @androidx.annotation.IntRange(from = 0) pageCount: Int,
    @androidx.annotation.IntRange(from = 0) initialPage: Int = 0,
    @FloatRange(from = 0.0, to = 1.0) initialPageOffset: Float = 0f,
    @androidx.annotation.IntRange(from = 1) initialOffscreenLimit: Int = 1,
    infiniteLoop: Boolean = false
): PagerState = rememberSaveable(saver = PagerState.Saver) {
    PagerState(
        pageCount = pageCount,
        currentPage = initialPage,
        currentPageOffset = initialPageOffset,
        offscreenLimit = initialOffscreenLimit,
        infiniteLoop = infiniteLoop
    )
}
