package com.github.shebangdog.onewaygenerator.services

import com.intellij.psi.PsiElement
import com.intellij.refactoring.rename.RenamePsiElementProcessor

class JSRenameProcessor : RenamePsiElementProcessor() {
    override fun canProcessElement(element: PsiElement): Boolean {
        return true
    }
}