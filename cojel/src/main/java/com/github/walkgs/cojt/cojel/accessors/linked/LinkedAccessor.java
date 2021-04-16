package com.github.walkgs.cojt.cojel.accessors.linked;

import com.github.walkgs.cojt.cojel.accessors.AccessorConfiguration;
import com.github.walkgs.cojt.cojel.accessors.AccessorQuery;
import com.github.walkgs.cojt.cojel.accessors.ClassAccessor;
import com.github.walkgs.cojt.cojel.accessors.type.AccessibleType;
import com.github.walkgs.cojt.cojel.accessors.type.AccessorGetterType;
import com.github.walkgs.cojt.cojut.ObjectAttachment;
import com.github.walkgs.cojt.cojut.element.ObjectLinker;
import com.github.walkgs.cojt.cojys.secure.SecureToken;
import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.NonNull;
import lombok.SneakyThrows;

import java.io.IOException;
import java.lang.reflect.*;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class LinkedAccessor implements ClassAccessor {

    private static final int PERMISSION = 0x0001;
    private static final String ILLEGAL_ACCESS_MESSAGE = "Access is denied, you do not have the required permission.";
    private static final ObjectAttachment<AccessibleObject, Object> ANNEXATION = new ObjectAttachment<>();
    private static final AccessorConfiguration DEFAULT_CONFIGURATION = AccessorConfiguration.builder()
            .andSuperClass(false)
            .sync(false)
            .build();

    private final Queue<AccessorQuery> queries = new ConcurrentLinkedQueue<>();

    @Override
    public <T> AccessorQuery<T> query(final Class<? extends T> clazz) {
        return query(clazz, DEFAULT_CONFIGURATION);
    }

    @Override
    public <T> AccessorQuery<T> query(final Class<? extends T> clazz, final String name) {
        return query(clazz, name, DEFAULT_CONFIGURATION);
    }

    public <T> AccessorQuery<T> query(final Class<? extends T> clazz, final SecureToken.Token _identifier) {
        return query(clazz, _identifier, DEFAULT_CONFIGURATION);
    }

    @Override
    public <T> AccessorQuery<T> query(final Class<? extends T> clazz, final AccessorConfiguration configuration) {
        return query(clazz, SecureToken.generate(), configuration);
    }

    @Override
    public <T> AccessorQuery<T> query(final Class<? extends T> clazz, final String name, final AccessorConfiguration configuration) {
        return query(clazz, SecureToken.generate(name.getBytes(), true), configuration);
    }

    @Override
    public <T> AccessorQuery<T> query(final Class<? extends T> clazz, final SecureToken.Token _identifier, final AccessorConfiguration _configuration) {
        final AccessorQuery<T> query = _configuration.isSync() ? newQuerySync(clazz, _identifier, _configuration) : newQuery(clazz, _identifier, _configuration);
        queries.add(query);
        return query;
    }

    private synchronized <T> AccessorQuery<T> newQuerySync(final Class<? extends T> clazz, final SecureToken.Token _identifier, final AccessorConfiguration _configuration) {
        return newQuery(clazz, _identifier, _configuration);
    }

    private <T> AccessorQuery<T> newQuery(final Class<? extends T> clazz, final SecureToken.Token _identifier, final AccessorConfiguration _configuration) {
        return new AccessorQuery<T>() {

            @Getter
            private final Class<? extends T> type = clazz;
            @Getter
            private final SecureToken.Token identifier = _identifier;

            private AccessorConfiguration configuration = _configuration;
            private List<Connection<T>> connections = new LinkedList<>();

            private boolean closed = false;

            @Override
            public Connection<T> open() {
                checkClosed(closed);
                final Connection<T> connection = new Connection<T>() {

                    private Map<AccessibleType, List<ObjectLinker<AccessibleObject, String>>> objects = new ConcurrentHashMap<>();

                    private List<Applicable<T>> applicants = new LinkedList<>();

                    private boolean closed = false;

                    @Override
                    public AccessibleObject get(@NonNull final String name) {
                        checkClosed(closed);
                        for (List<ObjectLinker<AccessibleObject, String>> list : objects.values()) {
                            for (ObjectLinker<AccessibleObject, String> object : list) {
                                if (object.getLinked().equals(name))
                                    return object.getObject();
                            }
                        }
                        return null;
                    }

                    @Override
                    public AccessibleObject get(@NonNull final int index, @NonNull final AccessibleType type) {
                        checkClosed(closed);
                        return objects.get(type).get(index).getObject();
                    }

                    @Override
                    public AccessibleObject get(@NonNull final String name, @NonNull final AccessibleType type) {
                        checkClosed(closed);
                        for (ObjectLinker<AccessibleObject, String> object : objects.get(type)) {
                            if (object.getLinked().equals(name))
                                return object.getObject();
                        }
                        return null;
                    }

                    @Override
                    public Applicable<T> applicable(final int index, final AccessibleType type) {
                        final AccessibleObject accessible = get(index, type);
                        return applicable(accessible, type);
                    }

                    @Override
                    public Applicable<T> applicable(final String name, final AccessibleType type) {
                        final AccessibleObject accessible = get(name, type);
                        return applicable(accessible, type);
                    }

                    @Override
                    public Applicable<T> applicable(final AccessibleObject _accessible, final AccessibleType _type) {
                        checkClosed(closed);
                        Applicable<T> applicable = new Applicable<T>() {

                            private AccessibleObject accessible = _accessible;
                            private AccessibleType type = _type;
                            private boolean closed = false;

                            @Override
                            @SuppressWarnings("unchecked")
                            public <A> A get() throws Exception {
                                checkClosed(closed);
                                switch (type) {
                                    case FIELD:
                                        return (A) ((Field) accessible).get(clazz);
                                    default:
                                        return (A) accessible;
                                }
                            }

                            @Override
                            @SuppressWarnings("unchecked")
                            public <A> A get(final AccessorGetterType getterType) throws Exception {
                                checkClosed(closed);
                                final Object result = get();
                                if (type == AccessibleType.FIELD)
                                    return (A) result;
                                final Executable executable = (Executable) result;
                                return ((A) (getterType == AccessorGetterType.ANNOTATIONS ? executable.getAnnotations() : executable.getDeclaredAnnotations()));
                            }

                            @Override
                            public int getModifiers() {
                                return ((Member) accessible).getModifiers();
                            }

                            @Override
                            public int assignModifier(final int modifier) {
                                return AccessController.doPrivileged(new PrivilegedAction<Integer>() {
                                    @Override
                                    @SneakyThrows
                                    public Integer run() {
                                        return memberAssignModifier(((Member) accessible), modifier);
                                    }
                                });
                            }

                            @Override
                            @SuppressWarnings("unchecked")
                            public <A> A apply(final T instance, final Object... values) throws Exception {
                                checkClosed(closed);
                                switch (type) {
                                    case FIELD: {
                                        ((Field) accessible).set(instance, values[0]);
                                        return null;
                                    }
                                    case METHOD:
                                        return (A) ((Method) accessible).invoke(instance, values);
                                    case CONSTRUCTOR:
                                        return (A) ((Constructor) accessible).newInstance(values);
                                }
                                return null;
                            }

                            @Override
                            @SuppressWarnings("unchecked")
                            public <A> A apply(final Object... values) throws Exception {
                                return apply(null, values);
                            }

                            @Override
                            public void close() {
                                checkClosed(closed);
                                closed = true;
                                final Queue<Object> attachments = ANNEXATION.getAttachments(accessible);
                                if (attachments.size() > 0)
                                    accessible.setAccessible((Boolean) attachments.poll());
                                ANNEXATION.destroy(accessible);
                                for (ObjectLinker<AccessibleObject, String> object : objects.get(type))
                                    if (object.getObject() == accessible) {
                                        objects.get(type).remove(object);
                                        break;
                                    }
                                type = null;
                                accessible = null;
                                applicants.remove(this);
                            }

                        };
                        applicants.add(applicable);
                        return applicable;
                    }

                    @Override
                    public void copy(final ObjectLinker<AccessibleObject, String> object, final AccessibleType type, final int permission) throws IllegalAccessException {
                        checkClosed(closed);
                        if (permission != PERMISSION)
                            throw new IllegalAccessException(ILLEGAL_ACCESS_MESSAGE);

                        final AccessibleObject accessible = object.getObject();
                        ANNEXATION.attach(accessible, accessible.isAccessible());
                        accessible.setAccessible(true);

                        objects.computeIfAbsent(type, it -> Lists.newArrayList()).add(object);
                    }

                    @Override
                    public void copy(final List<ObjectLinker<AccessibleObject, String>> objects, final AccessibleType type, final int permission) throws IllegalAccessException {
                        checkClosed(closed);
                        if (permission != PERMISSION)
                            throw new IllegalAccessException(ILLEGAL_ACCESS_MESSAGE);
                        final List<ObjectLinker<AccessibleObject, String>> list = this.objects.computeIfAbsent(type, it -> Lists.newArrayList());

                        for (ObjectLinker<AccessibleObject, String> object : objects) {
                            final AccessibleObject accessible = object.getObject();

                            ANNEXATION.attach(accessible, accessible.isAccessible());

                            accessible.setAccessible(true);
                            list.add(object);
                        }
                    }

                    @Override
                    public void close() throws IOException {
                        checkClosed(closed);
                        closed = true;
                        for (Applicable applicable : applicants)
                            applicable.close();
                        for (List list : objects.values())
                            list.clear();
                        objects.clear();
                        applicants.clear();
                        objects = null;
                        applicants = null;
                        connections.remove(this);
                    }

                };
                connections.add(connection);
                return connection;
            }

            private Member[] getMembers(Class<?> clazz, AccessibleType type) {
                return type == AccessibleType.FIELD ? clazz.getDeclaredFields() :
                        type == AccessibleType.METHOD ? clazz.getDeclaredMethods() :
                                type == AccessibleType.CONSTRUCTOR ? clazz.getDeclaredConstructors() : new Member[0];
            }

            private List<ObjectLinker<AccessibleObject, String>> getAccesses(Class<?> clazz, AccessibleType type) {
                final Member[] members = getMembers(clazz, type);
                if (members.length < 1)
                    return Lists.newArrayList();
                final List<ObjectLinker<AccessibleObject, String>> accesses = Lists.newArrayList();
                for (Member member : members)
                    accesses.add(new ObjectLinker<>(((AccessibleObject) member), member.getName()));
                return accesses;
            }

            @Override
            public AccessorQuery<T> cluster(final AccessibleType type) throws IllegalAccessException {
                checkClosed(closed);
                final PrivilegedAction<List<ObjectLinker<AccessibleObject, String>>> action = () -> {
                    final List<ObjectLinker<AccessibleObject, String>> fields = getAccesses(clazz, type);
                    if (configuration.andSuperClass()) {
                        Class<?> superclass = clazz.getSuperclass();
                        while (superclass != null) {
                            fields.addAll(getAccesses(superclass, type));
                            superclass = superclass.getSuperclass();
                        }
                    }
                    return fields;
                };
                final List<ObjectLinker<AccessibleObject, String>> objects = action.run();
                for (Connection<T> connection : connections)
                    cluster(connection, objects, type);
                return this;
            }

            @Override
            public AccessorQuery<T> cluster(final Connection<T> connection, final AccessibleType type) throws IllegalAccessException {
                checkClosed(closed);
                final PrivilegedAction<List<ObjectLinker<AccessibleObject, String>>> action = () -> {
                    final List<ObjectLinker<AccessibleObject, String>> fields = getAccesses(clazz, type);
                    if (configuration.andSuperClass()) {
                        Class<?> superclass = clazz.getSuperclass();
                        while (superclass != null) {
                            fields.addAll(getAccesses(superclass, type));
                            superclass = superclass.getSuperclass();
                        }
                    }
                    return fields;
                };
                cluster(connection, action.run(), type);
                return this;
            }

            @Override
            public AccessorQuery<T> cluster(final Connection<T> connection, final ObjectLinker<AccessibleObject, String> object, final AccessibleType type) throws IllegalAccessException {
                connection.copy(object, type, PERMISSION);
                return this;
            }

            @Override
            public AccessorQuery<T> cluster(final Connection<T> connection, final List<ObjectLinker<AccessibleObject, String>> objects, final AccessibleType type) throws IllegalAccessException {
                connection.copy(objects, type, PERMISSION);
                return this;
            }

            @Override
            public void close() throws IOException {
                checkClosed(closed);
                closed = true;
                queries.remove(this);
                for (Connection connection : connections)
                    connection.close();
                connections = null;
                configuration = null;
            }

        };
    }

    private void checkClosed(boolean closed) {
        if (closed)
            throw new IllegalStateException("Could not perform a function, reason: The connection has been closed.");
    }

    private int memberAssignModifier(Member member, int modifier) throws NoSuchFieldException, IllegalAccessException {
        final Field modifiers = Field.class.getDeclaredField("modifiers");
        final boolean oldAccessible = modifiers.isAccessible();

        modifiers.setAccessible(true);

        final int oldModifier = member.getModifiers();
        final int newModifier = oldModifier & modifier;

        modifiers.setInt(member, newModifier);

        modifiers.setAccessible(oldAccessible);

        return newModifier;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> AccessorQuery<T> find(final SecureToken.Token identifier) {
        for (AccessorQuery query : queries) {
            if (query.getIdentifier() == identifier)
                return query;
        }
        return null;
    }

    @Override
    public AccessorQuery[] queries() {
        return queries.toArray(new AccessorQuery[]{});
    }

}
