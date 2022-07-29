package ly.com.tahaben.onboarding_presentaion.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ly.com.tahaben.core_ui.Black
import ly.com.tahaben.core_ui.LocalSpacing

@Composable
fun MainScreenCard(
    modifier: Modifier = Modifier,
    text: String,
    @DrawableRes iconId: Int?,
    status: String,
    onClick: () -> Unit
) {
    val spacing = LocalSpacing.current
    Card(
        modifier = Modifier
            .height(150.dp)
            .width(150.dp)
            .clip(
                RoundedCornerShape(20)
            )
            .clickable {
                onClick()
            },
        backgroundColor = MaterialTheme.colors.secondary
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                if (iconId != null) {
                    Image(
                        modifier = Modifier.padding(start = 15.dp, top = 15.dp),
                        painter = painterResource(id = iconId),
                        contentDescription = text
                    )
                    Spacer(modifier = Modifier.width(spacing.spaceExtraSmall))
                }
                Text(
                    modifier = Modifier.padding(
                        horizontal = if (iconId == null) 15.dp else 8.dp,
                        vertical = 15.dp
                    ),
                    text = text,
                    style = MaterialTheme.typography.h4,
                    fontSize = 15.sp,
                    color = Black,
                    fontWeight = FontWeight.Bold,
                )
            }
            Spacer(modifier = Modifier.height(spacing.spaceExtraSmall))
            Text(
                modifier = Modifier.padding(horizontal = 15.dp),
                text = status,
                style = MaterialTheme.typography.h4,
                fontSize = 16.sp,
                color = Black,
                fontWeight = FontWeight.Normal
            )
        }


    }
}