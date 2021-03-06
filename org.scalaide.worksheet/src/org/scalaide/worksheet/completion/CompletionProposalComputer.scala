package org.scalaide.worksheet.completion

import scala.tools.eclipse.ScalaPresentationCompiler
import scala.tools.eclipse.ScalaWordFinder
import scala.tools.eclipse.completion.ScalaCompletions
import scala.tools.eclipse.ui.ScalaCompletionProposal
import scala.tools.eclipse.util.EditorUtils
import scala.tools.nsc.util.SourceFile

import org.eclipse.jface.text.ITextViewer
import org.eclipse.jface.text.contentassist.ICompletionProposal
import org.eclipse.jface.text.contentassist.IContentAssistProcessor
import org.eclipse.jface.text.contentassist.IContextInformation
import org.eclipse.ui.texteditor.ITextEditor
import org.scalaide.worksheet.ScriptCompilationUnit

class CompletionProposalComputer(textEditor: ITextEditor) extends ScalaCompletions with IContentAssistProcessor {
  def getCompletionProposalAutoActivationCharacters() = Array('.')

  def getContextInformationAutoActivationCharacters() = Array[Char]()

  def getErrorMessage = "No error"

  def getContextInformationValidator = null

  def computeCompletionProposals(viewer: ITextViewer, offset: Int): Array[ICompletionProposal] = {
    EditorUtils.getEditorCompilationUnit(textEditor) match {
      case Some(scu: ScriptCompilationUnit) =>
        // TODO: Not sure if this is the best way. Maybe compilation units should always be connected to something..
        scu.connect(viewer.getDocument)
        scu.withSourceFile { findCompletions(viewer, offset, scu) }(List[ICompletionProposal]()).toArray
      case _ => Array()
    }
  }

  private def findCompletions(viewer: ITextViewer, position: Int, scu: ScriptCompilationUnit)(sourceFile: SourceFile, compiler: ScalaPresentationCompiler): List[ICompletionProposal] = {
    val region = ScalaWordFinder.findCompletionPoint(viewer.getDocument.get, position)

    val res = findCompletions(region)(position, scu)(sourceFile, compiler).sortBy(_.relevance).reverse

    res.map(ScalaCompletionProposal(viewer.getSelectionProvider))

  }

  def computeContextInformation(viewer: ITextViewer, offset: Int): Array[IContextInformation] = {
    null
  }
}