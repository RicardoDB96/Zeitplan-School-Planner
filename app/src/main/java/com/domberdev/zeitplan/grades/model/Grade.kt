package com.domberdev.zeitplan.grades.model

data class Grade(
    val gradeTitle: String,
    val grade: Float,
    val gradeDay: String,
    val gradeWeight: Int,
    val gradeID: String,
    val subjectID: String
)
