
package com.example.kidscamp.data

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import com.example.kidscamp.model.Child
import com.example.kidscamp.model.Registration
import com.example.kidscamp.model.Workshop

object KidsCampData {

    val children = mutableListOf<Child>()

    val workshops = mutableListOf(
        Workshop("W1", "Atelier peinture", "Créativité", "Lundi", 9, 11, 4, 10, 12, 15.0),
        Workshop("W2", "Mini-foot", "Sport", "Lundi", 11, 13, 6, 12, 16, 12.0),
        Workshop("W3", "Robotique junior", "Sciences", "Mardi", 9, 11, 8, 14, 10, 20.0),
        Workshop("W4", "Création artistique", "Créativité", "Mercredi", 14, 16, 5, 12, 12, 18.0),
        Workshop("W5", "Jeux sportifs", "Sport", "Jeudi", 10, 12, 4, 12, 15, 10.0),
        Workshop("W6", "Petits scientifiques", "Sciences", "Vendredi", 14, 16, 7, 14, 10, 22.0)
    )

    val registrations = mutableListOf<Registration>()

    private const val PREFS_NAME = "kidscamp_storage"
    private const val KEY_CHILDREN = "children"
    private const val KEY_REGISTRATIONS = "registrations"
    private const val KEY_INITIALIZED = "initialized"

    fun save(context: Context) {
        val prefs = context.getSharedPreferences(
            PREFS_NAME,
            Context.MODE_PRIVATE
        )

        val childrenJson = JSONArray()

        children.forEach { child ->
            val item = JSONObject()
            item.put("id", child.id)
            item.put("firstName", child.firstName)
            item.put("age", child.age)
            childrenJson.put(item)
        }

        val registrationsJson = JSONArray()

        registrations.forEach { registration ->
            val item = JSONObject()
            item.put("childId", registration.childId)
            item.put("workshopId", registration.workshopId)
            registrationsJson.put(item)
        }

        prefs.edit()
            .putString(KEY_CHILDREN, childrenJson.toString())
            .putString(KEY_REGISTRATIONS, registrationsJson.toString())
            .putBoolean(KEY_INITIALIZED, true)
            .apply()
    }

    fun load(context: Context) {
        val prefs = context.getSharedPreferences(
            PREFS_NAME,
            Context.MODE_PRIVATE
        )

        if (!prefs.getBoolean(KEY_INITIALIZED, false)) {
            return
        }

        children.clear()
        registrations.clear()

        val childrenString = prefs.getString(KEY_CHILDREN, "[]") ?: "[]"
        val childrenJson = JSONArray(childrenString)

        for (i in 0 until childrenJson.length()) {
            val item = childrenJson.getJSONObject(i)

            children.add(
                Child(
                    id = item.getString("id"),
                    firstName = item.getString("firstName"),
                    age = item.getInt("age")
                )
            )
        }

        val registrationsString =
            prefs.getString(KEY_REGISTRATIONS, "[]") ?: "[]"
        val registrationsJson = JSONArray(registrationsString)

        for (i in 0 until registrationsJson.length()) {
            val item = registrationsJson.getJSONObject(i)

            val childId = item.getString("childId")
            val workshopId = item.getString("workshopId")

            // Ignore les inscriptions dont l'enfant ou l'atelier n'existe plus.
            val childExists = children.any { it.id == childId }
            val workshopExists = workshops.any { it.id == workshopId }

            if (childExists && workshopExists) {
                registrations.add(
                    Registration(
                        childId = childId,
                        workshopId = workshopId
                    )
                )
            }
        }
    }
}
