
package com.example.kidscamp

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.kidscamp.data.KidsCampData
import java.util.Locale

class InvoiceActivity : AppCompatActivity() {

    private lateinit var detailsText: TextView
    private lateinit var discountText: TextView
    private lateinit var totalText: TextView

    private val discountRate = 0.10

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_invoice)

        detailsText = findViewById(R.id.tvInvoiceDetails)
        discountText = findViewById(R.id.tvInvoiceDiscount)
        totalText = findViewById(R.id.tvInvoiceTotal)

        findViewById<Button>(R.id.btnBack).setOnClickListener {
            finish()
        }

        updateInvoice()
    }

    override fun onResume() {
        super.onResume()

        if (::detailsText.isInitialized) {
            updateInvoice()
        }
    }

    private fun updateInvoice() {
        val registrations = KidsCampData.registrations.toList()
        val children = KidsCampData.children.toList()
        val workshops = KidsCampData.workshops.toList()

        if (registrations.isEmpty()) {
            detailsText.text = "Aucune inscription pour le moment.\n\n" +
                    "Rends-toi dans « Nos ateliers » pour inscrire un enfant."
            discountText.text = "La réduction familiale sera calculée automatiquement."
            totalText.text = "0,00 DT"
            return
        }

        val registeredChildIds = registrations
            .map { it.childId }
            .distinct()

        val childSubtotals = mutableMapOf<String, Double>()
        val details = StringBuilder()

        var subtotal = 0.0

        details.append("🧾 RÉCAPITULATIF DES INSCRIPTIONS\n\n")

        children.filter { it.id in registeredChildIds }.forEach { child ->
            val childRegistrations = registrations.filter {
                it.childId == child.id
            }

            var childTotal = 0.0

            details.append("👧 ${child.firstName} (${child.age} ans)\n")

            childRegistrations.forEach { registration ->
                val workshop = workshops.find {
                    it.id == registration.workshopId
                }

                if (workshop != null) {
                    childTotal += workshop.price

                    details.append(
                        "   • ${workshop.title}: " +
                                "${formatMoney(workshop.price)}\n"
                    )
                }
            }

            childSubtotals[child.id] = childTotal
            subtotal += childTotal

            details.append(
                "   Sous-total : ${formatMoney(childTotal)}\n\n"
            )
        }

        val registeredChildrenCount = childSubtotals.count {
            it.value > 0.0
        }

        val discount = if (registeredChildrenCount >= 2) {
            subtotal * discountRate
        } else {
            0.0
        }

        val total = subtotal - discount

        details.append("--------------------------------\n")
        details.append("Nombre d'enfants inscrits : $registeredChildrenCount\n")
        details.append("Sous-total familial : ${formatMoney(subtotal)}")

        detailsText.text = details.toString()

        discountText.text = if (discount > 0.0) {
            "🎉 Réduction fratrie (10 %) : -${formatMoney(discount)}"
        } else {
            "💡 Réduction fratrie : 10 % à partir de 2 enfants inscrits. " +
                    "Pour le moment : 0,00 DT."
        }

        totalText.text = formatMoney(total)
    }

    private fun formatMoney(amount: Double): String {
        return String.format(Locale.FRANCE, "%.2f DT", amount)
    }
}
