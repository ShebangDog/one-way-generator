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

    private fun getCounter(editor: Editor, value: String, counter: Int = 0): Int {
        println(editor.document.text)
        val counterAsString = when (counter) {
            0 -> ""
            else -> "$counter"
        }

        if (editor.document.text.contains("$value$counterAsString")) {
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
            return "$value${getCounter(editor, value)}"
        }

        fun stateDefinition(value: String, setValue: String, feature: String): String {
            return "const [$value, $setValue] = $feature"
        }

        val initialValue = (context.parent?.text ?: "").drop(1).dropLast(1)
        val useStateExpr = context.parent?.parent?.text ?: ""

        val valueName = "value"
        val setValueName = "renderUI"
        val handlerParamName = "event"
        val handlerName = "handleChange"
        val hooksName = "createInputFeature"

        val hooksExpr = "$hooksName($initialValue)"
        val inputtedValue = "$handlerParamName.target.value"

        val currentPrimaryCaret = editor.caretModel.primaryCaret
        deleteStringByLengthFromCaret(currentPrimaryCaret, useStateExpr.length)

        val handlerDefinition = """const $handlerName = ($handlerParamName) => {
  $setValueName($inputtedValue)
}"""

        val inputHooksDefinition = """const $hooksName = (initialValue) => {
  ${stateDefinition(valueName, setValueName, useStateExpr)}
              
  $handlerDefinition
              
  return [
    $valueName,
    $handlerName,
  ]
}"""

        val valueAsValue = valueNameWithCounter(valueName)
        val handlerAsValue = valueNameWithCounter(handlerName)

        val useInputHooksStatement = stateDefinition(
            valueAsValue,
            handlerAsValue,
            hooksExpr
        ).trimIndent()

        val inputExpression = "<input value={$valueAsValue} onChange={${handlerAsValue}} />"

        val generatedCode = listOf(useInputHooksStatement, inputExpression)
            .joinToString("\n") { it.trimIndent() }

        val text = editor.document.text
        val importsIndex = text.lastIndexOf("import ")
        val newLineIndex = if (!text.contains("import ")) 0 else text.indexOf("\n", importsIndex)

        if (!isDefined(editor, hooksName)) editor.document.insertString(if (newLineIndex == 0) 0 else newLineIndex + 1, "\n$inputHooksDefinition\n")
        editor.document.insertString(currentPrimaryCaret.offset, generatedCode)

        currentPrimaryCaret.moveToOffset(currentPrimaryCaret.offset + generatedCode.length)

//        println(context.parent.reference.toString())
//        println("11111${context.parent.text}")
//
//        println(context.parent.parent.reference.toString())
//        println(context.parent.parent.text)
    }
}
