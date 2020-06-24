import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.ui.popup.ListPopup;
import com.intellij.psi.*;

import com.intellij.psi.impl.source.PsiJavaFileImpl;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.lang.UrlClassLoader;


import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class JBPopupFactoryAction extends AnAction {
    @Override
    public void actionPerformed(AnActionEvent e) {
        List<String> elementsNames = new ArrayList<>();
        elementsNames.add("Кнопка: Отменить");
        elementsNames.add("Кнопка: Добавить");
        elementsNames.add("Кнопка: Удалить");


        DefaultActionGroup actionGroup = (DefaultActionGroup) ActionManager.getInstance().getAction("Sample_JBPopupActionGroup");
        actionGroup.removeAll();

        for(String elementName : elementsNames){
            actionGroup.add(new AnAction(elementName) {
                @Override
                public void actionPerformed(AnActionEvent e) {
                    insertText(e, "\""+elementName+"\"");
                }
            });
        }

        ListPopup listPopup = JBPopupFactory.getInstance().createActionGroupPopup("Page elements list", actionGroup, e.getDataContext(), JBPopupFactory.ActionSelectionAid.SPEEDSEARCH, false);
        listPopup.showInBestPositionFor(e.getDataContext());
    }



    public void insertText(AnActionEvent e, String text) {
        // Get all the required data from data keys
        final Editor editor = e.getRequiredData(CommonDataKeys.EDITOR);
        final Project project = e.getRequiredData(CommonDataKeys.PROJECT);
        final Document document = editor.getDocument();


//__________________________________________________________________________________
        int caret =  editor.getCaretModel().getOffset();
        PsiFile psiFile = PsiDocumentManager.getInstance(project).getPsiFile(document);
//        PsiMethod method = PsiTreeUtil.getParentOfType(psiFile.findElementAt(caret), PsiMethod.class);

        PsiJavaFile psiJavaFile = (PsiJavaFile) PsiManager.getInstance(project).findFile(psiFile.getVirtualFile());
        System.out.println("Check: "+ psiJavaFile.findReferenceAt(caret-2).getElement().getReference().resolve().getText());
//        Получаем имя класса
        psiJavaFile.findReferenceAt(caret-2).getElement().getReference().resolve().getParent().getContainingFile().getVirtualFile().getName();
//        Название дирректории
        psiJavaFile.findReferenceAt(caret-2).getElement().getReference().resolve().getParent().getContainingFile().getContainingDirectory().getName();
//        Получаем имя класса без расширения
        psiJavaFile.findReferenceAt(caret-2).getElement().getReference().resolve().getParent().getContainingFile().getVirtualFile().getNameWithoutExtension();

        PsiFile psiFile1 = psiJavaFile.findReferenceAt(caret-2).getElement().getReference().resolve().getParent().getContainingFile();
        ((PsiJavaFileImpl) psiFile1).getPackageName();

//__________________________________________________________________________________


        // Work off of the primary caret to get the selection info
        Caret primaryCaret = editor.getCaretModel().getPrimaryCaret();
        int start = primaryCaret.getSelectionStart();
        int end = primaryCaret.getSelectionEnd();

        // Replace the selection with a fixed string.
        // Must do this document change in a write action context.
        WriteCommandAction.runWriteCommandAction(project, () ->
                document.replaceString(start, end, text)
        );

        // De-select the text range that was just replaced
        primaryCaret.removeSelection();
    }




}