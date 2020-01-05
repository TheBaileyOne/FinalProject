package com.example.studentlifeapp.data

import kotlinx.android.parcel.RawValue
import java.io.Serializable

class Subject(val name: String, val summary: String, val events:@RawValue MutableList<Event>) : Serializable