package com.example.studentlifeapp.data

import org.threeten.bp.LocalDateTime
//import org.threeten.bp.YearMonth

//TODO: Import data from current date onwards. Don't bother with data from before

fun importEvents():MutableList<Event> {
    val list = mutableListOf<Event>()
//    val currentMonth = YearMonth.now()

    list.add(Event("Reminder1", EventType.REMINDER, LocalDateTime.now()))
    list.add(Event("Class1", EventType.CLASS, LocalDateTime.now().minusHours(2),
        LocalDateTime.now(), location = Location("Class Room")))
    list.add(Event("Lecture1", EventType.LECTURE, LocalDateTime.now(), LocalDateTime.now().plusHours(1),
        location = Location("Lecture Hall")))
    list.add(Event("Job Shift", EventType.JOBSHIFT, LocalDateTime.now().plusMinutes(90),
        LocalDateTime.now().plusHours(5), location = Location("Work Place")))
    list.add(Event("Event1", EventType.EVENT, LocalDateTime.now().plusDays(4), location = Location("Somewhere")))
    list.add(Event("Reminder2", EventType.REMINDER,LocalDateTime.now().plusHours(12)))
    list.add(Event("Reminder2", EventType.CLASS,LocalDateTime.now().plusHours(12), location = Location("class")))
    list.add(Event("reminder3",EventType.REMINDER, LocalDateTime.now().plusDays(12)))
    list.add(Event("reminder3",EventType.EXAM, LocalDateTime.now().plusDays(12)))
    list.add(Event("reminder3",EventType.SOCIETY, LocalDateTime.now().plusDays(12)))
    list.add(Event("Study",EventType.STUDY, LocalDateTime.now().plusDays(13),LocalDateTime.now().plusDays(13).plusHours(1),note = "revise sectionB"))
    //TODO: Get events from database
    //TODO: Import/export by date
    return list
}

fun importSubjects():MutableList<Subject>{
    val list = mutableListOf<Subject>()
    val events1 = mutableListOf<Event>()
    val events2 = mutableListOf<Event>()
    events1.add(Event("Class1", EventType.CLASS, LocalDateTime.now().minusHours(2), LocalDateTime.now(),
        location = Location("Class Room")))
    events1.add(Event("Lecture1", EventType.LECTURE, LocalDateTime.now(), LocalDateTime.now().plusHours(1),
        location = Location("Lecture Hall")))
    events2.add(Event("Class1", EventType.CLASS, LocalDateTime.now().plusHours(12), LocalDateTime.now().plusHours(13),
        location = Location("Class Room")))
    events2.add(Event("Lecture1", EventType.LECTURE, LocalDateTime.now().plusHours(3), LocalDateTime.now().plusHours(5),
        location = Location("Lecture Hall")))
    events2.add(Event("Class2", EventType.CLASS, LocalDateTime.now().plusHours(22), LocalDateTime.now().plusHours(23),
        location = Location("Class Room")))
    events2.add(Event("Lecture2", EventType.LECTURE, LocalDateTime.now().plusHours(13), LocalDateTime.now().plusHours(15),
        location = Location("Lecture Hall")))
    events2.add(Event("Class3", EventType.CLASS, LocalDateTime.now().plusHours(32), LocalDateTime.now().plusHours(33),
        location = Location("Class Room")))
    events2.add(Event("Lecture3", EventType.LECTURE, LocalDateTime.now().plusHours(23), LocalDateTime.now().plusHours(25),
        location = Location("Lecture Hall")))
    events2.add(Event("Class1", EventType.CLASS, LocalDateTime.now().plusHours(42), LocalDateTime.now().plusHours(43),
        location = Location("Class Room")))
    events2.add(Event("Lecture2", EventType.LECTURE, LocalDateTime.now().plusHours(33), LocalDateTime.now().plusHours(35),
        location = Location("Lecture Hall")))
    events2.add(Event("Class1", EventType.CLASS, LocalDateTime.now().plusHours(52), LocalDateTime.now().plusHours(53),
        location = Location("Class Room")))
    events2.add(Event("Lecture2", EventType.LECTURE, LocalDateTime.now().plusHours(23), LocalDateTime.now().plusHours(35),
        location = Location("Lecture Hall")))
    list.add(Subject("Subject1","An exampleSubject", events1))
    list.add(Subject("Subject2","Second example Subject", events2))



    return list
}

