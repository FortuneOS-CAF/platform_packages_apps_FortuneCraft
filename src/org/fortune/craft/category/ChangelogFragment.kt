package org.fortune.craft.category

import android.os.Bundle
import android.graphics.Color
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.preference.PreferenceFragment
import com.android.settings.R
import java.io.FileReader
import java.io.IOException
import java.util.regex.Pattern
import java.util.regex.Matcher

class ChangelogFragment : PreferenceFragment() {

    private lateinit var textView: TextView

    companion object {
        private const val CHANGELOG_PATH = "/system/etc/Changelog.txt"
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.changelog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        textView = view.findViewById<TextView>(R.id.changelog_text)!!

        val data = StringBuilder()

        val date = Pattern.compile("(={20}|\\d{4}-\\d{2}-\\d{2})")
        val commit = Pattern.compile("([a-f0-9]{7})")
        val committer = Pattern.compile("\\[(\\D.*?)]")
        val title = Pattern.compile("(\\R\\s+[\\*]\\s.*)")

        var inputReader: FileReader? = null
        try {
            val tmp = CharArray(2048)
            var numRead: Int

            inputReader = FileReader(CHANGELOG_PATH)
            while (inputReader.read(tmp).also { numRead = it } >= 0) {
                data.append(tmp, 0, numRead)
            }
        } catch (e: IOException) {

        } finally {
            try {
                inputReader?.close()
            } catch (e: IOException) {

            }
        }

        val sb = SpannableStringBuilder(data)
        val theme = context?.theme
        val typedValue = TypedValue()
        theme?.resolveAttribute(android.R.attr.colorAccent, typedValue, true)
        val color = context?.getColor(typedValue.resourceId) ?: Color.BLACK

        var m: Matcher = date.matcher(data)
        while (m.find()) {
            sb.setSpan(ForegroundColorSpan(color), m.start(1), m.end(1), Spannable.SPAN_INCLUSIVE_INCLUSIVE)
            sb.setSpan(StyleSpan(Typeface.BOLD), m.start(1), m.end(1), Spannable.SPAN_INCLUSIVE_INCLUSIVE)
        }
        m = commit.matcher(data)
        while (m.find()) {
            sb.setSpan(StyleSpan(Typeface.NORMAL), m.start(1), m.end(1), Spannable.SPAN_INCLUSIVE_INCLUSIVE)
        }
        m = committer.matcher(data)
        while (m.find()) {
            sb.setSpan(ForegroundColorSpan(color), m.start(1), m.end(1), Spannable.SPAN_INCLUSIVE_INCLUSIVE)
            sb.setSpan(StyleSpan(Typeface.NORMAL), m.start(1), m.end(1), Spannable.SPAN_INCLUSIVE_INCLUSIVE)
        }
        m = title.matcher(data)
        while (m.find()) {
            sb.setSpan(ForegroundColorSpan(color), m.start(1), m.end(1), Spannable.SPAN_INCLUSIVE_INCLUSIVE)
            sb.setSpan(StyleSpan(Typeface.BOLD), m.start(1), m.end(1), Spannable.SPAN_INCLUSIVE_INCLUSIVE)
        }

        textView.text = sb
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
    }
}
