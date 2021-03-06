package pers.fw.tplugin.main;

import com.intellij.codeInsight.completion.*;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.util.ProcessingContext;
import pers.fw.tplugin.db.DbReference;
import pers.fw.tplugin.inter.CompletionContributorParameter;
import pers.fw.tplugin.inter.GotoCompletionContributor;
import pers.fw.tplugin.inter.GotoCompletionProviderInterface;
import org.jetbrains.annotations.NotNull;
import pers.fw.tplugin.util.GotoCompletionUtil;
import pers.fw.tplugin.util.Util;

//代码补全主入口
public class Completer extends CompletionContributor {

    public Completer() {


        extend(CompletionType.BASIC, PlatformPatterns.psiElement(), new CompletionProvider<CompletionParameters>() {
            @Override
            protected void addCompletions(@NotNull CompletionParameters completionParameters, ProcessingContext processingContext, @NotNull CompletionResultSet completionResultSet) {

                PsiElement psiElement = completionParameters.getOriginalPosition();
                if (psiElement == null) {   // || !LaravelProjectComponent.isEnabled(psiElement)) {
                    return;
                }
                CompletionContributorParameter parameter = null;
                for (GotoCompletionContributor contributor : GotoCompletionUtil.getContributors(psiElement)) {
                    GotoCompletionProviderInterface formReferenceCompletionContributor = contributor.getProvider(psiElement);
                    if (formReferenceCompletionContributor != null) {
                        if (formReferenceCompletionContributor instanceof DbReference.ColumnProvider) { //数据库列重定义前缀
                            PrefixMatcher prefixMatcher = completionResultSet.getPrefixMatcher();
                            String rePrefix = Util.rePrefix(prefixMatcher.getPrefix());
                            completionResultSet.withPrefixMatcher(prefixMatcher.cloneWithPrefix(rePrefix))
                                    .addAllElements(formReferenceCompletionContributor.getLookupElements());
                        } else {
                            completionResultSet.addAllElements(formReferenceCompletionContributor.getLookupElements());
                        }
                        if (parameter == null) {
                            parameter = new CompletionContributorParameter(completionParameters, processingContext, completionResultSet);
                        }
                        formReferenceCompletionContributor.getLookupElements(parameter);
                    }
                }
            }
        });
    }
}

