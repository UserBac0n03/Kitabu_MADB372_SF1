package com.stadiolinks.kitabu.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/** All 8 school colours, same order as the catalog. */
private val stadioSchoolColours = listOf(
    SchoolInformationTechnology,
    SchoolAccountancy,
    SchoolAdministration,
    SchoolCommerce,
    SchoolEducation,
    SchoolFashion,
    SchoolLaw,
    SchoolPublicSafety
)

/**
 * Thin strip of the school colours under the top bar.
 * STADIO branding, and it matches the shelf colours.
 */
@Composable
fun StadioColourStrip(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(4.dp)
    ) {
        stadioSchoolColours.forEach { colour ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(colour)
            )
        }
    }
}

@Preview
@Composable
fun StadioColourStripPreview() {
    StadioColourStrip()
}
