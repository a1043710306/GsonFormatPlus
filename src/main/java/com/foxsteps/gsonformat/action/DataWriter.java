package com.foxsteps.gsonformat.action;

import com.foxsteps.gsonformat.entity.ClassEntity;
import com.foxsteps.gsonformat.process.ClassProcessor;
import com.foxsteps.gsonformat.process.IProcessor;
import com.foxsteps.gsonformat.ui.Toast;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.MessageType;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiFile;

import java.util.ArrayList;
import java.util.List;

/** Writes generated PSI. Callers must invoke this from an IntelliJ write action. */
public class DataWriter {

    private PsiClass cls;
    private PsiElementFactory factory;
    private Project project;
    private ClassEntity targetClass;
    private List<String> generateClassList = new ArrayList<>();

    public DataWriter(PsiFile file, Project project, PsiClass cls) {
        factory = JavaPsiFacade.getElementFactory(project);
        this.project = project;
        this.cls = cls;
    }

    public void execute(ClassEntity targetClass) {
        this.targetClass = targetClass;
        // PSI mutations must run inside the write action supplied by the caller.
        // Running this from Task.Backgroundable used to work by accident, but
        // modern IntelliJ versions reject write actions from a background thread.
        long currentTimeMillis = System.currentTimeMillis();
        run();
        StringBuffer sb = new StringBuffer();
        sb.append("GsonFormat [" + (System.currentTimeMillis() - currentTimeMillis) + " ms]\n");
//                sb.append("generate class : ( "+generateClassList.size()+" )\n");
//                for (String item: generateClassList) {
//                    sb.append("    at "+item+"\n");
//                }
//                sb.append("  \n");
//                NotificationCenter.info(sb.toString());
        Toast.make(project, MessageType.INFO, sb.toString());
    }

    private void run() {
        if (targetClass == null) {
            return;
        }
        generateClassList.clear();
        new ClassProcessor(factory, cls).generate(targetClass, new IProcessor() {
            @Override
            public void onStarProcess(ClassEntity classEntity, PsiElementFactory factory, PsiClass cls) {
                generateClassList.add(cls.getQualifiedName());
            }

            @Override
            public void onEndProcess(ClassEntity classEntity, PsiElementFactory factory, PsiClass cls) {

            }

            @Override
            public void onStartGenerateClass(PsiElementFactory factory, ClassEntity classEntity, PsiClass parentClass) {

            }

            @Override
            public void onEndGenerateClass(PsiElementFactory factory, ClassEntity classEntity, PsiClass parentClass, PsiClass generateClass) {
                generateClassList.add(generateClass.getQualifiedName());
            }
        });
    }

}
