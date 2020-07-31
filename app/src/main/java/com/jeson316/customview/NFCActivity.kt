package com.jeson316.customview

import android.app.PendingIntent
import android.content.Intent
import android.nfc.NdefMessage
import android.nfc.NfcAdapter
import android.os.Bundle
import android.text.Html
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.nio.charset.Charset
import kotlin.experimental.and


/**
 * 该nfc模式属于阻断式子，只有当前页面开启状态写才能读取到 相关Intetn来供nfc使用
 *
 * nfc ndef 的格式
 */
class NFCActivity : AppCompatActivity() {

    private var nfcAdapter: NfcAdapter? = null
    private var nfcPendingIntent: PendingIntent? = null
    private lateinit var show: TextView
    private var buffer: StringBuffer = StringBuffer()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nfc)
        show = findViewById(R.id.nfc_tv_show)

        nfcAdapter = NfcAdapter.getDefaultAdapter(this)

        addMessage("是否支持NFC", nfcAdapter != null)
        val nfcEnabled = nfcAdapter?.isEnabled
        addMessage("是否打开NFC", nfcEnabled ?: false)

        nfcPendingIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
            0
        )

        if (intent != null) processIntent(intent)

    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (intent != null) processIntent(intent)
    }

    /**
     * 处理数据
     */
    private fun processIntent(intent: Intent) {
        val action = intent.action
        addMessage("==============", "->")
        when (action) {
            NfcAdapter.ACTION_TAG_DISCOVERED -> {
                addMessage("NFC协议", NfcAdapter.ACTION_TAG_DISCOVERED)


            }
            NfcAdapter.ACTION_NDEF_DISCOVERED -> {
                addMessage("NFC协议", NfcAdapter.ACTION_NDEF_DISCOVERED)
                processNDEF(intent)
            }
            NfcAdapter.ACTION_TECH_DISCOVERED -> {
                addMessage("NFC协议", NfcAdapter.ACTION_TECH_DISCOVERED)
            }
        }

    }

    /**
     * 处理  NDEF 格式数据
     */
    private fun processNDEF(intent: Intent) {
        //获取ndef 内容
        val rawMessages = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES)

        if (rawMessages == null) {
            addMessage("NDEF 信息数量", 0)
        } else {
            try {
                addMessage("NDEF 信息数量", rawMessages.size)
                for (m in rawMessages) {
                    if (m != null) {
                        var m2 = m as NdefMessage
                        addMessage("NDEF message", m2)
                        addMessage("NDEF 中NdefRecord 数量", m2.records.size)
                        for (ndefRecord in m2.records) {
                            if (ndefRecord.toUri() != null) {
                                addMessage("NdefRecord 是 URI NDEF. ", ndefRecord.toUri())
                            } else {
                                //其他格式，比如 text/ http/ 等等
                                addMessage(
                                    "内容 Contents，字节表示 ",
                                    ndefRecord.payload.contentToString()
                                )
                                //通过payload获取字节流信息
                                val payload = ndefRecord.payload


                                //根据首位来判断编码方式
                                var textEncoding = "UTF-8"
                                if ((payload[0] and 0x80.toByte()) != 0.toByte()) {
                                    textEncoding = "UTF-16"
                                }
                                addMessage("编码方式", textEncoding)

                                // 获得语言编码长度
                                val languageCodeLength: Int = (payload[0] and 0x3f.toByte()).toInt()
                                addMessage("编码长度", languageCodeLength)

                                // 获得语言编码
                                val languageCode =
                                    String(
                                        payload,
                                        1,
                                        languageCodeLength,
                                        Charset.forName("US-ASCII")
                                    )
                                addMessage("语言编码", languageCode)

                                //获取偏移量后的准确信息
                                val trueMessage = String(
                                    payload,
                                    languageCodeLength + 1,
                                    payload.size - languageCodeLength - 1,
                                    Charset.forName(textEncoding)
                                )
                                addMessage("有用的数据", trueMessage)
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                e.message?.let { addMessage("!!!!! 解析数据错误，", it) }
            }

        }
    }

    override fun onResume() {
        super.onResume()
        nfcAdapter?.enableForegroundDispatch(this, nfcPendingIntent, null, null)
    }

    override fun onPause() {
        super.onPause()
        nfcAdapter?.disableForegroundDispatch(this)
    }


    /**
     * 展示信息
     * detail 转换为字符串
     */
    private fun addMessage(tag: String, detail: Any) {
        val message = String.format("<br>%s : %s</br>", tag, detail.toString())
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            buffer.append(Html.fromHtml(message, Html.FROM_HTML_MODE_LEGACY))
        } else {
            buffer.append(Html.fromHtml(message))
        }
        show.text = buffer
    }

}