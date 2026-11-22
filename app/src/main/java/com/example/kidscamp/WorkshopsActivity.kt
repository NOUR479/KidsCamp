
package com.example.kidscamp

import android.content.Intent
import android.app.AlertDialog
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.kidscamp.data.KidsCampData
import com.example.kidscamp.model.Child
import com.example.kidscamp.model.Registration
import com.example.kidscamp.model.Workshop
import java.util.Locale

class WorkshopsActivity : AppCompatActivity() {

    private lateinit var searchInput: EditText
    private lateinit var workshopsContainer: LinearLayout
    private lateinit var countText: TextView
    private lateinit var emptyText: TextView

    private var selectedCategory = "Tous"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workshops)

        searchInput = findViewById(R.id.etSearchWorkshop)
        workshopsContainer = findViewById(R.id.workshopsContainer)
        countText = findViewById(R.id.tvWorkshopCount)
        emptyText = findViewById(R.id.tvNoWorkshops)

        findViewById<Button>(R.id.btnBack).setOnClickListener {
            finish()
        }

        findViewById<Button>(R.id.btnFilterAll).setOnClickListener {
            selectCategory("Tous")
        }

        findViewById<Button>(R.id.btnFilterArt).setOnClickListener {
            selectCategory("Créativité")
        }

        findViewById<Button>(R.id.btnFilterSport).setOnClickListener {
            selectCategory("Sport")
        }

        findViewById<Button>(R.id.btnFilterScience).setOnClickListener {
            selectCategory("Sciences")
        }

        searchInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?, start: Int, count: Int, after: Int
            ) {}

            override fun onTextChanged(
                s: CharSequence?, start: Int, before: Int, count: Int
            ) {
                displayWorkshops()
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        displayWorkshops()
    }

    override fun onResume() {
        super.onResume()
        if (::workshopsContainer.isInitialized) {
            displayWorkshops()
        }
    }

    private fun selectCategory(category: String) {
        selectedCategory = category
        displayWorkshops()
    }

    private fun displayWorkshops() {
        val query = searchInput.text.toString().trim()

        val filtered = KidsCampData.workshops.filter { workshop ->
            val matchesCategory =
                selectedCategory == "Tous" ||
                        workshop.category == selectedCategory

            val matchesSearch =
                workshop.title.contains(query, ignoreCase = true) ||
                        workshop.category.contains(query, ignoreCase = true) ||
                        workshop.day.contains(query, ignoreCase = true)

            matchesCategory && matchesSearch
        }

        workshopsContainer.removeAllViews()
        countText.text = "${filtered.size} activité(s) disponible(s)"
        emptyText.visibility =
            if (filtered.isEmpty()) View.VISIBLE else View.GONE

        filtered.forEach { workshop ->
            workshopsContainer.addView(createWorkshopCard(workshop))
        }
    }

    private fun createWorkshopCard(workshop: Workshop): View {
        val enrolledCount = KidsCampData.registrations.count {
            it.workshopId == workshop.id
        }

        val remainingPlaces =
            (workshop.capacity - enrolledCount).coerceAtLeast(0)

        val card = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(18), dp(16), dp(18), dp(16))
            background = getDrawable(R.drawable.bg_kidscamp_card)
            isClickable = true
            isFocusable = true
        }

        val category = TextView(this).apply {
            text = workshop.category.uppercase()
            textSize = 12f
            setTextColor(Color.parseColor("#E61171"))
            setTypeface(null, Typeface.BOLD)
        }

        val title = TextView(this).apply {
            text = workshop.title
            textSize = 19f
            setTextColor(Color.parseColor("#302522"))
            setTypeface(null, Typeface.BOLD)
            setPadding(0, dp(8), 0, dp(8))
        }

        val schedule = TextView(this).apply {
            text = "📅 ${workshop.day} • ${workshop.startHour}:00 - ${workshop.endHour}:00"
            textSize = 14f
            setTextColor(Color.parseColor("#796A64"))
        }

        val age = TextView(this).apply {
            text = "👧 Âge : ${workshop.minAge} à ${workshop.maxAge} ans"
            textSize = 14f
            setTextColor(Color.parseColor("#796A64"))
            setPadding(0, dp(6), 0, 0)
        }

        val places = TextView(this).apply {
            text = if (remainingPlaces > 0) {
                "🎟️ $remainingPlaces place(s) restante(s)"
            } else {
                "❌ Atelier complet"
            }
            textSize = 14f
            setTextColor(
                if (remainingPlaces > 0) Color.parseColor("#367C5B")
                else Color.parseColor("#C62828")
            )
            setPadding(0, dp(8), 0, 0)
        }

        val price = TextView(this).apply {
            text = String.format(Locale.FRANCE, "%.2f DT", workshop.price)
            textSize = 18f
            setTextColor(Color.parseColor("#E61171"))
            setTypeface(null, Typeface.BOLD)
            setPadding(0, dp(12), 0, 0)
        }

        val action = TextView(this).apply {
            text = "Appuyer pour gérer les inscriptions →"
            textSize = 13f
            setTextColor(Color.parseColor("#796A64"))
            setPadding(0, dp(10), 0, 0)
        }

        card.addView(category)
        card.addView(title)
        card.addView(schedule)
        card.addView(age)
        card.addView(places)
        card.addView(price)
        card.addView(action)

        card.setOnClickListener {
            val intent = Intent(this, WorkshopDetailActivity::class.java)
            intent.putExtra("WORKSHOP_ID", workshop.id)
            startActivity(intent)
        }

        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        params.bottomMargin = dp(14)
        card.layoutParams = params

        return card
    }

    private fun showWorkshopActions(workshop: Workshop) {
        val options = arrayOf(
            "Inscrire un enfant",
            "Annuler une inscription",
            "Fermer"
        )

        AlertDialog.Builder(this)
            .setTitle("🎨 ${workshop.title}")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> chooseChildForRegistration(workshop)
                    1 -> chooseChildForCancellation(workshop)
                }
            }
            .show()
    }

    private fun chooseChildForRegistration(workshop: Workshop) {
        if (KidsCampData.children.isEmpty()) {
            Toast.makeText(
                this,
                "Ajoute d'abord un enfant dans « Les petits campeurs ».",
                Toast.LENGTH_LONG
            ).show()
            return
        }

        val enrolledCount = KidsCampData.registrations.count {
            it.workshopId == workshop.id
        }

        if (enrolledCount >= workshop.capacity) {
            Toast.makeText(
                this,
                "Désolé, cet atelier est complet.",
                Toast.LENGTH_LONG
            ).show()
            return
        }

        val children = KidsCampData.children.toList()
        val labels = children.map { "${it.firstName} (${it.age} ans)" }
            .toTypedArray()

        AlertDialog.Builder(this)
            .setTitle("Choisir un enfant")
            .setItems(labels) { _, which ->
                registerChild(children[which], workshop)
            }
            .setNegativeButton("Annuler", null)
            .show()
    }

    private fun registerChild(child: Child, workshop: Workshop) {
        if (child.age !in workshop.minAge..workshop.maxAge) {
            Toast.makeText(
                this,
                "Âge incompatible : cet atelier accepte les ${workshop.minAge}–${workshop.maxAge} ans.",
                Toast.LENGTH_LONG
            ).show()
            return
        }

        val alreadyRegistered = KidsCampData.registrations.any {
            it.childId == child.id && it.workshopId == workshop.id
        }

        if (alreadyRegistered) {
            Toast.makeText(
                this,
                "${child.firstName} est déjà inscrit(e) à cet atelier.",
                Toast.LENGTH_LONG
            ).show()
            return
        }

        val enrolledCount = KidsCampData.registrations.count {
            it.workshopId == workshop.id
        }

        if (enrolledCount >= workshop.capacity) {
            Toast.makeText(
                this,
                "Cet atelier est complet.",
                Toast.LENGTH_LONG
            ).show()
            displayWorkshops()
            return
        }

        val hasConflict = KidsCampData.registrations.any { registration ->
            if (registration.childId != child.id) {
                false
            } else {
                val otherWorkshop = KidsCampData.workshops.find {
                    it.id == registration.workshopId
                }

                otherWorkshop != null &&
                        otherWorkshop.day == workshop.day &&
                        workshop.startHour < otherWorkshop.endHour &&
                        otherWorkshop.startHour < workshop.endHour
            }
        }

        if (hasConflict) {
            Toast.makeText(
                this,
                "Conflit d'horaire : ${child.firstName} est déjà inscrit(e) à un atelier à cette heure.",
                Toast.LENGTH_LONG
            ).show()
            return
        }

        KidsCampData.registrations.add(
            Registration(child.id, workshop.id)
        )
        KidsCampData.save(this)

        Toast.makeText(
            this,
            "✅ ${child.firstName} est inscrit(e) à ${workshop.title} !",
            Toast.LENGTH_LONG
        ).show()

        displayWorkshops()
    }

    private fun chooseChildForCancellation(workshop: Workshop) {
        val registeredIds = KidsCampData.registrations
            .filter { it.workshopId == workshop.id }
            .map { it.childId }
            .toSet()

        val registeredChildren = KidsCampData.children
            .filter { it.id in registeredIds }

        if (registeredChildren.isEmpty()) {
            Toast.makeText(
                this,
                "Aucune inscription à annuler pour cet atelier.",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val labels = registeredChildren.map {
            "${it.firstName} (${it.age} ans)"
        }.toTypedArray()

        AlertDialog.Builder(this)
            .setTitle("Annuler une inscription")
            .setItems(labels) { _, which ->
                val child = registeredChildren[which]

                KidsCampData.registrations.removeAll {
                    it.childId == child.id &&
                            it.workshopId == workshop.id
                }
                KidsCampData.save(this)

                Toast.makeText(
                    this,
                    "Inscription de ${child.firstName} annulée.",
                    Toast.LENGTH_SHORT
                ).show()

                displayWorkshops()
            }
            .setNegativeButton("Retour", null)
            .show()
    }

    private fun dp(value: Int): Int {
        return (value * resources.displayMetrics.density).toInt()
    }
}
