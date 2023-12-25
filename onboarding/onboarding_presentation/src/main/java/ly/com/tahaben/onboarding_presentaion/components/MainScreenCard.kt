package ly.com.tahaben.onboarding_presentaion.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ly.com.tahaben.core_ui.LocalSpacing

@Composable
fun MainScreenCard(
    modifier: Modifier = Modifier,
    text: String,
    @DrawableRes iconId: Int?,
    status: String,
    onClick: () -> Unit,
    mainSwitchEnabled: Boolean,
    showSnackBar: () -> Unit
) {
    val spacing = LocalSpacing.current
    Card(
        modifier = Modifier
            .height(66.dp)
            .fillMaxWidth()
            .clip(
                RoundedCornerShape(10.dp)
            )
            .clickable {
                if (mainSwitchEnabled) {
                    onClick()
                } else {
                    showSnackBar()
                }
            },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = spacing.spaceMedium),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.fillMaxHeight(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (iconId != null) {
                    Image(
                        modifier = Modifier,
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onBackground),
                        painter = painterResource(id = iconId),
                        contentDescription = text
                    )
                    Spacer(modifier = Modifier.width(spacing.spaceSmall))
                }
                Text(
                    modifier = Modifier,
                    text = text,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold,
                )
            }
            Text(
                modifier = Modifier,
                text = status,
                style = MaterialTheme.typography.headlineMedium,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(
                    alpha = 0.4f
                ),
                fontWeight = FontWeight.Normal
            )
        }
    }
}