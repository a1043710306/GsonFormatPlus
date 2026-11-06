package com.foxsteps.gsonformat;

import com.foxsteps.gsonformat.ui.JsonDialog;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;

/**
 * User: dim
 * Date: 14-7-4
 * Time: 下午1:44
 * <pre>
 * 插件主入口方法
 * </pre>
 */
public class MainAction extends AnAction {

    @Override
    public void update(AnActionEvent event) {
        Project project = event.getData(CommonDataKeys.PROJECT);
        Editor editor = event.getData(CommonDataKeys.EDITOR);
        PsiFile file = event.getData(CommonDataKeys.PSI_FILE);
        boolean enabled = project != null && editor != null && file instanceof PsiJavaFile
                && findTargetClass(editor, file) != null;
        event.getPresentation().setEnabledAndVisible(enabled);
    }

    @Override
    public void actionPerformed(AnActionEvent event) {
        Project project = event.getData(CommonDataKeys.PROJECT);
        Editor editor = event.getData(CommonDataKeys.EDITOR);
        if (project == null || editor == null) {
            return;
        }
        PsiFile mFile = event.getData(CommonDataKeys.PSI_FILE);
        PsiClass psiClass = findTargetClass(editor, mFile);
        if (!(mFile instanceof PsiJavaFile) || psiClass == null) {
            return;
        }
        //初始化Json弹框
        JsonDialog jsonD = new JsonDialog(psiClass, mFile, project);
        jsonD.setClass(psiClass);
        jsonD.setFile(mFile);
        jsonD.setProject(project);
        jsonD.setSize(1000, 700);
        jsonD.setLocationRelativeTo(null);
        jsonD.setVisible(true);
    }

    private PsiClass findTargetClass(Editor editor, PsiFile file) {
        if (!(file instanceof PsiJavaFile)) {
            return null;
        }
        int offset = editor.getCaretModel().getOffset();
        PsiElement element = file.findElementAt(offset);
        PsiClass target = PsiTreeUtil.getParentOfType(element, PsiClass.class);
        if (target != null) {
            return target;
        }
        PsiClass[] classes = ((PsiJavaFile) file).getClasses();
        return classes.length == 0 ? null : classes[0];
    }

}
