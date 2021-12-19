package com.github.shebangdog.onewaygenerator.services

import com.intellij.codeInsight.template.postfix.templates.PostfixTemplate
import com.intellij.codeInsight.template.postfix.templates.PostfixTemplateProvider
import com.intellij.lang.javascript.JSElementTypes
import com.intellij.openapi.editor.Caret
import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiElement
import com.intellij.psi.util.elementType
import kotlinx.coroutines.repackaged.net.bytebuddy.description.NamedElement

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

    override fun expand(context: PsiElement, editor: Editor) {
        fun deleteStringByLengthFromCaret(caret: Caret, length: Int): String {
            editor.document.deleteString(caret.offset - length, caret.offset)

            return editor.document.text
        }

        val valueName = "value"
        val setValueName = { name: String ->
            "set${name}"
        }

        val camelValueName = valueName.mapIndexed { index, c ->
            if (index == 0) c.uppercase().first()
            else c
        }.joinToString("")

        val useStateExpr = context.parent?.parent?.text ?: ""
        val contextStatement = "const [$valueName, ${setValueName(camelValueName)}] = $useStateExpr"

        val currentPrimaryCaret = editor.caretModel.primaryCaret
        deleteStringByLengthFromCaret(currentPrimaryCaret, useStateExpr.length)

        val handlerDeclaration = """
            const handleChange = (event) => {
              setValue(event.target.$valueName)
            }
            
        """.trimIndent()

        val inputExpression = """
            const bindInput = <input value={value} onChange={handleChange} />
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

//        val manager = PsiDocumentManager.getInstance(context.project)
//        manager.commitDocument(editor.document)
//
//
//        context.parent.parent.children.forEach {
//            println(it.text)
//            println(it.reference.toString())
//            it.reference?.handleElementRename("newState")
//        }
    }

}
