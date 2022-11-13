package com.carepet.model

enum class SensorType(val type: String) {
    Temperature("T"), Pulse("P"), Location("L"), Respiration("R");

    companion object {
        fun byLetter(type: String) = SensorType.values().find { it.type == type }!!
    }
}