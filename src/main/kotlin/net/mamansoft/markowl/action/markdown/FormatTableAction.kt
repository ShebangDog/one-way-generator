package net.mamansoft.markowl.action.markdown

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.editor.CaretModel
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.ui.Messages

class FormatTableAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val editor: Editor = e.getRequiredData(CommonDataKeys.EDITOR)
        val caretModel: CaretModel = editor.caretModel
        val primaryCaret = caretModel.primaryCaret
        val logicalPos = primaryCaret.logicalPosition
        val visualPos = primaryCaret.visualPosition

        val caretOffset = primaryCaret.offset
        val report = """
            $logicalPos
            $visualPos
            Offset: $caretOffset
            """.trimIndent()

        primaryCaret.moveToOffset(100)

        Messages.showInfoMessage(report, "Caret Parameters Inside The Editor")
    }
}