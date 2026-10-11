package com.stadiolinks.kitabu.data.local

import com.stadiolinks.kitabu.data.local.entity.BookEntity
import com.stadiolinks.kitabu.domain.Schools

/**
 * Books copied into the database the first time it is created.
 * Each one is filed under the STADIO school most likely to prescribe it.
 */
object SampleBooks {
    val all = listOf(
        // Information Technology
        BookEntity(title = "Clean Code", author = "Robert C. Martin", category = Schools.INFORMATION_TECHNOLOGY),
        BookEntity(title = "Kotlin in Action", author = "Dmitry Jemerov", category = Schools.INFORMATION_TECHNOLOGY),
        BookEntity(title = "Database System Concepts", author = "Abraham Silberschatz", category = Schools.INFORMATION_TECHNOLOGY),
        BookEntity(title = "Computer Networking: A Top-Down Approach", author = "James F. Kurose", category = Schools.INFORMATION_TECHNOLOGY),
        BookEntity(title = "Introduction to Algorithms", author = "Thomas H. Cormen", category = Schools.INFORMATION_TECHNOLOGY),

        // Accountancy
        BookEntity(title = "Financial Accounting", author = "Jerry J. Weygandt", category = Schools.ACCOUNTANCY),
        BookEntity(title = "Intermediate Accounting", author = "Donald E. Kieso", category = Schools.ACCOUNTANCY),
        BookEntity(title = "Managerial Accounting", author = "Ray H. Garrison", category = Schools.ACCOUNTANCY),
        BookEntity(title = "Auditing and Assurance Services", author = "Alvin A. Arens", category = Schools.ACCOUNTANCY),

        // Administration and Management
        BookEntity(title = "The Practice of Management", author = "Peter F. Drucker", category = Schools.ADMINISTRATION),
        BookEntity(title = "Good to Great", author = "Jim Collins", category = Schools.ADMINISTRATION),
        BookEntity(title = "Organizational Behavior", author = "Stephen P. Robbins", category = Schools.ADMINISTRATION),
        BookEntity(title = "The Lean Startup", author = "Eric Ries", category = Schools.ADMINISTRATION),

        // Commerce
        BookEntity(title = "Principles of Marketing", author = "Philip Kotler", category = Schools.COMMERCE),
        BookEntity(title = "Principles of Economics", author = "N. Gregory Mankiw", category = Schools.COMMERCE),
        BookEntity(title = "Principles of Corporate Finance", author = "Richard A. Brealey", category = Schools.COMMERCE),
        BookEntity(title = "Thinking, Fast and Slow", author = "Daniel Kahneman", category = Schools.COMMERCE),

        // Education
        BookEntity(title = "Pedagogy of the Oppressed", author = "Paulo Freire", category = Schools.EDUCATION),
        BookEntity(title = "Mindset", author = "Carol S. Dweck", category = Schools.EDUCATION),
        BookEntity(title = "Visible Learning", author = "John Hattie", category = Schools.EDUCATION),
        BookEntity(title = "How Children Learn", author = "John Holt", category = Schools.EDUCATION),

        // Fashion
        BookEntity(title = "Patternmaking for Fashion Design", author = "Helen Joseph-Armstrong", category = Schools.FASHION),
        BookEntity(title = "Fashion Design", author = "Sue Jenkyn Jones", category = Schools.FASHION),
        BookEntity(title = "The Fashion System", author = "Roland Barthes", category = Schools.FASHION),
        BookEntity(title = "The End of Fashion", author = "Teri Agins", category = Schools.FASHION),

        // Law
        BookEntity(title = "The Concept of Law", author = "H. L. A. Hart", category = Schools.LAW),
        BookEntity(title = "Law's Empire", author = "Ronald Dworkin", category = Schools.LAW),
        BookEntity(title = "The Rule of Law", author = "Tom Bingham", category = Schools.LAW),
        BookEntity(title = "Letters to a Law Student", author = "Nicholas J. McBride", category = Schools.LAW),

        // Policing and Law Enforcement
        BookEntity(title = "Criminology", author = "Larry J. Siegel", category = Schools.PUBLIC_SAFETY),
        BookEntity(title = "Criminal Investigation", author = "Charles R. Swanson", category = Schools.PUBLIC_SAFETY),
        BookEntity(title = "Forensic Science: From the Crime Scene to the Crime Lab", author = "Richard Saferstein", category = Schools.PUBLIC_SAFETY),
        BookEntity(title = "Discipline and Punish", author = "Michel Foucault", category = Schools.PUBLIC_SAFETY)
    )
}
