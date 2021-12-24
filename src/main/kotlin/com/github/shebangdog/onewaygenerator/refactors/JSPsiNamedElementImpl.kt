package com.github.shebangdog.onewaygenerator.refactors

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.lang.javascript.psi.JSPsiNamedElementBase

abstract class JSPsiNamedElementImpl(node: ASTNode) : JSPsiNamedElementBase, ASTWrapperPsiElement(node)
