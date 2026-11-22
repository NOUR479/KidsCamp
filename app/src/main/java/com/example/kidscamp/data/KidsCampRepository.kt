package com.example.kidscamp.data

import com.example.kidscamp.model.Child
import com.example.kidscamp.model.Workshop
import com.example.kidscamp.model.Registration

class KidsCampRepository {

    val children = mutableListOf<Child>()
    val workshops = mutableListOf<Workshop>()
    val registrations = mutableListOf<Registration>()

    fun registerChild(
        child: Child,
        workshop: Workshop
    ): String? {
        if (child.firstName.isBlank()) {
            return "Le prénom est obligatoire."
        }

        if (child.age !in workshop.minAge..workshop.maxAge) {
            return "Âge incompatible avec cet atelier."
        }

        if (registrations.any {
                it.childId == child.id &&
                        it.workshopId == workshop.id
            }) {
            return "Cet enfant est déjà inscrit."
        }

        val enrolled = registrations.count {
            it.workshopId == workshop.id
        }

        if (enrolled >= workshop.capacity) {
            return "Cet atelier est complet."
        }

        val conflict = registrations.any { registration ->
            if (registration.childId != child.id) {
                false
            } else {
                val other = workshops.find {
                    it.id == registration.workshopId
                }

                other != null &&
                        other.day == workshop.day &&
                        workshop.startHour < other.endHour &&
                        other.startHour < workshop.endHour
            }
        }

        if (conflict) {
            return "Conflit d'horaire."
        }

        registrations.add(
            Registration(child.id, workshop.id)
        )

        return null
    }
}