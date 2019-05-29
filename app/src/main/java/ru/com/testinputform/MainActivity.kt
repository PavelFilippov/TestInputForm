package ru.com.testinputform

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.google.android.material.button.MaterialButton
import com.jakewharton.rxbinding2.widget.RxTextView
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern

class MainActivity : AppCompatActivity() {

// Initialize regex
    private val cardNumberRegex = "^\\d{4} \\d{4} \\d{4} \\d{4,7}\$"
    private val bicRegex = "^\\d{9}\$"
    private val accountNumberRegex = "^\\d{20}\$"
    private val nameRegex = "^[а-яА-Я\\\\-\\\\s]{2,40}\$"

// Initialize disposables for card and account selections
    private var cardDisposable = CompositeDisposable()
    private var accountDisposable = CompositeDisposable()

// Initialize internal values

    //Identifier to restore validators on start
    private var identifier = -1;

    //Card validator values
    private var isCardValid = false;

    //Account validator values
    private var isBicValid = false;
    private var isAccountValid = false;
    private var isPaymentSelected = false;
    private var isLastNameValid = false;
    private var isFirstNameValid = false;
    private var isMiddleNameValid = false;

    @SuppressLint("RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Initialize Identifier spinner

        val identifierSpinnerAdapter = ArrayAdapter<String>(
            this,
            android.R.layout.simple_dropdown_item_1line,
            resources.getStringArray(R.array.identifier_types)
        )
        val identifierSpinner = findViewById<View>(R.id.mbsChooseIdentifier) as MaterialBetterSpinner
        identifierSpinner.setAdapter<ArrayAdapter<String>>(identifierSpinnerAdapter)

        identifierSpinner.onItemClickListener = object : AdapterView.OnItemClickListener {
            override fun onItemClick(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                when (position) {
                    0 -> {
                        identifier = position
                        tilCardNumber.visibility = View.VISIBLE
                        clAccount.visibility = View.GONE
                        validateCardFields()
                    }
                    1 -> {
                        identifier = position
                        clAccount.visibility = View.VISIBLE
                        tilCardNumber.visibility = View.GONE
                        validateAccountFields()
                    }
                }
            }
        }

        //Initialize Payment spinner on Account Identifier

        val paymentSpinnerAdapter = ArrayAdapter<String>(
            this,
            android.R.layout.simple_dropdown_item_1line,
            resources.getStringArray(R.array.payment_types)
        )
        val paymentSpinner = findViewById<View>(R.id.mbsChoosePayment) as MaterialBetterSpinner
        paymentSpinner.setAdapter<ArrayAdapter<String>>(paymentSpinnerAdapter)

        val buttonProceed = findViewById<View>(R.id.btnProceed) as MaterialButton
        buttonProceed.supportBackgroundTintList =
            ContextCompat.getColorStateList(this, R.color.button_orange_tint_selector)

        paymentSpinner.onItemClickListener = object : AdapterView.OnItemClickListener {
            override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                isPaymentSelected = true
                setButtonEnabled()
            }
        }
    }

    override fun onStart() {
        super.onStart()

        //Restore validators
        if (identifier == 0)
            validateCardFields()
        else if (identifier == 1)
            validateAccountFields()
    }

    override fun onStop() {
        super.onStop()
        if (!cardDisposable.isDisposed) cardDisposable.clear()
        if (!accountDisposable.isDisposed) accountDisposable.clear()
    }

//Card validation

    private fun validateCardFields() {
        with(cardDisposable) {
            clear()
            add(validateInput(tietCardNumber, { isCardNumberValid(tietCardNumber.text.toString()) }))
        }
    }

    private fun isCardNumberValid(cardNumber: String) {
        if (!Pattern.matches(cardNumberRegex, cardNumber)) {
            tilCardNumber.isErrorEnabled = true
            tilCardNumber.error = getString(R.string.wrong_value)
            isCardValid = false
        } else {
            tilCardNumber.isErrorEnabled = false
            isCardValid = true
        }
    }

//Account validation

    private fun validateAccountFields() {
        with(accountDisposable) {
            clear()
            add(validateInput(tietBic, { isBicValid(tietBic.text.toString()) }))
            add(
                validateInput(
                    tietAccountNumber,
                    { isAccountNumberValid(tietAccountNumber.text.toString()) })
            )
            add(validateInput(tietLastName, { isLastNameValid(tietLastName.text.toString()) }))
            add(validateInput(tietFirstName, { isFirstNameValid(tietFirstName.text.toString()) }))
            add(validateInput(tietMiddleName, { isMiddleNameValid(tietMiddleName.text.toString()) }))
        }
    }

    private fun isBicValid(bic: String) {
        if (!Pattern.matches(bicRegex, bic)) {
            tilBic.isErrorEnabled = true
            tilBic.error = getString(R.string.wrong_value)
            isBicValid = false
        } else {
            tilBic.isErrorEnabled = false
            isBicValid = true
        }
    }

    private fun isAccountNumberValid(accountNumber: String) {
        if (!Pattern.matches(accountNumberRegex, accountNumber)) {
            tilAccountNumber.isErrorEnabled = true
            tilAccountNumber.error = getString(R.string.wrong_value)
            isAccountValid = false
        } else {
            tilAccountNumber.isErrorEnabled = false
            isAccountValid = true;
        }
    }

    private fun isLastNameValid(name: String) {
        if (!Pattern.matches(nameRegex, name)) {
            tilLastName.isErrorEnabled = true
            tilLastName.error = getString(R.string.wrong_value)
            isLastNameValid = false
        } else {
            tilLastName.isErrorEnabled = false
            isLastNameValid = true
        }
    }

    private fun isFirstNameValid(name: String) {
        if (!Pattern.matches(nameRegex, name)) {
            tilFirstName.isErrorEnabled = true
            tilFirstName.error = getString(R.string.wrong_value)
            isFirstNameValid = false
        } else {
            tilFirstName.isErrorEnabled = false
            isFirstNameValid = true
        }
    }

    private fun isMiddleNameValid(name: String) {
        if (!Pattern.matches(nameRegex, name)) {
            tilMiddleName.isErrorEnabled = true
            tilMiddleName.error = getString(R.string.wrong_value)
            isMiddleNameValid = false;
        } else {
            tilMiddleName.isErrorEnabled = false
            isMiddleNameValid = true;
        }
    }

//Main validation fun

    private inline fun validateInput(
        inputView: TextView,
        crossinline body: () -> Unit
    ): Disposable {
        return RxTextView.textChanges(inputView)
            .skipInitialValue()
            .throttleLast(300, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
            .doOnNext {
                body()
                setButtonEnabled()
            }
            .subscribe { }
    }

//Check validators values to set enable/disable button

    fun setButtonEnabled() {
        when (identifier) {
            0 -> {
                btnProceed.isEnabled = isCardValid
            }
            1 -> {
                btnProceed.isEnabled = isBicValid &&
                        isAccountValid &&
                        isPaymentSelected &&
                        isLastNameValid &&
                        isFirstNameValid &&
                        isMiddleNameValid
            }
        }
    }
}
