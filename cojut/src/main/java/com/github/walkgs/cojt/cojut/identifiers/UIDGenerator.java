package com.github.walkgs.cojt.cojut.identifiers;

import com.github.walkgs.cojt.cojut.Applicable;
import lombok.ToString;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public interface UIDGenerator extends Applicable<UIDGenerator> {

    Map<UIDGenerator, Map<String, Storage>> GENERATORS = new LinkedHashMap<>();
    UIDGenerator DEFAULT = UIDGenerator.safeCreate(IDGenerator.class, UIDGenerator.genSeed(IDGenerator.SERIAL));

    static UIDGenerator safeCreate(Class<? extends UIDGenerator> generator, long seed) {
        try {
            return create(generator, seed);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException | InstantiationException e) {
            return null;
        }
    }

    static UIDGenerator create(Class<? extends UIDGenerator> generator) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        return create(generator, genSeed(UUID.randomUUID()));
    }

    static UIDGenerator create(Class<? extends UIDGenerator> generator, long seed) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        final UIDGenerator instance = generator.getConstructor(long.class).newInstance(seed);
        GENERATORS.put(instance, new LinkedHashMap<>());
        return instance;
    }

    static long genSeed(long identifier) {
        return genSeed(Long.toString(identifier));
    }

    static long genSeed(UUID identifier) {
        return genSeed(identifier.toString());
    }

    static long genSeed(String identifier) {
        return genSeed(identifier.getBytes());
    }

    static long genSeed(byte[] gen) {
        long seed = gen.length;
        for (byte f : gen)
            seed *= (f / gen.length);
        return seed;
    }

    static long generate(UIDGenerator generator, byte[] codes, long seed) {
        Storage alt = generator.getStorage(codes);
        if (alt == null) {
            alt = new Storage();
            GENERATORS.get(generator).put(Arrays.toString(codes), alt);
        }
        final long gen = nextGen(alt);
        long codeID = codes.length;
        for (byte code : codes)
            codeID += (code * codes.length) % seed;
        return (codeID * gen) / alt.resets;
    }

    static long nextGen(Storage alt) {
        alt.last = (alt.last < 0 ? 2 + alt.resets++ : alt.last + 1);
        return alt.last;
    }

    Identifier generate(long code);

    Identifier generate(UUID code);

    Identifier generate(String code);

    Identifier generate(byte[] code);

    default Storage getStorage(byte[] code) {
        return GENERATORS.get(this).get(Arrays.toString(code));
    }

    @ToString
    class Storage {
        long resets = 1;
        long last = 1;
    }

}
