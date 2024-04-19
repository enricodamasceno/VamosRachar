package com.example.constraintlayout

import android.content.Intent
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.Locale

class MainActivity : AppCompatActivity(), TextWatcher, TextToSpeech.OnInitListener {
    private lateinit var tts: TextToSpeech
    private lateinit var edtConta: EditText
    private lateinit var edtPessoas: EditText
    private lateinit var txtResult: TextView
    private lateinit var speakButton: Button
    private lateinit var shareButton: FloatingActionButton // Adicione essa linha
    private var ttsSuccess: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        edtConta = findViewById(R.id.edtConta)
        edtPessoas = findViewById(R.id.edtPessoas)
        txtResult = findViewById(R.id.result)
        speakButton = findViewById(R.id.btFalar)
        shareButton = findViewById(R.id.floatingActionButton)

        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                txtResult.text = "Calculando..."
            }

            override fun afterTextChanged(s: Editable?) {
                val contaStr = edtConta.text.toString()
                val pessoasStr = edtPessoas.text.toString()

                val conta = contaStr.toDoubleOrNull() ?: 0.0
                val pessoas = pessoasStr.toIntOrNull() ?: 0

                if (conta != 0.0 && pessoas != 0) {
                    val result = conta / pessoas
                    txtResult.text = result.toString()
                }
            }
        }

        edtConta.addTextChangedListener(textWatcher)
        edtPessoas.addTextChangedListener(textWatcher)

        // Initialize TTS engine
        tts = TextToSpeech(this, this)

        speakButton.setOnClickListener {
            val contaStr = edtConta.text.toString()
            val text = "A conta ficou $contaStr para cada!"
            if (text.isNotEmpty()) {
                tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
            }
        }
        // Adicione isso para lidar com o clique no botão de compartilhar
        shareButton.setOnClickListener {
            val result = txtResult.text.toString()
            if (result.isNotEmpty()) {
                shareResult(result)
            }
        }
    }

    private fun shareResult(result: String) {
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, "O valor a ser compartilhado é: $result")
            type = "text/plain"
        }

        val shareIntent = Intent.createChooser(sendIntent, null)
        startActivity(shareIntent)
    }

    override fun onDestroy() {
        // Release TTS engine resources
        tts.stop()
        tts.shutdown()
        super.onDestroy()
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            // TTS engine is initialized successfully
            tts.language = Locale.getDefault()
            ttsSuccess = true
            Log.d("PDM23", "Sucesso na Inicialização")
        } else {
            // TTS engine failed to initialize
            Log.e("PDM23", "Failed to initialize TTS engine.")
            ttsSuccess = false
        }
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        Log.d("PDM24", "Antes de mudar")
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        Log.d("PDM24", "Mudando")
    }

    override fun afterTextChanged(s: Editable?) {
        Log.d("PDM24", "Depois de mudar")
        val valor = s.toString().toDoubleOrNull()
        valor?.let { Log.d("PDM24", "v: $it") }
    }
}