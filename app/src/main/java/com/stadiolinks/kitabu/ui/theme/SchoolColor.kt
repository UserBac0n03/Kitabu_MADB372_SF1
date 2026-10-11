package com.stadiolinks.kitabu.ui.theme

import androidx.compose.ui.graphics.Color
import com.stadiolinks.kitabu.domain.Schools

/** The colour that represents a school in the catalog. */
fun schoolColor(category: String): Color = when (category) {
    Schools.INFORMATION_TECHNOLOGY -> SchoolInformationTechnology
    Schools.ACCOUNTANCY -> SchoolAccountancy
    Schools.ADMINISTRATION -> SchoolAdministration
    Schools.COMMERCE -> SchoolCommerce
    Schools.EDUCATION -> SchoolEducation
    Schools.FASHION -> SchoolFashion
    Schools.LAW -> SchoolLaw
    Schools.PUBLIC_SAFETY -> SchoolPublicSafety
    else -> Color.Gray
}