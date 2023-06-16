package com.domberdev.zeitplan.agenda.model

data class Task(
    val taskTitle:String = "Default title task",
    val timeLimit: String= "23:59",
    val finishedOn:String = "Monday, January 1, 1970",
    val day:String = "11",
    val category:String = "Default category",
    val taskID:String = "Default task ID",
    val subjectID:String = "Default subject ID",
    val taskDescription:String = "Default task description",
    val finished: Boolean = false,
    val subjectTitle: String? = "Subject",
    val subjectAcronym: String = "S",
    val subjectColor: Int = 0,
    val subjectSecondaryColor: Int = 1)