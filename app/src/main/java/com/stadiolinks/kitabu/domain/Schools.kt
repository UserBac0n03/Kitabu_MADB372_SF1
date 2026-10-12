package com.stadiolinks.kitabu.domain

/**
 * The STADIO schools a book can belong to. A book's category is one of these names.
 * Keeping them here means the spelling is typed once and reused everywhere.
 */
object Schools {
    const val INFORMATION_TECHNOLOGY = "Information Technology"
    const val ACCOUNTANCY = "Accountancy"
    const val ADMINISTRATION = "Administration and Management"
    const val COMMERCE = "Commerce"
    const val EDUCATION = "Education"
    const val FASHION = "Fashion"
    const val LAW = "Law"
    const val PUBLIC_SAFETY = "Policing and Law Enforcement"

    /** Short names for the jump chips, the full ones don't fit. */
    fun shortName(school: String): String = when (school) {
        INFORMATION_TECHNOLOGY -> "IT"
        ADMINISTRATION -> "Admin"
        PUBLIC_SAFETY -> "Policing"
        else -> school
    }

    /** Every school, in the order the catalog lists them. */
    val all = listOf(
        INFORMATION_TECHNOLOGY,
        ACCOUNTANCY,
        ADMINISTRATION,
        COMMERCE,
        EDUCATION,
        FASHION,
        LAW,
        PUBLIC_SAFETY
    )
}
