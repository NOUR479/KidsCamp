
package com.example.kidscamp

import android.app.Activity
import android.app.AlertDialog
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import com.example.kidscamp.data.KidsCampData
import com.example.kidscamp.model.Child
import com.example.kidscamp.model.Workshop
import com.example.kidscamp.model.Registration
import java.util.Locale

class WorkshopDetailActivity : Activity() {

    private fun dp(value: Int): Int =
        (value * resources.displayMetrics.density).toInt()

    private fun createBackground(
        color: Int,
        radius: Int
    ): GradientDrawable {
        return GradientDrawable().apply {
            setColor(color)
            cornerRadius = dp(radius).toFloat()
        }
    }

    private fun addText(
        parent: LinearLayout,
        text: String,
        size: Float,
        color: Int,
        bold: Boolean = false
    ) {
        val tv = TextView(this).apply {
            this.text = text
            textSize = size
            setTextColor(color)
            if (bold) setTypeface(null, Typeface.BOLD)
            setPadding(dp(4), dp(8), dp(4), dp(8))
        }
        parent.addView(tv)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val workshopId = intent.getStringExtra("WORKSHOP_ID")

        val workshop = KidsCampData.workshops.find {
            it.id == workshopId
        }

        if (workshop == null) {
            Toast.makeText(
                this,
                "Atelier introuvable.",
                Toast.LENGTH_SHORT
            ).show()
            finish()
            return
        }

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.rgb(255, 248, 244))
            setPadding(dp(20), dp(16), dp(20), dp(20))
        }

        val scrollView = ScrollView(this)

        val content = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }

        val backButton = Button(this).apply {
            text = "← Retour"
            isAllCaps = false
            setTextColor(Color.WHITE)
            background = createBackground(
                Color.rgb(108, 99, 217), 16
            )
            setOnClickListener { finish() }
        }

        content.addView(
            backButton,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                dp(48)
            ).apply {
                bottomMargin = dp(20)
            }
        )

        val card = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(20), dp(20), dp(20), dp(20))
            background = createBackground(Color.WHITE, 20)
        }

        addText(
            card,
            "DÉTAILS DE L'ATELIER",
            14f,
            Color.rgb(128, 100, 216),
            true
        )

        addText(
            card,
            workshop.title,
            26f,
            Color.rgb(65, 49, 105),
            true
        )

        addText(
            card,
            "Catégorie : ${workshop.category}",
            17f,
            Color.DKGRAY
        )

        addText(
            card,
            "Jour : ${workshop.day}",
            17f,
            Color.DKGRAY
        )

        addText(
            card,
            "Horaire : ${workshop.startHour}h - ${workshop.endHour}h",
            17f,
            Color.DKGRAY
        )

        addText(
            card,
            "Âge autorisé : ${workshop.minAge} à ${workshop.maxAge} ans",
            17f,
            Color.DKGRAY
        )

        addText(
            card,
            "Capacité : ${workshop.capacity} enfants",
            17f,
            Color.DKGRAY
        )

        addText(
            card,
            "Enfants inscrits : ${
                KidsCampData.registrations.count {
                    it.workshopId == workshop.id
                }
            } / ${workshop.capacity}",
            17f,
            Color.rgb(108, 99, 217),
            true
        )

        addText(
            card,
            "Prix : ${
                String.format(Locale.getDefault(), "%.2f", workshop.price)
            } DT",
            22f,
            Color.rgb(230, 17, 113),
            true
        )

        content.addView(
            card,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        )

        // Bouton : inscrire un enfant
        val registerButton = Button(this).apply {
            text = "Inscrire un enfant"
            isAllCaps = false
            textSize = 16f
            setTextColor(Color.WHITE)
            background = createBackground(
                Color.rgb(128, 100, 216), 16
            )

            setOnClickListener {
                showChildrenToRegister(workshop)
            }
        }

        content.addView(
            registerButton,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(54)
            ).apply {
                topMargin = dp(20)
            }
        )

        // Bouton : annuler une inscription
        val cancelButton = Button(this).apply {
            text = "Annuler une inscription"
            isAllCaps = false
            textSize = 16f
            setTextColor(Color.WHITE)
            background = createBackground(
                Color.rgb(255, 123, 114), 16
            )

            setOnClickListener {
                showRegisteredChildren(workshop)
            }
        }

        content.addView(
            cancelButton,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(54)
            ).apply {
                topMargin = dp(10)
            }
        )

        scrollView.addView(content)

        root.addView(
            scrollView,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1f
            )
        )

        setContentView(root)
    }

    // Afficher les enfants qui ne sont pas encore inscrits
    private fun showChildrenToRegister(workshop: Workshop) {

        if (KidsCampData.children.isEmpty()) {
            Toast.makeText(
                this,
                "Ajoutez d'abord un enfant.",
                Toast.LENGTH_LONG
            ).show()
            return
        }

        val alreadyRegisteredIds =
            KidsCampData.registrations
                .filter { it.workshopId == workshop.id }
                .map { it.childId }
                .toSet()

        val availableChildren = KidsCampData.children.filter {
            it.id !in alreadyRegisteredIds
        }

        if (availableChildren.isEmpty()) {
            Toast.makeText(
                this,
                "Tous les enfants sont déjà inscrits à cet atelier.",
                Toast.LENGTH_LONG
            ).show()
            return
        }

        AlertDialog.Builder(this)
            .setTitle("Choisir un enfant")
            .setItems(
                availableChildren.map {
                    "${it.firstName} — ${it.age} ans"
                }.toTypedArray()
            ) { _, which ->
                registerChild(availableChildren[which], workshop)
            }
            .setNegativeButton("Fermer", null)
            .show()
    }

    // Vérifier et enregistrer l'inscription
    private fun registerChild(
        child: Child,
        workshop: Workshop
    ) {

        if (child.age !in workshop.minAge..workshop.maxAge) {
            Toast.makeText(
                this,
                "Âge non autorisé pour cet atelier.",
                Toast.LENGTH_LONG
            ).show()
            return
        }

        if (KidsCampData.registrations.any {
                it.childId == child.id &&
                        it.workshopId == workshop.id
            }) {
            Toast.makeText(
                this,
                "Cet enfant est déjà inscrit.",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val registeredCount =
            KidsCampData.registrations.count {
                it.workshopId == workshop.id
            }

        if (registeredCount >= workshop.capacity) {
            Toast.makeText(
                this,
                "Désolé, cet atelier est complet.",
                Toast.LENGTH_LONG
            ).show()
            return
        }

        // Vérifier les conflits d'horaires
        val hasScheduleConflict =
            KidsCampData.registrations
                .filter { it.childId == child.id }
                .any { registration ->

                    val otherWorkshop =
                        KidsCampData.workshops.find {
                            it.id == registration.workshopId
                        }

                    otherWorkshop != null &&
                            otherWorkshop.id != workshop.id &&
                            otherWorkshop.day.equals(
                                workshop.day,
                                ignoreCase = true
                            ) &&
                            workshop.startHour < otherWorkshop.endHour &&
                            otherWorkshop.startHour < workshop.endHour
                }

        if (hasScheduleConflict) {
            Toast.makeText(
                this,
                "Conflit d'horaire : cet enfant a déjà un atelier à ce moment.",
                Toast.LENGTH_LONG
            ).show()
            return
        }

        AlertDialog.Builder(this)
            .setTitle("Confirmer l'inscription")
            .setMessage(
                "Inscrire ${child.firstName} à l'atelier \"${workshop.title}\" ?\n\n" +
                        "Prix : ${workshop.price} DT"
            )
            .setNegativeButton("Annuler", null)
            .setPositiveButton("Confirmer") { _, _ ->

                // Nouvelle vérification avant l'ajout
                val isAlreadyRegistered =
                    KidsCampData.registrations.any {
                        it.childId == child.id &&
                                it.workshopId == workshop.id
                    }

                val currentCount =
                    KidsCampData.registrations.count {
                        it.workshopId == workshop.id
                    }

                if (isAlreadyRegistered) {
                    Toast.makeText(
                        this,
                        "Cet enfant est déjà inscrit.",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setPositiveButton
                }

                if (currentCount >= workshop.capacity) {
                    Toast.makeText(
                        this,
                        "L'atelier est maintenant complet.",
                        Toast.LENGTH_LONG
                    ).show()
                    return@setPositiveButton
                }

                KidsCampData.registrations.add(
                    Registration(child.id, workshop.id)
                )

                KidsCampData.save(this)

                Toast.makeText(
                    this,
                    "${child.firstName} est inscrit(e) avec succès !",
                    Toast.LENGTH_LONG
                ).show()

                recreate()
            }
            .show()
    }

    // Afficher uniquement les enfants inscrits à cet atelier
    private fun showRegisteredChildren(workshop: Workshop) {

        val registeredIds =
            KidsCampData.registrations
                .filter { it.workshopId == workshop.id }
                .map { it.childId }
                .toSet()

        val registeredChildren =
            KidsCampData.children.filter {
                it.id in registeredIds
            }

        if (registeredChildren.isEmpty()) {
            Toast.makeText(
                this,
                "Aucune inscription à annuler pour cet atelier.",
                Toast.LENGTH_LONG
            ).show()
            return
        }

        AlertDialog.Builder(this)
            .setTitle("Choisir l'inscription à annuler")
            .setItems(
                registeredChildren.map {
                    "${it.firstName} — ${it.age} ans"
                }.toTypedArray()
            ) { _, which ->
                confirmCancellation(
                    registeredChildren[which],
                    workshop
                )
            }
            .setNegativeButton("Fermer", null)
            .show()
    }

    // Confirmer et supprimer l'inscription choisie
    private fun confirmCancellation(
        child: Child,
        workshop: Workshop
    ) {

        AlertDialog.Builder(this)
            .setTitle("Annuler l'inscription ?")
            .setMessage(
                "Voulez-vous vraiment désinscrire ${child.firstName} " +
                        "de l'atelier \"${workshop.title}\" ?"
            )
            .setNegativeButton("Non", null)
            .setPositiveButton("Oui, annuler") { _, _ ->

                val removed = KidsCampData.registrations.removeAll {
                    it.childId == child.id &&
                            it.workshopId == workshop.id
                }

                if (removed) {
                    KidsCampData.save(this)

                    Toast.makeText(
                        this,
                        "Inscription de ${child.firstName} annulée.",
                        Toast.LENGTH_LONG
                    ).show()

                    recreate()
                } else {
                    Toast.makeText(
                        this,
                        "Cette inscription n'existe plus.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            .show()
    }
}
