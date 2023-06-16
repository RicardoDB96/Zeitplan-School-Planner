package com.domberdev.zeitplan.subjects.model

data class Subject(val subjectTitle:String = "Default subject tittle",
                   val subjectAcronym:String = "DST",
                   val teacher:String = "Default teacher tittle",
                   val place:Any = "Default place",
                   val subjectID:String = "A",
                   val color:String = "-1",
                   val secondaryColor:String = "0",
                   val grade:String = "0",
                   val goal:String = "",
                   val generalGrade:Boolean = true)