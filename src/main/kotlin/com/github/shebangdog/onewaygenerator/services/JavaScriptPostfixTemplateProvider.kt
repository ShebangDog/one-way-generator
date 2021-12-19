package com.github.shebangdog.onewaygenerator.services

import com.intellij.codeInsight.template.postfix.templates.PostfixTemplate
import com.intellij.codeInsight.template.postfix.templates.PostfixTemplateProvider
import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiFile

class JavaScriptPostfixTemplateProvider : PostfixTemplateProvider {
    override fun isTerminalSymbol(currentChar: Char): Boolean {
        return '.' == currentChar
    }

    override fun getTemplates(): MutableSet<PostfixTemplate> {
        val javascriptPostFixTemplate = JavaScriptPostfixTemplate("input", this)

        return mutableSetOf(javascriptPostFixTemplate)
    }

    override fun preExpand(file: PsiFile, editor: Editor) {

    }

    override fun afterExpand(file: PsiFile, editor: Editor) {

    }

    override fun preCheck(copyFile: PsiFile, realEditor: Editor, currentOffset: Int): PsiFile {

        return copyFile
    }
}