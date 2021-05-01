package com.github.walkgs.cojt.codit;

import com.github.walkgs.cojt.codit.injector.Inject;
import com.github.walkgs.cojt.codit.injector.Injector;
import com.github.walkgs.cojt.codit.injector.impl.InjectorImpl;
import com.github.walkgs.cojt.codit.lifecycle.*;
import com.github.walkgs.cojt.codit.lifecycle.impl.LifeCycleHandlerImpl;
import com.github.walkgs.cojt.codit.lifecycle.notifiers.StateChangeNotification;
import com.github.walkgs.cojt.cojut.basic.accessor.Accessor;
import com.github.walkgs.cojt.cojut.basic.accessor.AccessorType;
import com.github.walkgs.cojt.cojut.basic.accessor.ClassAccessor;
import com.github.walkgs.cojt.cojut.basic.accessor.MemberAccessor;
import com.github.walkgs.cojt.cojut.basic.accessor.impl.LocalClassAcessor;
import com.github.walkgs.cojt.cojys.Cojys;
import com.github.walkgs.cojt.cojys.invokers.post.Post;
import com.github.walkgs.cojt.cojys.invokers.posture.Posture;
import com.github.walkgs.cojt.cojys.loader.CJY2Finder;
import com.github.walkgs.cojt.cojys.loader.CJY2Loader;
import com.github.walkgs.cojt.cojys.loader.impl.CJY2FinderImpl;
import com.github.walkgs.cojt.cojys.loader.impl.CJY2LoaderImpl;
import com.github.walkgs.cojt.cojys.properties.Exchanger;
import com.github.walkgs.cojt.cojys.properties.Name;
import com.github.walkgs.cojt.cojys.services.Services;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.Field;
import java.net.BindException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@LifeCycle
@RequiredArgsConstructor
public class Codit {

    static { //Bind services
        final Services<Object> localServices = Cojys.getSystemLocalServices();
        try {
            localServices.bind(new LifeCycleHandlerImpl(), "LifeCycleHandler");
        } catch (BindException e) {
            e.printStackTrace();
        }
    }

    @Name(name = "userName")
    private final String name;

    private Pedros pedros = new Pedros();

    public static void main(String[] args) throws Exception {

        Set<String> packages = new HashSet<String>();
        packages.add(Codit.class.getPackage().getName());
        final Injector injector = new InjectorImpl(packages);

        if (true)

            LocalClassAcessor.LOCAL.apply(ClassAccessor::setProvider);
        final MemberAccessor $accessor = ClassAccessor.getProvider().create(Codit.class);
        try (final MemberAccessor accessor = $accessor) {
            System.out.println("Accessor: " + accessor);
            final Accessor<Field>[] accessors = accessor.get(AccessorType.FIELDS);
            for (Accessor<Field> field : accessors)
                System.out.println("Field: " + field.get().get(new Codit("pedro")));
        }

        if (true)
            return;

        final CJY2Loader cjy2Loader = new CJY2LoaderImpl();
        try (final CJY2Finder finder = new CJY2FinderImpl(cjy2Loader).open()){
            final Map<String, Set<Class<?>>> classes = finder.findClasses(Package.getPackages());
            classes.forEach((key, value) -> System.out.println("Package: " + key + " \n* Classes: " + value));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        if (true)
            return;
        //final StrategyHandlerCreator strategyExecutorCreator = Cojys.getSystemLocalServices().get("StrategyHandlerCreator");
        //System.out.println("CREATOR: " + strategyExecutorCreator);
        //final StrategyHandler<Object, Object> strategyHandler = strategyExecutorCreator.create();
        //System.out.println("EXECUTOR: " + strategyHandler);

        final LifeCycleHandler handler = Cojys.getSystemLocalServices().get("LifeCycleHandler");
        //System.out.println("LOADER: " + handler + " ");
        final LifeDescription description = handler.createObjectLife(Codit.class, "pedro");
        final Object instance = description.getLivableObject();
        final Life life = description.getLife();
        handler.load(life, $ -> {
            //System.out.println("LOADED");
        }, $ -> {
           // System.out.println("COMPLETE");
        });
        handler.unload(life, $ -> {
            //System.out.println("UNLOADED");
        }, $ -> {
            //System.out.println("UNLOADED");
        });
        final LifeCycleInstaller installer = ((Exchanger<LifeCycleInstaller>) handler).request(null);
        installer.uninstall(description);

        //Manual install

        final LifeDescription description1 = installer.install(instance);
        installer.installEvents(description1, StateChangeNotification.class);

        handler.load(description1.getLife(), $ -> {
            //System.out.println("LOADED");
        }, $ -> {
            // System.out.println("COMPLETE");
        });


    }

    @Post(type = Posture.START)
    private void start() {
        System.out.println("START");
    }

    @Post(type = Posture.STOP)
    private void stop() {
        System.out.println("ST");
    }

    @Post(type = Posture.UNLOAD)
    private void unload() {
        System.out.println("UNL");
    }

    @Post(type = Posture.LOAD)
    private void load() {
        System.out.println("L");
    }

    public class Pedros {

        @Inject
        @Name(name = "userName")
        private String name;

    }

}
