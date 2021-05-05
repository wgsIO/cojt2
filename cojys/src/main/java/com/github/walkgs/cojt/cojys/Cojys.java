package com.github.walkgs.cojt.cojys;

import com.github.walkgs.cojt.cojys.executors.Context;
import com.github.walkgs.cojt.cojys.invokers.eventable.Event;
import com.github.walkgs.cojt.cojys.invokers.eventable.EventHandler;
import com.github.walkgs.cojt.cojys.invokers.eventable.EventHandlerCreator;
import com.github.walkgs.cojt.cojys.invokers.post.Post;
import com.github.walkgs.cojt.cojys.invokers.post.Powders;
import com.github.walkgs.cojt.cojys.invokers.strategies.StrategyHandler;
import com.github.walkgs.cojt.cojys.invokers.strategies.StrategyHandlerCreator;
import com.github.walkgs.cojt.cojys.invokers.strategies.setup.Strategy;
import com.github.walkgs.cojt.cojys.services.Service;
import com.github.walkgs.cojt.cojys.services.Services;
import com.github.walkgs.cojt.cojys.services.abs.AbstractServices;
import com.github.walkgs.cojt.cojys.services.binder.Binder;
import com.github.walkgs.cojt.cojys.services.binder.data.Binding;
import com.github.walkgs.cojt.cojys.services.binder.data.SingleBinder;
import com.google.common.collect.Iterators;
import com.google.common.collect.UnmodifiableIterator;
import lombok.Getter;
import lombok.SneakyThrows;

import java.lang.reflect.Method;
import java.net.BindException;
import java.util.Arrays;
import java.util.List;

public class Cojys {

    @Getter(lazy = true)
    private static final Services<Object> systemLocalServices = new AbstractServices<Object>() {

        @SneakyThrows
        @Post(type = CONFIGURATION)
        private void configure() {
            bind(new StrategyHandlerCreator() {
                @Override
                public <A, B> StrategyHandler<A, B> create() {
                    return new StrategyHandler<A, B>() {

                        private static final String NOT_STRATEGY_MESSAGE = "The inserted object is not a strategy";

                        private transient Binder<Object> strategies = new SingleBinder<>();

                        @Override
                        public boolean register(final Class<?> strategy) {
                            try {
                                checkIsStrategy(strategy);
                                final Strategy annotation = strategy.getAnnotation(Strategy.class);
                                strategies.bind(strategy.newInstance(), annotation.name());
                                return true;
                            } catch (IllegalAccessException | InstantiationException | BindException e) {
                                return false;
                            }
                        }

                        @Override
                        public boolean register(final Object strategy) {
                            try {
                                final Class<?> clazz = strategy.getClass();
                                checkIsStrategy(clazz);
                                final Strategy annotation = clazz.getAnnotation(Strategy.class);
                                strategies.bind(strategy, annotation.name());
                                return true;
                            } catch (BindException e) {
                                return false;
                            }
                        }

                        @Override
                        public boolean unregister(final Class<?> strategy) {
                            try {
                                checkIsStrategy(strategy);
                                final UnmodifiableIterator<Binding<Object>> binders = strategies.binders(SEARCH_IN_ALL);
                                final Strategy annotation = strategy.getAnnotation(Strategy.class);
                                final String name = annotation.name();
                                while (binders.hasNext()) {
                                    final Binding<Object> binding = binders.next();
                                    if (binding.getBinding().getClass() == strategy && binding.getName().equals(name)) {
                                        strategies.unbind(binding);
                                        return true;
                                    }
                                }
                                return false;
                            } catch (BindException e) {
                                return false;
                            }
                        }

                        @Override
                        public boolean unregister(final Object strategy) {
                            return unregister(strategy.getClass());
                        }

                        private void checkIsStrategy(Class<?> clazz) throws BindException {
                            if (!clazz.isAnnotationPresent(Service.class))
                                throw new BindException(NOT_STRATEGY_MESSAGE);
                        }

                        @Override
                        @SafeVarargs
                        public final void setup(final String name, final Context<A, B, StrategyHandler>... context) throws Exception {
                            final UnmodifiableIterator<Binding<Object>> binders = strategies.binders(SEARCH_IN_ALL);
                            while (binders.hasNext()) {
                                final Binding<Object> binding = binders.next();
                                if (binding.getName().equals(name)) {
                                    final Class<?> clazz = binding.getBinding().getClass();
                                    final Method[] methods = clazz.getDeclaredMethods();
                                    if (context.length > 0) {
                                        for (Method method : methods) {
                                            if (!method.isAnnotationPresent(Post.class))
                                                continue;
                                            //final List<Class<?>> params = Arrays.asList(method.getParameterTypes());
                                            //if (!params.contains(context[0].getClass()))
                                            //    continue;
                                            Powders.invoke(binding.getBinding(), method, SETUP, context[0]);
                                        }
                                        return;
                                    }
                                    Powders.invoke(binding.getBinding(), methods, SETUP);
                                    return;
                                }
                            }
                        }

                        @Override
                        @SafeVarargs
                        public final void dispose(final String name, final Context<A, B, StrategyHandler>... context) throws Exception {
                            final UnmodifiableIterator<Binding<Object>> binders = strategies.binders(SEARCH_IN_ALL);
                            while (binders.hasNext()) {
                                final Binding<Object> binding = binders.next();
                                if (binding.getName().equals(name)) {
                                    final Class<?> clazz = binding.getBinding().getClass();
                                    final Method[] methods = clazz.getDeclaredMethods();
                                    if (context.length > 0)
                                        for (Method method : methods) {
                                            if (!method.isAnnotationPresent(Post.class))
                                                continue;
                                            final List<Class<?>> params = Arrays.asList(method.getParameterTypes());
                                            if (!params.contains(context[0].getClass()))
                                                continue;
                                            Powders.invoke(binding.getBinding(), method, DISPOSE, context[0]);
                                            return;
                                        }
                                    Powders.invoke(binding.getBinding(), methods, DISPOSE);
                                    return;
                                }
                            }
                        }

                        @Override
                        @SafeVarargs
                        public final void setup(final String[] strategies, final Context<A, B, StrategyHandler>... context) throws Exception {
                            for (String strategy : strategies)
                                setup(strategy, context);
                        }

                        @Override
                        @SafeVarargs
                        public final void dispose(final String[] strategies, final Context<A, B, StrategyHandler>... context) throws Exception {
                            for (String strategy : strategies)
                                dispose(strategy, context);
                        }

                        @Override
                        public final void setup(final Context<A, B, StrategyHandler> context) throws Exception {
                            for (String strategy : strategies())
                                setup(strategy, context);
                        }

                        @Override
                        public final void dispose(final Context<A, B, StrategyHandler> context) throws Exception {
                            for (String strategy : strategies())
                                dispose(strategy, context);
                        }

                        @Override
                        public void shutdown() {
                            ((SingleBinder) strategies).destroy();
                            strategies = null;
                        }

                        @Override
                        public String[] strategies() {
                            final Binding[] bindings = Iterators.toArray(strategies.binders(), Binding.class);
                            final String[] strategies = new String[bindings.length];
                            for (int i = 0; i < bindings.length; i++)
                                strategies[i] = bindings[i].getName();
                            return strategies;
                        }
                    };
                }
            }, "StrategyHandlerCreator");
            bind(new EventHandlerCreator() {
                @Override
                public EventHandler create() {
                    return new EventHandler() {

                        private static final int NORMAL = 0x100;

                        private transient Binder<Object> events = new SingleBinder<>();

                        @Override
                        public void register(final Class<?> event) throws BindException {
                            try {
                                if (!event.isAnnotationPresent(Event.class))
                                    throw new BindException("The class is not an event");
                                events.bind(event.newInstance());
                            } catch (IllegalAccessException | InstantiationException | BindException e) {
                                throw new BindException("It was not possible to register the event; Possible reasons: 'failed to initialize' or 'is already registered'");
                            }
                        }

                        @Override
                        public void register(final Object event) throws BindException {
                            try {
                                final Class<?> clazz = event.getClass();
                                if (!clazz.isAnnotationPresent(Event.class))
                                    throw new BindException("The class is not an event");
                                events.bind(event);
                            } catch (BindException e) {
                                throw new BindException("It was not possible to register the event; Possible reasons: 'failed to initialize' or 'is already registered'");
                            }
                        }

                        @Override
                        public void unregister(final Class<?> event) throws BindException {
                            if (!event.isAnnotationPresent(Event.class))
                                throw new BindException("The class is not an event");
                            events.unbind(event);
                        }

                        @Override
                        public void unregister(final Object event) throws BindException {
                            unregister(event.getClass());
                        }

                        @Override
                        public void call(final Object... args) {
                            final UnmodifiableIterator<Binding<Object>> events = this.events.binders(Binder.SEARCH_IN_ALL);
                            while (events.hasNext()) {
                                final Binding<Object> event = events.next();
                                EventHandler.call(event, new Context<>(event.getBinding().getClass(), args, this), NORMAL);
                            }
                        }

                        @Override
                        public void call(final Class<?> event, final Object... args) {
                            call(event, args, NORMAL);
                        }

                        @Override
                        public void call(final String name, final Object... args) {
                            call(name, args, NORMAL);
                        }

                        @Override
                        public void call(final Class<?> event, final Object[] args, final int reasonId) {
                            call(event, args, reasonId, true);
                        }

                        @Override
                        public void call(final String name, final Object[] args, final int reasonId) {
                            final UnmodifiableIterator<Binding<Object>> events = this.events.binders(Binder.SEARCH_IN_ALL);
                            while (events.hasNext()) {
                                final Binding<Object> event = events.next();
                                if (event.getName().equals(name)) {
                                    EventHandler.call(event, new Context<>(event.getBinding().getClass(), args, this), reasonId);
                                    return;
                                }
                            }
                        }

                        @Override
                        public void call(final Class<?> event, final Object[] args, final int reasonId, final boolean byType) {
                            final UnmodifiableIterator<Binding<Object>> events = this.events.binders(Binder.SEARCH_IN_ALL);
                            final Context<Class<?>, Object[], EventHandler> context = new Context<>(event, args, this);
                            while (events.hasNext()) {
                                final Binding<Object> binding = events.next();
                                if (binding.getBinding().getClass() == event) {
                                    EventHandler.call(binding, context, reasonId);
                                    if (!byType)
                                        return;
                                }
                            }
                        }

                        @Override
                        public void shutdown() {
                            ((SingleBinder) events).destroy();
                            events = null;
                        }

                    };
                }
            }, "EventHandlerCreator");
        }

        @Override
        protected void checkIsServices(final Class<?> clazz) {
        } //To empty

    };

}
