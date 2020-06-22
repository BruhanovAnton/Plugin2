import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.ui.popup.ListPopup;
import com.intellij.psi.*;

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
        PsiMethod method = PsiTreeUtil.getParentOfType(psiFile.findElementAt(caret), PsiMethod.class);


        System.out.println(psiFile.findReferenceAt(caret-2).getElement());
        System.out.println(psiFile.getContainingFile());



        System.out.println("Check: "+psiFile.getVirtualFile().getPath());


        System.out.println("Check2: "+psiFile.getVirtualFile().getUrl());


//        psiFile.getReference().;

        if (method != null) {
          System.out.println(method.getName());
//            System.out.println(method.findReferenceAt(caret).getElement().getText());

        }
        PsiClass containingClass = method.getContainingClass();


//System.out.println(containingClass.getInnerClasses()[0].getReference());
        System.out.println("works!!!");
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