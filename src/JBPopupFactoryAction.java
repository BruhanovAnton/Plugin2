import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.ui.popup.ListPopup;
import com.intellij.psi.*;

import java.util.ArrayList;
import java.util.List;

public class JBPopupFactoryAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        final Editor editor = e.getRequiredData(CommonDataKeys.EDITOR);
        final Project project = e.getRequiredData(CommonDataKeys.PROJECT);
        final Document document = editor.getDocument();

        int caret = editor.getCaretModel().getOffset();
        PsiFile psiFile = PsiDocumentManager.getInstance(project).getPsiFile(document);
        PsiFile linkedPsiFile = psiFile.findReferenceAt(caret - 2).getElement().getReference().resolve().getContainingFile();

        PsiClass psiClass = ((PsiJavaFile) linkedPsiFile).getClasses()[0];
        List<String> elementsNames = new ArrayList<>();
        PsiField[] psiFields = psiClass.getAllFields();

        for (int i = 0; i < psiFields.length; i++) {
            PsiAnnotation psiAnnotation = psiFields[i].getAnnotations()[0];
            String annotationName = psiAnnotation.getNameReferenceElement().getReferenceNameElement().getText();
            if (annotationName.equals("ElementName")) {
                PsiNameValuePair[] attributes = psiAnnotation.getParameterList().getAttributes();
                for (int j = 0; j < attributes.length; j++) {
                    if (attributes[j].getAttributeName().equals("value")) {
                        elementsNames.add(attributes[j].getValue().getText());
                    }
                }
            }
        }

        DefaultActionGroup actionGroup = (DefaultActionGroup) ActionManager.getInstance().getAction("Sample_JBPopupActionGroup");
        actionGroup.removeAll();

        for (String elementName : elementsNames) {
            actionGroup.add(new AnAction(elementName) {
                @Override
                public void actionPerformed(AnActionEvent e) {
                    // Work off of the primary caret to get the selection info
                    Caret primaryCaret = editor.getCaretModel().getPrimaryCaret();
                    int start = primaryCaret.getSelectionStart();
                    int end = primaryCaret.getSelectionEnd();

                    // Replace the selection with a fixed string.
                    // Must do this document change in a write action context.
                    WriteCommandAction.runWriteCommandAction(project, () ->
                            document.replaceString(start, end, elementName)
                    );
                    // De-select the text range that was just replaced
                    primaryCaret.removeSelection();
                }
            });
        }

        ListPopup listPopup = JBPopupFactory.getInstance().createActionGroupPopup("Page elements list", actionGroup, e.getDataContext(), JBPopupFactory.ActionSelectionAid.SPEEDSEARCH, false);
        listPopup.showInBestPositionFor(e.getDataContext());
    }
}