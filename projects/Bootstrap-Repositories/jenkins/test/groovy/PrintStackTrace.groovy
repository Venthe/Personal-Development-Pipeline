import spock.lang.Ignore
import spock.lang.Specification

import static eu.venthe.Shared.printStackTrace

class PrintStackTrace extends Specification {
    static {
        System.setProperty("groovy.grape.enable", "false")
    }

    @Ignore
    def "1"() {
        when:
        def result = printStackTrace(new Exception("Test"))

        then:
        result.replaceAll("\\s+","") == """\
        java.lang.Exception: Test
        \tat java.base/jdk.internal.reflect.NativeConstructorAccessorImpl.newInstance0(Native Method)
        \tat java.base/jdk.internal.reflect.NativeConstructorAccessorImpl.newInstance(NativeConstructorAccessorImpl.java:77)
        \tat java.base/jdk.internal.reflect.DelegatingConstructorAccessorImpl.newInstance(DelegatingConstructorAccessorImpl.java:45)
        \tat java.base/java.lang.reflect.Constructor.newInstanceWithCaller(Constructor.java:499)
        \tat java.base/java.lang.reflect.Constructor.newInstance(Constructor.java:480)
        \tat org.codehaus.groovy.reflection.CachedConstructor.invoke(CachedConstructor.java:72)
        \tat org.codehaus.groovy.runtime.callsite.ConstructorSite\$ConstructorSiteNoUnwrapNoCoerce.callConstructor(ConstructorSite.java:105)
        \tat org.codehaus.groovy.runtime.callsite.CallSiteArray.defaultCallConstructor(CallSiteArray.java:59)
        \tat org.codehaus.groovy.runtime.callsite.AbstractCallSite.callConstructor(AbstractCallSite.java:263)
        \tat org.codehaus.groovy.runtime.callsite.AbstractCallSite.callConstructor(AbstractCallSite.java:277)
        \tat PrintStackTrace.\$spock_feature_0_0(PrintStackTrace.groovy:12)
        \tat java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
        \tat java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:77)
        \tat java.base/jdk.internal.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
        \tat java.base/java.lang.reflect.Method.invoke(Method.java:568)
        \tat org.spockframework.util.ReflectionUtil.invokeMethod(ReflectionUtil.java:196)
        \tat org.spockframework.runtime.model.MethodInfo.lambda\$new\$0(MethodInfo.java:49)
        \tat org.spockframework.runtime.model.MethodInfo.invoke(MethodInfo.java:156)
        \tat org.spockframework.runtime.PlatformSpecRunner.invokeRaw(PlatformSpecRunner.java:407)
        \tat org.spockframework.runtime.PlatformSpecRunner.invoke(PlatformSpecRunner.java:390)
        \tat org.spockframework.runtime.PlatformSpecRunner.runFeatureMethod(PlatformSpecRunner.java:324)
        \tat org.spockframework.runtime.IterationNode.execute(IterationNode.java:50)
        \tat org.spockframework.runtime.SimpleFeatureNode.execute(SimpleFeatureNode.java:58)
        \tat org.spockframework.runtime.SimpleFeatureNode.execute(SimpleFeatureNode.java:15)
        \tat org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda\$executeRecursively\$6(NodeTestTask.java:151)
        \tat org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:73)
        \tat org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda\$executeRecursively\$8(NodeTestTask.java:141)
        \tat org.spockframework.runtime.SpockNode.sneakyInvoke(SpockNode.java:40)
        \tat org.spockframework.runtime.IterationNode.lambda\$around\$0(IterationNode.java:67)
        \tat org.spockframework.runtime.PlatformSpecRunner.lambda\$createMethodInfoForDoRunIteration\$5(PlatformSpecRunner.java:236)
        \tat org.spockframework.runtime.model.MethodInfo.invoke(MethodInfo.java:156)
        \tat org.spockframework.runtime.PlatformSpecRunner.invokeRaw(PlatformSpecRunner.java:407)
        \tat org.spockframework.runtime.PlatformSpecRunner.invoke(PlatformSpecRunner.java:390)
        \tat org.spockframework.runtime.PlatformSpecRunner.runIteration(PlatformSpecRunner.java:218)
        \tat org.spockframework.runtime.IterationNode.around(IterationNode.java:67)
        \tat org.spockframework.runtime.SimpleFeatureNode.lambda\$around\$0(SimpleFeatureNode.java:52)
        \tat org.spockframework.runtime.SpockNode.sneakyInvoke(SpockNode.java:40)
        \tat org.spockframework.runtime.FeatureNode.lambda\$around\$0(FeatureNode.java:41)
        \tat org.spockframework.runtime.PlatformSpecRunner.lambda\$createMethodInfoForDoRunFeature\$4(PlatformSpecRunner.java:199)
        \tat org.spockframework.runtime.model.MethodInfo.invoke(MethodInfo.java:156)
        \tat org.spockframework.runtime.PlatformSpecRunner.invokeRaw(PlatformSpecRunner.java:407)
        \tat org.spockframework.runtime.PlatformSpecRunner.invoke(PlatformSpecRunner.java:390)
        \tat org.spockframework.runtime.PlatformSpecRunner.runFeature(PlatformSpecRunner.java:192)
        \tat org.spockframework.runtime.FeatureNode.around(FeatureNode.java:41)
        \tat org.spockframework.runtime.SimpleFeatureNode.around(SimpleFeatureNode.java:52)
        \tat org.spockframework.runtime.SimpleFeatureNode.around(SimpleFeatureNode.java:15)
        \tat org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda\$executeRecursively\$9(NodeTestTask.java:139)
        \tat org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:73)
        \tat org.junit.platform.engine.support.hierarchical.NodeTestTask.executeRecursively(NodeTestTask.java:138)
        \tat org.junit.platform.engine.support.hierarchical.NodeTestTask.execute(NodeTestTask.java:95)
        \tat java.base/java.util.ArrayList.forEach(ArrayList.java:1511)
        \tat org.junit.platform.engine.support.hierarchical.SameThreadHierarchicalTestExecutorService.invokeAll(SameThreadHierarchicalTestExecutorService.java:41)
        \tat org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda\$executeRecursively\$6(NodeTestTask.java:155)
        \tat org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:73)
        \tat org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda\$executeRecursively\$8(NodeTestTask.java:141)
        \tat org.spockframework.runtime.SpockNode.sneakyInvoke(SpockNode.java:40)
        \tat org.spockframework.runtime.SpecNode.lambda\$around\$0(SpecNode.java:63)
        \tat org.spockframework.runtime.PlatformSpecRunner.lambda\$createMethodInfoForDoRunSpec\$0(PlatformSpecRunner.java:61)
        \tat org.spockframework.runtime.model.MethodInfo.invoke(MethodInfo.java:156)
        \tat org.spockframework.runtime.PlatformSpecRunner.invokeRaw(PlatformSpecRunner.java:407)
        \tat org.spockframework.runtime.PlatformSpecRunner.invoke(PlatformSpecRunner.java:390)
        \tat org.spockframework.runtime.PlatformSpecRunner.runSpec(PlatformSpecRunner.java:55)
        \tat org.spockframework.runtime.SpecNode.around(SpecNode.java:63)
        \tat org.spockframework.runtime.SpecNode.around(SpecNode.java:11)
        \tat org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda\$executeRecursively\$9(NodeTestTask.java:139)
        \tat org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:73)
        \tat org.junit.platform.engine.support.hierarchical.NodeTestTask.executeRecursively(NodeTestTask.java:138)
        \tat org.junit.platform.engine.support.hierarchical.NodeTestTask.execute(NodeTestTask.java:95)
        \tat java.base/java.util.ArrayList.forEach(ArrayList.java:1511)
        \tat org.junit.platform.engine.support.hierarchical.SameThreadHierarchicalTestExecutorService.invokeAll(SameThreadHierarchicalTestExecutorService.java:41)
        \tat org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda\$executeRecursively\$6(NodeTestTask.java:155)
        \tat org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:73)
        \tat org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda\$executeRecursively\$8(NodeTestTask.java:141)
        \tat org.junit.platform.engine.support.hierarchical.Node.around(Node.java:137)
        \tat org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda\$executeRecursively\$9(NodeTestTask.java:139)
        \tat org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:73)
        \tat org.junit.platform.engine.support.hierarchical.NodeTestTask.executeRecursively(NodeTestTask.java:138)
        \tat org.junit.platform.engine.support.hierarchical.NodeTestTask.execute(NodeTestTask.java:95)
        \tat org.junit.platform.engine.support.hierarchical.SameThreadHierarchicalTestExecutorService.submit(SameThreadHierarchicalTestExecutorService.java:35)
        \tat org.junit.platform.engine.support.hierarchical.HierarchicalTestExecutor.execute(HierarchicalTestExecutor.java:57)
        \tat org.junit.platform.engine.support.hierarchical.HierarchicalTestEngine.execute(HierarchicalTestEngine.java:54)
        \tat org.junit.platform.launcher.core.EngineExecutionOrchestrator.execute(EngineExecutionOrchestrator.java:107)
        \tat org.junit.platform.launcher.core.EngineExecutionOrchestrator.execute(EngineExecutionOrchestrator.java:88)
        \tat org.junit.platform.launcher.core.EngineExecutionOrchestrator.lambda\$execute\$0(EngineExecutionOrchestrator.java:54)
        \tat org.junit.platform.launcher.core.EngineExecutionOrchestrator.withInterceptedStreams(EngineExecutionOrchestrator.java:67)
        \tat org.junit.platform.launcher.core.EngineExecutionOrchestrator.execute(EngineExecutionOrchestrator.java:52)
        \tat org.junit.platform.launcher.core.DefaultLauncher.execute(DefaultLauncher.java:114)
        \tat org.junit.platform.launcher.core.DefaultLauncher.execute(DefaultLauncher.java:86)
        \tat org.junit.platform.launcher.core.DefaultLauncherSession\$DelegatingLauncher.execute(DefaultLauncherSession.java:86)
        \tat org.junit.platform.launcher.core.SessionPerRequestLauncher.execute(SessionPerRequestLauncher.java:53)
        \tat com.intellij.junit5.JUnit5IdeaTestRunner.startRunnerWithArgs(JUnit5IdeaTestRunner.java:57)
        \tat com.intellij.rt.junit.IdeaTestRunner\$Repeater\$1.execute(IdeaTestRunner.java:38)
        \tat com.intellij.rt.execution.junit.TestsRepeater.repeat(TestsRepeater.java:11)
        \tat com.intellij.rt.junit.IdeaTestRunner\$Repeater.startRunnerWithArgs(IdeaTestRunner.java:35)
        \tat com.intellij.rt.junit.JUnitStarter.prepareStreamsAndStart(JUnitStarter.java:235)
        \tat com.intellij.rt.junit.JUnitStarter.main(JUnitStarter.java:54)
        """.stripMargin().stripIndent().replaceAll("\\s+","")
    }
}
