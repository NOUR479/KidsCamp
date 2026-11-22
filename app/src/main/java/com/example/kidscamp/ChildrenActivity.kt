package com.example.kidscamp

import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.kidscamp.data.KidsCampData
import com.example.kidscamp.model.Child
import java.util.UUID

class ChildrenActivity : AppCompatActivity() {

    private lateinit var nameInput: EditText
    private lateinit var ageInput: EditText
    private lateinit var formTitle: TextView
    private lateinit var saveButton: Button
    private lateinit var cancelButton: Button
    private lateinit var childrenContainer: LinearLayout
    private lateinit var countText: TextView
    private lateinit var emptyText: TextView

    private var editingChildId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_children)

        nameInput = findViewById(R.id.etChildName)
        ageInput = findViewById(R.id.etChildAge)
        formTitle = findViewById(R.id.tvChildFormTitle)
        saveButton = findViewById(R.id.btnSaveChild)
        cancelButton = findViewById(R.id.btnCancelEdit)
        childrenContainer = findViewById(R.id.childrenContainer)
        countText = findViewById(R.id.tvChildrenCount)
        emptyText = findViewById(R.id.tvNoChildren)

        findViewById<Button>(R.id.btnBack).setOnClickListener {
            finish()
        }

        saveButton.setOnClickListener {
            saveChild()
        }

        cancelButton.setOnClickListener {
            resetForm()
        }

        displayChildren()
    }

    private fun saveChild() {
        val name = nameInput.text.toString().trim()
        val ageText = ageInput.text.toString().trim()

        if (name.isBlank()) {
            nameInput.error = "Le prénom est obligatoire."
            nameInput.requestFocus()
            return
        }

        if (name.length < 2) {
            nameInput.error = "Saisissez au moins 2 caractères."
            nameInput.requestFocus()
            return
        }

        val age = ageText.toIntOrNull()

        if (age == null || age !in 3..17) {
            ageInput.error = "L'âge doit être compris entre 3 et 17 ans."
            ageInput.requestFocus()
            return
        }

        val editingId = editingChildId

        if (editingId == null) {
            val child = Child(
                id = UUID.randomUUID().toString(),
                firstName = name,
                age = age
            )
            KidsCampData.children.add(child)
            KidsCampData.save(this)
            Toast.makeText(this, "Enfant ajouté.", Toast.LENGTH_SHORT).show()
        } else {
            val child = KidsCampData.children.find { it.id == editingId }

            if (child != null) {
                child.firstName = name
                child.age = age
                KidsCampData.save(this)
                Toast.makeText(this, "Profil modifié.", Toast.LENGTH_SHORT).show()
            }
        }

        resetForm()
        displayChildren()
    }

    private fun displayChildren() {
        childrenContainer.removeAllViews()

        val children = KidsCampData.children.toList()

        countText.text = if (children.size <= 1) {
            "${children.size} enfant"
        } else {
            "${children.size} enfants"
        }

        emptyText.visibility =
            if (children.isEmpty()) View.VISIBLE else View.GONE

        children.forEach { child ->
            childrenContainer.addView(createChildCard(child))
        }
    }

    private fun createChildCard(child: Child): View {
        val card = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(16), dp(14), dp(16), dp(14))
            background = getDrawable(R.drawable.bg_kidscamp_card)
        }

        val name = TextView(this).apply {
            text = child.firstName
            textSize = 19f
            setTextColor(Color.parseColor("#302522"))
            setTypeface(null, android.graphics.Typeface.BOLD)
        }

        val age = TextView(this).apply {
            text = "Âge : ${child.age} ans"
            textSize = 14f
            setTextColor(Color.parseColor("#796A64"))
            setPadding(0, dp(5), 0, dp(10))
        }

        val buttons = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.END or Gravity.CENTER_VERTICAL
        }

        val editButton = Button(this).apply {
            text = "Modifier"
            isAllCaps = false
            setTextColor(Color.WHITE)
            background = getDrawable(R.drawable.bg_btn_edit)
            backgroundTintList = null
            setPadding(dp(16), 0, dp(16), 0)


            setOnClickListener {
                startEditing(child)
            }
        }

        val deleteButton = Button(this).apply {
            text = "Supprimer"
            isAllCaps = false
            setTextColor(Color.WHITE)
            background = getDrawable(R.drawable.bg_btn_delete)
            backgroundTintList = null
            setPadding(dp(16), 0, dp(16), 0)

            setOnClickListener {
                confirmDelete(child)
            }
        }
        buttons.addView(
            editButton,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                dp(48)
            ).apply {
                marginEnd = dp(10) // Espace entre les deux boutons
            }
        )

        buttons.addView(
            deleteButton,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                dp(48)
            )
        )

        card.addView(name)
        card.addView(age)
        card.addView(buttons)

        card.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            bottomMargin = dp(12)
        }

        return card
    }

    private fun startEditing(child: Child) {
        editingChildId = child.id

        nameInput.setText(child.firstName)
        ageInput.setText(child.age.toString())

        formTitle.text = "Modifier le profil"
        saveButton.text = "Enregistrer les modifications"
        cancelButton.visibility = View.VISIBLE

        nameInput.requestFocus()
    }

    private fun confirmDelete(child: Child) {
        AlertDialog.Builder(this)
            .setTitle("Supprimer le profil ?")
            .setMessage(
                "Voulez-vous supprimer le profil de ${child.firstName} ?"
            )
            .setNegativeButton("Annuler", null)
            .setPositiveButton("Supprimer") { _, _ ->
                KidsCampData.children.removeAll { it.id == child.id }
                KidsCampData.registrations.removeAll { it.childId == child.id }
                KidsCampData.save(this)

                if (editingChildId == child.id) {
                    resetForm()
                }

                displayChildren()
                Toast.makeText(
                    this,
                    "Profil supprimé.",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .show()
    }

    private fun resetForm() {
        editingChildId = null
        nameInput.text.clear()
        ageInput.text.clear()
        nameInput.error = null
        ageInput.error = null

        formTitle.text = "Ajouter un enfant"
        saveButton.text = "Ajouter l'enfant"
        cancelButton.visibility = View.GONE
    }

    private fun dp(value: Int): Int {
        return (value * resources.displayMetrics.density).toInt()
    }
}