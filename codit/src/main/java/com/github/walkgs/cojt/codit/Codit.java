package com.github.walkgs.cojt.codit;

import com.github.walkgs.cojt.codit.lifecycle.*;
import com.github.walkgs.cojt.codit.lifecycle.impl.LifeCycleHandlerImpl;
import com.github.walkgs.cojt.codit.lifecycle.notifiers.StateChangeNotification;
import com.github.walkgs.cojt.cojys.Cojys;
import com.github.walkgs.cojt.cojys.invokers.post.Post;
import com.github.walkgs.cojt.cojys.invokers.posture.Posture;
import com.github.walkgs.cojt.cojys.properties.Exchanger;
import com.github.walkgs.cojt.cojys.services.Services;
import javafx.application.Preloader;
import lombok.RequiredArgsConstructor;

import java.net.BindException;

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

    private final String name;

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

    public static void main(String[] args) throws Exception {
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

}
