package com.github.walkgs.cojt.cojut.identifiers;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@RequiredArgsConstructor
class IDGenerator implements UIDGenerator {

    static final long SERIAL = 5533745499892598345L;

    private final long seed;

    @Override
    public Identifier generate(final long code) {
        return generate(Long.toString(code));
    }

    @Override
    public Identifier generate(final UUID code) {
        return generate(code.toString());
    }

    @Override
    public Identifier generate(final String code) {
        return generate(code.getBytes());
    }

    @Override
    public Identifier generate(final byte[] code) {
        return new ID(UIDGenerator.generate(this, code, seed));
    }

    @Getter
    @RequiredArgsConstructor
    class ID implements Identifier {
        final long id;

        @Override
        public String toString() {
            return Long.toString(id);
        }
    }

}
