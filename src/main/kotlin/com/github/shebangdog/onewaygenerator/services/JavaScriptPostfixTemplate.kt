package com.github.shebangdog.onewaygenerator.services

import com.intellij.codeInsight.template.postfix.templates.PostfixTemplate
import com.intellij.codeInsight.template.postfix.templates.PostfixTemplateProvider
import com.intellij.lang.javascript.JSElementTypes
import com.intellij.openapi.editor.Caret
import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiElement
import com.intellij.psi.util.elementType

class JavaScriptPostfixTemplate(
    name: String,
    provider: PostfixTemplateProvider,
) : PostfixTemplate(
    "${JavaScriptPostfixTemplate::class.java.canonicalName}#${name.substring(1)}",
    name,
    "generate handler for onChange in Input",
    provider,
) {
    override fun isApplicable(context: PsiElement, copyDocument: Document, newOffset: Int): Boolean {
        val callExprElement = context.parent.parent
        val textIsUseState = { text: String ->
            text == "createFeature" || text == "useState"
        }

        val elementType = callExprElement.elementType
        return elementType == JSElementTypes.CALL_EXPRESSION && callExprElement.children.any { textIsUseState(it.text) }
    }

    private fun isDefined(editor: Editor, name: String): Boolean {
        val definition = "const$name"
        val text = editor.document.text
        val removedSpacesText = text.filterNot { it.isWhitespace() }

        return removedSpacesText.contains(definition)
    }

    private fun counterAsString(counter: Int): String {
        return when (counter) {
            0 -> ""
            else -> "$counter"
        }
    }

    private fun getCounter(editor: Editor, value: String, counter: Int = 0): Int {
        if (editor.document.text.contains("$value${counterAsString(counter)}")) {
            return getCounter(editor, value, counter + 1)
        }

        return counter
    }

    override fun expand(context: PsiElement, editor: Editor) {
        fun deleteStringByLengthFromCaret(caret: Caret, length: Int): String {
            editor.document.deleteString(caret.offset - length, caret.offset)

            return editor.document.text
        }

        fun valueNameWithCounter(value: String): String {
            val counter = getCounter(editor, value)

            return "$value${counterAsString(counter)}"
        }

        fun stateDefinition(value: String, setValue: String, feature: String): String {
            return "const [$value, $setValue] = $feature"
        }

        val initialValue = (context.parent?.text ?: "").drop(1).dropLast(1)
        val useStateExpr = context.parent?.parent?.text ?: ""

        val valueName = "value"
        val setValueName = "setValue"
        val handlerParamName = "event"
        val handlerName = "onChange"
        val hooksName = "useInput"

        val hooksExpr = "$hooksName($initialValue)"
        val inputtedValue = "$handlerParamName.target.value"

        val currentPrimaryCaret = editor.caretModel.primaryCaret
        deleteStringByLengthFromCaret(currentPrimaryCaret, useStateExpr.length)

        val handlerDefinition = """function $handlerName($handlerParamName) {
            $setValueName($inputtedValue)
        }"""

        val inputHooksDefinition = """function $hooksName(initialValue) {
            ${stateDefinition(valueName, setValueName, useStateExpr)}
              
            $handlerDefinition
              
            return {
                $valueName,
                $handlerName,
            }
        }"""

        val valueAsValue = valueNameWithCounter(valueName)
        val handlerAsValue = valueNameWithCounter(handlerName)

        fun useInputHooksStatement(): String {
            val declaration =
                if (valueName == valueAsValue && handlerName == handlerAsValue) "const { ${valueName}, ${handlerName} }"
                else "const {$valueName: $valueAsValue, $handlerName: $handlerAsValue}"

            return "$declaration= $hooksExpr"
        }

        val inputExpression = "<input value={$valueAsValue} onChange={${handlerAsValue}} />"

        val generatedCode = listOf(useInputHooksStatement(), inputExpression)
            .joinToString("\n") { it.trimIndent() }

        val text = editor.document.text
        val importsIndex = text.lastIndexOf("import ")
        val newLineIndex = if (!text.contains("import ")) 0 else text.indexOf("\n", importsIndex)

        if (!isDefined(editor, hooksName)) editor.document.insertString(
            if (newLineIndex == 0) 0 else newLineIndex + 1,
            "\n$inputHooksDefinition\n"
        )
        editor.document.insertString(currentPrimaryCaret.offset, generatedCode)

        currentPrimaryCaret.moveToOffset(currentPrimaryCaret.offset + generatedCode.length)
    }
}
