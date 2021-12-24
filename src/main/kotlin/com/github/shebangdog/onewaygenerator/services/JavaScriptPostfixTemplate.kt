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
            text == "useState"
        }

        val elementType = callExprElement.elementType

        return elementType == JSElementTypes.CALL_EXPRESSION && callExprElement.children.any { textIsUseState(it.text) }
    }

    private fun getCounter(editor: Editor, value: String, counter: Int = 0): Int {
        println(editor.document.text)
        val counterAsString = when(counter) {
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

        val countAsString = { value: String ->
            when (val result = getCounter(editor, value)) {
                0 -> ""
                else -> "$result"
            }
        }

        val valueElemName = "value"
        val setValueElemName = "setValue"
        val handleChangeElemName = "handleChange"
        val componentElemName = "bindInput"

        val valueName = "${valueElemName}${countAsString(valueElemName)}"
        val setValueName = "${setValueElemName}${countAsString(setValueElemName)}"
        val handlerName = "${handleChangeElemName}${countAsString(handleChangeElemName)}"
        val componentName = "${componentElemName}${countAsString(componentElemName)}"

        val useStateExpr = context.parent?.parent?.text ?: ""
        val contextStatement = "const [$valueName, $setValueName] = $useStateExpr"

        val currentPrimaryCaret = editor.caretModel.primaryCaret
        deleteStringByLengthFromCaret(currentPrimaryCaret, useStateExpr.length)

        val handlerDeclaration = """
            const $handlerName = (event) => {
              $setValueName(event.target.$valueName)
            }
            
        """.trimIndent()

        val inputExpression = """
            const $componentName = <input value={${valueName}} onChange={${handlerName}} />
        """.trimIndent()

        val generatedCode = listOf(
            contextStatement,
            handlerDeclaration,
            inputExpression
        ).joinToString("\n")

        editor.document.insertString(currentPrimaryCaret.offset, generatedCode)

        currentPrimaryCaret.moveToOffset(currentPrimaryCaret.offset + generatedCode.length)
        println(context.parent.reference.toString())
        println(context.parent.text)

        println(context.parent.parent.reference.toString())
        println(context.parent.parent.text)
    }
}
